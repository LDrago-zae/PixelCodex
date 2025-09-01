# PixelCodex 🎮

[![Android](https://img.shields.io/badge/Android-API%2029%2B-green.svg?style=flat&logo=android)](https://android-arsenal.com/api?level=29)
[![Kotlin](https://img.shields.io/badge/Language-Java-orange.svg?style=flat&logo=java)](https://www.java.com)
[![Firebase](https://img.shields.io/badge/Backend-Firebase-orange.svg?style=flat&logo=firebase)](https://firebase.google.com)
[![Material Design](https://img.shields.io/badge/Design-Material%20Design%203-blue.svg?style=flat&logo=material-design)](https://material.io)
[![License](https://img.shields.io/badge/License-Proprietary-red.svg?style=flat)](LICENSE)

**PixelCodex** is a comprehensive gaming platform and social hub for Android, designed to provide gamers with a seamless experience for discovering, managing, and enjoying their favorite games while connecting with the gaming community.

## 📱 About the App

PixelCodex combines the functionality of a digital game store with social networking features and AI-powered assistance, creating a unique gaming ecosystem for Android users. Whether you're looking to discover new games, connect with friends, or get personalized gaming recommendations, PixelCodex has you covered.

## 🌟 Features

### 🎯 Core Gaming Features
- **Game Discovery**: Browse and search through an extensive game catalog
- **Featured Games**: Discover trending and recommended games
- **Game Categories**: Organized game browsing by genre and platform
- **Game Details**: Comprehensive information about each game including descriptions, platforms, and media
- **Wishlist Management**: Save games for later purchase or reference
- **Shopping Cart**: Streamlined game purchasing experience
- **Game Gifting**: Send games as gifts to friends
- **Game Requests**: Request new games to be added to the platform

### 👥 Social Features
- **User Profiles**: Personalized profiles with avatars and gaming activity
- **Friends System**: Connect and interact with fellow gamers
- **Gaming News**: Stay updated with the latest gaming news and announcements
- **Recent Activity**: Track your gaming history and engagement
- **Shop History**: View your purchase history and downloads
- **Notifications**: Real-time updates on friends, games, and platform news

### 🤖 AI-Powered Assistance
- **Gemini Chat Integration**: Get AI-powered gaming recommendations and support
- **Intelligent Game Suggestions**: AI-driven game discovery based on preferences
- **Smart Search**: Enhanced search functionality with AI assistance

### 🔐 Authentication & Security
- **Multiple Login Options**: 
  - Google Sign-In
  - Discord Authentication
  - Steam Integration
  - Traditional email/password
- **Firebase Security**: Secure user data management and authentication
- **Admin Panel**: Administrative interface for platform management

## 🛠️ Technical Stack

### **Frontend**
- **Language**: Java
- **UI Framework**: Android Native with Material Design
- **Navigation**: Android Navigation Component
- **Animations**: Lottie Animations
- **Image Loading**: Glide

### **Backend & Services**
- **Authentication**: Firebase Authentication
- **Database**: Firebase Realtime Database & Firestore
- **Storage**: Firebase Cloud Storage
- **AI Integration**: Google Generative AI (Gemini)
- **HTTP Client**: OkHttp3 & Retrofit2

### **Build System & Dependencies**
- **Build Tool**: Gradle 8.11.1 with Kotlin DSL
- **Android Gradle Plugin**: 8.1.4+
- **Min SDK**: Android 10 (API 29)
- **Target SDK**: Android 14 (API 35)
- **Compile SDK**: Android 14 (API 35)
- **Java Version**: 11
- **Dependencies Management**: Version Catalogs (TOML)

## 📋 Prerequisites

Before setting up the project, ensure you have:

- **Android Studio** Arctic Fox (2020.3.1) or newer
- **Java Development Kit (JDK)** 11 or higher
- **Android SDK** with API levels 29-35
- **Firebase Account** for backend services
- **Google Cloud Account** for Gemini AI integration

## 🚀 Setup Instructions

### 1. Clone the Repository
```bash
git clone https://github.com/LDrago-zae/PixelCodex.git
cd PixelCodex
```

### 2. Firebase Configuration
1. Create a new Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Enable the following services:
   - Authentication (Google, Email/Password)
   - Realtime Database
   - Firestore Database
   - Cloud Storage
3. Download the `google-services.json` file
4. Place it in the `app/` directory

### 3. API Keys Configuration
1. Create a `secrets.xml` file in `app/src/main/res/values/`
2. Add your API keys:
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="gemini_api_key">YOUR_GEMINI_API_KEY</string>
    <!-- Add other API keys as needed -->
</resources>
```

### 4. Dependencies Installation
The project uses Gradle version catalogs. Simply sync the project in Android Studio, and all dependencies will be automatically downloaded.

### 5. Build and Run
```bash
# Make gradlew executable (Linux/Mac)
chmod +x gradlew

# Clean and build the project
./gradlew clean build

# Install debug APK on connected device/emulator
./gradlew installDebug

# Or run directly from Android Studio
# File -> Open -> Select project folder -> Run
```

### 6. Troubleshooting Setup
If you encounter build issues:
1. Check Android Studio version compatibility
2. Verify Android SDK components are installed
3. Ensure proper Firebase configuration
4. Update Gradle versions if needed in `gradle/libs.versions.toml`

## 📱 App Architecture

### Main Activities
- **SplashScreenActivity**: Welcome screen with animations
- **MainActivity**: Authentication and OAuth flows
- **DashboardActivity**: Main app navigation hub
- **AdminLoginActivity**: Administrative access point

### Core Fragments
- **SearchFragment**: Game search and discovery
- **ProfileFragment**: User profile management
- **NewsFragment**: Gaming news and announcements
- **WishlistFragment**: Saved games management
- **GameDetailsFragment**: Detailed game information
- **GeminiChatFragment**: AI-powered chat assistance
- **SettingsFragment**: App configuration and preferences

### Key Components
```
PixelCodex/
├── app/
│   ├── src/main/java/com/example/pixelcodex/
│   │   ├── MainActivity.java              # Authentication & OAuth
│   │   ├── DashboardActivity.java         # Main navigation hub
│   │   ├── SplashScreenActivity.java      # Welcome screen
│   │   ├── AdminLoginActivity.java        # Admin access
│   │   ├── SignUpActivity.java            # User registration
│   │   ├── SearchFragment.java            # Game search & discovery
│   │   ├── ProfileFragment.java           # User profile management
│   │   ├── NewsFragment.java              # Gaming news feed
│   │   ├── WishlistFragment.java          # Saved games
│   │   ├── GameDetailsFragment.java       # Game information
│   │   ├── GeminiChatFragment.java        # AI chat assistance
│   │   ├── GameRequestsFragment.java      # Game request system
│   │   ├── NotificationsFragment.java     # Push notifications
│   │   ├── SettingsFragment.java          # App preferences
│   │   ├── *Adapter.java                  # RecyclerView adapters
│   │   └── ChatMessage.java               # Data models
│   ├── src/main/res/
│   │   ├── layout/                        # XML UI layouts
│   │   ├── values/                        # Strings, colors, styles
│   │   ├── mipmap/                        # App icons (HDPI to XXXHDPI)
│   │   ├── raw/                           # Lottie JSON animations
│   │   └── drawable/                      # Vector drawables & images
│   ├── build.gradle.kts                   # App-level dependencies
│   └── google-services.json               # Firebase configuration
├── gradle/
│   └── libs.versions.toml                 # Version catalog
├── build.gradle.kts                       # Project-level build config
├── settings.gradle.kts                    # Project settings
└── gradlew                                # Gradle wrapper script
```

### Database Structure
**Firebase Realtime Database:**
- `users/` - User profiles and authentication data
- `games/` - Game catalog with metadata
- `game_requests/` - User-submitted game requests
- `news/` - Gaming news and announcements
- `notifications/` - User notifications

**Firebase Firestore Collections:**
- `wishlists` - User game wishlists
- `purchases` - Purchase history and transactions
- `friends` - User connections and relationships
- `chat_messages` - AI chat conversations

## 🎨 UI/UX Features

- **Material Design 3**: Modern, accessible interface
- **Dark/Light Theme**: Adaptive theming support
- **Smooth Animations**: Lottie-powered micro-interactions
- **Responsive Design**: Optimized for various screen sizes
- **Bottom Navigation**: Intuitive app navigation
- **Bottom Sheets**: Contextual dialogs and forms

## 🔧 Development & Contributing

### Development Environment Setup
1. **Install Android Studio**: Arctic Fox (2020.3.1) or newer
2. **Configure SDK**: Install Android SDK 29-35 via SDK Manager
3. **Enable Developer Options**: On your Android device for testing
4. **Set up Emulator**: Create AVD with API 29+ for testing

### Code Architecture
- **MVVM Pattern**: Model-View-ViewModel where applicable
- **Repository Pattern**: Data layer abstraction
- **LiveData/ObservableFields**: Reactive data binding
- **Navigation Component**: Single-activity architecture
- **Firebase Integration**: Backend-as-a-Service

### Running Tests
```bash
# Unit tests
./gradlew test

# Instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest

# Generate test reports
./gradlew testDebugUnitTest --continue
```

### Code Style & Guidelines
The project follows standard Android development practices:
- **Material Design 3**: UI/UX consistency
- **Accessibility**: WCAG 2.1 compliance
- **Performance**: Memory and battery optimization
- **Security**: Data encryption and secure authentication

### Pull Request Process
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Follow existing code style and patterns
4. Add tests for new functionality
5. Update documentation if needed
6. Commit changes (`git commit -m 'Add amazing feature'`)
7. Push to branch (`git push origin feature/amazing-feature`)
8. Open a Pull Request with detailed description

### Reporting Issues
When reporting bugs, please include:
- Android version and device model
- App version and build variant
- Steps to reproduce the issue
- Expected vs actual behavior
- Screenshots or logs if applicable

## 🐛 Known Issues & Troubleshooting

### Build Issues
- **Gradle Plugin Version**: If you encounter AGP version issues, ensure you're using compatible versions:
  - Android Gradle Plugin: 8.1.4+
  - Gradle: 8.0+
  - Update `gradle/libs.versions.toml` if needed

### Common Issues
- **Missing google-services.json**: Ensure Firebase configuration file is present
- **API Key Configuration**: Some features require proper API key setup in `secrets.xml`
- **Admin features**: Currently in development
- **Steam integration**: Partially implemented

### Build Commands
```bash
# Clean and rebuild
./gradlew clean build

# Fix gradlew permissions (Linux/Mac)
chmod +x gradlew

# Sync dependencies
./gradlew build --refresh-dependencies
```

## 🚀 Deployment & Release

### Build Variants
- **Debug**: Development version with debugging features
- **Release**: Production-ready optimized build

### Release Process
1. Update version in `app/build.gradle.kts`
2. Generate signed APK/AAB for Play Store
3. Test on multiple devices and API levels
4. Submit to Google Play Console

### Project Status
- ✅ Core functionality implemented
- ✅ Authentication system working
- ✅ Firebase backend integrated
- ✅ AI chat functionality operational
- 🚧 Admin panel in development
- 🚧 Steam integration partial
- 📋 Payment system planned
- 📋 Advanced social features roadmapped

## 📸 Screenshots

*Screenshots will be added as the app UI is finalized*

## 🤝 Support & Community

For support and questions:
- 🐛 **Issues**: Create an issue on GitHub for bugs and feature requests
- 💬 **Discussions**: Use GitHub Discussions for general questions
- 📧 **Contact**: Reach out to the development team
- 🔧 **In-app Support**: Use the support feature within the app

## 📄 License

This project is currently proprietary. Please contact the repository owner for licensing information.

## 🙏 Acknowledgments

- **[Firebase](https://firebase.google.com)** for comprehensive backend services
- **[Google Generative AI](https://ai.google.dev)** for intelligent chat features
- **[Lottie](https://lottiefiles.com)** for beautiful micro-animations
- **[Material Design](https://material.io)** for modern UI/UX guidelines
- **[Android Jetpack](https://developer.android.com/jetpack)** for reliable development tools
- **Community Contributors** who help improve the platform

---

**Built with ❤️ for the gaming community**

*Version 1.0 - Updated December 2024*