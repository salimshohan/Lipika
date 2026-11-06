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
                "ভালোবাসা", "ভালোবাসি", "মন", "হৃদয়", "প্রেম", "চুমু", "মায়া", "জান", "প্রিয়", "কলিজা",
                "bhalobasha", "valobasha", "bhalobashi", "valobashi", "prem", "chumu", "ridoy", "kolija", "priyo"
            ),
            emojis = listOf("❤️", "💖", "😍", "🥰", "😘")
        ),

        // Joy, Smile, Happiness, Laugh
        EmojiRule(
            keywords = listOf(
                "happy", "smile", "joy", "laugh", "haha", "lol", "cheerful", "glad", "yay", "fun", "grin",
                "হাসি", "আনন্দ", "খুশি", "হাসাহাসি", "মজা", "ভালো", "সুন্দর", "সুখী", "উল্লাস",
                "hasi", "anondo", "khushi", "moja", "valo", "valoi", "shundor", "ullas"
            ),
            emojis = listOf("😊", "😄", "😁", "😂", "🤣")
        ),

        // Fire, Awesome, Lit, Super
        EmojiRule(
            keywords = listOf(
                "fire", "hot", "lit", "burn", "awesome", "cool", "super", "flame", "legend", "swag",
                "আগুন", "সেরা", "দারুণ", "জোশ", "অসাধারণ", "উত্তাপ",
                "agun", "seraa", "sera", "darun", "josh", "osadharon", "uttap"
            ),
            emojis = listOf("🔥", "✨", "⚡", "💥", "😎")
        ),

        // Sadness, Tears, Crying, Hurt
        EmojiRule(
            keywords = listOf(
                "sad", "cry", "sob", "tear", "unhappy", "depressed", "hurt", "pain", "broken", "sorrow",
                "দুঃখ", "কান্না", "কষ্ট", "মন খারাপ", "ব্যথা", "অশ্রু", "বেদনা", "বিষাদ",
                "dukho", "kanna", "kosto", "mon kharap", "byatha", "bedona"
            ),
            emojis = listOf("😢", "😭", "🥺", "💔", "😞")
        ),

        // Thumbs up, OK, Approval, Agreement
        EmojiRule(
            keywords = listOf(
                "ok", "good", "yes", "nice", "great", "agree", "done", "perfect", "fine", "thumbs",
                "ঠিক", "আচ্ছা", "ভালো", "হ্যাঁ", "উত্তম", "হয়েছে", "সহমত",
                "thik", "accha", "valo", "haa", "shohomot", "hoyeche"
            ),
            emojis = listOf("👍", "👌", "✅", "💯", "🙌")
        ),

        // Disapproval, No, Bad
        EmojiRule(
            keywords = listOf(
                "no", "bad", "dislike", "wrong", "stop", "never", "hate",
                "না", "খারাপ", "ভুল", "থামুন", "ঘৃণা",
                "na", "kharap", "bhul", "thamun", "ghrina"
            ),
            emojis = listOf("👎", "❌", "🚫", "🛑")
        ),

        // Celebration, Party, Congrats, Birthday
        EmojiRule(
            keywords = listOf(
                "party", "congrats", "congratulations", "celebrate", "birthday", "cheers", "celebration", "gift",
                "অভিনন্দন", "জন্মদিন", "উৎসব", "শুভকামনা", "উপহার",
                "obhinondon", "jonmodin", "shuvokamona", "utsob", "upohar"
            ),
            emojis = listOf("🎉", "🥳", "🎊", "🎂", "🎁")
        ),

        // Pray, Thanks, Respect, Gratitude
        EmojiRule(
            keywords = listOf(
                "thanks", "thank", "please", "pray", "bless", "welcome", "salam", "namaste", "gratitude",
                "ধন্যবাদ", "দোয়া", "দয়া", "সালাম", "নমস্কার", "প্রণাম", "শুকরিয়া",
                "dhonnobad", "doya", "salam", "nomoskar", "shukriya"
            ),
            emojis = listOf("🙏", "🤲", "✨")
        ),

        // Anger, Fury, Frustration
        EmojiRule(
            keywords = listOf(
                "angry", "mad", "rage", "furious", "hate", "irritated", "annoyed",
                "রাগ", "রাগান্বিত", "বিরক্ত", "ক্ষুব্ধ",
                "rag", "raganwito", "birokto", "khubdho", "gussa"
            ),
            emojis = listOf("😠", "😡", "🤬", "😤")
        ),

        // Food, Hunger, Eating
        EmojiRule(
            keywords = listOf(
                "food", "eat", "hungry", "snack", "dinner", "lunch", "breakfast", "burger", "pizza", "biryani",
                "খাবার", "খাবো", "খিদে", "ক্ষুধা", "বিরিয়ানি", "ভাত", "মাংস",
                "khabar", "khabo", "khide", "khudha", "biryani", "bhat"
            ),
            emojis = listOf("🍔", "🍕", "🍗", "🍲", "😋")
        ),

        // Tea, Coffee
        EmojiRule(
            keywords = listOf(
                "tea", "coffee", "cup", "cafe", "drink",
                "চা", "কফি", "পানীয়",
                "cha", "coffee"
            ),
            emojis = listOf("☕", "🍵", "🧋")
        ),

        // Bangladesh, Country, Flag
        EmojiRule(
            keywords = listOf(
                "bangladesh", "bd", "bangla", "flag",
                "বাংলাদেশ", "পতাকা", "বাংলা",
                "bangladesh", "potaka"
            ),
            emojis = listOf("🇧🇩", "❤️")
        ),

        // Sleep, Night, Tired
        EmojiRule(
            keywords = listOf(
                "sleep", "tired", "bed", "night", "goodnight", "yawn", "sleepy", "rest",
                "ঘুম", "ক্লান্ত", "শুভরাত্রি", "রাত", "বিশ্রাম",
                "ghum", "klanto", "shuvoratri", "shuvo ratri", "raat"
            ),
            emojis = listOf("😴", "🥱", "🌙", "💤")
        ),

        // Thinking, Ideas, Smart
        EmojiRule(
            keywords = listOf(
                "think", "thinking", "idea", "hmm", "brain", "smart", "wonder",
                "ভাবছি", "চিন্তা", "বুদ্ধি", "উপায়", "জ্ঞান",
                "vabchi", "chinta", "buddhi", "upay"
            ),
            emojis = listOf("🤔", "💡", "🧠", "🧐")
        ),

        // Sun, Weather, Morning
        EmojiRule(
            keywords = listOf(
                "morning", "sun", "sunny", "rain", "rainy", "weather", "cloud",
                "সকাল", "সুপ্রভাত", "সূর্য", "বৃষ্টি", "মেঘ",
                "sokal", "suprobhat", "surjo", "bristi", "megh"
            ),
            emojis = listOf("☀️", "🌧️", "🌤️", "🌈")
        ),

        // Money, Cash, Rich
        EmojiRule(
            keywords = listOf(
                "money", "cash", "rich", "dollar", "taka", "price", "wealth",
                "টাকা", "পয়সা", "ধনী", "দাম", "সম্পদ",
                "taka", "poysa", "dhoni", "daam"
            ),
            emojis = listOf("💰", "💵", "🤑", "💸")
        ),

        // Flowers, Garden, Nature
        EmojiRule(
            keywords = listOf(
                "flower", "rose", "garden", "nature", "blossom",
                "ফুল", "গোলাপ", "বাগান", "প্রকৃতি",
                "ful", "golap", "bagan"
            ),
            emojis = listOf("🌹", "🌸", "💐", "🌺")
        ),

        // Travel, Transport, Driving
        EmojiRule(
            keywords = listOf(
                "car", "drive", "travel", "plane", "trip", "bike", "flight", "bus",
                "গাড়ি", "ভ্রমণ", "যাত্রা", "বিমান", "বাস",
                "gari", "vromon", "jatra", "biman", "bus"
            ),
            emojis = listOf("🚗", "✈️", "🏍️", "🚌")
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
                "জয়", "বিজয়", "খেলা", "ম্যাচ", "চ্যাম্পিয়ন",
                "joy", "bijoy", "khela", "champion"
            ),
            emojis = listOf("🏆", "🥇", "✌️", "🎯")
        ),

        // Pets: Cat, Dog
        EmojiRule(
            keywords = listOf(
                "cat", "kitty", "kitten", "dog", "puppy", "pet",
                "বিড়াল", "কুকুর", "পোষা",
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
            emojis = listOf("🎵", "🎶", "🎧", "💃")
        ),

        // Surprised, Shock, Wow
        EmojiRule(
            keywords = listOf(
                "shock", "wow", "omg", "surprised", "scared", "fear",
                "অবাক", "ভয়", "বিস্ময়",
                "obak", "bhoy"
            ),
            emojis = listOf("😱", "😲", "🤯", "😳")
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
