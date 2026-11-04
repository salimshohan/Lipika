# Lipika (লিপিকা)

<p align="center">
  <img width="128" height="128" alt="Lipika Logo" src="https://github.com/user-attachments/assets/346d3099-7f42-462a-931c-959a216ebccc" />
</p>

<p align="center">
  <strong>A fresh, clean, and super-fast open-source Bengali & Multilingual keyboard for Android.</strong>
</p>

---

[![Repo](https://img.shields.io/badge/GitHub-salimshohan%2FLipika-blue?logo=github)](https://github.com/salimshohan/Lipika)
[![Android](https://img.shields.io/badge/Platform-Android%208.0%2B%20(API%2024%2B)-blue)](https://developer.android.com)
[![Rust](https://img.shields.io/badge/Engine-Rust%20NDK-orange)](https://www.rust-lang.org)
[![Kotlin](https://img.shields.io/badge/UI-Jetpack%20Compose-purple)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-GPL%203.0-green.svg)](https://www.gnu.org/licenses/gpl-3.0)

---

## 🌟 Overview

**Lipika (লিপিকা)** is a modern, high-performance input method editor (IME) designed for effortless typing in Bengali, English, and Arabic. Built on modern Android architecture with **Jetpack Compose** for an ultra-clean UI and a high-speed **Rust native core** for spatial touch prediction and instant transliteration.

### Key Highlights
- **⚡ Super Speed & Low Latency**: Optimized touch response and reduced delay for fluid, instant typing.
- **🎨 Fresh & Clean Vibe**: Modern slate-cyan aesthetic, elegant keycaps, refined contrast, and streamlined settings.
- **🔒 Privacy First**: Completely offline by design. No background telemetry or analytics.
- **🇧🇩 Rich Bangla Support**: Full phonetic, Avro, Borno, National (BSTI), and Probhat layouts.

---

## ✨ Features

### Supported Layouts
| Language | Layouts |
|---|---|
| **Bangla** | Borno Phonetic, Avro Phonetic, Borno Easy, National (BSTI), Probhat, Khipro |
| **English** | QWERTY, Math symbols |
| **Arabic** | Arabic (with dedicated Harakat / diacritics row) |

*Custom layouts can be added by placing JSON layout files in `assets/layouts/`.*

### Input & Editing
* **Gesture Typing**: Swipe-to-type support across supported layouts.
* **Smart Next-Word Predictions**: On-device candidate completions and next-word suggestions.
* **Spacebar & Backspace Gestures**: Slide across the spacebar to navigate the cursor; slide left from the backspace key to quickly erase words.
* **Clipboard Manager**: History of copied text with pin/unpin support.
* **Text Editing Panel**: Dedicated directional arrows, text selection handles, copy, cut, paste, undo, and redo.
* **Emoji Picker**: Built-in emoji panel with search and category tabs.
* **Voice Typing**: Direct speech-to-text input via system voice recognizer.
* **One-Handed Mode & Resizing**: Toggleable left/right one-handed modes and adjustable keyboard height.
* **Haptic Feedback**: Keypress vibration support with configurable intensity.
* **Modern Themes**: Clean Dark & Light themes with vibrant accents, AMOLED Black, Tokyo Night, and customizable palettes.

---

## 📁 Project Structure

```
Lipika/
├── app/                  # Application entry point, onboarding, and settings UI
├── core/
│   ├── common/           # Shared models (KeyDef, KeyType, etc.)
│   ├── data/             # Preferences datastore and layout repositories
│   └── theme/            # Theme models, color schemes, and theme parser
├── feature/
│   ├── ime/              # InputMethodService implementation, Compose canvas, and JNI bindings
│   └── themedesigner/    # Theme creator and custom styling studio
├── native/
│   └── kiebord-engine/   # Rust native library (spatial model, trie dictionaries, transliteration)
└── docs/                 # Layout and theme development specifications
```

---

## 🛠️ Building from Source

### Prerequisites
* Android Studio (Ladybug / Meerkat or newer) or Android SDK command-line tools
* JDK 17 or 21
* Android SDK Platform 35 / 36
* Android NDK `26.2.11394342`
* Rust 1.75+ with `cargo-ndk` (only required if compiling native Rust sources)

### Build Commands

1. Clone the repository:
```bash
git clone https://github.com/salimshohan/Lipika.git
cd Lipika
```

2. Build the debug APK:
```bash
./gradlew assembleDebug
```

---

## 📜 Acknowledgments & License

Lipika is licensed under the [GNU General Public License v3.0](LICENSE).

Lipika proudly builds upon the foundational work and research of the open-source community:
- **Kiebord** by [Jayed Ahsan](https://github.com/JayedAhsan/Kiebord) & **Borno Keyboard** transliteration architecture.
- **AOSP LatinIME** for keyboard service foundation.
- **Khipro** for touchscreen m17n rules.
