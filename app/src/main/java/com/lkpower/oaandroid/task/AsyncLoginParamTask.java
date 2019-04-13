package com.lkpower.oaandroid.task;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lkpower.http.Http;
import com.lkpower.oaandroid.MainActivity;
import com.lkpower.util.Constants;
import com.lkpower.util.SharedPreferencesUtils;
import com.lkpower.util.Util;

/**
 * @author linger
 * @since 2015-11-09
 * <p>
 * 获取登录时需要的密码传输方式参数
 */
public class AsyncLoginParamTask extends AsyncTask<String, String, String> {

    private static final String TAG = AsyncLoginParamTask.class.getSimpleName();

    private String addr;
    private MainActivity context;

    private LoginParam loginParam;
    private String value = null;
    private int count = 0;

    public AsyncLoginParamTask(MainActivity context) {
        this.context = context;

    }

    @Override
    protected String doInBackground(String... params) {
        Util.printLog(TAG, "loginparam doInBackground ...");
        do {
            String url = SharedPreferencesUtils.getString(Constants.SERVICE_URL, Constants.EMPTY);
            addr = url + Constants.REQUEST_ENCRYPT_PARAM;

            value = Http.getStrContent(addr);
            if (null != value) {
                Gson gson = new Gson();
                try {
                    loginParam = gson.fromJson(value, LoginParam.class);
                    Util.printLog(TAG, loginParam.toString());
                    break;
                } catch (JsonSyntaxException ex) {
                    ex.printStackTrace();
                    loginParam = null;
                    value = null;
                }

            }

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } while (null == value && null == loginParam && count++ < 3);


        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (null != value && null != loginParam) {
            SharedPreferencesUtils.saveInt(Constants.IS_MD5_LOGIN, loginParam.getResult().getEncrypt());
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Util.printLog(TAG, "photo onProgressUpdate ...");
    }

}

class LoginParam {

    private EncryptParam result;

    @Override
    public String toString() {
        return result.toString();
    }

    public EncryptParam getResult() {
        return result;
    }

    public void setResult(EncryptParam result) {
        this.result = result;
    }

}

class EncryptParam {

    private int encrypt;

    @Override
    public String toString() {
        return "[ 登录类型参数 " + this.encrypt + " ]";
    }

    public int getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(int encrypt) {
        this.encrypt = encrypt;
    }

}


