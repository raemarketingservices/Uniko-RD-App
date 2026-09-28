package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.Screen
import com.example.ui.UnikoViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: UnikoViewModel,
    modifier: Modifier = Modifier
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val products by viewModel.products.collectAsState()
    val usuario by viewModel.usuario.collectAsState()
    val enviando by viewModel.enviandoCompra.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var cedula by remember { mutableStateOf("") }
    var nota by remember { mutableStateOf("") }
    var precargado by remember { mutableStateOf(false) }

    LaunchedEffect(usuario) {
        val u = usuario ?: return@LaunchedEffect
        if (!precargado) {
            nombre = u.nombre
            correo = u.email
            precargado = true
        }
    }

    val productMap = remember(products) { products.associateBy { it.id } }
    val lineas = remember(cartItems, productMap) {
        cartItems.mapNotNull { ci -> productMap[ci.productId]?.let { Triple(it, ci.quantity, it.price * ci.quantity) } }
    }
    val total = remember(lineas) { lineas.sumOf { it.third } }
    val unidades = remember(lineas) { lineas.sumOf { it.second } }

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceCanvas)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 100.dp)
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
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver", tint = CaribbeanNavy)
                    }
                    Column {
                        Text(
                            text = "Datos de compra",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = CaribbeanNavy
                            )
                        )
                        Text(
                            text = "Coordinamos contigo la entrega y el pago",
                            fontSize = 11.sp,
                            color = Tertiary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ORDER SUMMARY
            Surface(
                color = SurfacePure,
                shape = RoundedCornerShape(14.dp),
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
                        Text(
                            text = "Tu pedido ($unidades ${if (unidades == 1) "artículo" else "artículos"})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = CaribbeanNavy
                        )
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = CaribbeanNavy,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    lineas.forEach { (producto, cantidad, subtotal) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "$cantidad × ${producto.title}",
                                fontSize = 12.sp,
                                color = Tertiary,
                                maxLines = 1,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Text(
                                text = "RD$ %,.0f".format(subtotal),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CaribbeanNavy
                            )
                        }
                    }
                    if (lineas.isEmpty()) {
                        Text(
                            text = "Tu carrito está vacío.",
                            fontSize = 12.sp,
                            color = Tertiary
                        )
                    }
                    Divider(color = Color.LightGray.copy(alpha = 0.4f), modifier = Modifier.padding(vertical = 6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total a pagar:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CaribbeanNavy)
                        Text(
                            text = "RD$ %,.0f".format(total),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = CrimsonAccent
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Envío GRATIS (Metro Pac / 24h)",
                        fontSize = 11.sp,
                        color = SuccessGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // CONTACT FORM
            Surface(
                color = SurfacePure,
                shape = RoundedCornerShape(14.dp),
                shadowElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Tus datos de contacto",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = CaribbeanNavy
                    )

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkout_nombre_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        label = { Text("Nombre completo *") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Tertiary) }
                    )

                    OutlinedTextField(
                        value = direccion,
                        onValueChange = { direccion = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkout_direccion_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        label = { Text("Dirección de entrega *") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = Tertiary) },
                        placeholder = { Text("Calle, sector, ciudad, provincia") }
                    )

                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkout_telefono_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        label = { Text("Teléfono *") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Tertiary) },
                        placeholder = { Text("809-555-0199") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    OutlinedTextField(
                        value = correo,
                        onValueChange = { correo = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkout_correo_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        label = { Text("Correo electrónico *") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Tertiary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    OutlinedTextField(
                        value = cedula,
                        onValueChange = { cedula = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkout_cedula_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        label = { Text("Cédula o RNC *") },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = Tertiary) },
                        placeholder = { Text("001-1234567-8") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    OutlinedTextField(
                        value = nota,
                        onValueChange = { nota = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkout_nota_input"),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 3,
                        maxLines = 5,
                        label = { Text("Nota (opcional)") },
                        placeholder = { Text("Indicaciones de entrega, referencias, horarios…") }
                    )

                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = VerifiedBlue,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Usamos tus datos únicamente para atender tu compra, conforme a la Política de Privacidad de UNIKO-RD (Ley 158-13).",
                            fontSize = 10.sp,
                            color = Tertiary,
                            lineHeight = 14.sp,
                            modifier = Modifier.weight(1f)
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
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Button(
                    onClick = {
                        viewModel.enviarSolicitudCompra(
                            nombre = nombre,
                            direccion = direccion,
                            telefono = telefono,
                            correo = correo,
                            cedula = cedula,
                            nota = nota
                        ) { ok ->
                            if (ok) viewModel.navigateBack()
                        }
                    },
                    enabled = !enviando && lineas.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonAccent),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("checkout_submit_button")
                ) {
                    if (enviando) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Enviar solicitud de compra",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Te contactamos para coordinar entrega y pago en República Dominicana.",
                    fontSize = 10.sp,
                    color = Tertiary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}
