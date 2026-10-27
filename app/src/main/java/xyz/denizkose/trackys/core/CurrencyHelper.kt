package xyz.denizkose.trackys.core

data class CurrencyInfo(
    val code: String,
    val symbol: String,
    val name: String
) {
    val displayLabel: String get() = "$code ($symbol)"
}

object CurrencyHelper {
    val POPULAR_CURRENCIES = listOf(
        CurrencyInfo("USD", "$", "US Dollar"),
        CurrencyInfo("EUR", "€", "Euro"),
        CurrencyInfo("UAH", "₴", "Ukrainian Hryvnia"),
        CurrencyInfo("TRY", "₺", "Turkish Lira"),
        CurrencyInfo("PLN", "zł", "Polish Zloty"),
        CurrencyInfo("GBP", "£", "British Pound"),
        CurrencyInfo("CAD", "CA$", "Canadian Dollar"),
        CurrencyInfo("AUD", "A$", "Australian Dollar"),
        CurrencyInfo("JPY", "¥", "Japanese Yen"),
        CurrencyInfo("CHF", "Fr", "Swiss Franc"),
        CurrencyInfo("INR", "₹", "Indian Rupee"),
        CurrencyInfo("CNY", "¥", "Chinese Yuan"),
        CurrencyInfo("BRL", "R$", "Brazilian Real"),
        CurrencyInfo("SEK", "kr", "Swedish Krona"),
        CurrencyInfo("NOK", "kr", "Norwegian Krone"),
        CurrencyInfo("DKK", "kr", "Danish Krone"),
        CurrencyInfo("CZK", "Kč", "Czech Koruna"),
        CurrencyInfo("HUF", "Ft", "Hungarian Forint"),
        CurrencyInfo("RON", "lei", "Romanian Leu"),
        CurrencyInfo("BGN", "лв", "Bulgarian Lev"),
        CurrencyInfo("MXN", "$", "Mexican Peso"),
        CurrencyInfo("SGD", "S$", "Singapore Dollar"),
        CurrencyInfo("NZD", "NZ$", "New Zealand Dollar"),
        CurrencyInfo("KRW", "₩", "South Korean Won"),
        CurrencyInfo("AED", "د.إ", "UAE Dirham")
    )

    fun getSymbol(code: String): String {
        return POPULAR_CURRENCIES.find { it.code.equals(code, ignoreCase = true) }?.symbol ?: code
    }

    fun getDisplayLabel(code: String): String {
        val info = POPULAR_CURRENCIES.find { it.code.equals(code, ignoreCase = true) }
        return if (info != null) "${info.code} (${info.symbol})" else code
    }
}
