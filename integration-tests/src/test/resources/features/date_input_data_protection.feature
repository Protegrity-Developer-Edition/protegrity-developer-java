Feature: Successful Protection Operations For Date Input Data
    As a user of the Application Protector Java SDK
    I want to perform protect and unprotect operations on date input data
    So that I can ensure data security and integrity

    Background:
        Given the Application Protector Java SDK is initialized
        And the environment variables DEV_EDITION_EMAIL, DEV_EDITION_PASSWORD and DEV_EDITION_API_KEY are set
        And a policy is deployed with required data elements and user "superuser" is present

    @tokenization @single @date @positive
    Scenario: Tokenize and Detokenize single date input data
        When the user performs "tokenization" operations on single date input data using the following data elements
            | data_element | input_data              |
            | datetime     | 0600-01-01T00:00:00.000 |
            | datetime     | 2005-02-24 16:17:12.604 |
            | datetime     | 1931.07/16T15:29:24,838 |
            | datetime     | 1970-04-02T07:00:58.369 |
            | datetime     | 1963-03-21T20:00:22,816 |
            | datetime     | 1911-07-10 00:10:51,805 |
            | datetime     | 1906-12-02T13:11:09.400 |
            | datetime     | 1958/06/30 11:38:03,051 |
            | datetime     | 1897.07/12T23:42:54.666 |
            | datetime     | 3337-11-27T23:59:59.999 |
            | datetime     | 2000-01-01              |
            | datetime     | 1999-12-31              |
            | datetime     | 2024-06-15              |
        Then all the date data is validated
        And all the protected data should not equal the original data
        And all the unprotected data should equal the original data

    @tokenization @bulk @date @positive
    Scenario: Tokenize and Detokenize bulk date input data
        When the user performs "tokenization" operations on bulk date input data using the following data elements
            | data_element | input_data                                                                                         |
            | datetime     | 0600-01-01T00:00:00.000; 2005-02-24 16:17:12.604; 1931.07/16T15:29:24,838                          |
            | datetime     | 1970-04-02T07:00:58.369; 1963-03-21T20:00:22,816; 1911-07-10 00:10:51,805; 1906-12-02T13:11:09.400 |
            | datetime     | 2000-01-01; 1999-12-31; 2024-06-15                                                                 |
        Then all the bulk date data is validated
        And all the protected data should not equal the original data
        And all the unprotected data should equal the original data
