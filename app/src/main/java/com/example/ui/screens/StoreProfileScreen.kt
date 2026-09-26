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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.Screen
import com.example.ui.UnikoViewModel
import com.example.ui.theme.*

@Composable
fun StoreProfileScreen(
    storeId: String,
    viewModel: UnikoViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val stores by viewModel.stores.collectAsState()
    val products by viewModel.products.collectAsState()
    val followedStoreIds by viewModel.followedStoreIds.collectAsState()
    val userRatings by viewModel.userRatings.collectAsState()

    val store = stores.firstOrNull { it.id == storeId } ?: stores.firstOrNull()
    val isFollowed = followedStoreIds.contains(storeId)
    val currentRating = userRatings[storeId] ?: 5

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Productos, 1: Información
    var activeCategoryFilter by remember { mutableStateOf("Todos") }

    val storeProducts = remember(products, storeId) {
        products.filter { it.storeId == storeId || it.storeName.contains("Tech Caribe", ignoreCase = true) }
    }

    val scrollState = rememberScrollState()

    if (store == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = CrimsonAccent)
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
            // STORE COVER BANNER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(CaribbeanNavy)
            ) {
                AsyncImage(
                    model = store.bannerImageUrl,
                    contentDescription = store.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.4f),
                                    Color.Transparent,
                                    CaribbeanNavy.copy(alpha = 0.8f)
                                )
                            )
                        )
                )

                // Top Bar overlay
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.Black.copy(alpha = 0.6f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(SuccessGreen))
                                Text(
                                    text = store.openingHours,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "Conoce la tienda ${store.name} en UNIKO-RD: ${store.description}")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, null))
                            },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Compartir", tint = Color.White)
                        }
                    }
                }
            }

            // STORE IDENTITY CARD (overlapping banner)
            Surface(
                color = SurfacePure,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-30).dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        // Floating Avatar
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(CaribbeanNavy)
                                .border(3.dp, SurfacePure, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = store.avatarInitials,
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp
                                )
                                Text(
                                    text = "UNIKO",
                                    color = SecondaryFixed,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Badges
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (store.isVerified) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(VerifiedBlue.copy(alpha = 0.1f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = VerifiedBlue, modifier = Modifier.size(14.dp))
                                        Text("Verificada", fontSize = 11.sp, color = VerifiedBlue, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            if (store.isVip) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(WarningAmber.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Icon(imageVector = Icons.Default.HotelClass, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(14.dp))
                                        Text("Destacada VIP", fontSize = 11.sp, color = CaribbeanNavy, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = store.name,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = CaribbeanNavy
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = store.description,
                        style = MaterialTheme.typography.bodyMedium.copy(color = Tertiary, lineHeight = 20.sp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Bento Metric Strip (4 pills)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StoreMetricPill(
                                icon = Icons.Default.Devices,
                                title = "Categoría",
                                value = store.category,
                                modifier = Modifier.weight(1f)
                            )
                            StoreMetricPill(
                                icon = Icons.Default.LocationOn,
                                title = "Ubicación",
                                value = store.province,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StoreMetricPill(
                                icon = Icons.Default.Badge,
                                title = "Propietario",
                                value = store.ownerName,
                                modifier = Modifier.weight(1f)
                            )
                            StoreMetricPill(
                                icon = Icons.Default.Groups,
                                title = "Comunidad",
                                value = "${store.followers + if (isFollowed) 1 else 0} seguidores",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.toggleFollowStore(store.id) },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("store_follow_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, if (isFollowed) SuccessGreen else VerifiedBlue),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isFollowed) SuccessGreen.copy(alpha = 0.1f) else VerifiedBlue.copy(alpha = 0.08f)
                            )
                        ) {
                            Icon(
                                imageVector = if (isFollowed) Icons.Default.Check else Icons.Default.PersonAdd,
                                contentDescription = null,
                                tint = if (isFollowed) SuccessGreen else VerifiedBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isFollowed) "Siguiendo" else "Seguir Tienda",
                                fontWeight = FontWeight.Bold,
                                color = if (isFollowed) SuccessGreen else VerifiedBlue,
                                fontSize = 13.sp
                            )
                        }

                        Button(
                            onClick = {
                                val url = "https://wa.me/18095550199?text=Hola%20${store.name}%2C%20vi%20su%20tienda%20en%20UNIKO-RD"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                try {
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    viewModel.showToast("WhatsApp: +1 809-555-0199")
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("store_whatsapp_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonAccent),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Contactar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            // RATINGS & REVIEWS CARD WITH INTERACTIVE 5-STAR MODULE
            Surface(
                color = SurfacePure,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-20).dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
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
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(WarningAmber.copy(alpha = 0.15f))
                                    .border(1.dp, WarningAmber.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("4.9", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = CaribbeanNavy)
                                    Text("de 5.0", fontSize = 9.sp, color = Tertiary)
                                }
                            }

                            Column {
                                Row {
                                    repeat(5) {
                                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(16.dp))
                                    }
                                }
                                Text("Basado en 88 reseñas de compradores", fontSize = 11.sp, color = Tertiary)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SuccessGreen.copy(alpha = 0.1f))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text("100% transacciones seguras", fontSize = 10.sp, color = SuccessGreen, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = Color.LightGray.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Interactive Rating Module
                    Surface(
                        color = SurfaceCanvas,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("CALIFICA ESTA TIENDA", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = CrimsonAccent)
                                Text("Toca para puntuar", fontSize = 11.sp, color = Secondary, fontWeight = FontWeight.Medium)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                (1..5).forEach { star ->
                                    IconButton(
                                        onClick = { viewModel.rateStore(store.id, star) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "$star estrellas",
                                            tint = if (star <= currentRating) WarningAmber else Color.LightGray,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "ⓘ Tu calificación ayuda a la comunidad de UNIKO-RD. Solo usuarios activos pueden calificar.",
                                fontSize = 10.sp,
                                color = Tertiary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            // TABS: PRODUCTOS (142) vs INFORMACIÓN
            Surface(
                color = SurfacePure,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-10).dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceCanvas)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selectedTab == 0) CaribbeanNavy else Color.Transparent)
                                .clickable { selectedTab = 0 }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Productos (142)",
                                color = if (selectedTab == 0) Color.White else Tertiary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selectedTab == 1) CaribbeanNavy else Color.Transparent)
                                .clickable { selectedTab = 1 }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Información",
                                color = if (selectedTab == 1) Color.White else Tertiary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    if (selectedTab == 0) {
                        // Filter Chips
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(listOf("Todos los artículos", "En Oferta", "Audio & Auriculares", "Wearables")) { chip ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (activeCategoryFilter == chip) CaribbeanNavy else SurfacePure)
                                        .border(1.dp, if (activeCategoryFilter == chip) CaribbeanNavy else Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                        .clickable { activeCategoryFilter = chip }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = chip,
                                        fontSize = 11.sp,
                                        fontWeight = if (activeCategoryFilter == chip) FontWeight.Bold else FontWeight.Normal,
                                        color = if (activeCategoryFilter == chip) Color.White else CaribbeanNavy
                                    )
                                }
                            }
                        }

                        // Product Grid (2 columns)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            for (i in storeProducts.indices step 2) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    ProductGridItem(
                                        product = storeProducts[i],
                                        isFav = false,
                                        onCardClick = { viewModel.navigateTo(Screen.ProductDetail(storeProducts[i].id)) },
                                        onFavClick = { viewModel.toggleFavorite(storeProducts[i].id) }
                                    )
                                    if (i + 1 < storeProducts.size) {
                                        ProductGridItem(
                                            product = storeProducts[i + 1],
                                            isFav = false,
                                            onCardClick = { viewModel.navigateTo(Screen.ProductDetail(storeProducts[i + 1].id)) },
                                            onFavClick = { viewModel.toggleFavorite(storeProducts[i + 1].id) }
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    } else {
                        // Store Info Tab
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Datos Registrados del Comercio",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = CaribbeanNavy)
                            )

                            StoreInfoRow(Icons.Default.Person, "Propietario / Representante", store.ownerName, "Identidad Validada por Cédula")
                            StoreInfoRow(Icons.Default.Domain, "Registro Nacional de Contribuyente (RNC)", store.rnc, "DGII Activo y Verificado")
                            StoreInfoRow(Icons.Default.PinDrop, "Ubicación Física", store.address, "Santo Domingo, Distrito Nacional")
                            StoreInfoRow(Icons.Default.CalendarMonth, "Miembro de UNIKO-RD", "Desde Enero 2024", "+1,200 pedidos despachados")

                            Divider(color = Color.LightGray.copy(alpha = 0.4f))

                            Text("Políticas de Envío y Garantía", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CaribbeanNavy)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(imageVector = Icons.Default.LocalShipping, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
                                Text(
                                    text = "Envíos Nacionales: Despachos por Metro Pac, Caribe Tours y Vimenpaq garantizados en 24h laborables en todo el país.",
                                    fontSize = 11.sp,
                                    color = Tertiary,
                                    lineHeight = 16.sp
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = VerifiedBlue, modifier = Modifier.size(18.dp))
                                Text(
                                    text = "Garantía Local: Todos los equipos disponen de 90 a 365 días de garantía directa contra defectos de fábrica en taller en Santo Domingo.",
                                    fontSize = 11.sp,
                                    color = Tertiary,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // FAB: Publicar Producto
        ExtendedFloatingActionButton(
            onClick = { viewModel.navigateTo(Screen.PublishProduct) },
            containerColor = CrimsonAccent,
            contentColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 80.dp)
                .testTag("fab_publish_product")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Publicar Producto", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
private fun StoreMetricPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = SurfaceCanvas,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = VerifiedBlue, modifier = Modifier.size(18.dp))
            Column {
                Text(text = title, fontSize = 10.sp, color = Tertiary)
                Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CaribbeanNavy, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun StoreInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, value: String, sub: String) {
    Surface(
        color = SurfaceCanvas,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = CaribbeanNavy, modifier = Modifier.size(20.dp))
            Column {
                Text(title, fontSize = 10.sp, color = Tertiary)
                Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CaribbeanNavy)
                Text(sub, fontSize = 10.sp, color = VerifiedBlue)
            }
        }
    }
}
