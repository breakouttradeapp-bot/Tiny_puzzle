# Quick Start Guide - Tiny Genius

Get your app running in 15 minutes!

## Step 1: Import Project (2 minutes)

1. Open Android Studio
2. Click "Open" 
3. Navigate to the TinyGenius folder
4. Click "OK"
5. Wait for Gradle sync to complete

## Step 2: Add Placeholder Images (5 minutes)

### Coloring Images
Create 10 simple PNG files (or use placeholders):

```bash
# You need these files in app/src/main/res/drawable/
coloring_1.png  (Cat outline)
coloring_2.png  (Dog outline)
coloring_3.png  (Apple outline)
coloring_4.png  (Elephant outline)
coloring_5.png  (Banana outline)
coloring_6.png  (Lion outline)
coloring_7.png  (Rabbit outline)
coloring_8.png  (Orange outline)
coloring_9.png  (Bird outline)
coloring_10.png (Fish outline)
```

**Temporary Solution**: Copy any image 10 times and rename them for now.

### Puzzle Images
Create 9 PNG files:

```bash
# You need these files in app/src/main/res/drawable/
puzzle_1.png to puzzle_9.png
```

**Temporary Solution**: Use the same placeholder images.

### Quick Command (if you have test images):
```bash
cd app/src/main/res/drawable/
cp placeholder.png coloring_1.png
cp placeholder.png coloring_2.png
# ... repeat for all files
```

## Step 3: Update Package Name (Optional, 3 minutes)

If you want to change from `com.tinygenius` to your package:

1. Right-click on `com.tinygenius` in Project view
2. Refactor → Rename Package
3. Enter new package name
4. Update in build.gradle.kts and AndroidManifest.xml

## Step 4: Build and Run (5 minutes)

### Build the App
```bash
# In terminal or use Android Studio "Build" menu
./gradlew assembleDebug
```

### Run on Device/Emulator
1. Connect Android device or start emulator
2. Click the green "Run" button (▶️)
3. Select your device
4. Wait for installation

**First launch takes 2-3 minutes, subsequent launches are faster.**

## Step 5: Test Basic Features

### Home Screen ✓
- [ ] App launches
- [ ] All buttons visible
- [ ] Sound toggle works

### Coloring ✓
- [ ] Can navigate to coloring
- [ ] Can select a page
- [ ] Can tap to select colors
- [ ] Can tap on image (coloring works)

### Puzzle ✓
- [ ] Can navigate to puzzles
- [ ] Can select a level
- [ ] Can drag pieces
- [ ] Pieces snap to grid

## Troubleshooting

### "Cannot resolve symbol R"
```bash
# Clean and rebuild
./gradlew clean
./gradlew assembleDebug
```

### "Drawable not found"
- Make sure all image files exist in `res/drawable/`
- File names must match exactly (lowercase)
- No spaces in file names

### "Build failed"
- Check you have JDK 17 installed
- Update Android Studio to latest version
- Invalidate caches: File → Invalidate Caches → Restart

### Gradle sync issues
```bash
# Delete .gradle folder and sync again
rm -rf .gradle
./gradlew clean
```

## Next Steps

Once the app runs successfully:

1. **Add Real Content**
   - Replace placeholder images with actual coloring pages
   - Add proper puzzle images
   - Add sound files (optional)

2. **Configure AdMob** (for testing)
   - The app already has test Ad IDs configured
   - Ads will show in test mode
   - Later, create your AdMob account and replace IDs

3. **Test Premium Purchase**
   - Parent gate will show
   - Purchase won't work yet (needs Play Console setup)
   - This is normal for local testing

4. **Customize UI** (optional)
   - Change colors in `ui/theme/Color.kt`
   - Modify button text in `res/values/strings.xml`
   - Adjust layout in respective Screen files

## File Structure Quick Reference

```
Important files you'll modify:
├── Constants.kt              → Ad IDs, pricing
├── ContentRepository.kt      → Add/remove content
├── Color.kt                  → Change colors
├── strings.xml               → Change text
└── drawable/                 → Your images here

Files you rarely touch:
├── ViewModels               → Logic (already done)
├── Repositories             → Data handling (already done)
├── build.gradle.kts         → Dependencies (already configured)
└── AndroidManifest.xml      → Permissions (already set)
```

## Development Workflow

### Daily Development
1. Make changes to code
2. Click Run (▶️)
3. Test on device
4. Repeat

### Adding New Coloring Page
1. Add image to `drawable/`
2. Open `ContentRepository.kt`
3. Add new `ColoringPage()` entry
4. Run app

### Adding New Puzzle
1. Add image to `drawable/`
2. Open `ContentRepository.kt`
3. Add new `PuzzleLevel()` entry
4. Run app

### Changing Colors
1. Open `ui/theme/Color.kt`
2. Modify color values
3. Run app

## Testing Checklist

Quick tests before showing to others:

- [ ] App installs and runs
- [ ] No crashes on launch
- [ ] Can navigate all screens
- [ ] Coloring works (even if simplified)
- [ ] Puzzles can be solved
- [ ] Sound toggle works
- [ ] Back button works
- [ ] Parent gate appears for premium

## What's Already Working

You don't need to implement:
- ✅ Navigation
- ✅ State management
- ✅ Billing integration
- ✅ AdMob integration
- ✅ Sound management
- ✅ Parent gate
- ✅ Celebration animations
- ✅ UI layouts
- ✅ Color selection
- ✅ Puzzle drag and drop

## What You Need to Add

Only these things:
- 🎨 Your coloring page images
- 🧩 Your puzzle images
- 🔊 Sound files (optional)
- 🎨 Your color scheme (optional)
- 📝 Your text/naming (optional)

## Time Estimates

- **Minimum viable app**: 15 minutes
- **With custom images**: 1-2 hours
- **Fully polished**: 1-2 days
- **Play Store ready**: 3-5 days

## Getting Help

### Check Documentation
1. `README.md` - Overview
2. `SETUP_INSTRUCTIONS.md` - Detailed setup
3. `TESTING_GUIDE.md` - Testing help

### Common Issues
- Build errors → Check Gradle version
- Image errors → Check file names
- Crash on launch → Check logcat

### Debug Mode
```bash
# View logs
adb logcat | grep "TinyGenius"

# Check for errors
adb logcat | grep "AndroidRuntime"
```

## You're All Set! 🎉

Your app is now running. Here's what to do next:

**Option A: Quick Demo**
- Show it to someone
- Get feedback
- Iterate on UI

**Option B: Production Ready**
- Follow SETUP_INSTRUCTIONS.md
- Add all content
- Configure services
- Test thoroughly
- Publish

**Option C: Learn & Customize**
- Read the code
- Understand architecture
- Make it your own
- Add new features

**Pro Tip**: Start with Option A, then move to C, then B!

Happy coding! 🚀
