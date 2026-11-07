package io.lipika.feature.ime.ui.emoji

/**
 * Offline, private word-to-emoji prediction engine.
 * Maps English words, Unicode Bengali words, and Phonetic Banglish to relevant emojis.
 *
 * Runs completely locally without network access, guaranteeing zero latency and 100% privacy.
 */
object EmojiPredictionEngine {

    private data class EmojiRule(
        val keywords: List<String>,
        val emojis: List<String>
    )

    private val rules: List<EmojiRule> = listOf(
        // Love, Hearts, Romance
        EmojiRule(
            keywords = listOf(
                "love", "heart", "crush", "romance", "kiss", "dear", "sweet", "darling", "miss", "hug",
                "ভালোবাসা", "ভালোবাসি", "প্রেম", "হৃদয়", "প্রিয়", "প্রিয়তম", "কলিজা", "মায়া", "চুমু",
                "bhalobasha", "valobasha", "bhalobashi", "valobashi", "prem", "chumu", "ridoy", "kolija", "priyo"
            ),
            emojis = listOf("❤️", "🥰", "😍", "😘", "💕")
        ),

        // Joy, Smile, Happiness, Laugh
        EmojiRule(
            keywords = listOf(
                "happy", "smile", "joy", "laugh", "haha", "lol", "cheerful", "glad", "yay", "fun", "grin",
                "হাসি", "আনন্দ", "খুশি", "হাসাহাসি", "মজা", "ভালো", "সুন্দর", "সুখী", "উল্লাস",
                "hasi", "anondo", "khushi", "moja", "valo", "valoi", "shundor", "ullas"
            ),
            emojis = listOf("😂", "😊", "😁", "😃", "✨")
        ),

        // Fire, Awesome, Lit, Super
        EmojiRule(
            keywords = listOf(
                "fire", "hot", "lit", "burn", "awesome", "cool", "super", "flame", "legend", "swag",
                "আগুন", "সেরা", "দারুণ", "জোশ", "অসাধারণ", "উত্তাপ", "চরম",
                "agun", "seraa", "sera", "darun", "josh", "osadharon", "uttap"
            ),
            emojis = listOf("🔥", "⚡", "💥", "💯", "😎")
        ),

        // Sadness, Tears, Crying, Hurt
        EmojiRule(
            keywords = listOf(
                "sad", "cry", "sob", "tear", "unhappy", "depressed", "hurt", "pain", "broken", "sorrow",
                "দুঃখ", "কান্না", "কষ্ট", "মন খারাপ", "ব্যথা", "অশ্রু", "বেদনা", "হতাশ",
                "dukho", "kanna", "kosto", "mon kharap", "byatha", "bedona"
            ),
            emojis = listOf("😭", "😢", "💔", "🥺", "😔")
        ),

        // Thumbs up, OK, Approval, Agreement
        EmojiRule(
            keywords = listOf(
                "ok", "good", "yes", "nice", "great", "agree", "done", "perfect", "fine", "thumbs",
                "ঠিক", "আচ্ছা", "ভালো", "হ্যাঁ", "সহমত", "হয়েছে", "সঠিক",
                "thik", "accha", "valo", "haa", "shohomot", "hoyeche"
            ),
            emojis = listOf("👍", "👌", "✅", "🤝", "🙌")
        ),

        // Disapproval, No, Bad, Angry
        EmojiRule(
            keywords = listOf(
                "no", "bad", "dislike", "wrong", "stop", "never", "hate", "angry", "mad",
                "না", "খারাপ", "ভুল", "থামুন", "ঘৃণা", "রাগ", "ক্রোধ",
                "na", "kharap", "bhul", "thamun", "ghrina", "rag"
            ),
            emojis = listOf("👎", "❌", "😡", "😠", "🚫")
        ),

        // Celebration, Party, Congrats, Birthday
        EmojiRule(
            keywords = listOf(
                "party", "congrats", "congratulations", "celebrate", "birthday", "cheers", "celebration", "gift",
                "অভিনন্দন", "জন্মদিন", "উৎসব", "শুভকামনা", "উপহার",
                "obhinondon", "jonmodin", "shuvokamona", "utsob", "upohar"
            ),
            emojis = listOf("🎉", "🎂", "🥳", "🎁", "🥂")
        ),

        // Pray, Thanks, Respect, Gratitude
        EmojiRule(
            keywords = listOf(
                "thanks", "thank", "please", "pray", "bless", "welcome", "salam", "namaste", "gratitude",
                "ধন্যবাদ", "দয়া", "দয়া করে", "সালাম", "নমস্কার", "কৃতজ্ঞতা", "দোয়া",
                "dhonnobad", "doya", "salam", "nomoshkar", "dua"
            ),
            emojis = listOf("🙏", "🤲", "✨", "💐", "💖")
        ),

        // Thinking, Wonder, Curious, Confused
        EmojiRule(
            keywords = listOf(
                "think", "thinking", "wonder", "why", "curious", "idea", "hmm", "confused",
                "ভাবছি", "চিন্তা", "কেন", "কিভাবে", "ভাবনা", "উপায়",
                "vabchi", "chinta", "keno", "kivabe"
            ),
            emojis = listOf("🤔", "🧐", "💡", "❓")
        ),

        // Food, Drink, Dining, Tea, Coffee
        EmojiRule(
            keywords = listOf(
                "food", "eat", "hungry", "tea", "coffee", "cake", "burger", "pizza", "biryani", "breakfast", "dinner",
                "খাবার", "চা", "কফি", "খিদে", "মিষ্টি", "বিরিয়ানি", "ভাত", "পানি",
                "khabar", "cha", "coffee", "khete", "bhat", "biriyani", "pani"
            ),
            emojis = listOf("☕", "🍔", "🍕", "🍰", "🍎")
        ),

        // Sleep, Tired, Night
        EmojiRule(
            keywords = listOf(
                "sleep", "tired", "bed", "goodnight", "night", "dream", "yawn", "sleepy",
                "ঘুম", "ঘুমাবো", "ক্লান্ত", "রাত", "শুভরাত্রি", "স্বপ্ন",
                "ghum", "ghumabo", "klanto", "rat", "shuvo ratri", "goodnight"
            ),
            emojis = listOf("😴", "🥱", "💤", "🌙", "🛌")
        ),

        // Money, Cash, Rich, Business
        EmojiRule(
            keywords = listOf(
                "money", "cash", "rich", "wealth", "dollar", "taka", "paid",
                "টাকা", "পয়সা", "ধনী", "দাম", "খরচ", "বেতন",
                "taka", "poysa", "dhoni", "daam", "beton"
            ),
            emojis = listOf("💰", "💵", "🤑", "💳")
        ),

        // Flowers, Garden, Nature
        EmojiRule(
            keywords = listOf(
                "flower", "rose", "garden", "nature", "blossom",
                "ফুল", "গোলাপ", "বাগান", "প্রকৃতি",
                "ful", "golap", "bagan", "prokriti"
            ),
            emojis = listOf("🌸", "🌹", "🌺", "🌻", "🌿")
        ),

        // Travel, Transport, Driving
        EmojiRule(
            keywords = listOf(
                "car", "drive", "travel", "plane", "trip", "bike", "flight", "bus",
                "গাড়ি", "ভ্রমণ", "যাত্রা", "বিমান", "বাস",
                "gari", "vromon", "jatra", "biman", "bus"
            ),
            emojis = listOf("🚗", "✈️", "🚲", "🚌", "🚂")
        ),

        // Sickness, Health, Medicine
        EmojiRule(
            keywords = listOf(
                "sick", "fever", "doctor", "medicine", "hospital", "ill",
                "অসুস্থ", "জ্বর", "ডাক্তার", "ওষুধ", "হাসপাতাল",
                "osustho", "jwor", "doctor", "osudh"
            ),
            emojis = listOf("🤒", "😷", "💊", "🏥")
        ),

        // Victory, Win, Trophy, Game
        EmojiRule(
            keywords = listOf(
                "win", "winner", "victory", "first", "champion", "game", "match",
                "জয়", "বিজয়", "খেলা", "ম্যাচ", "চ্যাম্পিয়ন",
                "joy", "bijoy", "khela", "champion"
            ),
            emojis = listOf("🏆", "🥇", "✌️", "👑")
        ),

        // Pets: Cat, Dog
        EmojiRule(
            keywords = listOf(
                "cat", "kitty", "kitten", "dog", "puppy", "pet",
                "বিড়াল", "কুকুর", "পোষা",
                "biral", "kukur"
            ),
            emojis = listOf("🐱", "🐶", "🐾")
        ),

        // Music, Song, Dance
        EmojiRule(
            keywords = listOf(
                "music", "song", "audio", "dance", "guitar", "sing",
                "গান", "সুর", "নাচ", "সঙ্গীত",
                "gaan", "sur", "naach", "shongit"
            ),
            emojis = listOf("🎵", "🎶", "🎸", "🎤")
        ),

        // Surprised, Shock, Wow
        EmojiRule(
            keywords = listOf(
                "shock", "wow", "omg", "surprised", "scared", "fear",
                "অবাক", "ভয়", "বিস্ময়",
                "obak", "bhoy"
            ),
            emojis = listOf("😲", "😱", "🤯", "😮")
        ),

        // Weather
        EmojiRule(
            keywords = listOf(
                "sun", "rain", "storm", "cloud", "weather",
                "বৃষ্টি", "রোদ", "ঝড়", "মেঘ",
                "brishti", "rod", "jhor", "megh"
            ),
            emojis = listOf("☀️", "🌧️", "⚡", "🌈")
        )
    )

    // Direct lookup map: normalized keyword -> list of emojis
    private val keywordToEmojis = HashMap<String, MutableList<String>>()

    init {
        for (rule in rules) {
            for (kw in rule.keywords) {
                val clean = kw.trim().lowercase()
                val list = keywordToEmojis.getOrPut(clean) { mutableListOf() }
                for (emoji in rule.emojis) {
                    if (!list.contains(emoji)) {
                        list.add(emoji)
                    }
                }
            }
        }
    }

    /**
     * Predicts matching emojis based on the given word (English or Bengali).
     * Supports exact match and prefix match.
     */
    fun predictEmojis(word: String, limit: Int = 2): List<String> {
        val q = word.trim().lowercase()
        if (q.length < 2) return emptyList()

        // 1. Direct exact keyword match
        keywordToEmojis[q]?.let { list ->
            if (list.isNotEmpty()) return list.take(limit)
        }

        // 2. Prefix / Substring matching if word is at least 3 characters
        if (q.length >= 3) {
            val results = mutableListOf<String>()
            for ((keyword, emojis) in keywordToEmojis) {
                if (keyword.startsWith(q) || q.startsWith(keyword)) {
                    for (e in emojis) {
                        if (!results.contains(e)) {
                            results.add(e)
                            if (results.size >= limit) return results
                        }
                    }
                }
            }
            if (results.isNotEmpty()) return results
        }

        return emptyList()
    }
}
