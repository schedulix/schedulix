#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <ctype.h>

#include <jsstub.h>

/*
 * Private Prototypes
 */
void evalConfig(void);
void evalLine(void);
char advance(void);
char putback(void);
void readConfig(char *fileName);

/*
 * Constants
 */
const char *REPOHOST = "RepoHost";
const char *REPOPORT = "RepoPort";
const char *REPOUSER = "RepoUser";
const char *REPOPASS = "RepoPass";

/*
 * Globals
 */
char *host = NULL;
char *port = NULL;
char *user = NULL;
char *pass = NULL;

/*
 * Config Grammar:
 * file: line
 *  |    file line
 *  ;
 *
 * line: commentline
 *  |    userline
 *  |    passline
 *  |    hostline
 *  |    portline
 *  ;
 *
 * commentline: optws '#' CHARS '\n'
 *  |           optws '/' '/' CHARS '\n'
 *  ;
 *
 * userline: optws "RepoUser" optws ":|=" optws optqstring
 *  ;
 *
 * passline: optws "RepoPass" optws ":|=" optws optqstring
 *  ;
 *
 * hostline: optws "RepoHost" optws ":|=" optws optqstring
 *  ;
 *
 * portline: optws "RepoPort" optws ":|=" optws optqstring
 *  ;
 *
 * optqstring: STRING
 *  |        '"' STRING '"'
 *  ;
 *
 */

// #define DEBUG 1

static int bufptr = 0;
static char readBuf[16384];	// NO jobserver config file is this large
static int lineno = 1;
static int eof = 0;

char advance(void)
{
	if (eof) {
		fprintf(stderr, "Trying to read beyond EOF!\n");
		do_exit(1);
	}
#ifdef DEBUG
	printf("A: bufptr = %d, lineno = %d, actual character = '%c'\n", bufptr, lineno, (readBuf[bufptr] == '\0' ? '0' : readBuf[bufptr]));
#endif
	if (readBuf[bufptr] == '\0') { eof = 1; return '\0'; };
	if (readBuf[bufptr] == '\n') lineno++;
	return readBuf[bufptr++];
}

char putback(void)
{
	if (bufptr > 0) {
		--bufptr;
		if (readBuf[bufptr] == '\n') lineno--;
#ifdef DEBUG
		printf("P: bufptr = %d, lineno = %d, actual character = '%c'\n", bufptr, lineno, (readBuf[bufptr] == '\0' ? '0' : readBuf[bufptr]));
#endif
		return readBuf[bufptr - 1];
	}
	return '\0';
}

void evalLine(void)
{
	char c;
	char exp;
	int i;
	int expectQuote;
	const char *kw = NULL;
	int valueStart, valueLen;
	char **target;

	c = advance();
	for (i = 0; i < 4; ++i) {	// length of Repo
		exp = REPOHOST[i];
		if (c != exp) {
			fprintf(stderr, "Syntax error in Config File in line %d. Expected a(n) '%c', found a(n) %c\n", lineno, exp, c);
			do_exit(1);
		}
		c = advance();
	}
	// Now we need an 'H'(ost), 'P'(ort|ass), 'U'(ser)
	if ((c != 'H') && (c != 'P') && (c != 'U')) {
		fprintf(stderr, "Syntax error in Config File in line %d. Expected an 'H', a 'P' or a 'U', found a(n) %c\n", lineno, c);
		do_exit(1);
	}
	if (c == 'H') { kw = REPOHOST; target = &host; }
	if (c == 'U') { kw = REPOUSER; target = &user; }
	if (c == 'P') {
		kw = REPOPASS; target = &pass;
		c = advance();
		if (c == 'o') { kw = REPOPORT; target = &port; }
		c = putback();
	}
	for (i = 4; i < 8; ++i) {
		exp = kw[i];
		if (c != exp) {
			fprintf(stderr, "Syntax error in Config File in line %d. Expected a(n) '%c', found a(n) %c\n", lineno, exp, c);
			do_exit(1);
		}
		c = advance();
	}
	while (isblank(c)) c = advance();
	if ((c == ':') || (c == '=')) c = advance();
	else {
		fprintf(stderr, "Syntax error in Config File in line %d. Expected a ':' or a '=', found a(n) %c\n", lineno, c);
		do_exit(1);
	}
	while (isblank(c)) c = advance();
	if (c == '"') {
		expectQuote = 1;
		c = advance();
	} else {
		expectQuote = 0;
	}
	valueStart = bufptr - 1;
	valueLen = 0;

	while ((c != '\n') && (c != '\0')) {
		c = advance();
		valueLen++;
	}
	if (expectQuote) {
		c = putback();
		valueLen--;
		while(isspace(c)) {
			c = putback();
			valueLen--;
		}
		if (c != '"') {
			fprintf(stderr, "Syntax error in Config File in line %d. Expected a double quote, found a %c\n", lineno, c);
			do_exit(1);
		}
		while ((c != '\n') && (c != '\0')) c = advance();
	}
	*target = strndup(&readBuf[valueStart], valueLen);
}

void evalConfig(void)
{
	// position on first non whitespace character first
	char c = advance();
	while (isspace(c) && (c != '\0')) c = advance();

	while (c != '\0') {
		if (c == '#') {
			while ((c != '\n') && (c != '\0')) c = advance();
		} else if (c == '/') {
			c = advance();
			if (c != '/') {
				fprintf(stderr, "Syntax error in Config File in line %d. Expected a slash, found a %c\n", lineno, c);
				do_exit(1);
			}
			while ((c != '\n') && (c != '\0')) c = advance();
		} else {
			putback();
			evalLine();
		}
		c = advance();
		while(isspace(c) && (c != '\0')) c = advance();
	}
}

void readConfig(char *fileName)
{
	FILE *cf;
	struct stat statbuf;
	size_t numBytes;

	cf = fopen(fileName, "r");
	if (cf == NULL) {
		fprintf(stderr, "Error opening file %s: %s\n", fileName, strerror(errno));
		do_exit(1);
	}
	errno = 0;
	fstat(fileno(cf), &statbuf);
	if (errno != 0) {
		fprintf(stderr, "Error getting file information: %s\n", strerror(errno));
		do_exit(1);
	}

	if (statbuf.st_size > 16383) {
		fprintf(stderr, "Config File too large!\n");
		do_exit(1);
	}

	numBytes = fread(readBuf, 1, 16383, cf);
	if (errno != 0) {
		fprintf(stderr, "Error reading file: %s\n", strerror(errno));
		do_exit(1);
	}
	readBuf[numBytes] = '\0';	// terminate String; There's always room for this as we've only read maximally 16K -1 bytes

	evalConfig();			// since the buffer has been terminated, we don't need the length

	fclose(cf);
}

