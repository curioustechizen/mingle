package mingle.sample;


import android.app.ListActivity;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import mingle.annotations.MingleActivity;
import mingle.sample.misc.EventbusMixin;

@MingleActivity(base = ListActivity.class, mixins = {WifiMixin.class})
public class MyListActivity {
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
