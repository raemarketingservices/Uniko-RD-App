package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.Screen
import com.example.ui.UnikoViewModel
import com.example.ui.theme.*

@Composable
fun ServiceDetailScreen(
    serviceId: String,
    viewModel: UnikoViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val services by viewModel.services.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val isFav = remember(favorites, serviceId) { favorites.any { it.itemId == serviceId } }

    val service = services.firstOrNull { it.id == serviceId } ?: services.firstOrNull()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Descripción, 1: Reseñas
    val scrollState = rememberScrollState()

    if (service == null) {
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
            // TOP BAR (Task-Focused Detail View)
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
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Atrás", tint = CaribbeanNavy)
                        }
                        Column {
                            Text("SERVICIO CERTIFICADO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = VerifiedBlue)
                            Text(
                                text = "Reparación iPhone & Mac",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = CaribbeanNavy),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "Conoce este servicio verificado en UNIKO-RD: ${service.title}")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, null))
                        }) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Compartir", tint = CaribbeanNavy)
                        }

                        IconButton(onClick = { viewModel.toggleFavorite(service.id, "service") }) {
                            Icon(
                                imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorito",
                                tint = if (isFav) CrimsonAccent else CaribbeanNavy
                            )
                        }
                    }
                }
            }

            // 1. HERO IMAGE WITH STATUS BADGES
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.4f)
                    .background(SurfacePure)
            ) {
                AsyncImage(
                    model = service.imageUrl,
                    contentDescription = service.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Badges top left
                Column(
                    modifier = Modifier
                        .padding(14.dp)
                        .align(Alignment.TopStart),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(VerifiedBlue)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Text("Proveedor Verificado RNC", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfacePure.copy(alpha = 0.95f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(imageVector = Icons.Default.LocalShipping, contentDescription = null, tint = CrimsonAccent, modifier = Modifier.size(14.dp))
                            Text("Servicio a Domicilio", color = CaribbeanNavy, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Photo indicator bottom right
                Box(
                    modifier = Modifier
                        .padding(14.dp)
                        .align(Alignment.BottomEnd)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CaribbeanNavy.copy(alpha = 0.85f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                        Text("1 / 5 Fotos", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 2. SERVICE TITLE & PRIMARY PRICING
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
                        Text(
                            text = "ELECTRÓNICA & REPARACIONES",
                            color = VerifiedBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(SuccessGreen.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(imageVector = Icons.Default.Timer, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(13.dp))
                                Text(service.turnaroundTime, color = SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Reparación de Pantallas y Baterías iPhone & Mac en 45 Minutos",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = CaribbeanNavy,
                            lineHeight = 26.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Pricing Cluster Card
                    Surface(
                        color = SurfaceContainerLow,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Inversión del servicio", fontSize = 11.sp, color = Tertiary)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "RD$ %,.0f".format(service.priceStarting),
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = CrimsonAccent
                                    )
                                )
                                Text("Desde", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = CaribbeanNavy)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfacePure)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(14.dp))
                                Text("Precio final según diagnóstico o modelo", fontSize = 11.sp, color = Tertiary)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3. PROVIDER PROFILE CARD
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
                                    .background(VerifiedBlue.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Storefront, contentDescription = null, tint = VerifiedBlue, modifier = Modifier.size(24.dp))
                            }

                            Column {
                                Text(
                                    text = service.providerName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = CaribbeanNavy
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(VerifiedBlue.copy(alpha = 0.1f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("Verificado con RNC / Cédula", fontSize = 10.sp, color = VerifiedBlue, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        TextButton(onClick = { viewModel.navigateTo(Screen.StoreProfile("store_tech_caribe")) }) {
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
                            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(15.dp))
                            Text("4.9", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CaribbeanNavy)
                            Text("(68 reseñas)", fontSize = 11.sp, color = Tertiary)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = CrimsonAccent, modifier = Modifier.size(15.dp))
                            Text("Bella Vista, Santo Domingo D.N.", fontSize = 11.sp, color = CaribbeanNavy)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        color = SurfaceBright,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Moped, contentDescription = null, tint = Secondary, modifier = Modifier.size(18.dp))
                            Text(
                                text = "Cobertura garantizada: En todo el Distrito Nacional y Santo Domingo Este. Atención disponible a domicilio o en taller climatizado.",
                                fontSize = 11.sp,
                                color = Tertiary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 4. TABS: Descripción vs Reseñas
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
                            text = { Text("Reseñas (68)", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium, fontSize = 13.sp) }
                        )
                    }

                    Box(modifier = Modifier.padding(16.dp)) {
                        if (selectedTab == 0) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "Técnicos certificados con repuestos grado A y garantía escrita de 90 días en República Dominicana. Diagnóstico gratis si reparas con nosotros. Utilizamos sellado térmico original para mantener la resistencia a salpicaduras en modelos compatibles.",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = CaribbeanNavy, lineHeight = 20.sp)
                                )

                                Text("¿Qué incluye este servicio?", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CaribbeanNavy)

                                ServiceInclusionItem(Icons.Default.Speed, "Diagnóstico express en 15 mins", "Inspección de amperaje, salud de ciclos y calibración óptica antes de iniciar la reparación.")
                                ServiceInclusionItem(Icons.Default.Verified, "Garantía local de 90 días", "Póliza física y digital con reemplazo inmediato ante cualquier falla del repuesto instalado.")
                                ServiceInclusionItem(Icons.Default.ReceiptLong, "Factura con NCF si la requieres", "Comprobante fiscal válido para crédito fiscal empresarial o consumidor final de la DGII.")
                                ServiceInclusionItem(Icons.Default.HomeWork, "Atención en taller climatizado o a domicilio", "Unidad móvil equipada lista para atenderte en Santo Domingo o visita a sucursal Bella Vista.")

                                Divider(color = Color.LightGray.copy(alpha = 0.4f))

                                Text("Métodos de pago aceptados", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CaribbeanNavy)
                                Text("Efectivo RD$, Transferencia Banreservas/BHD/Popular, Tarjetas de Crédito.", fontSize = 12.sp, color = Tertiary)
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                ReviewBlock("Carlos Rosario", "Piantini, D.N.", "Vinieron a mi oficina en Piantini y en 35 minutos cambiaron la pantalla de mi iPhone 14 Pro Max. La calibración del True Tone quedó impecable.")
                                ReviewBlock("María Laura Fernández", "Bella Vista, D.N.", "Fui directo al taller en Bella Vista por la batería de mi MacBook Air M1. Te dan tu factura con NCF sin rodeos y el trato fue de primera.")
                                ReviewBlock("José Manuel Peña", "Santo Domingo Este", "Garantía 100% real. A los 20 días tuve una duda con el táctil y me atendieron el mismo día sin cobrarme un solo peso.")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // TRUST BANNER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(CaribbeanNavy)
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(VerifiedBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Gavel, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                    Column {
                        Text("Protección UNIKO-RD Garantizada", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(
                            text = "Servicios auditados. Proveedor con cédula y RNC validado ante las leyes de la República Dominicana.",
                            color = SurfaceBright.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedIconButton(
                    onClick = {
                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:8095550199"))
                        context.startActivity(dialIntent)
                    },
                    modifier = Modifier.size(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, VerifiedBlue)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Call, contentDescription = "Llamar", tint = VerifiedBlue, modifier = Modifier.size(18.dp))
                        Text("Llamar", fontSize = 10.sp, color = VerifiedBlue, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = {
                        val url = "https://wa.me/18095550199?text=Hola%20${service.providerName}%2C%20estoy%20interesado%20en%20el%20servicio%20${service.title}%20visto%20en%20UNIKO-RD."
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
                        .testTag("detail_contact_service_provider_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonAccent),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text("Contactar al Proveedor", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("• Responde en menos de 15 mins", color = Color.White.copy(alpha = 0.9f), fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ServiceInclusionItem(icon: ImageVector, title: String, desc: String) {
    Surface(
        color = SurfaceCanvas,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(VerifiedBlue.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = VerifiedBlue, modifier = Modifier.size(16.dp))
            }
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CaribbeanNavy)
                Text(desc, fontSize = 11.sp, color = Tertiary, lineHeight = 15.sp)
            }
        }
    }
}

@Composable
private fun ReviewBlock(name: String, city: String, comment: String) {
    Surface(
        color = SurfaceCanvas,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CaribbeanNavy)
                Text(city, fontSize = 10.sp, color = Tertiary)
            }
            Row(modifier = Modifier.padding(vertical = 2.dp)) {
                repeat(5) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(14.dp))
                }
            }
            Text(comment, fontSize = 11.sp, color = CaribbeanNavy, lineHeight = 16.sp)
        }
    }
}
