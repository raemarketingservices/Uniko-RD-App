package com.example.data

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object Remoto {
    const val BASE = "https://uniko-rd.com"
    private const val API_KEY = "sb_publishable_qDTaqHyWWdy92o7G6InGDJ_WEugr_zv"

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .build()

    private fun txt(o: JSONObject, k: String): String {
        if (!o.has(k) || o.isNull(k)) return ""
        val v = o.opt(k)?.toString() ?: ""
        return if (v == "null") "" else v
    }

    private fun num(o: JSONObject, k: String, def: Double = 0.0): Double =
        if (o.has(k) && !o.isNull(k)) o.optDouble(k, def) else def

    private fun ent(o: JSONObject, k: String): Int = num(o, k, 0.0).toInt()

    private fun bool(o: JSONObject, k: String): Boolean = o.optBoolean(k, false)

    private fun nullableNum(o: JSONObject, k: String): Double? =
        if (o.has(k) && !o.isNull(k)) o.optDouble(k) else null

    private fun img(u: String): String = when {
        u.isBlank() -> ""
        u.startsWith("http") -> u
        u.startsWith("/") -> BASE + u
        else -> "$BASE/storage/v1/object/public/marketplace/$u"
    }

    private fun get(path: String): JSONArray {
        val req = Request.Builder()
            .url(BASE + path)
            .header("apikey", API_KEY)
            .header("Accept", "application/json")
            .get()
            .build()
        client.newCall(req).execute().use { resp ->
            val body = resp.body?.string() ?: ""
            if (!resp.isSuccessful) throw IllegalStateException("HTTP ${resp.code}")
            return JSONArray(body)
        }
    }

    private fun descuento(precio: Double, anterior: Double?): Int =
        if (anterior != null && anterior > precio && anterior > 0)
            (((anterior - precio) / anterior) * 100).toInt()
        else 0

    private fun iniciales(nombre: String): String =
        nombre.trim().split(Regex("\\s+"))
            .filter { it.isNotEmpty() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
            .ifEmpty { "UN" }

    fun categorias(): Map<String, String> {
        val arr = get("/rest/v1/categories?select=id,name&order=position")
        return (0 until arr.length()).associate { i ->
            val o = arr.getJSONObject(i)
            txt(o, "id") to txt(o, "name")
        }.filterKeys { it.isNotEmpty() }
    }

    fun tiendas(): List<StoreEntity> {
        val arr = get("/rest/v1/stores?select=*&order=featured.desc,rating.desc")
        return (0 until arr.length()).map { i ->
            val o = arr.getJSONObject(i)
            val nombre = txt(o, "name")
            StoreEntity(
                id = txt(o, "id"),
                name = nombre,
                ownerName = txt(o, "owner_name"),
                category = txt(o, "category"),
                province = txt(o, "location"),
                rnc = txt(o, "rnc"),
                rating = num(o, "rating").toFloat(),
                reviewCount = ent(o, "reviews"),
                followers = ent(o, "followers"),
                productsCount = ent(o, "products_count"),
                avatarInitials = iniciales(nombre),
                isVerified = bool(o, "verified"),
                isVip = bool(o, "featured"),
                description = txt(o, "description"),
                address = txt(o, "location"),
                bannerImageUrl = img(txt(o, "cover")),
                openingHours = if (txt(o, "location").isEmpty()) "República Dominicana" else "Abierto hoy"
            )
        }.filter { it.id.isNotEmpty() }
    }

    fun productos(categorias: Map<String, String>): List<ProductEntity> {
        val arr = get("/rest/v1/products?select=*,stores(name)&order=created_at.desc")
        return (0 until arr.length()).map { i ->
            val o = arr.getJSONObject(i)
            val precio = num(o, "price")
            val anterior = nullableNum(o, "compare_at_price")
            val catId = txt(o, "category")
            ProductEntity(
                id = txt(o, "id"),
                title = txt(o, "title"),
                storeId = txt(o, "store_id"),
                storeName = o.optJSONObject("stores")?.let { txt(it, "name") } ?: "",
                price = precio,
                originalPrice = anterior,
                rating = num(o, "rating").toFloat(),
                reviewCount = ent(o, "reviews"),
                imageUrl = img(txt(o, "image")),
                category = categorias[catId] ?: catId.ifEmpty { "Otros" },
                province = txt(o, "location"),
                isVerified = bool(o, "verified"),
                isFeatured = bool(o, "featured"),
                isBestSeller = bool(o, "best_seller"),
                isNew = bool(o, "is_new"),
                hasShipping = bool(o, "shipping"),
                sku = txt(o, "sku"),
                description = txt(o, "description"),
                inStock = true,
                discountPercent = descuento(precio, anterior)
            )
        }.filter { it.id.isNotEmpty() }
    }

    fun servicios(categorias: Map<String, String>): List<ServiceEntity> {
        val arr = get("/rest/v1/services?select=*&order=created_at.desc")
        return (0 until arr.length()).map { i ->
            val o = arr.getJSONObject(i)
            val catId = txt(o, "category")
            ServiceEntity(
                id = txt(o, "id"),
                title = txt(o, "title"),
                providerName = txt(o, "provider"),
                category = categorias[catId] ?: catId.ifEmpty { "Otros" },
                province = txt(o, "location"),
                rating = num(o, "rating").toFloat(),
                reviewCount = ent(o, "reviews"),
                priceStarting = num(o, "price_from"),
                priceOld = null,
                imageUrl = img(txt(o, "image")),
                isRncVerified = bool(o, "verified"),
                coverage = txt(o, "coverage"),
                description = "",
                turnaroundTime = if (txt(o, "coverage").isEmpty()) "Cobertura nacional" else "Servicio verificado",
                includes = ""
            )
        }.filter { it.id.isNotEmpty() }
    }
}
