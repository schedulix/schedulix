#ifndef __SDMS_API_INTERNAL__
#define __SDMS_API_INTERNAL__

#define SDMS_STRING_CHUNK 64
#define SDMS_OUTPUT_CHUNK 512
#define SDMS_VECTOR_CHUNK 16

typedef struct {
	int isstring;
	int length;
	char *pbuf;
} SDMS_TOKEN;


extern int sdms_command_check(char *command);
extern int sdms_output(SDMS_OUTPUT **output);
extern int sdms_output_data(SDMS_OUTPUT_DATA **output_data);
extern char *sdms_token_get_string(SDMS_TOKEN *token);
extern int sdms_token_equals(SDMS_TOKEN *token, char* string);
extern int sdms_output_next_token(SDMS_TOKEN *token, char **cp);
extern int sdms_output_expect(char **cp, char c);
extern int sdms_output_parse_string(char **string, char **cp);
extern int sdms_output_parse_desc(SDMS_VECTOR **vector, char **cp);
extern int sdms_output_parse_record(SDMS_VECTOR **vector, char **cp);
extern int sdms_output_parse_table(SDMS_VECTOR **vector, char **cp);
extern int sdms_output_parse_data(SDMS_OUTPUT_DATA **output_data, char **cp);
extern int sdms_output_parse_error(SDMS_OUTPUT_ERROR **output_error, char **cp);
extern int sdms_output_parse(SDMS_OUTPUT **output, char **cp);


extern void sdms_output_desc_dump(SDMS_VECTOR *desc);
extern void sdms_output_record_dump(SDMS_VECTOR *vector);
extern void sdms_output_table_dump(SDMS_VECTOR *vector);
extern void sdms_output_data_dump(SDMS_OUTPUT_DATA *output_data);
extern void sdms_output_error_dump(SDMS_OUTPUT_ERROR *error);
extern void sdms_output_dump(SDMS_OUTPUT *output);

extern void sdms_output_data_free(SDMS_OUTPUT_DATA **output_data);

extern void sdms_output_error_free(SDMS_OUTPUT_ERROR *sdms_output_error);
extern void sdms_output_desc_free(SDMS_VECTOR **desc);
extern void sdms_output_record_free(SDMS_VECTOR **record);
extern void sdms_output_table_free(SDMS_VECTOR **table);

extern void sdms_error_clear(void);

extern void sdms_output_data_free(SDMS_OUTPUT_DATA **output_data);
extern void sdms_output_dump(SDMS_OUTPUT *output);

#endif
