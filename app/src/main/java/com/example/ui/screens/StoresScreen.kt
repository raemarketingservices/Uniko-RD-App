package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.StoreEntity
import com.example.ui.Screen
import com.example.ui.UnikoViewModel
import com.example.ui.theme.*

@Composable
fun StoresScreen(
    viewModel: UnikoViewModel,
    modifier: Modifier = Modifier
) {
    val stores by viewModel.stores.collectAsState()
    val followedStoreIds by viewModel.followedStoreIds.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedLocation by remember { mutableStateOf("Toda RD") }
    var vipOnly by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Todas") }

    val filteredStores = remember(stores, searchQuery, selectedLocation, vipOnly, selectedCategory) {
        stores.filter { s ->
            val matchQuery = searchQuery.isBlank() || s.name.contains(searchQuery, ignoreCase = true) || s.rnc.contains(searchQuery, ignoreCase = true)
            val matchVip = !vipOnly || s.isVip
            val matchLocation = selectedLocation == "Toda RD" || s.province.contains(selectedLocation.take(7), ignoreCase = true)
            val matchCat = selectedCategory == "Todas" || s.category.contains(selectedCategory, ignoreCase = true)
            matchQuery && matchVip && matchLocation && matchCat
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceCanvas)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // TOP BAR & SEARCH
            item {
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
                                Image(
                                    painter = painterResource(id = R.drawable.ic_uniko_logo_square),
                                    contentDescription = "UNIKO-RD",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.width(44.dp)
                                )
                                Column {
                                    Text(
                                        text = "UNIKO-RD",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = CaribbeanNavy
                                        )
                                    )
                                    Text(
                                        text = "• REPÚBLICA DOMINICANA",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = CrimsonAccent,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(onClick = { viewModel.showToast("No hay notificaciones nuevas") }) {
                                    Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notificaciones", tint = CaribbeanNavy)
                                }
                                IconButton(onClick = { viewModel.isFilterSheetVisible.value = true }) {
                                    Icon(imageVector = Icons.Default.Tune, contentDescription = "Filtrar", tint = CaribbeanNavy)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Search Bar
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
                            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Tertiary, modifier = Modifier.size(20.dp))
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Buscar tienda o comercio por nombre o RNC...", fontSize = 12.sp, color = Tertiary) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("stores_search_input"),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    cursorColor = CrimsonAccent
                                )
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfacePure)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(VerifiedBlue))
                                    Text("RNC", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CaribbeanNavy)
                                }
                            }
                        }
                    }
                }
            }

            // HERO & BREADCRUMBS
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Marketplace", fontSize = 11.sp, color = Tertiary)
                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Tertiary, modifier = Modifier.size(12.dp))
                        Text("Directorio Oficial", fontSize = 11.sp, color = VerifiedBlue, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Tiendas Dominicanas",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = CaribbeanNavy
                        )
                    )
                    Text(
                        text = "Comercios formales, distribuidores y artesanos con validación tributaria y garantía local.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Tertiary)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        color = SurfacePure,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(SuccessGreen))
                            Text(
                                text = "32 tiendas registradas y verificadas",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CaribbeanNavy
                            )
                        }
                    }
                }
            }

            // HORIZONTAL FILTER CHIPS
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Location chip
                        item {
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(18.dp))
                                    .clickable { selectedLocation = if (selectedLocation == "Toda RD") "Santo Domingo" else "Toda RD" },
                                color = if (selectedLocation != "Toda RD") CaribbeanNavy else SurfacePure,
                                border = BorderStroke(1.dp, if (selectedLocation != "Toda RD") CaribbeanNavy else Color.LightGray.copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(18.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = if (selectedLocation != "Toda RD") Color.White else CrimsonAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Santo Domingo D.N. (18)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selectedLocation != "Toda RD") Color.White else CaribbeanNavy
                                    )
                                }
                            }
                        }

                        // VIP Toggle Chip
                        item {
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(18.dp))
                                    .clickable { vipOnly = !vipOnly },
                                color = if (vipOnly) WarningAmber.copy(alpha = 0.15f) else SurfacePure,
                                border = BorderStroke(1.dp, if (vipOnly) WarningAmber else Color.LightGray.copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(18.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.HotelClass,
                                        contentDescription = null,
                                        tint = WarningAmber,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Destacadas VIP",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CaribbeanNavy
                                    )
                                }
                            }
                        }

                        item {
                            FilterChipText(text = "Santiago (7)", isSelected = selectedLocation == "Santiago", onClick = { selectedLocation = if (selectedLocation == "Santiago") "Toda RD" else "Santiago" })
                        }
                        item {
                            FilterChipText(text = "La Altagracia (4)", isSelected = selectedLocation == "La Altagracia", onClick = { selectedLocation = if (selectedLocation == "La Altagracia") "Toda RD" else "La Altagracia" })
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Categories sub-row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("CATEGORÍAS:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Tertiary)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(listOf("Todas", "Tecnología", "Moda", "Repuestos", "Hogar", "Artesanía")) { cat ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (selectedCategory == cat) VerifiedBlue else SurfacePure)
                                        .border(1.dp, if (selectedCategory == cat) VerifiedBlue else Color.LightGray.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                                        .clickable { selectedCategory = cat }
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = cat,
                                        fontSize = 11.sp,
                                        fontWeight = if (selectedCategory == cat) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectedCategory == cat) Color.White else CaribbeanNavy
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // STORE CARDS LIST
            items(filteredStores) { store ->
                val isFollowed = followedStoreIds.contains(store.id)
                StoreDirectoryCard(
                    store = store,
                    isFollowed = isFollowed,
                    onViewClick = { viewModel.navigateTo(Screen.StoreProfile(store.id)) },
                    onFollowClick = { viewModel.toggleFollowStore(store.id) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            // Benefit Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
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
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(imageVector = Icons.Default.Store, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                            Text(
                                text = "Vende con confianza en UNIKO-RD",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Validación RNC garantizada, pasarela de pago segura y logística en toda Quisqueya.",
                            fontSize = 11.sp,
                            color = SecondaryFixed
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { viewModel.navigateTo(Screen.PublishProduct) },
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonAccent),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(38.dp)
                        ) {
                            Text("Conocer Requisitos", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        // CONTEXTUAL FLOATING ACTION BUTTON: "¿Tienes un negocio? Crea tu tienda gratis"
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 80.dp)
        ) {
            Button(
                onClick = { viewModel.navigateTo(Screen.PublishProduct) },
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonAccent),
                shape = RoundedCornerShape(24.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("fab_create_store_free")
            ) {
                Icon(
                    imageVector = Icons.Default.AddBusiness,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "¿Tienes un negocio? Crea tu tienda gratis",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun StoreDirectoryCard(
    store: StoreEntity,
    isFollowed: Boolean,
    onViewClick: () -> Unit,
    onFollowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onViewClick),
        color = SurfacePure,
        shadowElevation = 1.dp
    ) {
        Column {
            if (store.isVip) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(CrimsonAccent, VerifiedBlue, CrimsonAccent)
                            )
                        )
                )
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CaribbeanNavy),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = store.avatarInitials,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = store.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = CaribbeanNavy
                            )
                            if (store.isVerified) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(VerifiedBlue.copy(alpha = 0.1f))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text("Verificada", fontSize = 9.sp, color = VerifiedBlue, fontWeight = FontWeight.Bold)
                                }
                            }
                            if (store.isVip) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(WarningAmber.copy(alpha = 0.15f))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text("VIP", fontSize = 9.sp, color = WarningAmber, fontWeight = FontWeight.ExtraBold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(store.category, fontSize = 11.sp, color = Tertiary)
                            Text("•", fontSize = 11.sp, color = Tertiary)
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = CrimsonAccent, modifier = Modifier.size(12.dp))
                            Text(store.province, fontSize = 11.sp, color = CaribbeanNavy, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = Color.LightGray.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(14.dp))
                        Text("%.1f".format(store.rating), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CaribbeanNavy)
                        Text("(${store.reviewCount} reseñas)", fontSize = 11.sp, color = Tertiary)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("${store.followers} seg.", fontSize = 11.sp, color = Tertiary)
                        Text("•", fontSize = 11.sp, color = Tertiary)
                        Text("${store.productsCount} productos", fontSize = 11.sp, color = CaribbeanNavy, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Actions row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onViewClick,
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonAccent),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                    ) {
                        Text("Ver Tienda", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                    }

                    OutlinedButton(
                        onClick = onFollowClick,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, if (isFollowed) SuccessGreen else VerifiedBlue),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isFollowed) SuccessGreen.copy(alpha = 0.1f) else Color.Transparent
                        ),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Icon(
                            imageVector = if (isFollowed) Icons.Default.Check else Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = if (isFollowed) SuccessGreen else VerifiedBlue,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isFollowed) "Siguiendo" else "Seguir",
                            color = if (isFollowed) SuccessGreen else VerifiedBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChipText(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) CaribbeanNavy else SurfacePure,
        border = BorderStroke(1.dp, if (isSelected) CaribbeanNavy else Color.LightGray.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(18.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else Tertiary,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
        )
    }
}
