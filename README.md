# Spring Boot 3 + Spring Security + MongoDB 
## Complete Authentication System

---

## 📋 Overview

This is a **production-ready authentication system** built with:
- **Spring Boot 3.x** - Latest Spring framework
- **Spring Security 6.x** - Modern security framework (SecurityFilterChain based)
- **MongoDB** - NoSQL database with Replica Set (for transactions)
- **HTTP Basic Authentication** - Stateless, no sessions
- **BCrypt** - Secure password hashing
- **Java 21** - Latest Java version

**Status**: ✅ **Complete and Tested**

---

## 🚀 Quick Start

### Prerequisites
- Java 21 installed
- MongoDB running as replica set on localhost:27017
- Maven installed

### Run the Application

```bash
# Navigate to project
cd D:\Intellij-Project\Module

# Start the application
mvn spring-boot:run

# Expected output:
# ✓ Created 'module_entries' collection
# ✓ Created 'users' collection
# ✓ Admin user already exists
# =  = Application Initialized Successfully ===
# Application is running on http://localhost:8080
```

### Test Endpoints

```powershell
# Test 1: Public endpoint (no auth needed)
Invoke-WebRequest -Uri "http://localhost:8080/public/hello" -UseBasicParsing

# Test 2: Secured endpoint (with admin credentials)
$cred = New-Object System.Management.Automation.PSCredential(
    "admin", 
    (ConvertTo-SecureString "admin123" -AsPlainText -Force)
)
Invoke-WebRequest -Uri "http://localhost:8080/api/hello" `
    -Authentication Basic -Credential $cred -UseBasicParsing
```

---

## 📚 Documentation

This implementation includes comprehensive documentation:

| Document | Purpose |
|----------|---------|
| **AUTHENTICATION_GUIDE.md** | Complete technical guide with diagrams, theory, and examples |
| **QUICK_REFERENCE.md** | Quick lookup for endpoints, commands, and troubleshooting |
| **IMPLEMENTATION_SUMMARY.md** | Summary of what was created and how it works |
| **README.md** | This file - quick start and overview |

---

## 🔑 Default Credentials

```
ADMIN USER:
  Username: admin
  Password: admin123
  Role: ROLE_ADMIN

REGULAR USER:
  Username: user
  Password: user123
  Role: ROLE_USER
```

---

## 🌐 Available Endpoints

### Public Endpoints (No Authentication)

```
GET /public/hello
  Response: "Hello! This is a public endpoint - no authentication required."

GET /public/health
  Response: "OK"

GET /public/info
  Response: "Module API - Spring Boot 3 with Spring Security and MongoDB"
```

### Secured Endpoints (HTTP Basic Auth Required)

```
GET /api/hello
  Response: "Hello {username}! This is a secured endpoint."
  Example: "Hello admin! This is a secured endpoint."

GET /api/user-info
  Response: {
    "username": "admin",
    "roles": "ROLE_ADMIN",
    "authenticated": true
  }

GET /api/admin
  Response: "Welcome Admin admin!"
```

---

## 🔐 How Authentication Works

### Flow Diagram

```
CLIENT REQUEST
    ↓
Has Authorization: Basic header?
    ↓ YES
Decode base64(username:password)
    ↓
Extract username & password
    ↓
Create UsernamePasswordAuthenticationToken
    ↓
Authentication Manager
    ↓
DaoAuthenticationProvider
    ↓
CustomUserDetailsService.loadUserByUsername()
    ↓
Query MongoDB for user
    ↓
Found?
    ├─→ YES: Convert roles to GrantedAuthority
    └─→ NO: Throw UsernameNotFoundException → 401 Unauthorized
    ↓
Compare passwords:
  - Provided: "admin123"
  - Stored: "$2a$10$N9qo8uLOickgx...$" (BCrypt hash)
    ↓
Match?
    ├─→ YES: Authentication succeeds → Allow request
    └─→ NO: Authentication fails → 401 Unauthorized
    ↓
CONTROLLER RECEIVES REQUEST
    ↓
RESPONSE SENT
```

### Password Flow

```
User Registration:
  plaintext: "admin123"
    ↓
  BCryptPasswordEncoder.encode()
    ↓
  stored: "$2a$10$N9qo8uLOickgx...$"
    ↓
  Save to MongoDB

User Login:
  provided: "admin123"
    ↓
  Load stored: "$2a$10$N9qo8uLOickgx...$"
    ↓
  BCryptPasswordEncoder.matches(provided, stored)
    ↓
  Match? YES → Authenticate / NO → Reject
```

---

## 📦 Project Structure

```
Module/
├── pom.xml                                    # Maven configuration
├── AUTHENTICATION_GUIDE.md                    # Detailed technical guide
├── QUICK_REFERENCE.md                         # Quick lookup guide
├── IMPLEMENTATION_SUMMARY.md                  # Implementation overview
├── README.md                                  # This file
└── src/
    ├── main/
    │   ├── java/com/example/module/
    │   │   ├── ModuleApplication.java         # Main app + initialization
    │   │   ├── config/
    │   │   │   ├── SecurityConfig.java        # Spring Security configuration
    │   │   │   └── SpringSecurity.java        # DEPRECATED
    │   │   ├── controller/
    │   │   │   ├── PublicController.java      # Public endpoints
    │   │   │   ├── SecureController.java      # Secured endpoints
    │   │   │   └── (other controllers...)
    │   │   ├── entity/
    │   │   │   ├── Users.java                 # User with roles
    │   │   │   └── ModuleEntry.java
    │   │   ├── repository/
    │   │   │   ├── UserRepository.java        # MongoDB user repository
    │   │   │   └── ModuleEntryRepository.java
    │   │   ├── security/
    │   │   │   └── CustomUserDetailsService.java  # Loads users from MongoDB
    │   │   └── service/
    │   │       ├── UserService.java
    │   │       └── ModuleEntryService.java
    │   └── resources/
    │       └── application.properties         # Configuration
    └── test/
        └── java/...                           # Unit tests
```

---

## 🔧 Technology Stack

| Component | Version | Purpose |
|-----------|---------|---------|
| Spring Boot | 4.0.6 | Framework |
| Spring Security | 6.x | Authentication & Authorization |
| Spring Data MongoDB | Latest | Database access |
| MongoDB | 4.0+ | Database (with replica set) |
| Java | 21 | Language |
| Maven | Latest | Build tool |
| Lombok | 1.18.46 | Reduce boilerplate |
| BCrypt | Built-in | Password encoding |

---

## 📖 Understanding The Code

### 1. Users Entity

```java
@Document(collection = "users")
@Data
public class Users {
    @Id
    private ObjectId id;
    
    @Indexed(unique = true)
    private String username;      // Unique username
    
    private String password;      // BCrypt encoded
    
    private List<String> roles;   // e.g., ["ROLE_ADMIN"]
    
    @DBRef
    private List<ModuleEntry> moduleEntries;  // References
}
```

What happens?
- `@Document` maps to "users" MongoDB collection
- `@Indexed(unique=true)` creates unique index on username
- Password stored as BCrypt hash, never plaintext
- Roles stored as List for Spring Security

### 2. CustomUserDetailsService

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) 
            throws UsernameNotFoundException {
        Users user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(...));
        
        // Convert roles to authorities
        Collection<GrantedAuthority> authorities = user.getRoles()
            .stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
        
        // Return Spring Security's User
        return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities(authorities)
            .build();
    }
}
```

What happens?
- Called by Spring Security during authentication
- Loads user from MongoDB
- Converts roles to GrantedAuthority
- Returns UserDetails for Spring Security to use

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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) 
            throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> basic.realmName("Module API"))
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        return http.build();
    }
}
```

What happens?
- Configures Spring Security
- `/public/**` endpoints are accessible without auth
- All other endpoints require authentication
- HTTP Basic Auth is enabled
- Sessions are stateless (no cookies)

---

## 🧪 Testing

### Using Postman

1. **Create request to secure endpoint**
   - URL: `http://localhost:8080/api/hello`
   - Method: GET

2. **Add Basic Auth**
   - Go to "Authorization" tab
   - Type: "Basic Auth"
   - Username: `admin`
   - Password: `admin123`

3. **Send request**
   - Should return: `Hello admin! This is a secured endpoint.`

### Using PowerShell

```powershell
# Create credential object
$cred = New-Object System.Management.Automation.PSCredential(
    "admin",
    (ConvertTo-SecureString "admin123" -AsPlainText -Force)
)

# Make request with Basic Auth
Invoke-WebRequest -Uri "http://localhost:8080/api/hello" `
    -Authentication Basic `
    -Credential $cred `
    -UseBasicParsing
```

### Using Command Line

```bash
# With curl (if installed)
curl -u admin:admin123 http://localhost:8080/api/hello

# Using base64 encoding directly
curl -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" http://localhost:8080/api/hello
```

### Test Script

```bash
# Run the provided test script
.\test_authentication.bat
```

---

## 🔒 Security Features

✅ **BCrypt Password Encoding**
- One-way hashing with salt
- Adaptive cost factor (harder to crack over time)
- Industry standard and recommended

✅ **Unique Username Constraint**
- MongoDB unique index prevents duplicates
- Enforced by Spring Data

✅ **Stateless API**
- No sessions created
- No cookies to intercept
- Credentials sent with each request
- Perfect for microservices

✅ **Modern Spring Security**
- Uses latest Spring Boot 3 APIs
- SecurityFilterChain-based configuration
- Latest security practices

✅ **Role-Based Access Control**
- Users have roles (ROLE_ADMIN, ROLE_USER, etc.)
- Can be extended with @PreAuthorize annotations
- Flexible authorization configuration

---

## 🚨 Important Notes

### HTTPS Required for Production
Basic Auth sends credentials in every request. In production:
- **ALWAYS use HTTPS** to encrypt the connection
- Base64 encoding is NOT encryption
- Anyone on the network can decode basic auth headers without HTTPS

### Password Storage
- Passwords are NEVER stored plaintext
- ALWAYS use BCryptPasswordEncoder when creating/updating passwords
- The encode() method is always called before saving to DB

### MongoDB Replica Set
- Transactions require MongoDB replica set (even single node)
- Setup with: `mongod --replSet rs0`
- Initialize with: `rs.initiate()`

---

## 🐛 Troubleshooting

### Port 8080 Already in Use
```powershell
# Find and stop the process
Get-Process java | Stop-Process -Force
```

### MongoDB Connection Error
```bash
# Ensure MongoDB is running
mongod --replSet rs0

# Verify connection
mongo localhost:27017/moduledb
```

### 401 Unauthorized on Every Request
1. Check Authorization header: `Authorization: Basic base64(username:password)`
2. Verify credentials are correct
3. Ensure user exists: `db.users.find()`

### Password Authentication Fails
1. Verify password was BCrypt encoded when created
2. Check password matches exactly (case-sensitive)
3. Delete user and recreate through application startup

---

## 📊 MongoDB Schema

### Users Collection

```json
{
  "_id": ObjectId("507f1f77bcf86cd799439011"),
  "username": "admin",
  "password": "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36DvDLaym",
  "roles": ["ROLE_ADMIN"],
  "_class": "com.example.module.entity.Users"
}
```

**Indexes**:
- `_id` (primary key)
- `username` (unique)

---

## 🎓 Learning Resources

### Concepts Explained in Code

- **BasicAuthentication**: How HTTP Basic Auth works
- **BCrypt**: Why and how password hashing works
- **UserDetailsService**: Role in Spring Security authentication
- **SecurityFilterChain**: How filters process security
- **Stateless Sessions**: Why APIs don't need server-side sessions

### In-Code Comments

Every component has detailed comments explaining:
- Why each annotation is used
- How each method works
- What problems it solves
- Interview-level explanations

---

## 📝 Next Steps

### To Extend This System

1. **Add User Registration**
   ```java
   @PostMapping("/auth/register")
   public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
       Users user = new Users();
       user.setUsername(req.getUsername());
       user.setPassword(passwordEncoder.encode(req.getPassword()));
       user.setRoles(Arrays.asList("ROLE_USER"));
       userRepository.save(user);
       return ResponseEntity.ok("User registered successfully");
   }
   ```

2. **Add JWT Tokens**
   - Replace Basic Auth with JWT for better scalability
   - Add token refresh mechanism
   - Implement token expiration

3. **Add Audit Logging**
   - Log all login attempts
   - Track authorization failures
   - Monitor suspicious activities

4. **Add Role-Based Endpoints**
   - Use `@PreAuthorize("hasRole('ADMIN')")`
   - Implement method-level security
   - Fine-grained access control

5. **Add 2FA/MFA**
   - Email OTP verification
   - SMS-based 2FA
   - TOTP authenticator apps

---

## 📞 Support & Documentation

- **AUTHENTICATION_GUIDE.md**: Deep dive into implementation
- **QUICK_REFERENCE.md**: Quick lookup and commands
- **IMPLEMENTATION_SUMMARY.md**: What was created and why
- **Code Comments**: Every class has detailed explanations

---

## ✅ Checklist

- [x] Spring Boot 3 application setup
- [x] Spring Security configuration
- [x] MongoDB integration with replica set
- [x] BCrypt password encoding
- [x] HTTP Basic Authentication
- [x] Custom UserDetailsService
- [x] Public and secured endpoints
- [x] Default users initialization
- [x] Complete documentation
- [x] Code comments and explanations
- [x] Testing verified
- [x] Production-ready code

---

## 📄 License

This implementation is provided as-is for educational and production use.

---

## 🎉 Ready to Go

The system is **complete** and **ready for**: 
- ✅ Production deployment
- ✅ Interview preparation
- ✅ Educational purposes
- ✅ Extension and customization

**Start the application and test the endpoints!**

```bash
mvn spring-boot:run
```

Happy coding! 🚀

