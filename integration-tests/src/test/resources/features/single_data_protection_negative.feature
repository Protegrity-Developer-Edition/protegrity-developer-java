Feature: Unsuccessful Protection Operations For Various Combinations of User, Operation, Data Element and Single Input Data
    As a user of the Application Protector Java SDK
    I want to verify protection operations fail for invalid, unauthorized and other negative cases
    So that I can ensure errors are handled correctly and data security is not compromised

    Background:
        Given the Application Protector Java SDK is initialized
        And the environment variables DEV_EDITION_EMAIL, DEV_EDITION_PASSWORD and DEV_EDITION_API_KEY are set
        And a policy is deployed with required data elements, users and permissions required to test the scenario

    @tokenization @single @string @negative
    Scenario Outline: Validate error message on single string input data access with user having no appropriate permissions
        When the user "<user>" performs "<operation>" operation using "tokenization" method on single string input data "<input_data>" using "<data_element>" data element
        Then the operation result should be "<result>" with error message "<error_message>" and output as "<output>"
        Examples:
            | operation | user           | data_element | input_data               | result    | output           | error_message                                                                             |
            | protect   | finance        | name         | John Doe                 | exception | null             | 3, The user does not have the appropriate permissions to perform the requested operation. |
            | protect   | marketing      | name         | John Doe                 | exception | null             | 3, The user does not have the appropriate permissions to perform the requested operation. |
            | protect   | hr             | name         | John Doe                 | exception | null             | 3, The user does not have the appropriate permissions to perform the requested operation. |
            | protect   | finance        | ccn          | 4111111122223333         | exception | null             | 3, The user does not have the appropriate permissions to perform the requested operation. |
            | unprotect | admin          | name         | John Doe                 | success   | John Doe         | null                                                                                      |
            | unprotect | admin          | ccn          | 4111111122223333         | success   | 4111111122223333 | null                                                                                      |
            | unprotect | devops         | name         | John Doe                 | success   | John Doe         | null                                                                                      |
            | unprotect | paloma.torres  | datetime     | 2023-09-04T14:30:00      | success   | null             | null                                                                                      |
            | unprotect | merlin.ishida  | address      | 123 Main St, Springfield | success   | null             | null                                                                                      |
            | unprotect | merlin.ishida  | ccn          | 4111111122223333         | success   | null             | null                                                                                      |

    @tokenization @single @string @negative
    Scenario Outline: Validate error message on performing protect/unprotect with user not present in policy
        When the user "<user>" performs "<operation>" operation using "tokenization" method on single string input data "<input_data>" using "<data_element>" data element
        Then the operation result should be "<result>" with error message "<error_message>" and output as "<output>"
        Examples:
            | operation | user      | data_element | input_data  | result    | output | error_message                                                                             |
            | protect   | dummyUser | name         | John Doe    | exception | null   | 3, The user does not have the appropriate permissions to perform the requested operation. |
            | unprotect | dummyUser | int          | 12345       | success   | null   | null                                                                                      |

    @tokenization @single @string @negative
    Scenario Outline: Validate error message on performing protect/unprotect with data element not present in policy
        When the user "<user>" performs "<operation>" operation using "tokenization" method on single string input data "<input_data>" using "<data_element>" data element
        Then the operation result should be "<result>" with error message "<error_message>" and output as "<output>"
        Examples:
            | operation | user      | data_element | input_data  | result    | output | error_message                                         |
            | protect   | superuser | dummyDE      | John Doe    | exception | null   | 2, The data element could not be found in the policy. |
            | unprotect | superuser | dummyDE      | 12345       | exception | null   | 2, The data element could not be found in the policy. |

    @tokenization @single @string @negative
    Scenario Outline: Validate error message on performing protect/unprotect on passing invalid single string input data
        When the user "<user>" performs "<operation>" operation using "tokenization" method on single string input data "<input_data>" using "<data_element>" data element
        Then the operation result should be "<result>" with error message "<error_message>" and output as "<output>"
        Examples:
            | operation | user      | data_element | input_data                  | result    | output | error_message                                   |
            | protect   | superuser | email        | John Doe                    | exception | null   | 44, The content of the input data is not valid. |
            | unprotect | superuser | email        | John Doe                    | exception | null   | 44, The content of the input data is not valid. |
            | protect   | superuser | ccn          | aA1 bB2 cC3 dD4 eE5 fF6 gG7 | exception | null   | 44, The content of the input data is not valid. |
            | protect   | superuser | int          | abcdef                      | exception | null   | 44, The content of the input data is not valid. |
            | unprotect | superuser | int          | abcdef                      | exception | null   | 44, The content of the input data is not valid. |
