package com.rio.rostry.ui.enthusiast.certificate

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.rio.rostry.data.database.entity.ProductEntity
import java.text.SimpleDateFormat
import java.util.*

/**
 * Digital Certificate Composable - Visual bird certificate with QR code.
 * Can be captured as bitmap for sharing.
 */
@Composable
fun DigitalCertificateComposable(
    product: ProductEntity,
    ownerName: String,
    lineageSummary: String?,
    modifier: Modifier = Modifier
) {
    val qrBitmap = remember(product.productId) {
        generateQRCode("rostry://product/${product.productId}/lineage", 200)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFFFF8E1), Color(0xFFFFECB3))
                    )
                )
                .border(4.dp, Color(0xFFD4AF37), RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Certificate Header
                Text(
                    "🏆",
                    fontSize = 48.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "CERTIFICATE OF OWNERSHIP",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5D4037),
                    textAlign = TextAlign.Center
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = Color(0xFFD4AF37)
                )

                // Bird Details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CertificateField("Name", product.name)
                    CertificateField("Breed", product.breed ?: "Unknown")
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CertificateField("Weight", "${product.weightGrams?.toInt() ?: 0}g")
                    CertificateField("Gender", product.gender ?: "Unknown")
                }

                Spacer(Modifier.height(16.dp))

                // Lineage
                lineageSummary?.let {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.5f)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Lineage",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF5D4037)
                            )
                            Text(
                                it,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF795548)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // QR Code
                qrBitmap?.let { bitmap ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White
                    ) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "QR Code",
                            modifier = Modifier
                                .size(120.dp)
                                .padding(8.dp)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Scan for verification",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF795548)
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Owner & Date
                HorizontalDivider(color = Color(0xFFD4AF37))
                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Owner",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF795548)
                        )
                        Text(
                            ownerName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF5D4037)
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "Issued",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF795548)
                        )
                        Text(
                            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF5D4037)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Footer
                Text(
                    "ROSTRY - Digital Poultry Management",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF795548)
                )
            }
        }
    }
}

@Composable
private fun CertificateField(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF795548)
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF5D4037)
        )
    }
}

/**
 * Generate a QR code bitmap from content.
 */
private fun generateQRCode(content: String, size: Int): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        bitmap
    } catch (e: Exception) {
        null
    }
}
