
package net.d53dev.dslfy.android;

import android.accounts.AccountsException;
import android.app.Activity;

import net.d53dev.dslfy.android.authenticator.ApiKeyProvider;
import net.d53dev.dslfy.android.core.DSLFYService;

import java.io.IOException;

import retrofit.RestAdapter;

/**
 * Provider for a {@link DSLFYService} instance
 */
public class DSLFYServiceProvider {

    private RestAdapter restAdapter;
    private ApiKeyProvider keyProvider;

    public DSLFYServiceProvider(RestAdapter restAdapter, ApiKeyProvider keyProvider) {
        this.restAdapter = restAdapter;
        this.keyProvider = keyProvider;
    }

    /**
     * Get service for configured key provider
     * <p/>
     * This method gets an auth key and so it blocks and shouldn't be called on the main thread.
     *
     * @return bootstrap service
     * @throws IOException
     * @throws AccountsException
     */
    public DSLFYService getService(final Activity activity)
            throws IOException, AccountsException {
        // The call to keyProvider.getAuthKey(...) is what initiates the login screen. Call that now.
        keyProvider.getAuthKey(activity);

        // TODO: See how that affects the bootstrap service.
        return new DSLFYService(restAdapter);
    }
}
