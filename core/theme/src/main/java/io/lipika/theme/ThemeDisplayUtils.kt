package io.lipika.theme

import android.content.Context

/**
 * Utility to resolve user-facing display names for all Lipika and Borno themes.
 * Prevents showing raw IDs like "borno_12" or "borno 12", ensuring names like
 * "Solar Eclipse", "Nebula Core", "Frosted Glass Effect", etc., are always shown.
 */
object ThemeDisplayUtils {

    private val BORNO_THEME_NAMES = mapOf(
        1 to "Crystal Daybreak",
        2 to "Rose Quartz Mist",
        3 to "Morning Dewdrop",
        4 to "Sunlit Halo",
        5 to "Aurora Glass",
        6 to "Oceanic Abyss",
        7 to "Crimson Dusk",
        8 to "Cyber Synthwave",
        9 to "Emerald Lagoon",
        10 to "Sunset Prism",
        11 to "Nebula Core",
        12 to "Solar Eclipse",
        13 to "Pure Chalk",
        14 to "Monochrome Platinum",
        15 to "Cupertino Slate",
        16 to "Astral Mirage",
        17 to "Blue Hydrangea",
        18 to "Celestial Sky",
        19 to "Eucalyptus Mist",
        20 to "Pistachio Gelato",
        21 to "Lilac Haze",
        22 to "Cotton Candy Sunset",
        23 to "Sakura Blossom",
        24 to "Raspberry Parfait",
        25 to "Coral Sorbet",
        26 to "Bubblegum Pop",
        27 to "Cashmere Rose",
        28 to "Toasted Almond",
        29 to "Cobalt Depth",
        30 to "Pacific Midnight",
        31 to "Oceanic Teal",
        32 to "Imperial Iris",
        33 to "Mystic Orchid",
        34 to "Crimson Forge",
        35 to "Roasted Espresso",
        36 to "Slate Mirage",
        37 to "Twilight Ember",
        38 to "Deep Charcoal",
        39 to "Shadow Obsidian",
        40 to "Midnight Carbon",
        41 to "Abyssal Void",
        42 to "Cyber AMOLED"
    )

    private val BUILTIN_THEME_NAMES = mapOf(
        "ocean_teal" to "Ocean Teal",
        "sunset_coral" to "Sunset Coral",
        "minimal_grey" to "Minimal Grey",
        "soft_lavender" to "Soft lavender",
        "frosted_glass" to "Frosted Glass Effect",
        "lipika_gradient" to "Luminous Aurora",
        "default_light" to "Default Light",
        "default_dark" to "Default Dark",
        "pure_black" to "Pure Black",
        "amoled_black" to "AMOLED Black",
        "tokyo_night" to "Tokyo Night",
        "nord" to "Nord Frost"
    )

    /**
     * Resolves the user-facing title of any theme.
     */
    fun resolveDisplayName(themeId: String, context: Context? = null): String {
        BUILTIN_THEME_NAMES[themeId]?.let { return it }

        // Borno numeric ID (e.g. "borno_12", "12", "borno_11", "11")
        val numId = when {
            themeId.startsWith("borno_") -> themeId.removePrefix("borno_").toIntOrNull()
            themeId.all { it.isDigit() } -> themeId.toIntOrNull()
            else -> null
        }
        if (numId != null) {
            BORNO_THEME_NAMES[numId]?.let { return it }
        }

        // Query repository for custom or imported themes if context available
        if (context != null) {
            try {
                val opt = ThemeRepository.getInstance(context).getThemeOption(themeId)
                if (opt.name.isNotBlank() &&
                    !opt.name.startsWith("borno_", ignoreCase = true) &&
                    !opt.name.matches(Regex("borno\\s*\\d+", RegexOption.IGNORE_CASE))
                ) {
                    return opt.name
                }
            } catch (_: Exception) {}
        }

        // Clean capitalized fallback
        return themeId.replace("_", " ").split(" ")
            .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
    }
}
