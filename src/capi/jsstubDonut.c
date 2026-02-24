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

#include <jsstub.h>

/*
 * Job Donut
 */
jobinfo jobdonut[MAX_JOBS];
int donut_start = 0;
int donut_end = 0;

/* prototypes */
jobinfo *getFirstInfo(void);
jobinfo *getLastInfo(void);
void discardFirst(void);
int addInfo(char *id);
int addFullInfo(char *id, time_t dueTime);
extern int getNextDuePeriod(void);
extern int saveDonut(char *fname);
extern int restoreDonut(char *fname);


jobinfo *getFirstInfo(void)
{
	if (donut_start == donut_end) return NULL;
	return &jobdonut[donut_start];
}

jobinfo *getLastInfo(void)
{
	if (donut_start == donut_end) return NULL;
	return &jobdonut[donut_end == 0 ? MAX_JOBS - 1 : donut_end - 1];
}

void discardFirst(void)
{
	if (donut_start == donut_end) {
		fprintf(stderr, "WARNING: trying to discard first from empty donut!\n");
		return;	// donut empty -> do nothing
	}
	if (jobdonut[donut_start].id != NULL) Free(jobdonut[donut_start].id);
	jobdonut[donut_start].id = NULL;
	if(++donut_start >= MAX_JOBS) donut_start -= MAX_JOBS;
}

int addInfo(char *id)
{
	time_t now = time(NULL);
	int nextEnd = donut_end + 1;
	if (nextEnd >= MAX_JOBS) nextEnd -= MAX_JOBS;
	if (nextEnd == donut_start) return 1;	// we would overwrite the first entry
	jobdonut[donut_end].dueTime = now + RUN_PERIOD;
	jobdonut[donut_end].id = Strdup(id);
	donut_end = nextEnd;
	return 0;
}

int addFullInfo(char *id, time_t dueTime)
{
	int nextEnd = donut_end + 1;
	if (nextEnd >= MAX_JOBS) nextEnd -= MAX_JOBS;
	if (nextEnd == donut_start) return 1;	// we would overwrite the first entry
	jobdonut[donut_end].dueTime = dueTime;
	jobdonut[donut_end].id = Strdup(id);
	donut_end = nextEnd;
	return 0;
}

extern int getNextDuePeriod(void)
{
	time_t now = time(NULL);
	if (donut_start == donut_end) return nop_delay;
	return jobdonut[donut_start].dueTime - now;
}

extern int saveDonut(char *fname)
{
	int i = donut_start;
	int myErrno;
	FILE *out = NULL;

	/* we always open and close the file. If the donut is empty this will be "reflected" in the file size */
	out = fopen(fname, "w");
	if (out == NULL) {
		myErrno = errno;
		fprintf(stderr, "Can't open file %s: %s(%d)\n", fname, strerror(myErrno), myErrno);
		return 1;
	}
	while (i != donut_end) {
		fprintf(out, "%s;%ld\n", jobdonut[i].id, (long int) jobdonut[i].dueTime);
		i++;
		if (i >= MAX_JOBS) i -= MAX_JOBS;
	}
	if (out != NULL) fclose(out);
	return 0;
}

extern int restoreDonut(char *fname)
{
	FILE *in = NULL;
	int myErrno;
	char jobId[20];	/* a long is maximally 2^63, wich corresponds to n*10^19 (for some n < 10) */
	char dueStr[20];
	time_t dueTime;
	char *endptr;
	int line = 0;
	int gotError = 0;
	int itemsRead;

	in = fopen(fname, "r");
	if (in == NULL) {
		myErrno = errno;
		fprintf(stderr, "Can't open file %s: %s(%d)\n", fname, strerror(myErrno), myErrno);
		return 1;
	}
	while (!feof(in)) {
		/* since this is just a tool for testing, we trust the file and do not real parsing
		 * Of course, this is an invitation to hackers to produce buffer overflows
		 */
		itemsRead = fscanf(in, "%s;%s\n", jobId, dueStr);
		if (itemsRead != 2) {
			fprintf(stderr, "Format error in line %d: Couldn't parse jobId and dueTime\n", line); 
			gotError = 1;
			break;
		}
		line++;
		dueTime = strtol(dueStr, &endptr, 10);	/* base 10 */
		if (*endptr != '\0') {
			fprintf(stderr, "Format error in line %d: invalid numeric value %s\n", line, dueStr); 
			gotError = 1;
			break;
		}
		addFullInfo(jobId, dueTime);
	}
	fclose(in);
	if (!gotError)
		unlink(fname);

	return gotError;
}
