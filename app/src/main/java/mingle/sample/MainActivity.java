package mingle.sample;


import android.app.Activity;
import android.os.Bundle;

import mingle.annotations.Mingle;
import mingle.annotations.MingleActivity;
import mingle.sample.misc.EventbusMixin;

@MingleActivity(base = Activity.class, mixins = {WifiMixin.class, EventbusMixin.class})
public class MainActivity {
}
