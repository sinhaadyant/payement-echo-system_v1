#!/bin/bash
echo "=========================================="
echo "Payment Echo System - Setup Verification"
echo "=========================================="
echo ""

# Check MySQL
echo "1. Checking MySQL..."
if mysql -u root -proot -h 127.0.0.1 -e "SELECT 1" > /dev/null 2>&1; then
    echo "   ✓ MySQL is running"
else
    echo "   ✗ MySQL is not running"
    echo "   Start MySQL: docker-compose up -d mysql"
    echo ""
fi

# Check Application
echo ""
echo "2. Checking Application..."
if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "   ✓ Application is running"
    echo "   Health: $(curl -s http://localhost:8080/actuator/health | head -1)"
else
    echo "   ✗ Application is not running"
    echo "   Start application: ./gradlew bootRun"
    echo ""
fi

echo ""
echo "=========================================="
echo "Next Steps:"
echo "=========================================="
echo "1. Ensure MySQL is running"
echo "2. Ensure Application is running"
echo "3. Run: ./test_api.sh"
echo ""
