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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.Screen
import com.example.ui.UnikoViewModel
import com.example.ui.theme.*

// Brand colors matching the uploaded screenshot:
private val DeepNavyBlue = Color(0xFF002B7F)
private val LightFieldBg = Color(0xFFFFFFFF)
private val FieldBorderColor = Color(0xFFD6DFEC)
private val CardSelectedPinkBg = Color(0xFFFDF2F4)
private val CardSelectedBorderRed = Color(0xFFCE0026)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: UnikoViewModel,
    modifier: Modifier = Modifier
) {
    var isLoginMode by remember { mutableStateOf(false) } // Default to "Crear cuenta" as shown in the user's screenshot
    var accountType by remember { mutableStateOf("store") } // "user" or "store" (store is selected in the screenshot)

    // Form fields
    var loginEmail by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    var regFirstName by remember { mutableStateOf("") }
    var regLastName by remember { mutableStateOf("") }
    var regCedula by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regStoreName by remember { mutableStateOf("") }
    var regCategory by remember { mutableStateOf("Selecciona...") }
    var regProvince by remember { mutableStateOf("Selecciona...") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var provinceDropdownExpanded by remember { mutableStateOf(false) }

    val categoriesList = listOf(
        "Tecnología & Celulares",
        "Moda & Calzado",
        "Hogar & Electrodomésticos",
        "Belleza & Cuidado",
        "Repuestos & Autos",
        "Comida & Delicatessen",
        "Servicios Profesionales"
    )

    val provincesList = listOf(
        "Santo Domingo, D.N.",
        "Santo Domingo Este",
        "Santo Domingo Norte",
        "Santo Domingo Oeste",
        "Santiago de los Caballeros",
        "La Altagracia (Punta Cana)",
        "La Romana",
        "Puerto Plata",
        "La Vega",
        "San Cristóbal",
        "San Pedro de Macorís",
        "Duarte (San Francisco de Macorís)",
        "Espaillat (Moca)"
    )

    val scrollState = rememberScrollState()

    val passwordStrength = remember(regPassword) {
        when {
            regPassword.isEmpty() -> 0f to "Mínimo 6 caracteres"
            regPassword.length < 6 -> 0.3f to "Muy corta (mín. 6)"
            regPassword.length < 9 -> 0.65f to "Aceptable"
            else -> 1f to "Contraseña Segura"
        }
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
            // TOP HEADER BAR (with Hamburger, Official Logo, Heart, and Cart badge '0')
            Surface(
                color = SurfacePure,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = { viewModel.navigateBack() },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menú",
                                tint = DeepNavyBlue,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Official UNIKO-RD Logo in Center
                        Image(
                            painter = painterResource(id = R.drawable.uniko_logo_header),
                            contentDescription = "UNIKO-RD",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .height(38.dp)
                                .clickable { viewModel.navigateTo(Screen.Home) }
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = { viewModel.showToast("Favoritos guardados") },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Favoritos",
                                    tint = DeepNavyBlue,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            IconButton(
                                onClick = { viewModel.isCartSheetVisible.value = true },
                                modifier = Modifier.size(36.dp)
                            ) {
                                BadgedBox(
                                    badge = {
                                        Badge(
                                            containerColor = CrimsonAccent,
                                            contentColor = Color.White
                                        ) {
                                            Text(
                                                text = "0",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.ShoppingCart,
                                        contentDescription = "Carrito",
                                        tint = DeepNavyBlue,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Persistent Search Bar as seen in screenshot:
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(SurfaceCanvas)
                            .border(1.dp, FieldBorderColor, RoundedCornerShape(22.dp))
                            .clickable { viewModel.navigateTo(Screen.Products) }
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "¿Qué estás buscando?",
                            color = Color(0xFF9CA3AF),
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // MAIN CONTENT CARD (White container with rounded corners and clean typography)
            Surface(
                color = SurfacePure,
                shape = RoundedCornerShape(24.dp),
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header: Title & Subtitle matching the screenshot:
                    Column {
                        Text(
                            text = if (isLoginMode) "Inicia sesión" else "Crea tu cuenta",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = DeepNavyBlue,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isLoginMode)
                                "Entra para comprar, vender y conectar en República Dominicana."
                            else
                                "Únete a UNIKO-RD en un minuto. Sin costo.",
                            fontSize = 13.sp,
                            color = Color(0xFF6B7280),
                            lineHeight = 18.sp
                        )
                    }

                    // SEGMENTED TOGGLE PILL: "Entrar" | "Crear cuenta"
                    Surface(
                        color = Color(0xFFF3F4F6),
                        shape = RoundedCornerShape(30.dp),
                        border = BorderStroke(1.dp, FieldBorderColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(if (isLoginMode) DeepNavyBlue else Color.Transparent)
                                    .clickable { isLoginMode = true }
                                    .testTag("toggle_entrar"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Entrar",
                                    color = if (isLoginMode) Color.White else Color(0xFF4B5563),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(if (!isLoginMode) DeepNavyBlue else Color.Transparent)
                                    .clickable { isLoginMode = false }
                                    .testTag("toggle_crear_cuenta"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Crear cuenta",
                                    color = if (!isLoginMode) Color.White else Color(0xFF4B5563),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    if (!isLoginMode) {
                        // =================== CREAR CUENTA FORM ===================

                        // Section: TIPO DE CUENTA
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "TIPO DE CUENTA",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = DeepNavyBlue,
                                letterSpacing = 0.5.sp
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Card 1: Usuario
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(84.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .border(
                                            width = if (accountType == "user") 1.5.dp else 1.dp,
                                            color = if (accountType == "user") CardSelectedBorderRed else FieldBorderColor,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .background(if (accountType == "user") CardSelectedPinkBg else LightFieldBg)
                                        .clickable { accountType = "user" }
                                        .testTag("account_type_user")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "Usuario",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = if (accountType == "user") DeepNavyBlue else Color(0xFF374151)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Compra y califica\ntiendas",
                                            fontSize = 11.sp,
                                            color = Color(0xFF6B7280),
                                            lineHeight = 14.sp
                                        )
                                    }
                                }

                                // Card 2: Tienda (selected in screenshot)
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(84.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .border(
                                            width = if (accountType == "store") 1.5.dp else 1.dp,
                                            color = if (accountType == "store") CardSelectedBorderRed else FieldBorderColor,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .background(if (accountType == "store") CardSelectedPinkBg else LightFieldBg)
                                        .clickable { accountType = "store" }
                                        .testTag("account_type_store")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "Tienda",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = if (accountType == "store") DeepNavyBlue else Color(0xFF374151)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Crea tu tienda y vende",
                                            fontSize = 11.sp,
                                            color = Color(0xFF6B7280),
                                            lineHeight = 14.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Field: NOMBRES
                        AuthInputField(
                            label = "NOMBRES",
                            value = regFirstName,
                            placeholder = "Tus nombres",
                            onValueChange = { regFirstName = it },
                            testTag = "reg_firstname_input"
                        )

                        // Field: APELLIDOS
                        AuthInputField(
                            label = "APELLIDOS",
                            value = regLastName,
                            placeholder = "Tus apellidos",
                            onValueChange = { regLastName = it },
                            testTag = "reg_lastname_input"
                        )

                        // Field: CÉDULA
                        AuthInputField(
                            label = "CÉDULA",
                            value = regCedula,
                            placeholder = "000-0000000-0",
                            onValueChange = { regCedula = it },
                            testTag = "reg_cedula_input"
                        )

                        // Field: TELÉFONO
                        AuthInputField(
                            label = "TELÉFONO",
                            value = regPhone,
                            placeholder = "809-000-0000",
                            onValueChange = { regPhone = it },
                            testTag = "reg_phone_input"
                        )

                        // CONDITIONAL CONTAINER: "DATOS DE TU TIENDA" (shown when accountType == "store")
                        if (accountType == "store") {
                            Surface(
                                color = CardSelectedPinkBg,
                                shape = RoundedCornerShape(18.dp),
                                border = BorderStroke(1.dp, CardSelectedBorderRed.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Storefront,
                                            contentDescription = null,
                                            tint = DeepNavyBlue,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = "DATOS DE TU TIENDA",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = DeepNavyBlue,
                                            letterSpacing = 0.5.sp
                                        )
                                    }

                                    // Owner live feedback
                                    Text(
                                        text = "Propietario: " + if (regFirstName.isNotBlank() || regLastName.isNotBlank())
                                            "$regFirstName $regLastName (visible en el perfil público)."
                                        else
                                            "tú (tu nombre y apellido, visible en el perfil público).",
                                        fontSize = 11.sp,
                                        color = Color(0xFF4B5563),
                                        lineHeight = 15.sp
                                    )

                                    // NOMBRE DE LA TIENDA
                                    AuthInputField(
                                        label = "NOMBRE DE LA TIENDA",
                                        value = regStoreName,
                                        placeholder = "Ej: Tech Store RD",
                                        onValueChange = { regStoreName = it },
                                        testTag = "reg_store_name_input"
                                    )

                                    // CATEGORÍA (Dropdown Select)
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = "CATEGORÍA",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = DeepNavyBlue,
                                            letterSpacing = 0.5.sp
                                        )
                                        Box(modifier = Modifier.fillMaxWidth()) {
                                            Surface(
                                                color = LightFieldBg,
                                                shape = RoundedCornerShape(14.dp),
                                                border = BorderStroke(1.dp, FieldBorderColor),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(48.dp)
                                                    .clickable { categoryDropdownExpanded = true }
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(horizontal = 14.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = regCategory,
                                                        color = if (regCategory == "Selecciona...") Color(0xFF9CA3AF) else DeepNavyBlue,
                                                        fontSize = 14.sp
                                                    )
                                                    Icon(
                                                        imageVector = Icons.Default.KeyboardArrowDown,
                                                        contentDescription = null,
                                                        tint = Color(0xFF6B7280)
                                                    )
                                                }
                                            }

                                            DropdownMenu(
                                                expanded = categoryDropdownExpanded,
                                                onDismissRequest = { categoryDropdownExpanded = false },
                                                modifier = Modifier.background(SurfacePure)
                                            ) {
                                                categoriesList.forEach { cat ->
                                                    DropdownMenuItem(
                                                        text = { Text(cat, fontSize = 13.sp) },
                                                        onClick = {
                                                            regCategory = cat
                                                            categoryDropdownExpanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // UBICACIÓN (Dropdown Select)
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = "UBICACIÓN",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = DeepNavyBlue,
                                            letterSpacing = 0.5.sp
                                        )
                                        Box(modifier = Modifier.fillMaxWidth()) {
                                            Surface(
                                                color = LightFieldBg,
                                                shape = RoundedCornerShape(14.dp),
                                                border = BorderStroke(1.dp, FieldBorderColor),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(48.dp)
                                                    .clickable { provinceDropdownExpanded = true }
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(horizontal = 14.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = regProvince,
                                                        color = if (regProvince == "Selecciona...") Color(0xFF9CA3AF) else DeepNavyBlue,
                                                        fontSize = 14.sp
                                                    )
                                                    Icon(
                                                        imageVector = Icons.Default.KeyboardArrowDown,
                                                        contentDescription = null,
                                                        tint = Color(0xFF6B7280)
                                                    )
                                                }
                                            }

                                            DropdownMenu(
                                                expanded = provinceDropdownExpanded,
                                                onDismissRequest = { provinceDropdownExpanded = false },
                                                modifier = Modifier.background(SurfacePure)
                                            ) {
                                                provincesList.forEach { prov ->
                                                    DropdownMenuItem(
                                                        text = { Text(prov, fontSize = 13.sp) },
                                                        onClick = {
                                                            regProvince = prov
                                                            provinceDropdownExpanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // CORREO ELECTRÓNICO
                        AuthInputField(
                            label = "CORREO ELECTRÓNICO",
                            value = regEmail,
                            placeholder = "tu@correo.com",
                            onValueChange = { regEmail = it },
                            testTag = "reg_email_input"
                        )

                        // CONTRASEÑA
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "CONTRASEÑA",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = DeepNavyBlue,
                                letterSpacing = 0.5.sp
                            )
                            OutlinedTextField(
                                value = regPassword,
                                onValueChange = { regPassword = it },
                                placeholder = { Text("Mínimo 6 caracteres", fontSize = 13.sp, color = Color(0xFF9CA3AF)) },
                                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                        Icon(
                                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Mostrar contraseña",
                                            tint = Color(0xFF6B7280)
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_password_input"),
                                shape = RoundedCornerShape(14.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = LightFieldBg,
                                    unfocusedContainerColor = LightFieldBg,
                                    focusedBorderColor = DeepNavyBlue,
                                    unfocusedBorderColor = FieldBorderColor
                                )
                            )

                            // Password Strength Bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                LinearProgressIndicator(
                                    progress = { passwordStrength.first },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    color = if (passwordStrength.first > 0.8f) SuccessGreen else if (passwordStrength.first > 0.4f) WarningAmber else CrimsonAccent,
                                    trackColor = Color(0xFFE5E7EB)
                                )
                                Text(passwordStrength.second, fontSize = 10.sp, color = Color(0xFF6B7280))
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // SUBMIT BUTTON (Vibrant Red)
                        Button(
                            onClick = {
                                viewModel.showToast("¡Cuenta ${if (accountType == "store") "de Tienda" else "de Usuario"} creada en UNIKO-RD!")
                                viewModel.navigateTo(Screen.Home)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonAccent),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_crear_cuenta_submit")
                        ) {
                            Icon(
                                imageVector = if (accountType == "store") Icons.Default.Storefront else Icons.Default.PersonAdd,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (accountType == "store") "Crear cuenta y tienda" else "Crear cuenta de Usuario",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                    } else {
                        // =================== ENTRAR FORM ===================
                        AuthInputField(
                            label = "CORREO ELECTRÓNICO",
                            value = loginEmail,
                            placeholder = "tu@correo.com",
                            onValueChange = { loginEmail = it },
                            testTag = "login_email_input"
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "CONTRASEÑA",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = DeepNavyBlue,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "¿Olvidaste tu contraseña?",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepNavyBlue,
                                    modifier = Modifier.clickable { viewModel.showToast("Enlace de recuperación enviado a tu correo.") }
                                )
                            }
                            OutlinedTextField(
                                value = loginPassword,
                                onValueChange = { loginPassword = it },
                                placeholder = { Text("••••••••••••", fontSize = 13.sp, color = Color(0xFF9CA3AF)) },
                                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                        Icon(
                                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Mostrar contraseña",
                                            tint = Color(0xFF6B7280)
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_password_input"),
                                shape = RoundedCornerShape(14.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = LightFieldBg,
                                    unfocusedContainerColor = LightFieldBg,
                                    focusedBorderColor = DeepNavyBlue,
                                    unfocusedBorderColor = FieldBorderColor
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = {
                                viewModel.showToast("¡Bienvenido de vuelta a UNIKO-RD!")
                                viewModel.navigateTo(Screen.Home)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonAccent),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_entrar_submit")
                        ) {
                            Icon(imageVector = Icons.Default.Login, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Entrar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        // SuperAdmin quick entry
                        Surface(
                            color = DeepNavyBlue,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.navigateTo(Screen.Admin) }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(20.dp))
                                    Column {
                                        Text("Panel SuperAdmin UNIKO", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("Moderación de tiendas y bloques", color = SecondaryFixed, fontSize = 10.sp)
                                    }
                                }
                                Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // FOOTER BRAND NOTE
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = DeepNavyBlue, modifier = Modifier.size(14.dp))
                    Text("Conexión Encriptada SSL · Servidores en la Nube", fontSize = 11.sp, color = Color(0xFF6B7280))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Al continuar aceptas los términos y condiciones de UNIKO-RD y las leyes de comercio digital de Rep. Dominicana.",
                    fontSize = 11.sp,
                    color = Color(0xFF9CA3AF),
                    textAlign = TextAlign.Center
                )
            }
        }

        // FLOATING CIRCULAR ASSISTANT BUTTON ON BOTTOM-RIGHT (Matching the red circular logo badge in the screenshot)
        Surface(
            shape = CircleShape,
            color = CrimsonAccent,
            shadowElevation = 8.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 80.dp)
                .size(56.dp)
                .clickable { viewModel.isChatbotVisible.value = true }
                .testTag("floating_uniko_logo_button")
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                // White inner disc with the official UNIKO-RD logo
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize().padding(4.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_uniko_logo_square),
                            contentDescription = "Asistente UNIKO-RD",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AuthInputField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    testTag: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = DeepNavyBlue,
            letterSpacing = 0.5.sp
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 13.sp, color = Color(0xFF9CA3AF)) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = LightFieldBg,
                unfocusedContainerColor = LightFieldBg,
                focusedBorderColor = DeepNavyBlue,
                unfocusedBorderColor = FieldBorderColor,
                cursorColor = DeepNavyBlue
            )
        )
    }
}
