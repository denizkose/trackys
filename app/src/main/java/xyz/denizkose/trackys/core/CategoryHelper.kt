package xyz.denizkose.trackys.core

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import xyz.denizkose.trackys.R
import java.util.Locale
import kotlin.math.abs

object CategoryHelper {

    val PRESET_PALETTE = listOf(
        Color(0xFF7E57C2), // Purple
        Color(0xFF2EC4B6), // Teal
        Color(0xFFFF5A5F), // Coral
        Color(0xFFFF9F1C), // Amber
        Color(0xFF3A86FF), // Blue
        Color(0xFFE91E63), // Pink
        Color(0xFF4CAF50), // Green
        Color(0xFF00B894), // Mint
        Color(0xFF673AB7), // Deep Purple
        Color(0xFFFF5722), // Deep Orange
        Color(0xFF00CEC9), // Cyan
        Color(0xFFE84393), // Magenta
        Color(0xFF6C757D)  // Slate
    )

    private val DEFAULT_CATEGORY_COLORS = mapOf(
        "entertainment" to Color(0xFF7E57C2),
        "work" to Color(0xFF2EC4B6),
        "utilities" to Color(0xFFFF5A5F),
        "education" to Color(0xFF3A86FF),
        "health" to Color(0xFF4CAF50),
        "other" to Color(0xFFFF9F1C)
    )

    fun getLocalizedName(category: String, context: Context): String {
        return when (category.trim().lowercase()) {
            "entertainment" -> context.getString(R.string.category_entertainment)
            "work" -> context.getString(R.string.category_work)
            "utilities" -> context.getString(R.string.category_utilities)
            "education" -> context.getString(R.string.category_education)
            "health" -> context.getString(R.string.category_health)
            "other" -> context.getString(R.string.category_other)
            else -> category
        }
    }

    fun getCategoryColor(category: String, customColorsMap: Map<String, String> = emptyMap()): Color {
        val key = category.trim().lowercase()
        val customHex = customColorsMap[key] ?: customColorsMap[category]
        if (customHex != null) {
            val parsed = parseColorHex(customHex)
            if (parsed != null) return parsed
        }

        val defaultColor = DEFAULT_CATEGORY_COLORS[key]
        if (defaultColor != null) return defaultColor

        val hue = (abs(category.hashCode()) % 360).toFloat()
        return Color.hsl(hue = hue, saturation = 0.70f, lightness = 0.55f)
    }

    fun colorToHex(color: Color): String {
        return String.format(Locale.US, "#%08X", color.toArgb())
    }

    fun parseColorHex(hex: String): Color? {
        return try {
            val cleanHex = hex.removePrefix("#")
            val colorInt = cleanHex.toLong(16).toInt()
            if (cleanHex.length == 6) {
                Color(colorInt or 0xFF000000.toInt())
            } else {
                Color(colorInt)
            }
        } catch (e: Exception) {
            null
        }
    }
}
