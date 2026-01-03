package com.rio.rostry.ui.enthusiast.components

import android.graphics.Bitmap
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.rio.rostry.data.database.entity.ProductEntity

@Composable
fun RoosterCardView(
    product: ProductEntity,
    modifier: Modifier = Modifier,
    onReadyToCapture: (View) -> Unit = {}
) {
    // Premium Design Constants
    val goldColor = Color(0xFFFFD700)
    val deepRed = Color(0xFF500000) // Deep red/maroon
    val black = Color.Black
    val gradient = Brush.verticalGradient(listOf(deepRed, black))

    // Just wrapping in a Box that we can capture using standard View methods if needed
    // Ideally, for "Clean" capture, we pass the View back
    
    AndroidView(
        factory = { context ->
            androidx.compose.ui.platform.ComposeView(context).apply {
                setContent {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.7f) // Card aspect ratio
                            .background(brush = gradient, shape = RoundedCornerShape(16.dp))
                            .border(4.dp, goldColor, RoundedCornerShape(16.dp))
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
                             drawLine(goldColor, Offset(0f, 0f), Offset(cornerSize, 0f), stroke*2)
                             drawLine(goldColor, Offset(0f, 0f), Offset(0f, cornerSize), stroke*2)
                             // TR
                             drawLine(goldColor, Offset(size.width, 0f), Offset(size.width - cornerSize, 0f), stroke*2)
                             drawLine(goldColor, Offset(size.width, 0f), Offset(size.width, cornerSize), stroke*2)
                             // BL
                             drawLine(goldColor, Offset(0f, size.height), Offset(cornerSize, size.height), stroke*2)
                             drawLine(goldColor, Offset(0f, size.height), Offset(0f, size.height - cornerSize), stroke*2)
                             // BR
                             drawLine(goldColor, Offset(size.width, size.height), Offset(size.width - cornerSize, size.height), stroke*2)
                             drawLine(goldColor, Offset(size.width, size.height), Offset(size.width, size.height - cornerSize), stroke*2)

                        }

                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "ROSTRY LEGEND",
                                color = goldColor,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                letterSpacing = 2.sp
                            )

                            Spacer(Modifier.height(16.dp))

                            // Bird Image
                            Box(
                                modifier = Modifier
                                    .size(200.dp)
                                    .border(2.dp, goldColor, CircleShape)
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

                            // Name
                            Text(
                                text = product.name.uppercase(),
                                color = Color.White,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = product.breed ?: "Unknown Breed",
                                color = Color.LightGray,
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Spacer(Modifier.height(24.dp))

                            // Stats Grid
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                    .border(1.dp, goldColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                    StatItem("AGE", "${product.ageWeeks ?: "?"} WKS")
                                    StatItem("WEIGHT", "${product.weightGrams?.let { it / 1000 } ?: "?"} KG")
                                }
                                Spacer(Modifier.height(12.dp))
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                    StatItem("COLOR", product.colorTag ?: product.color ?: "-")
                                    StatItem("WINS", "0") // Placeholder for wins
                                }
                                // Mini-Pedigree Row (Sire/Dam)
                                Spacer(Modifier.height(12.dp))
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                    // Use parentMaleId/parentFemaleId for bloodline
                                    val sireId = product.parentMaleId?.take(8) ?: "-"
                                    val damId = product.parentFemaleId?.take(8) ?: "-"
                                    StatItem("SIRE", sireId)
                                    StatItem("DAM", damId)
                                }
                            }
                            
                            Spacer(modifier = Modifier.weight(1f))
                            
                            // App Logo watermark
                            Text(
                                "ROSTRY",
                                color = goldColor.copy(alpha = 0.8f),
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
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color(0xFFFFD700), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}
