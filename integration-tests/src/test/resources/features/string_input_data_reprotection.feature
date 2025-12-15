Feature: Successful Reprotection Operations For String Input Data
    As a user of the Application Protector Java SDK
    I want to perform protect and reprotect operations on string input data
    So that I can ensure data can be reprotected with a different data element and values differ

    Background:
        Given the Application Protector Java SDK is initialized
        And the environment variables DEV_EDITION_EMAIL, DEV_EDITION_PASSWORD and DEV_EDITION_API_KEY are set
        And a policy is deployed with required data elements and user "superuser" is present

    @tokenization @single @string @reprotect @positive
    Scenario: Protect and Reprotect single string input data with tokenization
        When the user performs "protect" operation on single string input data "John Doe" using "name" data element
        Then the protected data should not equal the original data
        When the user performs "reprotect" operation on the protected data from "name" to "address" data element
        Then the reprotected data should not equal the original data
        And the reprotected data should not equal the protected data

    @tokenization @bulk @string @reprotect @positive
    Scenario: Protect and Reprotect bulk string input data with tokenization
        When the user performs "protect" operation on bulk string input data using the following configuration
            | old_data_element | input_data                                                              |
            | name             | John Doe,Jane Smith,Alice Johnson,Bob Lee                               |
            | address          | 123 Main St Springfield,456 Elm St Shelbyville,789 Oak Ave Capital City |
            | city             | Springfield,Shelbyville,Capital City,Smalltown                          |
            | ssn              | 123-45-6789,987-65-4321,111-22-3333,444-55-6666                         |
        Then all the protected data should not equal the original data
        When the user performs "reprotect" operation on the protected data using the following configuration
            | old_data_element | new_data_element |
            | name             | city         |
            | address          | passport             |
            | city             | name             |
            | ssn              | zipcode              |
        Then all the reprotected data should not equal the original data
        And all the reprotected data should not equal the protected data

    @encryption @single @string @reprotect @positive
    Scenario: Protect and Reprotect single string input data with encryption
        When the user performs "protect" operation on single string input data "The quick brown fox jumps over the lazy dog" using "text" data element with encryption
        Then the protected data should not equal the original data
        When the user performs "reprotect" operation on the encrypted data from "text" to "text" data element with different IV
        Then the reprotected data should not equal the original data
        And the reprotected data should not equal the protected data

    @tokenization @single @string @reprotect @positive
    Scenario Outline: Protect and Reprotect various data elements
        When the user performs "protect" operation on single string input data "<input_data>" using "<old_data_element>" data element
        Then the protected data should not equal the original data
        When the user performs "reprotect" operation on the protected data from "<old_data_element>" to "<new_data_element>" data element
        Then the reprotected data should not equal the original data
        And the reprotected data should not equal the protected data

        Examples:
            | old_data_element | new_data_element | input_data               |
            | name             | city          | John Doe                 |
            | address          | passport             | 123 Main St, Springfield |
            | city             | name             | Springfield              |
            | email            | email             | johndoe@example.com      |
            | ssn              | zipcode              | 123-45-6789              |
            | ccn              | ccn              | 4111111111111111         |
            | phone            | zipcode          | +1-202-555-0173          |
            | zipcode          | phone            | 90210                    |