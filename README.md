# Selavu - Daily Expense Tracker

> *"Manage your money, one day at a time."*

**Selavu** is an offline-first Android application designed with **Jetpack Compose** and **Material 3** for tracking, managing, searching, editing, and organizing daily personal expenses.

---

## 🌟 Key Features

1. **Welcome Screen**
   - Clean, modern landing page with financial wallet logo and brand tagline.
   - Quick navigation to Registration and Login.

2. **Secure Local Authentication**
   - Registration with mandatory fields (Email, Age, Password, Confirm Password).
   - Strict input validation: email regex, age bounds (1–120), minimum 8-character password.
   - **PBKDF2 with Salt Hashing**: Passwords are cryptographically salted and hashed. Plain-text passwords are never stored.
   - Android Keystore / EncryptedSharedPreferences for persistent session management.
   - Seamless logout and profile management.

3. **Main Dashboard**
   - Personalized welcome greeting with current date.
   - High-contrast Hero Card with current month's total spending.
   - Summary cards: Today's Expenses, Total Transactions.
   - Recent Transactions preview with quick "View All" link.
   - Quick Add Expense button (+).

4. **Add & Edit Expense**
   - Record expense details: Title, Amount, Category, Date, Payment Method, Notes.
   - 8 predefined categories with dedicated icons & soft-tint backgrounds:
     - 🍔 Food
     - 🚗 Transportation
     - 🛍️ Shopping
     - 📄 Bills
     - 🎬 Entertainment
     - 🏥 Healthcare
     - 🎓 Education
     - 📁 Others
   - 5 payment methods: Cash, UPI, Debit Card, Credit Card, Other.
   - Native Date Picker dialog.
   - Edit any existing expense record.
   - Delete expense with Material 3 confirmation dialog.

5. **Search, Filter & Sort (Expenses History)**
   - Instant search by title, category, or notes.
   - Filter by date range: *All Time*, *Today*, *This Week*, *This Month*.
   - Filter by category with horizontal chips.
   - Multi-option sorting: *Newest First*, *Oldest First*, *Highest Amount*, *Lowest Amount*.
   - Dynamic counter showing matching transactions count and total sum.
   - User-friendly empty states.

6. **Profile & Security**
   - Displays user initial avatar, email, age, and lifetime transaction count.
   - Explains privacy protections: 100% offline, Room database, PBKDF2 encryption.
   - Log Out with confirmation dialog.

---

## 🏗️ Architecture & Technology Stack

- **Platform:** Android (Min SDK 26 - Android 8.0+, Target SDK 34)
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose + Material 3 Design
- **Architecture:** MVVM (Model-View-ViewModel) + Repository Pattern
- **Authentication:** Firebase Authentication (Email/Password)
- **Cloud Database:** Cloud Firestore (`users/{uid}` and `users/{uid}/expenses/{expenseId}`)
- **Security:**
  - Firebase Authentication managed tokens & sessions.
  - No plain-text passwords stored anywhere.
  - Per-user path isolation: `users/{uid}/expenses` ensures each user only accesses their own expenses.
- **Theme:** Navy Blue (`#0F2942`), White (`#FFFFFF`), and Soft Emerald Green (`#10B981`)

---

## 📁 Project Structure

```
Selavu/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/selavu/app/
│       │   │   ├── SelavuApplication.kt
│       │   │   ├── MainActivity.kt
│       │   │   ├── data/
│       │   │   │   ├── local/
│       │   │   │   │   ├── entity/ (UserEntity, ExpenseEntity)
│       │   │   │   │   ├── dao/ (UserDao, ExpenseDao)
│       │   │   │   │   └── database/ (SelavuDatabase)
│       │   │   │   ├── repository/ (UserRepository, ExpenseRepository)
│       │   │   │   └── security/ (PasswordHasher, SessionManager)
│       │   │   ├── model/ (ExpenseCategory, PaymentMethod, FilterAndSortOptions)
│       │   │   ├── ui/
│       │   │   │   ├── theme/ (Color, Theme, Type)
│       │   │   │   ├── navigation/ (Screen, NavGraph, BottomNavItem)
│       │   │   │   ├── components/ (TopBar, TextField, Button, CategoryChip, ExpenseItemCard, EmptyStateView, ConfirmDeleteDialog)
│       │   │   │   └── screens/
│       │   │   │       ├── welcome/ (WelcomeScreen)
│       │   │   │       ├── auth/ (RegisterScreen, LoginScreen)
│       │   │   │       ├── dashboard/ (DashboardScreen)
│       │   │   │       ├── history/ (ExpenseHistoryScreen)
│       │   │   │       ├── addedit/ (AddEditExpenseScreen)
│       │   │   │       └── profile/ (ProfileScreen)
│       │   │   └── viewmodel/ (AuthViewModel, ExpenseViewModel, ViewModelFactory)
│       │   └── res/
│       │       ├── drawable/ (ic_launcher_background, ic_launcher_foreground)
│       │       ├── mipmap-anydpi-v26/ (ic_launcher, ic_launcher_round)
│       │       └── values/ (colors.xml, strings.xml, themes.xml)
│       └── test/java/com/selavu/app/ (SecurityAndModelUnitTest.kt)
├── gradle/wrapper/ (gradle-wrapper.jar, gradle-wrapper.properties)
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
└── README.md
```

---

### 📍 Location of Generated APK:
Once the build completes, the output APK will be located at:
```
debug\Selavu.apk
```

