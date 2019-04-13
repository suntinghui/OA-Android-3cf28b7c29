package com.lkpower.oaandroid.dialog;

/**
 * Created by yw on 17-10-20.
 */

public abstract class ICallBackInterface implements BaseCallBackInterface {
    public abstract void ok();
    public abstract void cancel();

    @Override
    public void middle() {
        //just do nothing
    }
}
