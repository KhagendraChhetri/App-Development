# Tripify - Your Ultimate Travel Companion ✈️

![Platform](https://img.shields.io/badge/platform-Android-green.svg)
![Language](https://img.shields.io/badge/language-Java-orange.svg)
![Database](https://img.shields.io/badge/database-Firebase-yellow.svg)
![License](https://img.shields.io/badge/license-MIT-blue.svg)

An intuitive, all-in-one travel companion for discovering destinations, planning itineraries, and navigating new places with ease. Tripify is a native Android application designed to enhance your travel experience by providing essential tools and information right at your fingertips.

## 📸 Screenshots

| Login Screen | Destinations | Admin Dashboard |
| :---: | :---: | :---: |
| <img src=".onboarding.jpg" width="200"> | <img src="./screenshots/destination details.jpg" width="200"> | <img src="./screenshots/admindashboard.jpg" width="200"> |

## ✨ Features

-   **Explore Destinations:** Discover beautiful and interesting places with detailed descriptions, images, and user reviews.
-   **Personalized Itinerary:** Plan and organize your trip day-by-day, saving locations and adding personal notes.
-   **Real-time Currency Converter:** Get up-to-the-minute exchange rates with a built-in currency converter, perfect for managing your budget abroad.
-   **User-Friendly Interface:** A clean, modern, and intuitive UI that makes travel planning simple and enjoyable.
-   **Firebase Integration:** Real-time data synchronization for saved locations, reviews, and trip plans across devices.
-   **Location-Based Services:** Find nearby attractions, restaurants, and hotels using your device's location.

## 🛠️ Tech Stack

-   **Platform:** Native Android
-   **IDE:** Android Studio
-   **Language:** Java
-   **Database & Backend:** Google Firebase (Realtime Database, Authentication)
-   **APIs:**
    -   Google Maps API for location services and mapping.
    -   A third-party exchange rate API (e.g., ExchangeRate-API) for live currency data.

## 🚀 Getting Started

To get a local copy up and running, follow these simple steps.

### Prerequisites

-   Android Studio installed on your machine.
-   A Firebase project set up.
-   An API key from a currency exchange rate provider.

### Installation

1.  **Clone the repository**
    ```sh
    git clone [https://github.com/KhagendraChhetri/App-Development.git](https://github.com/KhagendraChhetri/App-Development.git)
    ```
2.  **Open the project in Android Studio**
    -   Open Android Studio.
    -   Click on `File` > `Open` and navigate to the cloned repository folder.
3.  **Set up Firebase**
    -   Go to your [Firebase Console](https://console.firebase.google.com/) and create a new project.
    -   Add an Android app to your Firebase project with your app's package name.
    -   Download the `google-services.json` file and place it in the `app/` directory of your project.
4.  **Add API Keys**
    -   You will need to add your Google Maps API key and your currency exchange API key. It is recommended to store these securely in your `local.properties` file and access them via the `build.gradle` file to keep them out of version control.
5.  **Build and Run the App**
    -   Sync the project with Gradle files.
    -   Build and run the app on an Android emulator or a physical device.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

## 📧 Contact

Khagendra Chhetri - [chetrikkhagendra96@gmail.com](mailto:chetrikkhagendra96@gmail.com)

Project Link: [https://github.com/KhagendraChhetri/App-Development](https://github.com/KhagendraChhetri/App-Development)
