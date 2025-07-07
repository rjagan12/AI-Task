#!/bin/bash

# AI-Powered LMS Quick Start Script
# This script sets up the complete LMS environment

set -e

echo "🚀 Starting AI-Powered LMS Setup..."

# Check prerequisites
echo "📋 Checking prerequisites..."

if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker first."
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17+ first."
    exit 1
fi

if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven first."
    exit 1
fi

if ! command -v node &> /dev/null; then
    echo "❌ Node.js is not installed. Please install Node.js 18+ first."
    exit 1
fi

echo "✅ All prerequisites are satisfied!"

# Build backend services
echo "🔨 Building backend services..."

cd lms-backend

echo "Building parent project..."
mvn clean install -DskipTests

echo "Building API Gateway..."
mvn clean package -pl api-gateway -DskipTests

echo "Building User Service..."
mvn clean package -pl user-service -DskipTests

echo "Building Course Service..."
mvn clean package -pl course-service -DskipTests

echo "Building Enrollment Service..."
mvn clean package -pl enrollment-service -DskipTests

echo "Building Assessment Service..."
mvn clean package -pl assessment-service -DskipTests

echo "Building Notification Service..."
mvn clean package -pl notification-service -DskipTests

cd ..

# Install frontend dependencies
echo "📦 Installing frontend dependencies..."

cd dashboard-platform

if [ ! -d "node_modules" ]; then
    echo "Installing npm dependencies..."
    npm install
else
    echo "Node modules already installed, skipping..."
fi

cd ..

# Start infrastructure services
echo "🐳 Starting infrastructure services..."

docker-compose up -d postgres redis rabbitmq

echo "⏳ Waiting for infrastructure services to be ready..."
sleep 30

# Start backend services
echo "🔧 Starting backend services..."

docker-compose up -d eureka
sleep 10

docker-compose up -d api-gateway user-service course-service enrollment-service assessment-service notification-service

echo "⏳ Waiting for backend services to be ready..."
sleep 60

# Start frontend
echo "🎨 Starting frontend dashboard..."

docker-compose up -d dashboard-platform

# Start monitoring
echo "📊 Starting monitoring services..."

docker-compose up -d prometheus grafana

echo "✅ Setup complete!"

echo ""
echo "🌐 Access URLs:"
echo "   Frontend Dashboard: http://localhost:3000"
echo "   API Gateway: http://localhost:8080"
echo "   Eureka Dashboard: http://localhost:8761"
echo "   RabbitMQ Management: http://localhost:15672 (guest/guest)"
echo "   Grafana: http://localhost:3001 (admin/admin)"
echo "   Prometheus: http://localhost:9090"
echo ""
echo "📚 Documentation:"
echo "   - README.md: Complete project documentation"
echo "   - backend-architecture.md: Backend architecture details"
echo "   - frontend-architecture.md: Frontend architecture details"
echo "   - database.md: Database schema documentation"
echo ""
echo "🔧 Useful Commands:"
echo "   View logs: docker-compose logs -f [service-name]"
echo "   Stop all: docker-compose down"
echo "   Restart service: docker-compose restart [service-name]"
echo "   Build and restart: ./quick-start.sh"
echo ""
echo "🎉 AI-Powered LMS is now running!" 