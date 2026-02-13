# Advanced Coloring: Flood Fill Implementation

The current implementation uses a simplified region-based coloring system. For production-quality tap-to-fill coloring, implement a flood fill algorithm.

## Flood Fill Algorithm (Recommended for Production)

### 1. Create FloodFillHelper.kt

```kotlin
package com.tinygenius.utils

import android.graphics.Bitmap
import android.graphics.Color
import java.util.*

class FloodFillHelper {
    
    /**
     * Flood fill algorithm to color a region
     * @param bitmap The bitmap to fill
     * @param x Touch X coordinate
     * @param y Touch Y coordinate
     * @param fillColor Color to fill with
     * @param tolerance Color tolerance (0-255)
     */
    fun floodFill(
        bitmap: Bitmap,
        x: Int,
        y: Int,
        fillColor: Int,
        tolerance: Int = 40
    ): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return bitmap
        }
        
        val targetColor = bitmap.getPixel(x, y)
        
        // Don't fill if clicking on same color
        if (colorsMatch(targetColor, fillColor, 0)) {
            return bitmap
        }
        
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val visited = Array(width) { BooleanArray(height) }
        val queue: Queue<Point> = LinkedList()
        
        queue.offer(Point(x, y))
        visited[x][y] = true
        
        while (queue.isNotEmpty()) {
            val point = queue.poll() ?: continue
            val px = point.x
            val py = point.y
            
            mutableBitmap.setPixel(px, py, fillColor)
            
            // Check 4 directions
            val directions = listOf(
                Point(px + 1, py),
                Point(px - 1, py),
                Point(px, py + 1),
                Point(px, py - 1)
            )
            
            for (next in directions) {
                if (next.x in 0 until width && 
                    next.y in 0 until height && 
                    !visited[next.x][next.y]) {
                    
                    val neighborColor = mutableBitmap.getPixel(next.x, next.y)
                    
                    if (colorsMatch(neighborColor, targetColor, tolerance) &&
                        !colorsMatch(neighborColor, fillColor, 0)) {
                        queue.offer(next)
                        visited[next.x][next.y] = true
                    }
                }
            }
        }
        
        return mutableBitmap
    }
    
    /**
     * Check if two colors match within tolerance
     */
    private fun colorsMatch(color1: Int, color2: Int, tolerance: Int): Boolean {
        val r1 = Color.red(color1)
        val g1 = Color.green(color1)
        val b1 = Color.blue(color1)
        
        val r2 = Color.red(color2)
        val g2 = Color.green(color2)
        val b2 = Color.blue(color2)
        
        return Math.abs(r1 - r2) <= tolerance &&
               Math.abs(g1 - g2) <= tolerance &&
               Math.abs(b1 - b2) <= tolerance
    }
    
    data class Point(val x: Int, val y: Int)
}
```

### 2. Update ColoringViewModel.kt

```kotlin
class ColoringViewModel(
    private val context: Context,
    private val pageId: Int
) : ViewModel() {
    
    private val floodFillHelper = FloodFillHelper()
    
    private val _currentBitmap = MutableStateFlow<Bitmap?>(null)
    val currentBitmap: StateFlow<Bitmap?> = _currentBitmap.asStateFlow()
    
    fun loadInitialBitmap(bitmap: Bitmap) {
        _currentBitmap.value = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    }
    
    fun fillRegion(x: Int, y: Int, color: Int) {
        val current = _currentBitmap.value ?: return
        
        viewModelScope.launch(Dispatchers.Default) {
            val filled = floodFillHelper.floodFill(
                bitmap = current,
                x = x,
                y = y,
                fillColor = color,
                tolerance = 40
            )
            
            withContext(Dispatchers.Main) {
                _currentBitmap.value = filled
                soundManager.playTapSound()
            }
        }
    }
}
```

### 3. Update ColoringCanvas.kt

```kotlin
@Composable
private fun ColoringImage(
    imageRes: Int,
    currentBitmap: Bitmap?,
    onTap: (Int, Int) -> Unit,
    selectedColor: Long
) {
    var displayBitmap by remember { mutableStateOf<Bitmap?>(null) }
    
    // Load initial bitmap
    LaunchedEffect(imageRes) {
        val bitmap = loadBitmapFromResource(context, imageRes)
        displayBitmap = bitmap
        // Pass to ViewModel
        onInitialBitmapLoaded(bitmap)
    }
    
    // Update display when bitmap changes
    LaunchedEffect(currentBitmap) {
        displayBitmap = currentBitmap
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    displayBitmap?.let { bitmap ->
                        // Convert tap coordinates to bitmap coordinates
                        val x = (offset.x / size.width * bitmap.width).toInt()
                        val y = (offset.y / size.height * bitmap.height).toInt()
                        
                        onTap(x, y)
                    }
                }
            }
    ) {
        displayBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Coloring page",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
}
```

## Performance Optimization

### 1. Use Worker Thread
```kotlin
viewModelScope.launch(Dispatchers.Default) {
    val result = floodFillHelper.floodFill(...)
    withContext(Dispatchers.Main) {
        updateBitmap(result)
    }
}
```

### 2. Limit Fill Area
```kotlin
// Add max pixels to fill
fun floodFill(
    bitmap: Bitmap,
    x: Int,
    y: Int,
    fillColor: Int,
    tolerance: Int = 40,
    maxPixels: Int = 50000 // Prevent filling entire image
): Bitmap {
    var pixelsFilled = 0
    
    while (queue.isNotEmpty() && pixelsFilled < maxPixels) {
        // ... fill logic
        pixelsFilled++
    }
    
    return mutableBitmap
}
```

### 3. Optimize for Large Images
```kotlin
// Scale down for coloring, scale up for saving
fun scaleBitmapForColoring(bitmap: Bitmap, maxSize: Int = 800): Bitmap {
    val ratio = minOf(
        maxSize.toFloat() / bitmap.width,
        maxSize.toFloat() / bitmap.height
    )
    
    if (ratio >= 1) return bitmap
    
    val newWidth = (bitmap.width * ratio).toInt()
    val newHeight = (bitmap.height * ratio).toInt()
    
    return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
}
```

## Testing Flood Fill

### Test Cases
1. Fill small regions
2. Fill large regions
3. Fill with different tolerances
4. Test on black outlines (shouldn't cross)
5. Test on white backgrounds
6. Test performance with large images

### Debugging
```kotlin
Log.d("FloodFill", "Target color: ${Integer.toHexString(targetColor)}")
Log.d("FloodFill", "Fill color: ${Integer.toHexString(fillColor)}")
Log.d("FloodFill", "Pixels filled: $pixelsFilled")
Log.d("FloodFill", "Time taken: ${System.currentTimeMillis() - startTime}ms")
```

## Alternative: Pre-defined Regions

For better performance with complex images:

```kotlin
// Define regions in JSON
data class ColoringRegion(
    val id: Int,
    val boundaryPoints: List<Point>,
    val defaultColor: Int = Color.WHITE
)

// Load from assets
fun loadRegions(pageId: Int): List<ColoringRegion> {
    val json = context.assets.open("coloring_regions_$pageId.json")
        .bufferedReader()
        .use { it.readText() }
    
    return Json.decodeFromString(json)
}

// Check if point is in region
fun isPointInRegion(point: Point, region: ColoringRegion): Boolean {
    // Ray casting algorithm
    var inside = false
    val boundary = region.boundaryPoints
    
    for (i in boundary.indices) {
        val j = (i + 1) % boundary.size
        val xi = boundary[i].x
        val yi = boundary[i].y
        val xj = boundary[j].x
        val yj = boundary[j].y
        
        val intersect = ((yi > point.y) != (yj > point.y)) &&
                (point.x < (xj - xi) * (point.y - yi) / (yj - yi) + xi)
        
        if (intersect) inside = !inside
    }
    
    return inside
}
```

## Production Recommendations

1. **Use Flood Fill** for simple coloring pages with clear outlines
2. **Use Pre-defined Regions** for complex pages with intricate details
3. **Combine Both**: Pre-defined regions for main areas, flood fill for details
4. **Test Extensively**: Different images, different device sizes
5. **Add Loading State**: Show progress for large fill operations
6. **Implement Undo Stack**: Store bitmap states for undo functionality

## Image Preparation Tips

1. **Clear Outlines**: Use thick black outlines (3-5px)
2. **Closed Regions**: Ensure all regions are fully enclosed
3. **High Contrast**: Use pure black (#000000) for outlines
4. **Transparent Background**: Use PNG with transparency
5. **Optimal Size**: 800x800px to 1200x1200px
6. **Test Each Image**: Verify flood fill works on all regions

## Next Steps

1. Implement flood fill algorithm
2. Test with various images
3. Optimize for performance
4. Add undo/redo with bitmap history
5. Implement zoom/pan for detailed coloring
6. Add color picker from image
