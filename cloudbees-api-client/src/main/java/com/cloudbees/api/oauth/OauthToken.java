package com.cloudbees.api.oauth;

import com.cloudbees.api.BeesClient;
import com.cloudbees.api.BeesClientConfiguration;
import com.cloudbees.api.cr.Capability;
import com.cloudbees.api.cr.Credential;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.annotation.CheckReturnValue;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * OAuth token and information contained within it as defined by the OAuth spec.
 *
 * @author Vivek Pandey
 */
public class OauthToken extends AbstractOauthToken implements Cloneable {
    /**
     * A short-lived opaque token that you'll send in the "Authorize" HTTP header as "Authorize: bearer <i>valueOfAccessToken</i>"
     */
    @JsonProperty("access_token")
    public String accessToken;

    /**
     * Refresh token lasts for a long time and can be used to obtain additional {@link #accessToken}s.
     */
    @JsonProperty("refresh_token")
    public String refreshToken;

    @JsonProperty("token_type")
    public String tokenType;

    @JsonProperty("client_id")
    public String clientId;

    /**
     * Right now an OAuth token only grants access to one account only, but this might be something
     * we may want to change later, so we aren't providing direct access to this property.
     */
    @JsonProperty("account")
    private String account;

    /**
     * The number of seconds the access token will be valid, relative to the point of time where
     * the call is issued to obtain this object (such as via {@link OauthClient#validateToken(String, String...)}.
     *
     * 0 or less means the token has already expired.
     */
    @JsonProperty("expires_in")
    private Integer expiresIn;

    /**
     * White-space separated OAuth scopes of this token.
     *
     * The meaning of the scope values are up to the applications.
     */
    @JsonProperty("scope")
    public String scope;

    /**
     * OAuth scopes of this token split by the whitespace.
     */
    public List<String> getScopes() {
        return Arrays.asList(scope.split(" "));
    }

    /**
     * Return true if the given scope is fond in the scopes granted with this token
     */
    @JsonIgnore
    @CheckReturnValue
    public boolean validateScope(String scope){
        if(scope == null){
            return false;
        }
        for(String s: getScopes()){
            if(s.trim().equals(scope)){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if this token has any of the scopes specified
     */
    @CheckReturnValue
    public boolean validateScopes(String... scopes){
        for (String s : scopes) {
            if (validateScope(s))
                return true;
        }
        return false;
    }

    /**
     * Checks if this token has a scope that matches the given domain name and capability.
     *
     * @param host
     *      Represents the endpoint.
     */
    @CheckReturnValue
    public boolean validateCapability(URL host, Capability cap) {
        return validateScope(cap.to(host)) || validateScope(cap.toAll());
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

    /**
     * The number of seconds the access token will be valid, relative to the point of time where
     * the call is issued to obtain this object (such as via {@link OauthClient#validateToken(String, String...)}.
     *
     * 0 or less means the token has already expired.
     */
    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn){
        this.expiresIn = expiresIn;
    }

    @JsonIgnore
    public boolean isExpired(){
        return (expiresIn != null && expiresIn <= 0);
    }

    public OauthToken clone() {
        try {
            return (OauthToken)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Creates a new {@link BeesClient} that uses the access token encapsulated in this {@link OauthToken}
     */
    public BeesClient createClient() {
        BeesClientConfiguration config = new BeesClientConfiguration();
        config.setOAuthToken(accessToken);
        return new BeesClient(config);
    }

    /**
     * Creates a new {@link BeesClient} that uses the access token encapsulated in this {@link OauthToken}.
     *
     * @param current
     *      The configuration parameter to inherit from. This method does not mutate this object.
     */
    public BeesClient createClient(BeesClientConfiguration current) {
        // TODO: there should be a copy constructor
        BeesClientConfiguration config = new BeesClientConfiguration(current.getServerApiUrl());
        config.setOAuthToken(accessToken);
        return new BeesClient(config);
    }

    /**
     * Uses this OAuth token as a {@link Credential} object.
     */
    public Credential asCredential() {
        return Credential.oauth(this);
    }
}
