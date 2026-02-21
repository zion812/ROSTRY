package com.rio.rostry.ui.enthusiast.digitalfarm

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.rio.rostry.domain.model.BirdAppearance
import com.rio.rostry.domain.model.BreastShape
import com.rio.rostry.domain.model.Stance
import com.rio.rostry.ui.enthusiast.digitalfarm.BirdPartRenderer.drawBirdFromAppearance
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class BirdPartRendererTest {

    private val BACKGROUND_COLOR = AndroidColor.MAGENTA

    private fun renderBirdToBitmap(
        appearance: BirdAppearance = BirdAppearance(),
        rotation: Float = 0f,
        width: Int = 400,
        height: Int = 400
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(BACKGROUND_COLOR)
        val imageBitmap = bitmap.asImageBitmap()
        val canvas = Canvas(imageBitmap)
        val drawScope = CanvasDrawScope()

        drawScope.draw(
            Density(1f),
            LayoutDirection.Ltr,
            canvas,
            Size(width.toFloat(), height.toFloat())
        ) {
            drawBirdFromAppearance(
                x = width / 2f, // 200
                y = height * 0.75f, // 300
                appearance = appearance,
                rotation = rotation
            )
        }
        return bitmap
    }

    private fun hasAnyBirdPixelInLine(bitmap: Bitmap, startX: Int, endX: Int, y: Int): Boolean {
        for (x in startX..endX) {
            if (bitmap.getPixel(x, y) != BACKGROUND_COLOR) {
                return true
            }
        }
        return false
    }

    @Test
    fun debugPrintDetailedMap_RotationRight() {
        val appearance = BirdAppearance(stance = Stance.UPRIGHT, breast = BreastShape.FLAT)
        val bitmap = renderBirdToBitmap(appearance = appearance, rotation = 1.0f)
        
        println("DETAILED MAP ROTATION=1.0 (X: 180..240, Y: 260..300)")
        for (y in 260..300) {
            val row = java.lang.StringBuilder().append(y.toString().padStart(3, '0')).append(": ")
            for (x in 180..240) {
                if (bitmap.getPixel(x, y) != BACKGROUND_COLOR) {
                    row.append("X")
                } else {
                    row.append(".")
                }
            }
            println(row.toString())
        }
    }

    @Test
    fun debugPrintDetailedMap_RotationLeft() {
        val appearance = BirdAppearance(stance = Stance.UPRIGHT, breast = BreastShape.FLAT)
        val bitmap = renderBirdToBitmap(appearance = appearance, rotation = -1.0f)
        
        println("DETAILED MAP ROTATION=-1.0 (X: 160..220, Y: 260..300)")
        for (y in 260..300) {
            val row = java.lang.StringBuilder().append(y.toString().padStart(3, '0')).append(": ")
            for (x in 160..220) {
                if (bitmap.getPixel(x, y) != BACKGROUND_COLOR) {
                    row.append("X")
                } else {
                    row.append(".")
                }
            }
            println(row.toString())
        }
    }

    // TASK 1: BUG CONDITION EXPLORATION TESTS (These MUST fail on unfixed code)

    @Test
    fun testNeckConnection_MaximumLeftRotation_NoGaps() {
        val appearance = BirdAppearance(stance = Stance.UPRIGHT, breast = BreastShape.FLAT)
        val bitmap = renderBirdToBitmap(appearance = appearance, rotation = -1.0f)
        
        // At rotation -1.0 (Left), Head shifts left (-25), Body shifts left (-5).
        // Head pulls completely away from the body/chest.
        // Unfixed code leaves X=172..189 completely hollow at Y=281.
        // Robolectric draws shapes as hollow outlines, so we must probe the entire gap span 
        // to catch the boundary lines of drawNeckConnection.
        val hasConnection = hasAnyBirdPixelInLine(bitmap, 172, 189, 281)
        
        assertTrue("Found visual gap at neck with rotation=-1.0", hasConnection)
    }

    @Test
    fun testTailConnection_MaximumRightRotation_NoGaps() {
        val appearance = BirdAppearance(stance = Stance.UPRIGHT, breast = BreastShape.FLAT)
        val bitmap = renderBirdToBitmap(appearance = appearance, rotation = 1.0f)
        
        // At rotation 1.0 (Right), Tail shifts left (-20), Body shifts right (+5).
        // Tail is literally ripped off the body on the left side.
        // Unfixed code leaves X=172..192 completely hollow at Y=290.
        // We probe the entire gap span to catch the boundary lines of drawTailConnection.
        val hasConnection = hasAnyBirdPixelInLine(bitmap, 172, 192, 290)
        
        assertTrue("Found visual gap between tail and body with rotation=1.0", hasConnection)
    }

    // TASK 2: PRESERVATION PROPERTY TESTS (These must pass on unfixed code)

    @Test
    fun testBaselinePreservation_DefaultParameters() {
        val bitmapOriginal = renderBirdToBitmap(rotation = 0.0f)
        
        // At rotation 0.0, the baseline outlines are perfectly joined.
        // From the exact 1x1 map, there are solid outlines at x=184 and x=216 at y=281.
        // We probe the area just inside the bounding edges to ensure the baseline renders at all.
        val hasLeftOutline = hasAnyBirdPixelInLine(bitmapOriginal, 183, 186, 281)
        val hasRightOutline = hasAnyBirdPixelInLine(bitmapOriginal, 214, 218, 281)
        
        assertTrue("Baseline left outline missing", hasLeftOutline)
        assertTrue("Baseline right outline missing", hasRightOutline)
    }
}
