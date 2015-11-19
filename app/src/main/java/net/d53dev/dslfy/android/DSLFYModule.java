package net.d53dev.dslfy.android;

import android.accounts.AccountManager;
import android.content.Context;
import android.view.View;

import net.d53dev.dslfy.android.authenticator.ApiKeyProvider;
import net.d53dev.dslfy.android.authenticator.BootstrapAuthenticatorActivity;
import net.d53dev.dslfy.android.authenticator.LogoutService;
import net.d53dev.dslfy.android.core.DSLFYService;
import net.d53dev.dslfy.android.core.Constants;
import net.d53dev.dslfy.android.core.PostFromAnyThreadBus;
import net.d53dev.dslfy.android.core.RestAdapterRequestInterceptor;
import net.d53dev.dslfy.android.core.RestErrorHandler;
import net.d53dev.dslfy.android.model.UserAgentProvider;
import net.d53dev.dslfy.android.ui.PrefActivity;
import net.d53dev.dslfy.android.ui.ViewSelfieActivity;
import net.d53dev.dslfy.android.ui.camera.CameraResultActivity;
import net.d53dev.dslfy.android.ui.CheckInsListFragment;
import net.d53dev.dslfy.android.ui.MainActivity;
import net.d53dev.dslfy.android.ui.NavigationDrawerFragment;
import net.d53dev.dslfy.android.ui.UserActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Dagger module for setting up provides statements.
 * Register all of your entry points below.
 */
@Module(
        complete = false,

        injects = {
                DSLFY.class,
                BootstrapAuthenticatorActivity.class,
                MainActivity.class,
                CheckInsListFragment.class,
                NavigationDrawerFragment.class,
                UserActivity.class,
                CameraResultActivity.class,
                ViewSelfieActivity.class,
                PrefActivity.class
        }
)
public class DSLFYModule {

    @Singleton
    @Provides
    Bus provideOttoBus() {
        return new PostFromAnyThreadBus();
    }

    @Provides
    @Singleton
    LogoutService provideLogoutService(final Context context, final AccountManager accountManager) {
        return new LogoutService(context, accountManager);
    }

    @Provides
    DSLFYService provideBootstrapService(RestAdapter restAdapter) {
        return new DSLFYService(restAdapter);
    }

    @Provides
    DSLFYServiceProvider provideBootstrapServiceProvider(RestAdapter restAdapter, ApiKeyProvider apiKeyProvider) {
        return new DSLFYServiceProvider(restAdapter, apiKeyProvider);
    }

    @Provides
    ApiKeyProvider provideApiKeyProvider(AccountManager accountManager) {
        return new ApiKeyProvider(accountManager);
    }

    @Provides
    Gson provideGson() {
        /**
         * GSON instance to use for all request  with date format set up for proper parsing.
         * <p/>
         * You can also configure GSON with different naming policies for your API.
         * Maybe your API is Rails API and all json values are lower case with an underscore,
         * like this "first_name" instead of "firstName".
         * You can configure GSON as such below.
         * <p/>
         *
         * public static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd")
         *         .setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES).create();
         */
        return new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
    }

    @Provides
    RestErrorHandler provideRestErrorHandler(Bus bus) {
        return new RestErrorHandler(bus);
    }

    @Provides
    RestAdapterRequestInterceptor provideRestAdapterRequestInterceptor(UserAgentProvider userAgentProvider) {
        return new RestAdapterRequestInterceptor(userAgentProvider);
    }

    @Provides
    RestAdapter provideRestAdapter(RestErrorHandler restErrorHandler, RestAdapterRequestInterceptor restRequestInterceptor, Gson gson) {
        return new RestAdapter.Builder()
                .setEndpoint(Constants.Http.URL_BASE)
                .setErrorHandler(restErrorHandler)
                .setRequestInterceptor(restRequestInterceptor)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(gson))
                .build();
    }

}
