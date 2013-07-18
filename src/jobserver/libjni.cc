/*
Copyright (c) 2000-2013 "independIT Integrative Technologies GmbH",
Authors: Ronald Jeninga, Dieter Stubler

schedulix Enterprise Job Scheduling System

independIT Integrative Technologies GmbH [http://www.independit.de]
mailto:contact@independit.de

This file is part of schedulix

schedulix is free software:
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

JNIEXPORT jstring JNICALL Java_de_independit_scheduler_jobserver_Utils_getVersion (JNIEnv *env, jclass)
{
	const jstring j_str = env->NewStringUTF (get_progvers());
	if (! j_str)
		die ("(04301271550) NewStringUTF() failed");

	return j_str;
}

JNIEXPORT jstring JNICALL Java_de_independit_scheduler_jobserver_Utils_getCopyright (JNIEnv *env, jclass)
{
	const jstring j_str = env->NewStringUTF (get_copyright());
	if (! j_str)
		die ("(04301271551) NewStringUTF() failed");

	return j_str;
}

JNIEXPORT jstring JNICALL Java_de_independit_scheduler_jobserver_Utils_getCompany (JNIEnv *env, jclass)
{
	const jstring j_str = env->NewStringUTF (get_company());
	if (! j_str)
		die ("(04301271552) NewStringUTF() failed");

	return j_str;
}

JNIEXPORT jint JNICALL Java_de_independit_scheduler_jobserver_Utils_getPid (JNIEnv *, jclass)
{
	return (jint) getpid();
}

static void Utils_abortProgram (JNIEnv *env, jclass clazz, jobject ri, const char *const msg)
{
	const jmethodID jmid = env->GetStaticMethodID (clazz, "abortProgram", "(Ljobserver/RepoIface;Ljava/lang/String;)V");
	if (! jmid)
		die ("(04301271607) GetStaticMethodID() failed");

	const jstring j_msg = env->NewStringUTF (msg);
	if (! j_msg)
		die ("(04301271608) NewStringUTF() failed");

	env->CallStaticVoidMethod (clazz, jmid, ri, j_msg);
}

JNIEXPORT jboolean JNICALL Java_de_independit_scheduler_jobserver_Utils_isAlive (JNIEnv *env, jclass clazz, jobject ri, jstring pid)
{
	const char *const c_pid = env->GetStringUTFChars (pid, NULL);
	if (! c_pid)
		Utils_abortProgram (env, clazz, ri, "(04301271611) GetStringUTFChars() failed");

	bool alive;
	if (! isAlive (c_pid, &alive))
		Utils_abortProgram (env, clazz, ri, errText ("(04402151824) isAlive() failed: %s", last_error));

	env->ReleaseStringUTFChars (pid, c_pid);

	return alive ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jstring JNICALL Java_de_independit_scheduler_jobserver_Utils_getEnvCmd (JNIEnv *env, jclass)
{
	const jstring j_str = env->NewStringUTF (GET_ENV);
	if (! j_str)
		die ("(04301271613) NewStringUTF() failed");

	return j_str;
}

JNIEXPORT jstring JNICALL Java_de_independit_scheduler_jobserver_Utils_chdir (JNIEnv *env, jclass, jstring path)
{
	const char *const c_path = env->GetStringUTFChars (path, NULL);
	if (! c_path)
		die ("(04301271614) GetStringUTFChars() failed");

	const int rc = chdir (c_path);
	const int errn = errno;

	env->ReleaseStringUTFChars (path, c_path);

	if (! rc)
		return (jstring) NULL;

	const jstring j_str = env->NewStringUTF (strerror (errn));
	if (! j_str)
		die ("(04301271615) NewStringUTF() failed");

	return j_str;
}

JNIEXPORT jstring JNICALL Java_de_independit_scheduler_jobserver_Utils_setBoottimeHow (JNIEnv *env, jclass, jchar how)
{
	if (set_boottime_how ((char) how))
		return (jstring) NULL;

	const jstring j_str = env->NewStringUTF (last_error);
	if (! j_str)
		die ("(04307111429) NewStringUTF() failed");

	return j_str;
}

JNIEXPORT jchar JNICALL Java_de_independit_scheduler_jobserver_Utils_getBoottimeHow (JNIEnv *, jclass)
{
	return (jchar) boottime_how;
}
