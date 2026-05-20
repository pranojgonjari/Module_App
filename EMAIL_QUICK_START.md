# 📧 QUICK START - SEND EMAIL IN 5 MINUTES

## ✅ BUILD STATUS
- ✅ Compilation: SUCCESS
- ✅ All Tests: PASSED (19/19)
- ✅ EmailController: CREATED
- ✅ Ready to Send Emails: YES

---

## 🚀 STEP 1: START YOUR APPLICATION

Open Command Prompt / PowerShell and run:

```bash
cd D:\Intellij-Project\Module
mvn clean spring-boot:run
```

Wait for this message to appear:
```
Started ModuleApplication in 4.2 seconds
Tomcat started on port(s): 8080
```

✅ **Your application is now running!**

---

## 📧 STEP 2: SEND EMAIL

**Open a NEW Command Prompt/PowerShell** (keep the first one running)

### **Option A: Simple (Query Parameters)**

```powershell
$url = "http://localhost:8080/email/send?to=jethajibabitaji8436@gmail.com&subject=Test Mail&message=Jevlas Kay"
Invoke-WebRequest -Uri $url -Method POST
```

### **Option B: JSON Request**

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

**Expected Response:**
```
StatusCode        : 200
StatusDescription : OK
Content           : Email sent successfully to: jethajibabitaji8436@gmail.com
```

---

## 📧 STEP 3: CHECK YOUR EMAIL

1. Open: https://gmail.com
2. Login with: `jethajibabitaji8436@gmail.com`
3. Look for email with:
   - ✅ Subject: "Test Mail"
   - ✅ Message: "Jevlas Kay"
   - ✅ From: jethajibabitaji8436@gmail.com

---

## 🔍 HOW TO VERIFY IT WORKED

### **In First Terminal (mvn spring-boot:run):**

Look for these logs:
```
INFO  c.e.module.controller.EmailController - Email send request received
INFO  c.e.module.service.EmailService - Sending email to: jethajibabitaji8436@gmail.com
INFO  c.e.module.controller.EmailController - Email sent successfully to: jethajibabitaji8436@gmail.com
```

✅ **If these logs appear, your email was sent!**

### **In Gmail:**

✅ Check inbox for "Test Mail" message

---

## 🧪 TEST HEALTH CHECK (Optional)

Before sending, test the endpoint is working:

```powershell
Invoke-WebRequest -Uri "http://localhost:8080/email/health" -Method GET
```

Expected response: `Email service is running`

---

## 📋 COMPLETE EMAIL FLOW

```
You (Run PowerShell Command)
    ↓
POST http://localhost:8080/email/send?to=...
    ↓
EmailController (Receives Request)
    ↓
EmailService (Processes Email)
    ↓
MailSenderConfig (SMTP Settings)
    ↓
Gmail SMTP Server (Sends Email)
    ↓
✅ Email Delivered to Inbox
```

---

## ⚠️ IF EMAIL DOESN'T ARRIVE

### **Check 1: Application Logs**
- Do you see "Email sent successfully"?
- **NO** → Email sending failed (check error logs)
- **YES** → Email was sent, check next steps

### **Check 2: Gmail Inbox**
- Look in INBOX first
- **Not there?** Check SPAM folder
- **Still not there?** Wait 5-10 minutes

### **Check 3: Gmail Settings**
- Go to Gmail → Settings → Security
- Make sure 2FA is enabled
- Check app passwords list includes "Mail" and "Windows"

### **Check 4: Gmail Credentials**
- Email: `jethajibabitaji8436@gmail.com` ✅
- App Password: `nwoj ilyf czeb atuo` (with spaces) ✅
- Check in `application.yml`:
  ```yaml
  mail:
    smtp:
      username: jethajibabitaji8436@gmail.com
      password: nwoj ilyf czeb atuo
  ```

---

## 📝 DEFAULT CONFIGURATION

**From Email:** jethajibabitaji8436@gmail.com  
**To Email:** jethajibabitaji8436@gmail.com (or any email you want)  
**Subject:** Test Mail  
**Message:** Jevlas Kay  
**SMTP Host:** smtp.gmail.com  
**SMTP Port:** 587 (TLS)  

---

## 🛑 STOP APPLICATION

To stop the application:
1. Go to first terminal (mvn spring-boot:run)
2. Press: `Ctrl + C`

---

## 📚 RELATED FILES

- **EmailController.java** - REST API endpoints
  - `GET /email/health` - Health check
  - `POST /email/send` - Send email (query params)
  - `POST /email/send-json` - Send email (JSON body)

- **EmailService.java** - Business logic
  - `sendEmail()` - Send simple email
  - `sendEmailWithCc()` - Send with CC
  - `sendEmailWithCcBcc()` - Send with CC & BCC

- **MailSenderConfig.java** - Gmail SMTP configuration
  - Reads from `application.yml`
  - Sets up JavaMailSender bean

- **EmailServiceTest.java** - Unit tests (all passing ✅)

---

## 🎉 YOU'RE READY!

**Summary:**
1. ✅ Application is compiled and ready
2. ✅ All tests are passing
3. ✅ EmailController endpoint created
4. ✅ Gmail SMTP configured
5. ✅ Ready to send emails

**Next Step:** Run `mvn spring-boot:run` and send your first email! 🚀

---

## 💡 TIPS

- **Keep first terminal open** while running tests
- Use **new terminal** for sending requests
- **Gmail delay:** Sometimes 30 seconds to 1 minute
- **Check spam first** if email missing from inbox
- **Logs are your friend** - check them first if something fails

---

**Status: ✅ READY TO SEND EMAILS**

