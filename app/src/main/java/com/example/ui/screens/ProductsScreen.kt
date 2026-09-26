package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
fun ProductsScreen(
    viewModel: UnikoViewModel,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val favSet = remember(favorites) { favorites.map { it.itemId }.toSet() }

    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCat by viewModel.selectedCategory.collectAsState()
    val onlyVerified by viewModel.filterOnlyVerified.collectAsState()
    val maxPrice by viewModel.maxPriceFilter.collectAsState()

    // Filtered list
    val filteredProducts = remember(products, searchQuery, selectedCat, onlyVerified, maxPrice) {
        products.filter { p ->
            val matchQuery = searchQuery.isBlank() || p.title.contains(searchQuery, ignoreCase = true) || p.storeName.contains(searchQuery, ignoreCase = true)
            val matchCat = selectedCat == "Todas" || p.category.equals(selectedCat, ignoreCase = true)
            val matchVerified = !onlyVerified || p.isVerified
            val matchPrice = p.price <= maxPrice
            matchQuery && matchCat && matchVerified && matchPrice
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceCanvas)
    ) {
        // TOP APP BAR SPECIFIC FOR PRODUCTS SCREEN (with search header)
        Surface(
            color = SurfacePure,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Row 1: Back, Title, Actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { viewModel.navigateBack() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Atrás",
                                tint = CaribbeanNavy
                            )
                        }

                        // Logo emblem
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(CrimsonAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Text(
                            text = "Productos",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = CaribbeanNavy
                            )
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = { viewModel.navigateTo(Screen.Stores) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Store,
                                contentDescription = "Tiendas",
                                tint = Tertiary
                            )
                        }

                        IconButton(
                            onClick = { viewModel.isCartSheetVisible.value = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Carrito",
                                tint = CaribbeanNavy
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Row 2: Search input bar with mic and Dominican Republic chip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(SurfaceCanvas)
                        .border(1.dp, CaribbeanNavy.copy(alpha = 0.1f), RoundedCornerShape(22.dp))
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Tertiary,
                        modifier = Modifier.size(20.dp)
                    )

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.searchQuery.value = it },
                        placeholder = { Text("Buscar por producto, marca o tienda...", fontSize = 13.sp, color = Tertiary) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("products_search_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            cursorColor = CrimsonAccent
                        )
                    )

                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Búsqueda por voz",
                        tint = Tertiary,
                        modifier = Modifier.size(18.dp)
                    )

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(18.dp)
                            .background(Color.LightGray)
                    )

                    Text(
                        text = "🇩🇴 RD",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CaribbeanNavy
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 3: Horizontal Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Category chip (Tecnología / Todas)
                    item {
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .clickable {
                                    viewModel.selectedCategory.value =
                                        if (selectedCat == "Tecnología") "Todas" else "Tecnología"
                                },
                            color = if (selectedCat == "Tecnología") CaribbeanNavy else SurfacePure,
                            border = BorderStroke(
                                1.dp,
                                if (selectedCat == "Tecnología") CaribbeanNavy else Color.LightGray.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Devices,
                                    contentDescription = null,
                                    tint = if (selectedCat == "Tecnología") Color.White else CaribbeanNavy,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Tecnología",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (selectedCat == "Tecnología") Color.White else CaribbeanNavy
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CrimsonAccent)
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "86",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    // Location Chip
                    item {
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .clickable { viewModel.isFilterSheetVisible.value = true },
                            color = SurfacePure,
                            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Tertiary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Santo Domingo D.N.",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CaribbeanNavy
                                )
                                Icon(
                                    imageVector = Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = Tertiary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Verified Toggle Chip
                    item {
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .clickable { viewModel.filterOnlyVerified.value = !onlyVerified },
                            color = if (onlyVerified) VerifiedBlue.copy(alpha = 0.15f) else SurfacePure,
                            border = BorderStroke(
                                1.dp,
                                if (onlyVerified) VerifiedBlue else Color.LightGray.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = VerifiedBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Verificados",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (onlyVerified) VerifiedBlue else CaribbeanNavy
                                )
                            }
                        }
                    }

                    // Price Max Filter Chip
                    item {
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .clickable { viewModel.isFilterSheetVisible.value = true },
                            color = SurfacePure,
                            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = Tertiary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Máx: RD$ %,.0f".format(maxPrice),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CaribbeanNavy
                                )
                            }
                        }
                    }
                }
            }
        }

        // CONTROL ROW: Count & Sorting
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${filteredProducts.size}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = CaribbeanNavy
                )
                Text(
                    text = "productos encontrados",
                    fontSize = 12.sp,
                    color = Tertiary
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Limpiar",
                    color = CrimsonAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        viewModel.searchQuery.value = ""
                        viewModel.selectedCategory.value = "Todas"
                        viewModel.filterOnlyVerified.value = false
                        viewModel.maxPriceFilter.value = 100000f
                    }
                )

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(14.dp)
                        .background(Color.LightGray)
                )

                Surface(
                    color = SurfacePure,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Ordenar:",
                            fontSize = 11.sp,
                            color = Tertiary
                        )
                        Text(
                            text = "Relevancia",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CaribbeanNavy
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
        }

        // 2-COLUMN PRODUCT GRID
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filteredProducts) { product ->
                ProductGridItem(
                    product = product,
                    isFav = favSet.contains(product.id),
                    onCardClick = { viewModel.navigateTo(Screen.ProductDetail(product.id)) },
                    onFavClick = { viewModel.toggleFavorite(product.id) }
                )
            }

            // Promotional banner at bottom of grid
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    CaribbeanNavy,
                                    Secondary
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingBag,
                                    contentDescription = null,
                                    tint = CrimsonAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "COMPRA SEGURA RD",
                                    color = SecondaryFixed,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "¿Tienes una tienda o negocio local?",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Vende tus productos a nivel nacional hoy mismo.",
                                color = SurfaceBright.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        }

                        Button(
                            onClick = { viewModel.navigateTo(Screen.PublishProduct) },
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonAccent),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Vender",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductGridItem(
    product: ProductEntity,
    isFav: Boolean,
    onCardClick: () -> Unit,
    onFavClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
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

                // Top badges
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
                } else if (product.isBestSeller) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopStart)
                            .clip(RoundedCornerShape(8.dp))
                            .background(VerifiedBlue)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Más vendido",
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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = product.storeName,
                        style = MaterialTheme.typography.bodySmall.copy(color = Tertiary, fontSize = 11.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (product.isVerified) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verificado",
                            tint = VerifiedBlue,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = CaribbeanNavy
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

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
                        text = "%.1f".format(product.rating),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = CaribbeanNavy
                    )
                    Text(
                        text = "(${product.reviewCount})",
                        fontSize = 10.sp,
                        color = Tertiary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (product.originalPrice != null) {
                    Text(
                        text = "RD$ %,.0f".format(product.originalPrice),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Tertiary,
                            textDecoration = TextDecoration.LineThrough,
                            fontSize = 11.sp
                        )
                    )
                }

                Text(
                    text = "RD$ %,.0f".format(product.price),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = CaribbeanNavy
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Envío gratis 24h",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    )
                }
            }
        }
    }
}
