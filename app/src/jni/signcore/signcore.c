
#include <jni.h>
#include <string.h>
#include <openssl/sha.h>

#include "../com_lkpower_sign_SignCore.h"
#include "../sign/sign.h"
#include "../util/util.h"

#include <android/log.h>
#define LOG "signcore.c"
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG,__VA_ARGS__)


void jString2CString(JNIEnv *env, jstring jstr)
{
	char buf[128] = {0};
	const char *str = (*env)->GetStringUTFChars(env, jstr, 0);
	LOGD("%s", str);
	(*env)->ReleaseStringUTFChars(env, jstr, str);
}

char* jStringToCString(JNIEnv *env, jstring jstr)
{
	char* r = NULL;

	// 1. 获取一个类对象
	jclass clsString = (*env)->FindClass(env, "java/lang/String");
	// 2. 构造String对象，内容为“GB2312”,作为CallObjectMethod方法中的编码字符串
	jstring strEncode = (*env)->NewStringUTF(env, "utf-8");
	// 3. 获取对象或者接口实例的方法ID
	jmethodID mid = (*env)->GetMethodID(env, clsString, "getBytes", "(Ljava/lang/String;)[B");
	// 4、调用Call<type>Method来invoke方法getBytes(String charsetName)，获取jbyteArray对象
	jbyteArray bArr = (jbyteArray) (*env)->CallObjectMethod(env, jstr, mid, strEncode);
	// 5、返回jbyteArray对象的元素数量
	jsize alen = (*env)->GetArrayLength(env, bArr);
	//6、返回一个jbyte指针对象
	jbyte* ba = (*env)->GetByteArrayElements(env, bArr, JNI_FALSE);

	if (alen > 0)
	{
		r = (char*) malloc(alen + 1); // new char[alen + 1];
		memcpy(r, ba, alen);
		r[alen] = 0;
	}

	(*env)->ReleaseByteArrayElements(env, bArr, ba, 0);

	return r;
}

JNIEXPORT jint JNICALL Java_com_lkpower_sign_SignCore_verify
  (JNIEnv *env, jobject thiz, jstring pubkey, jstring digest, jstring sign)
{
	LOGD("======> 验签 ");
	char *cPubkey = jStringToCString(env, pubkey);
	LOGD("cPubkey %s", cPubkey);
	char *cDigest = jStringToCString(env, digest);
	LOGD("cDigest %s", cDigest);
	char *cSign   = jStringToCString(env, sign);
	LOGD("cSign %s", cSign);

	unsigned char bDigest[256] = {0};
	int len = stringToBytes(cDigest, bDigest, SHA_DIGEST_LENGTH);
	LOGD("digest len %d", len);

	unsigned char bSign[256] = {0};
	len = base64decode(cSign, bSign);
	LOGD("sign len %d", len);

	size_t siglen = getRSAPublicKeyLength(cPubkey);
	LOGD("siglen %d", siglen);

	int value = verify_rsa(cPubkey, bDigest, SHA_DIGEST_LENGTH, bSign, siglen);
	LOGD("verify result %d", value);

	return value;
}

JNIEXPORT jstring JNICALL Java_com_lkpower_sign_SignCore_sign
  (JNIEnv *env, jobject thiz, jstring prikey, jstring digest, jstring passwd)
{
	LOGD("======> 签名 ");
//	jString2CString(env, digest);
	char *cDigest = jStringToCString(env, digest);
	LOGD("cDigest %s", cDigest);
	char *cPrikey = jStringToCString(env, prikey);
	LOGD("cPrikey %s", cPrikey);
	char *cPasswd = NULL;
	if (passwd == NULL) {
		LOGD("passwd is null");
	}
	else {
		cPasswd = jStringToCString(env, passwd);
		LOGD("cPasswd %s", cPasswd);
	}


	unsigned char bDigest[256] = {0};
//	int len = string2Bytes(cDigest, bDigest, 20);
	int len = stringToBytes(cDigest, bDigest, SHA_DIGEST_LENGTH);
	LOGD("digest len = %d", len);

	unsigned char sig[256] = {0};
	len = sign_rsa(cPrikey, bDigest, SHA_DIGEST_LENGTH, sig, cPasswd);
	LOGD("sign len = %d", len);
	char b64[256] = {0};
	len = base64encode(sig, b64);
	LOGD("%d %d %s",len, strlen(b64), b64);


	return (*env)->NewStringUTF(env, b64);
}

