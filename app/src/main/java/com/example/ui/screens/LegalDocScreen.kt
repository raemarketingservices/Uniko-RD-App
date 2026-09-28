package com.example.ui.screens

import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.UnikoViewModel
import com.example.ui.theme.*

private val NavyLegal = Color(0xFF002B7F)

private val titulosLegales = mapOf(
    "terminos" to "Términos y Condiciones",
    "privacidad" to "Política de Privacidad",
    "cookies" to "Política de Cookies",
    "aviso" to "Aviso de Comercio Electrónico"
)

@Composable
fun LegalDocScreen(
    doc: String,
    viewModel: UnikoViewModel,
    modifier: Modifier = Modifier
) {
    val titulo = titulosLegales[doc] ?: "Documentos legales"
    var cargando by remember { mutableStateOf(true) }
    var falloCarga by remember { mutableStateOf(false) }
    var recarga by remember { mutableStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FC))
    ) {
        Surface(
            color = SurfacePure,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = NavyLegal
                    )
                }
                Text(
                    text = titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NavyLegal
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            if (falloCarga) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "No se pudo cargar el documento.",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )
                    Button(
                        onClick = {
                            falloCarga = false
                            cargando = true
                            recarga++
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyLegal),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Reintentar", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                key(recarga) {
                    AndroidView(
                        factory = { ctx ->
                            WebView(ctx).apply {
                                settings.javaScriptEnabled = true
                                settings.domStorageEnabled = true
                                webViewClient = object : WebViewClient() {
                                    override fun onPageFinished(view: WebView?, url: String?) {
                                        cargando = false
                                    }

                                    override fun onReceivedError(
                                        view: WebView?,
                                        request: WebResourceRequest?,
                                        err: WebResourceError?
                                    ) {
                                        if (request?.isForMainFrame == true) {
                                            cargando = false
                                            falloCarga = true
                                        }
                                    }
                                }
                                loadUrl("https://uniko-rd.com/legal/$doc")
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                if (cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = NavyLegal,
                        strokeWidth = 3.dp
                    )
                }
            }
        }
    }
}
