package com.cloudbees.api.oauth;

import com.cloudbees.api.OAuthObject;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * Common part between {@link OauthToken} and {@link OauthTokenDetail}
 *
 * @author Kohsuke Kawaguchi
 */
public class AbstractOauthToken extends OAuthObject {
    /**
     * ID that represents this token among other tokens that the user has created.
     * Used for updating/revoking this token.
     */
    @JsonProperty("id")
    public String id;

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
     * Deletes this token.
     */
    public void delete() throws OauthClientException {
        owner.deleteToken(id);
    }
}
