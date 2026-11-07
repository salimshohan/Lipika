# Lipika Keyboard (লিপিকা)

<p align="center">
  <img width="140" height="140" alt="Lipika Logo" src="docs/logo.svg" />
</p>

<p align="center">
  <strong>A fresh, clean, and super-fast open-source Bengali, English & Multilingual keyboard for Android.</strong>
</p>

---

<p align="center">
  <a href="https://github.com/salimshohan/Lipika/releases/tag/v0.2.9">
    <img src="https://img.shields.io/badge/Release-v0.2.9-06B6D4?style=for-the-badge&logo=android&logoColor=white" alt="Version 0.2.9" />
  </a>
  <a href="https://github.com/salimshohan/Lipika/releases/download/v0.2.9/Lipika%20v0.2.9.apk">
    <img src="https://img.shields.io/badge/Download-Lipika%20v0.2.9%20APK-10B981?style=for-the-badge&logo=android&logoColor=white" alt="Download APK" />
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

## ✨ Key Highlights

- **⌨️ Perfected Keyboard Layouts & Hints**:
  - Dedicated English Number Row (`1`–`0`) displaying Bengali numerals (`১`–`০`) as secondary hints on top-right corners; normal tap types English digit, long-press inserts corresponding Bengali numeral.
  - Dedicated Bengali Number Row (`১`–`০`) with English digit secondary hints (`1`–`0`).
  - Refined symbol hints on QWERTY alphabet keys (`q`→`৳`, `w`→`%`, `e`→`_`, `r`→`;`, `t`→`<`, `y`→`>`, `u`→`+`, `i`→`=`, `o`→`[`, `p`→`]`, `a`→`@`, `s`→`#`, `d`→`&`, `f`→`*`, `g`→`-`, `h`→`!`, `j`→`?`, `k`→`(`, `l`→`)`, `z`→`"`, `b`→`'`, `n`→`:`, `m`→`/`).
  - Streamlined bottom row order: `?123 / ?১২৩` ➔ `Emoji` ➔ `Slash (/)` ➔ `Spacebar` ➔ `Full Stop (.) / Dari (।)` ➔ `Go / Enter`.
- **🔊 Dedicated Sound & Vibration Settings**:
  - Independent sound and vibration management section in Settings.
  - Fixes sound and vibration feedback using `VibrationAttributes.USAGE_TOUCH` and `AudioAttributes.USAGE_ASSISTANCE_SONIFICATION` for 100% reliable feedback across all Android OEMs.
  - Custom sliders for sound volume (0–100%), vibration type (Haptic vs Vibration), intensity (0–100%), and duration (5–100ms).
- **🔤 Dual Font System & 10 New Fonts**:
  - Select English and Bengali fonts together with dedicated interactive previews.
  - Added 5 new Bengali fonts: **SolaimanLipi**, **Siyam Rupali**, **Noto Sans Bengali**, **Hind Siliguri**, **AponaLohit** (alongside Kalpurush).
  - Added 5 new English fonts: **Roboto**, **Inter**, **Poppins**, **Outfit**, **Montserrat** (alongside Tw Cent MT, Segoe UI, Georgia, Consolas, and others).
  - Quick **Fonts** shortcut added directly to the keyboard toolbar and drawer.
- **✨ Bengali Unicode & Provat Engine Refinement**:
  - Permanently eliminated dotted circle (`◌`) artifacts at the Unicode level with smart vowel kar merging, independent vowel resolution, and duplicate hasanta prevention.
- **⚡ Keyboard Performance & Responsiveness**:
  - Fallback tap handling to eliminate dropped keypresses during rapid multi-touch typing.
  - Spacebar swipe threshold reduced to 28dp for instant language switching.
- **🎨 Appearance & Theme Polish**:
  - Streamlined Appearance screen without unnecessary live preview boxes.
  - Added "System Default" to automated theme schedule.
  - Full multiline description wrapping across all settings screens.

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

👉 **[Download Lipika v0.2.9 APK](https://github.com/salimshohan/Lipika/releases/download/v0.2.9/Lipika%20v0.2.9.apk)**

👉 **[Download Latest Lipika APK](https://github.com/salimshohan/Lipika/releases/latest/download/Lipika%20v0.2.9.apk)**
