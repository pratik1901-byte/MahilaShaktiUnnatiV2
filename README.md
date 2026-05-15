Mahila Shakti Unnati
Overview

Mahila Shakti Unnati is a Women Self Help Group (SHG) Management Android Application developed to digitally manage SHG operations such as member registration, savings tracking, loan management, repayment monitoring, and financial record maintenance.

The application is designed specifically for Women Self Help Groups operating in rural and semi-urban communities where financial records are often maintained manually using notebooks and paper registers.

The project aims to simplify SHG workflows, improve transparency, reduce manual paperwork, minimize calculation errors, and support women empowerment through digital financial management.

The application was developed using Kotlin, Jetpack Compose, MVVM Architecture, and Room Database following an offline-first approach.

Problem Statement

Most Women Self Help Groups still rely on traditional manual record keeping methods for:

Member registration
Savings tracking
Loan management
Repayment monitoring
Financial reporting

This creates several challenges such as:

Manual calculation errors
Difficulty tracking repayments
Duplicate records
Poor financial transparency
Time-consuming maintenance
Risk of losing paper records

Mahila Shakti Unnati was developed as a digital solution to address these problems using a simple and user-friendly Android application.

Objectives of the Project
Digitize Women SHG operations
Simplify savings and loan management
Reduce manual paperwork
Improve financial transparency
Provide better loan tracking and repayment monitoring
Support offline-first data management
Build a user-friendly mobile application for SHG operations
Support women empowerment through digital finance management
Features
1. Member Management

The application allows SHGs to digitally manage all member-related activities.

Features:
Add new members
Generate unique member IDs
Upload profile photos
Store nominee details
View member profiles
Search members
Filter members
Prevent duplicate accounts
Validations:
Aadhaar number validation
Phone number validation
Age validation
Duplicate account prevention
2. Savings Management

The application supports tracking weekly savings contributions made by SHG members.

Features:
Weekly savings entry tracking
Savings history management
Total savings calculation
Pending savings monitoring
Member-wise savings display
3. Loan Management

The application includes a complete loan tracking system for SHG financial activities.

Features:
Loan issuing
Repayment tracking
Remaining balance calculation
Due date monitoring
Overdue loan detection
Loan status tracking
Loan repayment progress display
4. Dashboard Analytics

The dashboard provides summarized SHG financial insights.

Dashboard Includes:
Total members
Active members
Total savings
Active loans
Pending savings
Available SHG fund
Financial health indicators
Loan recovery information
5. Admin Login System

The application includes an admin access system for secure management operations.

Features:
Restricted admin access
Secure management operations
Controlled access to sensitive features
Technologies Used
Technology	Purpose
Kotlin	Main programming language
Jetpack Compose	UI development
MVVM Architecture	Clean application architecture
Room Database	Offline local data storage
Material Design 3	Modern UI components
Android Studio	Development environment
Coil	Image loading
uCrop	Image cropping
Kotlin Flow / StateFlow	Reactive state management
Architecture

The project follows MVVM (Model-View-ViewModel) Architecture.

Model

Handles:

Room Database
Entities
DAO operations
Data storage
View

Handles:

UI screens
User interaction
Compose components
ViewModel

Handles:

Business logic
State management
Communication between UI and database

This architecture improves:

Code organization
Scalability
Maintainability
Separation of concerns
Database Structure

The project uses Room Database for offline local data storage.

Main Entities
MemberEntity

Stores:

Member details
Savings details
Nominee details
Profile image URI
LoanEntity

Stores:

Loan details
Repayment information
Due dates
Remaining amount
Loan status
SavingsEntryEntity

Stores:

Weekly savings records
Savings history
Project Structure
app/
 ├── ui/
 │    ├── dashboard/
 │    ├── members/
 │    ├── loans/
 │    ├── theme/
 │
 ├── database/
 │    ├── entities/
 │    ├── dao/
 │    ├── viewmodel/
 │
 ├── MainActivity.kt
Application Workflow
Member Registration Workflow
User opens Add Member screen
Member details are entered
Validation checks are performed
Duplicate account check is performed
Member data is stored in Room Database
Loan Workflow
Admin selects a member
Loan details are entered
Loan is issued and stored
Repayment tracking begins
Due dates and remaining balances are monitored
Savings Workflow
Weekly savings are entered
Savings records are updated
Dashboard analytics refresh automatically
Offline-First Approach

The application is designed using an offline-first approach.

This means:

Data is stored locally using Room Database
The application works without internet connection
Faster local data access is possible
Suitable for rural and low-network areas
Installation Guide
Prerequisites
Android Studio
Kotlin SDK
Android SDK
Gradle
Steps to Run the Project
Step 1

Clone the repository:

git clone <repository-link>
Step 2

Open the project in Android Studio.

Step 3

Sync Gradle files.

Step 4

Run the application on:

Android Emulator OR
Physical Android Device
Validation Features

The application includes multiple validation mechanisms to improve reliability and prevent incorrect data entry.

Implemented Validations:
Aadhaar number validation
Phone number validation
Age validation
Duplicate member prevention
Women SHG eligibility restriction
Loan activity checks
Current Modules
Dashboard Module
Member Management Module
Loan Management Module
Savings Management Module
Member Profile Module
Admin Login Module
Future Enhancements

Possible future improvements include:

Firebase cloud synchronization
PDF report generation
Multi-language support
Notification reminders
Advanced analytics
Export functionality
UPI integration
Multi-admin access

Conclusion
Mahila Shakti Unnati is a practical Android application developed to digitally transform Women Self Help Group operations through efficient member management, savings tracking, loan management,
repayment monitoring, and financial analytics.
The project combines modern Android technologies with real-world SHG workflows to create a scalable, user-friendly, and socially impactful financial management solution supporting women empowerment and rural financial digitization.
