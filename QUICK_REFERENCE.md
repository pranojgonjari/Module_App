# Quick Reference Guide

## Default Test Credentials

```
Username: admin
Password: admin123
Role: ROLE_ADMIN

Username: user
Password: user123
Role: ROLE_USER
```

## Available Endpoints

### Public Endpoints (No Auth Required)

```
GET /public/hello
- Response: "Hello! This is a public endpoint - no authentication required."

GET /public/health
- Response: "OK"

GET /public/info
- Response: "Module API - Spring Boot 3 with Spring Security and MongoDB"
```

### Secured Endpoints (Authentication Required)

```
GET /api/hello
- Response: "Hello {username}! This is a secured endpoint."

GET /api/user-info
- Response: {
    "username": "{username}",
    "roles": "ROLE_ADMIN or ROLE_USER",
    "authenticated": true
  }

GET /api/admin
- Response: "Welcome Admin {username}!"
```

## Quick Test Commands

### Curl Commands

```bash
# Test public endpoint (no auth)
curl http://localhost:8080/public/hello

# Test secure endpoint with admin
curl -u admin:admin123 http://localhost:8080/api/hello

# Test with user account
curl -u user:user123 http://localhost:8080/api/hello

# Get user info
curl -u admin:admin123 http://localhost:8080/api/user-info

# Pretty print JSON
curl -u admin:admin123 http://localhost:8080/api/user-info | jq
```

### PowerShell Commands

```powershell
# Using Invoke-WebRequest
$cred = New-Object System.Management.Automation.PSCredential("admin", (ConvertTo-SecureString "admin123" -AsPlainText -Force))
Invoke-WebRequest -Uri "http://localhost:8080/api/hello" -Authentication Basic -Credential $cred

# Using Invoke-RestMethod
$uri = "http://localhost:8080/api/hello"
$header = @{
    "Authorization" = "Basic " + [System.Convert]::ToBase64String([System.Text.Encoding]::ASCII.GetBytes("admin:admin123"))
}
Invoke-RestMethod -Uri $uri -Headers $header -Method Get
```

### Base64 Encoding/Decoding

```bash
# Encode credentials
echo -n "admin:admin123" | base64
# Output: YWRtaW46YWRtaW4xMjM=

# Decode credentials
echo "YWRtaW46YWRtaW4xMjM=" | base64 -d
# Output: admin:admin123
```

## Manual Authorization Header

```
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

Format:
```
Authorization: Basic base64(username:password)
```

## Postman Collection

```json
{
  "info": {
    "name": "Module API",
    "description": "Spring Boot 3 + Spring Security + MongoDB"
  },
  "item": [
    {
      "name": "Public - Hello",
      "request": {
        "method": "GET",
        "url": "http://localhost:8080/public/hello"
      }
    },
    {
      "name": "Secure - Hello (Admin)",
      "request": {
        "method": "GET",
        "url": "http://localhost:8080/api/hello",
        "auth": {
          "type": "basic",
          "basic": [
            {"key": "username", "value": "admin"},
            {"key": "password", "value": "admin123"}
          ]
        }
      }
    },
    {
      "name": "Secure - User Info",
      "request": {
        "method": "GET",
        "url": "http://localhost:8080/api/user-info",
        "auth": {
          "type": "basic",
          "basic": [
            {"key": "username", "value": "admin"},
            {"key": "password", "value": "admin123"}
          ]
        }
      }
    }
  ]
}
```

## Troubleshooting

### 401 Unauthorized
- Check credentials are correct
- Ensure Authorization header is present
- Verify user exists in MongoDB
- Check if endpoint requires authentication

### User Not Found
```bash
# Check MongoDB
mongo localhost:27017/moduledb
db.users.find()
```

### Transaction Error
```
Setup MongoDB replica set:
mongod --replSet rs0
mongo
rs.initiate()
```

### Password Not Matching
- Ensure password is BCrypt encoded
- Check password was saved correctly
- Restart application to recreate default users

## File Locations

```
SecurityConfig:          src/main/java/com/example/module/config/SecurityConfig.java
CustomUserDetailsService: src/main/java/com/example/module/security/CustomUserDetailsService.java
Users Entity:            src/main/java/com/example/module/entity/Users.java
UserRepository:          src/main/java/com/example/module/repository/UserRepository.java
PublicController:        src/main/java/com/example/module/controller/PublicController.java
SecureController:        src/main/java/com/example/module/controller/SecureController.java
Application Config:      src/main/resources/application.properties
```

## Key Concepts Summary

| Concept | Description |
|---------|-------------|
| BCrypt | One-way password hashing with salt |
| Basic Auth | Stateless HTTP authentication |
| Stateless | No server-side session; auth headers sent with each request |
| GrantedAuthority | Spring Security representation of user roles |
| UserDetailsService | Interface for loading users from database |
| SecurityFilterChain | Filter chain that processes security |
| DaoAuthenticationProvider | Loads user via UserDetailsService |

## Common Error Messages

| Error | Solution |
|-------|----------|
| 401 Unauthorized | Check credentials and Authorization header |
| User not found | Verify user exists in MongoDB |
| Transaction error | Setup MongoDB replica set |
| No qualifying bean | Add @Bean for PasswordEncoder |
| Cannot find symbol | Check dependencies in pom.xml |

---

Last Updated: 2026-05-14

