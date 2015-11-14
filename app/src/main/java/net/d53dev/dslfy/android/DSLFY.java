

package net.d53dev.dslfy.android;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import net.d53dev.dslfy.android.util.Ln;

import io.fabric.sdk.android.Fabric;

/**
 * DSLFY application
 */
public class DSLFY extends Application {

    private static DSLFY instance;
    public static final String PREFNAME = "DLSFYprefs";

    /**
     * Create main application
     */
    public DSLFY() {
    }

    /**
     * Create main application
     *
     * @param context
     */
    public DSLFY(final Context context) {
        this();
        attachBaseContext(context);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if(getString(R.string.facebook_app_id) == null ){
            throw new IllegalStateException("Resvalue generation was unsuccessful");
        }
        FacebookSdk.sdkInitialize(getApplicationContext());
        TwitterAuthConfig authConfig =
                new TwitterAuthConfig(
                        getString(R.string.twitter_consumer_key),
                        getString(R.string.twitter_consumer_secret));
        Ln.d("Starting Fabric with TwitterAuth " + authConfig.getConsumerKey() + " and " + authConfig.getConsumerSecret());
        Fabric.with(this, new Crashlytics(), new Twitter(authConfig));

        instance = this;

        // Perform injection
        Injector.init(getRootModule(), this);

    }

    private Object getRootModule() {
        return new RootModule();
    }


    /**
     * Create main application
     *
     * @param instrumentation
     */
    public DSLFY(final Instrumentation instrumentation) {
        this();
        attachBaseContext(instrumentation.getTargetContext());
    }

    public static DSLFY getInstance() {
        return instance;
    }
}
