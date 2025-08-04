#!/bin/bash

echo "Testing Update Incident API directly..."

# Get existing incident
echo "Fetching incident #1..."
INCIDENT=$(curl -s "http://localhost:8080/api/incidents/1")
echo "Current incident: $INCIDENT"

# Test update with correct format (new enum values)
echo ""
echo "Testing update with new severity values..."
UPDATE_DATA='{
    "status": "IDENTIFIED",
    "severityLevel": "HIGH",
    "assignee": "Test User",
    "resolved": false
}'

echo "Sending data: $UPDATE_DATA"

RESPONSE=$(curl -s -X PUT \
  -H "Content-Type: application/json" \
  -d "$UPDATE_DATA" \
  "http://localhost:8080/api/incidents/1")

echo "Response: $RESPONSE"

# Test update with wrong format (old enum values)
echo ""
echo "Testing update with old severity format..."
WRONG_UPDATE_DATA='{
    "status": "IDENTIFIED",
    "severityLevel": "SEV2",
    "assignee": "Test User", 
    "resolved": false
}'

echo "Sending old enum data: $WRONG_UPDATE_DATA"

WRONG_RESPONSE=$(curl -s -X PUT \
  -H "Content-Type: application/json" \
  -d "$WRONG_UPDATE_DATA" \
  "http://localhost:8080/api/incidents/1")

echo "Old enum response: $WRONG_RESPONSE"
