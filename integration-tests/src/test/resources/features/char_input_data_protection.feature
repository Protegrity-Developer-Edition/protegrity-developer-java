Feature: Successful Protection Operations For Char Input Data
    As a user of the Application Protector Java SDK
    I want to perform protect and unprotect operations on char input data
    So that I can ensure data security and integrity

    Background:
        Given the Application Protector Java SDK is initialized
        And the environment variables DEV_EDITION_EMAIL, DEV_EDITION_PASSWORD and DEV_EDITION_API_KEY are set
        And a policy is deployed with required data elements and user "superuser" is present

    @tokenization @single @char @positive
    Scenario: Tokenize and Detokenize single char input data
        When the user performs "tokenization" operations on single char input data using the following data elements
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

    @encryption @single @char @positive
    Scenario: Encrypt and Decrypt single char input data
        When the user performs "encryption" operations on single char input data using the following data elements
            | data_element | input_data                                                                                                                    |
            | text         | The Encryption data Element , Encrypts all alphabetic symbols (both lowercase and uppercase letters), as well as digits 5678. |
        Then all the data is validated
        And all the protected data should not equal the original data
        And all the unprotected data should equal the original data

    @encryption @bulk @char @positive
    Scenario Outline: Encyrpt and Decrypt bulk char input data
        When the user performs "encryption" operations on bulk char input data using the following data elements
            | data_element | input_data                                                                                                                    |
            | text         | The Encryption data Element, Encrypts all alphabetic symbols, (both lowercase and uppercase letters), as well as digits 5678. |
        Then all the bulk data is validated
        And all the protected data should not equal the original data
        And all the unprotected data should equal the original data

    @tokenization @bulk @char @positive
    Scenario: Tokenize and Detokenize bulk char input data
        When the user performs "tokenization" operations on bulk char input data using the following data elements
            | data_element | input_data                                                              |
            | name         | John Doe,Jane Smith,Alice Johnson,Bob Lee                               |
            | address      | 123 Main St Springfield,456 Elm St Shelbyville,789 Oak Ave Capital City |
            | city         | Springfield,Shelbyville,Capital City,Smalltown                          |
            | email        | johndoe@example.com,janesmith@domain.co.uk,alicej@web.de,boblee@free.fr |
            | ssn          | 123-45-6789,987-65-4321,111-22-3333,444-55-6666                         |
            | ccn          | 4111111111111111,5500000000000004,340000000000009,30000000000004        |
        Then all the bulk data is validated
        And all the protected data should not equal the original data
        And all the unprotected data should equal the original data

    @tokenization @single @eiv @char @positive
    Scenario: Tokenize and Detokenize single char input data with External Initialization Vector (EIV)
        When the user performs "tokenization" operations on single char input data with external IV using the following data elements
            | data_element | input_data                                                                                                                | external_iv       |
            | name         | John Doe                                                                                                                  | iv_name_001       |
            | address      | 123 Main St, Springfield                                                                                                  | iv_address_004    |
            | city         | Springfield                                                                                                               | iv_city_007       |
            | zipcode      | 90210                                                                                                                     | iv_zipcode_011    |
            | phone        | +1-202-555-0173                                                                                                           | iv_phone_012      |
            | email        | johndoe@example.com                                                                                                       | iv_email_013      |
            | int          | 12345                                                                                                                     | iv_int_018        |
            | ssn          | 123-45-6789                                                                                                               | iv_ssn_020        |
            | ccn          | 4111111111111111                                                                                                          | iv_ccn_021        |
            | ccn_bin      | 4111111122223333                                                                                                          | iv_ccn_bin_022    |
            | iban_cc      | 12345678901234567890                                                                                                      | iv_iban_cc_025    |
            | string       | The 'string' data element protects all alphabetic symbols (both lowercase and uppercase letters), as well as digits 1234. | iv_string_026     |
            | number       | 9876543210 11 234                                                                                                         | iv_number_027     |
            | name_de      | Max Mustermann                                                                                                            | iv_name_de_002    |
            | name_fr      | Jean Dupont                                                                                                               | iv_name_fr_003    |
            | address_de   | Musterstraße 12, München                                                                                                  | iv_address_de_005 |
            | address_fr   | 10 Rue de Rivoli, Paris                                                                                                   | iv_address_fr_006 |
            | city_de      | München                                                                                                                   | iv_city_de_008    |
            | city_fr      | Paris                                                                                                                     | iv_city_fr_009    |
        Then all the data is validated for eiv
        And the same external IV produces consistent protected values
        And all the unprotected data should equal the original data

    @tokenization @bulk @eiv @char @positive
    Scenario: Tokenize and Detokenize bulk char input data with External Initialization Vector (EIV)
        When the user performs "tokenization" operations on bulk char input data with external IV using the following data elements
            | data_element | input_data                                                                                                                                                                                                                                                                                               | external_iv       |
            | name         | John Doe, Jane Smith, Alice Johnson, Bob Lee, Élodie Durand, Diana Prince, Eve Adams, Frank Müller                                                                                                                                                                                                       | iv_name_001       |
            | address      | 123 Main St, Springfield, 456 Elm St, Shelbyville, 789 Oak Ave, Capital City, 101 Maple Rd, Smalltown, 202 Pine St, Bigcity, 303 Cedar Blvd, Midtown, 404 Birch Ln, Uptown, 505 Walnut Dr, Downtown                                                                                                      | iv_address_002    |
            | city         | Springfield, Shelbyville, Capital City, Smalltown, Bigcity, Midtown, Uptown, München                                                                                                                                                                                                                     | iv_city_003       |
            | zipcode      | 90210, 10001, 60601, 94105, 30301, 73301, 02108, 12345                                                                                                                                                                                                                                                   | iv_zipcode_005    |
            | phone        | +1-202-555-0173, +44-20-7946-0958, +49-30-123456, +33-1-23456789, +91-22-12345678, +81-3-1234-5678, +61-2-9876-5432, +34-91-1234567                                                                                                                                                                      | iv_phone_006      |
            | email        | johndoe@example.com, janesmith@domain.co.uk, alicej@web.de, boblee@free.fr, charlieb@company.com, dianap@service.org, evea@site.net, frankm@provider.eu                                                                                                                                                  | iv_email_007      |
            | int          | 12345, 67890, 23456, 78901, 34567, 89012, 45678, 90123                                                                                                                                                                                                                                                   | iv_int_012        |
            | ssn          | 123-45-6789, 987-65-4321, 111-22-3333, 444-55-6666, 777-88-9999, 000-11-2222, 333-44-5555, 666-77-8888                                                                                                                                                                                                   | iv_ssn_014        |
            | ccn          | 4111111111111111, 5500000000000004, 340000000000009, 30000000000004, 6011000000000004, 201400000000009, 3088000000000009, 3530111333300000                                                                                                                                                               | iv_ccn_015        |
            | ccn_bin      | 4111111122223333, 5500000022223333, 340000002222333, 30000000222233, 6011000022223333, 201400002222333, 308800002222333, 3530111322223333                                                                                                                                                                | iv_ccn_bin_016    |
            | iban_cc      | 12345678901234567890, 09876543210987654321, 11223344556677889900, 22334455667788990011, 33445566778899001122, 44556677889900112233, 55667788990011223344, 66778899001122334455                                                                                                                           | iv_iban_cc_019    |
            | string       | The 'string' data element protects all alphabetic symbols (both lowercase and uppercase letters), as well as digits 1234., Uppercase and lowercase letters., Special characters are ignored., Bulk input test string 1., Bulk input test string 2., Bulk input test string 3., Bulk input test string 4. | iv_string_020     |
            | number       | 9876543210 11 234, 1122 33445 5, 2233445566, 3344556677, 4455667788, 5566778899, 6677889900, 7788990011                                                                                                                                                                                                  | iv_number_021     |
            | name_de      | Max Mustermann, Anna Schmidt, Peter Müller, Julia Fischer, Lukas Weber, Sophie Becker, Tim Wagner, Laura Hoffmann                                                                                                                                                                                        | iv_name_de_002    |
            | name_fr      | Jean Dupont, Marie Curie, Émile Zola, Lucie Bernard, Paul Moreau, Sophie Laurent, Louis Petit, Julie Robert                                                                                                                                                                                              | iv_name_fr_003    |
            | address_de   | Musterstraße 12, München, Bahnhofstr. 5, Berlin, Hauptstr. 7, Hamburg, Marktplatz 1, Köln, Schulstr. 3, Frankfurt, Ringstr. 8, Stuttgart, Parkweg 2, Düsseldorf, Wiesenweg 4, Bremen                                                                                                                     | iv_address_de_005 |
            | address_fr   | 10 Rue de Rivoli, Paris, 20 Avenue Victor Hugo, Lyon, 5 Boulevard Saint-Michel, Marseille, 15 Rue de la Païx, Nice, 8 Place Bellecour, Toulouse, 12 Rue du Bac, Bordeaux, 3 Avenue Foch, Lille, 7 Rue Gambetta, Nantes                                                                                   | iv_address_fr_006 |
            | city_de      | München, Berlin, Hamburg, Köln, Frankfurt, Stuttgart, Düsseldorf, Bremen                                                                                                                                                                                                                                 | iv_city_de_008    |
            | city_fr      | Paris, Lyon, Marseille, Nice, Toulouse, Bordeaux, Lille, Nantes                                                                                                                                                                                                                                          | iv_city_fr_009    |
        Then all the data is validated for eiv
        And the same external IV produces consistent protected values
        And all the unprotected data should equal the original data
