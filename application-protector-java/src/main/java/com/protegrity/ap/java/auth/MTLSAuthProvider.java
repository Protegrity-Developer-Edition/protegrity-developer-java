package com.protegrity.ap.java.auth;

import java.util.Map;

/**
 * Auth provider for mutual TLS (client certificate) authentication.
 *
 * <p>mTLS authenticates at the TLS layer using a client certificate. No HTTP headers are
 * modified — the TLS handshake provides identity. The cert/key paths are stored here
 * and used by the HTTP client configuration.
 *
 * <p>Environment variables:
 * <ul>
 *   <li>PTY_CLIENT_CERT — path to client certificate (.pem)</li>
 *   <li>PTY_CLIENT_KEY — path to client private key (.key)</li>
 *   <li>PTY_CA_CERT — path to CA bundle for server verification</li>
 * </ul>
 *
 * @since 1.1.0
 */
public class MTLSAuthProvider implements AuthProvider {

    private String clientCert;
    private String clientKey;
    private String caCert;

    @Override
    public void initialize() throws AuthenticationException {
        this.clientCert = System.getenv("PTY_CLIENT_CERT");
        this.clientKey = System.getenv("PTY_CLIENT_KEY");
        this.caCert = System.getenv("PTY_CA_CERT");

        if (clientCert == null || clientCert.isEmpty() || clientKey == null || clientKey.isEmpty()) {
            throw new AuthenticationException(
                "mTLS mode requires PTY_CLIENT_CERT and PTY_CLIENT_KEY environment variables.");
        }
    }

    @Override
    public Map<String, String> authenticateRequest(String method, String url,
                                                   Map<String, String> headers, byte[] body) {
        // mTLS auth is at the TLS handshake layer — no header changes needed
        return headers;
    }

    @Override
    public String getAuthMode() {
        return "mtls";
    }

    public String getClientCert() {
        return clientCert;
    }

    public String getClientKey() {
        return clientKey;
    }

    public String getCaCert() {
        return caCert;
    }
}
