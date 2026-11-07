# Lipika Keyboard (লিপিকা)

<p align="center">
  <img width="140" height="140" alt="Lipika Logo" src="docs/logo.svg" />
</p>

<p align="center">
  <strong>A fresh, clean, and super-fast open-source Bengali, English & Multilingual keyboard for Android.</strong>
</p>

---

<p align="center">
  <a href="https://github.com/salimshohan/Lipika/releases/tag/v0.3.7">
    <img src="https://img.shields.io/badge/Release-v0.3.7-06B6D4?style=for-the-badge&logo=android&logoColor=white" alt="Version 0.3.7" />
  </a>
  <a href="https://github.com/salimshohan/Lipika/releases/download/v0.3.7/Lipika.v0.3.7.apk">
    <img src="https://img.shields.io/badge/Download-Lipika%20v0.3.7%20APK-10B981?style=for-the-badge&logo=android&logoColor=white" alt="Download APK" />
  </a>
  <a href="https://github.com/salimshohan/Lipika/releases/download/v0.3.7/Lipika.apk">
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

## ✨ Key Highlights in v0.3.7

- **🎯 Strict Path Subtraction (`Path.Op.DIFFERENCE`) for Bengali Combining Marks**:
  - Standalone Bengali dependent vowel signs (Kar/Matra: া, ি, ী, ু, ূ, ৃ, ে, ৈ, ো, ৌ), Hasanta (্), and diacritics (ঁ, ং, ঃ) are now rendered via mathematical vector subtraction:
    - Extracts `\u25CC` + mark and `\u25CC` outlines with `paint.getTextPath`.
    - Subtracts the dotted circle via `combinedPath.op(circlePath, Path.Op.DIFFERENCE)` so only pure matra geometry remains.
    - Centers horizontally at `centerX` (`-bounds.centerX() + centerX`) and aligns vertically with `baselineY` with `Paint.Style.FILL`.
  - Applied to primary center key labels, top-right flick-up hints, long-press MoreKeys popups, quick-symbol drag overlays, and keyboard layout previews.
- **⚡ Lipika Smart Engine Multi-Tap Gestures in Settings**:
  - **Double-Tap**: Instantly toggles between **Dark Mode** and **Light Mode** across the entire app with smooth haptics and toast confirmation.
  - **Triple-Tap**: Instantly switches **App Language** between **বাংলা (Bengali)** and **English** with immediate UI updates.
- **🔤 Zero Input Disturbance**:
  - Character commitment to `InputConnection` is 100% preserved.

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

👉 **[Download Lipika v0.3.7 APK](https://github.com/salimshohan/Lipika/releases/download/v0.3.7/Lipika.v0.3.7.apk)**

👉 **[Download Latest Lipika APK](https://github.com/salimshohan/Lipika/releases/download/v0.3.7/Lipika.apk)**
