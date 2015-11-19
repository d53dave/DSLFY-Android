package net.d53dev.dslfy.android.model;

import android.text.TextUtils;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = -7495897652017488896L;

    protected String username;
    protected String objectId;
    protected String sessionToken;


    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(final String objectId) {
        this.objectId = objectId;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

}
