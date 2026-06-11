
<div align="center">

# Protegrity AI Developer Edition Java
[![Version](https://img.shields.io/badge/version-1.1.0-green.svg?style=flat)](https://github.com/Protegrity-Developer-Edition/protegrity-ai-developer-java/releases)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg?style=flat)](https://github.com/Protegrity-Developer-Edition/protegrity-ai-developer-java/blob/main/LICENSE)
[![Java 11+](https://img.shields.io/badge/java-11+-blue.svg?style=flat)](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
[![Linux](https://img.shields.io/badge/Linux-FCC624?style=flat&logo=linux&logoColor=black)](https://www.linux.org/)
[![Windows](https://img.shields.io/badge/Windows-0078D6?style=flat&logo=windows&logoColor=white)](https://www.microsoft.com/windows/)
[![macOS](https://img.shields.io/badge/mac%20os-000000?style=flat&logo=macos&logoColor=F0F0F0)](https://www.apple.com/macos/)
[![Maven Central 1.1.0](https://img.shields.io/maven-central/v/com.protegrity/protegrity-ai-developer-edition.svg?style=flat)](https://search.maven.org/artifact/com.protegrity/protegrity-ai-developer-edition)
[![Service Health](https://img.shields.io/badge/service-health-brightgreen.svg?style=flat&logo=statuspage&logoColor=white)](https://www.protegrity.com/developers/status)
[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/Protegrity-Developer-Edition/protegrity-ai-developer-java)

</div>

Welcome to the `protegrity-ai-developer-java` repository, part of the Protegrity AI Developer Edition suite. This repository provides the Java SDK for integrating Protegrity's Data Discovery and Protection APIs into GenAI and traditional applications.
Customize, compile, and use the modules as per your requirement.

> **💡Note:** This SDK should be built and used only if you intend to modify the source code or default behavior.

## Table of Contents

1. [Overview](#overview)
    - [Why This Matters](#why-this-matters)
2. [Repository Structure](#repository-structure)
3. [Features](#features)
    - [Protegrity AI Developer Edition Module](#protegrity-ai-developer-edition-module)
    - [Application Protector Java Module](#application-protector-java-module)
4. [Getting Started](#getting-started)
   - [Prerequisites](#prerequisites)
5. [Protegrity AI Developer Edition Module](#protegrity-ai-developer-edition-module-1)
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

- **protegrity-ai-developer-edition** - Focuses on data discovery, classification, and redaction of Personally Identifiable Information (PII) in unstructured text.
- **application-protector-java** - Provides comprehensive data protection and unprotection capabilities for structured data.

#### Why This Matters

Sensitive data shows up in more places than expected, such as logs, payloads, prompts, training sets, and unstructured text. This Java SDK provides tools to find and protect that data using tokenization, masking, and discovery. The data might be in an AI pipeline or a local application. No infrastructure, no UI, just code.

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
│       └── ApplicationProtectorJava-1.0.2.jar
├── protegrity-ai-developer-edition/            # Developer utilities module
│   ├── pom.xml
│   ├── src/
│   │   ├── main/java/com/protegrity/devedition/
│   │   └── test/
└── integration-tests/                        # Cucumber BDD tests
    ├── pom.xml
    ├── src/
    │   └── test/
    │       ├── java/com/protegrity/
    │       └── resources/features/
    └── README.md
```

## Features

### Protegrity AI Developer Edition Module

| Feature                | Description                                                                                   |
|------------------------|-----------------------------------------------------------------------------------------------|
| **Find and Redact**    | Classifies and redacts PII in unstructured text.        |
| **Find and Protect**   | Classifies and protects PII in unstructured text using Protegrity protection policies.        |
| **Find and Unprotect** | Restores original PII data from its protected form.     |
| **Cross-Platform Support** | Compatible with **Linux**, **MacOS**, and **Windows**.                                   |

### Application Protector Java Module

| Feature                | Description                                                                                   |
|------------------------|-----------------------------------------------------------------------------------------------|
| **Data Protection**    | Protects sensitive structured data using Protegrity policies.                                 |
| **Data Unprotection**  | Restores original data from its protected form.                                               |
| **Session Management** | Manages secure sessions for protection and unprotection operations.                           |
| **Cross-Platform Support** | Compatible with **Linux**, **MacOS**, and **Windows**.                                   |
| **Multiple Data Types** | Supports primitives, strings, bytes, dates, and more.                                        |

## Getting Started

### Prerequisites

#### Common Prerequisites
- [Git](https://git-scm.com/downloads)
- [Java 11 or later](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
- [Maven 3.6+](https://maven.apache.org/download.cgi) or use the included Maven wrapper

### Protegrity AI Developer Edition Prerequisites

No additional prerequisites required beyond the common ones.

### Application Protector Java Prerequisites

- In addition to the common prerequisites, Application Protector Java requires **API Key**, **Email**, and **Password**

   **Obtaining Credentials**

    **AI Developer Edition Portal Registration**
    - Visit [https://www.protegrity.com/developers/dev-edition-api](https://www.protegrity.com/developers/dev-edition-api).
    - Register for a developer account.
    - Open your inbox to view the email with your API key and password.

### Build the protegrity-ai-developer-java modules

1.  Clone the repository.

    ```bash
    git clone https://github.com/Protegrity-Developer-Edition/protegrity-ai-developer-java.git
    ```
2.  Navigate to the `protegrity-ai-developer-java` directory
3.  Build and install the modules by running the following command from the root directory of the repository.

    ```bash
    ./mvnw clean install
    ```
    The build completes and the success message is displayed. 
    The following artifacts are created:
    - `application-protector-java/target/ApplicationProtectorJava-1.0.2.jar` (fat JAR with dependencies)
    - `protegrity-ai-developer-edition/target/ProtegrityAiDeveloperJava-1.0.2.jar` (fat JAR with dependencies)
    - Maven artifacts in your local repository (`.m2/repository`)

4.  To run integration tests (optional):

    ```bash
    ./mvnw clean verify -DskipITs=false
    ```

## Protegrity AI Developer Edition Module
> **💡Note:** Ensure that the Protegrity AI Developer Edition is set up and running before using this module.
For setup instructions, refer to the [Protegrity AI Developer Edition readme](https://github.com/Protegrity-Developer-Edition/protegrity-ai-developer-edition/blob/main/README.md) or the [Protegrity AI Developer Edition documentation](https://developer.docs.protegrity.com/).

### Usage Examples

The following examples demonstrate common workflows using the Protegrity AI Developer Edition Java module. Each example shows how to configure the module, invoke Data Discovery, and apply a processing method such as redaction or tokenization to sensitive data found in plain text inputs.

#### Find and Redact

This example scans input text for sensitive entities such as names and social security numbers using Data Discovery. It then replaces the detected values with a configurable masking character to produce a redacted output.

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

Set the credentials as environment variables for running Protection examples:

```bash
export DEV_EDITION_EMAIL='<email_used_for_registration>'
export DEV_EDITION_PASSWORD='<Password_provided_in_email>'
export DEV_EDITION_API_KEY='<API_key_provided_in_email>'
```

#### Find and Protect

This example uses Data Discovery to identify sensitive entities in the input text. It then applies tokenization using the Protegrity API Service to replace each detected value with a format-preserving token that can be reversed with the appropriate permissions.

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

This example takes previously tokenized text, where sensitive values have been replaced with format-preserving tokens enclosed in entity tags, and reverses the protection to restore the original plaintext values. It is subject to the caller's role-based permissions.

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

The Application Protector Java module provides direct access to Protegrity's tokenization and detokenization capabilities. Use this module when you already know which fields contain sensitive data and want to apply protection or reverse it programmatically through the API Service.

### Usage Examples

Set the credentials as environment variables:

```bash
export DEV_EDITION_EMAIL='<email_used_for_registration>'
export DEV_EDITION_PASSWORD='<Password_provided_in_email>'
export DEV_EDITION_API_KEY='<API_key_provided_in_email>'
```

#### Protect & Unprotect Data

This example demonstrates how to tokenize and detokenize an array of sensitive values, such as credit card numbers using the Application Protector API. It connects to the API Service, applies protection using a specified data element and user role, then reverses the operation to retrieve the original values.

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

> **💡Note:** You **do not** need Protegrity AI Developer Edition running before executing just the Application Protector Java Module.

### Connecting to Team Edition (Cloud Protect)

The same `Protector` code works with your own Cloud Protect deployment, just with the endpoint and auth mode change. Settings are resolved in the order **environment variable → `~/.protegrity/config.yaml` → built-in default** (override the file path with `PTY_CONFIG_FILE`).

```bash
export PTY_CP_HOST=https://<your-cloud-protect-host>/pty
export PTY_AUTH_MODE=bearer_token            # or cognito, aws_iam, mtls, none
export PTY_STATIC_TOKEN=<your-jwt>           # for bearer_token mode (or fetch via OAuth2: PTY_TOKEN_ENDPOINT + PTY_CLIENT_ID + PTY_CLIENT_SECRET)
```

Or, equivalently, in `~/.protegrity/config.yaml`:

```yaml
protect_host: https://cp.example.com/pty
auth_mode: bearer_token
request_timeout: 30
max_retries: 3
# static_token: eyJhbGciOi...   # see security note below
```

> **🔒 Secret keys (`static_token`, `client_secret`) are read from the YAML file only when the file is `chmod 600`** (owner-only - the same rule SSH and `~/.pgpass` enforce). Loose permissions cause the secret keys to be dropped at load time with an slf4j WARN; non-secret keys still load normally. The POSIX permission check is skipped on Windows.

## Documentation

- [Protegrity AI Developer Edition documentation](http://developer.docs.protegrity.com/)
- For API reference and tutorials, visit [Developer Portal](https://www.protegrity.com/developers)
- For more information about Data Discovery, refer to the [Data Discovery documentation](https://docs.protegrity.com/data-discovery/2.0.0/docs/).
- For more information about Semantic Guardrails, refer to the [Semantic Guardrails documentation](https://docs.protegrity.com/sem_guardrail/1.1.1/docs/).
- For more information about Application Protector Java, refer to the [Application Protector Java documentation](https://docs.protegrity.com/protectors/10.0/docs/ap/ap_java/).

## Sample Use Case

Use this SDK to build GenAI applications like chatbots that:
- Detect Personally Identifiable Information (PII) in prompts using the classifier
- Protect, Redact, or Mask sensitive data before processing
- Protect and Unprotect structured sensitive data

## License

See [LICENSE](https://github.com/Protegrity-Developer-Edition/protegrity-ai-developer-java/blob/main/LICENSE) for terms and conditions.

