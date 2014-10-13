package mingle.sample.misc;


import android.app.Activity;
import android.os.Bundle;

public class EventbusMixin {

    public EventbusMixin(Activity activity){}

    public void onResume(){
        registerBus();
    }

    private void registerBus() {

    }

    public void onPause(){
        unregisterBus();
    }

    private void unregisterBus() {

    }

    public void onCreate(Bundle savedInstanceState){}
    public void onStart(){}
    public void onStop(){}
    public void onSaveInstanceState(Bundle outState){}
    public void onDestroy(){}
}
