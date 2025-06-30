#!/bin/bash

echo "Running Strategy Pattern Payment Processing Demo..."
echo "=================================================="

# Compile the project
echo "Compiling project..."
mvn clean compile

# Run the demo
echo "Running demo..."
mvn exec:java -Dexec.mainClass="com.payment.client.PaymentDemo"

echo ""
echo "Demo completed!" 