#!/bin/bash

# MongoDB Stop Script
# This script stops the MongoDB container

set -e

echo "Stopping MongoDB for Post Service..."

# Stop MongoDB container
if docker ps | grep -q mongodb; then
    echo "Stopping MongoDB container..."
    docker stop mongodb
    echo "✅ MongoDB stopped successfully!"
else
    echo "ℹ️  MongoDB container is not running"
fi

echo ""
echo "MongoDB container status:"
docker ps -a --filter name=mongodb --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

echo ""
echo "To start MongoDB again: ./start-mongodb.sh"
echo "To remove MongoDB completely: docker rm mongodb && docker volume rm mongodb-data"
