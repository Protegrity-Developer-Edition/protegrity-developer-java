package com.protegrity.ap.java.auth;

import java.util.Map;

/**
 * Auth provider for no authentication (internal/trusted networks).
 *
 * <p>Used when Cloud Protect is deployed on an internal network (e.g., OpenShift pod-to-pod)
 * where no authentication is required at the API gateway.
 *
 * @since 1.1.0
 */
public class NoneAuthProvider implements AuthProvider {

    @Override
    public void initialize() {
        // No initialization needed
    }

    @Override
    public Map<String, String> authenticateRequest(String method, String url,
                                                   Map<String, String> headers, byte[] body) {
        // No auth headers added
        return headers;
    }

    @Override
    public String getAuthMode() {
        return "none";
    }
}
