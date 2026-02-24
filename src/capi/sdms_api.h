#ifndef __SDMS_API__
#define __SDMS_API__

#define SDMS_OK  0
#define SDMS_ERR 1

#define SDMS_SCALAR_TYPE 0
#define SDMS_RECORD_TYPE 1
#define SDMS_TABLE_TYPE  2

typedef struct {
	char *number;
	int  number_allocated;
	char *message;
	int  message_allocated;
	int  sys_errno;
} SDMS_ERROR;

typedef struct {
	int socket;
} SDMS_CONNECTION;

typedef struct {
	void **buf;
	int    length;
	int    bufsize;
} SDMS_VECTOR;

typedef struct {
	int type;
	char *title;
	SDMS_VECTOR *desc;
	void *data;
} SDMS_OUTPUT_DATA;

typedef struct {
	char *errorcode;
	char *errormessage;
} SDMS_OUTPUT_ERROR;

typedef struct {
	SDMS_OUTPUT_DATA *data;
	SDMS_OUTPUT_ERROR *error;
	char *feedback;
} SDMS_OUTPUT;

typedef struct {
	char *buf;
	int   bufsize;
} SDMS_STRING;

extern int sdms_connection_open(SDMS_CONNECTION **connection, char *host, int port, char *user, char *password);
extern int sdms_command(SDMS_OUTPUT **output, SDMS_CONNECTION *connection, SDMS_STRING *command);
extern int sdms_connection_close(SDMS_CONNECTION **connection);

extern int sdms_string(SDMS_STRING **sdms_string, char *s);
extern int sdms_string_append(SDMS_STRING *string, char *text);
extern void sdms_string_clear(SDMS_STRING *string);
extern void sdms_string_free(SDMS_STRING **string);

extern int sdms_vector(SDMS_VECTOR **vector);
extern int sdms_vector_append(SDMS_VECTOR *vector, void *data);
extern void sdms_vector_free(SDMS_VECTOR **vector);

extern void sdms_output_free(SDMS_OUTPUT **output);

extern void sdms_error_print(char *text);
extern void sdms_error_clear(void);

extern int sdms_output_data_get_table_size(SDMS_OUTPUT_DATA *output_data, int *size);
extern int sdms_vector_find(SDMS_VECTOR *vector, char *name);
extern int sdms_output_data_get_by_name (SDMS_OUTPUT_DATA *output_data, SDMS_OUTPUT_DATA **value, char *name);
extern int sdms_output_data_get_string(SDMS_OUTPUT_DATA *output_data, char **value);
extern int sdms_output_data_get_row(SDMS_OUTPUT_DATA *output_data, SDMS_VECTOR **row, int index);

/* trace malloc/free */
#define MALLOC_TRACE 0
#if MALLOC_TRACE > 0
#define Free(x)	myFree(x, __LINE__, __FILE__)
#define Malloc(x) myMalloc(x, __LINE__, __FILE__)
#define Realloc(x, s) myRealloc(x, s, __LINE__, __FILE__)
#define Strdup(a) myStrdup(a, __LINE__, __FILE__)
#else
#define Free(x)	free(x)
#define Malloc(x) malloc(x)
#define Realloc(x, s) realloc(x, s)
#define Strdup(a) strdup(a)
#endif

extern void * myMalloc(size_t s, int line, char *f);
extern void * myRealloc(void *orig, size_t s, int line, char *f);
extern void myFree(void *p, int line, char *f);
extern char * myStrdup(const char *s, int line, char *f);
#endif
