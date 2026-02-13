# Testing Guide for Tiny Genius

## Pre-Release Testing Checklist

### 1. Installation & Launch
- [ ] App installs successfully
- [ ] App launches without crash
- [ ] Splash screen displays correctly (if added)
- [ ] Home screen loads within 3 seconds
- [ ] No ANRs (Application Not Responding)

### 2. Home Screen
- [ ] All buttons are visible and properly sized
- [ ] App logo/title displays correctly
- [ ] Button animations work smoothly
- [ ] Sound toggle works
- [ ] Sound setting persists after app restart
- [ ] "Unlock Premium" button visible (free version)
- [ ] "Unlock Premium" button hidden (after purchase)

### 3. Navigation
- [ ] Navigate to Coloring screen
- [ ] Navigate to Puzzle screen
- [ ] Back button returns to Home
- [ ] Android back button works correctly
- [ ] No navigation crashes
- [ ] Smooth transitions between screens

### 4. Coloring Module

#### Page Selection
- [ ] All free pages visible
- [ ] Premium pages show lock icon (free version)
- [ ] Premium pages unlocked (premium version)
- [ ] Page images load correctly
- [ ] Tapping locked page doesn't navigate
- [ ] Tapping unlocked page opens canvas

#### Canvas
- [ ] Coloring image loads correctly
- [ ] Image scales to fit screen
- [ ] Color palette displays all 20 colors
- [ ] Tapping color selects it (visual feedback)
- [ ] Tapping image colors the region
- [ ] Sound plays on tap (if enabled)
- [ ] Undo button works
- [ ] Undo button disabled when no history
- [ ] Clear button resets the page
- [ ] Clear button shows confirmation (optional)
- [ ] Back button returns to selection

#### Celebration
- [ ] Celebration triggers when page is mostly colored
- [ ] Confetti animation plays
- [ ] Success sound plays (if enabled)
- [ ] Can dismiss celebration by tapping
- [ ] Celebration doesn't block interaction permanently

### 5. Puzzle Module

#### Level Selection
- [ ] Levels grouped by difficulty
- [ ] Free levels visible (4-piece)
- [ ] Premium levels show lock icon (free version)
- [ ] Premium levels unlocked (premium version)
- [ ] Puzzle images load correctly
- [ ] Difficulty badges display correctly
- [ ] Piece count shows correctly
- [ ] Tapping locked level doesn't navigate
- [ ] Tapping unlocked level opens game

#### Game
- [ ] Puzzle image loads and splits correctly
- [ ] Pieces display without gaps/overlaps
- [ ] Pieces shuffle on start
- [ ] Can drag pieces
- [ ] Pieces snap to grid
- [ ] Correct placement detected
- [ ] Visual feedback for correct placement (green border)
- [ ] Sound plays on placement (if enabled)
- [ ] Move counter increments
- [ ] Puzzle completion detected
- [ ] Celebration triggers on completion
- [ ] Reset button works
- [ ] Reset shuffles pieces
- [ ] Back button returns to selection

### 6. Monetization

#### Ads (Free Version)
- [ ] Banner ad loads on Home screen
- [ ] Ad displays correctly (no overlap)
- [ ] Ad doesn't cover essential UI
- [ ] Ad is child-safe content
- [ ] Ad refreshes periodically
- [ ] No personalized ads shown
- [ ] No ads after premium purchase

#### Parent Gate
- [ ] Parent gate shows when tapping "Unlock Premium"
- [ ] Math problem generates correctly
- [ ] Can solve problem and proceed
- [ ] Incorrect answer shows error
- [ ] Can cancel parent gate
- [ ] Parent gate prevents accidental purchases

#### Purchase Flow
- [ ] Purchase screen opens after parent gate
- [ ] Product details load correctly
- [ ] Price displays in correct currency
- [ ] Purchase completes successfully
- [ ] Purchase state persists after app restart
- [ ] Premium features unlock immediately
- [ ] Ads removed after purchase
- [ ] "Unlock Premium" button disappears
- [ ] Purchase can't be repeated
- [ ] Test purchase can be consumed (testing only)

### 7. Settings & Persistence
- [ ] Sound setting saves
- [ ] Sound setting loads on app start
- [ ] Premium status saves
- [ ] Premium status loads on app start
- [ ] App works after force stop
- [ ] App works after device restart

### 8. Offline Functionality
- [ ] App launches without internet
- [ ] Coloring works offline
- [ ] Puzzles work offline
- [ ] Premium features work offline (after purchase)
- [ ] Only ads require internet

### 9. Performance

#### Memory
- [ ] No memory leaks (use Profiler)
- [ ] Memory usage < 100MB
- [ ] No OutOfMemoryErrors

#### Responsiveness
- [ ] UI responds instantly to taps
- [ ] No UI freezing
- [ ] Smooth animations (60fps)
- [ ] Fast image loading

#### Battery
- [ ] No excessive battery drain
- [ ] App suspends properly in background

### 10. Different Devices

#### Screen Sizes
- [ ] Works on 4" phones
- [ ] Works on 6"+ phones
- [ ] Works on 7" tablets
- [ ] Works on 10" tablets
- [ ] UI scales appropriately
- [ ] Touch targets are large enough (48dp min)

#### Android Versions
- [ ] Works on Android 7.0 (API 24)
- [ ] Works on Android 8.0 (API 26)
- [ ] Works on Android 10 (API 29)
- [ ] Works on Android 12 (API 31)
- [ ] Works on Android 13 (API 33)
- [ ] Works on Android 14 (API 34)

#### Orientations
- [ ] Portrait orientation locked
- [ ] No crashes on rotation
- [ ] State preserved on rotation

### 11. Edge Cases

#### Network
- [ ] Handles no internet gracefully
- [ ] Handles slow internet
- [ ] Handles interrupted connections

#### Storage
- [ ] Handles low storage
- [ ] Saved images don't fill storage

#### Interruptions
- [ ] Handles incoming call
- [ ] Handles switching apps
- [ ] Handles low battery
- [ ] Handles app going to background

### 12. Child Safety

#### Content
- [ ] All images are age-appropriate
- [ ] No scary or violent content
- [ ] No inappropriate text
- [ ] Colors are bright and cheerful

#### Usability
- [ ] UI is intuitive for 3-7 year olds
- [ ] Buttons are large and easy to tap
- [ ] No complex gestures required
- [ ] Clear visual feedback
- [ ] Forgiving interactions (no punishments)

#### Safety
- [ ] No external links (except privacy policy in settings)
- [ ] No web views
- [ ] No ability to share content
- [ ] No chat or social features
- [ ] No ability to access camera
- [ ] No ability to access microphone

### 13. Compliance

#### Permissions
- [ ] Only INTERNET permission requested
- [ ] No location permission
- [ ] No camera permission
- [ ] No microphone permission
- [ ] No storage permission

#### Privacy
- [ ] No data collection
- [ ] No analytics
- [ ] No crash reporting with PII
- [ ] Privacy policy accessible
- [ ] Non-personalized ads only

#### Google Play
- [ ] Complies with Families policy
- [ ] Content rating appropriate (Everyone)
- [ ] Target audience set to Kids
- [ ] Teacher Approved criteria met (if applying)

## Testing Tools

### Android Studio Profiler
```bash
# Monitor memory usage
View → Tool Windows → Profiler
Select your app → Memory
```

### Layout Inspector
```bash
# Check UI hierarchy
View → Tool Windows → Layout Inspector
```

### ADB Commands
```bash
# Clear app data
adb shell pm clear com.tinygenius

# Simulate low memory
adb shell am send-trim-memory com.tinygenius RUNNING_LOW

# Check for memory leaks
adb shell dumpsys meminfo com.tinygenius

# Monitor crashes
adb logcat | grep "AndroidRuntime"
```

### Testing Billing
```bash
# Use test credit cards
Card: 4111 1111 1111 1111
Expiry: Any future date
CVV: Any 3 digits

# Or use Google Play test accounts
# Add in Play Console → Setup → License testing
```

## Bug Reporting Template

```
**Description:**
[Clear description of the issue]

**Steps to Reproduce:**
1. [First step]
2. [Second step]
3. [And so on...]

**Expected Behavior:**
[What should happen]

**Actual Behavior:**
[What actually happens]

**Device Info:**
- Device: [e.g., Samsung Galaxy S21]
- Android Version: [e.g., Android 12]
- App Version: [e.g., 1.0.0]

**Screenshots/Videos:**
[Attach if available]

**Logs:**
[Attach logcat if crash]
```

## Automated Testing (Optional)

### Unit Tests
```kotlin
@Test
fun testColorSelection() {
    val viewModel = ColoringViewModel(context, 1)
    viewModel.selectColor(0xFFFF0000)
    assertEquals(0xFFFF0000, viewModel.selectedColor.value)
}
```

### UI Tests
```kotlin
@Test
fun testNavigationToColoring() {
    composeTestRule.onNodeWithText("Coloring").performClick()
    composeTestRule.onNodeWithText("Choose a Coloring Page").assertExists()
}
```

## Pre-Release Checklist

Before submitting to Play Store:

- [ ] All critical bugs fixed
- [ ] All features tested
- [ ] Performance acceptable
- [ ] Privacy policy uploaded
- [ ] Screenshots captured
- [ ] Store listing complete
- [ ] Content rating obtained
- [ ] APK/AAB signed
- [ ] Version code incremented
- [ ] Release notes written

## Post-Release Monitoring

After publishing:

- [ ] Monitor crash reports
- [ ] Monitor ANRs
- [ ] Monitor user reviews
- [ ] Track uninstall rate
- [ ] Monitor purchase completion rate
- [ ] Check ad fill rate

## Testing with Children

**Supervised Testing Only:**

1. Observe without interfering
2. Note confusion points
3. Check if instructions are clear
4. Verify difficulty is appropriate
5. Ensure content is engaging
6. Check for frustration points
7. Time how long they stay engaged

**Never:**
- Leave children unsupervised with test builds
- Use production purchases in tests
- Share test builds publicly
- Test on personal devices without backup
