# TrackYS 💳📱

**TrackYS** is a modern, open-source Android subscription and recurring expense tracking application built with **Jetpack Compose**, **Material 3**, **Clean Architecture**, and **Kotlin Coroutines**.

`TrackYS` helps you track, organize, and manage your recurring subscriptions (such as Spotify, Netflix, ChatGPT, cloud storage, software licenses, and utilities) effortlessly. It features multi-currency support with automatic exchange rate conversion, expense analytics with interactive visual charts, customizable categories, automated renewal reminders, and JSON backup/restore capabilities.

---

## 🌟 Key Features

- **📊 Expense Analytics & Visualizations**: Interactive donut chart displaying monthly expense breakdown by category, with total monthly cost calculations converted to your base currency.
- **💱 Multi-Currency Support**: Add subscriptions in any currency (USD, EUR, GBP, TRY, UAH, etc.). Automatically fetches latest exchange rates and caches them for offline availability.
- **📅 Renewal Reminders**: Background notifications via WorkManager that alert you 1 day prior to upcoming subscription payments.
- **🏷️ Customizable Categories & Colors**: Add custom categories and assign personalized hex colors.
- **⚡ Bulk Operations**: Select multiple subscriptions at once to change categories or delete in bulk.
- **💾 Backup & Restore (JSON)**: Export your subscriptions to a JSON file, share it across devices, and restore them anytime.
- **🌍 Multi-Language Support**: Fully localized in multiple languages including English, Ukrainian, German, Spanish, French, Italian, Belarusian, and Turkish.
- **🎨 Modern Material 3 UI**: Clean, responsive layout with Edge-to-Edge experience and full dark/light theme support.

---

## 🛠️ Tech Stack & Architecture

TrackYS is built following **Modern Android Development (MAD)** best practices and **Clean Architecture**:

- **UI & Layout**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with [Material 3](https://m3.material.io/) design components.
- **Navigation**: [Navigation Compose](https://developer.android.com/jetpack/compose/navigation) with type-safe routing.
- **Architecture**: Clean Architecture (Domain, Data, Presentation layers) + MVI / MVVM State-Event-Effect pattern.
- **Dependency Injection**: [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) for modular and testable DI.
- **Local Persistence**:
  - [Room Database](https://developer.android.com/training/data-storage/room) for caching subscriptions and exchange rates offline.
  - [Preferences DataStore](https://developer.android.com/topic/libraries/architecture/datastore) for user settings (base currency, categories, custom colors).
- **Network & Serialization**:
  - [Retrofit 3](https://square.github.io/retrofit/) + OkHttp logging for exchange rate fetching.
  - [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) for JSON handling and export/import functionality.
- **Background Operations**: [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) for reliable background task scheduling.
- **Asynchronous Flow**: Kotlin [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [StateFlow](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-state-flow/).
- **Build System & Tooling**: Gradle (AGP 9.x), KSP (Kotlin Symbol Processing), R8 minification.

---

## 🏗️ Project Architecture

```
xyz.denizkose.trackys/
├── core/                  # Utility helpers (WorkScheduler, CurrencyHelper, NotificationHelper, etc.)
├── data/
│   ├── local/             # Room DB (AppDatabase, DAOs, Entities, Converters), UserPreferences (DataStore)
│   ├── remote/            # Retrofit API service & DTOs
│   ├── repository/        # Repository implementations
│   └── worker/            # WorkManager workers (ReminderWorker)
├── di/                    # Hilt Dependency Injection modules (DatabaseModule, NetworkModule, RepositoryModule)
├── domain/
│   ├── model/             # Core domain models (Subscription, BillingCycle, CurrencyRate)
│   ├── repository/        # Repository interfaces
│   └── usecase/           # Business logic use cases (CalculateMonthlyExpenseUseCase, GetNextPaymentDateUseCase)
├── presentation/
│   ├── components/        # Reusable Compose UI components (DonutChart, SubscriptionCard)
│   ├── navigation/        # Navigation routing & screens
│   └── screens/           # UI Screens with ViewModels & Contracts (Dashboard, Analytics, AddEdit, Settings)
└── ui/theme/              # Material 3 colors, typography, and theme definitions
```

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio** (Ladybug or newer recommended)
- **JDK 11** or higher
- **Android Device or Emulator** running Android 10 (API 29) or higher

### Building & Running

1. **Clone the repository**:
   ```bash
   git clone https://github.com/denizkose/TrackYS.git
   cd TrackYS
   ```

2. **Open the project in Android Studio**:
   Open Android Studio and select `Open` -> navigate to the `TrackYS` folder.

3. **Build the project**:
   Build the project using Gradle command or via Android Studio UI:
   ```bash
   ./gradlew assembleDebug
   ```

4. **Run on Device / Emulator**:
   Select your target device in Android Studio and hit **Run** (`Shift + F10`), or install via ADB:
   ```bash
   ./gradlew installDebug
   ```

---

## 🧪 Running Unit Tests

To execute the unit test suite:
```bash
./gradlew testDebugUnitTest
```

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).
