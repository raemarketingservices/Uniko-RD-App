package com.example.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UnikoRepository(private val db: AppDatabase) {

    val products: Flow<List<ProductEntity>> = db.productDao().getAllProducts()
    val stores: Flow<List<StoreEntity>> = db.storeDao().getAllStores()
    val services: Flow<List<ServiceEntity>> = db.serviceDao().getAllServices()
    val cartItems: Flow<List<CartItemEntity>> = db.cartDao().getCartItems()
    val favorites: Flow<List<FavoriteEntity>> = db.favoriteDao().getAllFavorites()
    val pageBlocks: Flow<List<PageBlockEntity>> = db.pageBlockDao().getPageBlocks()

    suspend fun getProductById(id: String): ProductEntity? = db.productDao().getProductById(id)
    suspend fun getStoreById(id: String): StoreEntity? = db.storeDao().getStoreById(id)
    suspend fun getServiceById(id: String): ServiceEntity? = db.serviceDao().getServiceById(id)

    suspend fun insertProduct(product: ProductEntity) = db.productDao().insertProduct(product)
    suspend fun insertStore(store: StoreEntity) = db.storeDao().insertStore(store)
    suspend fun updateStore(store: StoreEntity) = db.storeDao().updateStore(store)
    suspend fun deleteStoreById(id: String) = db.storeDao().deleteStoreById(id)
    suspend fun deleteAllStores() = db.storeDao().deleteAllStores()

    suspend fun addToCart(productId: String, quantity: Int = 1) {
        db.cartDao().addToCart(CartItemEntity(productId = productId, quantity = quantity))
    }

    suspend fun removeFromCart(productId: String) = db.cartDao().removeFromCart(productId)
    suspend fun updateCartQuantity(productId: String, qty: Int) = db.cartDao().updateQuantity(productId, qty)
    suspend fun clearCart() = db.cartDao().clearCart()

    suspend fun toggleFavorite(itemId: String, type: String = "product") {
        if (db.favoriteDao().isFavorite(itemId)) {
            db.favoriteDao().removeFavorite(itemId)
        } else {
            db.favoriteDao().addFavorite(FavoriteEntity(itemId = itemId, itemType = type))
        }
    }

    suspend fun updatePageBlock(block: PageBlockEntity) = db.pageBlockDao().updatePageBlock(block)

    suspend fun seedInitialDataIfEmpty() {
        val currentProducts = db.productDao().getAllProducts().firstOrNull()
        if (currentProducts.isNullOrEmpty()) {
            db.storeDao().insertStores(sampleStores)
            db.productDao().insertProducts(sampleProducts)
            db.serviceDao().insertServices(sampleServices)
            db.pageBlockDao().insertPageBlocks(samplePageBlocks)
            // Seed 3 cart items as shown in badge
            db.cartDao().addToCart(CartItemEntity("prod_sony_xm5", 1))
            db.cartDao().addToCart(CartItemEntity("prod_oster_latte", 1))
            db.cartDao().addToCart(CartItemEntity("prod_nike_airmax", 1))
        }
    }

    suspend fun sincronizarRemoto(): Boolean = withContext(Dispatchers.IO) {
        try {
            val cats = Remoto.categorias()
            val tiendas = Remoto.tiendas()
            val productos = Remoto.productos(cats)
            val servicios = Remoto.servicios(cats)
            if (tiendas.isEmpty() && productos.isEmpty() && servicios.isEmpty()) {
                return@withContext false
            }
            if (tiendas.isNotEmpty()) {
                db.storeDao().deleteAllStores()
                db.storeDao().insertStores(tiendas)
            }
            if (productos.isNotEmpty()) {
                db.productDao().deleteAllProducts()
                db.productDao().insertProducts(productos)
            }
            if (servicios.isNotEmpty()) {
                db.serviceDao().deleteAllServices()
                db.serviceDao().insertServices(servicios)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        private const val SONY_HERO = "https://lh3.googleusercontent.com/aida-public/AB6AXuC_1suK5pXEQppMBm96Go5LQiJvQg4s61gS9Fdydpu_DFpUmiRT6t8CKxzE5POZ9fZzYjVX0DSB6oea8ZmU2Sx0kImgjopUKzwPLeVng11bjBJSUmeBbTeuwM5ObS6WLYxrguHgCAB6SasatZgcdXSaWP5W6kptGLbVk_9GmepU7ICPSJ1fEuCIib3E1i0EOfjT6UzXZoG00NtpyjFFGBKbQYiMSR7fNuf_TozKssNa9X8-4bqMZr1WuA"
        private const val OSTER_HERO = "https://lh3.googleusercontent.com/aida-public/AB6AXuA6SkAPtXQMIQcIp03wHf5q0V3-vR-Q-Ktk0FrWFc3IIUCO8MkDH6ND2LO4bM1JqwuqCImFYAZPbt8jr1p-UVhtrv9xibJEnajZQH2xKxl3rVyHj-KjNWjWDs_p4Zs8WiH8g0tuyRqh7dsrvlvxEv1f1Yh6gi36_nZLwcU2lW9XJOQ9O2l5sep4R3sHHB2Nf3X3hP9yrykl2fGjrXZbDjzmvKqBDJbXsbSvS5xVspTxf_BlhuGaiQsEDQ"
        private const val NIKE_HERO = "https://lh3.googleusercontent.com/aida-public/AB6AXuAWkh1YQTtdOqPWH3mENXnJoagdh5VjOWM8BwLhlkhc8YpDQOq0UljTpfdB7s7PpvwZzsZ4TVMojFqWa9FPf7QslBVcM9hQCySsU4YQ6ktYonn5xLvRECNGTxfEmpwEO0xISoa2DRucH9r1Nw62eDK75PN1iDKWDKp9n_UZ7udcjnyUH-h3P-Q_bHPXAbZniQGZ7ivSZsmL2FXAzs7cTlhxftkId8FmwzHuhKJqlpCdQb-zsbnCY9iDFQ"
        private const val THINKPAD_HERO = "https://lh3.googleusercontent.com/aida-public/AB6AXuDnxY6HHMf7RxPHquPqk4QmQd1zyEffe_9WMjmmD4-250wzAmUBvPhkV03QZ-nugV671ACHUcNlx6Fh-IQ0JAvFtOpnmJm9Smve6I4SCA7QjAeZSFDpmW2bm5tYhukwzyxvVGTrjkhoVBMuxJ1_yoOR3VoHvaHvPF99VA0y30_JUg5lJJDKe-8LMgHlnzcFE2jXiruwRETqGDHPaQ0g3QVvXsi2Bftw3ZCQ_R_p5M_GaH-41vudRRtBeA"
        private const val AMAZFIT_HERO = "https://lh3.googleusercontent.com/aida-public/AB6AXuDs6C6gWhko1VuehNwG5lFgjGlUjDdzq-VGOfFCoPRhzw3WUdQGvDx6OT9jVODeZECsEgDZHuwoXx6dYQl34RDrrSmW09Wfd-M8p1Ai87jaN2q1z0NzZYxFv6PKzQdrXeG1EiM9ePhdAISQnUArC9RWnbBNQ-ML_YNUsEFyCtUCYstoL1NIuntj16Im4SODgwqYul2Fg0rgNEW1tDwDmgr487FHGMhgqzGE5LMRoGa4b2Dzf2Z1J6LR8g"
        private const val TFAL_HERO = "https://lh3.googleusercontent.com/aida-public/AB6AXuA7XQzfafdEj7Q7EXOGHT_Nfj2hR0FLK9U0s09Hi9xea5bsSwV8URx2v0XY6xcnD1Uvrd-nTt84k7d4QO1X6E5Fv4Z6VSf7zdbzdrK03XFc-_Pc_56P7yZ7RDdo26e6zjzcR1gPR9UmtIwBW6wcaRyr06psr3vj53bpH_q_OO4j7BIZoD0jFh6a0zTV1hlwfqBGkE2dHm_9nezl8q8bzgKw7y7e8t9rB4VTDi7gdIMOHeJwH2_toxqckw"
        private const val SMARTWATCH_HERO = "https://lh3.googleusercontent.com/aida-public/AB6AXuDeeiUOqJyfeNZY--nmY4upg_NPd1fDfnZoGC-Jiixdw2WO-54LkgubqSJy-nkh1CvjATPlVRSFjhyxg3owbWhc06xSavGbZTE3EKBp50z3B3ddeud6Hh63RxUP23RWr4GUh9ZNvOHDKN_Rk0xBhSoHYWc-VILGk7gdmqPYYpPcxhp9purwxz8jtvIvBlkEA0WrxVivVK0GYjocoFGmPWVu6hW-Qw07wu3YqrH3DnmDUogzDt2YFrvaRw"
        private const val EARBUDS_HERO = "https://lh3.googleusercontent.com/aida-public/AB6AXuAzg4xgDRUPYNKdafTitBXd7zQxLE2mYFpDmVBNAaBd4JJVJLfBhASgAEA-6i-QloLWtTHAtSpTJNfE_IwZRkn77XobzznMIFSRfXjmlUAaB0VycFbJ1NmQ_QsW_A9HHwRZa4iR93fHnWOPDvLMIUwcx_z3ROMOSmPWNZDQJUZbXf1kpVoKF1Qf8QMf25MRzH5Isp_zKRsSd2XJleXH47s5_6Hjmw3keTl9A8fRzmpD5AC1WU7VQma_yQ"
        private const val GAN_HERO = "https://lh3.googleusercontent.com/aida-public/AB6AXuDMYpjhnI1bU9e87ap1PX1kJtb3snUbKqQzqKYXG9hKs-ZCaFI2h-EMmHhnDx0nVGMYwP_AZEcKyj0RxkVN87V0TOK7dBraw_9IsoViNtVgD0gM6zB3KTu2YuzbRlMHNW0ETn0a6vgEw2qT4XBYVxJjdQYLn9ebLBhii-rzLB53yr7h_H74B8RthaMZLEXB9YIEXrRxDKnM99JRd82KhJEF5Y5dj_oLGSCYjmJ5blrmgGAE-a0l5TR_VA"
        private const val STORE_COVER_HERO = "https://lh3.googleusercontent.com/aida-public/AB6AXuBiZ5Vtnit23mqg_eG1lE-_z65O_sjhnZ3wL0QF_noVPOt7mqpPPhShpe9wWZf2HV1DoxbN5PobK6pDNL538nR7uQP3VvDNYUXAncCTAqN46Su8XXSzoCiImhQGP3MGJFlk-NJ1_5nHBPwJu7itECU9r0nFFdxh095RVJ3GiG_qWLS0PKCJAuvxZMbbHr0OtvX70PEfnA3QkGRXqo6MvwszGOyJL9Jg0rgvwjDHbLQ5HL7pCvV64EKqlA"

        private const val SERV1_HERO = "https://lh3.googleusercontent.com/aida-public/AB6AXuBGQ1Z7JSmtcszRcgO43dLFoZg3-FQ4m7HNglholHXqji3KfnDrKj_-KSJM9F8F3tQPvmHzmgn7RhmwA5c956Yd7XF5flUVW0he7I_WKl6DSZAdcpVXYw2ImkwwEfcvGw4ZCA4UaXysv3WICBkV0PeZEP_lfB0ulEp1NXZnJqVyZldVdm3t1tM1qlzhQnWZmJsuflKKtn9rLF7Wta3Zser7YDiE2lwKBwTisBkXyIiTZFzhcMWPVrd6-w"
        private const val SERV2_HERO = "https://lh3.googleusercontent.com/aida-public/AB6AXuAK_bq5YtAYXjS0gQZ8ZYFd_MJj3b1WCWiv2YpNPRTeeNV0UA8YvZ-_Q01w3ldZmD074OlGNvHBvH5TsTHWQS65SGcUaogfTKSb3aWqMSZaiieW2GxLHEQXqWYEXRoi0N-QztkniMztOyhdv90EE8Brr3YOScWDCuirBDYUa63vPhNZRsOhTnp1D2WPb9nIncV3Y10OdoXJP8Ny-qSFYpWW1HsSBOtPV30kMO2SsD-TKxRj_yV6xpVf-A"
        private const val SERV3_HERO = "https://lh3.googleusercontent.com/aida-public/AB6AXuB95I_W_wix33k-vmGV0zS74Zr0pQw4k5OvfSOqFEGeoWRdzjm-CR3l_LyMG1LdAmksEDPWFPqjG5R14_odcsAGdL5CppjQGbXjR5atcuhukM9KB9cY9G2g0GZdRYNB5WJdRxFMJqNT3aVelchRHcUfMiivr-Q5Nc-mWnXbnk7tj_e0BCZTEZ-tsS6A511zXdOHWt0IdxtbvaRm-tq37OWdxw3Zy_v68hw7wrICmLcd1W_bGXNGefj-VA"
        private const val SERV4_HERO = "https://lh3.googleusercontent.com/aida-public/AB6AXuBqEBxB-hu8BrZC9D-DQSUefA21dXxJAUKUIISY6bo16Xe3RLC7rsHMlX8Xpzaw-i02GYxFVhZtrwnHgsoijwiLberksQ_Nz_zr_eYUmFQHsOlx7p6pakO5LjGQHEJlrCp2XrZk3p6F8JnGpD0FnH6ZtaxwM8XfxN5rBqBaVOv98JdLmbwncQVg9leOe8U5ByUIwX3b3WWRDpkrS38pVz7xc4u0w_T6DU1vBZm0d83lQj0Fmd-lwJ8gCA"

        val sampleStores = listOf(
            StoreEntity(
                id = "store_tech_caribe",
                name = "Tech Caribe RD",
                ownerName = "Carlos Henríquez",
                category = "Tecnología & Gadgets",
                province = "Santo Domingo D.N.",
                rnc = "131-48291-3",
                rating = 4.9f,
                reviewCount = 88,
                followers = 1420,
                productsCount = 142,
                avatarInitials = "TC",
                isVerified = true,
                isVip = true,
                description = "Comercio oficial importador de tecnología de alta gama en República Dominicana. Envíos garantizados en 24h a todo el país y garantía local en Santo Domingo.",
                address = "Av. 27 de Febrero #204, Plaza Central, Santo Domingo D.N.",
                bannerImageUrl = STORE_COVER_HERO
            ),
            StoreEntity(
                id = "store_cibao_auto",
                name = "Cibao Repuestos Auto",
                ownerName = "Yomaira Santos",
                category = "Repuestos y Accesorios",
                province = "Santiago de los Caballeros",
                rnc = "101-92384-5",
                rating = 4.8f,
                reviewCount = 185,
                followers = 2100,
                productsCount = 95,
                avatarInitials = "CB",
                isVerified = true,
                isVip = true,
                description = "Distribuidor líder de autopartes y repuestos OEM japoneses y americanos en toda la región norte y el Cibao.",
                address = "Av. Estrella Sadhalá, Santiago"
            ),
            StoreEntity(
                id = "store_moda_quisqueya",
                name = "Moda Criolla Quisqueya",
                ownerName = "Rafael Almonte",
                category = "Ropa y Accesorios",
                province = "La Romana",
                rnc = "132-00918-2",
                rating = 4.7f,
                reviewCount = 45,
                followers = 890,
                productsCount = 64,
                avatarInitials = "MQ",
                isVerified = true,
                isVip = true,
                description = "Confección artesanal de chacabanas en lino 100%, vestidos tropicales y calzado confeccionado por artesanos dominicanos.",
                address = "Calle Francisco Castillo Márquez, La Romana"
            ),
            StoreEntity(
                id = "store_artesanias_este",
                name = "Artesanías del Este",
                ownerName = "Rafael Almonte",
                category = "Artesanía & Souvenirs",
                province = "Punta Cana, La Altagracia",
                rnc = "132-00918-2",
                rating = 4.9f,
                reviewCount = 24,
                followers = 530,
                productsCount = 38,
                avatarInitials = "AE",
                isVerified = true,
                isVip = false,
                description = "Joyería fina en Larimar azul dominicano y Ámbar auténtico con certificado gemológico nacional.",
                address = "Bávaro, Punta Cana"
            ),
            StoreEntity(
                id = "store_electrolider",
                name = "ElectroLíder RD",
                ownerName = "Julio Martínez",
                category = "Electrodomésticos",
                province = "Santo Domingo D.N.",
                rnc = "130-99412-1",
                rating = 4.9f,
                reviewCount = 420,
                followers = 3200,
                productsCount = 210,
                avatarInitials = "EL",
                isVerified = true,
                isVip = false,
                description = "Líder en electrodomésticos para el hogar, cocinas, cafeteras e iluminación con garantía extendida en RD.",
                address = "Av. John F. Kennedy, D.N."
            )
        )

        val sampleProducts = listOf(
            ProductEntity(
                id = "prod_sony_xm5",
                title = "Audífonos Sony WH-1000XM5",
                storeId = "store_tech_caribe",
                storeName = "Tech Caribe RD",
                price = 18900.0,
                originalPrice = 21500.0,
                rating = 4.9f,
                reviewCount = 128,
                imageUrl = SONY_HERO,
                category = "Tecnología",
                province = "Santo Domingo D.N.",
                isVerified = true,
                isFeatured = true,
                hasShipping = true,
                sku = "PRD-89421",
                description = "Los audífonos Sony WH-1000XM5 reescriben las reglas de la escucha sin distracciones. Equipados con dos procesadores y ocho micrófonos dedicados a la cancelación activa de ruido líder en el mercado.",
                discountPercent = 12
            ),
            ProductEntity(
                id = "prod_oster_latte",
                title = "Cafetera Espresso Oster Prima Latte",
                storeId = "store_electrolider",
                storeName = "ElectroLíder RD",
                price = 8450.0,
                originalPrice = 9900.0,
                rating = 4.8f,
                reviewCount = 52,
                imageUrl = OSTER_HERO,
                category = "Hogar",
                province = "Santo Domingo D.N.",
                isVerified = true,
                isFeatured = true,
                hasShipping = true,
                sku = "PRD-77120",
                description = "Bomba italiana de 19 bares, depósito desmontable para leche y selección de bebidas espresso, cappuccino y latte con un solo toque.",
                discountPercent = 15
            ),
            ProductEntity(
                id = "prod_nike_airmax",
                title = "Zapatillas Nike Air Max Running",
                storeId = "store_moda_quisqueya",
                storeName = "UrbanStyle RD",
                price = 4200.0,
                originalPrice = null,
                rating = 4.7f,
                reviewCount = 45,
                imageUrl = NIKE_HERO,
                category = "Moda",
                province = "Santo Domingo D.N.",
                isVerified = true,
                isNew = true,
                hasShipping = true,
                sku = "PRD-54210",
                description = "Zapatillas deportivas con amortiguación Air Max de máxima respuesta, malla transpirable ideal para entrenamiento diario en clima tropical."
            ),
            ProductEntity(
                id = "prod_thinkpad_14",
                title = "Laptop Lenovo ThinkPad 14\"",
                storeId = "store_tech_caribe",
                storeName = "TechZone Santiago",
                price = 34500.0,
                originalPrice = 38000.0,
                rating = 4.8f,
                reviewCount = 92,
                imageUrl = THINKPAD_HERO,
                category = "Tecnología",
                province = "Santiago",
                isVerified = true,
                isBestSeller = true,
                hasShipping = true,
                sku = "PRD-99812",
                description = "Procesador Intel Core i7, 16GB RAM DDR4, SSD 512GB NVMe, pantalla 14\" FHD IPS antirreflejo y teclado resistente a derrames con TrackPoint.",
                discountPercent = 9
            ),
            ProductEntity(
                id = "prod_amazfit_gts",
                title = "Reloj Inteligente Amazfit GTS",
                storeId = "store_tech_caribe",
                storeName = "Gadgets RD",
                price = 3100.0,
                originalPrice = null,
                rating = 4.6f,
                reviewCount = 34,
                imageUrl = AMAZFIT_HERO,
                category = "Tecnología",
                province = "Santo Domingo D.N.",
                isVerified = true,
                hasShipping = true,
                sku = "PRD-33012",
                description = "Pantalla AMOLED cuadrada de 1.65\", monitor de frecuencia cardíaca 24/7, medición de oxígeno SpO2, más de 70 modos de deporte y resistencia al agua 5 ATM."
            ),
            ProductEntity(
                id = "prod_tfal_sartenes",
                title = "Juego de Sartenes Antiadherentes T-fal",
                storeId = "store_electrolider",
                storeName = "Hogar Dominicano",
                price = 2750.0,
                originalPrice = null,
                rating = 4.9f,
                reviewCount = 71,
                imageUrl = TFAL_HERO,
                category = "Hogar",
                province = "Santo Domingo D.N.",
                isVerified = false,
                hasShipping = true,
                sku = "PRD-12940",
                description = "Set de 3 sartenes con indicador de calor Thermo-Spot original, recubrimiento antiadherente de titanio y base difusora compatible con todas las estufas."
            ),
            ProductEntity(
                id = "prod_smartwatch_sport",
                title = "Smartwatch Series 8 Sport",
                storeId = "store_tech_caribe",
                storeName = "Tech Caribe RD",
                price = 4200.0,
                originalPrice = 4990.0,
                rating = 4.8f,
                reviewCount = 65,
                imageUrl = SMARTWATCH_HERO,
                category = "Tecnología",
                province = "Santo Domingo D.N.",
                isVerified = true,
                isNew = true,
                hasShipping = true,
                sku = "PRD-88204",
                description = "Reloj inteligente resistente con sensor cardíaco, llamadas Bluetooth, correa deportiva intercambiable y pantalla HD Always-On.",
                discountPercent = 15
            ),
            ProductEntity(
                id = "prod_earbuds_pro",
                title = "Earbuds Pro Wireless ANC",
                storeId = "store_tech_caribe",
                storeName = "Audio Pro",
                price = 6500.0,
                originalPrice = 7500.0,
                rating = 4.9f,
                reviewCount = 88,
                imageUrl = EARBUDS_HERO,
                category = "Tecnología",
                province = "Santo Domingo D.N.",
                isVerified = true,
                hasShipping = true,
                sku = "PRD-61902",
                description = "Auriculares inalámbricos con cancelación activa de ruido, modo ambiente transparente, estuche de carga USB-C y sonido envolvente Hi-Fi.",
                discountPercent = 13
            ),
            ProductEntity(
                id = "prod_gan_charger",
                title = "Cargador Rápido GaN 65W",
                storeId = "store_tech_caribe",
                storeName = "Accesorios",
                price = 1890.0,
                originalPrice = 2400.0,
                rating = 4.9f,
                reviewCount = 110,
                imageUrl = GAN_HERO,
                category = "Tecnología",
                province = "Santo Domingo D.N.",
                isVerified = true,
                isBestSeller = true,
                hasShipping = true,
                sku = "PRD-21045",
                description = "Tecnología de nitruro de galio (GaN) ultracompacta. Doble puerto USB-C y USB-A capaz de cargar laptops, tablets y smartphones a máxima velocidad.",
                discountPercent = 21
            )
        )

        val sampleServices = listOf(
            ServiceEntity(
                id = "serv_screen_repair",
                title = "Reparación de Pantallas OLED iPhone & Samsung con Garantía",
                providerName = "Tech Fix Santo Domingo",
                category = "Reparación & Celulares",
                province = "Santo Domingo D.N.",
                rating = 4.9f,
                reviewCount = 54,
                priceStarting = 1850.0,
                priceOld = 2200.0,
                imageUrl = SERV1_HERO,
                isRncVerified = true,
                coverage = "D.N. y Santo Domingo Este · Taller & Domicilio",
                description = "Técnicos certificados con repuestos grado A y garantía escrita de 90 días en República Dominicana. Diagnóstico gratis si reparas con nosotros. Utilizamos sellado térmico original para mantener la resistencia a salpicaduras.",
                turnaroundTime = "Entrega en 45 Minutos"
            ),
            ServiceEntity(
                id = "serv_ac_inverter",
                title = "Instalación & Mantenimiento Preventivo de Aires Inverter",
                providerName = "ClimaExpress RD",
                category = "Instalación & Hogar",
                province = "Santiago de los Caballeros",
                rating = 5.0f,
                reviewCount = 89,
                priceStarting = 2800.0,
                priceOld = null,
                imageUrl = SERV2_HERO,
                isRncVerified = true,
                coverage = "Santiago & Zona Cibao · Exclusivo a domicilio",
                description = "Limpieza profunda con hidrolavadora, recarga de gas ecológico R410A y diagnóstico de eficiencia energética.",
                turnaroundTime = "Servicio el mismo día"
            ),
            ServiceEntity(
                id = "serv_auto_mechanic",
                title = "Mecánica Rápida a Domicilio & Diagnóstico Computarizado OBD2",
                providerName = "AutoCare Cibao Móvil",
                category = "Mecánica & Autos",
                province = "Santiago",
                rating = 4.8f,
                reviewCount = 41,
                priceStarting = 1500.0,
                priceOld = null,
                imageUrl = SERV3_HERO,
                isRncVerified = true,
                coverage = "Santiago, La Vega & Moca · Servicio de grúa disponible",
                description = "Escaneo computarizado de motor, transmisión y frenos ABS. Reemplazo de baterías y alternadores a domicilio.",
                turnaroundTime = "Atención en 1 hora"
            ),
            ServiceEntity(
                id = "serv_makeup_beauty",
                title = "Maquillaje Profesional HD & Peinado para Bodas y Eventos",
                providerName = "Glamour Studio Punta Cana",
                category = "Belleza & Estética",
                province = "La Altagracia",
                rating = 4.95f,
                reviewCount = 67,
                priceStarting = 3500.0,
                priceOld = null,
                imageUrl = SERV4_HERO,
                isRncVerified = true,
                coverage = "Punta Cana, Bávaro & La Romana · A hoteles & villas",
                description = "Estilismo nupcial y maquillaje de larga duración a prueba de humedad tropical, productos MAC, Charlotte Tilbury y NARS.",
                turnaroundTime = "Cita previa reservada"
            )
        )

        val samplePageBlocks = listOf(
            PageBlockEntity("block_hero", "1. Bloque: Hero Banner Principal", true, 1, "El marketplace de República Dominicana", "Hero Slider"),
            PageBlockEntity("block_trust", "2. Barra de Confianza (Garantía Local & Envíos RD)", true, 2, "100% Garantía Local", "Trust Bar"),
            PageBlockEntity("block_categories", "3. Grid de Categorías Principales", true, 3, "Categorías Populares", "Category Grid"),
            PageBlockEntity("block_top_products", "4. Productos Más Vendidos de la Semana", true, 4, "Productos Destacados", "Product Grid")
        )
    }
}
