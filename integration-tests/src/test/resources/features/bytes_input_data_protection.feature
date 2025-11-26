Feature: Successful Protection Operations For Bytes Input Data
    As a user of the Application Protector Java SDK
    I want to perform protect and unprotect operations on byte array input data
    So that I can ensure data security and integrity

    Background:
        Given the Application Protector Java SDK is initialized
        And the environment variables DEV_EDITION_EMAIL, DEV_EDITION_PASSWORD and DEV_EDITION_API_KEY are set
        And a policy is deployed with required data elements and user "superuser" is present

    @tokenization @single @bytes @positive
    Scenario: Tokenize and Detokenize single bytes input data
        When the user performs "tokenization" operations on single bytes input data using the following data elements
            | data_element | input_data               |
            | name         | John Doe                 |
            | address      | 123 Main St, Springfield |
            | city         | Springfield              |
            | email        | johndoe@example.com      |
            | ssn          | 123-45-6789              |
            | ccn          | 4111111111111111         |
        Then all the bytes data is validated
        And all the protected data should not equal the original data
        And all the unprotected data should equal the original data

    @tokenization @bulk @bytes @positive
    Scenario: Tokenize and Detokenize bulk bytes input data
        When the user performs "tokenization" operations on bulk bytes input data using the following data elements
            | data_element | input_data                                                   |
            | name         | John Doe,Jane Smith,Alice Johnson                            |
            | email        | johndoe@example.com,janesmith@domain.co.uk,alicej@web.de     |
            | ssn          | 123-45-6789,987-65-4321,111-22-3333                          |
        Then all the bulk bytes data is validated
        And all the protected data should not equal the original data
        And all the unprotected data should equal the original data
