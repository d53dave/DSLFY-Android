

package net.d53dev.dslfy.android.ui;

import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import net.d53dev.dslfy.android.DSLFYServiceProvider;
import net.d53dev.dslfy.android.R;
import net.d53dev.dslfy.android.authenticator.LogoutService;
import net.d53dev.dslfy.android.core.DSLFYService;
import net.d53dev.dslfy.android.events.AlarmBroadcastReceived;
import net.d53dev.dslfy.android.events.NavItemSelectedEvent;
import net.d53dev.dslfy.android.ui.camera.CameraResultActivity;
import net.d53dev.dslfy.android.util.Ln;
import net.d53dev.dslfy.android.util.SafeAsyncTask;
import net.d53dev.dslfy.android.util.UIUtils;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.otto.Subscribe;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.ButterKnife;


/**
 * Initial activity for the application.
 *
 * If you need to remove the authentication from the application please see
 * {@link net.d53dev.dslfy.android.authenticator.ApiKeyProvider#getAuthKey(android.app.Activity)}
 */
public class MainActivity extends DSLFYFragmentActivity implements GalleryFragment.Contract {

    @Inject protected DSLFYServiceProvider serviceProvider;
    @Inject protected LogoutService logoutService;

    private boolean userHasAuthenticated = false;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence drawerTitle;
    private CharSequence title;
    private NavigationDrawerFragment navigationDrawerFragment;

    public static final String SOCIAL_NETWORK_TAG = "SocialIntegrationMain.SOCIAL_NETWORK_TAG";
    private static final int REQUEST_CAMERA = 5373;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);

        if(isTablet()) {
            setContentView(R.layout.main_activity_tablet);
        } else {
            setContentView(R.layout.main_activity);
        }

        // View injection with Butterknife
        ButterKnife.bind(this);

        // Set up navigation drawer
        title = drawerTitle = getTitle();


        if(!isTablet()) {
            drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//            drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,   )
            drawerToggle = new ActionBarDrawerToggle(
                    this,                    /* Host activity */
                    drawerLayout,           /* DrawerLayout object */
//                    R.drawable.ic_drawer,    /* nav drawer icon to replace 'Up' caret */
                    R.string.navigation_drawer_open,    /* "open drawer" description */
                    R.string.navigation_drawer_close) { /* "close drawer" description */

                /** Called when a drawer has settled in a completely closed state. */
                public void onDrawerClosed(View view) {
                    getSupportActionBar().setTitle(title);
                    supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }

                /** Called when a drawer has settled in a completely open state. */
                public void onDrawerOpened(View drawerView) {
                    getSupportActionBar().setTitle(drawerTitle);
                    supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }
            };

            drawerToggle.setHomeAsUpIndicator(R.drawable.ic_drawer);

            // Set the drawer toggle as the DrawerListener
            drawerLayout.setDrawerListener(drawerToggle);

            navigationDrawerFragment = (NavigationDrawerFragment)
                    getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

            // Set up the drawer.
            navigationDrawerFragment.setUp(
                    R.id.navigation_drawer,
                    (DrawerLayout) findViewById(R.id.drawer_layout));
        }


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        // Init default settings for ImageLoader
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getBaseContext()));

        this.scheduleNotification(buildNotification("Keep up the good work! Good habits are " +
                "formed through perseverance and thousands of selfies."));

        checkAuth();

    }

    private boolean isTablet() {
        return UIUtils.isTablet(this);
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if(!isTablet()) {
            // Sync the toggle state after onRestoreInstanceState has occurred.
            drawerToggle.syncState();
        }
    }


    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(!isTablet()) {
            drawerToggle.onConfigurationChanged(newConfig);
        }
    }


    private void initScreen() {
        if (userHasAuthenticated) {

            Ln.d("Foo");
            final FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, new CarouselFragment())
                    .commit();
        }

    }

    private void checkAuth() {

        new SafeAsyncTask<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                final DSLFYService svc = serviceProvider.getService(MainActivity.this);
                return svc != null;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                if (e instanceof OperationCanceledException) {
                    // User cancelled the authentication process (back button, etc).
                    // Since auth could not take place, lets finish this activity.
                    finish();
                }
            }

            @Override
            protected void onSuccess(final Boolean hasAuthenticated) throws Exception {
                super.onSuccess(hasAuthenticated);
                userHasAuthenticated = true;
                initScreen();
            }
        }.execute();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        if (!isTablet() && drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                //menuDrawer.toggleMenu();
                return true;
            case R.id.logout:
                doLogout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doLogout(){
        logoutService.logout(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,
                        "You are now logged out.",
                        Toast.LENGTH_SHORT).show();
                checkAuth();
            }
        });
    }

    @Subscribe
    public void onNavigationItemSelected(NavItemSelectedEvent event) {

        Ln.d("Selected: %1$s", event.getItemPosition());

        switch(event.getItemPosition()) {
            case 0:
                // Home
                // do nothing as we're already on the home screen.
                break;
            case 1:
                // Timer
                Toast.makeText(this, "Opening Settings", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(this, PrefActivity.class);
                startActivity(i);
                break;
            case 2:
                doLogout();
                break;
        }
    }

    @Override
    public void takePicture(Intent i) {
        startActivityForResult(i, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
//                ddBitmap bitmap = data.getParcelableExtra("data");
//                Bitmap thumb = ThumbnailUtils.extractThumbnail(bitmap, 50, 50);

                Intent showPictureIntent = new Intent(this, CameraResultActivity.class);

                String path = data.getData().toString();

                Bundle bundle = new Bundle();
                bundle.putString(GalleryFragment.IMAGE_PATH_IDENTIFIER, path);
                showPictureIntent.putExtras(bundle);

                startActivity(showPictureIntent);
            }
        }
    }

    private void scheduleNotification(Notification notification) {

        Intent notificationIntent = new Intent(this, AlarmBroadcastReceived.class);
        notificationIntent.putExtra(AlarmBroadcastReceived.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(AlarmBroadcastReceived.NOTIFICATION, notification);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(this, 0, notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        //TODO: Get this from Preferences
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 13);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(
                AlarmManager.RTC,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                pendingIntent);
    }

    private Notification buildNotification(String content) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Did you take your daily selfie?");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_launcher);
        return builder.build();
    }
}
