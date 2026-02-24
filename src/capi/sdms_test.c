#include <stdio.h>
#include <stdlib.h>

#ifdef _WIN32
#include <winsock.h>
#endif

#include "sdms_api.h"

/* some constants / literals */
/* default values */
char * LOCALHOST = (char *) "localhost";
char * PORT      = (char *) "2506";
char * SYSTEM    = (char *) "SYSTEM";
char * PASSWD    = (char *) "G0H0ME";

/* column names */
char * NAME      = (char *) "NAME";
char * GROUPS    = (char *) "GROUPS";
char * SESSIONID = (char *) "SESSIONID";
char * USER      = (char *) "USER";

/* used commands */
char * SHOW_USER = (char *) "SHOW USER;";
char * LIST_SESSION = (char *) "LIST SESSIONS;";

void do_exit (int exit_code);

SDMS_CONNECTION *sdms_connection = NULL; // sdms_connection_open() requires initialized pointer

int main (int argc, char *argv[])
{
	char *host;
	char *port;
	char *user;
        char *pass;
	if (argc >= 2)
		host = argv[1];
	else
		host = LOCALHOST;
	if (argc >= 3)
		port = argv[2];
	else
		port = PORT;
	if (argc >= 4)
		user = argv[3];
	else
		user = SYSTEM;
	if (argc >= 5)
		pass = argv[4];
	else
		pass = PASSWD;


#ifdef _WIN32
	WSADATA wsaData;
	if (WSAStartup (MAKEWORD(1, 1), &wsaData) != 0) {
		fprintf (stderr, "WSAStartup(): Can't initialise Winsock.\n");
		do_exit (1);
	}
#endif

	if (sdms_connection_open(&sdms_connection, host, atoi(port), user, pass) != SDMS_OK) {
		sdms_error_print((char *) "Error opening sdms connection");
		do_exit(1);
	}

	int size;
	int i;

	printf("---------------------------------------------------------------------------------\n");	

	SDMS_STRING *command = NULL; // sdms_string() requires initialized pointer
	SDMS_OUTPUT *sdms_output = NULL; // sdms_command() requires initialized pointer

	if (sdms_string (&command, SHOW_USER) != SDMS_OK) {
		sdms_error_print((char *) "Error allocating command SDMS_STRING");
		do_exit(1);
	}

	if (sdms_command (&sdms_output, sdms_connection, command) != SDMS_OK) {
		sdms_error_print((char *) "Error executing command");
		do_exit(1);
	}
	
	// sdms_output_dump(sdms_output);

	SDMS_OUTPUT_DATA *name;
	sdms_output_data_get_by_name(sdms_output->data, &name, NAME);
	fprintf (stderr, "User %s is in the groups", (char *)(name->data));
	SDMS_OUTPUT_DATA *groups;
	sdms_output_data_get_by_name(sdms_output->data, &groups, GROUPS);
	int groupname_idx = sdms_vector_find(groups->desc, NAME);
	sdms_output_data_get_table_size(groups, &size);
	char sep = ' ';
	for (i = 0; i < size; i ++) {
		SDMS_VECTOR *row;
		sdms_output_data_get_row(groups, &row, i);
		SDMS_OUTPUT_DATA *groupname = (SDMS_OUTPUT_DATA *)(row->buf[groupname_idx]);
		fprintf (stderr, "%c%s", sep, (char *)(groupname->data));
		sep = ',';
	}
	fprintf (stderr, "\n");

	sdms_output_free(&sdms_output);

	printf("---------------------------------------------------------------------------------\n");	

	sdms_string_clear(command);
	if (sdms_string_append(command, LIST_SESSION) != SDMS_OK) {
		sdms_error_print((char *) "Error building command");
		do_exit(1);
	}
	if (sdms_command (&sdms_output, sdms_connection, command) != SDMS_OK) {
		sdms_error_print((char *) "Error executing command");
		do_exit(1);
	}
	// sdms_output_dump(sdms_output);

	SDMS_OUTPUT_DATA *data = sdms_output->data;
	int sessionid_idx = sdms_vector_find(data->desc, SESSIONID);
	int user_idx = sdms_vector_find(data->desc, USER);
	sdms_output_data_get_table_size(data, &size);
	for (i = 0; i < size; i ++) {
		SDMS_VECTOR *row;
		sdms_output_data_get_row(data, &row, i);
		SDMS_OUTPUT_DATA *sessionid = (SDMS_OUTPUT_DATA *)(row->buf[sessionid_idx]);
		SDMS_OUTPUT_DATA *data_user = (SDMS_OUTPUT_DATA *)(row->buf[user_idx]);
		fprintf (stderr, "User %s connected with id %s\n", (char *)(data_user->data), (char *)(sessionid->data));
	}

	sdms_output_free(&sdms_output);

	printf("---------------------------------------------------------------------------------\n");	

	sdms_string_free(&command);

	if (sdms_connection_close(&sdms_connection) != SDMS_OK) {
		sdms_error_print((char *) "Error closing sdms connection");
		do_exit(1);
	}

	return 0;
}

void do_exit (int exit_code)
{
	// Try to close connection
	if (sdms_connection != NULL) 
		sdms_connection_close(&sdms_connection);
#ifdef _WIN32
	WSACleanup();
#endif
	exit(1);
}
