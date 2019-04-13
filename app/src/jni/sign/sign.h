/*
 * sign.h
 *
 *  Created on: 2015-5-6
 *      Author: linger
 */

#ifndef SIGN_H_
#define SIGN_H_

int sign();

/*
 * 对文件生成 SHA1 摘要信息，然后用私钥签名并做 base64 编码
 */
int sign_sha1_rsa_base64(const char *filename, const char *prikey, const char *out_b64_file);

/*
 * 对文件生成 SHA1 摘要信息，对签名数据做 base64 解码，然后用公钥验证签名
 */
int verify_sha1_rsa_base64(const char *filename, const char *pubkey, const char *data_b64_file);

/*
 * 生成 SHA1 摘要
 */
int digest_sha1(const char *filename, char *out_md);

/*
 * 生成 SHA256 摘要
 */
int digest_sha256(const char *filename, char *out_md);

/*
 * RSA 私钥签名
 */
int sign_rsa(const char *prikey, const unsigned char *md, size_t mdlen, unsigned char *sig, char *passwd);

/*
 * base64 编码
 */
int base64encode(const unsigned char *data, char *out_b64);

/**
 * base64 解码
 */
int base64decode(const char *b64, unsigned char *out_data);

/*
 * RSA 公钥验证签名
 */
int verify_rsa(const char *pubkey, const unsigned char *md, size_t mdlen, const unsigned char *sig, size_t siglen);

int getRSAPrivateKeyLength(const char *prikey);

int getRSAPublicKeyLength(const char *pubkey);

#endif /* SIGN_H_ */
