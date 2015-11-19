

package net.d53dev.dslfy.android.core;

/**
 * Bootstrap constants
 */
public final class Constants {
    private Constants() {}

    public static final class Auth {
        private Auth() {}

        /**
         * Account type id
         */
        public static final String DSLFY_ACCOUNT_TYPE = "net.d53dev.dslfy.android";

        /**
         * Account name
         */
        public static final String DSLFY_ACCOUNT_NAME = "DSLFY";

        /**
         * Provider id
         */
        public static final String DSLFY_PROVIDER_AUTHORITY = "net.d53dev.dslfy.android.sync";

        /**
         * Auth token type
         */
        public static final String AUTHTOKEN_TYPE = DSLFY_ACCOUNT_TYPE;
    }

    /**
     * All HTTP is done through a REST style API built for demonstration purposes on Parse.com
     * Thanks to the nice people at Parse for creating such a nice system for us to use for bootstrap!
     */
    public static final class Http {
        private Http() {}


        /**
         * Base URL for all requests
         */
        public static final String URL_BASE = "https://52.29.147.35";


        /**
         * Authentication URL
         */
        public static final String URL_AUTH_FRAG = "/api/v1/login";
        public static final String URL_AUTH = URL_BASE + URL_AUTH_FRAG;

        /**
         * Get user profile
         */
        public static final String URL_USER_FRAG =  "/api/v1/user/";
        public static final String URL_USER = URL_BASE + URL_USER_FRAG;


        /**
         * Get Friends Stream
         */
        public static final String URL_FRIENDS_STREAM_FRAG = "/user/{userId}/friends/stream";
        public static final String URL_FRIENDS_STREAM = URL_BASE + URL_FRIENDS_STREAM_FRAG;


        /**
         * Upload image
         */
        public static final String URL_UPLOAD_FRAG = "/user/{userId}/upload";
        public static final String URL_UPLOAD = URL_BASE + URL_UPLOAD_FRAG;

        /**
         * PARAMS for auth
         */
        public static final String PARAM_USERNAME = "username";
        public static final String PARAM_PASSWORD = "password";


        public static final String CONTENT_TYPE_JSON = "application/json";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";

        /**
         * HEADERS
         */
        public static final String HEADER_API_TOKEN = "X-API-TOKEN";

    }


    public static final class Intent {
        private Intent() {}

        /**
         * Action prefix for all intents created
         */
        public static final String INTENT_PREFIX = "net.d53dev.dslfy.android.";

    }
}


