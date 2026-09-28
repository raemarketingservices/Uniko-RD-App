package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.*
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.UnikoRDTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UnikoRDTheme {
                UnikoApp()
            }
        }
    }
}

@Composable
fun UnikoApp(viewModel: UnikoViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentBottomTab by viewModel.currentBottomTab.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val products by viewModel.products.collectAsState()
    val notification by viewModel.notification.collectAsState()

    val isFilterSheetVisible by viewModel.isFilterSheetVisible.collectAsState()
    val isCartSheetVisible by viewModel.isCartSheetVisible.collectAsState()
    val isChatbotVisible by viewModel.isChatbotVisible.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()

    val maxPrice by viewModel.maxPriceFilter.collectAsState()
    val onlyVerified by viewModel.filterOnlyVerified.collectAsState()

    // Handle system back navigation
    BackHandler(enabled = currentScreen !is Screen.Home) {
        viewModel.navigateBack()
    }

    val shouldShowBottomBar = remember(currentScreen) {
        currentScreen is Screen.Home ||
                currentScreen is Screen.Products ||
                currentScreen is Screen.Services ||
                currentScreen is Screen.Stores
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (currentScreen is Screen.Home) {
                UnikoTopAppBar(
                    title = "UNIKO",
                    subtitle = "REPÚBLICA DOMINICANA",
                    showBackButton = false,
                    onSearchClick = { viewModel.navigateTo(Screen.Products) },
                    cartItemCount = cartItems
                        .filter { ci -> products.any { it.id == ci.productId } }
                        .sumOf { it.quantity },
                    onCartClick = { viewModel.isCartSheetVisible.value = true },
                    onAdminClick = { viewModel.navigateTo(Screen.Admin) }
                )
            }
        },
        bottomBar = {
            if (shouldShowBottomBar) {
                UnikoBottomNavBar(
                    currentTab = currentBottomTab,
                    onTabSelected = { tab -> viewModel.selectBottomTab(tab) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                label = "ScreenTransition"
            ) { target ->
                when (target) {
                    is Screen.Home -> HomeScreen(viewModel = viewModel)
                    is Screen.Products -> ProductsScreen(viewModel = viewModel)
                    is Screen.ProductDetail -> ProductDetailScreen(
                        productId = target.productId,
                        viewModel = viewModel
                    )
                    is Screen.Services -> ServicesScreen(viewModel = viewModel)
                    is Screen.ServiceDetail -> ServiceDetailScreen(
                        serviceId = target.serviceId,
                        viewModel = viewModel
                    )
                    is Screen.Stores -> StoresScreen(viewModel = viewModel)
                    is Screen.StoreProfile -> StoreProfileScreen(
                        storeId = target.storeId,
                        viewModel = viewModel
                    )
                    is Screen.PublishProduct -> PublishProductScreen(viewModel = viewModel)
                    is Screen.Auth -> AuthScreen(viewModel = viewModel)
                    is Screen.Legal -> LegalDocScreen(doc = target.doc, viewModel = viewModel)
                    is Screen.Admin -> AdminScreen(viewModel = viewModel)
                    is Screen.Checkout -> CheckoutScreen(viewModel = viewModel)
                }
            }

            // Global floating toast banner
            if (notification != null) {
                ToastNotificationBanner(
                    notification = notification!!,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }

    // Modal Cart Sheet
    if (isCartSheetVisible) {
        CartBottomSheet(
            cartItems = cartItems,
            products = products,
            onDismiss = { viewModel.isCartSheetVisible.value = false },
            onUpdateQty = { id, qty -> viewModel.updateCartQty(id, qty) },
            onRemoveItem = { id -> viewModel.removeFromCart(id) },
            onClearCart = { viewModel.clearCart() },
            onCheckoutClick = {
                viewModel.isCartSheetVisible.value = false
                viewModel.navigateTo(Screen.Checkout)
            }
        )
    }

    // Modal Advanced Filter Sheet
    if (isFilterSheetVisible) {
        FilterBottomSheet(
            currentMaxPrice = maxPrice,
            onPriceChange = { viewModel.maxPriceFilter.value = it },
            onlyVerified = onlyVerified,
            onVerifiedToggle = { viewModel.filterOnlyVerified.value = it },
            onlyWithShipping = true,
            onShippingToggle = {},
            onDismiss = { viewModel.isFilterSheetVisible.value = false },
            onApply = { viewModel.showToast("Filtros aplicados") },
            onReset = {
                viewModel.maxPriceFilter.value = 100000f
                viewModel.filterOnlyVerified.value = false
                viewModel.showToast("Filtros restablecidos")
            }
        )
    }

    // Modal Chatbot Dialog ("Asistente UNIKO")
    if (isChatbotVisible) {
        ChatbotDialog(
            messages = chatMessages,
            onDismiss = { viewModel.isChatbotVisible.value = false },
            onSendMessage = { userText -> viewModel.sendChatMessage(userText) }
        )
    }
}
