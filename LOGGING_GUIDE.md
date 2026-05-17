# 🚀 Spring Boot Logging Setup Guide

## Overview
This guide explains the complete logging setup using **Lombok `@Slf4j`** and **Logback** in your Spring Boot application.

---

## 📋 What You Have Now

### 1. **logback.xml** (Configuration File)
   - **Location**: `src/main/resources/logback.xml`
   - **Purpose**: Configures how logs are captured and stored
   - **Features**: Console output + Rolling file logging

### 2. **AuthController.java** (Application Code)
   - **Annotation**: `@Slf4j` added to the class
   - **Log Statements**: Examples of `log.info()`, `log.warn()`, and `log.error()`

### 3. **Lombok Dependency** (Already in pom.xml)
   - Automatically generates logger instance for every class with `@Slf4j`

---

## 🎯 How Logging Works (Simple Explanation)

```
1. Application Code
   log.info("User registered successfully")
         ↓
2. @Slf4j (Lombok)
   Passes to Logger
         ↓
3. Logback Configuration
   Decides: Where to send? Console? File?
         ↓
4. Appenders (Output Destinations)
   CONSOLE APPENDER → Prints to console
   FILE APPENDER    → Writes to file with rolling
         ↓
5. Final Output
   logs/module.log (current) + logs/2026/05/17/module-0.log.zip (archived)
```

---

## 📝 Configuration Breakdown

### **Log File Configuration**
```xml
<property name="LOG_FILE_PATH" value="logs"/>
<property name="LOG_FILE_NAME" value="module"/>
```
- **LOG_FILE_PATH**: Directory where logs are stored → `logs/`
- **LOG_FILE_NAME**: Name of the log file → `module`
- **Result**: Current log file = `logs/module.log`

---

### **Console Appender**
```xml
<appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
        <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %5p %logger{36} - %msg%n</pattern>
    </encoder>
</appender>
```

**What it does?**
- Outputs logs directly to your console (System.out)
- Useful during development to see logs in real-time

**Pattern Breakdown:**
| Pattern | Example | Meaning |
|---------|---------|---------|
| `%d{yyyy-MM-dd HH:mm:ss.SSS}` | `2026-05-17 10:30:45.123` | Timestamp |
| `%5p` | `INFO` | Log Level (5 chars wide) |
| `%logger{36}` | `AuthController` | Class name (max 36 chars) |
| `%msg` | `User registered successfully` | Log message |
| `%n` | (newline) | New line |

**Example Console Output:**
```
2026-05-17 10:30:45.123  INFO AuthController - Signup request received for username: john_doe
2026-05-17 10:30:46.456  INFO AuthController - User 'john_doe' registered successfully with role ROLE_USER
```

---

### **Rolling File Appender**
```xml
<appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>${LOG_FILE_PATH}/${LOG_FILE_NAME}.log</file>
    <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
        <fileNamePattern>${LOG_FILE_PATH}/%d{yyyy/MM/dd}/${LOG_FILE_NAME}-%i.log.zip</fileNamePattern>
        <maxFileSize>10MB</maxFileSize>
        <maxHistory>30</maxHistory>
        <totalSizeCap>500MB</totalSizeCap>
    </rollingPolicy>
</appender>
```

**What is Rolling?**
- Instead of one huge log file, creates multiple smaller files
- Old files are compressed (.zip) and archived
- Prevents disk space from being consumed by unlimited log files

**Configuration Details:**

| Property | Value | Meaning |
|----------|-------|---------|
| `maxFileSize` | `10MB` | When current log file reaches 10MB, create a new one |
| `fileNamePattern` | `logs/%d{yyyy/MM/dd}/${LOG_FILE_NAME}-%i.log.zip` | How to name archived logs |
| `maxHistory` | `30` | Keep logs for 30 days, delete older ones |
| `totalSizeCap` | `500MB` | Maximum total size of all logs (oldest deleted when exceeded) |

**File Structure Example:**
```
logs/
├── module.log (current log file)
├── 2026/05/17/
│   ├── module-0.log.zip (first rollover)
│   └── module-1.log.zip (second rollover)
├── 2026/05/16/
│   ├── module-0.log.zip
│   └── module-1.log.zip
└── 2026/05/15/
    └── module-0.log.zip
```

---

### **Logger Configuration**
```xml
<root level="INFO">
    <appender-ref ref="CONSOLE"/>
    <appender-ref ref="FILE"/>
</root>

<logger name="com.example.module" level="DEBUG">
    <appender-ref ref="CONSOLE"/>
    <appender-ref ref="FILE"/>
</logger>

<logger name="org.springframework" level="WARN"/>
<logger name="org.mongodb" level="WARN"/>
```

**Log Levels** (in order of detail):
| Level | Shows | Example Use |
|-------|-------|------------|
| `TRACE` | Everything (rarely used) | Very detailed internal debugging |
| `DEBUG` | Detailed info for debugging | Variable values, function calls |
| `INFO` | General informational | Application events, user actions |
| `WARN` | Warnings (something unexpected) | Deprecated features, unusual states |
| `ERROR` | Errors (something failed) | Exceptions, failed operations |

**Root Logger**: `level="INFO"`
- Applies to all loggers by default
- Shows INFO, WARN, ERROR messages
- Hides DEBUG and TRACE messages

**Application Logger**: `name="com.example.module" level="DEBUG"`
- Overrides root for your application package
- Shows more detailed DEBUG messages
- Useful for debugging your own code

**Framework Loggers**: `level="WARN"`
- Spring and MongoDB loggers set to WARN
- Reduces noise from framework logs
- Only show warnings and errors from frameworks

---

## 💻 Using @Slf4j in Your Code

### **What is @Slf4j?**
Lombok annotation that automatically creates a logger for you.

**Before @Slf4j** (Manual way):
```java
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
}
```

**With @Slf4j** (Automatic):
```java
@Slf4j
@RestController
public class AuthController {
    // log is automatically available!
}
```

---

## 📌 Log Statement Examples

### **In Modified AuthController.java:**

#### **1. Info Logs (General Information)**
```java
log.info("Signup request received for username: {}", user.getUsername());
log.info("User '{}' registered successfully with role ROLE_USER", user.getUsername());
log.info("User '{}' logged in successfully", user.getUsername());
```
**When to use?**
- Application events (user signup, login, data saved)
- Important milestones
- Visible in INFO level and above

**Console Output:**
```
2026-05-17 10:30:45.123  INFO com.example.module.controller.AuthController - Signup request received for username: john_doe
```

---

#### **2. Debug Logs (Detailed Debugging)**
```java
log.debug("Attempting to authenticate user: {}", user.getUsername());
log.debug("Authentication successful for user: {}", user.getUsername());
```
**When to use?**
- Detailed intermediate steps
- Variable values for debugging
- Only shown if logger level is DEBUG or TRACE

**Console Output:** (If level="DEBUG")
```
2026-05-17 10:30:46.100  DEBUG com.example.module.controller.AuthController - Attempting to authenticate user: john_doe
2026-05-17 10:30:46.456  DEBUG com.example.module.controller.AuthController - Authentication successful for user: john_doe
```

---

#### **3. Warn Logs (Warnings)**
```java
log.warn("Warning message: Username '{}' already exists in the system", user.getUsername());
log.warn("Warning message: Login failed for username: {} - Reason: {}", user.getUsername(), e.getMessage());
```
**When to use?**
- Unexpected but recoverable situations
- Using deprecated features
- Invalid input from user

**Console Output:**
```
2026-05-17 10:30:47.200  WARN com.example.module.controller.AuthController - Warning message: Username 'john_doe' already exists in the system
```

---

#### **4. Error Logs (Errors with Stack Trace)**
```java
log.error("Error occurred while registering user '{}': {}", user.getUsername(), e.getMessage(), e);
log.error("Error occurred during login attempt for user '{}': {}", user.getUsername(), e.getMessage(), e);
```
**When to use?**
- Exceptions and errors
- Failed operations
- Pass the exception object (3rd parameter) to capture stack trace

**Console Output:**
```
2026-05-17 10:30:48.300  ERROR com.example.module.controller.AuthController - Error occurred while registering user 'john_doe': Connection timeout
java.sql.SQLException: Connection timeout
    at com.example.module.service.UserService.saveNewUser(UserService.java:45)
    at com.example.module.controller.AuthController.signup(AuthController.java:52)
    ...
```

---

## 🎬 Running and Testing

### **1. Run the Application**
```bash
mvn spring-boot:run
```

### **2. Trigger Logs**

**Signup Request:**
```bash
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```

**Login Request:**
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```

### **3. Check Logs**

**Console Output** (Real-time):
```
Logs appear in your IDE console immediately
```

**File Output**:
```
logs/module.log
logs/2026/05/17/module-0.log.zip (after 10MB or new day)
```

---

## 🔧 Common Customizations

### **Change Log Level for a Package**
```xml
<logger name="com.example.module.service" level="TRACE"/>
```

### **Disable Logging for Spring Boot Startup**
```xml
<logger name="org.springframework.boot" level="ERROR"/>
```

### **Change File Size Limit**
```xml
<maxFileSize>50MB</maxFileSize>  <!-- Increase from 10MB -->
```

### **Change Retention Period**
```xml
<maxHistory>90</maxHistory>  <!-- Keep 90 days instead of 30 -->
```

### **Change Log Pattern (Add Thread Name)**
```xml
<pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %5p [%thread] %logger{36} - %msg%n</pattern>
```

Output:
```
2026-05-17 10:30:45.123  INFO [main] AuthController - User registered successfully
```

---

## ✅ Summary

| Component | Purpose |
|-----------|---------|
| `logback.xml` | Configuration file for Logback |
| `@Slf4j` | Lombok annotation to auto-generate logger |
| `log.info()` | Log general information |
| `log.debug()` | Log detailed debugging info |
| `log.warn()` | Log warnings |
| `log.error()` | Log errors with stack trace |
| `CONSOLE Appender` | Outputs to console |
| `FILE Appender` | Writes to file with rolling |
| `maxFileSize` | When to rotate log files |
| `maxHistory` | How long to keep archived logs |
| `totalSizeCap` | Maximum total log storage |

---

## 🎓 Key Takeaways

1. **@Slf4j** = "Give me a logger automatically"
2. **log.info()** = "Note this important event"
3. **log.debug()** = "Show me details for debugging"
4. **log.warn()** = "Something unexpected happened"
5. **log.error()** = "An error occurred, save the stack trace"
6. **Rolling Logs** = "Keep logs manageable by rotating and compressing"
7. **logback.xml** = "Master control panel for all logging behavior"

---

## 📚 Next Steps

1. Run your application
2. Make signup/login requests
3. Check `logs/module.log` for output
4. Adjust log levels and patterns as needed
5. Add logging to other controllers and services

**Happy Logging! 🎉**

