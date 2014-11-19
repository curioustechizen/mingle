package mingle.sample;


import android.app.ListActivity;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import mingle.annotations.MingleActivity;
import mingle.annotations.OnStart;
import mingle.sample.misc.EventbusMixin;

@MingleActivity(base = ListActivity.class, mixins = {WifiMixin.class})
public class MyList {

    @OnStart
    public void onStart() {
        //Do Some Starting
    }
}
