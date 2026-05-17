# 💡 External API Integration - Examples & Testing

## Quick Start (Copy-Paste Examples)

### ✅ Test GET Endpoint

**Using cURL:**
```bash
# Basic request
curl -X GET "http://localhost:8080/weather/get?city=London"

# With headers
curl -X GET "http://localhost:8080/weather/get?city=Paris" \
  -H "Content-Type: application/json"

# Multiple cities (run separately)
curl -X GET "http://localhost:8080/weather/get?city=Tokyo"
curl -X GET "http://localhost:8080/weather/get?city=NewYork"
curl -X GET "http://localhost:8080/weather/get?city=Sydney"
```

**Using Postman:**
1. Click **New** → **Request**
2. Method: **GET**
3. URL: `http://localhost:8080/weather/get?city=London`
4. Headers: `Content-Type: application/json`
5. Click **Send**

---

### ✅ Test POST Endpoint

**Using cURL:**
```bash
# Basic request
curl -X POST "http://localhost:8080/weather/post" \
  -H "Content-Type: application/json" \
  -d '{"city":"London"}'

# With all fields
curl -X POST "http://localhost:8080/weather/post" \
  -H "Content-Type: application/json" \
  -d '{"city":"Paris","endpoint":"weather"}'

# Multiple cities
curl -X POST "http://localhost:8080/weather/post" \
  -H "Content-Type: application/json" \
  -d '{"city":"Tokyo"}'

curl -X POST "http://localhost:8080/weather/post" \
  -H "Content-Type: application/json" \
  -d '{"city":"NewYork"}'
```

**Using Postman:**
1. Click **New** → **Request**
2. Method: **POST**
3. URL: `http://localhost:8080/weather/post`
4. Headers: `Content-Type: application/json`
5. Body (raw JSON):
```json
{
  "city": "London",
  "endpoint": "weather"
}
```
6. Click **Send**

---

## 📨 Real Request Examples

### Example 1: GET Request for London

**Request:**
```
GET /weather/get?city=London HTTP/1.1
Host: localhost:8080
Content-Type: application/json
```

**Response (Success - 200):**
```json
{
  "success": true,
  "message": "Weather data retrieved successfully",
  "data": {
    "tempC": 15.2,
    "tempF": 59.36,
    "feelsLikeC": 12.8,
    "feelsLikeF": 55.04,
    "humidity": 72,
    "condition": "Partly cloudy",
    "windSpeedKmph": 12.5,
    "windSpeedMph": 7.77,
    "city": "London",
    "timezone": "Europe/London"
  },
  "statusCode": 200,
  "timestamp": "2026-05-17T14:22:10.543Z"
}
```

---

### Example 2: POST Request for Tokyo

**Request:**
```
POST /weather/post HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "city": "Tokyo",
  "endpoint": "weather"
}
```

**Response (Success - 200):**
```json
{
  "success": true,
  "message": "Weather data retrieved via POST successfully",
  "data": {
    "tempC": 22.5,
    "tempF": 72.5,
    "feelsLikeC": 21.0,
    "feelsLikeF": 69.8,
    "humidity": 65,
    "condition": "Sunny",
    "windSpeedKmph": 8.2,
    "windSpeedMph": 5.09,
    "city": "Tokyo",
    "timezone": "Asia/Tokyo"
  },
  "statusCode": 200,
  "timestamp": "2026-05-17T14:22:11.234Z"
}
```

---

### Example 3: Error - Invalid City

**Request:**
```
GET /weather/get?city= HTTP/1.1
Host: localhost:8080
Content-Type: application/json
```

**Response (Error - 400):**
```json
{
  "success": false,
  "message": "City parameter is required",
  "statusCode": 400,
  "timestamp": "2026-05-17T14:22:12.001Z"
}
```

---

### Example 4: Error - Missing Request Body

**Request:**
```
POST /weather/post HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{}
```

**Response (Error - 400):**
```json
{
  "success": false,
  "message": "City name is required in request body",
  "statusCode": 400,
  "timestamp": "2026-05-17T14:22:13.567Z"
}
```

---

### Example 5: API Timeout (5+ seconds)

**Request:**
```javascript
// If the external API doesn't respond in 5 seconds
GET /weather/get?city=London HTTP/1.1
```

**Response (Error - 500):**
```json
{
  "success": false,
  "message": "API call failed: java.net.SocketTimeoutException: Connection timed out",
  "statusCode": 500,
  "errorDetails": null,
  "timestamp": "2026-05-17T14:22:15.890Z"
}
```

---

## 🔄 Complete Test Flow

### Step 1: Start the Application
```bash
cd D:\Intellij-Project\Module
mvn clean spring-boot:run
```

Wait for output:
```
Started ModuleApplication in 2.345 seconds
```

### Step 2: Test Health Endpoint
```bash
curl -X GET "http://localhost:8080/weather/health"
```

Expected Output:
```json
{
  "success": true,
  "message": "Weather API is running",
  "data": "OK",
  "statusCode": 200
}
```

### Step 3: Test GET Endpoint
```bash
curl -X GET "http://localhost:8080/weather/get?city=London"
```

You should see weather data for London in the response.

### Step 4: Test POST Endpoint
```bash
curl -X POST "http://localhost:8080/weather/post" \
  -H "Content-Type: application/json" \
  -d '{"city":"Paris"}'
```

You should see weather data for Paris in the response.

### Step 5: Test Error Scenarios
```bash
# Test with empty city
curl -X GET "http://localhost:8080/weather/get?city="

# Test with missing body
curl -X POST "http://localhost:8080/weather/post" \
  -H "Content-Type: application/json" \
  -d '{}'
```

---

## 📊 Response Format Breakdown

All responses follow this structure:

```json
{
  "success": boolean,              // true = success, false = error
  "message": "string",             // Human-readable message
  "data": {                        // Actual response data (null on error)
    "tempC": 15.2,
    "tempF": 59.36,
    "humidity": 72,
    ...
  },
  "statusCode": 200,               // HTTP status code
  "timestamp": "ISO-8601 format",  // When response was created
  "errorDetails": "string or null" // Error details if any
}
```

---

## 🧪 Automated Testing Script

### Save as: `test_weather_api.sh` (Linux/Mac)

```bash
#!/bin/bash

BASE_URL="http://localhost:8080/weather"

echo "=== Testing Weather API ==="
echo ""

# Test 1: Health Check
echo "Test 1: Health Check"
curl -s -X GET "$BASE_URL/health" | jq .
echo ""

# Test 2: GET Weather for London
echo "Test 2: GET Weather for London"
curl -s -X GET "$BASE_URL/get?city=London" | jq .
echo ""

# Test 3: GET Weather for Paris
echo "Test 3: GET Weather for Paris"
curl -s -X GET "$BASE_URL/get?city=Paris" | jq .
echo ""

# Test 4: POST Weather for Tokyo
echo "Test 4: POST Weather for Tokyo"
curl -s -X POST "$BASE_URL/post" \
  -H "Content-Type: application/json" \
  -d '{"city":"Tokyo"}' | jq .
echo ""

# Test 5: Error Test - Empty city
echo "Test 5: Error Test - Empty City"
curl -s -X GET "$BASE_URL/get?city=" | jq .
echo ""

# Test 6: Error Test - Missing body
echo "Test 6: Error Test - Missing Body"
curl -s -X POST "$BASE_URL/post" \
  -H "Content-Type: application/json" \
  -d '{}' | jq .
echo ""

echo "=== All Tests Completed ==="
```

Run with:
```bash
bash test_weather_api.sh
```

---

### Save as: `test_weather_api.bat` (Windows)

```batch
@echo off
setlocal enabledelayedexpansion

set BASE_URL=http://localhost:8080/weather

echo === Testing Weather API ===
echo.

echo Test 1: Health Check
curl -X GET "%BASE_URL%/health"
echo.
echo.

echo Test 2: GET Weather for London
curl -X GET "%BASE_URL%/get?city=London"
echo.
echo.

echo Test 3: GET Weather for Paris
curl -X GET "%BASE_URL%/get?city=Paris"
echo.
echo.

echo Test 4: POST Weather for Tokyo
curl -X POST "%BASE_URL%/post" ^
  -H "Content-Type: application/json" ^
  -d "{\"city\":\"Tokyo\"}"
echo.
echo.

echo Test 5: Error Test - Empty City
curl -X GET "%BASE_URL%/get?city="
echo.
echo.

echo Test 6: Error Test - Missing Body
curl -X POST "%BASE_URL%/post" ^
  -H "Content-Type: application/json" ^
  -d "{}"
echo.
echo.

echo === All Tests Completed ===
pause
```

Run with:
```cmd
test_weather_api.bat
```

---

## 📝 Postman Collection (JSON)

Save as `weather-api.postman_collection.json` and import into Postman:

```json
{
  "info": {
    "name": "Weather API",
    "description": "Collection for testing Weather API integration"
  },
  "item": [
    {
      "name": "Health Check",
      "request": {
        "method": "GET",
        "url": "http://localhost:8080/weather/health"
      }
    },
    {
      "name": "GET Weather - London",
      "request": {
        "method": "GET",
        "url": "http://localhost:8080/weather/get?city=London"
      }
    },
    {
      "name": "GET Weather - Paris",
      "request": {
        "method": "GET",
        "url": "http://localhost:8080/weather/get?city=Paris"
      }
    },
    {
      "name": "POST Weather - Tokyo",
      "request": {
        "method": "POST",
        "url": "http://localhost:8080/weather/post",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\"city\":\"Tokyo\"}"
        }
      }
    },
    {
      "name": "Error - Empty City",
      "request": {
        "method": "GET",
        "url": "http://localhost:8080/weather/get?city="
      }
    },
    {
      "name": "Error - Missing Body",
      "request": {
        "method": "POST",
        "url": "http://localhost:8080/weather/post",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{}"
        }
      }
    }
  ]
}
```

---

## 🐛 Troubleshooting

### Issue: "Connection refused"
**Cause:** Application not running
**Solution:**
```bash
mvn clean spring-boot:run
```

### Issue: "Invalid API Key"
**Cause:** API key expired or incorrect
**Solution:**
1. Check `application.yml` has correct key
2. Replace with new key from api-ninjas.com

### Issue: "Timeout"
**Cause:** External API not responding
**Solution:**
1. Check internet connection
2. API might be down - check api-ninjas.com status
3. Increase timeout in AppConfig (default: 5s connect, 10s read)

### Issue: "No Handler Found"
**Cause:** Wrong endpoint URL
**Solution:**
- Verify URL: `GET /weather/get?city=London`
- Verify URL: `POST /weather/post`

---

## 📊 Log Output Examples

When running tests, check logs in:
- **Console:** Real-time output
- **File:** `logs/module.log`

### Example Log Output:

```
2026-05-17 14:22:10 - INFO WeatherController - GET /weather/get endpoint called with city: London
2026-05-17 14:22:10 - INFO WeatherService - Fetching weather data for city: London
2026-05-17 14:22:10 - DEBUG WeatherService - API URL: https://api.api-ninjas.com/v1/weather?city=London
2026-05-17 14:22:11 - INFO WeatherService - Weather data retrieved successfully for city: London
2026-05-17 14:22:11 - INFO WeatherController - Successfully retrieved weather for city: London
```

---

## ✨ Key Takeaways

✅ **GET Method** - Retrieve data with query parameters
✅ **POST Method** - Send data in request body
✅ **RestTemplate** - Simple HTTP client for external APIs
✅ **DTOs** - Separate request/response structures
✅ **Error Handling** - Graceful failure with meaningful messages
✅ **Logging** - Track API calls and issues
✅ **Configuration** - Externalize API settings

Happy Testing! 🎉

