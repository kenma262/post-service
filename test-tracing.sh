#!/bin/bash

echo "🚀 Starting Distributed Tracing Demo"
echo "======================================"

# Start Zipkin
echo "1. Starting Zipkin..."
./start-zipkin.sh

echo ""
echo "2. Waiting for services to be ready..."
sleep 5

echo ""
echo "3. Testing distributed tracing..."

# Test auth-service
echo "📡 Testing auth-service..."
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User",
    "dateOfBirth": "1990-01-01"
  }'

echo ""
echo ""

# Test post-service
echo "📡 Testing post-service (should see same trace ID propagated)..."
curl -X GET http://localhost:8082/api/posts

echo ""
echo ""
echo "✅ Distributed tracing test completed!"
echo ""
echo "🌐 View traces in Zipkin UI: http://localhost:9411"
echo "📊 Check application logs to see trace IDs in action"
