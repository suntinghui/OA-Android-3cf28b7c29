/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_lkpower_sign_SignCore */

#ifndef _Included_com_lkpower_sign_SignCore
#define _Included_com_lkpower_sign_SignCore
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_lkpower_sign_SignCore
 * Method:    sign
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_lkpower_sign_SignCore_sign
  (JNIEnv *, jobject, jstring, jstring, jstring);

/*
 * Class:     com_lkpower_sign_SignCore
 * Method:    verify
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_lkpower_sign_SignCore_verify
  (JNIEnv *, jobject, jstring, jstring, jstring);

#ifdef __cplusplus
}
#endif
#endif