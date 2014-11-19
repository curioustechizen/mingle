package mingle.sample;

import android.app.Activity;
import android.os.Bundle;
import java.lang.ref.WeakReference;

import mingle.Mingle;
import mingle.annotations.OnDestroy;
import mingle.annotations.OnPause;
import mingle.annotations.OnResume;

class WifiMixin {

    private WeakReference<Activity> mActivityRef;

    WifiMixin(Activity mOwner){
        mActivityRef = new WeakReference<Activity>(mOwner);
    }

    @OnPause(order = Mingle.ORDER_AFTER_SUPER)
    void onPause(){}

    @OnResume(order = Mingle.ORDER_AFTER_SUPER)
    void onResume(){}

    @OnDestroy
    void onDestroy(){
        mActivityRef.clear();
    }

}
