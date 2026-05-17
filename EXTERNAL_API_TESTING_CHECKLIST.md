# 🧪 External API Integration - Testing Checklist

## ✅ Pre-Requisites

- [ ] Java 21+ installed
- [ ] Maven installed
- [ ] curl or Postman installed
- [ ] Internet connection (for external API)
- [ ] Port 8080 is available

---

## 📋 Project Compilation

### Step 1: Clean Build
```bash
cd D:\Intellij-Project\Module
mvn clean compile
```

**Expected Output:**
```
[INFO] BUILD SUCCESS
```

**If Failed:**
- Check Java version: `java -version` (should be 21+)
- Check Maven: `mvn -version`
- Check internet for dependencies

### Step 2: Package Build
```bash
mvn clean package -DskipTests
```

**Expected Output:**
```
[INFO] BUILD SUCCESS
[INFO] Created: target/Module-0.0.1-SNAPSHOT.jar
```

---

## 🚀 Start Application

```bash
cd D:\Intellij-Project\Module
mvn spring-boot:run
```

**Expected Console Output:**
```
Started ModuleApplication in X.XXX seconds (process X)
Running with Spring Boot vX.X.X
```

**If Port 8080 is Already Used:**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=9090"
```

---

## 🧪 API Testing

### Test 1: Health Check ✅

**Command:**
```bash
curl -X GET "http://localhost:8080/weather/health"
```

**Expected Result:**
```json
{
  "success": true,
  "message": "Weather API is running",
  "data": "OK",
  "statusCode": 200
}
```

**Status:** `200 OK`

---

### Test 2: GET Request - Single City ✅

**Command:**
```bash
curl -X GET "http://localhost:8080/weather/get?city=London"
```

**Expected Result:**
```json
{
  "success": true,
  "message": "Weather data retrieved successfully",
  "data": {
    "tempC": <number>,
    "tempF": <number>,
    "feelsLikeC": <number>,
    "humidity": <number>,
    "condition": "<string>",
    "windSpeedKmph": <number>,
    "city": "London",
    "timezone": "<string>"
  },
  "statusCode": 200,
  "timestamp": "<ISO-8601-date>"
}
```

**Status:** `200 OK`

---

### Test 3: GET Request - Different Cities ✅

**Test Paris:**
```bash
curl -X GET "http://localhost:8080/weather/get?city=Paris"
```
**Expected:** 200 OK with Paris weather data

**Test Tokyo:**
```bash
curl -X GET "http://localhost:8080/weather/get?city=Tokyo"
```
**Expected:** 200 OK with Tokyo weather data

**Test New York:**
```bash
curl -X GET "http://localhost:8080/weather/get?city=NewYork"
```
**Expected:** 200 OK with New York weather data

---

### Test 4: POST Request - Single City ✅

**Command:**
```bash
curl -X POST "http://localhost:8080/weather/post" \
  -H "Content-Type: application/json" \
  -d '{"city":"Paris"}'
```

**Expected Result:**
```json
{
  "success": true,
  "message": "Weather data retrieved via POST successfully",
  "data": {
    "tempC": <number>,
    "humidity": <number>,
    "condition": "<string>",
    "city": "Paris",
    ...
  },
  "statusCode": 200,
  "timestamp": "<ISO-8601-date>"
}
```

**Status:** `200 OK`

---

### Test 5: POST Request - With Endpoint Parameter ✅

**Command:**
```bash
curl -X POST "http://localhost:8080/weather/post" \
  -H "Content-Type: application/json" \
  -d '{"city":"Tokyo","endpoint":"weather"}'
```

**Expected Result:**
```json
{
  "success": true,
  "message": "Weather data retrieved via POST successfully",
  "data": {
    "tempC": <number>,
    "city": "Tokyo",
    ...
  },
  "statusCode": 200
}
```

**Status:** `200 OK`

---

## 🚨 Error Testing

### Error Test 1: Empty City Parameter ⚠️

**Command:**
```bash
curl -X GET "http://localhost:8080/weather/get?city="
```

**Expected Result:**
```json
{
  "success": false,
  "message": "City parameter is required",
  "statusCode": 400
}
```

**Status:** `400 Bad Request`

---

### Error Test 2: Missing City Parameter ⚠️

**Command:**
```bash
curl -X GET "http://localhost:8080/weather/get"
```

**Expected Result:**
```json
{
  "success": false,
  "message": "City parameter is required",
  "statusCode": 400
}
```

**Status:** `400 Bad Request`

---

### Error Test 3: Empty JSON Body ⚠️

**Command:**
```bash
curl -X POST "http://localhost:8080/weather/post" \
  -H "Content-Type: application/json" \
  -d '{}'
```

**Expected Result:**
```json
{
  "success": false,
  "message": "City name is required in request body",
  "statusCode": 400
}
```

**Status:** `400 Bad Request`

---

### Error Test 4: Null City in POST ⚠️

**Command:**
```bash
curl -X POST "http://localhost:8080/weather/post" \
  -H "Content-Type: application/json" \
  -d '{"city":null}'
```

**Expected Result:**
```json
{
  "success": false,
  "message": "City name is required in request body",
  "statusCode": 400
}
```

**Status:** `400 Bad Request`

---

### Error Test 5: Invalid JSON ⚠️

**Command:**
```bash
curl -X POST "http://localhost:8080/weather/post" \
  -H "Content-Type: application/json" \
  -d 'invalid json'
```

**Expected Result:**
```
{
  "timestamp": "...",
  "status": 400,
  "error": "Bad Request"
}
```

**Status:** `400 Bad Request`

---

## 📊 Logging Tests

### Check Console Logs

While tests are running, look for these log messages:

```
INFO WeatherController - GET /weather/get endpoint called with city: London
INFO WeatherService - Fetching weather data for city: London
DEBUG WeatherService - API URL: https://api.api-ninjas.com/v1/weather?city=London
INFO WeatherService - Weather data retrieved successfully for city: London
INFO WeatherController - Successfully retrieved weather for city: London
```

---

### Check File Logs

```bash
# Unix/Linux/Mac
tail -f logs/module.log

# Windows PowerShell
Get-Content logs\module.log -Tail 20 -Wait
```

**Expected Log Format:**
```
2026-05-17 14:22:10 - INFO WeatherService - Fetching weather data for city: London
2026-05-17 14:22:11 - INFO WeatherService - Weather data retrieved successfully for city: London
```

---

## 🔁 Performance Tests

### Test 1: Response Time

**Command:**
```bash
# Unix/Linux/Mac - time the request
time curl -X GET "http://localhost:8080/weather/get?city=London"

# Windows PowerShell - measure request time
Measure-Command { curl -X GET "http://localhost:8080/weather/get?city=London" }
```

**Expected Response Time:**
- Health check: < 100ms
- Local API call: < 500ms
- External API call: 1-3 seconds (depends on network)

---

### Test 2: Multiple Requests

**Command:**
```bash
# Test 10 requests
for i in {1..10}; do
  curl -X GET "http://localhost:8080/weather/get?city=London"
  echo "Request $i completed"
done
```

**Expected:** All requests should succeed within timeout window

---

## 🔄 Timeout Test

### Simulate Slow Response

If you want to test timeout behavior, you can temporarily increase the timeout in `AppConfig.java`:

```java
.setReadTimeout(Duration.ofMillis(100))  // Very short timeout
```

Then run a test - should get timeout error after 100ms.

---

## ✨ Additional Validation

### Check File Exists

All files should be created:

```bash
# Check DTO files
ls -la src/main/java/com/example/module/dto/

# Check service
ls -la src/main/java/com/example/module/service/WeatherService.java

# Check controller
ls -la src/main/java/com/example/module/controller/WeatherController.java

# Check config
ls -la src/main/java/com/example/module/config/AppConfig.java

# Check documentation
ls -la EXTERNAL_API_*.md
```

---

### Check Config Properties

Verify `application.yml` contains weather API settings:

```bash
grep -A 3 "weather:" src/main/resources/application.yml
```

Expected output:
```yaml
weather:
  api:
    base-url: https://api.api-ninjas.com/v1/weather
    key: GfuKjCTXvstEGHRZzBsSKrcLBpTWo4M0dS1BFjG6
```

---

## 📈 Testing Summary

### Tests Passed: _____ / 15

- [ ] **Compilation** - Project compiles without errors
- [ ] **Application Start** - Starts within 5 seconds
- [ ] **Health Check** - Returns 200 OK
- [ ] **GET London** - Returns weather data
- [ ] **GET Paris** - Returns weather data
- [ ] **GET Tokyo** - Returns weather data
- [ ] **GET NewYork** - Returns weather data
- [ ] **POST Paris** - Returns weather data via POST
- [ ] **POST Tokyo** - Returns weather data via POST
- [ ] **Error: Empty City** - Returns 400 Bad Request
- [ ] **Error: Missing City** - Returns 400 Bad Request
- [ ] **Error: No Body** - Returns 400 Bad Request
- [ ] **Error: Null City** - Returns 400 Bad Request
- [ ] **Logging** - Console shows log messages
- [ ] **File Logs** - logs/module.log contains entries

---

## 🎯 Troubleshooting

### Issue: "Connection refused"
```bash
# Make sure app is running
mvn spring-boot:run
```

### Issue: "Timeout waiting for response"
```bash
# Check internet connection
ping api.api-ninjas.com

# If timeout, increase timeout in AppConfig.java
.setReadTimeout(Duration.ofSeconds(15))
```

### Issue: "JSON parse error"
```bash
# Check if API is returning valid JSON
curl -i https://api.api-ninjas.com/v1/weather?city=London \
  -H "X-Api-Key: GfuKjCTXvstEGHRZzBsSKrcLBpTWo4M0dS1BFjG6"
```

### Issue: "Invalid API Key"
```bash
# Verify key in application.yml
cat src/main/resources/application.yml | grep -A 2 weather:
```

---

## 📝 Test Report Template

```
Testing Date: __________
Tester Name: __________

Compilation:
- Build Status: SUCCESS / FAILED
- Warnings: None / __________

Application Start:
- Start Time: __________ seconds
- Memory Used: __________

API Tests:
- Health Check: PASS / FAIL
- GET Requests: PASS / FAIL (X/X)
- POST Requests: PASS / FAIL (X/X)
- Error Cases: PASS / FAIL (X/X)

Logging:
- Console Logs: Visible / Not Visible
- File Logs: Present / Missing
- Log Level: Correct / Incorrect

Performance:
- Response Time: __________ ms (average)
- Timeout: Works / Doesn't Work
- Concurrent Requests: __________

Issues Found:
1. __________
2. __________

Overall Status: PASS / FAIL
```

---

## 🎉 Success Criteria

**All tests pass when:**

✅ Application starts without errors  
✅ Health check returns 200 OK  
✅ GET requests return weather data  
✅ POST requests return weather data  
✅ Error handling works correctly  
✅ Logging appears in console and file  
✅ Response times are acceptable  

---

## 📞 Quick Commands Reference

```bash
# Start application
mvn spring-boot:run

# Test health
curl -X GET http://localhost:8080/weather/health

# Test GET
curl -X GET "http://localhost:8080/weather/get?city=London"

# Test POST
curl -X POST http://localhost:8080/weather/post \
  -H "Content-Type: application/json" \
  -d '{"city":"London"}'

# View logs
tail -f logs/module.log

# Stop application
Ctrl + C
```

---

**Happy Testing!** 🎉

All tests should pass. If any fail, check the logs and refer to the troubleshooting section.

Once all tests pass, your external API integration is working perfectly!

