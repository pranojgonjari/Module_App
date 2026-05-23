# Sentiment Module - Application Workflow

This document explains how to run the application, its internal workflow, and how to verify the outputs.

## 1. How to Run the Application

### Prerequisites
- **Java 21**
- **Maven 3.x**
- **MongoDB** (Running on `localhost:27017` with a replica set named `rs0` for transaction support)

### Steps to Run
1.  **Configure MongoDB**: Ensure MongoDB is running and the connection URI in `application.properties` matches your setup.
2.  **Configure Mailtrap**: The `application.properties` file is already configured with Mailtrap SMTP settings.
3.  **Build the Project**:
    ```bash
    mvn clean install
    ```
4.  **Run the Application**:
    ```bash
    mvn spring-boot:run
    ```

## 2. Application Workflow

### Core Components
- **Sentiment Enum**: Located in `com.example.module.enums.Sentiment`. Values: `HAPPY`, `SAD`, `ANGRY`, `ANXIOUS`.
- **Entity**: `ModuleEntry` now includes a `sentiment` field.
- **Scheduler**: `UserScheduler` runs every 10 minutes (`0 0/10 * ? * *`) to perform periodic tasks.
- **Cache**: Caching is enabled via `@EnableCaching` in `ModuleApplication` to improve performance.
- **Email Service**: `EmailService` handles sending emails using the Mailtrap SMTP server.

### Workflow Example: Creating a Module Entry with Sentiment
1.  **API Call**: Send a POST request to `/module/{username}` with a JSON body:
    ```json
    {
      "title": "My Day",
      "content": "I had a great day today!",
      "sentiment": "HAPPY"
    }
    ```
2.  **Processing**: The `ModuleEntryControllerV2` receives the request and saves the entry to MongoDB.
3.  **Scheduled Task**: Every 10 minutes, the `UserScheduler` triggers `fetchUsersAndSendMail()`.
4.  **Email Notification**: The scheduler can be configured to use `EmailService` to send a summary of user activities to your Mailtrap inbox.

## 3. How to Get the Output

### Console Logs
You will see the scheduler execution logs in the console:
```text
Executing scheduled task: fetchUsersAndSendMail
```

### Database Output
Verify the saved entries with sentiment in MongoDB:
```bash
db.module_entries.find()
```

### Email Output (Mailtrap)
1.  Log in to your [Mailtrap account](https://mailtrap.io/).
2.  Go to your **Inboxes** and select the sandbox inbox.
3.  You will see the emails sent by the application.

### Running Tests
To verify the scheduler logic using JUnit 5 and Mockito:
```bash
mvn test -Dtest=UserSchedulerTest
```
