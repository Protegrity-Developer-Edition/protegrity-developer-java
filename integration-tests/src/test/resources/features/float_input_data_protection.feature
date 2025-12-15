Feature: Successful Protection Operations For Float Input Data
    As a user of the Application Protector Java SDK
    I want to perform protect and unprotect operations on float input data
    So that I can ensure data security and integrity

    Background:
        Given the Application Protector Java SDK is initialized
        And the environment variables DEV_EDITION_EMAIL, DEV_EDITION_PASSWORD and DEV_EDITION_API_KEY are set
        And a policy is deployed with required data elements and user "superuser" is present

    @tokenization @bulk @float @positive
    Scenario: Tokenize and Detokenize bulk float input data
        When the user performs "tokenization" operations on bulk float input data using the following data elements
            | data_element  | input_data                        |
            | no_encryption | 12345,67890,23456,78901,34567     |
            | no_encryption | 0,1,-1,100,-100                   |
            | no_encryption | 2147483647,-2147483648,1615614986 |
        Then all the bulk float data is validated
        And all the protected data should equal the original data
        And all the unprotected data should equal the original data

    @encryption @single @float @positive
    Scenario: Encrypt and Decrypt single float input data
        When the user performs "encryption" operations on single float input data using the following data elements
            | data_element | input_data         |
            | text         | 12345              |
            | text         | 0                  |
            | text         | -2147483648        |
            | text         | 2147483647         |
            | text         | 1                  |
            | text         | -1                 |
            | text         | 100                |
            | text         | -100               |
            | text         | 1615614986         |
            | text         | -1426897611        |
            | text         | 1.00000000000      |
            | text         | 3900321272.9094255 |
            | text         | -1391345.81184187  |
        Then all the float data is validated
        And all the protected data should not equal the original data
        And all the unprotected data should equal the original data

    @encryption @bulk @float @positive
    Scenario: Encrypt and Decrypt bulk float input data
        When the user performs "encryption" operations on bulk float input data using the following data elements
            | data_element | input_data                                                             |
            | text         | 12345,67890,23456,78901,34567,1.00000000000                            |
            | text         | 0,1,-1,100,-100,1.00000000000                                          |
            | text         | 2147483647,-2147483648,1615614986,-1391345.81184187,3900321272.9094255 |
        Then all the bulk float data is validated
        And all the protected data should not equal the original data
        And all the unprotected data should equal the original data
