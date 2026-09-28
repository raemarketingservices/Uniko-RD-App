package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.ServiceEntity
import com.example.ui.Screen
import com.example.ui.UnikoViewModel
import com.example.ui.theme.*

@Composable
fun ServicesScreen(
    viewModel: UnikoViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val services by viewModel.services.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Todos") }
    var onlyRnc by remember { mutableStateOf(false) }

    val filteredServices = remember(services, searchQuery, selectedCategory, onlyRnc) {
        services.filter { s ->
            val matchQuery = searchQuery.isBlank() || s.title.contains(searchQuery, ignoreCase = true) || s.providerName.contains(searchQuery, ignoreCase = true)
            val matchCat = selectedCategory == "Todos" || s.category.contains(selectedCategory, ignoreCase = true)
            val matchRnc = !onlyRnc || s.isRncVerified
            matchQuery && matchCat && matchRnc
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceCanvas)
    ) {
        // TOP APP BAR (from HTML 8)
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
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Atrás", tint = CaribbeanNavy)
                    }

                    Image(
                        painter = painterResource(id = R.drawable.ic_uniko_logo_square),
                        contentDescription = "UNIKO-RD",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.width(40.dp)
                    )

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("UNIKO-RD", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CrimsonAccent)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(CrimsonAccent)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text("DOM", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            }
                        }
                        Text("Marketplace Nacional", fontSize = 10.sp, color = Tertiary)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = { viewModel.showToast("Búsqueda avanzada de servicios activa") }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar", tint = Tertiary)
                    }
                    IconButton(onClick = { viewModel.showToast("No hay alertas de servicios pendientes") }) {
                        Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notificaciones", tint = Tertiary)
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 70.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // TITLE & SEARCH
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Servicios Profesionales",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = CaribbeanNavy
                            )
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(VerifiedBlue.copy(alpha = 0.1f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = VerifiedBlue, modifier = Modifier.size(14.dp))
                                Text("Garantía RD", fontSize = 11.sp, color = VerifiedBlue, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Android M3 Search Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clip(RoundedCornerShape(23.dp))
                            .background(SurfacePure)
                            .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(23.dp))
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Tertiary, modifier = Modifier.size(20.dp))
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Buscar servicios, técnicos, profesionales...", fontSize = 12.sp, color = Tertiary) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("services_search_input"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                cursorColor = CrimsonAccent
                            )
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(SurfaceContainerLow)
                                .clickable { viewModel.isFilterSheetVisible.value = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = CrimsonAccent, modifier = Modifier.size(14.dp))
                            Text("Toda RD", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CaribbeanNavy)
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = Tertiary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // HORIZONTAL CHIPS
            item {
                Column(modifier = Modifier.padding(bottom = 10.dp)) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(listOf("Todos", "Reparación & Celulares", "Instalación & Hogar", "Mecánica & Autos", "Belleza & Estética")) { cat ->
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(18.dp))
                                    .clickable { selectedCategory = cat },
                                color = if (selectedCategory == cat) CaribbeanNavy else SurfacePure,
                                border = BorderStroke(1.dp, if (selectedCategory == cat) CaribbeanNavy else Color.LightGray.copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(18.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = when (cat) {
                                            "Reparación & Celulares" -> Icons.Default.Smartphone
                                            "Instalación & Hogar" -> Icons.Default.AcUnit
                                            "Mecánica & Autos" -> Icons.Default.DirectionsCar
                                            "Belleza & Estética" -> Icons.Default.Spa
                                            else -> Icons.Default.Build
                                        },
                                        contentDescription = null,
                                        tint = if (selectedCategory == cat) Color.White else Tertiary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = if (cat == "Todos") "Todos los servicios" else cat,
                                        fontSize = 12.sp,
                                        fontWeight = if (selectedCategory == cat) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectedCategory == cat) Color.White else CaribbeanNavy
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Secondary Toggles Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { onlyRnc = !onlyRnc },
                                color = if (onlyRnc) VerifiedBlue.copy(alpha = 0.15f) else SurfacePure,
                                border = BorderStroke(1.dp, if (onlyRnc) VerifiedBlue else Color.LightGray.copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = VerifiedBlue, modifier = Modifier.size(15.dp))
                                    Text("Solo con RNC", fontSize = 11.sp, color = VerifiedBlue, fontWeight = FontWeight.Bold)
                                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(SuccessGreen))
                                }
                            }

                            Surface(
                                color = SurfacePure,
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.SwapVert, contentDescription = null, tint = Tertiary, modifier = Modifier.size(15.dp))
                                    Text("Relevancia", fontSize = 11.sp, color = CaribbeanNavy)
                                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = Tertiary, modifier = Modifier.size(14.dp))
                                }
                            }
                        }

                        Text(
                            text = "${filteredServices.size} servicios en RD",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Tertiary
                        )
                    }
                }
            }

            // SERVICE CARDS FEED
            items(filteredServices) { service ->
                ServiceCardItem(
                    service = service,
                    onClick = { viewModel.navigateTo(Screen.ServiceDetail(service.id)) },
                    onCallClick = {
                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:8095550199"))
                        context.startActivity(dialIntent)
                    },
                    onContactClick = {
                        val url = "https://wa.me/18095550199?text=Hola%20${service.providerName}%2C%20vi%20su%20servicio%20${service.title}%20en%20UNIKO-RD."
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            viewModel.showToast("WhatsApp: +1 (809) 555-0199")
                        }
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            // SELLER / ONBOARDING PROMO BANNER
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
                        .padding(18.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Comunidad de Profesionales RD", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "¿Ofreces un servicio profesional en República Dominicana?",
                            style = MaterialTheme.typography.titleLarge.copy(color = Color.White, fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Crea tu perfil verificado, recibe solicitudes de clientes directos y aumenta tus ingresos sin comisiones abusivas.",
                            style = MaterialTheme.typography.bodySmall.copy(color = SecondaryFixed)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.navigateTo(Screen.PublishProduct) },
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonAccent),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(42.dp)
                        ) {
                            Text("Publicar mi servicio", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceCardItem(
    service: ServiceEntity,
    onClick: () -> Unit,
    onCallClick: () -> Unit,
    onContactClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = SurfacePure,
        shadowElevation = 1.dp
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(SurfaceCanvas)
            ) {
                AsyncImage(
                    model = service.imageUrl,
                    contentDescription = service.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Category pill pinned top left
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.TopStart)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CaribbeanNavy.copy(alpha = 0.9f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = service.category.uppercase(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // RNC Badge bottom left
                if (service.isRncVerified) {
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .align(Alignment.BottomStart)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfacePure.copy(alpha = 0.95f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = VerifiedBlue, modifier = Modifier.size(14.dp))
                            Text("RNC Registrado", color = VerifiedBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = service.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = CaribbeanNavy,
                        lineHeight = 20.sp
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Storefront, contentDescription = null, tint = VerifiedBlue, modifier = Modifier.size(16.dp))
                    Text(
                        text = service.providerName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = VerifiedBlue
                    )
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = VerifiedBlue, modifier = Modifier.size(14.dp))
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(14.dp))
                        Text("%.1f".format(service.rating), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CaribbeanNavy)
                        Text("(${service.reviewCount} reseñas)", fontSize = 11.sp, color = Tertiary)
                    }
                    Text("•", color = Tertiary, fontSize = 11.sp)
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = CrimsonAccent, modifier = Modifier.size(12.dp))
                    Text(service.coverage, fontSize = 11.sp, color = Tertiary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }

                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = Color.LightGray.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Mano de obra desde", fontSize = 10.sp, color = Tertiary)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "RD$ %,.0f".format(service.priceStarting),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = CaribbeanNavy
                            )
                            if (service.priceOld != null) {
                                Text(
                                    text = "RD$ %,.0f".format(service.priceOld),
                                    fontSize = 11.sp,
                                    color = Tertiary,
                                    textDecoration = TextDecoration.LineThrough
                                )
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = onCallClick,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, VerifiedBlue),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = "Llamar", tint = VerifiedBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Llamar", color = VerifiedBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = onContactClick,
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonAccent),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Chat, contentDescription = "Contactar", tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Contactar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
