Feature: Successful Protection Operations For Long Input Data
    As a user of the Application Protector Java SDK
    I want to perform protect and unprotect operations on long input data
    So that I can ensure data security and integrity

    Background:
        Given the Application Protector Java SDK is initialized
        And the environment variables DEV_EDITION_EMAIL, DEV_EDITION_PASSWORD and DEV_EDITION_API_KEY are set
        And a policy is deployed with required data elements and user "superuser" is present

    @tokenization @single @long @positive
    Scenario: Tokenize and Detokenize single long input data
        When the user performs "tokenization" operations on single long input data using the following data elements
            | data_element | input_data           |
            | long         | 0                    |
            | long         | 9223372036854775807  |
            | long         | -9223372036854775808 |
            | long         | -1                   |
            | long         | 1                    |
            | long         | -22                  |
            | long         | 22                   |
            | long         | -333                 |
            | long         | 333                  |
        Then all the long data is validated
        And all the protected data should not equal the original data
        And all the unprotected data should equal the original data

    @tokenization @bulk @long @positive
    Scenario: Tokenize and Detokenize bulk long input data
        When the user performs "tokenization" operations on bulk long input data using the following data elements
            | data_element | input_data                               |
            | long         | 0,1,-1,22,-22,333,-333                   |
            | long         | 9223372036854775807,-9223372036854775808 |
        Then all the bulk long data is validated
        And all the protected data should not equal the original data
        And all the unprotected data should equal the original data

    @encryption @single @long @positive
    Scenario: Encrypt and Decrypt single long input data
        When the user performs "encryption" operations on single long input data using the following data elements
            | data_element | input_data           |
            | text         | 0                    |
            | text         | 9223372036854775807  |
            | text         | -9223372036854775808 |
            | text         | -1                   |
            | text         | 1                    |
            | text         | -22                  |
            | text         | 22                   |
            | text         | -333                 |
            | text         | 333                  |
        Then all the long data is validated
        And all the protected data should not equal the original data
        And all the unprotected data should equal the original data

    @encryption @bulk @long @positive
    Scenario: Encrypt and Decrypt bulk long input data
        When the user performs "encryption" operations on bulk long input data using the following data elements
            | data_element | input_data                               |
            | text         | 0,1,-1,22,-22,333,-333                   |
            | text         | 9223372036854775807,-9223372036854775808 |
        Then all the bulk long data is validated
        And all the protected data should not equal the original data
        And all the unprotected data should equal the original data

    @tokenization @single @eiv @long @positive
    Scenario: Tokenize and Detokenize single long input data with External Initialization Vector (EIV)
        When the user performs "tokenization" operations on single long input data with external IV using the following data elements
            | data_element | input_data          | external_iv |
            | long         | 0                   | iv_long_001 |
            | long         | 9223372036854775807 | iv_long_002 |
            | long         | -1                  | iv_long_003 |
            | long         | 1                   | iv_long_004 |
        Then all the long data is validated for eiv
        And the same external IV produces consistent protected values
        And all the unprotected data should equal the original data
