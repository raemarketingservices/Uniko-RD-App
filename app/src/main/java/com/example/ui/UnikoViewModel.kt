package com.example.ui

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

sealed class Screen {
    object Home : Screen()
    object Products : Screen()
    data class ProductDetail(val productId: String) : Screen()
    object Services : Screen()
    data class ServiceDetail(val serviceId: String) : Screen()
    object Stores : Screen()
    data class StoreProfile(val storeId: String) : Screen()
    object PublishProduct : Screen()
    object Auth : Screen()
    object Admin : Screen()
    data class Legal(val doc: String) : Screen()
    object Checkout : Screen()
}

enum class BottomTab {
    INICIO, PRODUCTOS, SERVICIOS, TIENDAS, CUENTA
}

data class ChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class UiNotification(
    val message: String,
    val isSuccess: Boolean = true
)

class UnikoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UnikoRepository(AppDatabase.getDatabase(application))

    val products: StateFlow<List<ProductEntity>> = repository.products
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stores: StateFlow<List<StoreEntity>> = repository.stores
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val services: StateFlow<List<ServiceEntity>> = repository.services
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartItems: StateFlow<List<CartItemEntity>> = repository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favorites: StateFlow<List<FavoriteEntity>> = repository.favorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pageBlocks: StateFlow<List<PageBlockEntity>> = repository.pageBlocks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _screenStack = MutableStateFlow<List<Screen>>(listOf(Screen.Home))
    val currentScreen: StateFlow<Screen> = _screenStack.map { it.lastOrNull() ?: Screen.Home }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Screen.Home)

    val currentBottomTab: StateFlow<BottomTab> = _screenStack.map { stack ->
        when (stack.lastOrNull()) {
            is Screen.Home -> BottomTab.INICIO
            is Screen.Products, is Screen.ProductDetail -> BottomTab.PRODUCTOS
            is Screen.Services, is Screen.ServiceDetail -> BottomTab.SERVICIOS
            is Screen.Stores, is Screen.StoreProfile -> BottomTab.TIENDAS
            is Screen.Auth, is Screen.Admin, is Screen.Legal -> BottomTab.CUENTA
            is Screen.Checkout -> BottomTab.PRODUCTOS
            else -> BottomTab.INICIO
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BottomTab.INICIO)

    // Search & Filter State
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("Todas")
    val selectedProvince = MutableStateFlow("Toda RD")
    val filterOnlyVerified = MutableStateFlow(false)
    val filterOnlyVip = MutableStateFlow(false)
    val maxPriceFilter = MutableStateFlow(100000f)

    // UI Modals
    val isFilterSheetVisible = MutableStateFlow(false)
    val isCartSheetVisible = MutableStateFlow(false)
    val isChatbotVisible = MutableStateFlow(false)
    val notification = MutableStateFlow<UiNotification?>(null)

    private val _usuario = MutableStateFlow<UsuarioSesion?>(null)
    val usuario: StateFlow<UsuarioSesion?> = _usuario.asStateFlow()

    private val _autenticando = MutableStateFlow(false)
    val autenticando: StateFlow<Boolean> = _autenticando.asStateFlow()

    private val _enviandoCompra = MutableStateFlow(false)
    val enviandoCompra: StateFlow<Boolean> = _enviandoCompra.asStateFlow()

    // Followed stores in session
    val followedStoreIds = MutableStateFlow<Set<String>>(emptySet())
    val userRatings = MutableStateFlow<Map<String, Int>>(emptyMap())

    // Chatbot conversations
    val datosRemotos = MutableStateFlow(false)
    val sincronizando = MutableStateFlow(false)
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "1",
                text = "¡Hola! Soy UNIKO, tu asistente de compras 🇩🇴. ¿Buscas un producto específico en Santo Domingo, Santiago o necesitas ayuda para contactar a una tienda verificada?",
                isUser = false
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
            refrescar()
        }
        restaurarSesion()
    }

    private fun restaurarSesion() {
        val guardada = SesionStore.cargar(getApplication()) ?: return
        val expiro = guardada.expiraEn in 1 until System.currentTimeMillis()
        if (!expiro) {
            _usuario.value = guardada
            return
        }
        if (guardada.refreshToken.isEmpty()) {
            SesionStore.borrar(getApplication())
            return
        }
        viewModelScope.launch {
            val r = withContext(Dispatchers.IO) { Remoto.refrescar(guardada.refreshToken) }
            if (r.ok && r.usuario != null) {
                SesionStore.guardar(getApplication(), r.usuario)
                _usuario.value = r.usuario
            } else {
                SesionStore.borrar(getApplication())
            }
        }
    }

    fun registrarse(
        email: String,
        password: String,
        nombres: String,
        apellidos: String,
        cedula: String,
        telefono: String,
        esTienda: Boolean,
        aceptaTerminos: Boolean,
        aceptaMarketing: Boolean
    ) {
        val emailLimpio = email.trim()
        when {
            nombres.isBlank() || apellidos.isBlank() -> {
                showToast("Escribe tus nombres y apellidos.", isSuccess = false)
                return
            }
            !Patterns.EMAIL_ADDRESS.matcher(emailLimpio).matches() -> {
                showToast("Escribe un correo electrónico válido.", isSuccess = false)
                return
            }
            password.length < 6 -> {
                showToast("La contraseña debe tener al menos 6 caracteres.", isSuccess = false)
                return
            }
            !aceptaTerminos -> {
                showToast("Debes aceptar los Términos y la Política de Privacidad.", isSuccess = false)
                return
            }
        }
        viewModelScope.launch {
            _autenticando.value = true
            try {
                val r = withContext(Dispatchers.IO) {
                    Remoto.registrar(
                        email = emailLimpio,
                        password = password,
                        nombres = nombres.trim(),
                        apellidos = apellidos.trim(),
                        cedula = cedula.trim(),
                        telefono = telefono.trim(),
                        esTienda = esTienda,
                        aceptaTerminos = aceptaTerminos,
                        aceptaMarketing = aceptaMarketing
                    )
                }
                when {
                    r.ok && r.usuario != null -> {
                        SesionStore.guardar(getApplication(), r.usuario)
                        _usuario.value = r.usuario
                        showToast("¡Cuenta creada! Bienvenido/a a UNIKO-RD 🇩🇴")
                    }
                    r.ok && r.requiereConfirmacion ->
                        showToast("Cuenta creada. Revisa tu correo para confirmarla.")
                    else -> showToast(r.error ?: "No se pudo crear la cuenta.", isSuccess = false)
                }
            } catch (e: Exception) {
                showToast("Sin conexión. Revisa tu internet e inténtalo de nuevo.", isSuccess = false)
            } finally {
                _autenticando.value = false
            }
        }
    }

    fun entrar(email: String, password: String) {
        val emailLimpio = email.trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(emailLimpio).matches()) {
            showToast("Escribe un correo electrónico válido.", isSuccess = false)
            return
        }
        if (password.isEmpty()) {
            showToast("Escribe tu contraseña.", isSuccess = false)
            return
        }
        viewModelScope.launch {
            _autenticando.value = true
            try {
                val r = withContext(Dispatchers.IO) { Remoto.entrar(emailLimpio, password) }
                if (r.ok && r.usuario != null) {
                    SesionStore.guardar(getApplication(), r.usuario)
                    _usuario.value = r.usuario
                    showToast("¡Bienvenido/a de vuelta a UNIKO-RD!")
                } else {
                    showToast(r.error ?: "No se pudo iniciar sesión.", isSuccess = false)
                }
            } catch (e: Exception) {
                showToast("Sin conexión. Revisa tu internet e inténtalo de nuevo.", isSuccess = false)
            } finally {
                _autenticando.value = false
            }
        }
    }

    fun recuperarContrasena(email: String) {
        val emailLimpio = email.trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(emailLimpio).matches()) {
            showToast("Escribe tu correo en el campo de correo.", isSuccess = false)
            return
        }
        viewModelScope.launch {
            val r = withContext(Dispatchers.IO) { Remoto.recuperar(emailLimpio) }
            if (r.ok) {
                showToast("Te enviamos un enlace de recuperación a tu correo.")
            } else {
                showToast(r.error ?: "No se pudo enviar el correo.", isSuccess = false)
            }
        }
    }

    fun cerrarSesion() {
        SesionStore.borrar(getApplication())
        _usuario.value = null
        showToast("Sesión cerrada.")
    }

    fun refrescar() {
        viewModelScope.launch {
            sincronizando.value = true
            datosRemotos.value = repository.sincronizarRemoto()
            sincronizando.value = false
        }
    }

    fun navigateTo(screen: Screen) {
        val current = _screenStack.value
        if (current.lastOrNull() != screen) {
            _screenStack.value = current + screen
        }
    }

    fun navigateBack(): Boolean {
        val current = _screenStack.value
        return if (current.size > 1) {
            _screenStack.value = current.dropLast(1)
            true
        } else {
            false
        }
    }

    fun selectBottomTab(tab: BottomTab) {
        val targetScreen = when (tab) {
            BottomTab.INICIO -> Screen.Home
            BottomTab.PRODUCTOS -> Screen.Products
            BottomTab.SERVICIOS -> Screen.Services
            BottomTab.TIENDAS -> Screen.Stores
            BottomTab.CUENTA -> Screen.Auth
        }
        _screenStack.value = listOf(targetScreen)
    }

    fun showToast(msg: String, isSuccess: Boolean = true) {
        notification.value = UiNotification(msg, isSuccess)
        viewModelScope.launch {
            delay(3500)
            if (notification.value?.message == msg) {
                notification.value = null
            }
        }
    }

    fun addToCart(productId: String, qty: Int = 1) {
        viewModelScope.launch {
            try {
                repository.addToCart(productId, qty)
                showToast("Producto agregado al carrito 🇩🇴")
            } catch (e: Exception) {
                showToast("No se pudo agregar al carrito. Intenta de nuevo.", isSuccess = false)
            }
        }
    }

    fun updateCartQty(productId: String, qty: Int) {
        viewModelScope.launch {
            if (qty <= 0) {
                repository.removeFromCart(productId)
            } else {
                repository.updateCartQuantity(productId, qty)
            }
        }
    }

    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            repository.removeFromCart(productId)
            showToast("Producto removido del carrito")
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    fun enviarSolicitudCompra(
        nombre: String,
        direccion: String,
        telefono: String,
        correo: String,
        cedula: String,
        nota: String,
        terminar: (ok: Boolean) -> Unit
    ) {
        val n = nombre.trim()
        val dir = direccion.trim()
        val tel = telefono.trim()
        val mail = correo.trim()
        val ced = cedula.trim()
        when {
            n.length < 3 -> {
                showToast("Escribe tu nombre completo.", isSuccess = false)
                terminar(false)
                return
            }
            dir.length < 5 -> {
                showToast("Escribe tu dirección de entrega.", isSuccess = false)
                terminar(false)
                return
            }
            tel.filter { it.isDigit() }.length < 10 -> {
                showToast("Escribe un teléfono válido (10 dígitos).", isSuccess = false)
                terminar(false)
                return
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches() -> {
                showToast("Escribe un correo electrónico válido.", isSuccess = false)
                terminar(false)
                return
            }
            ced.filter { it.isDigit() }.length < 7 -> {
                showToast("Escribe tu cédula o RNC válido.", isSuccess = false)
                terminar(false)
                return
            }
            cartItems.value.isEmpty() -> {
                showToast("Tu carrito está vacío.", isSuccess = false)
                terminar(false)
                return
            }
        }

        val mapaProductos = products.value.associateBy { it.id }
        val itemsArr = JSONArray()
        var total = 0.0
        for (item in cartItems.value) {
            val p = mapaProductos[item.productId] ?: continue
            total += p.price * item.quantity
            itemsArr.put(
                JSONObject().apply {
                    put("id", p.id)
                    put("titulo", p.title)
                    put("tienda", p.storeName)
                    put("precio", p.price)
                    put("cantidad", item.quantity)
                }
            )
        }
        if (itemsArr.length() == 0) {
            showToast("Tu carrito está vacío.", isSuccess = false)
            terminar(false)
            return
        }

        val body = JSONObject().apply {
            put("full_name", n)
            put("address", dir)
            put("phone", tel)
            put("email", mail)
            put("cedula", ced)
            put("note", if (nota.isBlank()) JSONObject.NULL else nota.trim())
            put("items", itemsArr)
            put("total", total)
            put("source", "app")
            _usuario.value?.id?.takeIf { it.isNotEmpty() }?.let { put("user_id", it) }
        }

        viewModelScope.launch {
            _enviandoCompra.value = true
            try {
                val err = withContext(Dispatchers.IO) { Remoto.enviarSolicitud(body) }
                if (err == null) {
                    repository.clearCart()
                    showToast("¡Solicitud de compra enviada! Te contactaremos pronto.")
                    terminar(true)
                } else {
                    showToast(err, isSuccess = false)
                    terminar(false)
                }
            } catch (e: Exception) {
                showToast("Sin conexión. Tu solicitud no se envió. Intenta de nuevo.", isSuccess = false)
                terminar(false)
            } finally {
                _enviandoCompra.value = false
            }
        }
    }

    fun toggleFavorite(itemId: String, type: String = "product") {
        viewModelScope.launch {
            repository.toggleFavorite(itemId, type)
        }
    }

    fun toggleFollowStore(storeId: String) {
        val current = followedStoreIds.value
        if (current.contains(storeId)) {
            followedStoreIds.value = current - storeId
            showToast("Dejaste de seguir esta tienda")
        } else {
            followedStoreIds.value = current + storeId
            showToast("¡Ahora sigues esta tienda!")
        }
    }

    fun rateStore(storeId: String, stars: Int) {
        userRatings.value = userRatings.value + (storeId to stars)
        showToast("¡Calificaste con $stars estrellas! Guardado.")
    }

    fun sendChatMessage(userText: String) {
        if (userText.isBlank()) return
        val userMsg = ChatMessage(id = System.currentTimeMillis().toString(), text = userText, isUser = true)
        _chatMessages.value = _chatMessages.value + userMsg

        viewModelScope.launch {
            delay(800)
            val lower = userText.lowercase()
            val replyText = when {
                lower.contains("sony") || lower.contains("audifonos") || lower.contains("auricular") ->
                    "Tenemos los Audífonos Sony WH-1000XM5 en oferta por RD$ 18,900 en Tech Caribe RD (Santo Domingo). Cuentan con garantía local de 90 días y envío express en 24h."
                lower.contains("envio") || lower.contains("entrega") || lower.contains("delivery") ->
                    "Hacemos envíos garantizados a todo el país vía Metro Pac, Caribe Tours y Vimenpaq. En Santo Domingo y Santiago ofrecemos entrega express en 24 horas."
                lower.contains("pago") || lower.contains("tarjeta") || lower.contains("transferencia") ->
                    "Puedes pagar con tarjeta de débito/crédito, transferencia bancaria Popular / Banreservas / BHD, o contra entrega en efectivo (RD$)."
                lower.contains("tienda") || lower.contains("vender") || lower.contains("comercio") ->
                    "¡Puedes registrar tu tienda gratis en UNIKO-RD! Sin comisión por los primeros 30 días para comercios dominicanos con RNC o Cédula validada."
                lower.contains("reparacion") || lower.contains("pantalla") || lower.contains("tecnico") ->
                    "En la sección de Servicios encontrarás talleres certificados como Tech Fix Santo Domingo para reparación de pantallas y baterías iPhone & Mac en 45 minutos."
                else ->
                    "Con gusto te asisto. En UNIKO-RD contamos con más de 1,200 productos y comercios verificados en las 32 provincias de República Dominicana. ¿Deseas ver tecnología, moda o servicios?"
            }
            _chatMessages.value = _chatMessages.value + ChatMessage(
                id = (System.currentTimeMillis() + 1).toString(),
                text = replyText,
                isUser = false
            )
        }
    }

    // SuperAdmin controls
    fun toggleStoreVerified(store: StoreEntity) {
        viewModelScope.launch {
            val updated = store.copy(isVerified = !store.isVerified)
            repository.updateStore(updated)
            showToast("Estado de verificación actualizado para ${store.name}")
        }
    }

    fun toggleStoreVip(store: StoreEntity) {
        viewModelScope.launch {
            val updated = store.copy(isVip = !store.isVip)
            repository.updateStore(updated)
            showToast("Estado VIP actualizado para ${store.name}")
        }
    }

    fun deleteStore(storeId: String, storeName: String) {
        viewModelScope.launch {
            repository.deleteStoreById(storeId)
            showToast("Tienda '$storeName' eliminada correctamente en cascada.")
        }
    }

    fun deleteAllStoresCascade() {
        viewModelScope.launch {
            repository.deleteAllStores()
            showToast("Comando de purga ejecutado. Todas las tiendas eliminadas.")
        }
    }
}
