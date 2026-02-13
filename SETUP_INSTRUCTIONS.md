# Tiny Genius - Coloring & Puzzle App
## Complete Setup Instructions

### 1. Project Structure
```
TinyGenius/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/tinygenius/
│   │       │   ├── MainActivity.kt
│   │       │   ├── TinyGeniusApp.kt
│   │       │   ├── navigation/
│   │       │   │   ├── NavGraph.kt
│   │       │   │   └── Screen.kt
│   │       │   ├── ui/
│   │       │   │   ├── theme/
│   │       │   │   │   ├── Color.kt
│   │       │   │   │   ├── Theme.kt
│   │       │   │   │   └── Type.kt
│   │       │   │   ├── home/
│   │       │   │   │   ├── HomeScreen.kt
│   │       │   │   │   └── HomeViewModel.kt
│   │       │   │   ├── coloring/
│   │       │   │   │   ├── ColoringScreen.kt
│   │       │   │   │   ├── ColoringViewModel.kt
│   │       │   │   │   └── ColoringCanvas.kt
│   │       │   │   ├── puzzle/
│   │       │   │   │   ├── PuzzleScreen.kt
│   │       │   │   │   ├── PuzzleViewModel.kt
│   │       │   │   │   └── PuzzlePiece.kt
│   │       │   │   └── components/
│   │       │   │       ├── ParentGate.kt
│   │       │   │       └── CelebrationAnimation.kt
│   │       │   ├── data/
│   │       │   │   ├── model/
│   │       │   │   │   ├── ColoringPage.kt
│   │       │   │   │   └── PuzzleLevel.kt
│   │       │   │   ├── repository/
│   │       │   │   │   ├── BillingRepository.kt
│   │       │   │   │   └── ContentRepository.kt
│   │       │   │   └── local/
│   │       │   │       └── PreferencesManager.kt
│   │       │   └── utils/
│   │       │       ├── SoundManager.kt
│   │       │       └── Constants.kt
│   │       ├── res/
│   │       │   ├── drawable/
│   │       │   ├── values/
│   │       │   └── raw/
│   │       └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
└── settings.gradle.kts
```

### 2. Create New Android Studio Project
1. Open Android Studio
2. Create New Project → Empty Activity
3. Name: TinyGenius
4. Package name: com.tinygenius
5. Language: Kotlin
6. Minimum SDK: API 24 (Android 7.0)
7. Build configuration language: Kotlin DSL

### 3. Configure Build Files

Replace your `build.gradle.kts` (Project level) with the provided file.
Replace your `build.gradle.kts` (Module: app) with the provided file.

### 4. Add Dependencies
All dependencies are included in the provided build.gradle.kts files:
- Jetpack Compose
- Navigation Compose
- ViewModel
- Google Play Billing Library v6
- Google Mobile Ads (AdMob)
- Coil for image loading

### 5. Setup AdMob
1. Create AdMob account at https://admob.google.com
2. Create new app in AdMob console
3. Create Banner Ad Unit
4. Copy the App ID and Ad Unit ID
5. Replace in AndroidManifest.xml:
   ```xml
   <meta-data
       android:name="com.google.android.gms.ads.APPLICATION_ID"
       android:value="ca-app-pub-YOUR_APP_ID"/>
   ```
6. Replace in Constants.kt:
   ```kotlin
   const val BANNER_AD_UNIT_ID = "ca-app-pub-YOUR_BANNER_AD_UNIT_ID"
   ```

For testing, use test IDs:
- App ID: ca-app-pub-3940256099942544~3347511713
- Banner: ca-app-pub-3940256099942544/6300978111

### 6. Setup Google Play Billing
1. Create app in Google Play Console
2. Set up In-App Products
3. Create Managed Product:
   - Product ID: premium_unlock
   - Price: ₹199
   - Title: "Premium Unlock"
   - Description: "Unlock all coloring pages and puzzles"
4. Replace PREMIUM_PRODUCT_ID in Constants.kt if needed

### 7. Add Coloring Images
Create outline images for coloring:
1. Create 10 PNG images with transparent backgrounds
2. Name them: coloring_1.png to coloring_10.png
3. Place in res/drawable/
4. Images should have clear black outlines
5. Recommended size: 800x800px

Example images needed:
- coloring_1.png (Cat)
- coloring_2.png (Dog)
- coloring_3.png (Elephant)
- coloring_4.png (Apple)
- coloring_5.png (Banana)
- coloring_6.png (Lion)
- coloring_7.png (Rabbit)
- coloring_8.png (Orange)
- coloring_9.png (Bird)
- coloring_10.png (Fish)

### 8. Add Puzzle Images
Create puzzle images:
1. Create 9 PNG images for puzzles
2. Name them: puzzle_1.png to puzzle_9.png
3. Place in res/drawable/
4. Recommended size: 600x600px

### 9. Add Sound Files (Optional)
Create/download child-safe sound effects:
1. Place in res/raw/
2. tap_sound.mp3 (tap feedback)
3. success_sound.mp3 (completion)
4. You can use royalty-free sounds from freesound.org

### 10. Configure Privacy & Safety
1. In Google Play Console → Policy → App Content
2. Complete Privacy Policy (required for kids apps)
3. Complete Target Audience → Select "Kids"
4. Complete Content Rating questionnaire
5. Declare no data collection
6. Submit for Designed for Families program

### 11. AndroidManifest.xml Configuration
The provided AndroidManifest.xml includes:
- Hardware acceleration enabled
- Correct permissions (INTERNET only)
- AdMob App ID metadata
- Portrait orientation locked

### 12. Build and Test
```bash
# Build debug APK
./gradlew assembleDebug

# Run on device
./gradlew installDebug
```

### 13. Testing Checklist
- [ ] Home screen loads correctly
- [ ] Navigation works between screens
- [ ] Coloring pages load and can be colored
- [ ] Color fill works correctly
- [ ] Undo/Clear buttons work
- [ ] Puzzles can be dragged and snapped
- [ ] Puzzle completion detected
- [ ] Parent gate shows before purchase
- [ ] Sound toggle works
- [ ] Banner ad displays (test mode)
- [ ] Purchase flow works (test mode)
- [ ] Premium features unlock after purchase
- [ ] App works offline
- [ ] No crashes or ANRs

### 14. Release Build
1. Create signing key:
   ```bash
   keytool -genkey -v -keystore tinygenius.jks -keyalg RSA -keysize 2048 -validity 10000 -alias tinygenius
   ```

2. Update app/build.gradle.kts with signing config

3. Build release APK:
   ```bash
   ./gradlew assembleRelease
   ```

### 15. Google Play Console Upload
1. Create app listing
2. Upload APK/AAB
3. Complete store listing with:
   - App icon (512x512px)
   - Feature graphic (1024x500px)
   - Screenshots (minimum 2)
   - Privacy policy URL
   - Description emphasizing educational value
4. Complete Content Rating
5. Set pricing (Free with IAP)
6. Submit for review

### 16. Important Notes
- Test thoroughly with real children (supervised)
- Ensure all content is age-appropriate
- No external links except privacy policy
- No social features or user-generated content
- Comply with COPPA regulations
- Use only non-personalized ads for children

### 17. Post-Launch
- Monitor crash reports in Play Console
- Respond to user reviews
- Update content regularly
- Monitor refund requests
- Keep dependencies updated

### Need Help?
- Google Play Families Policy: https://support.google.com/googleplay/android-developer/answer/9893335
- AdMob for Kids Apps: https://support.google.com/admob/answer/6223431
- Play Billing Library: https://developer.android.com/google/play/billing
