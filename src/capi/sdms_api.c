#include <stdio.h>
#include <errno.h>
#include <string.h>
#include <stdlib.h>
#include <ctype.h>

#ifdef _WIN32
// Windows
#include <winsock.h>
#else
// Unix
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <unistd.h>
#endif

#include "sdms_api.h"
#include "sdms_api_internal.h"

// some literals
static const char *DATA         = "DATA";
static const char *DESC         = "DESC";
static const char *ERROR        = "ERROR";
static const char *ERRORCODE    = "ERRORCODE";
static const char *ERRORMESSAGE = "ERRORMESSAGE";
static const char *FEEDBACK     = "FEEDBACK";
static const char *RECORD       = "RECORD";
static const char *TABLE        = "TABLE";
static const char *TITLE        = "TITLE";

// #define DEBUG 0

SDMS_ERROR sdms_error;

const int MALLOC_MAGIC = 0x10203040;
const char *leadOut = "ABCDEFGHIJKLMNO";

void *myRealloc(void *orig, size_t s, int line, char *f)
{
	int *p = (int *) orig;
	size_t brutto_s = s + 3 * sizeof(int) + 16 /* leadOut */;
	void *rc;

	if (orig == NULL) 
		return myMalloc(s, line, f);

	if (s == 0) {
		myFree(orig, line, f);
		return NULL;
	}

	p -= 3; /* should be the original pointer */
	if (*p != MALLOC_MAGIC) {
		fprintf (stderr, "Pointer %p/%p seems to be corrupted. Magic : %X, Line : %d, Size %d\n", orig, p, *p, *(p + 1), *(p + 2));
	}
	rc = myMalloc(brutto_s, line, f);
	if (rc == NULL) {
		Free(orig);
		return rc;
	}
	if (*(p + 2) <= s)	// original mem was smaller, original contents fits
		memcpy(rc, orig, *(p + 2));
	else			// original mem was larger, contents will be truncated
		memcpy(rc, orig, s);
	Free(orig);

	return rc;
}

void *myMalloc(size_t s, int line, char *f)
{
	void *rc;
	size_t brutto_s = s + 3 * sizeof(int) + 16 /* leadOut */;

	rc = malloc(brutto_s);
	/* I know, fprintf allocates memory. If the malloc failed, the fprintf will fail as well */
	if (rc != NULL) {
		fprintf(stderr, "%s(%d): malloc() -- size %d/%d, address %p/%p\n", f, line, (int) s, (int) brutto_s, rc, ((char *) rc) + 3 * sizeof(int));
	} else {
		fwrite("malloc returned NULL\n", 21, 1, stderr);
		return NULL;
	}
	*((int *) rc) = MALLOC_MAGIC;
	*((int *) rc + 1) = line;
	*((int *) rc + 2) = s;
	rc = ((char *) rc) + 3 * sizeof(int);
	memcpy((char *) rc + s, leadOut, 16);

	return rc;
}

void myFree(void *ptr, int line, char *f)
{
	int *p = (int *) ptr;
	p -= 3;
	if (*p != MALLOC_MAGIC) {
		fprintf (stderr, "Pointer %p/%p seems to be corrupted. Magic : %X, Line : %d, Size %d\n", ptr, p, *p, *(p + 1), *(p + 2));
	}
	
	fprintf(stderr, "%s(%d): free() -- address %p/%p\n", f, line, ptr, p);
	free(p);
}

char *myStrdup(const char *s, int line, char *f)
{
	char *rc, *p;
	size_t l = strlen(s) + 1;
	rc = (char *) myMalloc(l, line, f);
	if (rc == NULL)
		return rc;
	p = rc;
	while (*s) {
		*p = *s;
		p++; s++;
	}
	*p = '\0';
	return rc;
}

/* ---------------------------------------------------------------------------------------------------------
   sdms_connection
   --------------------------------------------------------------------------------------------------------- */

/*
	Open an SDMS connection
	*sdms_connection must be NULL (initialize to NULL or close an sdms connection befor reusing it)
	if user is a number > 0 (uses atoi(user)) the connection is opened as a job connection.
	if user conttains a '.' the connection is opened as a job server connection.
	jobserver names must be given quoted to be interpreted case sensitive or if jobserver name contains special characters.
	Examples:
		"GLOBAL.'CaseSesitiveServerName'"
		"GLOBAL.'ServerName with blanks'"
*/
extern int sdms_connection_open(SDMS_CONNECTION **sdms_connection, char *host, int port, char *user, char *password)
{
	char *p = NULL;
	int isopen = 0;
	SDMS_OUTPUT *sdms_output = NULL;
	SDMS_STRING *command = NULL;
	int sockfd;
	struct sockaddr_in serv_addr;

	if (*sdms_connection != NULL) {
		sdms_error_clear();
		sdms_error.number    = (char *) "02311112012";
		sdms_error.message   = (char *) "Cannot create SDMS_CONNECTION on unitialized or not closed sdms_connection pointer (!= NULL)";
		goto error;
	}

	if ((sockfd = socket (AF_INET, SOCK_STREAM, 0)) == -1) {
		sdms_error_clear();
		sdms_error.number    = (char *) "02311071106";
		sdms_error.message   = (char *) "Cannot get socket";
		sdms_error.sys_errno = errno;
		goto error;
	}

	serv_addr.sin_family = AF_INET;
	serv_addr.sin_port = htons(port);
	serv_addr.sin_addr.s_addr = inet_addr(host);
	if (serv_addr.sin_addr.s_addr == -1) {
		struct hostent *hp;

		hp = gethostbyname(host);
		if (hp == NULL) {
			sdms_error_clear();
			sdms_error.number    = (char *) "02311071110";
			sdms_error.message   = (char *) "Unknown host";
			goto error;
		}
		memcpy(&(serv_addr.sin_addr.s_addr), hp->h_addr, hp->h_length);
	}
	
	if (connect(sockfd, (struct sockaddr *) &serv_addr, sizeof(struct sockaddr_in)) == -1) {
		sdms_error_clear();
		sdms_error.number    = (char *) "02311071111";
		sdms_error.message   = (char *) "Cannot connect";
		sdms_error.sys_errno = errno;
		goto error;
	}

	*sdms_connection = (SDMS_CONNECTION*)(Malloc(sizeof(SDMS_CONNECTION)));
	if (*sdms_connection == NULL) {
		close(sockfd);
		sdms_error_clear();
		sdms_error.number    = (char *) "02311071203";
		sdms_error.message   = (char *) "Cannot allocate SDMS_CONNECTION";
		sdms_error.sys_errno = errno;
		goto error;
	}
	(*sdms_connection)->socket = sockfd;

	isopen = 1;

	if (sdms_string(&command, (char *) "CONNECT ") != SDMS_OK)
		goto error;
	if (atoi(user) != 0)
		if (sdms_string_append(command, (char *) "JOB ") != SDMS_OK)
			goto error;
	if (strchr(user,'.') != NULL)
		if (sdms_string_append(command, (char *) "JOB SERVER ") != SDMS_OK)
			goto error;
	if (atoi(user) == 0 && strchr(user,'.') == NULL)
		if (sdms_string_append(command, (char *) "'") != SDMS_OK) 
			goto error;
	if (sdms_string_append(command, user) != SDMS_OK) 
		goto error;
	if (atoi(user) == 0 && strchr(user,'.') == NULL)
		if (sdms_string_append(command, (char *) "'") != SDMS_OK)
			goto error;
	if (sdms_string_append(command, (char *) " IDENTIFIED BY '") != SDMS_OK)
		goto error;
	if (sdms_string_append(command, password) != SDMS_OK)
		goto error;
	if (sdms_string_append(command, (char *) "' WITH PROTOCOL = PYTHON") != SDMS_OK)
		goto error;

	if (sdms_command(&sdms_output, *sdms_connection, command) != SDMS_OK)
		goto error;

	if (sdms_output->error != NULL) {
		sdms_error_clear();
		if (sdms_output->error->errorcode != NULL) {
			p = Strdup(sdms_output->error->errorcode);
			if (p == NULL) {
				sdms_error_clear();
				sdms_error.number    = (char *) "02311081020";
				sdms_error.message   = (char *) "Cannot allocate errorcode";
				sdms_error.sys_errno = errno;
				goto error;
			}
			sdms_error.number = p;
			sdms_error.number_allocated = 1;
		}
		else
			sdms_error.number = NULL;

		if (sdms_output->error->errormessage != NULL) {
			p = Strdup(sdms_output->error->errormessage);
			if (p == NULL) {
				sdms_error.number    = (char *) "02311081021";
				sdms_error.message   = (char *) "Cannot allocate errormessage";
				sdms_error.sys_errno = errno;
				goto error;
			}
			sdms_error.message = p;
			sdms_error.message_allocated = 1;
		}
		else
			sdms_error.message = NULL;

		sdms_error.sys_errno = 0;
		goto error;
	}

	return SDMS_OK;
error:
	if (sdms_output != NULL)
		sdms_output_free(&sdms_output);
	if (isopen)
		sdms_connection_close(sdms_connection);

	return SDMS_ERR;
}

extern int sdms_command_check(char *command)
{
	char *p = command;
	int instring = 0;
	int escape = 0;
	while (*p != '\0') {
		if (*p == '\\') {
			if (escape == 0) {
				escape = 1;
			} else {
				escape = 0;
			}
		} else {
			if (*p == '\'') {
				if (escape == 0) {
					if (instring == 0) {
						instring = 1;
					} else {
						instring = 0;
					}
				}
			}
			escape = 0;
		}
		p++;
	}
	if (instring == 1) {
		sdms_error_clear();
		sdms_error.number    = (char *) "02311261442";
		sdms_error.message   = (char *) "Unterminated string in command";
		return SDMS_ERR;
	}
	return SDMS_OK;
}

extern int sdms_command(SDMS_OUTPUT **sdms_output, SDMS_CONNECTION *sdms_connection, SDMS_STRING *command)
{
	char *buf = NULL;
	int bufsize;
	int len;

	if (sdms_command_check(command->buf) != SDMS_OK)
		goto error;

	if (*sdms_output != NULL) {
		sdms_error_clear();
		sdms_error.number    = (char *) "02311112011";
		sdms_error.message   = (char *) "Cannot create SDMS_OUTPUT on unitialized or not freed sdms_output (!= NULL)";
		goto error;
	}
	len = strlen(command->buf);
	while ((isspace(command->buf[len-1]) || 
	        command->buf[len-1] == ';'
	       ) && 
	       len > 0
	      ) {
		command->buf[len-1] = '\0';
		len --;
	}
	if (len == 0) {
		sdms_error_clear();
		sdms_error.number    = (char *) "02311071555";
		sdms_error.message   = (char *) "Empty Statement";
		goto error;
	}
#ifdef DEBUG
	printf("sending command : %s\n", command->buf);
#endif
	bufsize = SDMS_OUTPUT_CHUNK;
	buf = Malloc(bufsize + 1);
	if (buf == NULL) {
		sdms_error_clear();
		sdms_error.number    = (char *) "02311071306";
		sdms_error.message   = (char *) "Cannot allocate recv buffer";
		sdms_error.sys_errno = errno;
		goto error;
	}
	int sent = 0;
	int s;
	while (sent < len) {
		s = send(sdms_connection->socket,
				(command->buf) + sent,
				len - sent,
				0
			);
		if (s == -1) {
			sdms_error_clear();
			sdms_error.number    = (char *) "02311071246";
			sdms_error.message   = (char *) "Cannot send command";
			sdms_error.sys_errno = errno;
			goto error;
		}
		sent += s;
	}
	while ((s = send(sdms_connection->socket, ";", 1, 0)) != 1) {
		if (s == -1) {
			sdms_error_clear();
			sdms_error.number    = (char *) "02311071340";
			sdms_error.message   = (char *) "Cannot send command";
			sdms_error.sys_errno = errno;
			goto error;
		}
	}
	// get the output 
	int r;
	int instr = 0;
	int esc = 0;
	int lvl = 0;
	int gotdata = 0;
	int expectdata = 1;
	char *pos = buf;
	while(expectdata == 1) {
		r = recv(sdms_connection->socket, pos, buf + bufsize - pos, 0);
		if (r == -1) {
			sdms_error_clear();
			sdms_error.number    = (char *) "02311071348";
			sdms_error.message   = (char *) "Cannot receive output";
			sdms_error.sys_errno = errno;
			goto error;
		}
		int i;
		for (i = 0; i < r; i ++) {
			switch(pos[i]) {
				case '\'': 
					if (esc == 1)
						esc = 0;
					else
						if (instr == 0)
							instr = 1;
						else
							instr = 0;
					break;
				case '{':
					if (instr == 1)
						break;
					if (esc == 1)
						esc = 0;
					else
						lvl ++;
					gotdata = 1;
					break;
				case '}':
					if (instr == 1)
						break;
					if (esc == 1)
						esc = 0;
					else {
						lvl --;
						if (lvl == 0 && gotdata == 1)
							expectdata = 0;
					}
					break;
				case '\\':
					if (esc == 0)
						esc = 1;
					else
						esc = 0;
					break;
				default:
					esc = 0;
			}
		}
		if (expectdata == 0)
			break; // we are done
		pos += r;
		if (pos - buf == bufsize) {
			buf = Realloc(buf, bufsize + SDMS_OUTPUT_CHUNK + 1);
			if (buf == NULL) {
				sdms_error_clear();
				sdms_error.number    = (char *) "02311071430";
				sdms_error.message   = (char *) "Cannot reallocate recv buffer";
				sdms_error.sys_errno = errno;
				goto error;
			}
			pos = buf + bufsize;
			bufsize += SDMS_OUTPUT_CHUNK;
		}
	}
	int rawsize = pos + r - buf;
	buf[rawsize] = '\0';
	char *cp = buf;
#ifdef DEBUG
	printf("received response :\n%s\n", buf);
#endif
	if (sdms_output_parse(sdms_output, &cp) != SDMS_OK) goto error;
	Free(buf);
	buf = NULL;

	return SDMS_OK;
error:
	if (buf != NULL) {
		Free(buf);
		buf = NULL;
	}
	if (*sdms_output != NULL)
		sdms_output_free(sdms_output);

	return SDMS_ERR;
}

extern int sdms_connection_close(SDMS_CONNECTION **sdms_connection)
{
	if (*sdms_connection == NULL) {
		sdms_error_clear();
		sdms_error.number    = (char *) "02311111941";
		sdms_error.message   = (char *) "Cannot close NULL connection";
		return SDMS_ERR;
	}
	if (close((*sdms_connection)->socket) != 0) {
		sdms_error_clear();
		sdms_error.number    = (char *) "02311071140";
		sdms_error.message   = (char *) "Cannot close socket";
		sdms_error.sys_errno = errno;
		return SDMS_ERR;
	}
	Free(*sdms_connection);
	*sdms_connection = NULL;

	return SDMS_OK;
} 

/* ---------------------------------------------------------------------------------------------------------
   sdms_string
   --------------------------------------------------------------------------------------------------------- */

extern int sdms_string(SDMS_STRING **string, char *s)
{
	if (*string != NULL) {
		sdms_error_clear();
		sdms_error.number    = (char *) "02311112008";
		sdms_error.message   = (char *) "Cannot create SDMS_STRING on unitialized or not freed pointer (!= NULL)";
		return SDMS_ERR;
	}
	*string = Malloc(sizeof(SDMS_STRING));
	if (*string == NULL) {
		sdms_error_clear();
		sdms_error.number    = (char *) "02311071204";
		sdms_error.message   = (char *) "Cannot allocate SDMS_STRING";
		sdms_error.sys_errno = errno;
		return SDMS_ERR;
	}
	(*string)->buf     = NULL;
	(*string)->bufsize = 0;
	if (s != NULL) {
		(*string)->buf = Strdup(s);
		if ((*string)->buf == NULL) {
			sdms_error_clear();
			sdms_error.number    = (char *) "02311071250";
			sdms_error.message   = (char *) "Cannot allocate SDMS_STRING buffer";
			sdms_error.sys_errno = errno;
			Free(*string);
			*string = NULL;
			return SDMS_ERR;
		}
		(*string)->bufsize = strlen(s) + 1;
	}
	return SDMS_OK;
}

extern int sdms_string_append(SDMS_STRING *string, char *text)
{
	char *s = NULL;
	if (text == NULL)
		return SDMS_OK;
	int textlen = strlen(text);
	if (textlen == 0) 
		return SDMS_OK;
	int bufsize = 0;
	if (string->buf != NULL)
		bufsize = strlen(string->buf);
	bufsize += strlen(text);
	bufsize = (bufsize / SDMS_STRING_CHUNK + 1) * SDMS_STRING_CHUNK;
	if (bufsize > string->bufsize) {
		s = Realloc(string->buf, bufsize);
		if (s == NULL) {
			sdms_error_clear();
			sdms_error.number    = (char *) "02311071221";
			sdms_error.message   = (char *) "Cannot (re)allocate SDMS_STRING buffer";
			sdms_error.sys_errno = errno;
			return SDMS_ERR;
		}
		if (string->buf == NULL)
			*s = '\0';
		string->buf     = s;
		string->bufsize = bufsize;
	}
	strcat(string->buf, text);
	return SDMS_OK;
}

extern void sdms_string_clear(SDMS_STRING *sdms_string)
{
	if (sdms_string->buf != NULL)
		sdms_string->buf[0] = '\0';
}

extern void sdms_string_free(SDMS_STRING **sdms_string)
{
	if ((*sdms_string)->buf != NULL) {
		Free ((*sdms_string)->buf);
		(*sdms_string)->buf = NULL;
	}

	Free(*sdms_string);
	*sdms_string = NULL;
}

/* ---------------------------------------------------------------------------------------------------------
   sdms_vector
   --------------------------------------------------------------------------------------------------------- */

extern int sdms_vector(SDMS_VECTOR **vector)
{

	if (*vector != NULL) {
		sdms_error_clear();
		sdms_error.number    = (char *) "02311210744";
		sdms_error.message   = (char *) "Cannot create SDMS_VECTOR on unitialized or not freed pointer (!= NULL)";
		return SDMS_ERR;
	}
	*vector = Malloc(sizeof(SDMS_VECTOR));
	if (*vector == NULL) {
		sdms_error_clear();
		sdms_error.number    = (char *) "02311210745";
		sdms_error.message   = (char *) "Cannot allocate SDMS_VECTOR";
		sdms_error.sys_errno = errno;
		return SDMS_ERR;
	}
	(*vector)->buf     = NULL;
	(*vector)->length  = 0;
	(*vector)->bufsize = 0;
	return SDMS_OK;
}

extern int sdms_vector_append(SDMS_VECTOR *vector, void *data)
{

	void *s = NULL;
	int bufsize = (vector->length / SDMS_VECTOR_CHUNK + 1) * SDMS_VECTOR_CHUNK;
	if (bufsize > vector->bufsize) {
		s = Realloc(vector->buf, bufsize * sizeof(void*));
		if (s == NULL) {
			sdms_error_clear();
			sdms_error.number    = (char *) "02311210746";
			sdms_error.message   = (char *) "Cannot (re)allocate SDMS_VECTOR buffer";
			sdms_error.sys_errno = errno;
			return SDMS_ERR;
		}
		vector->buf     = s;
		vector->bufsize = bufsize;
	}
	vector->buf[vector->length] = data;
	vector->length ++;
	return SDMS_OK;
}

extern void sdms_vector_free(SDMS_VECTOR **vector)
{
	if (*vector == NULL)
		return;
	if ((*vector)->buf != NULL) {
		Free ((*vector)->buf);
		(*vector)->buf = NULL;
	}
	Free(*vector);
	*vector = NULL;
}

/* ---------------------------------------------------------------------------------------------------------
   sdms_output_data
   --------------------------------------------------------------------------------------------------------- */

extern void sdms_output_desc_free(SDMS_VECTOR **desc)
{
	if (*desc == NULL)
		return;
	int i;
	for (i = 0; i < (*desc)->length; i ++) {
		Free((*desc)->buf[i]);
		(*desc)->buf[i] = NULL;
	}
	sdms_vector_free(desc);
}

void sdms_output_record_free(SDMS_VECTOR **record)
{
	if (*record == NULL)
		return;
	int i;
	for (i = 0; i < (*record)->length; i ++) {
		sdms_output_data_free((SDMS_OUTPUT_DATA **)(&((*record)->buf[i])));
	}
	sdms_vector_free(record);
}

extern void sdms_output_table_free(SDMS_VECTOR **table)
{
	if (*table == NULL)
		return;
	int i;
	for (i = 0; i < (*table)->length; i ++) {
		sdms_output_record_free((SDMS_VECTOR **)(&((*table)->buf[i])));
	}
	sdms_vector_free(table);
}

extern int sdms_output_data(SDMS_OUTPUT_DATA **output_data)
{
	if (*output_data != NULL) {
		sdms_error_clear();
		sdms_error.number    = (char *) "02311260954";
		sdms_error.message   = (char *) "Cannot create SDMS_OUTPUT_DATA on unitialized or not freed pointer (!= NULL)";
		return SDMS_ERR;
	}

	*output_data = Malloc(sizeof(SDMS_OUTPUT_DATA));
	if (*output_data == NULL) {
		sdms_error_clear();
		sdms_error.number    = (char *) "02311210924";
		sdms_error.message   = (char *) "Cannot allocate SDMS_OUTPUT_DATA";
		sdms_error.sys_errno = errno;
		return SDMS_ERR;
	}
	(*output_data)->type  = -1;
	(*output_data)->title = NULL;
	(*output_data)->desc  = NULL;
	(*output_data)->data  = NULL;

	return SDMS_OK;
}

extern void sdms_output_data_free(SDMS_OUTPUT_DATA **output_data)
{
	if (*output_data == NULL)
		return;

	if ((*output_data)->type == SDMS_SCALAR_TYPE) {
		if ((*output_data)->data != NULL) {
			Free((*output_data)->data);
			(*output_data)->data = NULL;
		}
		return;
	}
	if ((*output_data)->title != NULL) {
		Free ((*output_data)->title);
		(*output_data)->title = NULL;
	}
	sdms_output_desc_free(&((*output_data)->desc));
	if ((*output_data)->type == SDMS_RECORD_TYPE)
		sdms_output_record_free((SDMS_VECTOR **)(&((*output_data)->data)));
	else
		sdms_output_table_free((SDMS_VECTOR **)(&((*output_data)->data)));
	Free(*output_data);
	*output_data = NULL;
}
/* ---------------------------------------------------------------------------------------------------------
   sdms_output
   --------------------------------------------------------------------------------------------------------- */

extern int sdms_output(SDMS_OUTPUT **output)
{

	if (*output != NULL) {
		sdms_error_clear();
		sdms_error.number    = (char *) "02311261047";
		sdms_error.message   = (char *) "Cannot create SDMS_OUTPUT on unitialized or not freed pointer (!= NULL)";
		return SDMS_ERR;
	}

	*output = Malloc(sizeof(SDMS_OUTPUT));
	if (*output == NULL) {
		sdms_error_clear();
		sdms_error.number    = (char *) "02311261048";
		sdms_error.message   = (char *) "Cannot allocate SDMS_OUTPUT";
		sdms_error.sys_errno = errno;
		return SDMS_ERR;
	}
	(*output)->data = NULL;
	(*output)->error = NULL;
	(*output)->feedback = NULL;

	return SDMS_OK;
}

extern void sdms_output_error_free(SDMS_OUTPUT_ERROR *sdms_output_error)
{
	if (sdms_output_error->errorcode    != NULL) {
		Free (sdms_output_error->errorcode);
		sdms_output_error->errorcode = NULL;
	}
	if (sdms_output_error->errormessage != NULL) {
		Free (sdms_output_error->errormessage);
		sdms_output_error->errormessage = NULL;
	}
	Free(sdms_output_error);
	sdms_output_error = NULL;
}

extern void sdms_output_free(SDMS_OUTPUT **sdms_output)
{
	if ((*sdms_output)->error    != NULL) sdms_output_error_free((*sdms_output)->error);
	if ((*sdms_output)->data     != NULL) sdms_output_data_free(&((*sdms_output)->data));
	if ((*sdms_output)->feedback != NULL) {
		Free ((*sdms_output)->feedback);
		(*sdms_output)->feedback = NULL;
	}
	Free(*sdms_output);
	*sdms_output = NULL;
}

/* ---------------------------------------------------------------------------------------------------------
   sdms_parse
   --------------------------------------------------------------------------------------------------------- */

/*
	Allocate and return a string containing the token
	The allocated space migth be a little longer than the actual string
	because escapes '\' have to be removed in the output string.
	To avoid this we would have to count escapes before malloc.
	I decided that more speed is more valueable than a few additional bytes allocated.
*/
extern char *sdms_token_get_string(SDMS_TOKEN *token)
{
	char *string = Malloc(token->length + 1);
	if (string == NULL) {
		sdms_error_clear();
		sdms_error.number    = (char *) "02311220749";
		sdms_error.message   = (char *) "Cannot allocate string for token";
		sdms_error.sys_errno = errno;
		return NULL;
	}
	char *s = token->pbuf;
	char *t = string;
	int escape = 0;
	while (s < token->pbuf + token->length) {
		if (*s == '\\' && escape == 0) {
			escape = 1;
			s++;
			continue;
		}
/* I guess something like this is intended.
                else {
			switch (*s) {
				case 'a': *s = '\a'; break;
				case 'b': *s = '\b'; break;
				case 'f': *s = '\f'; break;
				case 'n': *s = '\n'; break;
				case 'r': *s = '\r'; break;
				case 't': *s = '\t'; break;
				case 'v': *s = '\v'; break;
			}
		}
*/
		escape = 0;
		*t = *s;
		s++;
		t++;
	}
	*t = '\0';
	return string;
}

/*
	Check whether a token is equal to a given string 
*/
extern int sdms_token_equals(SDMS_TOKEN *token, char* string)
{
	char *t = token->pbuf;
	char *s = string;
	int escape = 0;
	int escapes = 0;
	int slen = strlen(string);
	while (t < token->pbuf + token->length && s < s + slen) {
		if (*t == '\\' && escape == 0) {
			escape = 1;
			escapes ++;
			t++;
			continue;
		}
		escape = 0;
		if (*t != *s)
			return 0;
		s++;
		t++;
	}
	if (slen != token->length - escapes)
		return 0;
	return 1;
}

extern int sdms_output_next_token(SDMS_TOKEN *token, char **cp)
{
	// char *c;
	while (isspace(**cp)) (*cp)++; // skip whitespace

	// c = *cp;
	token->pbuf = *cp;
	switch (**cp) {
		case '\'': {
			// we have to parse a string
			// int escape = 0;
			(*cp)++; // skip first ' char
			// if (*c == '\\') {	// since c == *cp - 1, and *(*cp - 1) == '\'', this can't be true
				// escape = 1;
				// (*cp)++; // skip escape at beginning of token (shouldn't happen)
			// }
			token->pbuf = *cp;
			while ((**cp != '\'') && **cp != '\0') {
				if (**cp == '\\') {
					(*cp)++; // skip escape
				}
				(*cp)++;	// an occasional escaped character is now skipped. even if it is a single quote
			}
			token->length = *cp - token->pbuf;
			token->isstring = 1;
			if (**cp != '\'') {
				sdms_error_clear();
				sdms_error.number    = (char *) "02311220859";
				sdms_error.message   = (char *) "Incomplete string";
				return SDMS_ERR;
			}
			break;
		default:
			// all other tokens are always just a single char Except the 'None' token
			if (**cp == 'N') {
				token->length = 4;
				token->isstring = 0;
				(*cp)+= 3;
			} else {
				token->length = 1;
				token->isstring = 0;
			}
		}
	}
	if (**cp == '\0') {
		sdms_error_clear();
		sdms_error.number    = (char *) "02311260827";
		sdms_error.message   = (char *) "unexpected end of input";
		sdms_error.sys_errno = 0;
		return SDMS_ERR;
	}
	(*cp)++; // advance buffer pointer to 1. char after the token
#if DEBUG >= 1
	fprintf(stderr,"sdms_output_next_token : %s length = %d, isstring = %d\n", sdms_token_get_string(token), token->length, token->isstring);
#endif
	return SDMS_OK;
}

extern int sdms_output_expect(char **cp, char c)
{
	SDMS_TOKEN token;
	if (sdms_output_next_token(&token, cp) != SDMS_OK)
		return SDMS_ERR;
	if (token.isstring == 1 || *(token.pbuf) != c) {
		sdms_error_clear();
		sdms_error.number    = (char *) "02311260826";
		sdms_error.message   = Strdup("expected ?");
		sdms_error.message_allocated = 1;
		sdms_error.message[strlen(sdms_error.message) - 1] = c;
		sdms_error.sys_errno = 0;
		return SDMS_ERR;
	}
	return SDMS_OK;
}

extern int sdms_output_parse_string(char **string, char **cp)
{
	SDMS_TOKEN token;
	if (sdms_output_next_token (&token, cp) != SDMS_OK)
		return SDMS_ERR;
	if (token.isstring == 0)
		if (*(token.pbuf) != 'N') {
			sdms_error_clear();
			sdms_error.number    = (char *) "02311260907";
			sdms_error.message   = Strdup("unexpected ?");
			sdms_error.message_allocated = 1;
			sdms_error.message[strlen(sdms_error.message) - 1] = *(token.pbuf);
			sdms_error.sys_errno = 0;
			return SDMS_ERR;
		}
		else {
			*string = NULL;
		}
	else
		if ((*string = sdms_token_get_string(&token)) == NULL)
			return SDMS_ERR;

	return SDMS_OK;
}

extern int sdms_output_parse_desc(SDMS_VECTOR **vector, char **cp)
{
	SDMS_TOKEN token;
	sdms_error_clear();

	if (sdms_output_expect(cp, '[') != SDMS_OK)
		goto error;

	if (sdms_vector(vector) != SDMS_OK)
		goto error;

	while(1) {
		if (sdms_output_next_token (&token, cp) != SDMS_OK)
			goto error;
		if (token.isstring == 0 && *(token.pbuf) == ',') // ignore ',' seperators
			continue;
		if (token.isstring == 0 && *(token.pbuf) == ']') // we are done
			break;
		if (token.isstring == 1) { 
			char *s = sdms_token_get_string(&token);
			if (s == NULL)
				goto error;
			sdms_vector_append(*vector, s);
		}
	}
	return SDMS_OK;
error:
	if (sdms_error.number != NULL && sdms_error.message == NULL && sdms_error.sys_errno == 0) {
		sdms_error.number    = (char *) "02311221255";
		sdms_error.message   = (char *) "Error parsing desc";
	}
	return SDMS_ERR;
}

extern int sdms_output_parse_record(SDMS_VECTOR **vector, char **cp)
{
	if (sdms_output_expect(cp, '{') != SDMS_OK)
		goto error;

	if (sdms_vector(vector) != SDMS_OK)
		goto error;

	SDMS_TOKEN token;
	sdms_error_clear();

	while(1) {
		if (sdms_output_next_token (&token, cp) != SDMS_OK)
			goto error;
		if (token.isstring == 0) {
			if (*(token.pbuf) == ',')
				continue; // ignore ',' seperators
			if (*(token.pbuf) == '}') // we are done
				break;
			else {
				sdms_error_clear();
				sdms_error.number    = (char *) "02311261014";
				sdms_error.message   = (char *) "key string expected";
				sdms_error.sys_errno = 0;
				goto error;
			}
		}
		if (sdms_output_expect(cp, ':') != SDMS_OK)
			goto error;

		SDMS_OUTPUT_DATA *data = NULL;
		if (sdms_output_parse_data(&data, cp) != SDMS_OK)
			goto error;
		sdms_vector_append(*vector, (void*)data);
	}
	return SDMS_OK;
error:
	if (sdms_error.number != NULL && sdms_error.message == NULL && sdms_error.sys_errno == 0) {
		sdms_error.number    = (char *) "02311251605";
		sdms_error.message   = (char *) "Error parsing output_record";
	}

	if (*vector != NULL)
		sdms_output_record_free(vector);

	return SDMS_ERR;
}

extern int sdms_output_parse_table(SDMS_VECTOR **vector, char **cp)
{
	if (sdms_output_expect(cp, '[') != SDMS_OK)
		goto error;

	if (sdms_vector(vector) != SDMS_OK)
		goto error;

	SDMS_TOKEN token;
	sdms_error_clear();
	while(1) {
		char *savep = *cp;
		if (sdms_output_next_token (&token, cp) != SDMS_OK)
			goto error;
		if (token.isstring == 0 && *(token.pbuf) == ',') // ignore ',' seperators
			continue;
		if (token.isstring == 0 && *(token.pbuf) == ']') // we are done
			break;
		if (token.isstring == 0 && *(token.pbuf) == '{') {
			*cp = savep;
			SDMS_VECTOR *record = NULL;
			if (sdms_output_parse_record(&record, cp) != SDMS_OK)
				goto error;
			sdms_vector_append(*vector, (void*)record);			
		}
	}
	return SDMS_OK;
error:
	if (*vector != NULL)
		sdms_output_table_free(vector);

	return SDMS_ERR;
}

extern int sdms_output_parse_data(SDMS_OUTPUT_DATA **output_data, char **cp)
{
	if (sdms_output_data(output_data) != SDMS_OK)
		goto error;

	SDMS_TOKEN token;
	sdms_error_clear();

	if (sdms_output_next_token (&token, cp) != SDMS_OK)
		goto error;

	if (token.isstring == 1) {
		(*output_data)->type = SDMS_SCALAR_TYPE;
		if ((char *)(((*output_data)->data) = sdms_token_get_string(&token)) == NULL)
			goto error;
		return SDMS_OK;
	}
	if (*(token.pbuf) == 'N') {
		(*output_data)->type = SDMS_SCALAR_TYPE;
		return SDMS_OK; // None -> NULL string
	}

	if (*(token.pbuf) != '{') {
		sdms_error_clear();
		sdms_error.number    = (char *) "02311260826";
		sdms_error.message   = (char *) "expected {";
		sdms_error.sys_errno = 0;
		goto error;
	}

	while(1) {
		if (sdms_output_next_token (&token, cp) != SDMS_OK)
			goto error;
		if (token.isstring == 0 && *(token.pbuf) == ',') 
			continue; // ignore ',' seperators
		if (token.isstring == 0 && *(token.pbuf) == '}') 
			break; // we are done
		if (sdms_token_equals (&token, (char *) TITLE)) {
			if (sdms_output_expect(cp, ':') != SDMS_OK) 
				goto error;
			if (sdms_output_parse_string(&((*output_data)->title), cp) != SDMS_OK)
				goto error;
		}
		else if (sdms_token_equals (&token, (char *) DESC)) {
			if (sdms_output_expect(cp, ':') != SDMS_OK) 
				goto error;
			if (sdms_output_parse_desc(&((*output_data)->desc), cp) != SDMS_OK) 
				goto error;
		}
		else if (sdms_token_equals (&token, (char *) RECORD)) {
			if (sdms_output_expect(cp, ':') != SDMS_OK) 
				goto error;
			(*output_data)->type = SDMS_RECORD_TYPE;
			if (sdms_output_parse_record((SDMS_VECTOR **)(&((*output_data)->data)), cp) != SDMS_OK)
				goto error;
		}
		else if (sdms_token_equals (&token, (char *) TABLE)) {
			if (sdms_output_expect(cp, ':') != SDMS_OK) 
				goto error;
			(*output_data)->type = SDMS_TABLE_TYPE;
			if (sdms_output_parse_table((SDMS_VECTOR **)(&((*output_data)->data)), cp) != SDMS_OK)
				goto error;
		}
	} 
	return SDMS_OK;
error:
	if (sdms_error.number != NULL && sdms_error.message == NULL && sdms_error.sys_errno == 0) {
		sdms_error.number    = (char *) "02311221240";
		sdms_error.message   = (char *) "Error parsing output_data";
	}

	return SDMS_ERR;
}

extern int sdms_output_parse_error(SDMS_OUTPUT_ERROR **output_error, char **cp)
{
	*output_error = Malloc(sizeof(SDMS_OUTPUT_ERROR));
	if (*output_error == NULL) {
		sdms_error_clear();
		sdms_error.number    = (char *) "02311080928";
		sdms_error.message   = (char *) "Cannot allocate SDMS_OUTPUT_ERROR";
		sdms_error.sys_errno = errno;
		goto error;
	}
	(*output_error)->errorcode    = NULL;
	(*output_error)->errormessage = NULL;

	SDMS_TOKEN token;
	sdms_error_clear();
	if (sdms_output_expect(cp, '{') != SDMS_OK)
		goto error;
	while(1) {
		if (sdms_output_next_token (&token, cp) != SDMS_OK)
			goto error;
		if (token.isstring == 0 && *(token.pbuf) == ',')
			continue; // ignore ',' separators
		if (token.isstring == 0 && *(token.pbuf) == '}') 
			break; // we are done
		if (sdms_token_equals (&token, (char *) ERRORCODE)) {
			if (sdms_output_expect(cp, ':') != SDMS_OK)
				goto error;
			if (sdms_output_parse_string(&((*output_error)->errorcode), cp) != SDMS_OK)
				goto error;
		}
		else if (sdms_token_equals (&token, (char *) ERRORMESSAGE)) {
			if (sdms_output_expect(cp, ':') != SDMS_OK)
				goto error;
			if (sdms_output_parse_string(&((*output_error)->errormessage), cp) != SDMS_OK)
				goto error;
		}
	}
	return SDMS_OK;
error:
	if (sdms_error.number != NULL && sdms_error.message == NULL && sdms_error.sys_errno == 0) {
		sdms_error.number    = (char *) "02311221011";
		sdms_error.message   = (char *) "Error parsing output_error";
	}
	return SDMS_ERR;
}

extern int sdms_output_parse(SDMS_OUTPUT **output, char **cp)
{
	if (sdms_output_expect(cp, '{') != SDMS_OK)
		goto error;

	if (sdms_output(output) != SDMS_OK)
		goto error;

	SDMS_TOKEN token;
	sdms_error_clear();
	while (1) {
		if (sdms_output_next_token (&token, cp) != SDMS_OK)
			goto error;
		if (token.isstring == 0 && *(token.pbuf) == ',')
			continue; // ignore ',' seperators
		if (token.isstring == 0 && *(token.pbuf) == '}') 
			break; // we are done
		if (sdms_token_equals (&token, (char *) DATA)) {
			if (sdms_output_expect(cp, ':') != SDMS_OK)
				goto error;
			if (sdms_output_parse_data(&((*output)->data), cp) != SDMS_OK)
				goto error;
		}
		else if (sdms_token_equals (&token, (char *) ERROR)) {
			if (sdms_output_expect(cp, ':') != SDMS_OK)
				goto error;
			if (sdms_output_parse_error(&((*output)->error), cp) != SDMS_OK)
				goto error;
		}
		else if (sdms_token_equals (&token, (char *) FEEDBACK)) {
			if (sdms_output_expect(cp, ':') != SDMS_OK)
				goto error;
			if (sdms_output_parse_string(&((*output)->feedback), cp) != SDMS_OK)
				goto error;
		}
	} 
	return SDMS_OK;
error:
	if (sdms_error.number != NULL && sdms_error.message == NULL && sdms_error.sys_errno == 0) {
		sdms_error.number    = (char *) "02311200729";
		sdms_error.message   = (char *) "Error parsing output";
	}

	return SDMS_ERR;
}

/* ---------------------------------------------------------------------------------------------------------
   sdms_output dump
   --------------------------------------------------------------------------------------------------------- */

extern void sdms_output_desc_dump(SDMS_VECTOR *desc)
{
	int i;
	char d = '[';
	for (i = 0; i < desc->length; i ++) {
		fprintf(stderr, "%c%s", d, (char *)(desc->buf[i]));
		d = ',';
	}
	fprintf(stderr, "]\n");
}

extern void sdms_output_record_dump(SDMS_VECTOR *vector)
{
	fprintf(stderr, "BEGIN RECORD\n");
	int i;
	for (i = 0; i < vector->length; i ++)
		sdms_output_data_dump(vector->buf[i]);
	fprintf(stderr, "END RECORD\n");
}

extern void sdms_output_table_dump(SDMS_VECTOR *vector)
{
	fprintf(stderr, "BEGIN TABLE\n");
	int i;
	for (i = 0; i < vector->length; i ++)
		sdms_output_record_dump(vector->buf[i]);
	fprintf(stderr, "END TABLE\n");
}

extern void sdms_output_data_dump(SDMS_OUTPUT_DATA *output_data)
{
	char *title;
	switch (output_data->type) {
		case SDMS_SCALAR_TYPE:
			fprintf(stderr, "SCALAR = ");
			if (output_data->data != NULL)
				fprintf(stderr, "'%s'\n", (char*)(output_data->data));
			else
				fprintf(stderr, "NULL\n");
			break;
		case SDMS_RECORD_TYPE:
			title = output_data->title;
			if (title == NULL)
				fprintf(stderr, "RECORD ");
			else
				fprintf(stderr, "RECORD(%s) ", title);
			sdms_output_desc_dump (output_data->desc);
			sdms_output_record_dump((SDMS_VECTOR*)(output_data->data));
			break;
		case SDMS_TABLE_TYPE:
			title = output_data->title;
			if (title == NULL)
				fprintf(stderr, "TABLE ");
			else
				fprintf(stderr, "TABLE(%s) ", title);
			sdms_output_desc_dump (output_data->desc);
			sdms_output_table_dump((SDMS_VECTOR*)(output_data->data));
			break;
		default:
			fprintf(stderr, "unknown output_data->type %d\n", output_data->type);
	}
}

extern void sdms_output_error_dump(SDMS_OUTPUT_ERROR *error)
{
	fprintf(stderr, "ERROR = %s,", error->errorcode);
	if (error->errorcode != NULL)
		fprintf(stderr, "(%s)", error->errorcode);
	else
		fprintf(stderr, "(NULL)");
	if (error->errormessage != NULL)
		fprintf(stderr, "%s\n", error->errormessage);
	else
		fprintf(stderr, "NULL\n");
}

extern void sdms_output_dump(SDMS_OUTPUT *output)
{
	if (output->data != NULL) {
		sdms_output_data_dump(output->data);
	}
	if (output->error != NULL) {
		sdms_output_error_dump(output->error);
	}
	fprintf(stderr, "FEEDBACK = ");
	if (output->feedback != NULL)
		fprintf(stderr, "%s\n", output->feedback);
	else
		fprintf(stderr, "NULL\n");
}

/* ---------------------------------------------------------------------------------------------------------
   sdms_error
   --------------------------------------------------------------------------------------------------------- */

extern void sdms_error_print (char *text)
{
	static const char *blank = " ";
	static const char *empty = "";
	int printed = 0;

	if (text != NULL) {
		fprintf(stderr, "%s:", text);
		printed = 1;
	}
	if (sdms_error.number != NULL) {
		fprintf(stderr, "%s(%s)", (printed ? blank : empty), sdms_error.number);
		printed = 1;
	}
	if (sdms_error.message != NULL) {
		fprintf(stderr, "%s%s", (printed ? blank : empty), sdms_error.message);
		printed = 1;
	}
	if (sdms_error.sys_errno != 0) {
		fprintf(stderr, "%s%d:%s]", (printed ? blank : empty), sdms_error.sys_errno, strerror(sdms_error.sys_errno));
		printed = 1;
	}
	if (printed == 1) fprintf(stderr, "\n");
}

extern void sdms_error_clear()
{
	if (sdms_error.number != NULL) {
		if (sdms_error.number_allocated == 1) {
			Free(sdms_error.number);
			sdms_error.number_allocated = 0;
		}
		sdms_error.number = NULL;
	}
	if (sdms_error.message != NULL) {
		if (sdms_error.message_allocated == 1) {
			Free(sdms_error.message);
			sdms_error.message_allocated = 0;
		}
		sdms_error.message = NULL;
	}
	sdms_error.sys_errno = 0;
}

/* ---------------------------------------------------------------------------------------------------------
   sdms_output access
   --------------------------------------------------------------------------------------------------------- */

extern int sdms_output_data_get_table_size(SDMS_OUTPUT_DATA *output_data, int *size)
{
	if (output_data->type != SDMS_TABLE_TYPE) {
		sdms_error_clear();
		sdms_error.number    = (char *) "02311261511";
		sdms_error.message   = (char *) "output_data is not of type table";
		sdms_error.sys_errno = 0;
		return SDMS_ERR;
	}
	SDMS_VECTOR *v = (SDMS_VECTOR *)(output_data->data);
	*size = v->length;

	return SDMS_OK;
}

extern int sdms_vector_find(SDMS_VECTOR *vector, char *name)
{
	int i;
	for (i = 0; i < vector->length; i ++)
		if (!strcmp((char *)(vector->buf[i]), name))
			return i;
	return -1;
}

extern int sdms_output_data_get_by_name (SDMS_OUTPUT_DATA *output_data, SDMS_OUTPUT_DATA **value, char *name)
{
	if (output_data == NULL) return SDMS_ERR;
	if (output_data->type != SDMS_RECORD_TYPE) {
		sdms_error_clear();
		sdms_error.number    = (char *) "02311261517";
		sdms_error.message   = (char *) "output_data is not of type record";
		sdms_error.sys_errno = 0;
		return SDMS_ERR;
	}
	int index = sdms_vector_find(output_data->desc, name);
	if (index < 0) {
		sdms_error_clear();
		sdms_error.number    = (char *) "02311261530";
		sdms_error.message   = (char *) "attribute not found";
		sdms_error.sys_errno = 0;
		return SDMS_ERR;
	}

	SDMS_VECTOR *v = (SDMS_VECTOR *)(output_data->data);
	*value = (SDMS_OUTPUT_DATA *)(v->buf[index]);

	return SDMS_OK;
}

extern int sdms_output_data_get_row(SDMS_OUTPUT_DATA *output_data, SDMS_VECTOR **row, int index)
{
	if (output_data->type != SDMS_TABLE_TYPE) {
		sdms_error_clear();
		sdms_error.number    = (char *) "02311261534";
		sdms_error.message   = (char *) "output_data is not of type table";
		sdms_error.sys_errno = 0;
		return SDMS_ERR;
	}
	SDMS_VECTOR *v = (SDMS_VECTOR *)(output_data->data);
	if (index < 0 || index >= v->length) {
		sdms_error_clear();
		sdms_error.number    = (char *) "02311261535";
		sdms_error.message   = (char *) "index out of bounds";
		sdms_error.sys_errno = 0;
		return SDMS_ERR;
	}
	*row = (SDMS_VECTOR *)(v->buf[index]);

	return SDMS_OK;
}

