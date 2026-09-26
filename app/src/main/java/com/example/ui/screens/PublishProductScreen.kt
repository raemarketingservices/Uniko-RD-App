package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.ProductEntity
import com.example.ui.Screen
import com.example.ui.UnikoViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishProductScreen(
    viewModel: UnikoViewModel,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var title by remember { mutableStateOf("iPhone 15 Pro Max 256GB Titanium Natural") }
    var description by remember { mutableStateOf("Equipo completamente sellado de fábrica con garantía de 1 año con Apple y 3 meses local con factura con valor fiscal (NCF). Incluye cable trenzado USB-C original y caja intacta.") }
    var category by remember { mutableStateOf("Tecnología & Celulares") }
    var province by remember { mutableStateOf("Santo Domingo D.N.") }
    var currentPriceText by remember { mutableStateOf("68,500") }
    var previousPriceText by remember { mutableStateOf("74,000") }
    var offerNationwideShipping by remember { mutableStateOf(true) }

    var isSubmitting by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableFloatStateOf(0.85f) }

    val scrollState = rememberScrollState()

    // Calculated discount
    val currentPrice = currentPriceText.replace(",", "").toDoubleOrNull() ?: 0.0
    val previousPrice = previousPriceText.replace(",", "").toDoubleOrNull()
    val discountPercent = remember(currentPrice, previousPrice) {
        if (previousPrice != null && previousPrice > currentPrice && previousPrice > 0) {
            (((previousPrice - currentPrice) / previousPrice) * 100).toInt()
        } else 0
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceCanvas)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 90.dp)
        ) {
            // TOP BAR
            Surface(
                color = SurfacePure,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(52.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        IconButton(onClick = { viewModel.navigateBack() }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar", tint = CaribbeanNavy)
                        }

                        Column {
                            Text(
                                text = "Publicar Producto",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = CaribbeanNavy
                                )
                            )
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = VerifiedBlue, modifier = Modifier.size(12.dp))
                                Text("Tienda: Tech Caribe RD (Verificada)", fontSize = 11.sp, color = VerifiedBlue, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick = { viewModel.showToast("Borrador guardado localmente") }) {
                            Icon(imageVector = Icons.Default.SaveAs, contentDescription = "Guardar", tint = Tertiary)
                        }
                        IconButton(onClick = { viewModel.showToast("Guía de requisitos y fotos disponible") }) {
                            Icon(imageVector = Icons.Default.HelpOutline, contentDescription = "Ayuda", tint = Tertiary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 1. BANNER: Publicando en Tech Caribe RD
            Surface(
                color = SurfacePure,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, VerifiedBlue.copy(alpha = 0.2f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(VerifiedBlue.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = VerifiedBlue, modifier = Modifier.size(18.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Publicando en", fontSize = 13.sp, color = CaribbeanNavy)
                            Text("Tech Caribe RD", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = VerifiedBlue)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SuccessGreen.copy(alpha = 0.12f))
                                    .padding(horizontal = 6.dp, vertical = 1.dp)
                            ) {
                                Text("RNC Verificado", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Tu cuenta cumple con la validación de comerciante. ¿Requieres otra tienda?",
                            fontSize = 11.sp,
                            color = Tertiary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. FORM FIELDS CONTAINER
            Surface(
                color = SurfacePure,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Step 1: Título
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("1. Título del producto *", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CaribbeanNavy)
                            Text("${title.length} / 120", fontSize = 11.sp, color = Tertiary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = title,
                            onValueChange = { if (it.length <= 120) title = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("publish_title_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VerifiedBlue,
                                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.6f)
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Sé específico: incluye marca, modelo, capacidad o atributos clave.", fontSize = 11.sp, color = Tertiary)
                    }

                    // Step 2: Descripción
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("2. Descripción *", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CaribbeanNavy)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(13.dp))
                                Text("Mínimo 20 car. cumplido", fontSize = 10.sp, color = SuccessGreen, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .testTag("publish_desc_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VerifiedBlue,
                                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.6f)
                            )
                        )
                    }

                    // Step 3: Código SKU
                    Column {
                        Text("3. Código SKU (Inventario)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Tertiary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            color = SurfaceContainerHighest.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null, tint = Tertiary, modifier = Modifier.size(18.dp))
                                Text("AUTO - GENERADO - POSTGRES", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CaribbeanNavy.copy(alpha = 0.7f))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("ⓘ Se genera automáticamente al guardar por el sistema de base de datos.", fontSize = 11.sp, color = Tertiary)
                    }

                    // Step 4 & 5: Categoría & Ubicación
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("4. Categoría *", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CaribbeanNavy)
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                color = SurfaceCanvas,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(category, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = CaribbeanNavy)
                                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = Tertiary)
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("5. Ubicación (Provincia) *", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CaribbeanNavy)
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                color = SurfaceCanvas,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(province, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = CaribbeanNavy)
                                    Icon(imageVector = Icons.Default.PinDrop, contentDescription = null, tint = CrimsonAccent, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    // Step 6: Precios en Moneda Dominicana (RD$)
                    Column {
                        Text("6. Precios en Moneda Dominicana (RD$)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CaribbeanNavy)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Precio de Venta *", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = CaribbeanNavy)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = currentPriceText,
                                    onValueChange = { currentPriceText = it },
                                    prefix = { Text("RD$ ", fontWeight = FontWeight.Bold, color = CaribbeanNavy, fontSize = 13.sp) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text("Precio Anterior (Opcional)", fontSize = 11.sp, color = Tertiary)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = previousPriceText,
                                    onValueChange = { previousPriceText = it },
                                    prefix = { Text("RD$ ", color = Tertiary, fontSize = 13.sp) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )
                            }
                        }

                        if (discountPercent > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = SuccessGreen.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.25f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                    Text(
                                        text = "✓ Descuento del $discountPercent% calculado (El precio anterior es mayor que el precio de venta)",
                                        fontSize = 11.sp,
                                        color = SuccessGreen,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    // Step 7: Envío Switch
                    Surface(
                        color = SurfaceContainerLow,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(VerifiedBlue.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.LocalShipping, contentDescription = null, tint = VerifiedBlue, modifier = Modifier.size(20.dp))
                                }
                                Column {
                                    Text("7. Ofrecer envío a todo el país", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CaribbeanNavy)
                                    Text("Envío express disponible en Santo Domingo, Santiago y provincias.", fontSize = 10.sp, color = Tertiary)
                                }
                            }
                            Switch(
                                checked = offerNationwideShipping,
                                onCheckedChange = { offerNationwideShipping = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = VerifiedBlue)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. FOTOS & VIDEOS SECTION
            Surface(
                color = SurfacePure,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Fotos
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null, tint = CrimsonAccent, modifier = Modifier.size(20.dp))
                            Text("Fotos del producto", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CaribbeanNavy)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceContainerHigh)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("3/10", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CaribbeanNavy)
                            }
                        }
                        Text("★ Primera foto será la portada", fontSize = 10.sp, color = CrimsonAccent, fontWeight = FontWeight.Bold)
                    }

                    Text("Formatos: JPG, PNG, WebP · Máx 10 MB c/u", fontSize = 11.sp, color = Tertiary)

                    // 4-item photo preview grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PublishPhotoThumb(
                            url = "https://lh3.googleusercontent.com/aida-public/AB6AXuAiFM-j1AUWQVeYGIgCT0pzg3TBQrnex4hSEwoUwIcqw8iBdd8DcKSMgwZPtOL3jVD-uXIOw-4uWzvu2WFZlbqcMUszukgReVQbeeTwEAFVlEJbHnZ-cZSjPfEjgcELS8YtxDaaWY8XklKs5hyRGdXt7Ey9Qm2AVzmM_0Z6EiQSn_VYGDodwD9bbcaZjLjH7jPCZKNMVJP98KYASMKQdIbhZYkstre7mQbVhkDVuBqq2C9Q6p3YuoGiHQ",
                            isCover = true,
                            modifier = Modifier.weight(1f)
                        )
                        PublishPhotoThumb(
                            url = "https://lh3.googleusercontent.com/aida-public/AB6AXuDvxVQ-YNAxnQXt8bHkJKw8Vy8DtyvSnTwrK5qDjy2adu_E0CpXifX0iN_jJJFLSfXc-IYMcv7bC5HCSCEWdtXGn7X9vjIHFxqnLWK_TApL62PArbSuHKFyWrja3IuOkKDpKKGlfCICqckCP2Ocf4ikxIFM-5YUG6G7H7L4PB5ljH0Y80R7UPRZlecKDvg8XGDYLbobF54pT-5T0NFzqE6vo-5fNyPVYkla2IHoWDvxODX11JQuw5JP2w",
                            isCover = false,
                            modifier = Modifier.weight(1f)
                        )
                        PublishPhotoThumb(
                            url = "https://lh3.googleusercontent.com/aida-public/AB6AXuD6lz5SJoH5vUkP9o9KcLIBTic5xsf-7qCoDiY15uKRESGSrBQ0NJh-k4lXXWzWJxNwt8pi0t3BHBLFuE5nC8o2TFtm6NOf-2RSrzIW-SJPaRmidVBtPrcb43yM4DGJ9szGvh6FFvwsamhtzt2RdmjQeFeN-seRvL1OsOTlOauLY20Eh1m6_ddrAQEXT_41s9P7NsV-QghfkWYLM5R2qdfTpFkylD_mSUE3Kvs20koWAQCKc3XzjuTlLA",
                            isCover = false,
                            modifier = Modifier.weight(1f)
                        )
                        // Add Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(VerifiedBlue.copy(alpha = 0.06f))
                                .border(1.5.dp, VerifiedBlue.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                                .clickable { viewModel.showToast("Galería de fotos de República Dominicana abierta") },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, tint = VerifiedBlue, modifier = Modifier.size(24.dp))
                                Text("+ Foto", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VerifiedBlue)
                            }
                        }
                    }

                    Divider(color = Color.LightGray.copy(alpha = 0.3f))

                    // Videos Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.Default.Videocam, contentDescription = null, tint = VerifiedBlue, modifier = Modifier.size(20.dp))
                            Text("Videos del producto", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CaribbeanNavy)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceContainerHigh)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("1/10", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CaribbeanNavy)
                            }
                        }
                        Text("Opcional", fontSize = 11.sp, color = Tertiary)
                    }

                    // Video thumb
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black)
                        ) {
                            AsyncImage(
                                model = "https://lh3.googleusercontent.com/aida-public/AB6AXuA9lVAI9OjbsGnaV5I6YAsX7XeouxvHxpTuhpzKWNuijYZQTkbTIrWxVKVzxaRfKlqLmMfRVkaoRh-Hpc9dzJoei_9mxIczhVpVcUszm6MxosfTRPNQ3wt6FUvRoqtmaq0P68iSkaeWwv0NnuZddmCb1P3DtyamMgMBSDI8m3bpMrf0Sm1L4PS3zfrU2Cnv4qnZxUNEErw2tLGGT4sk5dUJtwTck4BiH17U5aUHlBZPEeuCVxVMD97dhA",
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().alpha(0.8f)
                            )
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.align(Alignment.Center).size(28.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.Black.copy(alpha = 0.8f))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text("0:45", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceCanvas)
                                .border(1.5.dp, Color.LightGray.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .clickable { viewModel.showToast("Subir video de producto (Hasta 50MB)") },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(imageVector = Icons.Default.VideoCall, contentDescription = null, tint = Tertiary, modifier = Modifier.size(24.dp))
                                Text("+ Video", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CaribbeanNavy)
                                Text("Hasta 50MB", fontSize = 9.sp, color = Tertiary)
                            }
                        }

                        Spacer(modifier = Modifier.weight(2f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. STORAGE PROGRESS BAR
            Surface(
                color = SurfacePure,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, VerifiedBlue.copy(alpha = 0.2f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.Default.Sync, contentDescription = null, tint = VerifiedBlue, modifier = Modifier.size(18.dp))
                            Text("Subiendo archivos a storage/marketplace", fontSize = 12.sp, color = CaribbeanNavy, fontWeight = FontWeight.SemiBold)
                        }
                        Text("${(uploadProgress * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CrimsonAccent)
                    }

                    LinearProgressIndicator(
                        progress = { uploadProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = CrimsonAccent,
                        trackColor = SurfaceContainerHigh
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Optimizando resolución para red móvil RD", fontSize = 10.sp, color = Tertiary)
                        Text("2.4 MB de 2.8 MB", fontSize = 10.sp, color = Tertiary)
                    }
                }
            }
        }

        // STICKY BOTTOM ACTION BAR
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding(),
            color = SurfacePure,
            shadowElevation = 12.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { viewModel.showToast("Borrador guardado con éxito") },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, VerifiedBlue),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Drafts, contentDescription = null, tint = VerifiedBlue, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Borrador", color = VerifiedBlue, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            isSubmitting = true
                            coroutineScope.launch {
                                delay(900)
                                val newProd = ProductEntity(
                                    id = "prod_${System.currentTimeMillis()}",
                                    title = title,
                                    storeId = "store_tech_caribe",
                                    storeName = "Tech Caribe RD",
                                    price = currentPrice,
                                    originalPrice = previousPrice,
                                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAiFM-j1AUWQVeYGIgCT0pzg3TBQrnex4hSEwoUwIcqw8iBdd8DcKSMgwZPtOL3jVD-uXIOw-4uWzvu2WFZlbqcMUszukgReVQbeeTwEAFVlEJbHnZ-cZSjPfEjgcELS8YtxDaaWY8XklKs5hyRGdXt7Ey9Qm2AVzmM_0Z6EiQSn_VYGDodwD9bbcaZjLjH7jPCZKNMVJP98KYASMKQdIbhZYkstre7mQbVhkDVuBqq2C9Q6p3YuoGiHQ",
                                    category = "Tecnología",
                                    province = province,
                                    isVerified = true,
                                    isNew = true,
                                    sku = "PRD-${(10000..99999).random()}",
                                    description = description,
                                    discountPercent = discountPercent
                                )
                                viewModel.showToast("¡Producto publicado exitosamente en UNIKO-RD!")
                                isSubmitting = false
                                viewModel.navigateTo(Screen.Products)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonAccent),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("publish_submit_button"),
                        enabled = !isSubmitting
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Publicar Producto", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Al publicar, tu producto aparecerá inmediatamente en el marketplace UNIKO-RD.",
                    fontSize = 10.sp,
                    color = Tertiary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun PublishPhotoThumb(url: String, isCover: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .border(if (isCover) 2.dp else 1.dp, if (isCover) CrimsonAccent else Color.LightGray.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
    ) {
        AsyncImage(
            model = url,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        if (isCover) {
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .align(Alignment.TopStart)
                    .clip(RoundedCornerShape(4.dp))
                    .background(CrimsonAccent)
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text("Portada", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            }
        }
        Box(
            modifier = Modifier
                .padding(4.dp)
                .size(20.dp)
                .align(Alignment.TopEnd)
                .clip(CircleShape)
                .background(CaribbeanNavy.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.White, modifier = Modifier.size(12.dp))
        }
    }
}
