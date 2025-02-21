<div id="top"></div>

<br/>
<div align="center">
    <img src="https://github.com/user-attachments/assets/a5e18061-711b-420b-9cd0-b8b76b5eea68" alt="Logo" width="100" height="100">
    <h3 align="center">Date Range Exporter</h3>
</div>

## Overview 📌

A native Android app that allows their users to select multiple date ranges and share a screenshot of each calendar with other apps. It also offers calendar edition like adding a label on each.

## Features 🚀

- Up to **3 calendar date ranges selection**, distinguished by different drawings and colors;
- Add a **chip label** to the calendars to identify the selection;
- **Share selection screenshots** to other apps.

## Demo 📱

https://github.com/user-attachments/assets/6ba6f9a7-04d3-45e0-afb9-31fd6506bb2d

## Tech Stack 🛠️ 

- **Language**: Kotlin
- **Architecture**: MVVM / Clean Architecture
- **UI Toolkit**: Jetpack Compose
- **Design System**: Material Design UI
- **Dependency Injection**: Koin
- **Unit Testing**: JUnit / MockK / Kluent / Faker

## Project Structure 📂

```
app/src/main/java/com/fstengineering/daterangeexporter
├── calendarExport/                        # Calendar Export screen presentation layer components (Screen, ViewModel, etc.)
│   ├── composables/                       # Calendar Export screen UI composables
│   ├── models/                            # Calendar Export screen model classes
│   ├── utils/                             # Calendar Export screen utility classes and helpers
│   ├── CalendarExportScreenKt             # Calendar Export screen UI logic
│   ├── CalendarExportViewModel.kt         # Calendar Export screen ViewModel with presentation UI/business logic
├── core/                                  # App scoped components and configs
│   ├── application/                       # **[Layer]** Application logic and platform-wide concerns
│   │   ├── contentProviders/              # App Content Providers implementations and handlers
│   │   ├── di/                            # Dependency Injection related logic
│   │   │   ├── modules/                   # Dependency Injection modules declarations
│   │   │   ├── DependencyInjection.kt     # Dependency Injection main config and modules initialization
│   │   ├── monitoring/                    # Logging configs (Analytics, crashes, etc.)
│   │   ├── theme/                         # App theme configs
│   │   │   ├── AppColorsKt                # App color schema config (light/dark mode)
│   │   │   ├── AppShapesKt                # App shapes config
│   │   │   ├── AppThemeKt                 # App theme main config
│   │   │   ├── AppTypographyKt            # App typography config
│   ├── data/                              # **[Layer]** Data persistence business logic
│   │   ├── dataSources/                   # Data sources handlers interfaces and implementations
│   │   │   ├── internalStorage/           # App-specific storage handlers
│   │   ├── exceptions/                    # Data layer exceptions
│   │   ├── repositories/                  # Repository implementations with data source combination logic
│   ├── domain/                            # **[Layer]** Plataform agnostic business logic
│   │   ├── repositories/                  # Repository interfaces
│   │   ├── utils/                         # Domain layer utility classes and helpers
│   │   │   ├── DataSourceError.kt         # Domain errors related to data sources thrown exceptions
│   │   │   ├── Error.kt                   # Domain error generic interface
│   │   │   ├── ResultKt                   # Generic classes to pass success and error data to presentation layer
│   │   │   ├── ValidationError.kt         # Domain errors related to validation violations
│   │   ├── validators/                    # Validator interfaces and implementations
│   ├── presentation/                      # **[Layer]** Presentation UI logic
│   │   ├── composables/                   # App global composables
│   │   ├── utils/                         # Presentation layer utility classes and helpers
├── CalendarExportApplication.kt           # Configures app components initialization
├── MainActivity.kt                        # Sets Compose View content to be presented and other configs

app/src/test/java/com/fstengineering/daterangeexporter
├── testUtils/                             # Local test utility functions and helpers
```

## Installation & Setup 🔧

1. Clone the repository:

   `git clone https://github.com/Franco904/date-range-exporter.git`

2. Open in Android Studio and sync dependencies.
3. Make sure a Gradle JDK version 11+ is used (check on Android Studio -> Settings -> Build, Execution, Deployment -> Build Tools -> Gradle).

   Preferably use JDK version 17.

4. Connect to an emulator or physical device.
5. Build and Run the project from Android Studio's "Run" button

## Running Tests 🧪

- To run all the unit tests:

   `make test`, or `./gradlew :app:testDebugUnitTest`

## Contributing 🤝

1. Clone the repository.
2. Create a new branch (feature/your-feature).
3. Commit your changes (git commit -m "Add new feature").
4. Push to the branch (git push origin feature/your-feature).
5. Open a Pull Request for code review.

## License ⚖️

This project is licensed under the MIT License.
