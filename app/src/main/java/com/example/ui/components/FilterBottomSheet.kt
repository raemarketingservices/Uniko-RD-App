package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Tune
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
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    currentMaxPrice: Float,
    onPriceChange: (Float) -> Unit,
    onlyVerified: Boolean,
    onVerifiedToggle: (Boolean) -> Unit,
    onlyWithShipping: Boolean,
    onShippingToggle: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var sliderValue by remember { mutableFloatStateOf(currentMaxPrice) }
    var verifiedCheck by remember { mutableStateOf(onlyVerified) }
    var shippingCheck by remember { mutableStateOf(onlyWithShipping) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfacePure,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = CaribbeanNavy
                    )
                    Text(
                        text = "Filtros Avanzados",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = CaribbeanNavy
                        )
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = Tertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Price Range Slider
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rango de Precio Máximo",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = CaribbeanNavy
                        )
                    )
                    Text(
                        text = "RD$ %,.0f".format(sliderValue),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = CrimsonAccent
                        )
                    )
                }
                Text(
                    text = "Establece el monto máximo que deseas gastar.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Tertiary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Slider(
                    value = sliderValue,
                    onValueChange = { sliderValue = it },
                    valueRange = 1000f..100000f,
                    steps = 19,
                    colors = SliderDefaults.colors(
                        thumbColor = CrimsonAccent,
                        activeTrackColor = CrimsonAccent,
                        inactiveTrackColor = SurfaceContainerHighest
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("RD$ 1,000", style = MaterialTheme.typography.bodySmall, color = Tertiary)
                    Text("RD$ 50,000", style = MaterialTheme.typography.bodySmall, color = Tertiary)
                    Text("RD$ 100,000+", style = MaterialTheme.typography.bodySmall, color = Tertiary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Delivery & Verification Toggles
            Text(
                text = "Opciones y Garantías RD",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = CaribbeanNavy
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (verifiedCheck) VerifiedBlue.copy(alpha = 0.1f) else SurfaceCanvas)
                    .border(
                        1.dp,
                        if (verifiedCheck) VerifiedBlue else Color.LightGray.copy(alpha = 0.4f),
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { verifiedCheck = !verifiedCheck }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Solo Comercios Verificados",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = CaribbeanNavy
                    )
                    Text(
                        "Validación formal de RNC y Cédula DGII",
                        fontSize = 11.sp,
                        color = Tertiary
                    )
                }
                Checkbox(
                    checked = verifiedCheck,
                    onCheckedChange = { verifiedCheck = it },
                    colors = CheckboxDefaults.colors(checkedColor = VerifiedBlue)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (shippingCheck) SuccessGreen.copy(alpha = 0.1f) else SurfaceCanvas)
                    .border(
                        1.dp,
                        if (shippingCheck) SuccessGreen else Color.LightGray.copy(alpha = 0.4f),
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { shippingCheck = !shippingCheck }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Con Envío Nacional Disponible",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = CaribbeanNavy
                    )
                    Text(
                        "Entregas vía Metro Pac, Caribe Tours y Mensajería",
                        fontSize = 11.sp,
                        color = Tertiary
                    )
                }
                Checkbox(
                    checked = shippingCheck,
                    onCheckedChange = { shippingCheck = it },
                    colors = CheckboxDefaults.colors(checkedColor = SuccessGreen)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        sliderValue = 100000f
                        verifiedCheck = false
                        shippingCheck = false
                        onReset()
                        onDismiss()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Tertiary.copy(alpha = 0.4f)))
                ) {
                    Text("Restablecer", color = CaribbeanNavy, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        onPriceChange(sliderValue)
                        onVerifiedToggle(verifiedCheck)
                        onShippingToggle(shippingCheck)
                        onApply()
                        onDismiss()
                    },
                    modifier = Modifier
                        .weight(1.5f)
                        .height(48.dp)
                        .testTag("apply_filters_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonAccent),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Aplicar Filtros", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
