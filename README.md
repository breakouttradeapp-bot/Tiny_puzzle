# Tiny Genius - Coloring & Puzzle App

A complete, production-ready Android app for kids aged 3-7, built with Kotlin and Jetpack Compose.

## 📱 Features

### Coloring Module
- 10 coloring pages (3 free, 7 premium)
- Tap-to-fill coloring mechanism
- 20-color palette
- Undo/Clear functionality
- Sound feedback
- Celebration animation on completion
- Save colored images internally

### Puzzle Module
- 9 puzzle levels across 3 difficulty levels
- 4-piece puzzles (free)
- 6-piece puzzles (premium)
- 9-piece puzzles (premium)
- Drag-and-drop mechanics
- Snap-to-position
- Move counter
- Completion detection with celebration

### Monetization
- **Free Version**: Limited content + banner ads
- **Premium (₹199 one-time)**: All content unlocked + ad-free
- Parent gate protection
- Google Play Billing integration
- Child-safe, non-personalized ads only

## 🏗️ Architecture

- **Pattern**: Clean Architecture + MVVM
- **UI**: Jetpack Compose (single activity)
- **Navigation**: Navigation Compose
- **State Management**: StateFlow + ViewModel
- **Local Storage**: DataStore Preferences
- **Billing**: Google Play Billing Library 6.x
- **Ads**: Google Mobile Ads SDK

## 📁 Project Structure

```
com.tinygenius/
├── MainActivity.kt
├── TinyGeniusApp.kt
├── data/
│   ├── model/
│   │   ├── ColoringPage.kt
│   │   └── PuzzleLevel.kt
│   ├── repository/
│   │   ├── BillingRepository.kt
│   │   └── ContentRepository.kt
│   └── local/
│       └── PreferencesManager.kt
├── ui/
│   ├── theme/
│   ├── home/
│   ├── coloring/
│   ├── puzzle/
│   └── components/
├── navigation/
│   ├── NavGraph.kt
│   └── Screen.kt
└── utils/
    ├── Constants.kt
    └── SoundManager.kt
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog | 2023.1.1 or newer
- JDK 17
- Android SDK 24+ (Android 7.0+)
- Google Play Console account
- AdMob account

### Setup Steps

1. **Clone/Download the project**

2. **Configure AdMob**
   - Create AdMob app
   - Update `AndroidManifest.xml` with your App ID
   - Update `Constants.kt` with your Ad Unit ID

3. **Configure Play Billing**
   - Create in-app product in Play Console
   - Product ID: `premium_unlock`
   - Price: ₹199

4. **Add Images**
   - Add coloring images: `coloring_1.png` to `coloring_10.png` in `res/drawable/`
   - Add puzzle images: `puzzle_1.png` to `puzzle_9.png` in `res/drawable/`
   - Images should be 800x800px for coloring, 600x600px for puzzles

5. **Add Sound Files (Optional)**
   - Add `tap_sound.mp3` in `res/raw/`
   - Add `success_sound.mp3` in `res/raw/`

6. **Build and Run**
   ```bash
   ./gradlew assembleDebug
   ./gradlew installDebug
   ```

## 🎨 Customization

### Adding New Coloring Pages
1. Add image to `res/drawable/`
2. Update `ContentRepository.kt`:
```kotlin
ColoringPage(
    id = 11,
    name = "New Image",
    imageRes = R.drawable.coloring_11,
    isPremium = true,
    category = ColoringCategory.ANIMALS
)
```

### Adding New Puzzle Levels
1. Add image to `res/drawable/`
2. Update `ContentRepository.kt`:
```kotlin
PuzzleLevel(
    id = 10,
    name = "New Puzzle",
    imageRes = R.drawable.puzzle_10,
    pieceCount = 9,
    isPremium = true
)
```

### Changing Colors
Update `Color.kt` in `ui/theme/` package.

### Changing Premium Price
Update product price in Google Play Console.

## 📊 Testing

### Test AdMob Integration
Use test IDs (already configured):
- App ID: `ca-app-pub-3940256099942544~3347511713`
- Banner: `ca-app-pub-3940256099942544/6300978111`

### Test Billing
1. Add test account in Play Console
2. Upload AAB to internal testing track
3. Install via Play Store
4. Test purchase flow

## 🔒 Privacy & Compliance

### COPPA Compliance
- ✅ No personal data collection
- ✅ No analytics tracking
- ✅ No external links (except privacy policy)
- ✅ Non-personalized ads only
- ✅ No social features
- ✅ Parent gate for purchases
- ✅ Offline functionality

### Required Permissions
- `INTERNET` only (for AdMob and Play Billing)
- No camera, microphone, location, or storage permissions

### Privacy Policy
Create a privacy policy stating:
- No data collection
- No third-party sharing
- How ads work
- How IAP works
- Contact information

Host on a public URL and link in Play Console.

## 🎯 Google Play Families Policy

This app is designed to comply with:
- Target audience declaration (Ages 3-7)
- Content rating (ESRB: Everyone)
- Designed for Families program requirements
- Teacher Approved program (optional)

## 📦 Release Checklist

- [ ] Replace test AdMob IDs with production IDs
- [ ] Create signing keystore
- [ ] Configure signing in `build.gradle.kts`
- [ ] Test on multiple devices
- [ ] Test all features offline
- [ ] Verify no crashes or ANRs
- [ ] Test purchase flow end-to-end
- [ ] Create store listing assets
- [ ] Write privacy policy
- [ ] Complete Play Console questionnaires
- [ ] Submit for review

## 🛠️ Technologies Used

- **Language**: Kotlin 1.9.20
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM + Clean Architecture
- **Dependency Injection**: Manual (lightweight)
- **Async**: Kotlin Coroutines + Flow
- **Navigation**: Navigation Compose
- **Local Storage**: DataStore Preferences
- **Image Loading**: Coil
- **Billing**: Play Billing Library 6.1.0
- **Ads**: Google Mobile Ads 22.6.0

## 📄 License

This is a production-ready template. You own the code and can:
- Use commercially
- Modify as needed
- Publish to Play Store
- Remove attribution

## 🤝 Support

For issues or questions:
1. Check SETUP_INSTRUCTIONS.md
2. Review Android documentation
3. Check Google Play Families policies
4. Consult AdMob and Billing documentation

## 🎉 Credits

Built with ❤️ for kids' education and entertainment.

---

**Important**: Before publishing:
1. Add your own coloring and puzzle images
2. Test thoroughly with real children (supervised)
3. Ensure all content is age-appropriate
4. Get legal review of privacy policy
5. Comply with all local regulations
