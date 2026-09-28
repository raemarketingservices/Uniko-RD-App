package com.example.data

import android.content.Context
import org.json.JSONObject

data class UsuarioSesion(
    val id: String,
    val email: String,
    val nombre: String,
    val tipo: String,
    val accessToken: String,
    val refreshToken: String,
    val expiraEn: Long
)

object SesionStore {
    private const val PREFS = "uniko_sesion"

    fun guardar(ctx: Context, u: UsuarioSesion) {
        val o = JSONObject().apply {
            put("id", u.id)
            put("email", u.email)
            put("nombre", u.nombre)
            put("tipo", u.tipo)
            put("accessToken", u.accessToken)
            put("refreshToken", u.refreshToken)
            put("expiraEn", u.expiraEn)
        }
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString("sesion", o.toString())
            .apply()
    }

    fun cargar(ctx: Context): UsuarioSesion? {
        val raw = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString("sesion", null) ?: return null
        return try {
            val o = JSONObject(raw)
            val u = UsuarioSesion(
                id = o.optString("id"),
                email = o.optString("email"),
                nombre = o.optString("nombre"),
                tipo = o.optString("tipo", "user"),
                accessToken = o.optString("accessToken"),
                refreshToken = o.optString("refreshToken"),
                expiraEn = o.optLong("expiraEn", 0L)
            )
            if (u.id.isEmpty() || u.accessToken.isEmpty()) null else u
        } catch (e: Exception) {
            null
        }
    }

    fun borrar(ctx: Context) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }
}
