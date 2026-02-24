#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <time.h>
#include <sys/select.h>
#include <signal.h>

#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>

#include <jsstub.h>
#include <sdms_api.h>

/* private prototypes */
void readArgs(int argc, char *argv[]);
int register_server(void);
void run(void);
int get_next_job(int *action);
void processJobs(void);
char *getTimeStamp(void);
void setSignalHandling(void);
int reconnect(void);

/*
 * Constants
 */
const char *DEFAULTHOST = "localhost";
const char *DEFAULTPORT = "2506";

const int ACT_NOP = 0;
const int ACT_SHUTDOWN = 1;
const int ACT_STARTJOB = 2;
const int ACT_ALTER = 3;
const int ACT_DISPOSE = 4;

const int MAX_RETRIES = 10;

const int TRACE_INFO = 1;
const int TRACE_WARNING = 2;
const int TRACE_DEBUG = 3;

#define AJ_BUF_SIZE 255
/*
 * Defaults
 */
const int DEFAULT_NOP_DELAY = 5;
const int DEFAULT_RECONNECT_DELAY = 30;
const int DEFAULT_TRACE_LEVEL = 1 /* TRACE_INFO */;

/*
 * Config Items, Get Next Job columns
 */
const char *NOPDELAY = "NOPDELAY";
const char *NOTIFYPORT = "NOTIFYPORT";
const char *RECONNECTDELAY = "RECONNECTDELAY";
const char *TRACELEVEL = "TRACELEVEL";

const char *COMMAND = "COMMAND";

const char *CMD_NOP = "NOP";
const char *CMD_SHUTDOWN = "SHUTDOWN";
const char *CMD_STARTJOB = "STARTJOB";
const char *CMD_ALTER = "ALTER";
const char *CMD_DISPOSE = "DISPOSE";

const char *STARTJOB_ID = "ID";
const char *STARTJOB_RUN = "RUN";
const char *STARTJOB_CMD = "CMD";
const char *STARTJOB_ARGS = "ARGS";
const char *STARTJOB_DIR = "DIR";
const char *STARTJOB_LOG = "LOG";
const char *STARTJOB_LOGAPP = "LOGAPP";
const char *STARTJOB_ERR = "ERR";
const char *STARTJOB_ERRAPP = "ERRAPP";
const char *STARTJOB_ENV = "ENV";
const char *STARTJOB_JOBENV = "JOBENV";
const char *ALTER_CONFIG = "CONFIG";


/*
 * Config Values
 */
int nop_delay;		// globally visible
int notify_port;
int reconnect_delay;
int trace_level;

char *donutName;

/*
 * Globals
 */
SDMS_CONNECTION *sdms_connection = NULL; // sdms_connection_open() requires initialized pointer

void usage(char *prog)
{
	printf("Usage: %s <conffile | user passwd [host [port]]>\n", prog);
	do_exit(1);
}

void setSignalHandling(void)
{
	// we get a SIGPIPE if the connection gets lost; we ignore this
	// The error handling will try to reconnect afterwards
	struct sigaction sa;

	sa.sa_sigaction = NULL;	// initialize this first. If it happens to be a union with sa_handler, it'll be overwritten afterwards; else it is defined void
	sa.sa_handler = SIG_IGN;
	memset(&sa.sa_mask, 0, sizeof(sa.sa_mask));
	sa.sa_flags = 0;
	sigaction(SIGPIPE, &sa, NULL);
}

void readArgs(int argc, char *argv[])
{
	int len;

	if (argc < 2) {
		usage(argv[0]);
	}
	if (argc == 2) {
		readConfig(argv[1]);
		if (user == NULL) {
			fprintf(stderr, "Corrupt config file: RepoUser not found\n");
			do_exit(1);
		}
		if (pass == NULL) {
			fprintf(stderr, "Corrupt config file: RepoPass not found\n");
			do_exit(1);
		}
		if (host == NULL)
			host = Strdup(DEFAULTHOST);
		if (port == NULL)
			port = Strdup(DEFAULTPORT);
	} else {
		user = argv[1];
		pass = argv[2];
		if (argc >= 4) {
			host = argv[3];
			if (argc == 5) {
				port = argv[4];
			} else {
				if (argc == 4) {
					port = Strdup(DEFAULTPORT);
				} else {
					usage(argv[0]);
				}
			}
		} else {
			host = Strdup(DEFAULTHOST);
			port = Strdup(DEFAULTPORT);
		}
	}
	len = strlen(user) + 5 /* DONUT */ + 1 /* dot */ + 1 /* \0 */;
	donutName = (char *) malloc(len);
	if (snprintf(donutName, len, "%s.donut", user) > len) {
		fprintf(stderr, "Error constructing File Name for saving donut\n");
		exit(1);
	};
}

int register_server(void)
{
	int len = 22 + 1 + strlen(user) + 10;
	char tmpString[len];

	SDMS_STRING *command = NULL; // sdms_string() requires initialized pointer
	SDMS_OUTPUT *sdms_output = NULL; // sdms_command() requires initialized pointer
	SDMS_OUTPUT_DATA *value;

	pid_t pid;
	pid = getpid();
	if (snprintf(tmpString, len, "register with pid = %d;", pid) > len) {
		sdms_error_print((char *) "Error creating register command");
		return SDMS_ERR;
	}

	if (sdms_string (&command, tmpString) != SDMS_OK) {
		sdms_error_print((char *) "Error converting register command to an SDMS_STRING");
		return SDMS_ERR;
	}
	if (sdms_command (&sdms_output, sdms_connection, command) != SDMS_OK) {
		sdms_error_print((char *) "Error executing command");
		return SDMS_ERR;
	}

	if (sdms_output->error != NULL) {
		fprintf(stderr, "Error occurred during register:\n");
		fprintf(stderr, "Errorcode: %s\n", sdms_output->error->errorcode);
		fprintf(stderr, "Errormessage: %s\n", sdms_output->error->errormessage);
		return SDMS_ERR;
	}

	if (sdms_output_data_get_by_name(sdms_output->data, &value, (char *) NOTIFYPORT) != SDMS_OK) {
		fprintf(stderr, "Error retrieving the notify port\n");
		notify_port = 0;
	} else {
		printf("Notify Port = %s\n", (char *)(value->data));
		notify_port = atoi(value->data);	// TODO: make this robust (use strtol)
	}
	
	if (sdms_output_data_get_by_name(sdms_output->data, &value, (char *) TRACELEVEL) != SDMS_OK) {
		trace_level = DEFAULT_TRACE_LEVEL;
	} else {
		printf("Trace Level = %s\n", (char *)(value->data));
		trace_level = atoi(value->data);	// TODO: make this robust (use strtol)
	}
	
	if (sdms_output_data_get_by_name(sdms_output->data, &value, (char *) NOPDELAY) != SDMS_OK) {
		nop_delay = DEFAULT_NOP_DELAY;
	} else {
		printf("Nop Delay = %s\n", (char *)(value->data));
		nop_delay = atoi(value->data);	// TODO: make this robust (use strtol)
	}
	
	if (sdms_output_data_get_by_name(sdms_output->data, &value, (char *) RECONNECTDELAY) != SDMS_OK) {
		reconnect_delay = DEFAULT_RECONNECT_DELAY;
	} else {
		printf("Reconnect Delay = %s\n", (char *)(value->data));
		reconnect_delay = atoi(value->data);	// TODO: make this robust (use strtol)
	}
	
	sdms_output_free(&sdms_output);
	sdms_string_free(&command); command = NULL;
	return SDMS_OK;
}

int get_next_job(int *action)
{
	SDMS_STRING *command = NULL; // sdms_string() requires initialized pointer
	SDMS_OUTPUT *sdms_output = NULL; // sdms_command() requires initialized pointer
	SDMS_OUTPUT_DATA *value;
	char alterJobCommand[AJ_BUF_SIZE + 1];
	jobinfo *info;

	if (sdms_string (&command, (char *) "get next job;") != SDMS_OK) {
		sdms_error_print((char *) "Error converting register command to an SDMS_STRING");
		return SDMS_ERR;
	}
	if (sdms_command (&sdms_output, sdms_connection, command) != SDMS_OK) {
		sdms_error_print((char *) "Error executing command");
		return SDMS_ERR;
	}
	sdms_string_free(&command); command = NULL;

	if (sdms_output->error != NULL) {
		fprintf(stderr, "Error occurred during get next job:\n");
		fprintf(stderr, "Errorcode: %s\n", sdms_output->error->errorcode);
		fprintf(stderr, "Errormessage: %s\n", sdms_output->error->errormessage);
		return SDMS_ERR;
	}

	// we always get a COMMAND
	if (sdms_output_data_get_by_name(sdms_output->data, &value, (char *) COMMAND) != SDMS_OK) {
		fprintf(stderr, "Expected a COMMAND item");
		*action = ACT_NOP;
		return SDMS_OK;
	}
	
	if (strcmp(CMD_NOP, (char *)(value->data)) == 0) {
		// in case of a NOP there's nothing to do
		*action = ACT_NOP;
	} else if (strcmp(CMD_SHUTDOWN, (char *)(value->data)) == 0) {
		// report a SHUTDOWN
		*action = ACT_SHUTDOWN;
	} else if (strcmp(CMD_STARTJOB, (char *)(value->data)) == 0) {
		// start a Job
		*action = ACT_STARTJOB;
		if (sdms_output_data_get_by_name(sdms_output->data, &value, (char *) STARTJOB_ID) != SDMS_OK) {
			fprintf(stderr, "Expected an ID while 'starting' a job\n");
			return SDMS_ERR;	// regard this as a fatal error for now
		}
		if (addInfo((char *)(value->data)) != 0) {
			// donut is filled up; we'll have to wait now until some jobs finish
			// We rely on the server to do something sane, even if we don't acknowledge this job
			// We'll ask for it again within short time.
			sdms_output_free(&sdms_output); sdms_output = NULL;
			*action = ACT_NOP;
			return SDMS_OK;
		}
		info = getLastInfo();
		
		sdms_output_free(&sdms_output); sdms_output = NULL;

		if (snprintf(alterJobCommand, AJ_BUF_SIZE, "alter job %s with state = started;", info->id) > AJ_BUF_SIZE) {
			fprintf(stderr, "Not enough space for alter job command\n");
			return SDMS_ERR;
		};
		if (sdms_string (&command, alterJobCommand) != SDMS_OK) {
			sdms_error_print((char *) "Error converting alter job command to an SDMS_STRING");
			return SDMS_ERR;
		}
		if (sdms_command (&sdms_output, sdms_connection, command) != SDMS_OK) {
			sdms_error_print((char *) "Error executing alter job command");
		}
		sdms_string_free(&command); command = NULL;
		sdms_output_free(&sdms_output); sdms_output = NULL;

		fprintf(stdout, "Started Job %s (due time = %ld)\n", info->id, (long int) info->dueTime);
		
		if (snprintf(alterJobCommand, AJ_BUF_SIZE, "alter job %s with state = running, exec pid = 0, ext pid = 0;", info->id) > AJ_BUF_SIZE) {
			fprintf(stderr, "Not enough space for alter job command\n");
			return SDMS_ERR;
		};
		if (sdms_string (&command, alterJobCommand) != SDMS_OK) {
			sdms_error_print((char *) "Error converting alter job command to an SDMS_STRING");
			return SDMS_ERR;
		}
		if (sdms_command (&sdms_output, sdms_connection, command) != SDMS_OK) {
			sdms_error_print((char *) "Error executing alter job command");
		}
		sdms_string_free(&command); command = NULL;
		sdms_output_free(&sdms_output); sdms_output = NULL;
		
	} else if (strcmp(CMD_ALTER, (char *)(value->data)) == 0) {
		// alter config
		*action = ACT_ALTER;
	} else if (strcmp(CMD_DISPOSE, (char *)(value->data)) == 0) {
		// not used
		*action = ACT_DISPOSE;
	} else {
		fprintf(stderr, "Unknown Command: %s\n", (char *)(value->data));
		*action = ACT_NOP;
	}
	
	if (sdms_output != NULL) sdms_output_free(&sdms_output);

	return SDMS_OK;
}

int reconnect(void)
{
	int failureCtr;
	if ((sdms_connection != NULL) && (sdms_connection_close(&sdms_connection) != SDMS_OK)) {
		sdms_error_print((char *) "Error closing sdms connection");
	}
	sdms_connection = NULL;
	failureCtr = 0;
	while (sdms_connection_open(&sdms_connection, host, atoi(port), user, pass) != SDMS_OK) {
		sdms_error_print((char *) "Error opening sdms connection");
		sleep(reconnect_delay);
		failureCtr++;
		if (failureCtr >= MAX_RETRIES) {
			fprintf(stderr, "Tried to connect %d times without success. Give up\n", MAX_RETRIES);
			do_exit(1);
		}
	}

	return 0;
}

char *getTimeStamp(void)
{
	char *ts;
	struct tm tmts;
	time_t now;
	int rc;

	now = time(NULL);
	gmtime_r(&now, &tmts);
	ts = (char *) Malloc(32 * sizeof(char));
	if (ts == NULL) {
		fwrite ("malloc returned NULL\n", 22, 1, stderr);	// (f)printf does a malloc
		do_exit(1);
	}
	rc = snprintf(ts, 31, "%02d-%02d-%04d %02d:%02d:%02d GMT", tmts.tm_mday, tmts.tm_mon+1, tmts.tm_year+1900, tmts.tm_hour, tmts.tm_min, tmts.tm_sec);
	if (rc < 0) {
		sdms_error_print((char *) "Error formatting timestamp");
		do_exit(1);
	}
	return ts;
}

void processJobs(void)
{
	SDMS_STRING *command = NULL; // sdms_string() requires initialized pointer
	SDMS_OUTPUT *sdms_output = NULL; // sdms_command() requires initialized pointer
	char alterJobCommand[AJ_BUF_SIZE + 1];
	char *ts = getTimeStamp();
	jobinfo *info;
	time_t now = time(NULL);

	info = getFirstInfo();
	while ((info != NULL) && (info->dueTime <= now)) {
		snprintf(alterJobCommand, AJ_BUF_SIZE, "alter job %s with state = finished, exit code = 0, timestamp = '%s';", info->id, ts);
		if (sdms_string (&command, alterJobCommand) != SDMS_OK) {
			sdms_error_print((char *) "Error converting alter job command to an SDMS_STRING");
			do_exit(1);
		}
		if (sdms_command (&sdms_output, sdms_connection, command) != SDMS_OK) {
			sdms_error_print((char *) "Error executing alter job command");
			sdms_string_free(&command); command = NULL;
			sdms_output_free(&sdms_output); sdms_output = NULL;
			return;
		}
		sdms_string_free(&command); command = NULL;
		sdms_output_free(&sdms_output); sdms_output = NULL;
		fprintf(stdout, "Finished Job %s (due time = %ld)\n", info->id, (long int) info->dueTime);
		discardFirst();		// invalidates the info structure
		info = getFirstInfo();
	}
		
	Free(ts);
}

void run(void)
{
	int n;
	int action;
	int soc = 0, rc;
	int nextDue = 0;
	struct sockaddr_in servAddr, cliAddr;
	const int yes = 1;
	fd_set fdSet;
	struct timeval tv;
#define BUFSIZE 255
	char buf[BUFSIZE + 1];
	socklen_t len = sizeof (cliAddr);

	while (register_server() != SDMS_OK) {
		sleep(reconnect_delay);
		reconnect();
	};

	if (notify_port != 0) {
		soc = socket (AF_INET, SOCK_DGRAM, 0);
		if (soc < 0) {
  			fprintf (stderr, "Can't open socket for notications (%s)\n", strerror(errno));
			soc = 0;
 		} else {
			printf("opened socket %d\n", soc);

			servAddr.sin_family = AF_INET;
			servAddr.sin_addr.s_addr = htonl (INADDR_ANY);
			servAddr.sin_port = htons (notify_port);
			setsockopt(soc, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof(int));
			rc = bind ( soc, (struct sockaddr *) &servAddr, sizeof (servAddr));
			if (rc < 0) {
				printf ("Can't bind port %d (%s)\n", notify_port, strerror(errno));
				close(soc);
				soc = -1;
			}
		}
	}

	// Restore donut state
	restoreDonut(donutName);
	while (1) {
		if (get_next_job(&action) != SDMS_OK) {
			sleep(reconnect_delay);
			reconnect();
			continue;
		} else {
			if (action == ACT_SHUTDOWN)
				break;		// shutdown leaves the while(true) loop
			if (action == ACT_NOP) {
				nextDue = getNextDuePeriod();
				if (nextDue > nop_delay) nextDue = nop_delay;	// we wait at most nop_delay seconds
			}
			if (action == ACT_STARTJOB)
				nextDue = 0;	// if a job has been started, we ask immediately for a new one
		}
		// now we sleep til the next due time
		// Then we report all due jobs as finished
		// If we are woken up by a UDP package, we do the same
		if (nextDue > 0) {
			if (soc > 0) {
				FD_ZERO(&fdSet);
				FD_SET(soc, &fdSet);
				tv.tv_sec = nextDue;
				tv.tv_usec = 0;
				if (select(soc+1, &fdSet, NULL, NULL, &tv) > 0) {
					n = recvfrom ( soc, buf, BUFSIZE, 0, (struct sockaddr *) &cliAddr, &len );
					if (n < 0) {
						fprintf(stderr, "Error receiving UDP packet\n");
					} else {
						fprintf(stderr, "received UDP packet\n");
					}
				}
			} else
				sleep(nextDue);
		}
		if (soc > 0) {
			// here we read all remaining datagrams (if there are any)
			// The next action will be to report all due jobs finished
			// and to retrieve the next job to start
			FD_ZERO(&fdSet);
			FD_SET(soc, &fdSet);
			tv.tv_sec = 0;
			tv.tv_usec = 0;
			while (select(soc+1, &fdSet, NULL, NULL, &tv) > 0) {
				n = recvfrom ( soc, buf, BUFSIZE, 0, (struct sockaddr *) &cliAddr, &len );
				if (n < 0) {
					fprintf(stderr, "Error receiving UDP packet\n");
				}
				/* it is said that these values can be changed by select().
				 * So we explicitly set them to avoid strange effects
				 */
				FD_ZERO(&fdSet);
				FD_SET(soc, &fdSet);
				tv.tv_sec = 0;
				tv.tv_usec = 0;
			}
		}
		processJobs();
	}
	saveDonut(donutName);
	// Save donut state
}

int main (int argc, char *argv[])
{
	reconnect_delay = DEFAULT_RECONNECT_DELAY;
	nop_delay = DEFAULT_NOP_DELAY;
	readArgs(argc, argv);

	setSignalHandling();

	reconnect();	// in case there's no active connection, it simply connects

	run();

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
	exit(1);
}
