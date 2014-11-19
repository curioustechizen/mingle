package mingle.sample;


import android.app.Activity;
import android.os.Bundle;

import mingle.annotations.MingleActivity;
import mingle.annotations.OnCreate;
import mingle.sample.misc.EventbusMixin;

@MingleActivity(base = Activity.class, mixins = {WifiMixin.class, EventbusMixin.class})
public class Main {

    public Main(Activity owner){}
    @OnCreate
    public void onCreate(Bundle savedInstanceState){
        //Do Some Creating
    }
}
