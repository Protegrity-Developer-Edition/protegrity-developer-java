Feature: Successful Protection Operations For Integer Input Data
    As a user of the Application Protector Java SDK
    I want to perform protect and unprotect operations on integer input data
    So that I can ensure data security and integrity

    Background:
        Given the Application Protector Java SDK is initialized
        And the environment variables DEV_EDITION_EMAIL, DEV_EDITION_PASSWORD and DEV_EDITION_API_KEY are set
        And a policy is deployed with required data elements and user "superuser" is present

    @tokenization @single @integer @positive
    Scenario: Tokenize and Detokenize single integer input data
        When the user performs "tokenization" operations on single integer input data using the following data elements
            | data_element | input_data  |
            | int          | 12345       |
            | int          | 0           |
            | int          | -2147483648 |
            | int          | 2147483647  |
            | int          | 1           |
            | int          | -1          |
            | int          | 100         |
            | int          | -100        |
            | int          | 1615614986  |
            | int          | -1426897611 |
        Then all the integer data is validated
        And all the protected data should not equal the original data
        And all the unprotected data should equal the original data

    @tokenization @bulk @integer @positive
    Scenario: Tokenize and Detokenize bulk integer input data
        When the user performs "tokenization" operations on bulk integer input data using the following data elements
            | data_element | input_data                        |
            | int          | 12345,67890,23456,78901,34567     |
            | int          | 0,1,-1,100,-100                   |
            | int          | 2147483647,-2147483648,1615614986 |
        Then all the bulk integer data is validated
        And all the protected data should not equal the original data
        And all the unprotected data should equal the original data

    @encryption @single @integer @positive
    Scenario: Encrypt and Decrypt single integer input data
        When the user performs "encryption" operations on single integer input data using the following data elements
            | data_element | input_data  |
            | text         | 12345       |
            | text         | 0           |
            | text         | -2147483648 |
            | text         | 2147483647  |
            | text         | 1           |
            | text         | -1          |
            | text         | 100         |
            | text         | -100        |
            | text         | 1615614986  |
            | text         | -1426897611 |
        Then all the integer data is validated
        And all the protected data should not equal the original data
        And all the unprotected data should equal the original data

    @encryption @bulk @integer @positive
    Scenario: Encrypt and Decrypt bulk integer input data
        When the user performs "encryption" operations on bulk integer input data using the following data elements
            | data_element | input_data                        |
            | text         | 12345,67890,23456,78901,34567     |
            | text         | 0,1,-1,100,-100                   |
            | text         | 2147483647,-2147483648,1615614986 |
        Then all the bulk integer data is validated
        And all the protected data should not equal the original data
        And all the unprotected data should equal the original data

    @tokenization @single @eiv @integer @positive
    Scenario: Tokenize and Detokenize single integer input data with External Initialization Vector (EIV)
        When the user performs "tokenization" operations on single integer input data with external IV using the following data elements
            | data_element | input_data | external_iv |
            | int          | 12345      | iv_int_001  |
            | int          | 0          | iv_int_002  |
            | int          | -1         | iv_int_003  |
            | int          | 2147483647 | iv_int_004  |
        Then all the integer data is validated for eiv
        And the same external IV produces consistent protected values
        And all the unprotected data should equal the original data
