#!/bin/bash

# MongoDB Startup Script
# This script starts MongoDB using Docker with persistent data storage

set -e

echo "Starting MongoDB for Post Service..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "Error: Docker is not running. Please start Docker first."
    exit 1
fi

# Stop and remove existing MongoDB container if it exists
echo "Cleaning up any existing MongoDB container..."
docker stop mongodb 2>/dev/null || true
docker rm mongodb 2>/dev/null || true

# Create a Docker network if it doesn't exist
echo "Creating Docker network..."
docker network create post-service-network 2>/dev/null || true

# Create a volume for MongoDB data persistence
echo "Creating MongoDB data volume..."
docker volume create mongodb-data 2>/dev/null || true

# Start MongoDB container
echo "Starting MongoDB container..."
docker run -d \
  --name mongodb \
  --network post-service-network \
  -p 27017:27017 \
  -e MONGO_INITDB_ROOT_USERNAME=admin \
  -e MONGO_INITDB_ROOT_PASSWORD=admin123 \
  -e MONGO_INITDB_DATABASE=postservice \
  -v mongodb-data:/data/db \
  -v mongodb-config:/data/configdb \
  mongo:7.0

# Wait for MongoDB to be ready
echo "Waiting for MongoDB to be ready..."
sleep 10

# Check if MongoDB is running
if docker ps | grep -q mongodb; then
    echo "✅ MongoDB started successfully!"
    echo "📊 MongoDB is running on: mongodb://localhost:27017"
    echo "🔐 Admin credentials: admin/admin123"
    echo "🗄️  Database: postservice"
    echo "🐳 Container name: mongodb"
    echo "📁 Data persisted in Docker volume: mongodb-data"
    echo ""
    echo "Connection URLs:"
    echo "  - Application: mongodb://admin:admin123@localhost:27017/postservice?authSource=admin"
    echo "  - MongoDB Compass: mongodb://admin:admin123@localhost:27017/postservice?authSource=admin"
    echo ""
    echo "To stop MongoDB: docker stop mongodb"
    echo "To view logs: docker logs mongodb"
    echo "To access MongoDB shell: docker exec -it mongodb mongosh -u admin -p admin123"
else
    echo "❌ Failed to start MongoDB"
    exit 1
fi
