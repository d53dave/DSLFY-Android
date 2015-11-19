
package net.d53dev.dslfy.android.core;

import net.d53dev.dslfy.android.model.User;

import java.util.List;

import retrofit.RestAdapter;

/**
 * Bootstrap API service
 */
public class DSLFYService {

    private RestAdapter restAdapter;

    /**
     * Create bootstrap service
     * Default CTOR
     */
    public DSLFYService() {
    }

    /**
     * Create bootstrap service
     *
     * @param restAdapter The RestAdapter that allows HTTP Communication.
     */
    public DSLFYService(RestAdapter restAdapter) {
        this.restAdapter = restAdapter;
    }

    private UserService getUserService() {
        return getRestAdapter().create(UserService.class);
    }


    private FriendStreamService getFriendStreamService() {
        return getRestAdapter().create(FriendStreamService.class);
    }

    private RestAdapter getRestAdapter() {
        return restAdapter;
    }

    /**
     * Get current user
     */
    public User getUsers() {
        return getUserService().getUser();
    }

    /**
     * Get all bootstrap Checkins that exists on Parse.com
     */
    public List<CheckIn> getImages() {
       return getFriendStreamService().getFriendStream().getResults();
    }

    public User authenticate(String email, String password) {
        return getUserService().authenticate(email, password);
    }
}
