package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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
            is Screen.Auth, is Screen.Admin -> BottomTab.CUENTA
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

    // Followed stores in session
    val followedStoreIds = MutableStateFlow<Set<String>>(emptySet())
    val userRatings = MutableStateFlow<Map<String, Int>>(emptyMap())

    // Chatbot conversations
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
            repository.addToCart(productId, qty)
            showToast("Producto agregado al carrito 🇩🇴")
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
