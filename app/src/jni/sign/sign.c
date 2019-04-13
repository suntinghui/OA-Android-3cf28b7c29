/*
 * sign.c
 *
 *  Created on: 2015-5-6
 *      Author: linger
 */

#include <stdio.h>
#include <string.h>
#include <time.h>

#include <openssl/rsa.h>
#include <openssl/evp.h>
#include <openssl/objects.h>
#include <openssl/x509.h>
#include <openssl/err.h>
#include <openssl/pem.h>
#include <openssl/ssl.h>
#include "util.h"

#define EXEC_SUCCESS 1
#define EXEC_FAIL    0

#define BUFFER_SIZE 2048
#define READ_SIZE 1024
#define READ_COUNT 1

// 读取私钥文件错误
#define SIGN_ERROR_CODE_201 -201

// 从文件中获取私钥错误
#define SIGN_ERROR_CODE_202 -202

// 分配公钥算法上下文环境错误
#define SIGN_ERROR_CODE_203 -203

// 初始化签名公钥算法上下文环境错误
#define SIGN_ERROR_CODE_204 -204

// 设置RSA填充模式错误
#define SIGN_ERROR_CODE_205 -205

// 设置消息摘要算法类型错误
#define SIGN_ERROR_CODE_206 -206

// 签名错误
#define SIGN_ERROR_CODE_207 -207

// 读取公钥证书文件错误
#define SIGN_ERROR_CODE_208 -208

// 获取公钥证书错误
#define SIGN_ERROR_CODE_209 -209

// 从公钥证书中获取公钥错误
#define SIGN_ERROR_CODE_210 -210

// 初始化验签公钥算法上下文环境错误
#define SIGN_ERROR_CODE_211 -211

// 验证签名错误
#define SIGN_ERROR_CODE_212 -212



int sign() {
	printf("\n------ sign ------\n");

    int err;
    int sig_len;
    unsigned char sig_buf[1024] = {0};
//    static char certfile[] = "cert.pem";
//    static char keyfile[] = "key.pem";
    static char certfile[] = "client.crt";
    static char keyfile[] = "client_pkcs8_nocrypt.key";
//    static char data[] = "I owe you...";
    static char data[] = "abc";
    EVP_MD_CTX md_ctx;
    EVP_PKEY *pkey;
    FILE *fp;
    X509 *x509;

    /*
     * Just load the crypto library error strings, SSL_load_error_strings()
     * loads the crypto AND the SSL ones
     */
    /* SSL_load_error_strings(); */
    ERR_load_crypto_strings();

    /* Read private key */

    fp = fopen(keyfile, "r");
    if (fp == NULL) {
    	printf("error\n");
        exit(1);
    }
    pkey = PEM_read_PrivateKey(fp, NULL, NULL, NULL);
    fclose(fp);

    if (pkey == NULL) {
        ERR_print_errors_fp(stderr);
        exit(1);
    }

    /* Do the signature */

    int result = EVP_SignInit(&md_ctx, EVP_sha1());
    printf("result = %d\n", result);
    result = EVP_SignUpdate(&md_ctx, data, strlen(data));
    printf("result = %d\n", result);
    sig_len = sizeof(sig_buf);
    printf("sig_len = %d\n", sig_len);
    err = EVP_SignFinal(&md_ctx, sig_buf, &sig_len, pkey);
    printf("result = %d\n", err);
    printf("sig_len = %d, sizeof = %lu, strlen = %lu\n", sig_len, sizeof(sig_buf), strlen(sig_buf));

    int i;
    for (i=0; i<sig_len; i++) {
    	printf("%x ", sig_buf[i]);
    }
    printf("\n");
    if (err != 1) {
        ERR_print_errors_fp(stderr);
        exit(1);
    }

    EVP_PKEY_free(pkey);

    // base64 encode
    printf("base64 encode\n");
	char base64out[1024];
	result = EVP_EncodeBlock(base64out, sig_buf, sig_len);
	printf("result = %d, len = %lu\n", result, strlen(base64out));
	printf("%s\n", base64out);

	// base64 decode
	char base64out2[1024];
	result = EVP_DecodeBlock(base64out2, base64out, strlen(base64out));
	printf("result = %d, len = %lu\n", result, strlen(base64out2));
	for (i = 0; i < 128; i++) {
		printf("%x ", base64out2[i]);
	}
	printf("\n");

    /* Read public key */

    fp = fopen(certfile, "r");
    if (fp == NULL)
        exit(1);
    x509 = PEM_read_X509(fp, NULL, NULL, NULL);
    fclose(fp);

    if (x509 == NULL) {
        ERR_print_errors_fp(stderr);
        exit(1);
    }

    /* Get public key - eay */
    pkey = X509_get_pubkey(x509);
    if (pkey == NULL) {
        ERR_print_errors_fp(stderr);
        exit(1);
    }

    /* Verify the signature */

    result = EVP_VerifyInit(&md_ctx, EVP_sha1());
    printf("result = %d\n", result);
    result = EVP_VerifyUpdate(&md_ctx, data, strlen((char *)data));
    printf("result = %d\n", result);
//    err = EVP_VerifyFinal(&md_ctx, sig_buf, sig_len, pkey);
    err = EVP_VerifyFinal(&md_ctx, base64out2, 128, pkey);
    printf("result = %d\n", err);
    EVP_PKEY_free(pkey);

    if (err != 1) {
        ERR_print_errors_fp(stderr);
        exit(1);
    }
    printf("Signature Verified Ok.\n");
    return (0);
}


/*
 * 对文件生成 SHA1 摘要信息，然后用私钥签名并做 base64 编码
 */
int sign_sha1_rsa_base64(const char *filename, const char *prikey, const char *out_b64_file) {
	printf("\n------ sign_sha1_rsa_base64 ------\n");

    int sig_len;
    unsigned char sig_buf[4096];
    EVP_MD_CTX md_ctx;
    EVP_PKEY *pkey = NULL;
    FILE *fp = NULL, *src = NULL, *dest = NULL;
    // 从文件读取数据的缓冲区
    unsigned char buf[BUFFER_SIZE];

    ERR_load_crypto_strings();

    /* Read private key */
    fp = fopen(prikey, "r");
    if (fp == NULL) {
    	printf("读取私钥文件失败\n");
        exit(-1);
    }
    pkey = PEM_read_PrivateKey(fp, NULL, NULL, NULL);
    fclose(fp);

    if (pkey == NULL) {
        ERR_print_errors_fp(stderr);
        exit(-1);
    }

    src = fopen(filename, "r");
    if (src == NULL) {
    	printf("打开文件失败\n");
    	exit(-1);
    }

    // 签名
    int result = EVP_SignInit(&md_ctx, EVP_sha1());
    printf("init result = %d\n", result);
    // 摘要
	while (!feof(src)) {
		int read_len = fread(buf, READ_SIZE, READ_COUNT, src);
		printf("read_len = %d, buf_sizeof = %lu, buf_strlen = %lu\n", read_len, sizeof(buf), strlen(buf));
		result = EVP_SignUpdate(&md_ctx, buf, strlen(buf));
		printf("update result = %d\n", result);
	}
	result = fclose(src);
	printf("close src result = %d\n", result);

    sig_len = sizeof(sig_buf);
    printf("sig_len = %d, strlen = %lu\n", sig_len, strlen(sig_buf));
    result = EVP_SignFinal(&md_ctx, sig_buf, &sig_len, pkey);
    printf("final result = %d\n", result);
    printf("sig_len = %d, sizeof = %lu, strlen = %lu\n", sig_len, sizeof(sig_buf), strlen(sig_buf));

    int i;
    for (i=0; i<sig_len; i++) {
    	printf("%x ", sig_buf[i]);
    }
    printf("\n");

    if (result != 1) {
        ERR_print_errors_fp(stderr);
        exit(-1);
    }

    EVP_PKEY_free(pkey);

    // base64 encode
	printf("base64 encode\n");
	char base64out[1024];
	result = EVP_EncodeBlock(base64out, sig_buf, sig_len);
	printf("result = %d, len = %lu\n", result, strlen(base64out));
	printf("%s\n", base64out);

	dest = fopen(out_b64_file, "w");
	if (dest == NULL) {
		printf("打开文件失败\n");
		exit(-1);
	}
	int write_len = fwrite(base64out, strlen(base64out), 1, dest);
	printf("write_len = %d\n", write_len);
	fclose(dest);

	return 1;
}

/*
 * 对文件生成 SHA1 摘要信息，对签名数据做 base64 解码，然后用公钥验证签名
 */
int verify_sha1_rsa_base64(const char *filename, const char *pubkey, const char *data_b64_file) {
	printf("\n------ verify_sha1_rsa_base64 ------\n");

	long int b64_file_len;
	EVP_MD_CTX md_ctx;
	EVP_PKEY *pkey;
	FILE *fp, *src, *dest;
	X509 *x509;
	// 从文件读取数据的缓冲区
	unsigned char buf[BUFFER_SIZE] = {0};
	// 从文件读取 base64 编码的数据
	char b64_buf[BUFFER_SIZE] = {0};

	ERR_load_crypto_strings();

	/* Read public key */

	fp = fopen(pubkey, "r");
	if (fp == NULL) {
		printf("打开公钥证书文件失败\n");
		exit(1);
	}

	x509 = PEM_read_X509(fp, NULL, NULL, NULL);
	fclose(fp);

	if (x509 == NULL) {
		printf("获取公钥证书失败\n");
		ERR_print_errors_fp(stderr);
		exit(1);
	}

	/* Get public key */
	pkey = X509_get_pubkey(x509);
	if (pkey == NULL) {
		printf("从公钥证书中获取公钥失败\n");
		ERR_print_errors_fp(stderr);
		exit(1);
	}

	// 读取被签名并且 base64 编码的数据
	dest = fopen(data_b64_file, "r");
	if (dest == NULL) {
		printf("打开文件失败\n");
		exit(-1);
	}

	int result = fseek(dest, 0, SEEK_END);
	printf("result = %d\n", result);
	b64_file_len = ftell(dest);
	printf("b64_file_len = %ld\n", b64_file_len);
	rewind(dest);

	printf("b64_buf_sizeof = %lu, b64_buf_strlen = %lu\n", sizeof(b64_buf), strlen(b64_buf));
	int read_len = fread(b64_buf, b64_file_len, 1, dest);
	printf("read_len = %d, b64_buf_sizeof = %lu, b64_buf_strlen = %lu\n", read_len, sizeof(b64_buf), strlen(b64_buf));
	printf("%s\n", b64_buf);
	fclose(dest);

	// base64 decode
	unsigned char base64out[1024];
	printf("base64out_sizeof = %lu, base64out_strlen = %lu\n", sizeof(base64out), strlen(base64out));
	result = EVP_DecodeBlock(base64out, b64_buf, strlen(b64_buf));
	printf("result = %d, base64out_sizeof = %lu, base64out_strlen = %lu\n", result, sizeof(base64out), strlen(base64out));
	int i;
	for (i = 0; i < 128; i++) {
		printf("%x ", base64out[i]);
	}
	printf("\n");


	src = fopen(filename, "r");
	if (src == NULL) {
		printf("打开文件失败\n");
		exit(-1);
	}

	// 验证签名
	result = EVP_VerifyInit(&md_ctx, EVP_sha1());
	printf("init result = %d\n", result);
	// 摘要
	while (!feof(src)) {
		int read_len = fread(buf, READ_SIZE, READ_COUNT, src);
		printf("read_len = %d, buf_sizeof = %lu, buf_strlen = %lu\n", read_len,
				sizeof(buf), strlen(buf));
		result = EVP_VerifyUpdate(&md_ctx, buf, strlen(buf));
		printf("update result = %d\n", result);
	}
	result = fclose(src);
	printf("close src result = %d\n", result);

	result = EVP_VerifyFinal(&md_ctx, base64out, 128, pkey);
	printf("final result = %d\n", result);

	EVP_PKEY_free(pkey);

	if (result != 1) {
		printf("验证签证失败\n");
		ERR_print_errors_fp(stderr);
		exit(-1);
	}
	printf("======> Signature Verified Ok.\n");

	return 1;
}

/*
 * 生成 SHA1 摘要
 */
int digest_sha1(const char *filename, unsigned char *data) {
	write_log("计算sha1摘要");

	// 存放摘要数据
//	unsigned char data[SHA_DIGEST_LENGTH];
	// 从文件读取数据的缓冲区
	unsigned char buf[2048];
	int md_len = 0;

	memset(data, 0, sizeof(data));
	memset(buf, 0, sizeof(buf));

	EVP_MD_CTX ctx;
	EVP_MD_CTX_init(&ctx);
	EVP_DigestInit_ex(&ctx, EVP_sha1(), NULL);

	FILE *file = fopen(filename, "r");
	if (file == NULL) {
		write_log("打开文件失败");
		return EXEC_FAIL;
	}

//	int count = 0;
	while ((fgets(buf, 2048, file)) != NULL) {
//		count = count + strlen(buf);
//		char temp [10];
//		sprintf(temp, "%d", count);
//		write_log(temp);
		EVP_DigestUpdate(&ctx, buf, strlen(buf));
	}

	EVP_DigestFinal_ex(&ctx, data, &md_len);
	// 使用该函数释放 ctx 占用的资源，如果使用 _ex 系列函数，这是必须调用的
	EVP_MD_CTX_cleanup(&ctx);

//	int j;
//	for (j = 0; j < SHA_DIGEST_LENGTH; j++) {
//		sprintf(&(out_md[j * 2]), "%02x", data[j]);
//	}
//	write_log(out_md);
//	char temp [10];
//	sprintf(temp, "%d", md_len);
//	write_log(temp);

	return EXEC_SUCCESS;
}

/*
 * 生成 SHA256 摘要
 */
int digest_sha256(const char *filename, unsigned char *data) {
	write_log("计算sha256摘要");

	// 存放摘要数据
//	unsigned char data[SHA256_DIGEST_LENGTH];
	// 从文件读取数据的缓冲区
	unsigned char buf[2048];
	int md_len = 0;

	memset(data, 0, sizeof(data));
	memset(buf, 0, sizeof(buf));

	EVP_MD_CTX ctx;
	EVP_MD_CTX_init(&ctx);
	EVP_DigestInit_ex(&ctx, EVP_sha256(), NULL);

	FILE *file = fopen(filename, "r");
	if (file == NULL) {
		write_log("打开文件失败");
		return EXEC_FAIL;
	}

//	int count = 0;
	while ((fgets(buf, 2048, file)) != NULL) {
//		count = count + strlen(buf);
//		char temp [10];
//		sprintf(temp, "%d", count);
//		write_log(temp);
		EVP_DigestUpdate(&ctx, buf, strlen(buf));
	}

	EVP_DigestFinal_ex(&ctx, data, &md_len);
	// 使用该函数释放 ctx 占用的资源，如果使用 _ex 系列函数，这是必须调用的
	EVP_MD_CTX_cleanup(&ctx);

//	int j;
//	for (j = 0; j < SHA256_DIGEST_LENGTH; j++) {
//		sprintf(&(out_md[j * 2]), "%02x", data[j]);
//	}
//	write_log(out_md);
//	char temp [10];
//	sprintf(temp, "%d", md_len);
//	write_log(temp);

	return EXEC_SUCCESS;
}

/*
 * RSA 私钥签名
 *
 * 签名正确则返回签名的长度，失败则返回错误码
 */
int sign_rsa(const char *prikey, const unsigned char *md, size_t mdlen, unsigned char *sig, char *passwd) {
	write_log("私钥签名");

//	unsigned char *sig = NULL;
	size_t siglen;
	EVP_PKEY *pkey;
	FILE *fp = NULL;
	EVP_PKEY_CTX *ctx;

	// 读取私钥文件
	fp = fopen(prikey, "r");
	if (fp == NULL) {
		printf("读取私钥文件失败\n");
		return SIGN_ERROR_CODE_201;
	}

	// 这行代码必须提前加载
	OpenSSL_add_all_algorithms();

	// 读取私钥
	if (passwd != NULL) {
		pkey = PEM_read_PrivateKey(fp, NULL, 0, passwd);
	}
	else {
		pkey = PEM_read_PrivateKey(fp, NULL, NULL, NULL);
	}
	fclose(fp);

	if (pkey == NULL) {
		ERR_print_errors_fp(stderr);
		return SIGN_ERROR_CODE_202;
	}

	ctx = EVP_PKEY_CTX_new(pkey, NULL);
	if (ctx == NULL) {
		write_log("EVP_PKEY_CTX_new 失败");
		return SIGN_ERROR_CODE_203;
	}

	int result = EVP_PKEY_sign_init(ctx);
	if (result != 1) {
		write_log("EVP_PKEY_sign_init 失败");
		return SIGN_ERROR_CODE_204;
	}
	result = EVP_PKEY_CTX_set_rsa_padding(ctx, RSA_PKCS1_PADDING);
	if (result <= 0) {
		write_log("EVP_PKEY_CTX_set_rsa_padding 失败");
		return SIGN_ERROR_CODE_205;
	}
//	result = EVP_PKEY_CTX_set_signature_md(ctx, EVP_sha256());
	result = EVP_PKEY_CTX_set_signature_md(ctx, EVP_sha1());
	if (result <= 0) {
		write_log("EVP_PKEY_CTX_set_signature_md 失败");
		return SIGN_ERROR_CODE_206;
	}

//	result = EVP_PKEY_sign(ctx, NULL, &siglen, md, mdlen);
//	if (result != 1) {
//		write_log("EVP_PKEY_sign 失败");
//		return EXEC_FAIL;
//	}
//
//	sig = OPENSSL_malloc(siglen);
//	if (!sig) {
//
//		return EXEC_FAIL;
//	}

	result = EVP_PKEY_sign(ctx, sig, &siglen, md, mdlen);
	printf("siglen = %lu\n", siglen);
	if (result != 1) {
		write_log("签名失败");
		return SIGN_ERROR_CODE_207;
	}

	EVP_PKEY_free(pkey);
	write_log("签名成功");

	return siglen;
}

/*
 * RSA 公钥验证签名
 *
 * 验证正确则返回1，失败则返回错误码
 */
int verify_rsa(const char *pubkey, const unsigned char *md, size_t mdlen, const unsigned char *sig, size_t siglen) {
//int verify_rsa(const char *pubkey, const unsigned char *md, unsigned char *data) {
	write_log("公钥签名");

	EVP_PKEY *pkey;
	FILE *fp = NULL;
	EVP_PKEY_CTX *ctx;
	X509 *x509;

	// 读取公钥证书文件
	fp = fopen(pubkey, "r");
	if (fp == NULL) {
		printf("打开公钥证书文件失败\n");
		return SIGN_ERROR_CODE_208;
	}

	x509 = PEM_read_X509(fp, NULL, NULL, NULL);
	fclose(fp);

	if (x509 == NULL) {
		printf("获取公钥证书失败\n");
		return SIGN_ERROR_CODE_209;
	}

	// 获取公钥
	pkey = X509_get_pubkey(x509);
	if (pkey == NULL) {
		printf("从公钥证书中获取公钥失败\n");
		return SIGN_ERROR_CODE_210;
	}

	ctx = EVP_PKEY_CTX_new(pkey, NULL);
	if (ctx == NULL) {
		write_log("EVP_PKEY_CTX_new 失败");
		return SIGN_ERROR_CODE_203;
	}
	int result = EVP_PKEY_verify_init(ctx);
	if (result != 1) {
		write_log("EVP_PKEY_verify_init 失败");
		return SIGN_ERROR_CODE_211;
	}
	result = EVP_PKEY_CTX_set_rsa_padding(ctx, RSA_PKCS1_PADDING);
	if (result <= 0) {
		write_log("EVP_PKEY_CTX_set_rsa_padding 失败");
		return SIGN_ERROR_CODE_205;
	}
//	result = EVP_PKEY_CTX_set_signature_md(ctx, EVP_sha256());
	result = EVP_PKEY_CTX_set_signature_md(ctx, EVP_sha1());
	if (result <= 0) {
		write_log("EVP_PKEY_CTX_set_signature_md 失败");
		return SIGN_ERROR_CODE_206;
	}

	result = EVP_PKEY_verify(ctx, sig, siglen, md, mdlen);
	if (result != 1) {
		write_log("验证签名不正确");
		return SIGN_ERROR_CODE_212;
	}
	EVP_PKEY_free(pkey);
	write_log("验证签名成功");

	return EXEC_SUCCESS;
}

/*
 * base64 编码
 */
int base64encode(const unsigned char *data, char *out_b64) {
	write_log("BASE64编码");

	char len[16];
	sprintf(len, "%lu", strlen(data));
	write_log(len);

	int result = EVP_EncodeBlock(out_b64, data, strlen(data));
	write_log(out_b64);

	// 输出编码后的长度
	char temp[16];
	sprintf(temp, "%d", result);
	write_log(temp);

	return result;
}

/**
 * base64 解码
 */
int base64decode(const char *b64, unsigned char *out_data) {
	write_log("BASE64解码");

	char len[16];
	sprintf(len, "%lu", strlen(b64));
	write_log(len);

	int result = EVP_DecodeBlock(out_data, b64, strlen(b64));

	// 输出解码后的长度
	char temp[16];
	sprintf(temp, "%d", result);
	write_log(temp);

	return result;
}

int getRSAPrivateKeyLength(const char *prikey) {
	write_log("获取私钥长度");
	EVP_PKEY *pkey;
	FILE *fp = NULL;
	EVP_PKEY_CTX *ctx;

	// 读取私钥文件
	fp = fopen(prikey, "r");
	if (fp == NULL) {
		printf("读取私钥文件失败\n");
		return SIGN_ERROR_CODE_201;
	}

	// 读取私钥
	pkey = PEM_read_PrivateKey(fp, NULL, NULL, NULL);
	fclose(fp);

	if (pkey == NULL) {
		ERR_print_errors_fp(stderr);
		return SIGN_ERROR_CODE_202;
	}

	RSA *rsa = EVP_PKEY_get1_RSA(pkey);

	int len = RSA_size(rsa);
	return len;
}

int getRSAPublicKeyLength(const char *pubkey) {
	write_log("获取公钥长度");

	EVP_PKEY *pkey;
	FILE *fp = NULL;
	EVP_PKEY_CTX *ctx;
	X509 *x509;

	// 读取公钥证书文件
	fp = fopen(pubkey, "r");
	if (fp == NULL) {
		write_log("打开公钥证书文件失败\n");
		return SIGN_ERROR_CODE_208;
	}

	x509 = PEM_read_X509(fp, NULL, NULL, NULL);
	fclose(fp);

	if (x509 == NULL) {
		write_log("获取公钥证书失败\n");
		return SIGN_ERROR_CODE_209;
	}

	// 获取公钥
	pkey = X509_get_pubkey(x509);
	if (pkey == NULL) {
		write_log("从公钥证书中获取公钥失败\n");
		return SIGN_ERROR_CODE_210;
	}

	RSA *rsa = EVP_PKEY_get1_RSA(pkey);

	int len = RSA_size(rsa);
	return len;
}

int pass_cb(char *buf, int size, int rwflag, void *u) {
	int len;
	char *tmp;
	/* We'd probably do something else if 'rwflag' is 1 */
//	printf("Enter pass phrase for \"%s\"\n", u);

	/* get pass phrase, length 'len' into 'tmp' */
//	tmp = "c222222";
//	len = strlen(tmp);

//	if (len <= 0) return 0;
	/* if too long, truncate */
//	if (len > size) len = size;
//	memcpy(buf, tmp, len);

	printf("请输入密钥并回车：");
	scanf("%s", buf);
	printf("密钥是 %s\n", buf);

//	if (key) {
//		strcpy(buf, key);
//	}
//	else {
//		if (rwflag == 1) {
//			printf("加密\n");
//		}
//		else {
//			printf("解密\n");
//		}
//		scanf("%s", buf);
//	}
	len = strlen(buf);
	printf("密钥长度是 len = %d\n", len);

	return len;
}

int getRSAPrivateKeyWithPassword(const char *prikey) {
	write_log("获取带密码的私钥");
	EVP_PKEY *pkey;
	FILE *fp = NULL;
	EVP_PKEY_CTX *ctx;

	// 读取私钥文件
	fp = fopen(prikey, "r");
	if (fp == NULL) {
		printf("读取私钥文件失败\n");
		return SIGN_ERROR_CODE_201;
	}

	// 这行代码必须提前加载
	OpenSSL_add_all_algorithms();

	// 读取私钥，直接传递密钥密码
	pkey = PEM_read_PrivateKey(fp, NULL, 0, "c222222");
	fclose(fp);

	if (pkey == NULL) {
		ERR_print_errors_fp(stderr);
		return SIGN_ERROR_CODE_202;
	}

	RSA *rsa = EVP_PKEY_get1_RSA(pkey);

	int len = RSA_size(rsa);
	return len;
}

int getRSAPrivateKeyWithPassword_2(const char *prikey) {
	write_log("获取带密码的私钥_2");
	EVP_PKEY *pkey;
	FILE *fp = NULL;
	EVP_PKEY_CTX *ctx;

	// 读取私钥文件
	fp = fopen(prikey, "r");
	if (fp == NULL) {
		printf("读取私钥文件_2失败\n");
		return SIGN_ERROR_CODE_201;
	}

	// 这行代码必须提前加载
	OpenSSL_add_all_algorithms();

	// 读取私钥，通过回调传递密码
	pkey = PEM_read_PrivateKey(fp, NULL, pass_cb, "key");
	fclose(fp);

	if (pkey == NULL) {
		ERR_print_errors_fp(stderr);
		return SIGN_ERROR_CODE_202;
	}

	RSA *rsa = EVP_PKEY_get1_RSA(pkey);
	RSA_print_fp(stdout, rsa, 4);

	int len = RSA_size(rsa);
	return len;
}


