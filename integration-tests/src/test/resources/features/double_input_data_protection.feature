Feature: Successful Protection Operations For Double Input Data
    As a user of the Application Protector Java SDK
    I want to perform protect and unprotect operations on double input data
    So that I can ensure data security and integrity

    Background:
        Given the Application Protector Java SDK is initialized
        And the environment variables DEV_EDITION_EMAIL, DEV_EDITION_PASSWORD and DEV_EDITION_API_KEY are set
        And a policy is deployed with required data elements and user "superuser" is present

    @tokenization @bulk @double @positive
    Scenario: Tokenize and Detokenize bulk double input data
        When the user performs "tokenization" operations on bulk double input data using the following data elements
            | data_element  | input_data                        |
            | no_encryption | 12345,67890,23456,78901,34567     |
            | no_encryption | 0,1,-1,100,-100                   |
            | no_encryption | 2147483647,-2147483648,1615614986 |
        Then all the bulk double data is validated
        And all the protected data should equal the original data
        And all the unprotected data should equal the original data

    @encryption @single @double @positive
    Scenario: Encrypt and Decrypt single double input data
        When the user performs "encryption" operations on single double input data using the following data elements
            | data_element | input_data             |
            | text         | 12345                  |
            | text         | 0                      |
            | text         | -2147483648            |
            | text         | 2147483647             |
            | text         | 1                      |
            | text         | -1                     |
            | text         | 100                    |
            | text         | -100                   |
            | text         | 1615614986             |
            | text         | -1426897611            |
            | text         | 1.00000000000          |
            | text         | 3900321272.9094255     |
            | text         | -1391345.81184187      |
            | text         | 1.7976931348623157E308 |
            | text         | 4.9E-324               |
            | text         | 3.141592653589793      |
        Then all the double data is validated
        And all the protected data should not equal the original data
        And all the unprotected data should equal the original data

    @encryption @bulk @double @positive
    Scenario: Encrypt and Decrypt bulk double input data
        When the user performs "encryption" operations on bulk double input data using the following data elements
            | data_element | input_data                                                                               |
            | text         | 12345,67890,23456,78901,34567,1.00000000000                                              |
            | text         | 0,1,-1,100,-100,1.00000000000                                                            |
            | text         | 2147483647,-2147483648,1615614986,-1391345.81184187,3900321272.9094255,3.141592653589793 |
        Then all the bulk double data is validated
        And all the protected data should not equal the original data
        And all the unprotected data should equal the original data
