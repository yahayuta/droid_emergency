# HELP ME - Emergency Android App

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Android](https://img.shields.io/badge/Android-API%2029+-green.svg)](https://developer.android.com/about/versions/android-10)
[![Gradle](https://img.shields.io/badge/Gradle-8.9-blue.svg)](https://gradle.org/)

A lifesaving Android application that sends emergency emails with GPS location data to pre-configured contacts when you're in need of immediate help.

## 🚨 Features

- **Emergency Widget**: Quick access emergency button on your home screen
- **GPS Location**: Automatically includes your current location in emergency messages
- **Multiple Contacts**: Send emergency alerts to up to 5 different email addresses
- **Custom Messages**: Personalize your emergency message
- **Gmail Integration**: Uses Gmail SMTP for reliable email delivery
- **Offline Support**: Stores contact information locally for quick access
- **Vibration Feedback**: Provides tactile confirmation when emergency message is sent

## 📱 Screenshots

*Screenshots will be added here*

## 🛠️ Requirements

- Android API 29+ (Android 10 or higher)
- Gmail account for sending emergency emails
- Internet connection for email delivery
- GPS enabled for location sharing

## 📦 Installation

### Prerequisites

- Android Studio Arctic Fox or later
- Android SDK API 29+
- Gradle 8.9

### Building from Source

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/droid_emergency.git
   cd droid_emergency
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing Android Studio project"
   - Navigate to the cloned directory and select it

3. **Build and Run**
   ```bash
   ./gradlew assembleDebug
   ```

4. **Install on Device**
   ```bash
   ./gradlew installDebug
   ```

## 🔧 Configuration

### Initial Setup

1. **Launch the app** - Tap the "HELP ME" app icon
2. **Enter Required Information**:
   - Your Name (Required)
   - Gmail Address (Required)
   - Gmail Password (Required)
   - Emergency Contact Email 1 (Required)
   - Emergency Contact Emails 2-5 (Optional)
   - Custom Message (Optional)

3. **Test Configuration** - Use the "SEND TEST MAIL" button to verify your setup

### Adding the Emergency Widget

1. Long press on your home screen
2. Select "Widgets"
3. Find "HELP ME" in the widget list
4. Drag and drop the widget to your home screen

## 🚀 Usage

### Emergency Alert

1. **Tap the Emergency Widget** on your home screen
2. **Confirm the action** when prompted
3. **Wait for confirmation** - The app will vibrate and show "MAIL IS SENT"

### What Happens During Emergency

- Your current GPS location is captured
- An email is sent to all configured contacts with:
  - Your name
  - Current GPS coordinates
  - Custom message (if configured)
  - Timestamp
- Device vibrates to confirm successful sending

## 📋 Permissions

The app requires the following permissions:

- **Internet**: To send emergency emails
- **Network State**: To check connectivity before sending
- **Location**: To include GPS coordinates in emergency messages
- **Vibrate**: To provide feedback when emergency message is sent

## 🏗️ Project Structure

```
droid_emergency/
├── app/
│   ├── src/main/
│   │   ├── java/droid/emergency/
│   │   │   ├── EmergencyActivity.java      # Main configuration activity
│   │   │   ├── EmergencyAppWidgetProvider.java  # Widget implementation
│   │   │   ├── EmergencyService.java       # Background service
│   │   │   ├── EmergencyDBHelper.java      # Database operations
│   │   │   ├── EmergencyContentProvider.java     # Content provider
│   │   │   ├── EmergencyEntity.java        # Data model
│   │   │   ├── EmergencyUtil.java          # Utility functions
│   │   │   └── EmergencyConst.java         # Constants
│   │   ├── res/
│   │   │   ├── layout/                     # UI layouts
│   │   │   ├── values/                     # String resources
│   │   │   └── drawable/                   # App icons
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── gradle/
└── README.md
```

## 🔒 Security Considerations

- **Gmail Credentials**: Your Gmail password is stored locally on your device
- **Location Data**: GPS coordinates are only sent during emergency alerts
- **Network Security**: Uses Gmail's secure SMTP servers
- **Local Storage**: All data is stored locally on your device

## 🐛 Troubleshooting

### Common Issues

**"NO NETWORK CONNECTION"**
- Check your internet connection
- Ensure WiFi or mobile data is enabled

**"MAIL TRANSPORT ERROR"**
- Verify your Gmail credentials
- Check if "Less secure app access" is enabled in Gmail settings
- Ensure your Gmail account has 2FA properly configured

**"NO GPS DATA"**
- Enable GPS in your device settings
- Allow location permissions for the app
- Move to an area with better GPS signal

**"TO USE THIS APPLICATION, YOU NEED TO EDIT YOUR INFORMATION FIRST"**
- Launch the app and complete the initial configuration
- Save your information using the "SAVE" button

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## ⚠️ Disclaimer

This application is designed for emergency situations. It is the user's responsibility to:
- Ensure the app is properly configured before an emergency
- Test the functionality regularly
- Have alternative emergency contact methods available
- Use appropriate emergency services (911, etc.) for life-threatening situations

## 📞 Support

If you encounter any issues or have questions:

1. Check the [Troubleshooting](#troubleshooting) section
2. Search existing [Issues](../../issues)
3. Create a new issue with detailed information about your problem

## 🙏 Acknowledgments

- Built with Android SDK and Java
- Uses JavaMail API for email functionality
- GPS location services provided by Android Location API
- Icons and UI elements designed for emergency situations

---

**Remember**: This app is a tool to help in emergency situations, but it should not replace professional emergency services. Always call 911 or your local emergency number for immediate life-threatening situations. 