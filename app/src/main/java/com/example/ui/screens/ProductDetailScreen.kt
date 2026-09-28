package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.ProductEntity
import com.example.ui.Screen
import com.example.ui.UnikoViewModel
import com.example.ui.theme.*

@Composable
fun ProductDetailScreen(
    productId: String,
    viewModel: UnikoViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val products by viewModel.products.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val isFav = remember(favorites, productId) { favorites.any { it.itemId == productId } }

    val product = products.firstOrNull { it.id == productId }

    var quantity by remember { mutableIntStateOf(1) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Desc, 1: Specs, 2: Reviews
    var selectedImageIndex by remember { mutableIntStateOf(0) }

    val scrollState = rememberScrollState()

    val carouselImages = remember(product) {
        listOf(
            product?.imageUrl ?: "",
            "https://lh3.googleusercontent.com/aida-public/AB6AXuDl31vxuQFKGFy5na0ynm5XusgOQ7w96ZtHC6caCaXEVoTA-UaH2i9miAFA2ZNtrtPPt3oXvHCMNo5r_ps0Pl7IKMCrqYc6eWkW4B_7q89JlXG5OP2u4iR1YLJYV570JtmuE93HNCvzSzzVJUaYXQ8F6zPr7g6TjN_2QHf_ZcFqraW7ZydsGMz-9NRalfv8EQtUl5qnZvJiALb6l1g-qtpdl2JW3hsy33c0wrG1hD_o8oxAMFHq_qum8w",
            "https://lh3.googleusercontent.com/aida-public/AB6AXuA9lpUALMzqpBlie4RPAy4-cTfoKOaZ_J7jd9ETwa1duAITR3_4fKJ1JgeRt4ILa75204--LlSGIRAajAR3vTQvEtzVklSVKgvcox9kUwmiRa8slQx1VrrED8XsWXKUe2r7dVoM_qzPq-xHANdrptfsOSGWDron1Yn0Uy8z9PlkrpSsiL0_EtfTLPFRh_wAN-ZgXRVSVApm6GMDXRLEDEE1TCa9mGVbw6VgrS01H7PTlKdwpGCUKUh2Mg",
            "https://lh3.googleusercontent.com/aida-public/AB6AXuCVaoL8-jHGOq3tMtFswGhnIsZQe-gAWNNteZ_M9-QjZ3fHRYPDaZkc9h1oVKYW5TKTVak7OTN5ZqW_avwMQRP7w0O4ZOsDWRrS-yMPlmHHf72ViaM6rIJM-7sHZYuUv3F91pFbmGZsExiy7pZ4mzU-B1VGvPiGJSZ1GPUfIlG30zWlRkJtPJSopfXskqlA6g4xMBAP6HsELB98VT7HFY9Mo5bWOZs4jcy_V1sdK8bsVKFH-PhOfXLBFQ"
        )
    }

    if (product == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (products.isEmpty()) {
                    CircularProgressIndicator(color = CrimsonAccent)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Cargando producto…", color = Tertiary, fontSize = 13.sp)
                } else {
                    Icon(
                        imageVector = Icons.Outlined.SearchOff,
                        contentDescription = null,
                        tint = Tertiary,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Producto no encontrado",
                        style = MaterialTheme.typography.titleMedium,
                        color = CaribbeanNavy
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Puede que haya sido retirado del marketplace.",
                        color = Tertiary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.navigateBack() },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonAccent)
                    ) {
                        Text("Volver", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        return
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
                        .height(48.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        IconButton(onClick = { viewModel.navigateBack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Volver",
                                tint = CaribbeanNavy
                            )
                        }
                        Text(
                            text = product.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = CaribbeanNavy
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = {
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "¡Mira este producto en UNIKO-RD!: ${product.title} por RD$ ${product.price}")
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, null)
                                context.startActivity(shareIntent)
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Compartir", tint = CaribbeanNavy)
                        }

                        IconButton(onClick = { viewModel.toggleFavorite(product.id) }) {
                            Icon(
                                imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorito",
                                tint = if (isFav) CrimsonAccent else CaribbeanNavy
                            )
                        }
                    }
                }
            }

            // 1. CAROUSEL SECTION
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.15f)
                    .background(SurfacePure)
            ) {
                AsyncImage(
                    model = carouselImages[selectedImageIndex.coerceIn(0, carouselImages.size - 1)],
                    contentDescription = product.title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )

                // Top Left Badges
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (product.discountPercent > 0) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(CrimsonAccent)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "OFERTA -${product.discountPercent}%",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    if (product.isBestSeller) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(CaribbeanNavy)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "MÁS VENDIDO",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Counter Indicator Bottom Right
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomEnd)
                        .clip(RoundedCornerShape(14.dp))
                        .background(CaribbeanNavy.copy(alpha = 0.85f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                        Text(
                            text = "${selectedImageIndex + 1}/${carouselImages.size}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Bottom Center Dots
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    carouselImages.indices.forEach { index ->
                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .width(if (selectedImageIndex == index) 20.dp else 6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (selectedImageIndex == index) CrimsonAccent else Color.LightGray.copy(alpha = 0.6f))
                                .clickable { selectedImageIndex = index }
                        )
                    }
                }
            }

            // 2. PRODUCT HEADER & PRICING
            Surface(
                color = SurfacePure,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (product.sku.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(SurfaceCanvas)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "SKU: ${product.sku}",
                                    fontSize = 11.sp,
                                    color = Tertiary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.width(0.dp))
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(16.dp))
                            Text(
                                text = "%.1f".format(product.rating),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = CaribbeanNavy
                            )
                            Text(
                                text = "(${product.reviewCount} reseñas)",
                                fontSize = 11.sp,
                                color = Tertiary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = product.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = CaribbeanNavy,
                            lineHeight = 26.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "RD$ %,.0f".format(product.price),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = CaribbeanNavy
                            )
                        )
                        if (product.originalPrice != null && product.originalPrice > product.price) {
                            Text(
                                text = "RD$ %,.0f".format(product.originalPrice),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Tertiary,
                                    textDecoration = TextDecoration.LineThrough
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (product.originalPrice != null && product.originalPrice > product.price) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(PrimaryFixed)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "Ahorras RD$ %,.0f".format(product.originalPrice - product.price),
                                    color = Primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        if (product.inStock) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
                                Text(text = "En stock disponible", color = SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quantity Selector Row
                    Surface(
                        color = SurfaceCanvas,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Cantidad para compra rápida",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = CaribbeanNavy
                                )
                                Text(
                                    text = "Máximo 5 unidades por usuario",
                                    fontSize = 11.sp,
                                    color = Tertiary
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfacePure)
                                    .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                IconButton(
                                    onClick = { if (quantity > 1) quantity-- },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Menos", tint = CaribbeanNavy, modifier = Modifier.size(16.dp))
                                }

                                Text(
                                    text = quantity.toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = CaribbeanNavy
                                )

                                IconButton(
                                    onClick = { if (quantity < 5) quantity++ },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = "Más", tint = CaribbeanNavy, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3. STORE CARD
            Surface(
                color = SurfacePure,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CaribbeanNavy),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = product.storeName
                                        .trim()
                                        .split(Regex("\\s+"))
                                        .filter { it.isNotEmpty() }
                                        .take(2)
                                        .joinToString("") { it.first().uppercase() }
                                        .ifEmpty { "UN" },
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp
                                )
                            }

                            Column {
                                Text(
                                    text = product.storeName.ifEmpty { "Tienda UNIKO-RD" },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = CaribbeanNavy
                                )
                                if (product.isVerified) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(VerifiedBlue.copy(alpha = 0.1f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                            Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = VerifiedBlue, modifier = Modifier.size(12.dp))
                                            Text(text = "Verificada con RNC", fontSize = 10.sp, color = VerifiedBlue, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        TextButton(onClick = { viewModel.navigateTo(Screen.StoreProfile(product.storeId)) }) {
                            Text("Ver tienda >", color = VerifiedBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = Color.LightGray.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = Tertiary, modifier = Modifier.size(15.dp))
                            Text(product.province.ifEmpty { "República Dominicana" }, fontSize = 11.sp, color = Tertiary)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(imageVector = Icons.Default.LocalShipping, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(15.dp))
                            Text(
                                if (product.hasShipping) "Envíos (1-2 días)" else "Recogida en tienda",
                                fontSize = 11.sp,
                                color = SuccessGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 4. DOMINICAN PAYMENT METHODS & TRUST GUARANTEE
            Surface(
                color = SurfacePure,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(VerifiedBlue.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Payments, contentDescription = null, tint = VerifiedBlue, modifier = Modifier.size(18.dp))
                        }
                        Column {
                            Text("Métodos de pago aceptados en RD", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CaribbeanNavy)
                            Text("Tarjeta, transferencia bancaria o contra entrega", fontSize = 11.sp, color = Tertiary)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PaymentPill(icon = Icons.Default.CreditCard, label = "Tarjetas Débito/Crédito")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PaymentPill(icon = Icons.Default.AccountBalance, label = "Transferencia Popular / BHD")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PaymentPill(icon = Icons.Default.LocalAtm, label = "Pago contra entrega (Efectivo RD$)")
                    }

                    Surface(
                        color = SecondaryFixed.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = VerifiedBlue, modifier = Modifier.size(20.dp))
                            Text(
                                text = "Garantía de compra protegida UNIKO-RD: Comercio con Cédula/RNC validado formalmente.",
                                fontSize = 11.sp,
                                color = CaribbeanNavy,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 5. TABS: Descripción / Especificaciones / Reseñas
            Surface(
                color = SurfacePure,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = SurfacePure,
                        contentColor = CrimsonAccent,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = CrimsonAccent,
                                height = 3.dp
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Descripción", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium, fontSize = 13.sp) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Especificaciones", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium, fontSize = 13.sp) }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = { Text("Reseñas (${product.reviewCount})", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Medium, fontSize = 13.sp) }
                        )
                    }

                    Box(modifier = Modifier.padding(16.dp)) {
                        when (selectedTab) {
                            0 -> {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(
                                        text = product.description.ifBlank {
                                            "${product.title} disponible en ${product.storeName.ifEmpty { "UNIKO-RD" }}, ${product.province}. Producto original con garantía del vendedor y soporte a través del sistema de mensajes de UNIKO-RD."
                                        },
                                        style = MaterialTheme.typography.bodyMedium.copy(color = CaribbeanNavy, lineHeight = 20.sp)
                                    )

                                    FeatureCard(
                                        icon = Icons.Default.Shield,
                                        title = "Compra protegida UNIKO-RD",
                                        desc = "Comercio con cédula o RNC validado. Tu pago se coordina directo con la tienda verificada."
                                    )
                                    FeatureCard(
                                        icon = Icons.Default.LocalShipping,
                                        title = if (product.hasShipping) "Envíos a todo el país" else "Recogida en la tienda",
                                        desc = "Entrega coordinada en 24 a 72 horas según tu provincia en República Dominicana."
                                    )
                                    FeatureCard(
                                        icon = Icons.Default.AssignmentReturn,
                                        title = "Devoluciones dentro de 7 días",
                                        desc = "Si el producto llega defectuoso o distinto a la publicación, tienes 7 días para devolverlo."
                                    )
                                }
                            }
                            1 -> {
                                Column(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(1.dp, Color.LightGray.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                ) {
                                    SpecRow("Categoría", product.category, true)
                                    SpecRow("SKU", product.sku.ifEmpty { "—" }, false)
                                    SpecRow("Tienda", product.storeName.ifEmpty { "UNIKO-RD" }, true)
                                    SpecRow("Ubicación", product.province.ifEmpty { "República Dominicana" }, false)
                                    SpecRow("Envío", if (product.hasShipping) "Envío nacional" else "Recogida local", true)
                                }
                            }
                            2 -> {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    ReviewItem(
                                        name = "Carlos M.",
                                        city = "Santo Domingo, D.N.",
                                        comment = "Excelente producto 100% original. Llegó al día siguiente por mensajería en Bella Vista. La tienda respondió rápido por WhatsApp."
                                    )
                                    ReviewItem(
                                        name = "Laura Pimentel",
                                        city = "Santiago de los Caballeros",
                                        comment = "Muy buena atención, me enviaron la factura con RNC sin problemas. Recomendado para comprar desde UNIKO-RD."
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 6. PRODUCTOS RELACIONADOS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Productos relacionados",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = CaribbeanNavy)
                )
                Text("Ver todos", color = VerifiedBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val related = products.filter { it.id != product.id }.take(3)
                items(related) { rel ->
                    Surface(
                        modifier = Modifier
                            .width(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.navigateTo(Screen.ProductDetail(rel.id)) },
                        color = SurfacePure,
                        shadowElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            AsyncImage(
                                model = rel.imageUrl,
                                contentDescription = rel.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceCanvas)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = rel.title,
                                style = MaterialTheme.typography.titleSmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                                color = CaribbeanNavy,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(rel.storeName, fontSize = 10.sp, color = Tertiary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "RD$ %,.0f".format(rel.price),
                                fontWeight = FontWeight.ExtraBold,
                                color = CaribbeanNavy,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // STICKY BOTTOM ACTION BAR (Direct order & Store contact flow)
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding(),
            color = SurfacePure,
            shadowElevation = 12.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Call store button
                OutlinedIconButton(
                    onClick = {
                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:8095550199"))
                        context.startActivity(dialIntent)
                    },
                    modifier = Modifier.size(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, CaribbeanNavy.copy(alpha = 0.2f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Llamar a la tienda",
                        tint = CaribbeanNavy
                    )
                }

                // Add to Cart button
                OutlinedButton(
                    onClick = { viewModel.addToCart(product.id, quantity) },
                    modifier = Modifier
                        .height(50.dp)
                        .testTag("detail_add_to_cart_button"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, VerifiedBlue),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Icon(imageVector = Icons.Default.AddShoppingCart, contentDescription = null, tint = VerifiedBlue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Al Carrito", color = VerifiedBlue, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                // Primary Contact via WhatsApp / Direct Checkout
                Button(
                    onClick = {
                        val url = "https://wa.me/18095550199?text=Hola%20${product.storeName}%2C%20estoy%20interesado%20en%20el%20producto%20${product.title}%20por%20RD%24${product.price}%20visto%20en%20UNIKO-RD."
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            viewModel.showToast("WhatsApp: +1 (809) 555-0199")
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("detail_contact_store_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonAccent),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Contactar Tienda",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentPill(icon: ImageVector, label: String) {
    Surface(
        color = SurfaceCanvas,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = VerifiedBlue, modifier = Modifier.size(16.dp))
            Text(text = label, fontSize = 11.sp, color = CaribbeanNavy, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun FeatureCard(icon: ImageVector, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceCanvas)
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = VerifiedBlue, modifier = Modifier.size(20.dp))
        Column {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CaribbeanNavy)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = desc, fontSize = 11.sp, color = Tertiary, lineHeight = 15.sp)
        }
    }
}

@Composable
private fun SpecRow(label: String, value: String, isEven: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isEven) SurfaceCanvas else SurfacePure)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = Tertiary)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = CaribbeanNavy)
    }
}

@Composable
private fun ReviewItem(name: String, city: String, comment: String) {
    Surface(
        color = SurfaceCanvas,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CaribbeanNavy)
                Text(text = city, fontSize = 10.sp, color = Tertiary)
            }
            Row(modifier = Modifier.padding(vertical = 2.dp)) {
                repeat(5) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(14.dp))
                }
            }
            Text(text = comment, fontSize = 11.sp, color = CaribbeanNavy, lineHeight = 15.sp)
        }
    }
}
