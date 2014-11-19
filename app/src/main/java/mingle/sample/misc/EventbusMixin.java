package mingle.sample.misc;


import android.app.Activity;
import android.os.Bundle;

import mingle.Mingle;
import mingle.annotations.OnPause;
import mingle.annotations.OnResume;

public class EventbusMixin {

    public EventbusMixin(Activity activity){}

    @OnResume(order = Mingle.ORDER_END)
    public void onResume(){
        registerBus();
    }

    private void registerBus() {

    }

    @OnPause(order = Mingle.ORDER_BEGINNING)
    public void onPause(){
        unregisterBus();
    }

    private void unregisterBus() {

    }
}
