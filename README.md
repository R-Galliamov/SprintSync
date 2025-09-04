# 🏃 SprintSync – Run Tracker (Android)

**Status:** In testing  

---

## 📱 Overview
SprintSync is a running tracker that records **distance, pace, duration, calories**, and maps your route.  
It supports **pause/resume**, a **foreground tracking service** with ongoing notification, and **clear post-run stats**.

---

## 🎯 Problem & Audience
Self-initiated fitness app for **recreational runners** who want **lightweight tracking without accounts**.  
Focus: **accurate on-device tracking** and **clear post-run stats**.

---

## 👤 Role & Ownership
- Solo developer  
- Designed in **Figma**  
- Defined **IA and navigation**  
- Implemented **Clean Architecture + MVVM**  
- Wrote all code  

---

## 🏗️ Architecture
- **Patterns:** Clean Architecture, MVVM  
- **Layers:** Core, Data, Domain, Presentation (feature sub-packages; single-module project)  
- **Navigation:** Android Jetpack Navigation Component  
- **Long-running work:** Foreground Service for workout tracking with ongoing notification  
- **Data:** Room for local storage, DataStore for preferences  

---

## ✨ Key Features
- Start, pause, resume, stop workouts  
- Real-time GPS tracking via **foreground service** with ongoing notification  
- **Route visualization** on Google Maps  
- Metrics: distance, pace, duration, calories  
- **Post-run details** with pace graph  
- Run history with **weekly and all-time stats and bests**  
- User parameters for **calorie estimates**  
- **Offline storage** with Room and DataStore  
- **Pace smoothing** (custom analyzer reduces spikes samples before graphing and aggregations, runs in domain layer pipeline)

---

## 🛠 Tech Stack
- **Language:** Kotlin  
- **Architecture:** Clean Architecture, MVVM  
- **Concurrency:** Coroutines, Flow  
- **Dependency Injection:** Hilt  
- **Navigation:** Jetpack Navigation Component  
- **Database:** Room  
- **Preferences:** DataStore  
- **Location:** Google Play Services Location  
- **Maps:** Google Maps SDK for Android  
- **UI:** Views/XML with Material Design  
- **Charts:** MPAndroidChart  
- **Imaging:** Coil  
- **JSON:** Gson  
- **Logging:** Timber  
- **Testing:** JUnit 4/5  
- **Static analysis:** ktlint, detekt  
- **Firebase:** Crashlytics  
- **Play Services:** Base, Maps  
