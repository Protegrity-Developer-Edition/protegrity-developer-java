package com.protegrity.ap.java.auth;

import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Auth provider for AWS IAM (SigV4) authentication.
 *
 * <p>Signs requests using AWS Signature Version 4. Credentials are resolved from the
 * standard AWS credential chain: environment variables (AWS_ACCESS_KEY_ID,
 * AWS_SECRET_ACCESS_KEY, AWS_SESSION_TOKEN) or AWS profile.
 *
 * @since 1.1.0
 */
public class AWSIAMAuthProvider implements AuthProvider {

    private static final Logger logger = LoggerFactory.getLogger(AWSIAMAuthProvider.class);
    private static final String ALGORITHM = "AWS4-HMAC-SHA256";
    private static final String SERVICE = "execute-api";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");

    private String accessKeyId;
    private String secretAccessKey;
    private String sessionToken;
    private String region;

    @Override
    public void initialize() throws AuthenticationException {
        this.accessKeyId = System.getenv("AWS_ACCESS_KEY_ID");
        this.secretAccessKey = System.getenv("AWS_SECRET_ACCESS_KEY");
        this.sessionToken = System.getenv("AWS_SESSION_TOKEN");
        this.region = System.getenv("AWS_DEFAULT_REGION");

        if (this.region == null || this.region.isEmpty()) {
            this.region = System.getenv("AWS_REGION");
        }
        if (this.region == null || this.region.isEmpty()) {
            this.region = "us-east-1";
        }

        if (this.accessKeyId == null || this.secretAccessKey == null) {
            // Try to load from default credential provider chain via profile
            String profile = System.getenv("AWS_PROFILE");
            if (profile != null && !profile.isEmpty()) {
                loadFromProfile(profile);
            }
        }

        if (this.accessKeyId == null || this.secretAccessKey == null) {
            throw new AuthenticationException(
                "AWS credentials not found. Set AWS_ACCESS_KEY_ID/AWS_SECRET_ACCESS_KEY or AWS_PROFILE.");
        }
    }

    private void loadFromProfile(String profile) {
        // Prefer `aws configure export-credentials` — works for SSO, sso-session,
        // assume-role, and static profiles by resolving the AWS credential chain.
        // Falls back to `aws configure get` for older AWS CLI versions
        // (pre-v2.13) that lack export-credentials.
        if (loadFromExportCredentials(profile)) {
            return;
        }
        loadFromConfigureGet(profile);
    }

    private boolean loadFromExportCredentials(String profile) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "aws", "configure", "export-credentials",
                "--profile", profile, "--format", "env");
            Process p = pb.start();
            String stdout = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            p.waitFor();
            if (p.exitValue() != 0) {
                return false;
            }
            // Lines look like: export AWS_ACCESS_KEY_ID=AKIA...
            for (String line : stdout.split("\\R")) {
                line = line.trim();
                if (line.startsWith("export ")) {
                    line = line.substring("export ".length());
                }
                int eq = line.indexOf('=');
                if (eq <= 0) continue;
                String key = line.substring(0, eq).trim();
                String val = line.substring(eq + 1).trim();
                if (val.length() >= 2 && val.charAt(0) == '"' && val.charAt(val.length() - 1) == '"') {
                    val = val.substring(1, val.length() - 1);
                }
                switch (key) {
                    case "AWS_ACCESS_KEY_ID":     this.accessKeyId = val; break;
                    case "AWS_SECRET_ACCESS_KEY": this.secretAccessKey = val; break;
                    case "AWS_SESSION_TOKEN":     this.sessionToken = val; break;
                    case "AWS_DEFAULT_REGION":
                    case "AWS_REGION":
                        if (this.region == null || this.region.isEmpty()) this.region = val;
                        break;
                    default: break;
                }
            }
            return this.accessKeyId != null && this.secretAccessKey != null;
        } catch (Exception e) {
            logger.debug("export-credentials unavailable for profile '{}': {}", profile, e.getMessage());
            return false;
        }
    }

    private void loadFromConfigureGet(String profile) {
        // Legacy path: only works for profiles with static keys in ~/.aws/credentials.
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "aws", "configure", "get", "aws_access_key_id", "--profile", profile);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            String keyId = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
            p.waitFor();
            if (p.exitValue() == 0 && !keyId.isEmpty()) {
                this.accessKeyId = keyId;
            }

            pb = new ProcessBuilder(
                "aws", "configure", "get", "aws_secret_access_key", "--profile", profile);
            pb.redirectErrorStream(true);
            p = pb.start();
            String secret = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
            p.waitFor();
            if (p.exitValue() == 0 && !secret.isEmpty()) {
                this.secretAccessKey = secret;
            }

            // Try session token (may not exist for long-term creds)
            pb = new ProcessBuilder(
                "aws", "configure", "get", "aws_session_token", "--profile", profile);
            pb.redirectErrorStream(true);
            p = pb.start();
            String token = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
            p.waitFor();
            if (p.exitValue() == 0 && !token.isEmpty()) {
                this.sessionToken = token;
            }

            // Region
            pb = new ProcessBuilder(
                "aws", "configure", "get", "region", "--profile", profile);
            pb.redirectErrorStream(true);
            p = pb.start();
            String reg = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
            p.waitFor();
            if (p.exitValue() == 0 && !reg.isEmpty()) {
                this.region = reg;
            }
        } catch (Exception e) {
            logger.warn("Failed to load AWS profile '{}': {}", profile, e.getMessage());
        }
    }

    @Override
    public Map<String, String> authenticateRequest(String method, String url,
                                                   Map<String, String> headers, byte[] body) {
        try {
            ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
            String dateStamp = now.format(DATE_FORMAT);
            String amzDate = now.format(DATETIME_FORMAT);

            URI uri = new URI(url);
            String host = uri.getHost();
            String path = uri.getRawPath();
            if (path == null || path.isEmpty()) path = "/";
            String query = uri.getRawQuery() != null ? uri.getRawQuery() : "";

            headers.put("host", host);
            headers.put("x-amz-date", amzDate);
            if (sessionToken != null && !sessionToken.isEmpty()) {
                headers.put("x-amz-security-token", sessionToken);
            }

            // Create canonical request
            String payloadHash = sha256Hex(body != null ? body : new byte[0]);
            headers.put("x-amz-content-sha256", payloadHash);

            TreeMap<String, String> sortedHeaders = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            sortedHeaders.putAll(headers);

            StringBuilder canonicalHeaders = new StringBuilder();
            StringBuilder signedHeadersList = new StringBuilder();
            for (Map.Entry<String, String> entry : sortedHeaders.entrySet()) {
                String key = entry.getKey().toLowerCase();
                canonicalHeaders.append(key).append(":").append(entry.getValue().trim()).append("\n");
                if (signedHeadersList.length() > 0) signedHeadersList.append(";");
                signedHeadersList.append(key);
            }

            String signedHeaders = signedHeadersList.toString();
            String canonicalRequest = method + "\n" + path + "\n" + query + "\n"
                + canonicalHeaders + "\n" + signedHeaders + "\n" + payloadHash;

            // Create string to sign
            String credentialScope = dateStamp + "/" + region + "/" + SERVICE + "/aws4_request";
            String stringToSign = ALGORITHM + "\n" + amzDate + "\n" + credentialScope + "\n"
                + sha256Hex(canonicalRequest.getBytes(StandardCharsets.UTF_8));

            // Calculate signature
            byte[] signingKey = getSignatureKey(secretAccessKey, dateStamp, region, SERVICE);
            String signature = hmacSha256Hex(signingKey, stringToSign);

            // Build Authorization header
            String authorization = ALGORITHM + " Credential=" + accessKeyId + "/" + credentialScope
                + ", SignedHeaders=" + signedHeaders + ", Signature=" + signature;
            headers.put("Authorization", authorization);

            return headers;
        } catch (Exception e) {
            throw new AuthenticationException("SigV4 signing failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String getAuthMode() {
        return "aws_iam";
    }

    private static byte[] hmacSha256(byte[] key, String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    private static String hmacSha256Hex(byte[] key, String data) throws Exception {
        byte[] hash = hmacSha256(key, data);
        return bytesToHex(hash);
    }

    private static byte[] getSignatureKey(String key, String dateStamp, String region, String service) throws Exception {
        byte[] kSecret = ("AWS4" + key).getBytes(StandardCharsets.UTF_8);
        byte[] kDate = hmacSha256(kSecret, dateStamp);
        byte[] kRegion = hmacSha256(kDate, region);
        byte[] kService = hmacSha256(kRegion, service);
        return hmacSha256(kService, "aws4_request");
    }

    private static String sha256Hex(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data);
        return bytesToHex(hash);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
