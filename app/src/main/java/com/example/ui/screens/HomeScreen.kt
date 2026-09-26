package com.example.ui.screens

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.*
import com.example.ui.Screen
import com.example.ui.UnikoViewModel
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: UnikoViewModel,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()
    val stores by viewModel.stores.collectAsState()
    val services by viewModel.services.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val favSet = remember(favorites) { favorites.map { it.itemId }.toSet() }

    val scrollState = rememberScrollState()

    Box(modifier = modifier.fillMaxSize().background(SurfaceCanvas)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 80.dp)
        ) {
            // Search & Location Header Bar
            Surface(
                color = SurfacePure,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Search Bar
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(SurfaceCanvas)
                            .border(1.dp, Color.LightGray.copy(alpha = 0.4f), RoundedCornerShape(22.dp))
                            .clickable { viewModel.navigateTo(Screen.Products) }
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Tertiary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Buscar en colmados, tiendas y se...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Tertiary.copy(alpha = 0.8f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Province Selector Chip
                    Row(
                        modifier = Modifier
                            .height(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(SurfaceContainerLow)
                            .border(1.dp, CaribbeanNavy.copy(alpha = 0.1f), RoundedCornerShape(22.dp))
                            .clickable { viewModel.isFilterSheetVisible.value = true }
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = CrimsonAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "D.N.",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = CaribbeanNavy
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = Tertiary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 1. HERO BANNER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                VerifiedBlue,
                                CaribbeanNavy
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Badge: 100% DOMINICANA
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = WarningAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "100% DOMINICANA",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "El marketplace de\nRepública Dominicana",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            lineHeight = 30.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "+5,000 Productos y Servicios locales en Santo Domingo, Santiago y todo el país.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SurfaceBright.copy(alpha = 0.9f),
                            lineHeight = 16.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3 Metric Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricPill(
                            title = "5K+",
                            subtitle = "Ofertas Activas",
                            modifier = Modifier.weight(1f)
                        )
                        MetricPill(
                            title = "32",
                            subtitle = "Provincias",
                            modifier = Modifier.weight(1f)
                        )
                        MetricPill(
                            title = "99%",
                            subtitle = "Verificados",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Primary Action Button
                    Button(
                        onClick = { viewModel.navigateTo(Screen.Products) },
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonAccent),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("hero_explore_button")
                    ) {
                        Text(
                            text = "Explorar Ofertas Locales",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. TRUST STRIP
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                color = SurfacePure,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    TrustItem(
                        icon = Icons.Default.LocalShipping,
                        title = "Envíos a todo el país",
                        subtitle = "Nacional y expreso"
                    )
                    TrustItem(
                        icon = Icons.Default.VerifiedUser,
                        title = "Comercios verificados",
                        subtitle = "Cédula & RNC seguro"
                    )
                    TrustItem(
                        icon = Icons.Default.Payments,
                        title = "Pago flexible",
                        subtitle = "Contra entrega / Transf."
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3. CATEGORÍAS POPULARES
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Categorías Populares",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = CaribbeanNavy
                    )
                )
                Text(
                    text = "Ver todas",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Secondary,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.clickable { viewModel.navigateTo(Screen.Products) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                val categories = listOf(
                    CategoryItemData("Tecnología", Icons.Default.Devices, CrimsonAccent.copy(alpha = 0.1f), CrimsonAccent),
                    CategoryItemData("Moda", Icons.Default.Checkroom, VerifiedBlue.copy(alpha = 0.1f), VerifiedBlue),
                    CategoryItemData("Vehículos", Icons.Default.DirectionsCar, SuccessGreen.copy(alpha = 0.1f), SuccessGreen),
                    CategoryItemData("Hogar", Icons.Default.Weekend, WarningAmber.copy(alpha = 0.15f), WarningAmber),
                    CategoryItemData("Servicios", Icons.Default.Handshake, SecondaryContainer.copy(alpha = 0.3f), Secondary)
                )
                items(categories) { cat ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable {
                                if (cat.title == "Servicios") {
                                    viewModel.navigateTo(Screen.Services)
                                } else {
                                    viewModel.selectedCategory.value = cat.title
                                    viewModel.navigateTo(Screen.Products)
                                }
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(cat.bgColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = cat.icon,
                                contentDescription = cat.title,
                                tint = cat.tint,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = cat.title,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = CaribbeanNavy
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. PRODUCTOS DESTACADOS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Productos Destacados",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = CaribbeanNavy
                        )
                    )
                    Text(
                        text = "Comercio seguro y verificado en RD",
                        style = MaterialTheme.typography.bodySmall.copy(color = Tertiary)
                    )
                }
                IconButton(onClick = { viewModel.isFilterSheetVisible.value = true }) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Filtrar",
                        tint = CaribbeanNavy
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2-Column Product Grid for Home
            val displayProducts = products.take(4)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (i in displayProducts.indices step 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ProductCardMini(
                            product = displayProducts[i],
                            isFav = favSet.contains(displayProducts[i].id),
                            onCardClick = { viewModel.navigateTo(Screen.ProductDetail(displayProducts[i].id)) },
                            onFavClick = { viewModel.toggleFavorite(displayProducts[i].id) },
                            modifier = Modifier.weight(1f)
                        )
                        if (i + 1 < displayProducts.size) {
                            ProductCardMini(
                                product = displayProducts[i + 1],
                                isFav = favSet.contains(displayProducts[i + 1].id),
                                onCardClick = { viewModel.navigateTo(Screen.ProductDetail(displayProducts[i + 1].id)) },
                                onFavClick = { viewModel.toggleFavorite(displayProducts[i + 1].id) },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 5. VENDOR CALLOUT BANNER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                CrimsonAccent,
                                Primary
                            )
                        )
                    )
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "¡OPORTUNIDAD LOCAL!",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Vende tus productos en\nUNIKO-RD",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 24.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Sin comisión por los primeros 30 días para comercios dominicanos registrados.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.9f))
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { viewModel.navigateTo(Screen.PublishProduct) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(42.dp)
                    ) {
                        Text(
                            text = "Crear Mi Tienda Gratis",
                            color = CrimsonAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 6. TIENDAS DESTACADAS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tiendas Destacadas",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = CaribbeanNavy
                        )
                    )
                    Text(
                        text = "Comercios oficiales con garantía local",
                        style = MaterialTheme.typography.bodySmall.copy(color = Tertiary)
                    )
                }
                Text(
                    text = "Ver todas",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Secondary,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.clickable { viewModel.navigateTo(Screen.Stores) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                stores.take(3).forEach { store ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { viewModel.navigateTo(Screen.StoreProfile(store.id)) },
                        color = SurfacePure,
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(SecondaryFixed.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = store.avatarInitials,
                                    fontWeight = FontWeight.Bold,
                                    color = CaribbeanNavy,
                                    fontSize = 16.sp
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = store.name,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = CaribbeanNavy
                                    )
                                    if (store.isVerified) {
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = "Verificada",
                                            tint = VerifiedBlue,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = Tertiary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = store.province,
                                        style = MaterialTheme.typography.bodySmall.copy(color = Tertiary),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = WarningAmber,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "%.1f".format(store.rating),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = CaribbeanNavy
                                    )
                                    Text(
                                        text = "(+${store.productsCount * 3} ventas)",
                                        fontSize = 11.sp,
                                        color = Tertiary
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Tertiary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 7. SERVICIOS PROFESIONALES
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Servicios Profesionales",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = CaribbeanNavy
                        )
                    )
                    Text(
                        text = "Contrata técnicos y expertos certificados en tu zona",
                        style = MaterialTheme.typography.bodySmall.copy(color = Tertiary)
                    )
                }
                Text(
                    text = "Explorar",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Secondary,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.clickable { viewModel.navigateTo(Screen.Services) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                services.take(3).forEach { service ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { viewModel.navigateTo(Screen.ServiceDetail(service.id)) },
                        color = SurfacePure,
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SurfaceContainerLow),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (service.category) {
                                        "Reparación & Celulares" -> Icons.Default.Smartphone
                                        "Instalación & Hogar" -> Icons.Default.AcUnit
                                        "Mecánica & Autos" -> Icons.Default.Build
                                        else -> Icons.Default.Spa
                                    },
                                    contentDescription = null,
                                    tint = VerifiedBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(SurfaceContainerLow)
                                        .padding(horizontal = 6.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = service.category,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Tertiary
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = service.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = CaribbeanNavy,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = service.coverage,
                                    style = MaterialTheme.typography.bodySmall.copy(color = Tertiary),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Desde RD$ %,.0f".format(service.priceStarting),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = CrimsonAccent
                                    )
                                    Text(
                                        text = "Contactar >",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = VerifiedBlue
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // FLOATING ACTION PILL: "Asistente UNIKO"
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 80.dp)
        ) {
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .clickable { viewModel.isChatbotVisible.value = true }
                    .testTag("floating_chatbot_button"),
                color = CrimsonAccent,
                shadowElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = null,
                            tint = CrimsonAccent,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "Asistente UNIKO",
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
fun ProductCardMini(
    product: ProductEntity,
    isFav: Boolean,
    onCardClick: () -> Unit,
    onFavClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onCardClick),
        color = SurfacePure,
        shadowElevation = 1.dp
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(SurfaceCanvas)
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Badges
                if (product.discountPercent > 0) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopStart)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CrimsonAccent)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "-${product.discountPercent}%",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else if (product.isNew) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopStart)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CaribbeanNavy)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Nuevo",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Heart favorite icon
                IconButton(
                    onClick = onFavClick,
                    modifier = Modifier
                        .padding(6.dp)
                        .size(32.dp)
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.85f))
                ) {
                    Icon(
                        imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (isFav) CrimsonAccent else Tertiary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (product.isVerified) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verificado",
                            tint = VerifiedBlue,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                    Text(
                        text = product.storeName,
                        style = MaterialTheme.typography.bodySmall.copy(color = Tertiary, fontSize = 11.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = CaribbeanNavy
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "RD$ %,.0f".format(product.price),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = CrimsonAccent
                        )
                    )
                    if (product.originalPrice != null) {
                        Text(
                            text = "RD$ %,.0f".format(product.originalPrice),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Tertiary,
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Envío disponible",
                        fontSize = 11.sp,
                        color = SuccessGreen,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricPill(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.12f))
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp
            )
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun TrustItem(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = VerifiedBlue,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = CaribbeanNavy
            ),
            textAlign = TextAlign.Center
        )
        Text(
            text = subtitle,
            fontSize = 9.sp,
            color = Tertiary,
            textAlign = TextAlign.Center
        )
    }
}

data class CategoryItemData(
    val title: String,
    val icon: ImageVector,
    val bgColor: Color,
    val tint: Color
)
