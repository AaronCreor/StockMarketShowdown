# StockMarketShowdown

A competitive **stock market simulation** Android app built in **Kotlin**.

**Demo video:** https://www.youtube.com/watch?v=Yhh5TCp1GSE  
[![Demo Video](https://img.youtube.com/vi/Yhh5TCp1GSE/0.jpg)](https://www.youtube.com/watch?v=Yhh5TCp1GSE)

---

## Overview

StockMarketShowdown is a game-like stock market simulator where users can sign in, view companies, and participate in a competitive “showdown” experience.

The app ships with a bundled company dataset (`companyData.json`) and uses Firebase services for user/auth + data storage. :contentReference[oaicite:1]{index=1}

---

## Tech Stack

- **Android (Kotlin)**
- **Firebase** (Auth + Firestore + Realtime Database) :contentReference[oaicite:2]{index=2}
- **Retrofit + OkHttp** (networking) :contentReference[oaicite:3]{index=3}
- **Glide** (image loading) :contentReference[oaicite:4]{index=4}
- AndroidX Navigation (Safe Args), ViewModel / LiveData :contentReference[oaicite:5]{index=5}

---

## Requirements

- **Android Studio** (recommended)
- Android SDK **34**
- A device/emulator running **Android 14+** (the project currently targets `minSdk = 34`) :contentReference[oaicite:6]{index=6}

---

## Getting Started

### 1) Clone
```bash
git clone https://github.com/AaronCreor/StockMarketShowdown.git
cd StockMarketShowdown
