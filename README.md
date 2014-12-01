#Mingle

`mingle` is a **mixin library** with the goal of reducing boilerplate code in Android applications. It achieves this goal using **compile time annotation processing**. `mingle` promotes the "Favor composition over Inheritance" advice by turning each mixin into an member variable of a class.
`mingle` is currently **pre-alpha**. I'm looking for feedback (in terms of the approach I'm using) as well as code contributions.

###Basic usage

Lets say you are using some sort of an EventBus. In every `Activity`, you need to register the EventBus in `onStart()` and unregister in `onStop()`. The traditional way to solve this is to create a base Activity from which all Activitie in your app extend:

```java
public class EventBusBaseActivity extends Activity {
    private EventBus mBus;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBus = EventBus.getInstance(this);
    }

    public void onStart(){
        super.onStart();
        mBus.register(this);
    }

    public void onStop(){
        super.onStop();
        mBus.unregister(this);
    }
}
```

But then, what if one of the Activities in your app needs to extend `ListActivity`? Or another Activity class from a third-party library? You'll have to replicate the exact same code in all those classes. This is a classic example of violation of DRY principle in Android.

Using Mingle, you can do this:

```java
@MingleActivity(base = Activity.class, mixins = {WifiMixin.class, EventbusMixin.class})
public class Main {

    public Main(Activity owner){}

    @OnCreate
    public void onCreate(Bundle savedInstanceState){
        //Do Some Creating
    }
}

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


public class EventbusMixin {

    public EventbusMixin(Activity activity){}

    @OnResume(order = Mingle.ORDER_END)
    public void onResume(){
        registerBus();
    }

    private void registerBus() {
        //Register with the bus here
    }

    @OnPause(order = Mingle.ORDER_BEGINNING)
    public void onPause(){
        unregisterBus();
    }

    private void unregisterBus() {
        //Unregister from the bus here
    }
}
```

This will generate the following:

```java
public class MainActivity_ extends android.app.Activity {

    private mingle.sample.Main __mingle_mingle_sample_Main_$$22;
    private mingle.sample.WifiMixin __mingle_mingle_sample_WifiMixin_$$138;
    private mingle.sample.misc.EventbusMixin __mingle_mingle_sample_misc_EventbusMixin_$$191;

    @Override
    protected void onCreate(Bundle savedInstanceState$$0) {
        if(__mingle_mingle_sample_Main_$$22 == null){
            __mingle_mingle_sample_Main_$$22 = new mingle.sample.Main();
        }
        if(__mingle_mingle_sample_WifiMixin_$$138 == null){
            __mingle_mingle_sample_WifiMixin_$$138 = new mingle.sample.WifiMixin(this);
        }
        if(__mingle_mingle_sample_misc_EventbusMixin_$$191 == null){
            __mingle_mingle_sample_misc_EventbusMixin_$$191 = new mingle.sample.misc.EventbusMixin(this);
        }
        super.onCreate(savedInstanceState$$0);
        __mingle_mingle_sample_Main_$$22.onCreate(savedInstanceState$$0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        __mingle_mingle_sample_WifiMixin_$$138.onResume();
        __mingle_mingle_sample_misc_EventbusMixin_$$191.onResume();
    }

    @Override
    protected void onPause() {
        __mingle_mingle_sample_misc_EventbusMixin_$$191.onPause();
        super.onPause();
        __mingle_mingle_sample_WifiMixin_$$138.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        __mingle_mingle_sample_WifiMixin_$$138.onDestroy();
    }
}
```

if you want your Activity to extend from another base Activity, just change the `base =` to another class:

```java
@MingleActivity(base = ListActivity.class, mixins = {EventBusMixin.class})
public class MyList{
    // ...
}
```

###Comparison to other boilerplate-elimination libraries for Android

  - Since Java annotation processing does not allow editing source code (at least not without hacks of the kind used in [Project Lombok](http://projectlombok.org/)), this library is *not* a 100% transparent to the developer. This means, as a developer you need to manually point to the generated files in your code. This is similar to the approach followed by [AndroidAnnotations](https://github.com/excilys/androidannotations/).
  - If you wish to eliminate these steps entirely, you could look at byte-code manipulation using javassist - in particular check out [Mimic by Stephane Nicolas](https://github.com/stephanenicolas/mimic). Note however that you will lose the ability to step-debug through generated code if you use byte code manipulation. One could argue that generated code should be of such a quality that a developer should never have to debug it - but well ...
  - Yet another approach is [Transfuse by John Ericksen](http://androidtransfuse.org/) which eliminates the need to manually refer to generated code by managing the AndroidManifest.xml for you.