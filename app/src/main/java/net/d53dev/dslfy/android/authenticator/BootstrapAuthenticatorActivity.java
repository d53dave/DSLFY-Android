package net.d53dev.dslfy.android.authenticator;

import static android.R.layout.simple_dropdown_item_1line;
import static android.accounts.AccountManager.KEY_ACCOUNT_NAME;
import static android.accounts.AccountManager.KEY_ACCOUNT_TYPE;
import static android.accounts.AccountManager.KEY_AUTHTOKEN;
import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;
import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.KEYCODE_ENTER;
import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import net.d53dev.dslfy.android.Injector;
import net.d53dev.dslfy.android.R;
import net.d53dev.dslfy.android.R.id;
import net.d53dev.dslfy.android.R.layout;
import net.d53dev.dslfy.android.R.string;
import net.d53dev.dslfy.android.core.DSLFYService;
import net.d53dev.dslfy.android.core.Constants;
import net.d53dev.dslfy.android.model.User;
import net.d53dev.dslfy.android.events.UnAuthorizedErrorEvent;
import net.d53dev.dslfy.android.ui.TextWatcherAdapter;
import net.d53dev.dslfy.android.util.Ln;
import net.d53dev.dslfy.android.util.SafeAsyncTask;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.kevinsawicki.wishlist.Toaster;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.RetrofitError;

/**
 * Activity to authenticate the user against an API (example API on Parse.com)
 */
public class BootstrapAuthenticatorActivity extends ActionBarAccountAuthenticatorActivity{

    /**
     * PARAM_CONFIRM_CREDENTIALS
     */
    public static final String PARAM_CONFIRM_CREDENTIALS = "confirmCredentials";

    /**
     * PARAM_PASSWORD
     */
    public static final String PARAM_PASSWORD = "password";

    /**
     * PARAM_USERNAME
     */
    public static final String PARAM_USERNAME = "username";

    /**
     * PARAM_AUTHTOKEN_TYPE
     */
    public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";

    private enum RequestType {
        email, facebook, twitter;
    }

    private static RequestType requestType;

    private AccountManager accountManager;
    private CallbackManager callbackManager;

    @Inject
    DSLFYService DSLFYService;
    @Inject Bus bus;

    @Bind(id.et_email) protected AutoCompleteTextView emailText;
    @Bind(id.et_password) protected EditText passwordText;
    @Bind(id.b_signin) protected Button signInButton;
    @Bind(id.b_signin_facebook) protected LoginButton facebookSignInButton;
    @Bind(id.b_signin_twitter) protected TwitterLoginButton twitterSignInButton;

    private final TextWatcher watcher = validationTextWatcher();

    private SafeAsyncTask<Boolean> authenticationTask;
    private String authToken;
    private String authTokenType;

    /**
     * If set we are just checking that the user knows their credentials; this
     * doesn't cause the user's password to be changed on the device.
     */
    private Boolean confirmCredentials = false;

    private String email;

    private String password;


    /**
     * In this instance the token is simply the sessionId returned from Parse.com. This could be a
     * oauth token or some other type of timed token that expires/etc. We're just using the parse.com
     * sessionId to prove the example of how to utilize a token.
     */
    private String token;

    /**
     * Was the original caller asking for an entirely new account?
     */
    protected boolean requestNewAccount = false;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        Injector.inject(this);

        accountManager = AccountManager.get(this);

        final Intent intent = getIntent();
        email = intent.getStringExtra(PARAM_USERNAME);
        authTokenType = intent.getStringExtra(PARAM_AUTHTOKEN_TYPE);
        confirmCredentials = intent.getBooleanExtra(PARAM_CONFIRM_CREDENTIALS, false);

        requestNewAccount = email == null;

        setContentView(layout.login_activity);

        ButterKnife.bind(this);

        emailText.setAdapter(new ArrayAdapter<String>(this,
                simple_dropdown_item_1line, userEmailAccounts()));

        passwordText.setOnKeyListener(new OnKeyListener() {

            public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
                if (event != null && ACTION_DOWN == event.getAction()
                        && keyCode == KEYCODE_ENTER && signInButton.isEnabled()) {
                    handleLogin(signInButton);
                    return true;
                }
                return false;
            }
        });

        passwordText.setOnEditorActionListener(new OnEditorActionListener() {

            public boolean onEditorAction(final TextView v, final int actionId,
                                          final KeyEvent event) {
                if (actionId == IME_ACTION_DONE && signInButton.isEnabled()) {
                    handleLogin(signInButton);
                    return true;
                }
                return false;
            }
        });

        emailText.addTextChangedListener(watcher);
        passwordText.addTextChangedListener(watcher);

        final TextView signUpText = (TextView) findViewById(id.tv_signup);
        signUpText.setMovementMethod(LinkMovementMethod.getInstance());
        signUpText.setText(Html.fromHtml(getString(string.signup_link)));

        requestType = RequestType.email;


        //Facebook
        callbackManager = CallbackManager.Factory.create();

        facebookSignInButton.setReadPermissions(
                Arrays.asList("public_profile", "email", "user_friends"));
        facebookSignInButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Ln.d("Loginsuccess " + loginResult.toString());
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                // Application code
                                Ln.d("NewMeRequest finished!");
                                Ln.d(object.toString());
                                try {
                                    email = "facebook+"+object.getString("id")+"@dslfy.internal.email.net";
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                finishLogin();
                            }
                        });
                //add or exclude fields
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Ln.d("Logincancel ");
            }

            @Override
            public void onError(FacebookException exception)
            {
                Toast.makeText(BootstrapAuthenticatorActivity.this,
                        "Facebook error: "+exception.getMessage(), Toast.LENGTH_SHORT).show();
                onAuthenticationResult(false);
            }
        });

        //Twitter

        twitterSignInButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls

                Ln.d("Twitter session username: "+result.data.getUserName());
                Ln.d("Twitter session userid: "+result.data.getUserId());
                Ln.d("Twitter session authtoken: "+result.data.getAuthToken().toString());
                email = "twitter"+result.data.getUserId()+"@dslfy.internal.net";
                finishLogin();
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                Ln.e("Twitter exception: " + exception.getMessage());
                Toast.makeText(BootstrapAuthenticatorActivity.this,
                        "Twitter error: "+exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private List<String> userEmailAccounts() {
        final Account[] accounts = accountManager.getAccountsByType("com.google");
        final List<String> emailAddresses = new ArrayList<String>(accounts.length);
        for (final Account account : accounts) {
            emailAddresses.add(account.name);
        }
        return emailAddresses;
    }

    private TextWatcher validationTextWatcher() {
        return new TextWatcherAdapter() {
            public void afterTextChanged(final Editable gitDirEditText) {
                updateUIWithValidation();
            }

        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
        updateUIWithValidation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    private void updateUIWithValidation() {
        final boolean populated = populated(emailText) && populated(passwordText);
        signInButton.setEnabled(populated);
    }

    private boolean populated(final EditText editText) {
        return editText.length() > 0;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getText(string.message_signing_in));
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(final DialogInterface dialog) {
                if (authenticationTask != null) {
                    authenticationTask.cancel(true);
                }
            }
        });
        return dialog;
    }

    @Subscribe
    public void onUnAuthorizedErrorEvent(UnAuthorizedErrorEvent unAuthorizedErrorEvent) {
        // Could not authorize for some reason.
        Toaster.showLong(BootstrapAuthenticatorActivity.this, R.string.message_bad_credentials);
    }

    /**
     * Handles onClick event on the Submit button. Sends username/password to
     * the server for authentication.
     * <p/>
     * Specified by android:onClick="handleLogin" in the layout xml
     *
     * @param view
     */
    public void handleLogin(final View view) {
        if (authenticationTask != null) {
            return;
        }

        if (requestNewAccount) {
            email = emailText.getText().toString();
        }

        password = passwordText.getText().toString();
        showProgress();

        authenticationTask = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {

                final String query = String.format("%s=%s&%s=%s",
                        PARAM_USERNAME, email, PARAM_PASSWORD, password);

                User loginResponse = DSLFYService.authenticate(email, password);
                token = loginResponse.getSessionToken();

                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                // Retrofit Errors are handled inside of the {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showLong(BootstrapAuthenticatorActivity.this, cause.getMessage());
                    }
                }
            }

            @Override
            public void onSuccess(final Boolean authSuccess) {
                onAuthenticationResult(authSuccess);
            }

            @Override
            protected void onFinally() throws RuntimeException {
                hideProgress();
                authenticationTask = null;
            }
        };
        authenticationTask.execute();
    }

    /**
     * Called when response is received from the server for confirm credentials
     * request. See onAuthenticationResult(). Sets the
     * AccountAuthenticatorResult which is sent back to the caller.
     *
     * @param result
     */
    protected void finishConfirmCredentials(final boolean result) {
        final Account account = new Account(email, Constants.Auth.DSLFY_ACCOUNT_TYPE);
        accountManager.setPassword(account, password);

        final Intent intent = new Intent();
        intent.putExtra(KEY_BOOLEAN_RESULT, result);
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Called when response is received from the server for authentication
     * request. See onAuthenticationResult(). Sets the
     * AccountAuthenticatorResult which is sent back to the caller. Also sets
     * the authToken in AccountManager for this account.
     */

    protected void finishLogin() {
        if(email == null){
            Toast.makeText(this, "Login did not provide user data.", Toast.LENGTH_SHORT).show();
            return;
        }
        final Account account = new Account(email, Constants.Auth.DSLFY_ACCOUNT_TYPE);

        if (requestNewAccount) {
            accountManager.addAccountExplicitly(account, password, null);
        } else {
            accountManager.setPassword(account, password);
        }

        authToken = token;

        final Intent intent = new Intent();
        intent.putExtra(KEY_ACCOUNT_NAME, email);
        intent.putExtra(KEY_ACCOUNT_TYPE, Constants.Auth.DSLFY_ACCOUNT_TYPE);

        if (authTokenType != null
                && authTokenType.equals(Constants.Auth.AUTHTOKEN_TYPE)) {
            intent.putExtra(KEY_AUTHTOKEN, authToken);
        }

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Hide progress dialog
     */
    @SuppressWarnings("deprecation")
    protected void hideProgress() {
        dismissDialog(0);
    }

    /**
     * Show progress dialog
     */
    @SuppressWarnings("deprecation")
    protected void showProgress() {
        showDialog(0);
    }

    /**
     * Called when the authentication process completes (see attemptLogin()).
     *
     * @param result
     */
    public void onAuthenticationResult(final boolean result) {
        if (result) {
            if (!confirmCredentials) {
                finishLogin();
            } else {
                finishConfirmCredentials(true);
            }
        } else {
            Ln.d("onAuthenticationResult: failed to authenticate");
            if (requestNewAccount) {
                Toaster.showLong(BootstrapAuthenticatorActivity.this,
                        string.message_auth_failed_new_account);
            } else {
                Toaster.showLong(BootstrapAuthenticatorActivity.this,
                        string.message_auth_failed);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Ln.d("onActivityResult for "+requestType);

        //Facebook
        if(requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()){
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } else if(requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
            twitterSignInButton.onActivityResult(requestCode, resultCode, data);
        } else {
            Ln.w("Requestcode %d was not recognized.", requestCode);
        }

        if(CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()
                == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE){
            Ln.e("Twitter and Facebook requestCodes were the same. Undefined behaviour!");
        }
    }
}
