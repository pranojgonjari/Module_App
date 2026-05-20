# 📧 EMAIL EXECUTION GUIDE - COMPLETE WALKTHROUGH

## ❌ WHAT WAS WRONG

The issue is that **you created the EmailService but never called it!**

Think of it like this:
- ✅ You have a mailbox (EmailService)
- ❌ But no way to put mail in it (No endpoint to call it)

The application was running, but nobody was actually triggering the email sending.

---

## ✅ NOW IT'S FIXED - COMPLETE EMAIL FLOW

### **1️⃣ HOW IT WORKS (Step by Step)**

```
User (You)
    ↓
    [Makes HTTP Request to /email/send]
    ↓
EmailController (Receives request)
    ↓
    [Extracts email parameters: to, subject, message]
    ↓
EmailService (Business logic)
    ↓
    [Creates SimpleMailMessage with your data]
    ↓
JavaMailSender (Spring's mail client)
    ↓
    [Connects to Gmail SMTP: smtp.gmail.com:587]
    ↓
Gmail Server
    ↓
    [Authenticates with: jethajibabitaji8436@gmail.com]
    ↓
[Email Delivered to Recipient]
```

---

## 🚀 HOW TO ACTUALLY SEND THE EMAIL

### **STEP 1: Start Your Application**

```bash
cd D:\Intellij-Project\Module
mvn clean spring-boot:run
```

You'll see logs like:
```
INFO  - Started ModuleApplication in 4.2 seconds
INFO  - Tomcat started on port(s): 8080
```

**✅ Application is running**

---

### **STEP 2: Open Another Terminal/Command Prompt**

Leave the spring-boot:run terminal running. Open a NEW terminal window.

---

### **STEP 3: Send Email Using the Endpoint**

#### **Option A: Using Query Parameters**

```bash
curl -X POST "http://localhost:8080/email/send?to=jethajibabitaji8436@gmail.com&subject=Test Mail&message=Jevlas Kay"
```

**Or in PowerShell:**
```powershell
$url = "http://localhost:8080/email/send?to=jethajibabitaji8436@gmail.com&subject=Test Mail&message=Jevlas Kay"
Invoke-WebRequest -Uri $url -Method POST
```

#### **Option B: Using JSON Request Body**

```bash
curl -X POST http://localhost:8080/email/send-json \
  -H "Content-Type: application/json" \
  -d '{"to":"jethajibabitaji8436@gmail.com","subject":"Test Mail","message":"Jevlas Kay"}'
```

**Or in PowerShell:**
```powershell
$body = @{
    to = "jethajibabitaji8436@gmail.com"
    subject = "Test Mail"
    message = "Jevlas Kay"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/email/send-json" `
  -Method POST `
  -ContentType "application/json" `
  -Body $body
```

---

### **STEP 4: Check Your Gmail Inbox**

1. Open browser: go to `https://gmail.com`
2. Login with: `jethajibabitaji8436@gmail.com`
3. Check **Inbox** for the email with subject "Test Mail" and message "Jevlas Kay"

---

## 🔍 HOW TO DEBUG IF EMAIL DOESN'T ARRIVE

### **Step 1: Check Application Logs**

Look at the terminal running `mvn spring-boot:run`. You should see:

```
INFO  EmailController - Email send request received - To: jethajibabitaji8436@gmail.com, Subject: Test Mail
INFO  EmailService - Sending email to: jethajibabitaji8436@gmail.com
INFO  EmailService - Email sent successfully
INFO  EmailController - Email sent successfully to: jethajibabitaji8436@gmail.com
```

**If you see these logs → Email was sent from your app ✅**

**If you don't see these logs:**
- Check if request actually reached the controller
- Verify endpoint URL is correct
- Check if application started successfully

---

### **Step 2: Check for Errors in Logs**

Look for ERROR lines like:
```
ERROR EmailService - Exception occurred: ...
ERROR MailSenderConfig - Failed to connect to SMTP: ...
ERROR EmailController - Failed to send email: ...
```

---

### **Step 3: Common Issues & Solutions**

#### **Issue 1: "Unable to connect to SMTP server"**

**Cause:** Gmail SMTP credentials are wrong or Gmail 2FA not setup

**Solution:**
1. Verify Gmail address: `jethajibabitaji8436@gmail.com` ✅
2. Verify app password: `nwoj ilyf czeb atuo` ✅
3. Check Gmail 2FA is enabled
4. In Gmail settings → Security → App passwords → Check "Mail" and "Windows" are selected

---

#### **Issue 2: "Authentication failed"**

**Cause:** App password missing spaces or wrong

**Solution:**
- The app password should have spaces: `nwoj ilyf czeb atuo` (not `nwojilyfczebatuo`)
- Check in `application.yml` at:
  ```yaml
  mail:
    smtp:
      password: nwoj ilyf czeb atuo
  ```

---

#### **Issue 3: Email sent but not received**

**Cause:** Email went to spam folder

**Solution:**
1. Check **Spam** folder in Gmail
2. Check **All Mail** folder
3. Wait 5-10 minutes (sometimes delays)
4. Check logs for actual error messages

---

#### **Issue 4: Endpoint not found (404 error)**

**Cause:** EmailController not loaded or application not started

**Solution:**
1. Verify application started: Look for "Started ModuleApplication"
2. Verify controller is in correct package: `com.example.module.controller`
3. Try health check first: `curl http://localhost:8080/email/health`

---

## 📋 COMPLETE EXECUTION CHECKLIST

- [ ] **Compile project:** `mvn clean compile` ✅
- [ ] **Run tests:** `mvn test` ✅ (should see 5 EmailServiceTest pass)
- [ ] **Start application:** `mvn spring-boot:run`
- [ ] **Wait for:** "Started ModuleApplication"
- [ ] **Open new terminal/PowerShell**
- [ ] **Test health:** `curl http://localhost:8080/email/health`
- [ ] **Send email:** Use curl command above
- [ ] **Check logs:** Look for "Email sent successfully"
- [ ] **Check Gmail:** Verify email received in inbox
- [ ] **Check Spam:** If not in inbox
- [ ] **Check All Mail:** If not in spam

---

## 🔧 QUICK TEST COMMANDS

### **PowerShell Commands (Windows)**

```powershell
# Test 1: Health Check
Invoke-WebRequest -Uri "http://localhost:8080/email/health" -Method GET

# Test 2: Send Simple Email
$url = "http://localhost:8080/email/send?to=jethajibabitaji8436@gmail.com&subject=Test Mail&message=Jevlas Kay"
Invoke-WebRequest -Uri $url -Method POST

# Test 3: Send JSON Email
$body = @{
    to = "jethajibabitaji8436@gmail.com"
    subject = "Test Mail"
    message = "Jevlas Kay"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/email/send-json" `
  -Method POST `
  -ContentType "application/json" `
  -Body $body -Verbose
```

---

## 📊 ARCHITECTURE FLOW

```
┌────────────────────────────────────────────────────────────┐
│                    YOUR REQUEST                            │
│  curl /email/send?to=...&subject=...&message=...          │
└────────────────────────────────────────────────────────────┘
                              ↓
┌────────────────────────────────────────────────────────────┐
│              EmailController (@RestController)             │
│  - Receives HTTP POST request                             │
│  - Extracts parameters (to, subject, message)             │
│  - Calls emailService.sendEmail()                         │
└────────────────────────────────────────────────────────────┘
                              ↓
┌────────────────────────────────────────────────────────────┐
│              EmailService (@Service)                       │
│  - Creates SimpleMailMessage                              │
│  - Sets: from, to, subject, text                          │
│  - Calls: javaMailSender.send(message)                   │
│  - Logs result                                            │
└────────────────────────────────────────────────────────────┘
                              ↓
┌────────────────────────────────────────────────────────────┐
│         MailSenderConfig (JavaMailSender Bean)             │
│  - Configured with Gmail SMTP settings                    │
│  - Host: smtp.gmail.com                                  │
│  - Port: 587 (TLS)                                       │
│  - Auth: jethajibabitaji8436@gmail.com                   │
│  - Password: nwoj ilyf czeb atuo                         │
└────────────────────────────────────────────────────────────┘
                              ↓
┌────────────────────────────────────────────────────────────┐
│              Gmail SMTP Server                             │
│  - Receives SimpleMailMessage                             │
│  - Authenticates with app password                        │
│  - Processes email                                        │
└────────────────────────────────────────────────────────────┘
                              ↓
┌────────────────────────────────────────────────────────────┐
│            Gmail Inbox (jethajibabitaji8436)              │
│  ✅ Email Delivered with:                                 │
│     - Subject: Test Mail                                 │
│     - Message: Jevlas Kay                               │
└────────────────────────────────────────────────────────────┘
```

---

## 📝 FILES CREATED/MODIFIED

### **NEW FILE CREATED:**
✅ `EmailController.java` - REST API endpoints for email sending
   - GET `/email/health` - Health check
   - POST `/email/send` - Send email (query parameters)
   - POST `/email/send-json` - Send email (JSON body)

### **EXISTING FILES:**
✅ `EmailService.java` - Business logic (already existed)
✅ `MailSenderConfig.java` - Gmail SMTP configuration (already existed)
✅ `application.yml` - Email properties (already configured)

---

## ✅ BUILD & VERIFICATION

```
mvn clean compile && mvn test
```

**Expected Output:**
```
[INFO] BUILD SUCCESS
[INFO] Tests run: 19, Failures: 0, Errors: 0
```

---

## 🎯 QUICK SUMMARY

| Step | Action | Command |
|------|--------|---------|
| 1 | Compile | `mvn clean compile` |
| 2 | Test | `mvn test` |
| 3 | Start App | `mvn spring-boot:run` |
| 4 | Health Check | `curl http://localhost:8080/email/health` |
| 5 | Send Email | `curl http://localhost:8080/email/send?to=...` |
| 6 | Check Gmail | Login to gmail.com & check inbox |

---

## 🎊 SUCCESS INDICATORS

✅ Application starts without errors
✅ Logs show "Email send request received"
✅ Logs show "Email sent successfully"
✅ Email appears in jethajibabitaji8436@gmail.com inbox
✅ Email has subject "Test Mail" and message "Jevlas Kay"

---

**Status: Ready to Send Emails! 🚀**

