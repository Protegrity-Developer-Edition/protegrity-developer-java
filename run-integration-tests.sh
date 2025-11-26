#!/bin/bash

# Integration Tests Quick Start Script
# This script helps set up and run integration tests for protegrity-developer-java

set -e

echo "=========================================="
echo "Protegrity Developer Java Integration Tests"
echo "=========================================="
echo ""

# Check if environment variables are set
echo "Checking environment variables..."
if [ -z "$DEV_EDITION_EMAIL" ]; then
    echo "❌ DEV_EDITION_EMAIL is not set"
    echo "   Please set it with: export DEV_EDITION_EMAIL='your-email@example.com'"
    exit 1
fi

if [ -z "$DEV_EDITION_PASSWORD" ]; then
    echo "❌ DEV_EDITION_PASSWORD is not set"
    echo "   Please set it with: export DEV_EDITION_PASSWORD='your-password'"
    exit 1
fi

if [ -z "$DEV_EDITION_API_KEY" ]; then
    echo "❌ DEV_EDITION_API_KEY is not set"
    echo "   Please set it with: export DEV_EDITION_API_KEY='your-api-key'"
    exit 1
fi

echo "✅ All environment variables are set"
echo ""

# Determine what to run based on arguments
if [ "$1" == "all" ]; then
    echo "Running all integration tests..."
    mvn clean verify
elif [ "$1" == "positive" ]; then
    echo "Running positive tests only..."
    mvn verify -Dcucumber.filter.tags="@positive"
elif [ "$1" == "negative" ]; then
    echo "Running negative tests only..."
    mvn verify -Dcucumber.filter.tags="@negative"
elif [ "$1" == "string" ]; then
    echo "Running string protection tests..."
    mvn verify -Dcucumber.filter.tags="@string"
elif [ "$1" == "integer" ]; then
    echo "Running integer protection tests..."
    mvn verify -Dcucumber.filter.tags="@integer"
    mvn verify -Dcucumber.filter.tags="@integer"
elif [ "$1" == "bytes" ]; then
    echo "Running byte protection tests..."
    mvn verify -Dcucumber.filter.tags="@bytes"
elif [ "$1" == "single" ]; then
    echo "Running single data tests..."
    mvn verify -Dcucumber.filter.tags="@single"
elif [ "$1" == "bulk" ]; then
    echo "Running bulk data tests..."
    mvn verify -Dcucumber.filter.tags="@bulk"
elif [ "$1" == "eiv" ]; then
    echo "Running External IV tests..."
    mvn verify -Dcucumber.filter.tags="@eiv"
elif [ "$1" == "report" ]; then
    echo "Opening test report..."
    if [ -f "target/cucumber-reports/cucumber.html" ]; then
        if command -v xdg-open > /dev/null; then
            xdg-open target/cucumber-reports/cucumber.html
        elif command -v open > /dev/null; then
            open target/cucumber-reports/cucumber.html
        else
            echo "Report location: target/cucumber-reports/cucumber.html"
        fi
    else
        echo "❌ No test report found. Please run tests first."
        exit 1
    fi
elif [ "$1" == "help" ] || [ "$1" == "-h" ] || [ "$1" == "--help" ]; then
    echo "Usage: ./run-integration-tests.sh [option]"
    echo ""
    echo "Options:"
    echo "  all       Run all integration tests (default)"
    echo "  positive  Run only positive test scenarios"
    echo "  negative  Run only negative test scenarios"
    echo "  string    Run string protection tests"
    echo "  integer   Run integer protection tests"
    echo "  bytes     Run byte protection tests"
    echo "  single    Run single data item tests"
    echo "  bulk      Run bulk data array tests"
    echo "  eiv       Run External IV tests"
    echo "  report    Open the test report in browser"
    echo "  help      Show this help message"
    echo ""
    echo "Examples:"
    echo "  ./run-integration-tests.sh all"
    echo "  ./run-integration-tests.sh positive"
    echo "  ./run-integration-tests.sh string"
    echo ""
    exit 0
else
    echo "Running all integration tests (default)..."
    mvn clean test
fi

echo ""
echo "=========================================="
echo "Test execution completed!"
echo "=========================================="
echo ""
echo "View reports at:"
echo "  HTML: target/cucumber-reports/cucumber.html"
echo "  JSON: target/cucumber-reports/cucumber.json"
echo "  XML:  target/cucumber-reports/cucumber.xml"
echo ""
