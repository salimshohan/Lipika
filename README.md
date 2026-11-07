# Lipika Keyboard (লিপিকা)

<p align="center">
  <img width="140" height="140" alt="Lipika Logo" src="docs/logo.svg" />
</p>

<p align="center">
  <strong>A fresh, clean, and super-fast open-source Bengali, English & Multilingual keyboard for Android.</strong>
</p>

---

<p align="center">
  <a href="https://github.com/salimshohan/Lipika/releases/tag/v0.3.5">
    <img src="https://img.shields.io/badge/Release-v0.3.5-06B6D4?style=for-the-badge&logo=android&logoColor=white" alt="Version 0.3.5" />
  </a>
  <a href="https://github.com/salimshohan/Lipika/releases/download/v0.3.5/Lipika.v0.3.5.apk">
    <img src="https://img.shields.io/badge/Download-Lipika%20v0.3.5%20APK-10B981?style=for-the-badge&logo=android&logoColor=white" alt="Download APK" />
  </a>
  <a href="https://github.com/salimshohan/Lipika/releases/download/v0.3.5/Lipika.apk">
    <img src="https://img.shields.io/badge/Direct%20APK-Lipika.apk-0284C7?style=for-the-badge&logo=android&logoColor=white" alt="Download Direct APK" />
  </a>
  <a href="https://github.com/salimshohan/Lipika">
    <img src="https://img.shields.io/badge/GitHub-salimshohan%2FLipika-8B5CF6?style=for-the-badge&logo=github&logoColor=white" alt="GitHub" />
  </a>
  <a href="mailto:salimshohan@proton.me">
    <img src="https://img.shields.io/badge/Developer-Salim%20Hosen%20Shohan-38BDF8?style=for-the-badge&logo=protonmail&logoColor=white" alt="Developer" />
  </a>
</p>

---

## 🌟 Overview

**Lipika (লিপিকা)** is a modern, high-performance input method editor (IME) engineered for fast and elegant typing on Android. Built with modern Android architecture using **Jetpack Compose** for a fluid UI, bundled with an ultra-responsive **Rust native core** for spatial touch prediction and instant phonetic transliteration.

---

## ✨ Key Highlights in v0.3.5

- **🎨 Restored v0.3.3 Official Master Logo**:
  - Restored the official v0.3.3 stylized Bengali 'ল' logo on a pure white borderless background across all vector icons, mipmaps, and documentation.
- **✨ HD Emojis & iOS-Style Floating Preview**:
  - Enlarged high-definition emoji picker grid (7 columns in portrait, 4.2 visible rows) with crystal-clear rendering.
  - Prominent iOS-style floating pop-up preview bubble (68sp) with smooth haptics and frosted card backdrop.
- **🔤 Fixed Auto-Capitalization**:
  - Dynamically tracks cursor position and sentence boundaries using Android's native `InputConnection.getCursorCapsMode()`.
  - Automatically shifts to uppercase at the start of fields, after newlines, sentence-terminating punctuation, and backspacing.
- **⚡ Fixed Double-Space Period**:
  - Expanded double-tap window to 800ms with robust preceding space detection.
  - Automatically inserts `". "` for English and `"। "` for Bengali layouts followed by immediate sentence auto-capitalization.
- **🧠 Advanced Word Suggestions, Next-Word Predictions & Personalized Learning**:
  - Instant candidate suggestions prioritizing exact matches and learned words.
  - Smooth next-word bigram and trigram predictions right in the suggestion strip.
  - Personalized user dictionary learns new words and bi-grams dynamically upon candidate selection and typing completion.
- **🎯 Smart Emoji Prediction Engine**:
  - Pristine Unicode emoji predictions mapping hundreds of English, Unicode Bengali, and Banglish keywords (e.g., "love" / "ভালোবাসা" -> ❤️, "happy" / "হাসি" -> 😂, "fire" / "আগুন" -> 🔥, "ok" / "ঠিক" -> 👍, "thanks" / "ধন্যবাদ" -> 🙏).
- **⚙️ Updated Default Preferences**:
  - Auto-enabled out-of-the-box: Word Suggestions, Next Word Prediction, Emoji Prediction, Learned Words, Offensive Words Filter, Auto Capitalization, Double-Space Period.
  - Auto-disabled by default: Show English Raw Input Candidate, Auto Correction, Phonetic Auto Correct.
- **🧹 Streamlined Layouts**:
  - Borno Phonetic removed from layout selection and settings; clean Avro Phonetic, Probhat, National (BSTI), and Easy layouts fully maintained.

---

## ⌨️ Curated Language Layouts

Lipika features 5 carefully chosen layouts:

| Layout | Description |
|---|---|
| **English (QWERTY)** | Standard English keyboard with direct copy/paste shortcuts and number flicking. |
| **বাংলা (প্রভাত)** | Popular fixed traditional Bengali layout for fast typing with smart vowel kar composition. |
| **বাংলা (অভ্র)** | Intuitive phonetic typing (`ami` → `আমি`) powered by the native engine. |
| **Arabic (العربية)** | Standard Arabic layout with full Harakat diacritics. |
| **বাংলা (জাতীয়)** | Official National BSTI layout standard. |

---

## 👤 Author & Maintainer

- **Developer**: **Salim Hosen Shohan**
- **Email**: [salimshohan@proton.me](mailto:salimshohan@proton.me)
- **Institution**: Department of Information Science and Library Management, **University of Rajshahi**
- **GitHub**: [@salimshohan](https://github.com/salimshohan)

---

## 📥 Download

Download the stable pre-built APK directly from GitHub Releases:

👉 **[Download Lipika v0.3.5 APK](https://github.com/salimshohan/Lipika/releases/download/v0.3.5/Lipika.v0.3.5.apk)**

👉 **[Download Latest Lipika APK](https://github.com/salimshohan/Lipika/releases/download/v0.3.5/Lipika.apk)**
