package com.cloudbees.api.oauth;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Vivek Pandey
 */
public class OauthToken {
    /**
     * Refresh token lasts for a long time and can be used to obtain additional {@link #accessToken}s.
     */
    @JsonProperty("refresh_token")
    public String refreshToken;

    /**
     * A short-lived opaque token that you'll send in the "Authorize" HTTP header as "Authorize: bearer <i>valueOfAccessToken</i>"
     */
    @JsonProperty("access_token")
    public String accessToken;

    @JsonProperty("token_type")
    public String tokenType;

    /**
     * The number of seconds the access token will be valid, relative to the point of time where
     * the call is issued to obtain this object (such as via {@link OauthClient#validateToken(String, String...)}.
     *
     * 0 or less means the token has already expired.
     */
    @JsonProperty("expires_in")
    public int expiresIn;

    @JsonProperty("client_id")
    public String clientId;

    /**
     * Internal user ID that identifies the user who generated this token.
     */
    @JsonProperty("uid")
    public String uid;

    /**
     * E-mail address of the user identified by {@link #uid}
     */
    @JsonProperty("email")
    public String email;

    /**
     * Right now an OAuth token only grants access to one account only, but this might be something
     * we may want to change later, so we aren't providing direct access to this property.
     */
    @JsonProperty("account")
    private String account;

    /**
     * ID that represents this token among other tokens that the user has created.
     * Used for updating/revoking this token.
     */
    @JsonProperty("id")
    public String id;

    /**
     * OAuth scopes of this token.
     *
     * The meaning of the scope values are up to the applications.
     */
    @JsonProperty("scopes")
    public List<String> scopes;


    /**
     * Return true if the given scope is fond in the scopes granted with this token
     */
    @JsonIgnore
    public boolean validateScope(String scope){
        if(scope == null){
            return false;
        }
        for(String s: scopes){
            if(s.trim().equals(scope)){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if this token grants access to the specified account.
     */
    public boolean belongsToAccount(String account) {
        return this.account.equals(account);
    }

    /**
     * List up all the accounts to which this token grants some access.
     */
    public Collection<String> listAccounts() {
        return Collections.singleton(account);
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
