package mingle.sample;


import android.app.Activity;
import android.os.Bundle;

import mingle.annotations.MingleActivity;
import mingle.sample.misc.EventbusMixin;

@MingleActivity(base = Activity.class, mixins = {WifiMixin.class, EventbusMixin.class})
public class Main {
    public void onCreate(Bundle savedInstanceState) {

    }

    public void onStart() {

    }

    public void onResume() {

    }

    public void onPause() {

    }

    public void onStop() {

    }

    public void onDestroy() {

    }

    public void onSaveInstanceState(Bundle outState) {

    }
}
