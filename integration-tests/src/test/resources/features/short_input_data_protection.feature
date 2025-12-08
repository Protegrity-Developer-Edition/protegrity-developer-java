Feature: Successful Protection Operations For Short Input Data
    As a user of the Application Protector Java SDK
    I want to perform protect and unprotect operations on short input data
    So that I can ensure data security and integrity

    Background:
        Given the Application Protector Java SDK is initialized
        And the environment variables DEV_EDITION_EMAIL, DEV_EDITION_PASSWORD and DEV_EDITION_API_KEY are set
        And a policy is deployed with required data elements and user "superuser" is present

    @tokenization @single @short @positive
    Scenario: Tokenize and Detokenize single short input data
        When the user performs "tokenization" operations on single short input data using the following data elements
            | data_element | input_data |
            | short        | 12345      |
            | short        | 0          |
            | short        | -32768     |
            | short        | 32767      |
            | short        | 1          |
            | short        | -1         |
            | short        | 100        |
            | short        | -100       |
            | short        | 16156      |
            | short        | -14268     |
        Then all the short data is validated
        And all the protected data should not equal the original data
        And all the unprotected data should equal the original data

    @tokenization @bulk @short @positive
    Scenario: Tokenize and Detokenize bulk short input data
        When the user performs "tokenization" operations on bulk short input data using the following data elements
            | data_element | input_data                 |
            | short        | 12345,6789,23456,7890,3456 |
            | short        | 0,1,-1,100,-100            |
            | short        | 32767,-32768,16156         |
        Then all the bulk short data is validated
        And all the protected data should not equal the original data
        And all the unprotected data should equal the original data

    @encryption @single @short @positive
    Scenario: Encrypt and Decrypt single short input data
        When the user performs "encryption" operations on single short input data using the following data elements
            | data_element | input_data |
            | text         | 12345      |
            | text         | 0          |
            | text         | -32768     |
            | text         | 32767      |
            | text         | 1          |
            | text         | -1         |
            | text         | 100        |
            | text         | -100       |
            | text         | 16156      |
            | text         | -14268     |
        Then all the short data is validated
        And all the protected data should not equal the original data
        And all the unprotected data should equal the original data

    @encryption @bulk @short @positive
    Scenario: Encrypt and Decrypt bulk short input data
        When the user performs "encryption" operations on bulk short input data using the following data elements
            | data_element | input_data                 |
            | text         | 12345,6789,23456,7890,3456 |
            | text         | 0,1,-1,100,-100            |
            | text         | 32767,-32768,16156         |
        Then all the bulk short data is validated
        And all the protected data should not equal the original data
        And all the unprotected data should equal the original data

    @tokenization @single @eiv @short @positive
    Scenario: Tokenize and Detokenize single short input data with External Initialization Vector (EIV)
        When the user performs "tokenization" operations on single short input data with external IV using the following data elements
            | data_element | input_data | external_iv  |
            | short        | 12345      | iv_short_001 |
            | short        | 0          | iv_short_002 |
            | short        | -1         | iv_short_003 |
            | short        | 32767      | iv_short_004 |
        Then all the short data is validated for eiv
        And the same external IV produces consistent protected values
        And all the unprotected data should equal the original data
