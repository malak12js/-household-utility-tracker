Household Utility Tracker

A native Android application built with Java to help users track, manage, and calculate monthly household utility expenses.

Features

Authentication

LoginSignUpActivity

Provides user login and account registration through a sliding interface that allows users to switch between the login and sign-up screens.

Dashboard

HomeActivity

Provides a dark-themed home screen with a personalized welcome message, monthly expense summaries, and quick navigation options.

Add Expense

AddUtilityActivity

Allows users to record household utility expenses by entering the relevant information, including the date, utility details, optional notes, and cost in Iraqi Dinar (IQD).

Records View

ViewUtilityActivity

Displays previously recorded utility expenses in a structured list, allowing users to review their household utility records.

Utility Calculator

UtilityCalculatorActivity

Provides a dedicated calculator for estimating monthly utility costs across different categories:

* Electricity
* Water
* Internet
* Gas

Tech Stack

* Language: Java
* Platform: Android
* Minimum SDK: 24
* Target SDK: 36
* Compile SDK: 36
* UI: XML layouts
* Theme: Custom dark theme
* Build System: Gradle with Kotlin DSL
* Dependency Management: Version Catalogs (libs.versions.toml)
* Database: SQLite

Project Structure

HouseholdUtilityTracker/
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/example/myapplication/
│           └── res/
│               ├── drawable/
│               ├── layout/
│               └── values/
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── gradlew

Project Overview

The Household Utility Tracker was developed as an Android application for recording and monitoring recurring household utility expenses.

The application combines expense tracking, monthly cost calculation, utility categorization, and a user-friendly interface to help users manage their household expenses efficiently.

Utility Categories

The application supports the following household utility categories:

* Electricity
* Water
* Internet
* Gas

Project Information

Project: Household Utility Tracker
Platform: Android
Language: Java
Development Environment: Android Studio
