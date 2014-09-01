package com.bitvault;

import java.util.*;

import com.google.common.io.BaseEncoding;

/**
 * Created by Julian on 8/27/14.
 */
public class Authorizer {
    private Map<String, String> schemes = new HashMap<String, String>();

    public void authorize(String scheme, Map<String, String> params) {
        String credential = null;
        if (scheme.equals("Basic")) {
            String email = params.get("email");
            String password = params.get("password");
            credential = email + ":" + password;
            //credential = Base64.getEncoder().encodeToString(credential.getBytes());
            credential = BaseEncoding.base64().encode(credential.getBytes());
        } else {
            credential = compileParams(params);
        }

        schemes.put(scheme, credential);
    }

    public String getCredentials(String scheme) {
        String credential = schemes.get(scheme);

        if (credential == null) credential = "data=none";

        return scheme + " " + credential;
    }

    public boolean isAuthorized(String scheme) {
        return schemes.containsKey(scheme);
    }

    private String compileParams(Map<String, String> params) {
        List<String> credentials = new ArrayList<String>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            credentials.add(entry.getKey() + "=" + entry.getValue());
        }
        return Util.join(credentials, ", ");
    }
}
