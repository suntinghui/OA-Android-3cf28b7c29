/*
 * util.c
 *
 *  Created on: 2015-6-11
 *      Author: linger
 */

#include <stdio.h>
#include <string.h>
#include <time.h>
#include <ctype.h>

#include <android/log.h>

#define LOG "util.c"
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, LOG,__VA_ARGS__)

// 打开文件错误
#define OPENFILE_ERROR -200

// 调试开关
#define IS_DEBUG 1

int hexToDecimal(char c) {
	int num = 0;
	if (c <= '9' && c >= '0') {
		num = c - '0';
	}
	else if (c <= 'f' && c >= 'a') {
		num = c - 'a' + 10;
	}
	else if (c <= 'F' && c >= 'A') {
		num = c - 'A' + 10;
	}
	else {
		printf("uncorrect\n");
		num = -1;
	}
	return num;
}

int stringToBytes(unsigned char* src, unsigned char* dest, int destLen) {
	if (src == NULL) {
		return 0;
	}
	int iLen = strlen((char *) src);
	if (iLen <= 0 || iLen % 2 != 0 || dest == NULL || destLen < iLen / 2) {
		return 0;
	}

	iLen /= 2;

	int i;
	for (i = 0; i < iLen; i++) {
		int iVal = 0;
		int high = hexToDecimal(src[2*i]);
		int lower = hexToDecimal(src[2*i + 1]);
		high = high << 4;
		iVal = high | lower;
		dest[i] = (unsigned char) iVal;
	}

	return iLen;
}

/*
 * 保存文件
 */
int saveToFile(const char *filename, const char *mode, const unsigned char *data) {
	// 打开文件
	FILE *file = fopen(filename, mode);
	if (file == NULL) {
		printf("打开日志文件失败\n");
		return OPENFILE_ERROR;
	}
	// fputs 成功时返回非负值, 失败时返回EOF
	int value = fputs(data, file);
	fclose(file);

	return value;
}


/*
 * byte to string
 */
int bytes2String(unsigned char *pSrc, int nSrcLen, unsigned char *pDst, int nDstMaxLen)
{
    if (pDst != NULL)
    {
        *pDst = 0;
    }

    if (pSrc == NULL || nSrcLen <= 0 || pDst == NULL || nDstMaxLen <= nSrcLen*2)
    {
        return 0;
    }

    // 0x0-0xf 的字符查找表
//    const char szTable[] = "0123456789ABCDEF";
    const char szTable[] = "0123456789abcdef";
    int i;
    for(i=0; i<nSrcLen; i++)
    {
        //输出低4位
        *pDst++ = szTable[pSrc[i] >> 4];
        // 输出高4位
        *pDst++ = szTable[pSrc[i] & 0x0f];
    }
    // 输出字符串加个结束符
    *pDst = '\0';
    //返回目标字符串长度
    return  nSrcLen * 2;
}

void toLower(const char *src, char *dest) {
	if (src == NULL || dest == NULL)
	{
		return;
	}

	int len = strlen(src);
    int i = 0;
    for (; i<len; i++) {
    	*dest++ = tolower(*src++);
    }
    *dest = '\0';
}

void toUpper(const char *src, char *dest) {
	if (src == NULL || dest == NULL)
	{
		return;
	}

	int len = strlen(src);
    int i = 0;
    for (; i<len; i++) {
    	*dest++ = toupper(*src++);
    }
    *dest = '\0';
}

/*
 * string to byte
 */
int string2Bytes(unsigned char* szSrc, unsigned char* pDst, int nDstMaxLen)
{
    if(szSrc == NULL)
    {
        return 0;
    }
    LOGD(szSrc);
    int iLen = strlen((char *)szSrc);
    LOGD("%d %d %s", iLen, nDstMaxLen, pDst);

    if (iLen <= 0 || iLen%2 != 0 || pDst == NULL || nDstMaxLen < iLen/2)
    {
        return 0;
    }

    iLen /= 2;
    // 将字符串转为大写形式
//    strupr((char *)szSrc);

    int i;
    for (i=0; i<iLen; i++)
    {
        int iVal = 0;
        unsigned char *pSrcTemp = szSrc + i*2;
        LOGD("2 %s", pSrcTemp);
        sscanf((char *)pSrcTemp, "%02x", &iVal);
        pDst[i] = (unsigned char)iVal;
    }

    return iLen;
}

/*
 * 写日志
 */
void write_log_3(const char *logfile, const char *str) {
	if (IS_DEBUG) {
		// 打开文件
		FILE *log = fopen(logfile, "a");
		if (log == NULL) {
			printf("打开日志文件失败\n");
			return;
		}

		// 获取时间
		time_t timep;
		time(&timep);
		struct tm *now;
		now = localtime(&timep);
		char timestamp[32];
		strftime(timestamp, 32, "[%Y-%m-%d %H:%M:%S]", now);
//		printf("%s\n", timestamp);

		// 写日志
		fputs(timestamp, log);
		fputs(str, log);
		fputs("\n", log);

		fclose(log);
	}
}

void write_log(const char *str) {
	const char *logfile = "/mnt/sdcard/test/log.log";
	write_log_3(logfile, str);
}

void write_log_2(const char *logfile, const char *str) {
	if (IS_DEBUG) {
		time_t timep;
		time(&timep);
		printf("%s\n", ctime(&timep));

		struct tm *now;
		now = localtime(&timep);
		char timestamp[32] = {0};
		size_t result = strftime(timestamp, 30, "%Y_%m_%d", now);
		printf("result = %lu, %s\n", result, timestamp);

//		result = strftime(timestamp, 20, "%Y_%m_%d_%H_%M_%S", now);
//		printf("result = %lu, %s\n", result, timestamp);

		char *filename = "log_";
		char *val = strcat(filename, timestamp);
		printf("val = %s, filename = %s\n", val, filename);

	}
}
