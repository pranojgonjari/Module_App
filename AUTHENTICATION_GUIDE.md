# Spring Boot 3 + Spring Security + MongoDB Authentication System

## Overview

This is a complete production-ready authentication system using:
- **Spring Boot 3** - Latest Spring framework
- **Spring Security 6** - Modern security framework
- **MongoDB** - NoSQL database with BCrypt password encoding
- **HTTP Basic Authentication** - Stateless authentication method
- **Java 21** - Latest Java version

---

## Table of Contents

1. [Project Structure](#project-structure)
2. [Architecture Explanation](#architecture-explanation)
3. [How Authentication Works](#how-authentication-works)
4. [How Basic Authentication Works](#how-basic-authentication-works)
5. [Password Encoding with BCrypt](#password-encoding-with-bcrypt)
6. [Component Explanations](#component-explanations)
7. [Testing with Postman](#testing-with-postman)
8. [Testing with Curl](#testing-with-curl)
9. [MongoDB Documents](#mongodb-documents)
10. [Common Errors & Solutions](#common-errors--solutions)

---

## Project Structure

```
Module/
├── pom.xml                          # Maven configuration with dependencies
├── src/
│   ├── main/
│   │   ├── java/com/example/module/
│   │   │   ├── ModuleApplication.java          # Main app with initialization
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java         # Spring Security configuration
│   │   │   │   └── SpringSecurity.java         # DEPRECATED (for reference)
│   │   │   ├── controller/
│   │   │   │   ├── PublicController.java       # Public endpoints (no auth)
│   │   │   │   ├── SecureController.java       # Secured endpoints (with auth)
│   │   │   │   └── (other controllers...)
│   │   │   ├── entity/
│   │   │   │   ├── Users.java                  # User entity with roles
│   │   │   │   └── ModuleEntry.java
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java         # MongoDB repository
│   │   │   │   └── ModuleEntryRepository.java
│   │   │   ├── security/
│   │   │   │   └── CustomUserDetailsService.java  # Loads users from MongoDB
│   │   │   └── service/
│   │   │       ├── UserService.java
│   │   │       └── ModuleEntryService.java
│   │   └── resources/
│   │       └── application.properties          # Configuration
│   └── test/
│       └── java/...                            # Unit tests
└── target/                          # Compiled classes
```

---

## Architecture Explanation

### Layered Architecture

```
┌─────────────────────────────────────┐
│        HTTP Request                 │
│  (with Basic Auth Header)           │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│    Spring Security Filter Chain     │
│  (BasicAuthenticationFilter)        │  ← Extracts credentials from header
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│    AuthenticationManager            │
│  (processes authentication)         │  ← Manages authentication process
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  DaoAuthenticationProvider          │  ← Uses UserDetailsService
│  (loads user & compares password)   │  ← Compares with BCrypt
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  CustomUserDetailsService           │  ← Loads users from MongoDB
│  (implements UserDetailsService)    │  ← Converts roles to authorities
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  UserRepository                     │  ← MongoDB query
│  (Spring Data MongoDB)              │  ← Returns User from DB
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  MongoDB                            │  ← Stores BCrypt encoded password
│  (users collection)                 │  ← Stores roles
└─────────────────────────────────────┘
```

---

## How Authentication Works

### Step-by-Step Authentication Flow

1. **Client Sends Request with Credentials**
   ```
   GET /api/hello HTTP/1.1
   Authorization: Basic YWRtaW46YWRtaW4xMjM=
   Host: localhost:8080
   ```
   - `YWRtaW46YWRtaW4xMjM=` is base64 encoding of `admin:admin123`

2. **Spring Security Filter Intercepts Request**
   - `BasicAuthenticationFilter` catches the request
   - Extracts the `Authorization: Basic` header
   - Decodes base64 to get `username:password`

3. **AuthenticationManager Processes Credentials**
   - Uses `DaoAuthenticationProvider` to authenticate
   - Calls `CustomUserDetailsService.loadUserByUsername(username)`

4. **CustomUserDetailsService Loads User from MongoDB**
   - Queries: `db.users.findOne({username: "admin"})`
   - Gets user document with BCrypt encoded password
   - Converts roles to Spring Security GrantedAuthority objects

5. **Password Verification**
   - `DaoAuthenticationProvider` compares passwords
   - Takes provided password: `admin123`
   - Compares with stored hash: `$2a$10$...`
   - BCrypt algorithm verifies the match

6. **Authentication Successful**
   - Spring Security creates `Authentication` object
   - Sets it in `SecurityContext`
   - Request proceeds to controller

7. **Controller Receives Authenticated Request**
   - `@GetMapping("/api/hello")` method executes
   - `Authentication` object injected automatically
   - Returns response with user info

### Authentication Failure Cases

**Case 1: Invalid Username**
```
User "invalid" not found in MongoDB
→ CustomUserDetailsService throws UsernameNotFoundException
→ Authentication fails
→ Server returns 401 Unauthorized
```

**Case 2: Invalid Password**
```
Password "wrongpassword" doesn't match BCrypt hash
→ DaoAuthenticationProvider rejects authentication
→ Server returns 401 Unauthorized
```

**Case 3: No Credentials Provided**
```
Authorization header missing or invalid format
→ BasicAuthenticationFilter creates BadCredentialsException
→ Server returns 401 Unauthorized
```

---

## How Basic Authentication Works

### What is HTTP Basic Authentication?

Basic Authentication is a simple HTTP authentication scheme:
1. Client sends username and password in `Authorization` header
2. Credentials are base64 encoded (NOT encrypted!)
3. Server decodes and verifies credentials
4. Sends back response with authentication info

### Base64 Encoding (NOT Encryption)

⚠️ **IMPORTANT: Base64 is encoding, NOT encryption**

```
Plaintext:     admin:admin123
Base64:        YWRtaW46YWRtaW4xMjM=
Decoded back:  admin:admin123

ANYONE CAN DECODE THIS!
Use HTTPS to encrypt the connection!
```

### Authorization Header Format

```
Authorization: Basic base64(username:password)
Authorization: Basic YWRtaW46YWRtaW4xMjM=
Authorization: Basic dXNlcjp1c2VyMTIz
```

### Why Use HTTPS?

```
Without HTTPS:
Client → [Base64 credentials] → Network → Attacker can see!
                           
With HTTPS:
Client → [Encrypted: Base64 credentials] → Network → Can't read
```

### How Spring Security Handles Basic Auth

```java
// In SecurityConfig.java
.httpBasic(basic -> basic
    .realmName("Module API")  // Optional realm name
)

// Spring Security automatically:
// 1. Looks for Authorization: Basic header
// 2. Extracts and base64 decodes the header
// 3. Gets username and password
// 4. Creates UsernamePasswordAuthenticationToken
// 5. Passes to AuthenticationManager
```

### Spring Security's BasicAuthenticationFilter

```
HTTP Request
    ↓
BasicAuthenticationFilter checks for "Authorization: Basic" header
    ↓
YES: Extracts and decodes credentials
    ↓
     Creates UsernamePasswordAuthenticationToken(username, password)
    ↓
    Passes to AuthenticationManager for verification
    ↓
NO (no header): Creates BadCredentialsException
    ↓
AuthenticationEntryPoint handles the exception
    ↓
Returns 401 Unauthorized with WWW-Authenticate header
```

---

## Password Encoding with BCrypt

### What is BCrypt?

BCrypt is a password hashing algorithm:
- **One-way function** - Cannot be reversed to get original password
- **Adaptive** - Gets slower over time as computers get faster
- **Salted** - Each password hash includes a random salt
- **Industry standard** - Widely used in production applications

### Why BCrypt?

```
❌ MD5:        password123 → 482c811da5d5b4bc6d497ffa98491e38
               (Anyone can lookup this hash online)

❌ SHA-256:    password123 → ef92b778bafe771e89245d171bafcd74f588f...
               (Still too fast to compute, vulnerable to brute force)

✅ BCrypt:     password123 → $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUx...
               (Takes 0.5+ seconds per attempt, includes salt)
```

### BCrypt Output Format

```
$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36DvDLaym
│││ ││ │                             │                          │
││└─┬─┘ │                             │                          │
│└────┬─┘                             │                          │
│     │                               │                          │
│     └─ Cost factor (10 = 2^10 iterations)                      │
│                                                                │
└─ BCrypt algorithm marker                                       │
                        └─ Random salt (22 characters)           │
                                   └─────────────────── Actual hash

Breaking it down:
- $2a$ = BCrypt algorithm version
- $10$ = Cost factor (10 means 2^10 = 1024 iterations)
- N9qo8uLOickgx2ZMRZoMyeIj = Salt (22 characters)
- Zga7b3XeKeUxWdeS86E36DvDLaym = Actual hash
```

### How BCrypt Works

#### Encoding (Storing Password)

```
Input:      password = "admin123"

Step 1:     Generate random salt (included in output)
            salt = N9qo8uLOickgx2ZMRZoMyeIj

Step 2:     Hash password + salt multiple times
            iterations = 2^cost_factor = 2^10 = 1024
            hash the result 1024 times

Step 3:     Combine into final output
            $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZga7b3XeKeUxWdeS86E36DvDLaym

Result:     Stored in database
```

#### Verification (Checking Password)

```
Input 1:    password provided by user = "admin123"
Input 2:    stored hash = $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZga7b3XeKeUxWdeS86E36DvDLaym

Step 1:     Extract salt from stored hash
            salt = N9qo8uLOickgx2ZMRZoMyeIj

Step 2:     Hash provided password with extracted salt (1024 iterations)
            hash_of_provided = hash("admin123" + salt) × 1024

Step 3:     Compare
            hash_of_provided == stored_hash ?
            
Result:     YES = Password correct, Authentication succeeds
            NO = Password wrong, Authentication fails
```

### BCrypt in Spring Security

```java
// In ModuleApplication.java - Encoding password during initialization
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();  // Strength: 10 (default)
}

// Encoding a password
String rawPassword = "admin123";
String encodedPassword = passwordEncoder.encode(rawPassword);
// Result: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZga7b3XeKeUxWdeS86E36DvDLaym

// Verifying a password
String rawPassword = "admin123";  // User login attempt
String storedHash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZga7b3XeKeUxWdeS86E36DvDLaym";
boolean matches = passwordEncoder.matches(rawPassword, storedHash);
// Result: true (password is correct)

// Never do this (WRONG!):
if (rawPassword.equals(storedHash)) {  // ❌ WRONG
    // Never compare plaintext with hash!
}

// Always use matches() method:
if (passwordEncoder.matches(rawPassword, storedHash)) {  // ✅ CORRECT
    // This is what Spring Security does internally
}
```

### Cost Factor

```java
new BCryptPasswordEncoder();           // Default: cost = 10
new BCryptPasswordEncoder(12);         // Custom: cost = 12

Cost 10:  ~0.2 seconds per verification
Cost 11:  ~0.4 seconds per verification
Cost 12:  ~0.8 seconds per verification

Higher cost = More secure but slower
Adjust based on your performance requirements
```

---

## Component Explanations

### 1. Users Entity (`entity/Users.java`)

```java
@Document(collection = "users")  // Maps to "users" collection in MongoDB
@Data                             // Generates getters, setters, equals, toString
public class Users {
    @Id                           // Marks as MongoDB document ID
    private ObjectId id;
    
    @Indexed(unique = true)       // Creates unique index, prevents duplicate usernames
    private String username;
    
    private String password;      // MUST be BCrypt encoded!
    
    private List<String> roles;   // e.g., ["ROLE_ADMIN", "ROLE_USER"]
    
    @DBRef                        // Database reference to ModuleEntry documents
    private List<ModuleEntry> moduleEntries;
}
```

#### @Document Annotation
- Marks class as a MongoDB document
- `collection = "users"` specifies collection name
- Spring Data MongoDB automatically handles serialization/deserialization

#### @Indexed(unique = true)
- Creates MongoDB unique index on this field
- Ensures no two users have same username
- Throws DuplicateKeyException if violated

#### @DBRef Annotation
- Creates manual reference to another collection
- Stores only the ObjectId of referenced documents
- Allows loading related documents later

### 2. CustomUserDetailsService

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Override
    public UserDetails loadUserByUsername(String username) 
            throws UsernameNotFoundException {
        
        // 1. Query MongoDB
        Users user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(...));
        
        // 2. Convert roles to GrantedAuthority
        Collection<GrantedAuthority> authorities = user.getRoles()
            .stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
        
        // 3. Return Spring Security's User
        return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPassword())  // BCrypt hash from DB
            .authorities(authorities)
            .build();
    }
}
```

#### Why UserDetailsService?
- Spring Security interface that must be implemented
- Tells Spring Security where to load users from
- In our case: MongoDB instead of SQL database

#### loadUserByUsername() Purpose
- Called during authentication
- Loads user from MongoDB by username
- Converts to Spring Security's UserDetails interface

#### Spring Security User vs Our Users Entity
```
Our Entity (Users):
├── Username
├── Password (BCrypt)
├── Roles (List<String>)
└── Module Entries

Spring Security (UserDetails):
├── Username
├── Password (BCrypt)
├── Authorities (Collection<GrantedAuthority>)
├── Account not expired
├── Account not locked
├── Credentials not expired
└── Enabled

Conversion:
User roles ["ROLE_ADMIN"] → GrantedAuthority [SimpleGrantedAuthority("ROLE_ADMIN")]
```

### 3. SecurityConfig

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = 
            new DaoAuthenticationProvider(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) 
            throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> basic
                .realmName("Module API")
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        return http.build();
    }
}
```

#### SecurityFilterChain
- Configures HTTP security
- Defines which endpoints are public/secured
- Enables authentication method (Basic Auth)
- Manages sessions

#### CSRF Disabled?
- CSRF (Cross-Site Request Forgery) protection not needed for stateless APIs
- API doesn't use session cookies
- CSRF protection is mainly for form submissions

#### SessionCreationPolicy.STATELESS
- No HTTP session created
- No JSESSIONID cookie
- Authentication sent with every request
- Ideal for microservices and mobile apps

### 4. PublicController

```java
@RestController
@RequestMapping("/public")
public class PublicController {
    
    @GetMapping("/hello")
    public String hello() {
        return "Public endpoint - no auth needed";
    }
}
```

- Endpoints accessible without authentication
- `permitAll()` in SecurityConfig
- No Authorization header needed

### 5. SecureController

```java
@RestController
@RequestMapping("/api")
public class SecureController {
    
    @GetMapping("/hello")
    public String hello(Authentication authentication) {
        return "Hello " + authentication.getName();
    }
}
```

- Endpoints require authentication
- `authenticated()` in SecurityConfig
- `Authentication` object injected automatically
- Authorization header required

---

## Testing with Postman

### Prerequisites
- Postman installed
- Application running on localhost:8080

### Setting Up Postman

#### Method 1: Using Authorization Tab (Recommended)

1. **Create New Request**
   - Click "New"
   - Select "HTTP Request"

2. **Configure URL**
   - Method: GET
   - URL: `http://localhost:8080/api/hello`

3. **Add Authorization**
   - Tab: "Authorization"
   - Type: "Basic Auth"
   - Username: `admin`
   - Password: `admin123`
   - Click "Refresh headers"

4. **Send Request**
   - Click "Send"
   - Expected response: `Hello admin! This is a secured endpoint.`

#### Method 2: Using Authorization Header

1. **Create New Request**
   - Click "New"
   - Select "HTTP Request"

2. **Configure URL & Headers**
   - Method: GET
   - URL: `http://localhost:8080/api/hello`

3. **Add Header Manually**
   - Tab: "Headers"
   - Key: `Authorization`
   - Value: `Basic YWRtaW46YWRtaW4xMjM=`
     - (This is base64 encoding of `admin:admin123`)

4. **Send Request**
   - Click "Send"

### Test Cases in Postman

#### Test 1: Public Endpoint (No Auth)
```
GET http://localhost:8080/public/hello
Headers: (none)
Authorization: None

Expected Response:
Status: 200 OK
Body: "Hello! This is a public endpoint - no authentication required."
```

#### Test 2: Secured Endpoint (With Auth)
```
GET http://localhost:8080/api/hello
Authorization: Basic Auth
Username: admin
Password: admin123

Expected Response:
Status: 200 OK
Body: "Hello admin! This is a secured endpoint."
```

#### Test 3: Secured Endpoint (Wrong Password)
```
GET http://localhost:8080/api/hello
Authorization: Basic Auth
Username: admin
Password: wrongpassword

Expected Response:
Status: 401 Unauthorized
Body: (varies, usually error message)
```

#### Test 4: Secured Endpoint (No Auth)
```
GET http://localhost:8080/api/hello
Headers: (no Authorization header)

Expected Response:
Status: 401 Unauthorized
Body: (varies, usually error message)
```

#### Test 5: Get User Info
```
GET http://localhost:8080/api/user-info
Authorization: Basic Auth
Username: admin
Password: admin123

Expected Response:
Status: 200 OK
Body: {
    "username": "admin",
    "roles": "ROLE_ADMIN",
    "authenticated": true
}
```

### Postman Collection Template

```json
{
  "info": {
    "name": "Module API Authentication",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
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
      "name": "Secured - Hello (Admin)",
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
    }
  ]
}
```

---

## Testing with Curl

### Prerequisites
- curl command-line tool (usually pre-installed)
- Application running on localhost:8080

### Basic Curl Commands

#### 1. Public Endpoint (No Auth)
```bash
curl http://localhost:8080/public/hello

# Output:
# Hello! This is a public endpoint - no authentication required.
```

#### 2. Secured Endpoint (With Basic Auth)
```bash
curl -u admin:admin123 http://localhost:8080/api/hello

# Equivalent (using Authorization header):
# curl -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" http://localhost:8080/api/hello

# Output:
# Hello admin! This is a secured endpoint.
```

#### 3. User Info (With Auth)
```bash
curl -u admin:admin123 http://localhost:8080/api/user-info

# Output (formatted):
# {
#   "username": "admin",
#   "roles": "ROLE_ADMIN",
#   "authenticated": true
# }
```

#### 4. Wrong Password
```bash
curl -u admin:wrongpassword http://localhost:8080/api/hello

# Output:
# (401 Unauthorized - error message)
```

#### 5. Verify Base64 Encoding
```bash
# Encode credentials to base64
echo -n "admin:admin123" | base64
# Output: YWRtaW46YWRtaW4xMjM=

# Decode back
echo "YWRtaW46YWRtaW4xMjM=" | base64 -d
# Output: admin:admin123
```

### Advanced Curl Options

```bash
# Verbose output (see headers and response)
curl -v -u admin:admin123 http://localhost:8080/api/hello

# Pretty print JSON response (with jq)
curl -u admin:admin123 http://localhost:8080/api/user-info | jq

# Save response to file
curl -u admin:admin123 http://localhost:8080/api/hello -o response.txt

# Follow redirects
curl -L -u admin:admin123 http://localhost:8080/api/hello

# Custom User-Agent
curl -H "User-Agent: MyApp/1.0" -u admin:admin123 http://localhost:8080/api/hello

# Multiple headers
curl -H "User-Agent: MyApp/1.0" \
     -H "X-Custom-Header: value" \
     -u admin:admin123 \
     http://localhost:8080/api/hello
```

### Bash Script for Testing

```bash
#!/bin/bash

# Test script for authentication

BASE_URL="http://localhost:8080"
ADMIN_USER="admin"
ADMIN_PASS="admin123"

echo "=== Testing Public Endpoints ==="
echo -e "\n1. Testing /public/hello:"
curl $BASE_URL/public/hello
echo -e "\n"

echo "=== Testing Secured Endpoints ==="
echo -e "\n2. Testing /api/hello with credentials:"
curl -u $ADMIN_USER:$ADMIN_PASS $BASE_URL/api/hello
echo -e "\n"

echo -e "\n3. Testing /api/user-info:"
curl -u $ADMIN_USER:$ADMIN_PASS $BASE_URL/api/user-info | jq
echo -e "\n"

echo -e "\n4. Testing with wrong password:"
curl -u $ADMIN_USER:wrongpassword $BASE_URL/api/hello
echo -e "\n"

echo "=== Test Complete ==="
```

Save as `test_auth.sh` and run:
```bash
chmod +x test_auth.sh
./test_auth.sh
```

---

## MongoDB Documents

### Sample User Document

After running the application, check MongoDB for the created users:

```bash
# Connect to MongoDB
mongo localhost:27017/moduledb

# View users collection
db.users.find().pretty()
```

#### Sample Output

```json
{
  "_id": ObjectId("507f1f77bcf86cd799439011"),
  "username": "admin",
  "password": "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36DvDLaym",
  "roles": ["ROLE_ADMIN"],
  "_class": "com.example.module.entity.Users"
}

{
  "_id": ObjectId("507f1f77bcf86cd799439012"),
  "username": "user",
  "password": "$2a$10$dXdBTHcHAGZl0zqM7vKEmuIjZAgcg7b3XeKeUxWdeS86E36DvDLaym",
  "roles": ["ROLE_USER"],
  "_class": "com.example.module.entity.Users"
}
```

#### Understanding the Document

```
{
  "_id": ObjectId("507f1f77bcf86cd799439011")
  │
  └─ Unique MongoDB identifier (automatically generated)

  "username": "admin"
  │
  └─ Username (indexed with unique constraint)

  "password": "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36DvDLaym"
  │
  └─ BCrypt encoded hash of password "admin123"
     Not the plaintext password!

  "roles": ["ROLE_ADMIN"]
  │
  └─ List of roles/authorities for this user
     Used by Spring Security for authorization

  "_class": "com.example.module.entity.Users"
  │
  └─ Java class name (Spring Data MongoDB feature)
     Used for object mapping
}
```

### Database Indexes

MongoDB automatically creates indexes based on our annotations:

```bash
# View indexes
db.users.getIndexes()

# Output:
[
  {
    "v": 2,
    "key": {"_id": 1},
    "name": "_id_"
  },
  {
    "v": 2,
    "key": {"username": 1},
    "name": "username_1",
    "unique": true          # ← Unique index due to @Indexed(unique=true)
  }
]
```

### Verifying Password Hash

```bash
# In MongoDB shell, you can verify the hash format
db.users.findOne({username: "admin"})

# Password field should start with $2a$ or $2b$ (BCrypt marker)
# Never compare with plaintext!
```

---

## Common Errors & Solutions

### Error 1: 401 Unauthorized All Requests

**Symptoms:**
```
Status: 401 Unauthorized
Body: (error message)
```

**Causes:**
1. No Authorization header
2. Wrong credentials
3. User not found in database

**Solution:**
```bash
# Check if user exists
mongo localhost:27017/moduledb
db.users.findOne({username: "admin"})

# Verify credentials
curl -u admin:admin123 http://localhost:8080/api/hello

# Check application logs for details
# logging.level.org.springframework.security=DEBUG in application.properties
```

### Error 2: Transaction Numbers Not Supported

**Symptoms:**
```
"Transaction numbers are only allowed on a replica set member or mongos"
```

**Causes:**
- MongoDB is running as single instance, not replica set
- Spring Security requires transactions

**Solution:**
```bash
# Setup MongoDB as single-node replica set (Windows)
# Start MongoDB:
mongod --replSet rs0

# Connect and initialize:
mongo
rs.initiate()

# Verify replica set:
rs.status()
```

### Error 3: Password Not Matching

**Symptoms:**
```
Authentication fails even with correct password
```

**Causes:**
- Password stored as plaintext instead of BCrypt
- User created without password encoding

**Solution:**
```bash
# Delete users and restart app
mongo localhost:27017/moduledb
db.users.deleteMany({})

# Restart application
# ModuleApplication will recreate users with proper BCrypt encoding
```

### Error 4: Spring Security Not Intercepting Requests

**Symptoms:**
```
All endpoints accessible without authentication
```

**Causes:**
1. SecurityConfig not loaded
2. Typo in endpoint path
3. @EnableWebSecurity missing

**Solution:**
```java
// Check SecurityConfig is present in security package
// Check @EnableWebSecurity annotation exists
// Check endpoint paths in SecurityConfig match real endpoints

// Add logging to verify
logging.level.org.springframework.security.web.FilterChainProxy=DEBUG
```

### Error 5: "User not found" Exception

**Symptoms:**
```
UsernameNotFoundException: user=admin, status=401
```

**Causes:**
- User doesn't exist in MongoDB
- Username typo
- Wrong collection name

**Solution:**
```bash
# Check MongoDB
mongo localhost:27017/moduledb
db.users.find()

# Check collection name in @Document annotation
# Ensure username matches exactly (case-sensitive)
```

### Error 6: BCryptPasswordEncoder Not Found

**Symptoms:**
```
Cannot find symbol 'BCryptPasswordEncoder'
```

**Causes:**
- Spring Security dependency not in pom.xml

**Solution:**
```xml
<!-- Add to pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### Error 7: "No qualifying bean of type 'PasswordEncoder'"

**Symptoms:**
```
org.springframework.beans.factory.NoSuchBeanDefinitionException
```

**Causes:**
-PasswordEncoder bean not defined in SecurityConfig

**Solution:**
```java
// Ensure this bean exists in SecurityConfig
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

---

## Interview Questions & Answers

### Q1: How is the password stored in the database?

**Answer:**
The password is stored using BCrypt hashing with salt. BCrypt takes a plaintext password, generates a random salt, and hashes the combination multiple times (cost factor determines iterations). The output includes the salt and hash in the format: `$2a$10$[salt][hash]`. This is a one-way function - the original password cannot be retrieved from the hash.

### Q2: How does Spring Security know to use your UserDetailsService?

**Answer:**
We configure this in the SecurityConfig by creating a DaoAuthenticationProvider bean with the CustomUserDetailsService. The authentication manager uses this provider to load users. When BasicAuthenticationFilter intercepts a request with Basic Auth header, it extracts the username and passes it to the AuthenticationManager, which calls our UserDetailsService to load the user.

### Q3: Why is the session stateless?

**Answer:**
Stateless means no HTTP session is created server-side. Each request includes the authentication credentials (Basic Auth header). This is ideal for APIs because:
1. No cookie/session state to maintain
2. Can scale horizontally - no need for session replication
3. Each request is independent - good for distributed systems
4. Works well with microservices

### Q4: What happens when user logs in?

**Answer:**
1. Client sends Authorization: Basic header with base64(username:password)
2. Spring Security's BasicAuthenticationFilter extracts this
3. Calls CustomUserDetailsService.loadUserByUsername(username)
4. Loads user from MongoDB and converts roles to authorities
5. DaoAuthenticationProvider compares BCrypt hash of stored password with provided password
6. If match, authentication succeeds and Authentication object is set in SecurityContext
7. Request proceeds to controller

### Q5: Is Basic Auth secure?

**Answer:**
Basic Auth itself is not secure because credentials are base64 encoded (not encrypted). Anyone who intercepts the header can decode it. However, it becomes secure when used over HTTPS because the entire HTTP connection is encrypted. For APIs, JWT tokens or OAuth2 are often preferred over Basic Auth.

### Q6: Why use BCrypt instead of MD5 or SHA?

**Answer:**
BCrypt is much slower by design (0.5+ seconds per verification). This makes brute force attacks impractical. MD5 and SHA are cryptographic hashes designed for speed, making them vulnerable to brute force. BCrypt also includes salt and is adaptive - the cost factor can be increased as computers get faster without changing existing hashes.

### Q7: Can you intercept the encoded password and use it?

**Answer:**
Yes! That's why HTTPS is critical. The base64 encoded string in the Authorization header is just one request away from the plaintext password. An attacker could capture the Authorization header and replay it. HTTPS encrypts the entire HTTP transmission, making it unreadable. Always use HTTPS in production for Basic Auth.

### Q8: How does the framework know which roles a user has?

**Answer:**
When CustomUserDetailsService loads a user from MongoDB, it converts the user's roles (List<String>) to Spring Security's GrantedAuthority objects. These authorities are stored in the Authentication object. Later, Spring Security can check if the user has required roles/authorities for accessing protected resources using annotations like @PreAuthorize or configuration in SecurityFilterChain.

---

## Best Practices

1. **Always use HTTPS** - Basic Auth sends credentials in every request
2. **Use BCrypt** - Never use MD5, SHA, or plaintext for passwords
3. **Stateless APIs** - Don't create sessions for APIs
4. **Organize packages** - security, config, entity, repository, service, controller
5. **Use Optional** - For nullable database queries
6. **Add logging** - DEBUG level for Spring Security to troubleshoot
7. **Validate input** - Check username/password constraints
8. **Use transactions** - For operations involving multiple collections
9. **Create indexes** - On frequently queried fields like username
10. **Document code** - Add comments explaining why, not what

---

## Running the Application

```bash
# Navigate to project
cd D:\Intellij-Project\Module

# Clean compile
mvn clean compile

# Run application
mvn spring-boot:run

# Application starts on http://localhost:8080
```

**Output:**
```
✓ Created 'module_entries' collection
✓ Created 'users' collection
✓ Created default admin user
  Username: admin
  Password: admin123 (BCrypt encoded in database)
  Role: ROLE_ADMIN
✓ Created default regular user
  Username: user
  Password: user123 (BCrypt encoded in database)
  Role: ROLE_USER

=== Application Initialized Successfully ===
```

---

## Summary

This system provides:
- ✅ Production-ready authentication
- ✅ MongoDB integration with Spring Data
- ✅ Spring Security 6 with modern API
- ✅ BCrypt password encoding
- ✅ Stateless HTTP Basic Authentication
- ✅ Proper layered architecture
- ✅ Complete documentation
- ✅ Ready for interviews/production

Happy coding! 🚀

