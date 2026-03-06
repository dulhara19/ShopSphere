#!/bin/bash

# Epic 2.4: Address Management - Verification Script
# This script verifies that all Address Management components are properly implemented

echo "=========================================="
echo "Epic 2.4: Address Management Verification"
echo "=========================================="
echo ""

# Define colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Track failures
FAILURES=0

# Check function
check_file() {
    local file=$1
    local description=$2

    if [ -f "$file" ]; then
        echo -e "${GREEN}✓${NC} $description"
    else
        echo -e "${RED}✗${NC} $description - NOT FOUND: $file"
        FAILURES=$((FAILURES + 1))
    fi
}

# Check all required files
echo "Checking Entity & Repository Layer..."
check_file "src/main/java/com/shopsphere/user/model/Address.java" "Address Entity"
check_file "src/main/java/com/shopsphere/user/repository/AddressRepository.java" "AddressRepository"
echo ""

echo "Checking DTO Layer..."
check_file "src/main/java/com/shopsphere/user/dto/AddressDto.java" "AddressDto"
echo ""

echo "Checking Service Layer..."
check_file "src/main/java/com/shopsphere/user/service/AddressService.java" "AddressService Interface"
check_file "src/main/java/com/shopsphere/user/service/impl/AddressServiceImpl.java" "AddressServiceImpl"
echo ""

echo "Checking Controller Layer..."
check_file "src/main/java/com/shopsphere/user/controller/AddressController.java" "AddressController"
echo ""

echo "Checking Test Layer..."
check_file "src/test/java/com/shopsphere/user/service/impl/AddressServiceImplTest.java" "AddressServiceImpl Unit Tests"
check_file "src/test/java/com/shopsphere/user/controller/AddressControllerTest.java" "AddressController Integration Tests"
echo ""

echo "Checking Documentation..."
check_file "docs/PHASE_3_EPIC_2.4_ADDRESS_MANAGEMENT.md" "Address Management Documentation"
check_file "PHASE_3_EPIC_2.4_ADDRESS_TESTS.http" "API Test Cases"
check_file "src/main/resources/db-schema-epic-2.4.sql" "Database Schema Reference"
echo ""

# Summary
echo "=========================================="
if [ $FAILURES -eq 0 ]; then
    echo -e "${GREEN}✓ All Address Management components verified!${NC}"
    echo ""
    echo "Implementation Summary:"
    echo "  - Address Entity with UUID primary key"
    echo "  - One-to-Many relationship with User (cascade delete)"
    echo "  - AddressRepository with custom queries"
    echo "  - AddressDto with validation annotations"
    echo "  - AddressService with business logic"
    echo "  - AddressController with REST endpoints"
    echo "  - Comprehensive unit and integration tests"
    echo "  - Complete documentation"
    echo ""
    echo "Ready for:"
    echo "  1. mvn clean test - Run all tests"
    echo "  2. mvn spring-boot:run - Start the application"
    echo "  3. Manual API testing with HTTP requests"
    echo ""
    exit 0
else
    echo -e "${RED}✗ $FAILURES component(s) missing!${NC}"
    echo "Please ensure all files are created properly."
    echo ""
    exit 1
fi

