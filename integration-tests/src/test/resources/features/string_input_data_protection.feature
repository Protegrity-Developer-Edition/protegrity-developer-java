Feature: Successful Protection Operations For String Input Data
    As a user of the Application Protector Java SDK
    I want to perform protect and unprotect operations on string input data
    So that I can ensure data security and integrity

    Background:
        Given the Application Protector Java SDK is initialized
        And the environment variables DEV_EDITION_EMAIL, DEV_EDITION_PASSWORD and DEV_EDITION_API_KEY are set
        And a policy is deployed with required data elements and user "superuser" is present

    @tokenization @single @string @positive
    Scenario: Tokenize and Detokenize single string input data
        When the user performs "tokenization" operations on single string input data using the following data elements
            | data_element | input_data                                                                                                                |
            | name         | John Doe                                                                                                                  |
            | address      | 123 Main St, Springfield                                                                                                  |
            | city         | Springfield                                                                                                               |
            | postcode     | SW1A 1AA                                                                                                                  |
            | zipcode      | 90210                                                                                                                     |
            | phone        | +1-202-555-0173                                                                                                           |
            | email        | johndoe@example.com                                                                                                       |
            | datetime     | 2023-09-04T14:30:00                                                                                                       |
            | int          | 12345                                                                                                                     |
            | nin          | QQ123456C                                                                                                                 |
            | ssn          | 123-45-6789                                                                                                               |
            | ccn          | 4111111111111111                                                                                                          |
            | ccn_bin      | 4111111122223333                                                                                                          |
            | passport     | X1234567                                                                                                                  |
            | iban         | GB82WEST12345698765432                                                                                                    |
            | iban_cc      | 12345678901234567890                                                                                                      |
            | string       | The 'string' data element protects all alphabetic symbols (both lowercase and uppercase letters), as well as digits 1234. |
            | number       | 9876543210 11 234                                                                                                         |
            | name_de      | Max Mustermann                                                                                                            |
            | name_fr      | Jean Dupont                                                                                                               |
            | address_de   | Musterstraße 12, München                                                                                                  |
            | address_fr   | 10 Rue de Rivoli, Paris                                                                                                   |
            | city_de      | München                                                                                                                   |
            | city_fr      | Paris                                                                                                                     |
        Then all the data is validated
        And all the protected data should not equal the original data
        And all the unprotected data should equal the original data

    @encryption @single @string @positive
    Scenario: Encrypt and Decrypt single string input data
        When the user performs "encryption" operations on single string input data using the following data elements
            | data_element | input_data                                                                                                                    |
            | text         | The Encryption data Element , Encrypts all alphabetic symbols (both lowercase and uppercase letters), as well as digits 5678. |
        Then all the data is validated
        And all the protected data should not equal the original data
        And all the unprotected data should equal the original data

    @tokenization @bulk @string @positive
    Scenario: Tokenize and Detokenize bulk string input data
        When the user performs "tokenization" operations on bulk string input data using the following data elements
            | data_element | input_data                                                                   |
            | name         | John Doe,Jane Smith,Alice Johnson,Bob Lee                                    |
            | address      | 123 Main St Springfield,456 Elm St Shelbyville,789 Oak Ave Capital City     |
            | city         | Springfield,Shelbyville,Capital City,Smalltown                               |
            | email        | johndoe@example.com,janesmith@domain.co.uk,alicej@web.de,boblee@free.fr     |
            | ssn          | 123-45-6789,987-65-4321,111-22-3333,444-55-6666                              |
            | ccn          | 4111111111111111,5500000000000004,340000000000009,30000000000004             |
        Then all the bulk data is validated
        And all the protected data should not equal the original data
        And all the unprotected data should equal the original data

    @tokenization @single @eiv @string @positive
    Scenario: Tokenize and Detokenize single string input data with External Initialization Vector (EIV)
        When the user performs "tokenization" operations on single string input data with external IV using the following data elements
            | data_element | input_data                                                                                                                | external_iv       |
            | name         | John Doe                                                                                                                  | iv_name_001       |
            | address      | 123 Main St, Springfield                                                                                                  | iv_address_004    |
            | city         | Springfield                                                                                                               | iv_city_007       |
            | zipcode      | 90210                                                                                                                     | iv_zipcode_011    |
            | email        | johndoe@example.com                                                                                                       | iv_email_013      |
            | int          | 12345                                                                                                                     | iv_int_018        |
            | ssn          | 123-45-6789                                                                                                               | iv_ssn_020        |
            | ccn          | 4111111111111111                                                                                                          | iv_ccn_021        |
            | string       | The 'string' data element protects all alphabetic symbols (both lowercase and uppercase letters), as well as digits 1234. | iv_string_026     |
        Then all the data is validated for eiv
        And the same external IV produces consistent protected values
        And all the unprotected data should equal the original data
