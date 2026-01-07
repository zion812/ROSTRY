package com.rio.rostry.ui.enthusiast.components

import android.content.Context
import android.graphics.Bitmap
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.ui.enthusiast.cards.CardTemplate

/**
 * Tilt state for 3D parallax effect.
 */
@Composable
fun rememberTiltState(enabled: Boolean = true): TiltState {
    val context = LocalContext.current
    val tiltState = remember { TiltState() }
    
    if (enabled) {
        DisposableEffect(Unit) {
            val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
            val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    event?.let {
                        // Normalize to -1..1 range, smoothed
                        val newX = (it.values[0] / 10f).coerceIn(-1f, 1f)
                        val newY = (it.values[1] / 10f).coerceIn(-1f, 1f)
                        tiltState.x = tiltState.x * 0.8f + newX * 0.2f
                        tiltState.y = tiltState.y * 0.8f + newY * 0.2f
                    }
                }
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }
            
            accelerometer?.let {
                sensorManager?.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI)
            }
            
            onDispose {
                sensorManager?.unregisterListener(listener)
            }
        }
    }
    
    return tiltState
}

class TiltState {
    var x by mutableFloatStateOf(0f)
    var y by mutableFloatStateOf(0f)
}

@Composable
fun RoosterCardView(
    product: ProductEntity,
    modifier: Modifier = Modifier,
    template: CardTemplate = CardTemplate.Showstopper,
    enableTilt: Boolean = true,
    onReadyToCapture: (View) -> Unit = {}
) {
    // Get tilt state for 3D effect
    val tiltState = rememberTiltState(enabled = enableTilt)
    
    // Use template colors
    val primaryColor = template.primaryColor
    val textColor = template.textColor
    val accentColor = template.accentColor
    val backgroundGradient = template.backgroundGradient
    
    // Legacy fallback colors
    val goldColor = primaryColor
    val deepRed = Color(0xFF500000)
    val black = Color.Black

    // Just wrapping in a Box that we can capture using standard View methods if needed
    // Ideally, for "Clean" capture, we pass the View back
    
    AndroidView(
        factory = { context ->
            androidx.compose.ui.platform.ComposeView(context).apply {
                setContent {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.7f)
                            // 3D Tilt effect (Comment 3)
                            .graphicsLayer {
                                if (enableTilt) {
                                    rotationX = tiltState.y * 10f
                                    rotationY = -tiltState.x * 10f
                                    cameraDistance = 12f * density
                                }
                            }
                            .background(brush = backgroundGradient, shape = RoundedCornerShape(16.dp))
                            .border(4.dp, primaryColor, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        // Background Pattern (Mesh)
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val step = 40.dp.toPx()
                            for (x in 0..size.width.toInt() step step.toInt()) {
                                drawLine(
                                    color = Color.White.copy(alpha = 0.05f),
                                    start = Offset(x.toFloat(), 0f),
                                    end = Offset(x.toFloat(), size.height),
                                    strokeWidth = 1f
                                )
                            }
                            for (y in 0..size.height.toInt() step step.toInt()) {
                                drawLine(
                                    color = Color.White.copy(alpha = 0.05f),
                                    start = Offset(0f, y.toFloat()),
                                    end = Offset(size.width, y.toFloat()),
                                    strokeWidth = 1f
                                )
                            }
                            // Gold corners
                             val cornerSize = 60.dp.toPx()
                             val stroke = 2.dp.toPx()
                             // TL
                             drawLine(primaryColor, Offset(0f, 0f), Offset(cornerSize, 0f), stroke*2)
                             drawLine(primaryColor, Offset(0f, 0f), Offset(0f, cornerSize), stroke*2)
                             // TR
                             drawLine(primaryColor, Offset(size.width, 0f), Offset(size.width - cornerSize, 0f), stroke*2)
                             drawLine(primaryColor, Offset(size.width, 0f), Offset(size.width, cornerSize), stroke*2)
                             // BL
                             drawLine(primaryColor, Offset(0f, size.height), Offset(cornerSize, size.height), stroke*2)
                             drawLine(primaryColor, Offset(0f, size.height), Offset(0f, size.height - cornerSize), stroke*2)
                             // BR
                             drawLine(primaryColor, Offset(size.width, size.height), Offset(size.width - cornerSize, size.height), stroke*2)
                             drawLine(primaryColor, Offset(size.width, size.height), Offset(size.width, size.height - cornerSize), stroke*2)

                        }

                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Title with template name
                            Text(
                                text = "ROSTRY ${template.name.uppercase()}",
                                color = primaryColor,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                letterSpacing = 2.sp
                            )

                            Spacer(Modifier.height(16.dp))

                            // Bird Image with parallax offset
                            Box(
                                modifier = Modifier
                                    .size(200.dp)
                                    .offset {
                                        // Parallax: image moves opposite to tilt
                                        IntOffset(
                                            x = if (enableTilt) (-tiltState.x * 15).toInt() else 0,
                                            y = if (enableTilt) (-tiltState.y * 15).toInt() else 0
                                        )
                                    }
                                    .border(2.dp, primaryColor, CircleShape)
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .background(Color.DarkGray)
                            ) {
                                val model = product.imageUrls.firstOrNull() ?: product.qrCodeUrl
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        ImageRequest.Builder(LocalContext.current)
                                            .data(model)
                                            .crossfade(true)
                                            .build()
                                    ),
                                    contentDescription = "Bird Photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(Modifier.height(16.dp))

                            // Name with parallax
                            Text(
                                text = product.name.uppercase(),
                                color = textColor,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.offset {
                                    IntOffset(
                                        x = if (enableTilt) (tiltState.x * 5).toInt() else 0,
                                        y = if (enableTilt) (tiltState.y * 5).toInt() else 0
                                    )
                                }
                            )

                            Text(
                                text = product.breed ?: "Unknown Breed",
                                color = textColor.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Spacer(Modifier.height(24.dp))

                            // Stats Grid with template styling
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                    .border(1.dp, primaryColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                    StatItem("AGE", "${product.ageWeeks ?: "?"} WKS", primaryColor, textColor)
                                    StatItem("WEIGHT", "${product.weightGrams?.let { it / 1000 } ?: "?"} KG", primaryColor, textColor)
                                }
                                Spacer(Modifier.height(12.dp))
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                    StatItem("COLOR", product.colorTag ?: product.color ?: "-", primaryColor, textColor)
                                    StatItem("WINS", "0", primaryColor, textColor)
                                }
                                // Mini-Pedigree Row (Sire/Dam)
                                Spacer(Modifier.height(12.dp))
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                    val sireId = product.parentMaleId?.take(8) ?: "-"
                                    val damId = product.parentFemaleId?.take(8) ?: "-"
                                    StatItem("SIRE", sireId, primaryColor, textColor)
                                    StatItem("DAM", damId, primaryColor, textColor)
                                }
                            }
                            
                            Spacer(modifier = Modifier.weight(1f))
                            
                            // App Logo watermark
                            Text(
                                "ROSTRY",
                                color = primaryColor.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier,
        update = { view ->
            onReadyToCapture(view)
        }
    )
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    labelColor: Color = Color(0xFFFFD700),
    valueColor: Color = Color.White
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = labelColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(value, color = valueColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

