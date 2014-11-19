package mingle.sample;

import android.app.Activity;
import android.os.Bundle;
import java.lang.ref.WeakReference;

class WifiMixin {

    private WeakReference<Activity> mActivityRef;

    WifiMixin(Activity mOwner){
        mActivityRef = new WeakReference<Activity>(mOwner);
    }

    void onCreate(Bundle savedInstanceState){}
    void onPause(){}
    void onResume(){}
    void onDestroy(){
        mActivityRef.clear();
    }

}
