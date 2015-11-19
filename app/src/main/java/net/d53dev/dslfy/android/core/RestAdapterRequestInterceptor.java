package net.d53dev.dslfy.android.core;

import net.d53dev.dslfy.android.model.UserAgentProvider;

import retrofit.RequestInterceptor;

public class RestAdapterRequestInterceptor implements RequestInterceptor {

    private UserAgentProvider userAgentProvider;

    public RestAdapterRequestInterceptor(UserAgentProvider userAgentProvider) {
        this.userAgentProvider = userAgentProvider;
    }

    @Override
    public void intercept(RequestFacade request) {

        // Add header to set content type of JSON
        request.addHeader("Content-Type", "application/json");

        request.addHeader(Constants.Http.HEADER_API_TOKEN, "");

        // Add the user agent to the request.
        request.addHeader("User-Agent", userAgentProvider.get());
    }
}
