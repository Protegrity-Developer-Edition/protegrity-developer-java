
<div align="center">

# Protegrity Developer Edition Java
[![Version](https://img.shields.io/badge/version-1.0.0-green.svg?style=flat)](https://github.com/Protegrity-Developer-Edition/protegrity-developer-java/releases)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg?style=flat)](https://github.com/Protegrity-Developer-Edition/protegrity-developer-java/blob/main/LICENSE)
[![Java 11+](https://img.shields.io/badge/java-11+-blue.svg?style=flat)](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
[![Linux](https://img.shields.io/badge/Linux-FCC624?style=flat&logo=linux&logoColor=black)](https://www.linux.org/)
[![Windows](https://img.shields.io/badge/Windows-0078D6?style=flat&logo=windows&logoColor=white)](https://www.microsoft.com/windows/)
[![macOS](https://img.shields.io/badge/mac%20os-000000?style=flat&logo=macos&logoColor=F0F0F0)](https://www.apple.com/macos/)
[![Maven Central 1.1.0](https://img.shields.io/maven-central/v/com.protegrity/protegrity-developer-edition.svg?style=flat)](https://search.maven.org/artifact/com.protegrity/protegrity-developer-edition)
[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/Protegrity-Developer-Edition/protegrity-developer-java)

</div>

Welcome to the `protegrity-developer-java` repository, part of the Protegrity Developer Edition suite. This repository provides the Java SDK for integrating Protegrity's Data Discovery and Protection APIs into GenAI and traditional applications.
Customize, compile, and use the modules as per your requirement.

> **💡Note:** This SDK should be built and used only if you intend to modify the source code or default behavior.

## Table of Contents

1. [Overview](#overview)
    - [Why This Matters](#why-this-matters)
2. [Repository Structure](#repository-structure)
3. [Features](#features)
    - [Protegrity Developer Edition Module](#protegrity-developer-edition-module)
    - [Application Protector Java Module](#application-protector-java-module)
4. [Getting Started](#getting-started)
   - [Prerequisites](#prerequisites)
5. [Protegrity Developer Edition Module](#protegrity-developer-edition-module-1)
   - [Usage Examples](#usage-examples)
     - [Find and Redact](#find-and-redact)
     - [Find and Protect](#find-and-protect)
     - [Find and Unprotect](#find-and-unprotect)
6. [Application Protector Java Module](#application-protector-java-module-1)
   - [Usage Examples](#usage-examples-1)
     - [Protect & Unprotect Data](#protect--unprotect-data)
7. [Documentation](#documentation)
8. [Sample Use Case](#sample-use-case)
9. [License](#license)

### Overview

This repository contains two powerful modules designed to handle different aspects of data protection:

- **protegrity-developer-edition** - Focuses on data discovery, classification, and redaction of Personally Identifiable Information (PII) in unstructured text
- **application-protector-java** - Provides comprehensive data protection and unprotection capabilities for structured data

#### Why This Matters

Sensitive data shows up in more places than you'd expect — logs, payloads, prompts, training sets, and unstructured text. This Java SDK gives you tools to find and protect that data using tokenization, masking, and discovery — whether it's in an AI pipeline or a local application. No infrastructure, no UI, just code.

- **Developer-first experience:** Open APIs, sample apps, and modular design make it easy to embed data discovery and protection into any Java project.

- **Accelerate innovation:** Prototype and validate data discovery and protection strategies in a lightweight, containerized sandbox.

- **Enable responsible AI:** Protect sensitive information in training data, prompts, and outputs for GenAI and machine learning workflows.

- **Simplify compliance:** Meet regulatory requirements for data privacy with built-in detection and protection capabilities.

## Repository Structure

```text
├── LICENSE
├── README.md
├── pom.xml                                    # Parent POM
├── mvnw, mvnw.cmd                            # Maven wrapper
├── application-protector-java/               # Core SDK module
│   ├── pom.xml
│   ├── src/
│   │   ├── main/java/com/protegrity/ap/java/
│   │   └── test/
│   └── target/
│       └── ApplicationProtectorJava-1.0.0.jar
├── protegrity-developer-edition/            # Developer utilities module
│   ├── pom.xml
│   ├── src/
│   │   ├── main/java/com/protegrity/devedition/
│   │   └── test/
│   └── target/
│       └── ProtegrityDeveloperJava-1.0.0.jar
└── integration-tests/                        # Cucumber BDD tests
    ├── pom.xml
    ├── src/
    │   └── test/
    │       ├── java/com/protegrity/
    │       └── resources/features/
    └── README.md
```

## Features

### Protegrity Developer Edition Module

| Feature                | Description                                                                                   |
|------------------------|-----------------------------------------------------------------------------------------------|
| **Find and Redact**    | Classifies and redacts Personally Identifiable Information (PII) in unstructured text.        |
| **Find and Protect**   | Classifies and protects Personally Identifiable Information (PII) in unstructured text using Protegrity protection policies.        |
| **Find and Unprotect** | Restores original Personally Identifiable Information (PII) data from its protected form.     |
| **Cross-Platform Support** | Compatible with **Linux**, **Windows**, and **MacOS**.                                   |

### Application Protector Java Module

| Feature                | Description                                                                                   |
|------------------------|-----------------------------------------------------------------------------------------------|
| **Data Protection**    | Protects sensitive structured data using Protegrity policies.                                 |
| **Data Unprotection**  | Restores original data from its protected form.                                               |
| **Session Management** | Manages secure sessions for protection and unprotection operations.                           |
| **Cross-Platform Support** | Compatible with **Linux**, **Windows**, and **MacOS**.                                   |
| **Multiple Data Types** | Supports primitives, strings, bytes, dates, and more.                                        |

##  Getting Started

### Prerequisites

#### Common Prerequisites
- [Git](https://git-scm.com/downloads)
- [Java 11 or later](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
- [Maven 3.6+](https://maven.apache.org/download.cgi) or use the included Maven wrapper

### Protegrity Developer Edition Prerequisites

No additional prerequisites required beyond the common ones.

### Application Protector Java Prerequisites

- In addition to the common prerequisites, Application Protector Java requires **API Key, Email, and Password**

   **Obtaining Credentials**

    **Developer Edition Portal Registration**
    - Visit [https://www.protegrity.com/developers/get-api-credentials](https://www.protegrity.com/developers/get-api-credentials)
    - Register for a developer account.
    - You will receive an email with your API Key and Password.

### Build the protegrity-developer-java modules

1.  Clone the repository.
    ```bash
    git clone https://github.com/Protegrity-Developer-Edition/protegrity-developer-java.git
    ```
2.  Navigate to the `protegrity-developer-java` directory
3.  Build and install the modules by running the following command from the root directory of the repository.

    ```bash
    ./mvnw clean install
    ```
    The build completes and the success message is displayed. This creates:
    - `application-protector-java/target/ApplicationProtectorJava-1.0.0.jar` (fat JAR with dependencies)
    - `protegrity-developer-edition/target/ProtegrityDeveloperJava-1.0.0.jar` (fat JAR with dependencies)
    - Maven artifacts in your local repository (`.m2/repository`)

4.  To run integration tests (optional):
    ```bash
    ./mvnw clean verify -DskipITs=false
    ```

## Protegrity Developer Edition Module
> **💡Note:** Ensure that the Protegrity Developer Edition is running before using this module.
For setup instructions, please refer to the documentation [here](https://github.com/Protegrity-Developer-Edition/protegrity-developer-edition/blob/main/README.md).

### Usage Examples
#### Find and Redact

```java
package com.protegrity.devedition.samples;

import com.protegrity.devedition.utils.Discover;
import com.protegrity.devedition.utils.PiiProcessing;
import com.protegrity.devedition.utils.Config;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class FindAndRedactExample {
    public static void main(String[] args) throws Exception {
        // Configure
        Map<String, String> entityMap = new HashMap<>();
        entityMap.put("PERSON", "NAME");
        entityMap.put("SOCIAL_SECURITY_ID", "SSN");
        Config.setNamedEntityMap(entityMap);
        Config.setMaskingChar("#");
        Config.setMethod("redact");
        
        // Discover and redact
        String inputText = "John Doe's SSN is 123-45-6789.";
        JsonNode discoveryResult = Discover.discover(inputText);
        List<PiiProcessing.EntitySpan> entities = PiiProcessing.collectEntitySpans(discoveryResult, inputText);
        String outputText = PiiProcessing.redactData(entities, inputText);
        
        System.out.println("Redacted: " + outputText);
    }
}
```

Set your credentials as environment variables for running Protection examples:

```bash
export DEV_EDITION_EMAIL='<email_used_for_registration>'
export DEV_EDITION_PASSWORD='<Password_provided_in_email>'
export DEV_EDITION_API_KEY='<API_key_provided_in_email>'
```

#### Find and Protect

```java
package com.protegrity.devedition.samples;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.protegrity.devedition.utils.Config;
import com.protegrity.devedition.utils.Discover;
import com.protegrity.devedition.utils.PiiProcessing;

public class FindAndProtectExample {
    public static void main(String[] args) throws Exception {
        
        // Configure
        Map<String, String> entityMap = new HashMap<>();
        entityMap.put("PERSON", "NAME");
        entityMap.put("SOCIAL_SECURITY_ID", "SSN");
        Config.setNamedEntityMap(entityMap);

        // Discover PII
        String inputText = "John Doe's SSN is 123-45-6789.";
        JsonNode discoveryResult = Discover.discover(inputText);
        List<PiiProcessing.EntitySpan> entities = PiiProcessing.collectEntitySpans(discoveryResult, inputText);
        
        // Protect data
        String outputText = PiiProcessing.protectData(entities, inputText);
        
        System.out.println("Protected: " + outputText);
    }
}
```

#### Find and Unprotect

```java
package com.protegrity.devedition.samples;

import com.protegrity.devedition.utils.PiiProcessing;

public class FindAndUnprotectExample {
    public static void main(String[] args) throws Exception {
        
        // Protected text from FindAndProtectExample
        String protectedText = "[PERSON]7ro8 lfU'I[/PERSON] SSN is [SOCIAL_SECURITY_ID]616-16-2210[/SOCIAL_SECURITY_ID].";
        
        // Unprotect
        String outputText = PiiProcessing.unprotectData(protectedText);
        
        System.out.println("Unprotected: " + outputText);
    }
}
```

## Application Protector Java Module
### Usage Examples

Set your credentials as environment variables:

```bash
export DEV_EDITION_EMAIL='<email_used_for_registration>'
export DEV_EDITION_PASSWORD='<Password_provided_in_email>'
export DEV_EDITION_API_KEY='<API_key_provided_in_email>'
```

#### Protect & Unprotect Data

```java
package com.protegrity.devedition.samples;

import com.protegrity.ap.java.*;
import java.util.Arrays;

public class BulkProtectExample {
    public static void main(String[] args) {
        try {
            Protector protector = Protector.getProtector();
            String userName = "superuser";
            String dataElement = "ccn";
            String[] data = {"5555555555554444", "378282246310005", "4111111111111111"};
            
            SessionObject session = protector.createSession(userName);
            System.out.println("Original Data: " + Arrays.toString(data));

            // Protect bulk
            String[] protectedData = new String[data.length];
            protector.protect(session, dataElement, data, protectedData);
            System.out.println("Protected Data: " + Arrays.toString(protectedData));
            
            // Unprotect bulk
            String[] unprotectedData = new String[data.length];
            protector.unprotect(session, dataElement, protectedData, unprotectedData);
            System.out.println("Unprotected Data: " + Arrays.toString(unprotectedData));
            
        } catch (ProtectorException e) {
            e.printStackTrace();
        }
    }
}
```

> **💡Note:** You **do not** need Protegrity Developer Edition running before executing just the Application Protector Java Module.

## Documentation

- [Protegrity Developer Edition documentation](http://developer.docs.protegrity.com/)
- For API reference and tutorials, visit [Developer Portal](https://www.protegrity.com/developers)
- For more information about Data Discovery, refer to the [Data Discovery documentation]( https://docs.protegrity.com/data-discovery/1.1.1/docs/).
- For more information about Semantic Guardrails, refer to the [Semantic Guardrails documentation]( https://docs.protegrity.com/sem_guardrail/1.1.0/docs/).
- For more information about Application Protector Java, refer to the [Application Protector Java documentation]( https://docs.protegrity.com/10.0/protectors/application_protector/ap_java/).

## Sample Use Case

Use this SDK to build GenAI applications like chatbots that:
- Detect Personally Identifiable Information (PII) in prompts using the classifier
- Protect, Redact, or Mask sensitive data before processing
- Protect and Unprotect structured sensitive data

## License

See [LICENSE](https://github.com/Protegrity-Developer-Edition/protegrity-developer-java/blob/main/LICENSE) for terms and conditions.

