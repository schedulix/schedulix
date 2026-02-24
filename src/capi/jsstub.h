#ifndef __JSSTUB_H__
#define __JSSTUB_H__

#include <sdms_api.h>
/*
 * Prototypes
 */
extern void do_exit (int exit_code);
extern void readConfig(char *fileName);
extern void usage(char *prog);

/*
 * Constants
 */
extern const char *DEFAULTHOST;
extern const char *DEFAULTPORT;
extern const char *REPOHOST;
extern const char *REPOPORT;
extern const char *REPOUSER;
extern const char *REPOPASS;

/*
 * Globals
 */
extern SDMS_CONNECTION *sdms_connection;
extern char *host;
extern char *port;
extern char *user;
extern char *pass;

extern int nop_delay;

/*
 * Donut of current jobs
 */
#define MAX_JOBS 2048
#define RUN_PERIOD 10	/* 10s run time */

typedef struct {
	char *id;
	time_t dueTime;
} jobinfo;

extern jobinfo *getFirstInfo(void);		// get the first entry of the list
extern jobinfo *getLastInfo(void);		// get the last entry of the list
extern void discardFirst(void);			// remove the first entry
extern int addInfo(char *id);			// returns 0 on success, nonzero in case of an error (no space left on device ;)
extern int getNextDuePeriod(void);		// returns the number of seconds to wait until the next job finishes

/* Donut methods */
extern int getNextDuePeriod(void);
extern int saveDonut(char *fname);
extern int restoreDonut(char *fname);

#endif
