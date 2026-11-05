package io.kiebord.feature.ime.filter

/**
 * Offline, zero-latency offensive words filter for text correction and word suggestions.
 * Filters out profane, abusive, and sexually explicit terms from candidates.
 */
object OffensiveWordsFilter {

    private val offensiveWords: Set<String> = hashSetOf(
        // English profane / offensive words
        "fuck", "fucker", "fucking", "shit", "bitch", "bastard", "asshole", "dick", "pussy",
        "cunt", "nigger", "nigga", "whore", "slut", "cock", "motherfucker", "faggot", "retard",
        "dumbass", "jackass", "dipshit", "bullshit", "prick", "wanker", "twat",

        // Bengali abusive / profane words (Unicode Bengali)
        "খানকি", "খানকির", "খানকিরপোলা", "মাদারচোদ", "মাগীর", "মাগী", "চোদা", "চোদনা", "চোদানোর",
        "বাল", "বালে", "বালপাকনা", "লুচ্চা", "কুত্তারবাচ্চা", "কুত্তা", "শুয়োরেরবাচ্চা",
        "হারামজাদা", "হারামি", "বেশ্যা", "বেশ্যারপোলা", "চুতিয়া", "ভোদা", "বাঁড়া", "লেউড়া",

        // Phonetic Banglish abusive / profane words
        "khanki", "khankir", "khankirpola", "madarchod", "magi", "magir", "choda", "chodna",
        "baal", "bale", "baalpanna", "kuttarbacha", "kutta", "shuyor", "shuyorerbacha",
        "haramzada", "harami", "beshya", "chutiya", "bhoda", "bara", "leura"
    )

    /**
     * Checks if a word is potentially offensive.
     */
    fun isOffensive(word: String): Boolean {
        if (word.isBlank()) return false
        val clean = word.trim().lowercase()
        if (offensiveWords.contains(clean)) return true

        // Check if any offensive root exists as whole token or sub-token
        val stripped = clean.filter { it.isLetter() }
        if (offensiveWords.contains(stripped)) return true

        return false
    }
}
