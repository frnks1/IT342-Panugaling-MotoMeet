# MotoMeet App Startup Guide

This document explains how to start the MotoMeet app in this repository on Windows.

## Prerequisites

- Java JDK installed (Java 17+ recommended)
- Android Studio or Android SDK for the mobile app
- Git and command-line tools
- Internet access for Gradle/Maven dependency downloads

---

## 1. Start the backend server (web)

The backend is located in the `web/` folder.

### A. Load database environment variables

The database settings are stored in `web/.env`.

Open PowerShell and run:

```powershell
cd "c:\Users\jhaja\OneDrive\Desktop\IT342-Panugaling-MotoMeet\web"
$env:DB_URL = "jdbc:postgresql://aws-1-ap-northeast-1.pooler.supabase.com:5432/postgres?sslmode=require"
$env:DB_USER = "postgres.raxibifeuvngmewzvour"
$env:DB_PASSWORD = "Jhajan1231."
```

> Note: These values come from `web/.env`.

### B. Run the backend

Use the Maven wrapper:

```powershell
cd "c:\Users\jhaja\OneDrive\Desktop\IT342-Panugaling-MotoMeet\web"
.\mvnw spring-boot:run
```

If the server starts successfully, it will run on `http://localhost:8080` by default.

---

## 2. Start the mobile app

The mobile app is located in the `mobile/` folder.

### A. Using Android Studio

1. Open Android Studio.
2. Choose `Open` and select the `mobile/` folder.
3. Let Gradle sync complete.
4. Run the app on a connected device or emulator.

### B. Using the Gradle wrapper from PowerShell

```powershell
cd "c:\Users\jhaja\OneDrive\Desktop\IT342-Panugaling-MotoMeet\mobile"
.\gradlew clean assembleDebug
```

After the build succeeds, you can install the APK on a device or open the project in Android Studio.

---

## 3. Notes

- If the backend uses environment variables from `.env`, make sure they are exported before starting Maven.
- If port `8080` is already in use, stop the process using that port or change the backend port in Spring Boot configuration.
- The mobile app uses the backend APIs, so start the backend before launching the mobile app if you want full functionality.

---

## 4. Optional web admin page

There is also a web admin page in the repository:

- `web/admin_page.html`
- `web/admin_page_live.html`

These can be opened directly in a browser or served by the backend if configured.
