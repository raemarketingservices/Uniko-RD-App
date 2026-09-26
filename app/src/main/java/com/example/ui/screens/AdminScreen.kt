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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PageBlockEntity
import com.example.data.StoreEntity
import com.example.ui.Screen
import com.example.ui.UnikoViewModel
import com.example.ui.theme.*

@Composable
fun AdminScreen(
    viewModel: UnikoViewModel,
    modifier: Modifier = Modifier
) {
    val stores by viewModel.stores.collectAsState()
    val pageBlocks by viewModel.pageBlocks.collectAsState()

    var selectedTab by remember { mutableIntStateOf(1) } // 1 is Tiendas
    var adminSearchQuery by remember { mutableStateOf("") }
    var selectedProvince by remember { mutableStateOf("Todas las Provincias") }

    // Chatbot config
    var botEnabled by remember { mutableStateOf(true) }
    var botGreeting by remember { mutableStateOf("¡Hola! Soy UNIKO, tu asistente de compras 🇩🇴. ¿Buscas un producto específico o necesitas ayuda para contactar a una tienda?") }
    var botCatalogScope by remember { mutableStateOf("all") } // "all", "verified", "disabled"

    // Hero Block config
    var heroHeading by remember { mutableStateOf("El marketplace de República Dominicana") }
    var heroActive by remember { mutableStateOf(true) }

    var showDeleteConfirmDialog by remember { mutableStateOf<StoreEntity?>(null) }
    var showCascadeDeleteAllDialog by remember { mutableStateOf(false) }

    val filteredStores = remember(stores, adminSearchQuery, selectedProvince) {
        stores.filter { s ->
            val matchQuery = adminSearchQuery.isBlank() || s.name.contains(adminSearchQuery, ignoreCase = true) || s.rnc.contains(adminSearchQuery, ignoreCase = true)
            val matchProvince = selectedProvince == "Todas las Provincias" || s.province.contains(selectedProvince.take(6), ignoreCase = true)
            matchQuery && matchProvince
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
                .padding(bottom = 70.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // 1. TOP APP BAR SUPERADMIN
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
                                .height(50.dp),
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
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(CrimsonAccent),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Storefront, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(CaribbeanNavy)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(SuccessGreen))
                                        Text("SuperAdmin", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.showToast("Sesión de SuperAdmin cerrada")
                                        viewModel.navigateTo(Screen.Home)
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(containerColor = ErrorContainer.copy(alpha = 0.5f)),
                                    border = BorderStroke(1.dp, Error.copy(alpha = 0.3f)),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null, tint = Error, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Salir", color = Error, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Scrollable Tab Row
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(listOf("Home (Diseño)", "Tiendas (48)", "Productos (1.2k)", "Usuarios", "Chatbot UNIKO", "Seguridad").mapIndexed { idx, title -> idx to title }) { (idx, title) ->
                                Column(
                                    modifier = Modifier
                                        .clickable { selectedTab = idx }
                                        .padding(bottom = 6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = title,
                                        fontSize = 12.sp,
                                        fontWeight = if (selectedTab == idx) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selectedTab == idx) CrimsonAccent else Tertiary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .height(3.dp)
                                            .width(28.dp)
                                            .background(if (selectedTab == idx) CrimsonAccent else Color.Transparent)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. BENTO STATS ROW (4 Cards)
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AdminStatCard(
                            title = "TIENDAS ACTIVAS",
                            value = "48",
                            sub = "+6 este mes",
                            icon = Icons.Default.Store,
                            iconBg = VerifiedBlue.copy(alpha = 0.1f),
                            iconColor = VerifiedBlue,
                            modifier = Modifier.weight(1f)
                        )
                        AdminStatCard(
                            title = "CATÁLOGO PRODUCTOS",
                            value = "1,250",
                            sub = "98% en stock",
                            icon = Icons.Default.ShoppingBag,
                            iconBg = CrimsonAccent.copy(alpha = 0.1f),
                            iconColor = CrimsonAccent,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AdminStatCard(
                            title = "USUARIOS REGISTRADOS",
                            value = "3,420",
                            sub = "Activos hoy: 412",
                            icon = Icons.Default.Group,
                            iconBg = SecondaryFixed.copy(alpha = 0.4f),
                            iconColor = CaribbeanNavy,
                            modifier = Modifier.weight(1f)
                        )
                        AdminStatCard(
                            title = "ASISTENTE IA UNIKO",
                            value = "Activo",
                            sub = "94% respuestas",
                            icon = Icons.Default.SmartToy,
                            iconBg = SuccessGreen.copy(alpha = 0.15f),
                            iconColor = SuccessGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 3. GESTIÓN DE TIENDAS Y MODERACIÓN
            item {
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(imageVector = Icons.Default.Store, contentDescription = null, tint = CrimsonAccent, modifier = Modifier.size(20.dp))
                                    Text("Gestión de Tiendas y Moderación", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CaribbeanNavy)
                                }
                                Text("Control de comerciantes dominicanos, verificación de RNC y borrado seguro.", fontSize = 11.sp, color = Tertiary)
                            }
                        }

                        // Action button row: Eliminar todas (48) with warning
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { showCascadeDeleteAllDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Error.copy(alpha = 0.4f)),
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = ErrorContainer.copy(alpha = 0.4f)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Error, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Eliminar todas (${stores.size})", color = Error, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { viewModel.navigateTo(Screen.PublishProduct) },
                                colors = ButtonDefaults.buttonColors(containerColor = CaribbeanNavy),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(imageVector = Icons.Default.AddBusiness, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Nueva Tienda", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Search & Filter in Admin
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceCanvas)
                                .border(1.dp, Color.LightGray.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Tertiary, modifier = Modifier.size(18.dp))
                            OutlinedTextField(
                                value = adminSearchQuery,
                                onValueChange = { adminSearchQuery = it },
                                placeholder = { Text("Buscar por nombre comercial, RNC o provincia...", fontSize = 11.sp, color = Tertiary) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("admin_store_search"),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    cursorColor = CrimsonAccent
                                )
                            )
                        }

                        // Store Cards in Admin
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            filteredStores.forEach { store ->
                                AdminStoreCard(
                                    store = store,
                                    onToggleVerified = { viewModel.toggleStoreVerified(store) },
                                    onToggleVip = { viewModel.toggleStoreVip(store) },
                                    onDeleteClick = { showDeleteConfirmDialog = store }
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(14.dp))
            }

            // 4. EDITOR DE BLOQUES HOME ('page_blocks')
            item {
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = VerifiedBlue, modifier = Modifier.size(20.dp))
                                Text("Editor de Bloques Home", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CaribbeanNavy)
                            }
                            Text("Tabla: page_blocks", fontSize = 10.sp, color = Tertiary)
                        }

                        Text("Organiza y activa el orden dinámico de componentes en la pantalla inicial.", fontSize = 11.sp, color = Tertiary)

                        // Bloque 1: Hero Banner Principal (Expandido / Editable)
                        Surface(
                            color = SurfaceBright,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.5.dp, VerifiedBlue.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(imageVector = Icons.Default.ViewCarousel, contentDescription = null, tint = VerifiedBlue, modifier = Modifier.size(18.dp))
                                        Text("1. Bloque: Hero Banner Principal", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CaribbeanNavy)
                                    }
                                    Switch(
                                        checked = heroActive,
                                        onCheckedChange = { heroActive = it },
                                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = SuccessGreen)
                                    )
                                }

                                Text("Título Principal (Hero Heading)", fontSize = 11.sp, color = Tertiary)
                                OutlinedTextField(
                                    value = heroHeading,
                                    onValueChange = { heroHeading = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true
                                )

                                Button(
                                    onClick = { viewModel.showToast("Cambios de bloques guardados en Supabase") },
                                    colors = ButtonDefaults.buttonColors(containerColor = VerifiedBlue),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.align(Alignment.End).height(36.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Guardar bloques", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Other blocks collapsed
                        AdminCollapsedBlock("2. Barra de Confianza (Garantía Local & Envíos RD)", Icons.Default.Verified)
                        AdminCollapsedBlock("3. Grid de Categorías Principales", Icons.Default.Category)
                        AdminCollapsedBlock("4. Productos Más Vendidos de la Semana", Icons.Default.LocalFireDepartment)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(14.dp))
            }

            // 5. CONFIGURACIÓN CHATBOT UNIKO ASISTENTE
            item {
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(imageVector = Icons.Default.SmartToy, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
                                Text("Chatbot UNIKO Asistente", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CaribbeanNavy)
                            }
                            Icon(imageVector = Icons.Default.HelpOutline, contentDescription = null, tint = Tertiary, modifier = Modifier.size(18.dp))
                        }

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
                                Column {
                                    Text("Estado del Asistente", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CaribbeanNavy)
                                    Text("Activa respuestas automáticas en la app", fontSize = 11.sp, color = Tertiary)
                                }
                                Switch(
                                    checked = botEnabled,
                                    onCheckedChange = { botEnabled = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = SuccessGreen)
                                )
                            }
                        }

                        Text("Saludo Inicial del Bot (Editable)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Tertiary)
                        OutlinedTextField(
                            value = botGreeting,
                            onValueChange = { botGreeting = it },
                            modifier = Modifier.fillMaxWidth().height(80.dp),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Text("Tiendas y Catálogos Vinculados al Bot", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Tertiary)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = botCatalogScope == "all", onClick = { botCatalogScope = "all" })
                                Text("Todas las tiendas (Catálogo Global *)", fontSize = 12.sp, color = CaribbeanNavy)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = botCatalogScope == "verified", onClick = { botCatalogScope = "verified" })
                                Text("Sólo tiendas verificadas con RNC", fontSize = 12.sp, color = CaribbeanNavy)
                            }
                        }

                        Button(
                            onClick = { viewModel.showToast("Configuración de Asistente sincronizada") },
                            colors = ButtonDefaults.buttonColors(containerColor = CaribbeanNavy),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(44.dp)
                        ) {
                            Icon(imageVector = Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Guardar configuración de Asistente", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // FLOATING ACTION BAR: CREDENCIALES ADMIN & GUARDAR CAMBIOS
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            color = Color.Transparent
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { viewModel.showToast("Configuraciones guardadas y sincronizadas") },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonAccent),
                    shape = RoundedCornerShape(24.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Guardar Cambios", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }

    // Delete Single Store Confirmation Dialog
    if (showDeleteConfirmDialog != null) {
        val targetStore = showDeleteConfirmDialog!!
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = null },
            title = { Text("Eliminar Tienda", fontWeight = FontWeight.Bold) },
            text = {
                Text("¿Estás seguro de eliminar '${targetStore.name}'?\n\nEsta acción borrará en cascada los productos, pedidos y fotos asociadas en la base de datos.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteStore(targetStore.id, targetStore.name)
                        showDeleteConfirmDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Error)
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirmDialog = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Cascade Delete All Dialog
    if (showCascadeDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showCascadeDeleteAllDialog = false },
            title = { Text("ADVERTENCIA CRÍTICA", color = Error, fontWeight = FontWeight.Bold) },
            text = {
                Text("Esta acción eliminará todas las ${stores.size} tiendas registradas con borrado en cascada en la base de datos local y Supabase.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAllStoresCascade()
                        showCascadeDeleteAllDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Error)
                ) {
                    Text("ELIMINAR TODAS", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showCascadeDeleteAllDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun AdminStatCard(
    title: String,
    value: String,
    sub: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = SurfacePure,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Tertiary)
                Text(value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = CaribbeanNavy)
                Text(sub, fontSize = 10.sp, color = SuccessGreen, fontWeight = FontWeight.SemiBold)
            }
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun AdminStoreCard(
    store: StoreEntity,
    onToggleVerified: () -> Unit,
    onToggleVip: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Surface(
        color = SurfacePure,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceContainerLow),
                    contentAlignment = Alignment.Center
                ) {
                    Text(store.avatarInitials, fontWeight = FontWeight.Bold, color = CaribbeanNavy, fontSize = 16.sp)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(store.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CaribbeanNavy)
                    Text("owner_name: ${store.ownerName}", fontSize = 11.sp, color = Tertiary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = Color.LightGray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("RNC Dominicano", fontSize = 10.sp, color = Tertiary)
                    Text(store.rnc, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = CaribbeanNavy)
                }
                Column {
                    Text("Provincia", fontSize = 10.sp, color = Tertiary)
                    Text(store.province, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = CaribbeanNavy)
                }
                Column {
                    Text("Productos", fontSize = 10.sp, color = Tertiary)
                    Text("${store.productsCount} artículos", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = CaribbeanNavy)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable(onClick = onToggleVerified)
                    ) {
                        Checkbox(checked = store.isVerified, onCheckedChange = { onToggleVerified() })
                        Text("Verificada", fontSize = 11.sp, color = if (store.isVerified) VerifiedBlue else Tertiary, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable(onClick = onToggleVip)
                    ) {
                        Checkbox(
                            checked = store.isVip,
                            onCheckedChange = { onToggleVip() },
                            colors = CheckboxDefaults.colors(checkedColor = WarningAmber)
                        )
                        Text("VIP", fontSize = 11.sp, color = if (store.isVip) WarningAmber else Tertiary, fontWeight = FontWeight.Bold)
                    }
                }

                IconButton(onClick = onDeleteClick, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar", tint = Error, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun AdminCollapsedBlock(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        color = SurfaceContainerLow,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = icon, contentDescription = null, tint = Tertiary, modifier = Modifier.size(18.dp))
                Text(title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = CaribbeanNavy)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(SuccessGreen.copy(alpha = 0.12f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text("Activo", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
            }
        }
    }
}
