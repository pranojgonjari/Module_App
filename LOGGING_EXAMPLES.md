# 📺 Logging Output Examples

## Console Log Examples

These are the exact logs you'll see in your IDE console when running the application.

---

## 🔐 User Signup Flow

### Request:
```bash
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"alice_smith","password":"secret123","email":"alice@example.com"}'
```

### Expected Console Output:
```
2026-05-17 14:22:10.345  INFO com.example.module.controller.AuthController - Signup request received for username: alice_smith
2026-05-17 14:22:10.678  INFO com.example.module.controller.AuthController - User 'alice_smith' registered successfully with role ROLE_USER
```

### What Each Log Means:
| Log | Level | Meaning |
|-----|-------|---------|
| "Signup request received..." | INFO | Someone initiated signup |
| "User '...' registered successfully..." | INFO | Registration completed successfully |

---

## 🔐 User Login Flow - **SUCCESSFUL LOGIN**

### Request:
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice_smith","password":"secret123"}'
```

### Expected Console Output:
```
2026-05-17 14:22:15.120  INFO com.example.module.controller.AuthController - Login request received for username: alice_smith
2026-05-17 14:22:15.234  DEBUG com.example.module.controller.AuthController - Attempting to authenticate user: alice_smith
2026-05-17 14:22:15.456  DEBUG com.example.module.controller.AuthController - Authentication successful for user: alice_smith
2026-05-17 14:22:15.567  INFO com.example.module.controller.AuthController - User 'alice_smith' logged in successfully
```

### What Each Log Means:
| Log | Level | Meaning |
|-----|-------|---------|
| "Login request received..." | INFO | Login attempt started |
| "Attempting to authenticate..." | DEBUG | Username/password validation starting |
| "Authentication successful..." | DEBUG | Credentials were correct |
| "User '...' logged in successfully" | INFO | Session created, login complete |

---

## ⚠️ Login Flow - **DUPLICATE USERNAME (Signup Second Time)**

### Request (Trying to register same username again):
```bash
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"alice_smith","password":"newpassword","email":"alice2@example.com"}'
```

### Expected Console Output:
```
2026-05-17 14:22:20.100  INFO com.example.module.controller.AuthController - Signup request received for username: alice_smith
2026-05-17 14:22:20.212  WARN com.example.module.controller.AuthController - Warning message: Username 'alice_smith' already exists in the system
```

### What Each Log Means:
| Log | Level | Meaning |
|-----|-------|---------|
| "Signup request received..." | INFO | Signup attempt initiated |
| "Warning message: Username '...' already exists..." | WARN | Username taken, registration rejected |

---

## ❌ Login Flow - **INVALID PASSWORD**

### Request (Wrong password):
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice_smith","password":"wrongpassword"}'
```

### Expected Console Output:
```
2026-05-17 14:22:25.100  INFO com.example.module.controller.AuthController - Login request received for username: alice_smith
2026-05-17 14:22:25.234  DEBUG com.example.module.controller.AuthController - Attempting to authenticate user: alice_smith
2026-05-17 14:22:25.500  WARN com.example.module.controller.AuthController - Warning message: Login failed for username: alice_smith - Reason: Bad credentials
2026-05-17 14:22:25.501  ERROR com.example.module.controller.AuthController - Error occurred during login attempt for user 'alice_smith': Bad credentials
org.springframework.security.authentication.BadCredentialsException: Bad credentials
    at org.springframework.security.authentication.dao.DaoAuthenticationProvider.additionalAuthenticationChecks(DaoAuthenticationProvider.java:187)
    at org.springframework.security.authentication.dao.DaoAuthenticationProvider.authenticate(DaoAuthenticationProvider.java:143)
    at com.example.module.controller.AuthController.login(AuthController.java:70)
    at java.base/java.lang.reflect.Method.invoke(Method.java:566)
    [... more stack trace ...]
```

### What Each Log Means:
| Log | Level | Meaning |
|-----|-------|---------|
| "Login request received..." | INFO | Login attempt started |
| "Attempting to authenticate..." | DEBUG | Username/password validation starting |
| "Warning message: Login failed..." | WARN | Credentials incorrect |
| "Error occurred during login attempt..." | ERROR | Full error with stack trace |

---

## 💾 File Log Examples

### Location: `logs/module.log`

```
2026-05-17 14:22:10.345  INFO com.example.module.controller.AuthController - Signup request received for username: alice_smith
2026-05-17 14:22:10.678  INFO com.example.module.controller.AuthController - User 'alice_smith' registered successfully with role ROLE_USER
2026-05-17 14:22:15.120  INFO com.example.module.controller.AuthController - Login request received for username: alice_smith
2026-05-17 14:22:15.234  DEBUG com.example.module.controller.AuthController - Attempting to authenticate user: alice_smith
2026-05-17 14:22:15.456  DEBUG com.example.module.controller.AuthController - Authentication successful for user: alice_smith
2026-05-17 14:22:15.567  INFO com.example.module.controller.AuthController - User 'alice_smith' logged in successfully
2026-05-17 14:22:20.100  INFO com.example.module.controller.AuthController - Signup request received for username: alice_smith
2026-05-17 14:22:20.212  WARN com.example.module.controller.AuthController - Warning message: Username 'alice_smith' already exists in the system
2026-05-17 14:22:25.100  INFO com.example.module.controller.AuthController - Login request received for username: alice_smith
2026-05-17 14:22:25.234  DEBUG com.example.module.controller.AuthController - Attempting to authenticate user: alice_smith
2026-05-17 14:22:25.500  WARN com.example.module.controller.AuthController - Warning message: Login failed for username: alice_smith - Reason: Bad credentials
2026-05-17 14:22:25.501  ERROR com.example.module.controller.AuthController - Error occurred during login attempt for user 'alice_smith': Bad credentials
```

---

## 📊 Log Level Color Guide (In Most IDEs)

| Level | Color | Meaning |
|-------|-------|---------|
| INFO | 🟢 Green | Normal operation, important events |
| DEBUG | 🔵 Blue | Detailed debugging information |
| WARN | 🟡 Yellow | Warning about unusual situations |
| ERROR | 🔴 Red | Error with full stack trace |

---

## 🔍 Understanding Log Pattern Format

```
2026-05-17 14:22:10.345  INFO com.example.module.controller.AuthController - Signup request received...
└─ Timestamp               └─ Level └─ Class Name                               └─ Message
```

### Pattern Breakdown:

```xml
<pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %5p %logger{36} - %msg%n</pattern>
```

| Pattern Element | Example | Meaning |
|-----------------|---------|---------|
| `%d{...}` | `2026-05-17 14:22:10.345` | Date and time with milliseconds |
| `%5p` | `INFO` | Log level (5 characters wide) |
| `%logger{36}` | `com.example.module...AuthController` | Class name (max 36 chars) |
| `-` | `-` | Separator |
| `%msg` | `Signup request received...` | The actual log message |
| `%n` | (newline) | New line character |

---

## 📈 Simulating Application Activity

### Scenario: One day of normal application usage

```bash
# Run multiple requests over time
# Each creates new log entry

# 14:22 - First user signup
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"pass1"}'

# 14:23 - First user login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"pass1"}'

# 14:25 - Second user signup
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"user2","password":"pass2"}'

# 14:26 - Second user login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user2","password":"pass2"}'

# 14:27 - Another user tries to signup with existing username
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"pass123"}'
```

### Result in `logs/module.log`:

```
2026-05-17 14:22:10.100  INFO com.example.module.controller.AuthController - Signup request received for username: user1
2026-05-17 14:22:11.200  INFO com.example.module.controller.AuthController - User 'user1' registered successfully with role ROLE_USER
2026-05-17 14:23:05.100  INFO com.example.module.controller.AuthController - Login request received for username: user1
2026-05-17 14:23:05.150  DEBUG com.example.module.controller.AuthController - Attempting to authenticate user: user1
2026-05-17 14:23:05.300  DEBUG com.example.module.controller.AuthController - Authentication successful for user: user1
2026-05-17 14:23:05.400  INFO com.example.module.controller.AuthController - User 'user1' logged in successfully
2026-05-17 14:25:20.100  INFO com.example.module.controller.AuthController - Signup request received for username: user2
2026-05-17 14:25:21.200  INFO com.example.module.controller.AuthController - User 'user2' registered successfully with role ROLE_USER
2026-05-17 14:26:15.100  INFO com.example.module.controller.AuthController - Login request received for username: user2
2026-05-17 14:26:15.150  DEBUG com.example.module.controller.AuthController - Attempting to authenticate user: user2
2026-05-17 14:26:15.300  DEBUG com.example.module.controller.AuthController - Authentication successful for user: user2
2026-05-17 14:26:15.400  INFO com.example.module.controller.AuthController - User 'user2' logged in successfully
2026-05-17 14:27:10.100  INFO com.example.module.controller.AuthController - Signup request received for username: user1
2026-05-17 14:27:10.200  WARN com.example.module.controller.AuthController - Warning message: Username 'user1' already exists in the system
```

---

## 🎯 Key Observations

### **What You DON'T See (Filtered Out)**
- Spring Framework logs (TRACE, DEBUG, INFO)
- Hibernate logs (TRACE, DEBUG, INFO)
- MongoDB logs (TRACE, DEBUG, INFO)

**Why?** Set to WARN level to reduce noise:
```xml
<logger name="org.springframework" level="WARN"/>
<logger name="org.mongodb" level="WARN"/>
```

### **What You DO See (All Levels)**
- Your application logs (com.example.module) - ALL levels shown
- Important framework errors/warnings - WARN and ERROR only

---

## 📁 Archived Logs Example

After 10MB, logs are automatically rotated:

```
logs/
├── module.log (current - growing)
├── 2026/05/17/
│   ├── module-0.log.zip (First 10MB on May 17)
│   ├── module-1.log.zip (Second 10MB on May 17)
│   └── module-2.log.zip (Third 10MB on May 17)
├── 2026/05/16/
│   ├── module-0.log.zip (First 10MB on May 16)
│   └── module-1.log.zip (Second 10MB on May 16)
├── 2026/05/15/
│   ├── module-0.log.zip (May 15 logs)
│   └── module-1.log.zip
...and so on (up to 30 days)
```

Each .zip file contains compressed logs, saving disk space!

---

## ⚡ Performance Impact

**Logging Performance**: Minimal ✅
- Asynchronous by default in Logback
- File I/O doesn't block application
- Compression happens in background

**Disk Usage**: Controlled ✅
- 10MB per file maximum
- Auto-compressed to .zip
- Old files auto-deleted after 30 days
- Total cap: 500MB max

---

## 🔧 Troubleshooting: Not Seeing Expected Logs?

### Logs Not Appearing in Console?
1. Check IDE console is active
2. Verify `logback.xml` exists in `src/main/resources/`
3. Look for Logback configuration logs on startup
4. Check application log level isn't set to ERROR

### Want More DEBUG Logs?
```xml
<logger name="com.example.module" level="DEBUG"/>  <!-- Already set -->
```

### Want ONLY Errors?
```xml
<root level="ERROR">
    <appender-ref ref="CONSOLE"/>
    <appender-ref ref="FILE"/>
</root>
```

### Want All Details (Including Spring)?
```xml
<logger name="org.springframework" level="DEBUG"/>
```

---

## 💡 Best Practices

✅ **DO**:
- Log user actions (signup, login, logout)
- Log errors with full stack trace
- Use INFO level for important events
- Use DEBUG for intermediate steps in complex logic
- Include variable values: `log.info("User: {}", username)`

❌ **DON'T**:
- Log passwords or sensitive data
- Log too much (causes large files)
- Use ERROR for warnings
- Use DEBUG for INFO-level messages
- Leave DEBUG logs in production (increase to WARN)

---

## 📞 Quick Help

**I don't see any logs!**
→ Check `logs/` directory exists and has write permissions

**Log file is too large!**
→ It's auto-managed (rotates at 10MB and compresses)

**Want to disable file logging?**
```xml
<root level="INFO">
    <appender-ref ref="CONSOLE"/>  <!-- Remove FILE ref -->
</root>
```

**Want to log exceptions?**
```java
log.error("Operation failed: {}", e.getMessage(), e);  // Pass exception as 3rd param
```

---

## 📊 Log Statistics

| Metric | Value |
|--------|-------|
| Current Log File | `logs/module.log` |
| Max File Size | 10 MB |
| Compression Format | .zip |
| Archive Pattern | `yyyy/MM/dd/module-N.log.zip` |
| Retention Days | 30 days |
| Max Total Size | 500 MB |
| Console Pattern | Timestamp Level Class - Message |
| File Pattern | Same as console |
| Root Level | INFO |
| App Package Level | DEBUG |
| Spring Level | WARN |

---

**Everything working? Great! Your logging is production-ready! 🎉**


