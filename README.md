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
public class EventBusMixin {

    private EventBus mBus;
    private WeakReference<Activity> mOwnerActivityRef;

    public EventBusMixin(Activity owner){
        mOwnerActivityRef = new WeakReference<Activity>(owner);
    }

    public void onCreate(Bundle savedInstanceState) {
        mBus = EventBus.getInstance(mOwnerActivityRef.get());
    }

    public void onStart(){
        mBus.register(mOwnerActivityRef.get());
    }

    public void onStop(){
        mBus.unregister(mOwnerActivityRef.get());
    }

    public void onDestroy(){
        mOwnerActivityRef.clear();
    }
}

@MingleActivity(base = Activity.class, mixins = {EventBusMixin.class})
public class MyActivity {
}
```

This will generate the following:

```java
public class MainActivity_ extends android.app.Activity {

    private mingle.sample.EventBusMixin mEventbusMixin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(mEventbusMixin == null) {
            mEventbusMixin = new mingle.sample.EventBusMixin(this);
        } else {
            mEventbusMixin.onCreate(savedInstanceState);
        }
    }

    protected void onStart() {
        super.onStart();
        mEventbusMixin.onStart();
    }

    protected void onStop() {
        super.onStop();
        mEventbusMixin.onStop();
    }

    protected void onDestroy() {
        super.onDestroy();
        mEventbusMixin.onDestroy();
    }

}
```

if you want your Activity to extend from another base Activity, just change the `base =` to another class:

```java
@MingleActivity(base = ListActivity.class, mixins = {EventBusMixin.class})
public class MyListActivity {
}
```

###Comparison to other boilerplate-elimination libraries for Android

  - Since Java annotation processing does not allow editing source code (at least not without hacks of the kind used in [Project Lombok](http://projectlombok.org/)), this library is *not* a 100% transparent to the developer. This means, as a developer you need to manually point to the generated files in your code. This is similar to the approach followed by [AndroidAnnotations](https://github.com/excilys/androidannotations/).
  - If you wish to eliminate these steps entirely, you could look at byte-code manipulation using javassist - in particular check out [Mimic by Stephane Nicolas](https://github.com/stephanenicolas/mimic). Note however that you will lose the ability to step-debug through generated code if you use byte code manipulation. One could argue that generated code should be of such a quality that a developer should never have to debug it - but well ...
  - Yet another approach is [Transfuse by John Ericksen](http://androidtransfuse.org/) which eliminates the need to manually refer to generated code by managing the AndroidManifest.xml for you.