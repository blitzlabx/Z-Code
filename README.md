# Z-Code

**Offline • Private • Compact**

**Created by Blitz**  
GitHub: [blitzlabx](https://github.com/blitzlabx) · Telegram: [@blitzlabx](https://t.me/blitzlabx) / [t.me/blitzmax](https://t.me/blitzmax)

Z-Code is a compact offline-first communication encoding system for Android 7+.  
Turn any message into a short, obscure string and decode it anytime, anywhere — completely offline.

> Your Message → Z-Code → Their Mind

## Features

- **Z-Code Generator** – English (any Unicode) → Z-Code  
- **Z-Code Translator** – Z-Code → original text  
- Mandatory configuration: Code Type, Hash, Z Language, Mode  
- Optional password (real AES-256-GCM encryption)  
- Multiple modes, code types and Z languages  
- Fully functional offline encoding / decoding with round-trip verification  
- Local history (Room), saved configurations, import/export, QR support  
- Proper validation, error handling  
- Passwords never stored in plaintext  
- Clear separation of encoding, hashing and real encryption  
- Accurate preservation of Unicode, punctuation and multiline messages  
- Dark premium blue/glow UI matching the concept design  
- Splash, Onboarding, Home, Generator, Translator, History, Languages, Modes, Settings, Security, Import/Export, QR, Documentation, About  
- Accessibility, theme support (light / dark / system), state restoration  

## Architecture

Clean modular Kotlin architecture:

- `core/` – pure encoding/decoding engine (no Android dependencies)  
- `data/` – Room + DataStore persistence  
- `ui/` – Jetpack Compose screens + ViewModel  
- Local-only persistence, no network dependency for core features  

## Encoding Pipeline

1. UTF-8 encode input (preserves all Unicode)  
2. Optional Deflate compression (Compact / Ultra Compact modes)  
3. Optional AES-256-GCM encryption when password is supplied or Secure mode is active  
   - Key derived with PBKDF2-HMAC-SHA256 (50k–150k iterations)  
   - Random salt + IV, never store the password  
4. Integrity hash (SHA-256 / SHA-512 truncated according to mode)  
5. Self-describing binary frame: `MAGIC | VERSION | FLAGS | META | [SALT][IV] | PAYLOAD | HASH`  
6. Base-N encoding using the selected Z-Language alphabet  

Decoding reverses the steps with magic/header validation, hash verification and password-gated decryption.

### Supported Options

| Category     | Values |
|--------------|--------|
| Code Type    | Compact, Standard, Extended |
| Hash         | SHA-256, SHA-512, None |
| Z Language   | Z-Alpha, Z-Numeric, Z-Symbolic, Z-Alphanumeric, Z-Custom |
| Mode         | Standard, Compact, Secure, Ultra Compact, Custom |

## Security Model

- **Encoding** – reversible transformation to a custom alphabet  
- **Hashing** – integrity check only (not secrecy)  
- **Encryption** – real AES-GCM when a password is provided  
- Passwords are derived on the fly and never persisted  
- Offline-first: no data leaves the device for core operations  

## Building

Requirements:

- JDK 17+  
- Android SDK 34 + Build-Tools 34  
- Android Studio Ladybug+ or command-line Gradle  

```bash
git clone https://github.com/blitzlabx/Z-Code.git
cd Z-Code
echo "sdk.dir=/path/to/Android/Sdk" > local.properties
./gradlew assembleDebug
```

APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

## Testing

Core engine unit tests cover:

- Round-trip ASCII / Unicode / multiline / emoji  
- All mode × language × code-type combinations  
- Password protection and wrong-password rejection  
- Malformed input rejection  
- Long text and compression behaviour  

```bash
./gradlew test
```

## CI/CD

GitHub Actions workflow (`.github/workflows/android.yml`) runs on every push and PR:

- Checkout  
- Setup JDK 17  
- Setup Android SDK  
- Run unit tests  
- Lint  
- Assemble debug & release APKs  
- Upload artifacts  

## Project Structure

```
Z-Code/
├── app/
│   ├── src/main/java/com/blitzlabx/zcode/
│   │   ├── core/          # ZCodeEngine, BaseN, Config
│   │   ├── data/          # Room, DataStore
│   │   ├── ui/            # Compose screens, theme, ViewModel
│   │   ├── MainActivity.kt
│   │   └── ZCodeApplication.kt
│   └── src/test/          # Unit tests
├── .github/workflows/
└── README.md
```

## Contributing

1. Fork the repository  
2. Create a feature branch  
3. Write tests for new behaviour  
4. Ensure `./gradlew test assembleDebug` succeeds  
5. Open a Pull Request  

## License

Copyright © Blitz / blitzlabx. All rights reserved.  
Created for demonstration and personal use.

---

**Z-Code** – Compact Communication, Limitless Possibilities.  
Created by Blitz · blitzlabx · https://t.me/blitzmax
