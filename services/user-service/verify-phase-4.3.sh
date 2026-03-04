#!/bin/bash

# Phase 4.3: Event Publishing - Verification Script
# Run this script to verify Phase 4.3 implementation

echo "=========================================="
echo "Phase 4.3 Event Publishing - Verification"
echo "=========================================="
echo ""

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check 1: Maven dependency
echo "✓ Checking spring-boot-starter-amqp dependency..."
if grep -q "spring-boot-starter-amqp" "services/user-service/pom.xml"; then
    echo -e "${GREEN}✓ AMQP dependency found in pom.xml${NC}"
else
    echo -e "${RED}✗ AMQP dependency NOT found in pom.xml${NC}"
fi
echo ""

# Check 2: RabbitMQConfig.java
echo "✓ Checking RabbitMQConfig.java..."
if [ -f "services/user-service/src/main/java/com/shopsphere/user/config/RabbitMQConfig.java" ]; then
    echo -e "${GREEN}✓ RabbitMQConfig.java exists${NC}"

    # Check for key beans
    if grep -q "public TopicExchange userExchange()" "services/user-service/src/main/java/com/shopsphere/user/config/RabbitMQConfig.java"; then
        echo -e "${GREEN}  ✓ userExchange() bean defined${NC}"
    fi

    if grep -q "public Queue userCreatedQueue()" "services/user-service/src/main/java/com/shopsphere/user/config/RabbitMQConfig.java"; then
        echo -e "${GREEN}  ✓ userCreatedQueue() bean defined${NC}"
    fi

    if grep -q "Jackson2JsonMessageConverter" "services/user-service/src/main/java/com/shopsphere/user/config/RabbitMQConfig.java"; then
        echo -e "${GREEN}  ✓ Jackson2JsonMessageConverter configured${NC}"
    fi
else
    echo -e "${RED}✗ RabbitMQConfig.java NOT found${NC}"
fi
echo ""

# Check 3: UserEventPublisher.java
echo "✓ Checking UserEventPublisher.java..."
if [ -f "services/user-service/src/main/java/com/shopsphere/user/service/UserEventPublisher.java" ]; then
    echo -e "${GREEN}✓ UserEventPublisher.java exists${NC}"

    if grep -q "public void publishUserCreatedEvent(UserInternalDto userDto)" "services/user-service/src/main/java/com/shopsphere/user/service/UserEventPublisher.java"; then
        echo -e "${GREEN}  ✓ publishUserCreatedEvent() method defined${NC}"
    fi

    if grep -q "public void publishUserUpdatedEvent(UserInternalDto userDto)" "services/user-service/src/main/java/com/shopsphere/user/service/UserEventPublisher.java"; then
        echo -e "${GREEN}  ✓ publishUserUpdatedEvent() method defined${NC}"
    fi

    if grep -q "public void publishUserDeletedEvent(UserInternalDto userDto)" "services/user-service/src/main/java/com/shopsphere/user/service/UserEventPublisher.java"; then
        echo -e "${GREEN}  ✓ publishUserDeletedEvent() method defined${NC}"
    fi
else
    echo -e "${RED}✗ UserEventPublisher.java NOT found${NC}"
fi
echo ""

# Check 4: AuthService.java integration
echo "✓ Checking AuthService.java integration..."
if grep -q "UserEventPublisher" "services/user-service/src/main/java/com/shopsphere/user/service/AuthService.java"; then
    echo -e "${GREEN}✓ UserEventPublisher injected in AuthService${NC}"

    if grep -q "publishUserCreatedEvent(userDto)" "services/user-service/src/main/java/com/shopsphere/user/service/AuthService.java"; then
        echo -e "${GREEN}  ✓ publishUserCreatedEvent() called in registerUser()${NC}"
    fi
else
    echo -e "${RED}✗ UserEventPublisher NOT integrated in AuthService${NC}"
fi
echo ""

# Check 5: RabbitMQ running
echo "✓ Checking RabbitMQ status..."
if docker ps | grep -q rabbitmq; then
    echo -e "${GREEN}✓ RabbitMQ container is running${NC}"
else
    echo -e "${YELLOW}⚠ RabbitMQ container is NOT running${NC}"
    echo "  Start with: docker-compose up -d rabbitmq"
fi
echo ""

# Check 6: RabbitMQ API accessibility
echo "✓ Checking RabbitMQ Management API..."
if curl -s -u guest:guest http://localhost:15672/api/overview > /dev/null 2>&1; then
    echo -e "${GREEN}✓ RabbitMQ Management API is accessible${NC}"
else
    echo -e "${YELLOW}⚠ RabbitMQ Management API is NOT accessible${NC}"
    echo "  Check if RabbitMQ is running and accessible at http://localhost:15672"
fi
echo ""

# Summary
echo "=========================================="
echo "Verification Complete!"
echo "=========================================="
echo ""
echo "Next Steps:"
echo "1. Run: mvn clean install -DskipTests (in services/user-service)"
echo "2. Start RabbitMQ: docker-compose up -d rabbitmq"
echo "3. Start User Service: mvn spring-boot:run (in services/user-service)"
echo "4. Test registration and verify events in RabbitMQ UI"
echo ""
echo "RabbitMQ Management UI: http://localhost:15672"
echo "Username: guest | Password: guest"

