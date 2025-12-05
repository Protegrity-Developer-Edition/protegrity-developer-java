package com.protegrity.ap.integration;

import org.junit.platform.suite.api.*;
import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * Cucumber test runner for integration tests.
 * 
 * To run specific tags, use:
 * mvn test -Dcucumber.filter.tags="@positive"
 * mvn test -Dcucumber.filter.tags="@tokenization and @single"
 * mvn test -Dcucumber.filter.tags="@string or @integer"
 * mvn test -Dcucumber.filter.tags="not @negative"
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty,html:target/cucumber-html.html,json:target/cucumber-reports/cucumber.json")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.protegrity.ap.integration")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "not @wip")
public class CucumberIntegrationTest {
    // This class will be used as the test runner
    // All cucumber configurations are done via annotations
}
