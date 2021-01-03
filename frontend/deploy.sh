#!/usr/bin/env bash

XXXX="sem"
DROPLET_URL="46.101.211.174"

echo "##############################"
echo "Building the frontend project"
echo "##############################"
npm run build

echo "##############################"
echo "Deploying Frontend project..."
echo "##############################"

scp -r ./build root@$DROPLET_URL:/var/www/$XXXX