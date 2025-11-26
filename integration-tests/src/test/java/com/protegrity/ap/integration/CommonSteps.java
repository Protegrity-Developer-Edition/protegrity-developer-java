package com.protegrity.ap.integration;

import com.protegrity.ap.java.Protector;
import com.protegrity.ap.java.ProtectorException;
import com.protegrity.ap.java.SessionObject;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.And;

import static org.junit.Assert.*;

/**
 * Common step definitions shared across all feature files.
 */
public class CommonSteps {
    private final TestContext testContext;
    
    public CommonSteps(TestContext testContext) {
        this.testContext = testContext;
    }
    
    @Before
    public void setUp() {
        testContext.reset();
    }
    
    @Given("the Application Protector Java SDK is initialized")
    public void theApplicationProtectorJavaSDKIsInitialized() throws ProtectorException {
        Protector protector = Protector.getProtector();
        assertNotNull("Protector should be initialized", protector);
        testContext.setProtector(protector);
        
        // Create default session with superuser
        SessionObject session = protector.createSession("superuser");
        testContext.setSession(session);
    }
    
    @And("the environment variables DEV_EDITION_EMAIL, DEV_EDITION_PASSWORD and DEV_EDITION_API_KEY are set")
    public void theEnvironmentVariablesAreSet() {
        String email = System.getenv("DEV_EDITION_EMAIL");
        String password = System.getenv("DEV_EDITION_PASSWORD");
        String apiKey = System.getenv("DEV_EDITION_API_KEY");
        
        assertNotNull("DEV_EDITION_EMAIL environment variable should be set", email);
        assertNotNull("DEV_EDITION_PASSWORD environment variable should be set", password);
        assertNotNull("DEV_EDITION_API_KEY environment variable should be set", apiKey);
    }
    
    @And("a policy is deployed with required data elements and user {string} is present")
    public void aPolicyIsDeployedWithRequiredDataElementsAndUserIsPresent(String user) {
        testContext.setCurrentUser(user);
    }
    
    @And("a policy is deployed with required data elements, users and permissions required to test the scenario")
    public void aPolicyIsDeployedWithRequiredDataElementsUsersAndPermissions() {
        // Policy is assumed to be deployed on the server
        // This is a placeholder for policy validation
    }
}
