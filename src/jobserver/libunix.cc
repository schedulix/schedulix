/*
Copyright (c) 2000-2013 "independIT Integrative Technologies GmbH",
Authors: Ronald Jeninga, Dieter Stubler

BICsuite!Open Enterprise Job Scheduling System

independIT Integrative Technologies GmbH [http://www.independit.de]
mailto:contact@independit.de

This file is part of BICsuite!Open

BICsuite!Open is free software:
you can redistribute it and/or modify it under the terms of the
GNU Affero General Public License as published by the
Free Software Foundation, either version 3 of the License,
or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
*/


#include "libjobserver.h"
#include "libcommon.h"

#define SIGNUM_MIN SIGHUP
#define SIGNUM_MAX SIGSYS

#define VAR_RUN_LASTBOOT "/var/run/lastboot"

static const int SIG_BASE = 128;

char boottime_how;
static const long BOOTTIME_JITTER = 5;

static const char *const PID_FMT = "%d" PID_SEP "%c%ld" JIF_SEP "%ld";

#ifdef LINUX
static const size_t CHUNK_SIZE = 512;
#endif

static volatile bool wait_error = false;
static volatile int  child_exit_status;

LIBEXPORT FILEHANDLE open_jobfile (void)
{
	FILEHANDLE fil;
	for (;;) {
		fil = fopen (job.filnam, "r+");
		if (fil)
			break;

		if (errno != ENFILE)
			die ("(04301271517) Cannot open %s", job.filnam, errno);

		sleep (FILE_REOPEN_DELAY_SECONDS);
	}

	for (;;) {
		if (lockf (fileno (fil), F_LOCK, 0) != -1)
			return fil;

		if (errno != ENOLCK)
			die ("(04301271518) Cannot lock %s", job.filnam, errno);

		sleep (FILE_REOPEN_DELAY_SECONDS);
	}
}

LIBEXPORT void close_jobfile (FILEHANDLE fil)
{
	if (fclose (fil) == EOF)
		die ("(04301271519) Cannot close %s", job.filnam, errno);
}

LIBEXPORT bool read_jobfile (FILEHANDLE fil, char **const str)
{

	struct stat buf;
	if (fstat (fileno (fil), &buf))
		RETURN_FALSE (errText ("(04402161512) Cannot stat() %s", job.filnam, errno));

	const long size = (long) buf.st_size;

	*str = (char *) malloc (size + sizeof ('\0'));
	if (! *str)
		RETURN_FALSE ("(04301271532) Out of memory");

	if (! fread (*str, size, 1, fil))
		RETURN_FALSE (errText ("(04301271533) Cannot read %s", job.filnam, errno));
	(*str) [size] = '\0';

	return true;
}

static void seek_logical_end (FILEHANDLE fil)
{
	char *buf;
	if (! read_jobfile (fil, &buf))
		die (last_error);

	const long end = strchr (buf, '\0') - buf;

	free (buf);

	if (fseek (fil, end, SEEK_SET) == -1)
		die ("(04301271525) Cannot seek %s", job.filnam, errno);
}

LIBEXPORT void append_jobfile (const char *const id, const char *const val)
{
	FILEHANDLE fil = open_jobfile();

	seek_logical_end (fil);

	int written;
	if (strchr (val, '\n') || strchr (val, '\r'))
		written = fprintf (fil, "%s %s'%d=%s\n", timestamp(), id, (int) strlen (val), val);
	else
		written = fprintf (fil, "%s %s=%s\n", timestamp(), id, val);
	if (written <= 0)
		die ("(04301271527) Cannot write %s", job.filnam, errno);

	close_jobfile (fil);
}

LIBEXPORT void append_jobfile (const char *const id, const int val)
{
	append_jobfile (id, itoa (val));
}

#ifdef LINUX

static const char *read_line (FILE *const stream)
{
	static size_t size   = CHUNK_SIZE;
	static char  *buffer = NULL;

	if (! buffer) {
		buffer = (char *) malloc (CHUNK_SIZE);
		if (! buffer)
			die ("(04301271553) Out of memory");
	}

	char    *buf_ofs  = buffer;
	unsigned buf_free = (unsigned) size;
	char    *last_pos;
	bool     done;

	do {
		if (! fgets (buf_ofs, buf_free, stream))
			return NULL;

		last_pos = buf_ofs + strlen (buf_ofs) - 1;
		done = (*last_pos == '\n');
		if (! done) {
			size += CHUNK_SIZE;
			buffer = (char *) realloc (buffer, size);
			if (! buffer)
				die ("(04301271554) Out of memory");
			buf_ofs  = buffer + size - CHUNK_SIZE - 1;
			buf_free = CHUNK_SIZE + 1;
		}
	} while (!done);
	*last_pos = '\0';

	return buffer;
}
#endif

static bool get_boottime (long *const bt)
{
	switch (boottime_how) {
	case BOOTTIME_NONE:
		*bt = 0;
		return true;

	case BOOTTIME_FILE: {
		struct stat buf;
		if (stat (VAR_RUN_LASTBOOT, &buf))
			RETURN_FALSE (errText ("(04307090020) Cannot stat() " VAR_RUN_LASTBOOT, errno));

		*bt = (long) buf.st_mtime;

		return true;
	}

	case BOOTTIME_SYSTEM:
#ifdef LINUX
	{
		const char *const BTIME = "btime ";

		FILE *const fil = fopen ("/proc/stat", "r");
		if (! fil)
			RETURN_FALSE (errText ("(04301271555) Cannot fopen() /proc/stat", errno));

		const char *line;
		while ((line = read_line (fil)))
			if (! strncmp (line, BTIME, strlen (BTIME))) {
				if (sscanf (line + strlen (BTIME), "%ld", bt) != 1)
					RETURN_FALSE ("(04301271556) Error scanning /proc/stat: unknown format");
				break;
			}

		if (fclose (fil) == EOF)
			RETURN_FALSE (errText ("(04301271557) Cannot fclose() /proc/stat", errno));
	}

	return true;
#endif

#ifdef SOLARIS
	{
		struct stat buf;
		if (stat ("/proc/1", &buf))
			RETURN_FALSE (errText ("(04301271558) Cannot stat() /proc/1", errno));

		*bt = (long) buf.st_mtime;
	}

	return true;
#endif

#ifdef NETBSD
	{
		int mib [2];
		mib [0] = CTL_KERN;
		mib [1] = KERN_BOOTTIME;

		struct timeval myboottime;
		size_t len = sizeof (myboottime);
		sysctl (mib, 2, &myboottime, &len, NULL, NULL);
		if (myboottime.tv_sec != 0)
			*bt = (long) myboottime.tv_sec;
		else
			RETURN_FALSE (errText ("(04504072319) Cannot sysctl (kern.boottime)", errno));
	}

	return true;
#endif

	default:
		char str[] = {boottime_how, 0};
		RETURN_FALSE (errText ("(04307090044) invalid boottime determination: '%s'", str));
	}
}

static const bool get_jiffies (const PID_T pid, long *const jiffy)
{
	char nam [32];

#ifdef LINUX
	snprintf (nam, sizeof (nam), "/proc/%d/stat", pid);

	FILE *const fil = fopen (nam, "r");
	if (! fil)
		RETURN_FALSE (errText ("(04301271559) Cannot fopen() %s", nam, errno));

	if (fscanf (fil, "%*d %*s %*c %*d %*d %*d %*d %*d %*u %*u %*u %*u %*u %*d %*d %*d %*d %*d %*d %*u %*u %ld", jiffy) != 1)
		RETURN_FALSE (errText ("(04301271600) Error scanning %s", nam));

	if (fclose (fil) == EOF)
		RETURN_FALSE (errText ("(04301271601) Cannot fclose() %s", nam, errno));
#endif

#ifdef SOLARIS
	snprintf (nam, sizeof (nam), "/proc/%d", (int) pid);

	struct stat buf;
	if (stat (nam, &buf))
		RETURN_FALSE (errText ("(04301271602) Cannot stat() %s", nam, errno));

	*jiffy = (long) buf.st_mtime;
#endif

#ifdef NETBSD

	int mib2 [6];
	mib2 [0] = CTL_KERN;
	mib2 [1] = KERN_PROC2;
	mib2 [2] = KERN_PROC_PID;
	mib2 [3] = pid;
	mib2 [4] = (int) sizeof (struct kinfo_proc2);
	mib2 [5] = 0;

	size_t sz2;
	if (sysctl (mib2, 6, NULL, &sz2, NULL, 0) == -1)
		RETURN_FALSE (errText ("(04504072320) Cannot sysctl()", errno));

	struct kinfo_proc2 *const kp2 = (struct kinfo_proc2 *) malloc (sz2);
	if (kp2 = == NULL)
		RETURN_FALSE ("(04504072321) Out of memory");

	size_t kpsz2 = sizeof (struct kinfo_proc2);
	mib2 [5] = sz2 / kpsz2;
	if (sysctl (mib2, 6, kp2, &sz2, NULL, 0) == -1)
		RETURN_FALSE (errText ("(04504072322) Cannot sysctl()", errno));

	*jiffy = kp2->p_ustart_usec;

	free (kp2);
#endif

	return true;
}

LIBEXPORT bool set_boottime_how (const char how)
{
	const char old_how = boottime_how;
	boottime_how = how;

	long dummy;
	const bool ok = get_boottime (&dummy);

	if (! ok)
		boottime_how = old_how;

	return ok;
}

LIBEXPORT const char *get_unique_pid (const PID_T pid)
{
	static char buf [64];

	long boottime;
	if (! get_boottime (&boottime))
		die ("(04307112117) Cannot determine boottime: %s", last_error);

	long jiffies;
	if (! get_jiffies (pid, &jiffies))
		die ("(04402151810) Cannot determine jiffies: %s", last_error);

	snprintf (buf, sizeof (buf), PID_FMT, (int) pid, boottime_how, boottime, jiffies);

	return buf;
}

extern bool isAlive (const char *const upid, bool *const alive)
{
	int pid;
	long bot, jif;
	char how;
	if (sscanf (upid, PID_FMT, &pid, &how, &bot, &jif) != 4)
		RETURN_FALSE (errText ("(04301271612) Invalid pid: %s", upid));

	if ((how != BOOTTIME_NONE) && (how != BOOTTIME_SYSTEM) && (how != BOOTTIME_FILE))
		RETURN_FALSE (errText ("(04307091722) invalid pid: %s", upid));

	long boottime;
	if (! get_boottime (&boottime))
		RETURN_FALSE (errText ("(04402151808) Cannot determine boottime: %s", last_error));

	long jiffies;
	if (! get_jiffies (pid, &jiffies))
		*alive = false;
	else
		*alive = (labs (bot -  boottime) <= BOOTTIME_JITTER) && (jif == jiffies);

	return true;
}

static int set_all_signals (const struct sigaction aktschn)
{
	for (int signum = SIGNUM_MIN; signum <= SIGNUM_MAX; ++signum)
		if ((signum != SIGKILL) && (signum != SIGSTOP)) {
			const int rc = sigaction (signum, &aktschn, NULL);
			if (rc)
				return rc;
		}

	return 0;
}

LIBEXPORT void ignore_all_signals (void)
{
	struct sigaction aktschn;

#ifdef NETBSD
	sigemptyset (&aktschn.sa_mask);

	aktschn.sa_flags   = 0;
#endif
	aktschn.sa_handler = SIG_IGN;

	const int rc = set_all_signals (aktschn);
	if (rc)
		die ("(04303271529) Cannot ignore signals", rc);
}

static void default_all_signals (void)
{
	struct sigaction aktschn;
	aktschn.sa_handler = SIG_DFL;

	const int rc = set_all_signals (aktschn);
	if (rc)
		giveup (FEIL_STATUS_CHILD_ERROR, "(04303252229) Cannot default signals", rc);
}

static void signal_handler (int sign)
{
	if (wait ((void *) &child_exit_status) == -1)
		wait_error = true;
}

static bool install_child_handler (void)
{
	struct sigaction sigact;
	sigact.sa_handler = signal_handler;
	sigemptyset (&sigact.sa_mask);
	sigact.sa_flags = SA_NOCLDSTOP;
	if (sigaction (SIGCHLD, &sigact, NULL))
		RETURN_FALSE (errText ("(04301271603) Cannot sigaction()", errno));

	sigset_t my_sigset;
	sigemptyset (&my_sigset);
	sigaddset (&my_sigset, SIGCHLD);
	if (sigprocmask (SIG_BLOCK, &my_sigset, NULL))
		RETURN_FALSE (errText ("(04301271604) Cannot sigprocmask()", errno));

	return true;
}

static bool start_child (void)
{
	if (! install_child_handler())
		RETURN_FALSE (errText ("(04301271547) %s", last_error));

	const pid_t pid = fork();
	if (pid == -1)
		RETURN_FALSE (errText ("(04301271548) Cannot fork", errno));

	if (pid)
		return true;

	append_jobfile (FEIL_EXTPID, get_unique_pid (getpid()));

	if (job.verboselogs)
		verbose_print_started();

	default_all_signals();

	append_jobfile (FEIL_STATUS, FEIL_STATUS_RUNNING);

	if (setsid() == -1) {

	}

	if (job.usepath) {
		execvp (job.command, job.args);
		giveup (FEIL_STATUS_CHILD_ERROR, "(04301271542) Cannot execute %s", job.command, errno);
	} else {
		execv (job.command, job.args);
		giveup (FEIL_STATUS_CHILD_ERROR, "(04301271543) Cannot execute %s", job.command, errno);
	}

	return true;
}

static bool wait_for_child (void)
{
	sigset_t emptyset, suspendset;

	sigemptyset (&emptyset);
	if (sigprocmask (SIG_BLOCK, &emptyset, &suspendset))
		RETURN_FALSE (errText ("(04301271605) Cannot sigprocmask()", errno));

	sigdelset (&suspendset, SIGCHLD);
	sigsuspend (&suspendset);

	if (wait_error)
		RETURN_FALSE (errText ("(04301271606) Cannot wait()", errno));

	return true;
}

LIBEXPORT bool execute_child (int *const rc)
{
	const bool ok = start_child() && wait_for_child();
	if (ok) {
		if (WIFSIGNALED (child_exit_status))
			*rc = SIG_BASE + WTERMSIG (child_exit_status);
		else if (WIFEXITED (child_exit_status))
			*rc = WEXITSTATUS (child_exit_status);
		else
			*rc = 0;
	}

	return ok;
}

extern "C" {
	extern void _init (void)
	{
		if (! set_boottime_how (BOOTTIME_DEFAULT))
			die ("(04307111431) %s", last_error);
	}
}
