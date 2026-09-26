package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val title: String,
    val storeId: String,
    val storeName: String,
    val price: Double,
    val originalPrice: Double? = null,
    val rating: Float = 4.8f,
    val reviewCount: Int = 24,
    val imageUrl: String,
    val category: String,
    val province: String,
    val isVerified: Boolean = true,
    val isFeatured: Boolean = false,
    val isBestSeller: Boolean = false,
    val isNew: Boolean = false,
    val hasShipping: Boolean = true,
    val sku: String,
    val description: String,
    val specifications: String = "",
    val inStock: Boolean = true,
    val discountPercent: Int = 0
)

@Entity(tableName = "stores")
data class StoreEntity(
    @PrimaryKey val id: String,
    val name: String,
    val ownerName: String,
    val category: String,
    val province: String,
    val rnc: String,
    val rating: Float = 4.8f,
    val reviewCount: Int = 88,
    val followers: Int = 1420,
    val productsCount: Int = 142,
    val avatarInitials: String = "TC",
    val isVerified: Boolean = true,
    val isVip: Boolean = true,
    val description: String,
    val address: String,
    val bannerImageUrl: String = "",
    val openingHours: String = "Abierto hoy hasta 7:00 PM"
)

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey val id: String,
    val title: String,
    val providerName: String,
    val category: String,
    val province: String,
    val rating: Float = 4.9f,
    val reviewCount: Int = 54,
    val priceStarting: Double,
    val priceOld: Double? = null,
    val imageUrl: String,
    val isRncVerified: Boolean = true,
    val coverage: String,
    val description: String,
    val turnaroundTime: String = "Entrega en 45 Minutos",
    val includes: String = ""
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: String,
    val quantity: Int = 1,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val itemId: String,
    val itemType: String // "product" or "service"
)

@Entity(tableName = "page_blocks")
data class PageBlockEntity(
    @PrimaryKey val id: String,
    val name: String,
    val isActive: Boolean = true,
    val orderIndex: Int = 0,
    val heading: String = "",
    val blockType: String = "Hero Slider"
)
