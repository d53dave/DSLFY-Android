package net.d53dev.dslfy.android.core;

import retrofit.http.GET;

public interface FriendStreamService {

    @GET(Constants.Http.URL_FRIENDS_STREAM)
    ImageWrapper getFriendStream();
}
