#!/bin/bash
set -e

# Variables
DOCKER_USERNAME="genoyves"
IMAGE_NAME="medadmin"
TAG="latest"

echo "Building Docker image..."
docker build -t $IMAGE_NAME .

echo "Tagging image for Docker Hub..."
docker tag $IMAGE_NAME $DOCKER_USERNAME/$IMAGE_NAME:$TAG

echo "Pushing image to Docker Hub..."
docker push $DOCKER_USERNAME/$IMAGE_NAME:$TAG

echo "Done."
