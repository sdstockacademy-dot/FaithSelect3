# 🕉️ FaithSelect — Complete Build, Firebase & Play Store Guide

---

## TABLE OF CONTENTS

1. Project Structure Overview
2. Firebase Setup (Free Tier)
3. Firestore Database Structure
4. Audio Upload Guide
5. Google Play Billing Setup
6. Subscription Setup in Play Console
7. Building the AAB for Play Store
8. Play Store Publishing Steps
9. Privacy Policy Requirements
10. How to Add a New Religion Later
11. Firestore Security Rules
12. FCM Notification Setup

---

## 1. PROJECT STRUCTURE OVERVIEW

```
FaithSelect/
├── app/
│   ├── src/main/
│   │   ├── java/com/faithselect/
│   │   │   ├── FaithSelectApp.kt           ← Hilt Application class
│   │   │   ├── data/
│   │   │   │   ├── local/                  ← Room DB (favorites + downloads)
│   │   │   │   │   ├── FaithSelectDatabase.kt
│   │   │   │   │   ├── dao/Daos.kt
│   │   │   │   │   └── entities/Entities.kt
│   │   │   │   ├── remote/firebase/
│   │   │   │   │   └── FirestoreDataSource.kt
│   │   │   │   └── repository/
│   │   │   │       ├── ContentRepositoryImpl.kt
│   │   │   │       ├── PreferencesRepositoryImpl.kt
│   │   │   │       └── SubscriptionRepositoryImpl.kt ← Billing logic
│   │   │   ├── di/AppModules.kt            ← Hilt DI modules
│   │   │   ├── domain/
│   │   │   │   ├── model/Models.kt         ← All data models
│   │   │   │   ├── repository/Repositories.kt ← Interfaces
│   │   │   │   └── usecase/UseCases.kt     ← Business logic
│   │   │   ├── presentation/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── MainViewModel.kt
│   │   │   │   ├── audio/                  ← Audio screens + service
│   │   │   │   ├── favorites/
│   │   │   │   ├── home/                   ← Home + Search
│   │   │   │   ├── library/                ← Religion → Scripture → Chapter
│   │   │   │   ├── navigation/             ← NavGraph + Screen routes
│   │   │   │   ├── onboarding/
│   │   │   │   ├── paywall/                ← Subscription gate
│   │   │   │   ├── profile/
│   │   │   │   ├── reader/                 ← Verse list + reader
│   │   │   │   ├── splash/
│   │   │   │   └── theme/Theme.kt          ← Colors, typography
│   │   │   └── utils/FaithFCMService.kt
│   │   ├── AndroidManifest.xml
│   │   └── res/
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── gradle/libs.versions.toml               ← Version catalog
├── build.gradle.kts
└── settings.gradle.kts
```

**Architecture: Clean Architecture + MVVM**
- `domain/` — pure Kotlin, no Android dependencies
- `data/` — implements domain interfaces, talks to Firebase & Room
- `presentation/` — Jetpack Compose UI + ViewModels

---

## 2. FIREBASE SETUP (FREE TIER — STEP BY STEP)

### Step 1: Create Firebase Project
1. Go to https://console.firebase.google.com
2. Click **Add Project** → Name it "FaithSelect"
3. Disable Google Analytics (optional, you can enable it)
4. Click **Create Project**

### Step 2: Register Android App
1. In Firebase Console → Project Overview → click **Android** icon
2. Android package name: `com.faithselect`
3. App nickname: "FaithSelect Android"
4. **Download `google-services.json`**
5. Place it at: `FaithSelect/app/google-services.json`

### Step 3: Enable Firestore
1. Firebase Console → Build → Firestore Database
2. Click **Create Database**
3. Choose **Start in test mode** (we'll add security rules later)
4. Select region closest to India: `asia-south1` (Mumbai)
5. Click **Done**

### Step 4: Enable Firebase Storage
1. Firebase Console → Build → Storage
2. Click **Get Started** → Start in test mode
3. Same region: `asia-south1`

### Step 5: Enable Firebase Auth (Optional but recommended)
1. Firebase Console → Build → Authentication
2. Click **Get Started**
3. Enable **Google** sign-in provider
4. Add your SHA-1 fingerprint (from Android Studio → Gradle → signingReport)

### Step 6: Enable Firebase Cloud Messaging
1. Firebase Console → Engage → Messaging
2. No setup needed — it's auto-enabled with google-services.json

### Free Tier Limits (Spark Plan):
| Service      | Free Limit                              |
|-------------|------------------------------------------|
| Firestore    | 50K reads/day, 20K writes/day, 1GB storage |
| Storage      | 5GB storage, 1GB/day download            |
| FCM          | Unlimited messages                       |
| Auth         | Unlimited users                          |

---

## 3. FIRESTORE DATABASE STRUCTURE

Create these collections in Firestore Console:

### Collection: `religions`
```
religions/
  hinduism/
    id: "hinduism"
    name: "Hinduism"
    nameHindi: "हिन्दू धर्म"
    nameBengali: "হিন্দু ধর্ম"
    iconUrl: ""
    description: "The world's oldest living religion"
    isActive: true
    sortOrder: 1
```

### Collection: `scriptures`
```
scriptures/
  bhagavad_gita/
    id: "bhagavad_gita"
    religionId: "hinduism"
    title: "Bhagavad Gita"
    titleHindi: "श्रीमद भगवद्गीता"
    titleBengali: "শ্রীমদ্ভগবদ্গীতা"
    description: "The Song of God — 18 chapters of divine wisdom"
    coverImageUrl: ""
    totalChapters: 18
    isActive: true
    sortOrder: 1

  ramayan/
    id: "ramayan"
    religionId: "hinduism"
    title: "Ramayan"
    titleHindi: "रामायण"
    titleBengali: "রামায়ণ"
    totalChapters: 7
    isActive: true
    sortOrder: 2

  mahabharat/
    id: "mahabharat"
    religionId: "hinduism"
    title: "Mahabharat"
    titleHindi: "महाभारत"
    titleBengali: "মহাভারত"
    totalChapters: 18
    isActive: true
    sortOrder: 3
```

### Collection: `chapters`
```
chapters/
  gita_ch1/
    id: "gita_ch1"
    scriptureId: "bhagavad_gita"
    religionId: "hinduism"
    chapterNumber: 1
    title: "Arjuna Vishada Yoga"
    titleHindi: "अर्जुन विषाद योग"
    titleBengali: "অর্জুন বিষাদ যোগ"
    totalVerses: 47
    summary: "The chapter of Arjuna's grief and despondency"
```

### Collection: `verses`
```
verses/
  gita_1_1/
    id: "gita_1_1"
    chapterId: "gita_ch1"
    scriptureId: "bhagavad_gita"
    religionId: "hinduism"
    chapterNumber: 1
    verseNumber: 1
    originalText: "धृतराष्ट्र उवाच | धर्मक्षेत्रे कुरुक्षेत्रे..."
    hindiText: "धृतराष्ट्र ने कहा: हे संजय..."
    bengaliText: "ধৃতরাষ্ট্র বললেন: হে সঞ্জয়..."
    englishText: "Dhritarashtra said: O Sanjaya, after..."
    hindiMeaning: "राजा धृतराष्ट्र ने पूछा..."
    bengaliMeaning: "রাজা ধৃতরাষ্ট্র জিজ্ঞেস করলেন..."
    englishMeaning: "King Dhritarashtra asked Sanjaya..."
    audioUrl: "https://storage.googleapis.com/faithselect.appspot.com/audio/verses/gita_1_1.mp3"
    tags: ["kurukshetra", "dharma", "war", "dhritarashtra", "arjuna"]
```

### Collection: `audio`
```
audio/
  hanuman_chalisa_full/
    id: "hanuman_chalisa_full"
    religionId: "hinduism"
    scriptureId: ""
    title: "Hanuman Chalisa"
    titleHindi: "हनुमान चालीसा"
    titleBengali: "হনুমান চালিসা"
    description: "Complete Hanuman Chalisa with Hindi pronunciation"
    audioUrl: "https://storage.googleapis.com/faithselect.appspot.com/audio/prayers/hanuman_chalisa.mp3"
    coverImageUrl: ""
    durationSeconds: 480
    category: "PRAYER"
    isDownloadable: true

  bajrangbali_story/
    id: "bajrangbali_story"
    religionId: "hinduism"
    title: "Bajrangbali — The Mighty One"
    titleHindi: "बजरंगबली की कहानी"
    audioUrl: "https://storage.googleapis.com/..."
    durationSeconds: 1800
    category: "STORY"
    isDownloadable: true
```

### Collection: `daily_content`
```
daily_content/
  2024-01-15/
    id: "2024-01-15"
    date: "2024-01-15"
    verseId: "gita_2_47"
    originalText: "कर्मण्येवाधिकारस्ते मा फलेषु कदाचन"
    hindiText: "तेरा कर्म करने में ही अधिकार है..."
    bengaliText: "তোমার কাজ করার অধিকার আছে..."
    englishText: "You have a right to perform your duties..."
    source: "Bhagavad Gita 2.47"
    backgroundImageUrl: ""
```

---

## 4. AUDIO UPLOAD GUIDE

### Recommended Format:
- **Format:** MP3 (best compatibility)
- **Bitrate:** 128 kbps (good quality, small file size)
- **Sample Rate:** 44.1 kHz

### Upload Steps:
1. Firebase Console → Storage → Upload files
2. Create folder structure:
   ```
   audio/
     prayers/
       hanuman_chalisa.mp3
       aarti_ganesh.mp3
     stories/
       bajrangbali_story.mp3
     verses/
       gita_1_1.mp3
       gita_2_47.mp3
     teachings/
       bhagwat_katha.mp3
   ```
3. After uploading, right-click → **Get download URL**
4. Copy the URL and paste it into the Firestore `audioUrl` field

### IMPORTANT — Storage Rules:
Set Firebase Storage rules so only authenticated or any user can read (not write):
```
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /audio/{allPaths=**} {
      allow read: if true;   // Public read — audio can be streamed
      allow write: if false; // No user uploads
    }
  }
}
```

### File Size Guide:
| Content         | Duration | Size @ 128kbps |
|----------------|----------|----------------|
| Single verse    | ~30s     | ~480 KB        |
| Hanuman Chalisa | ~8 min   | ~7.5 MB        |
| Full story      | ~30 min  | ~28 MB         |

---

## 5. GOOGLE PLAY BILLING SETUP

### Step 1: Create Subscription in Play Console
1. Go to https://play.google.com/console
2. Select your app → **Monetize** → **Subscriptions**
3. Click **Create Subscription**
4. Fill in:
   - **Product ID:** `faith_select_monthly_99` ← must match `BuildConfig.BASE_SUBSCRIPTION_ID`
   - **Name:** "Faith Select Premium"
   - **Description:** "Unlimited access to all sacred scriptures, audio, and daily verses"

### Step 2: Add Base Plan
1. Under Subscriptions → Add **Base Plan**
2. Base Plan ID: `monthly-inr-99`
3. Billing period: **Monthly**
4. Price: **₹99** (auto-converts to other currencies)
5. Tax: Enable "Tax exclusive" so Google handles GST

### Step 3: Add Free Trial
1. Under Base Plan → **Add Offer**
2. Offer ID: `free-trial-3-days`
3. Offer Type: **Free Trial**
4. Duration: **3 days**
5. Eligibility: **New subscribers only**
6. Click **Save & Activate**

### Step 4: Activate
1. Click **Activate** on both the base plan and the offer
2. Wait ~24h for the product to be available in testing

### Step 5: Set Up Test Accounts
1. Play Console → Setup → **License Testing**
2. Add your Gmail address as a tester
3. Testers can purchase without being charged
4. Purchases process instantly for license testers (no real billing period)

---

## 6. BUILDING THE AAB FILE FOR PLAY STORE

### Step 1: Create a Keystore (one-time, KEEP THIS SAFE)
```bash
keytool -genkey -v -keystore faithselect-release.jks \
  -alias faithselect -keyalg RSA -keysize 2048 -validity 10000
```
⚠️ **NEVER lose this file.** You cannot update your app without it.

### Step 2: Configure Signing in Android Studio
1. `Build` → `Generate Signed Bundle/APK`
2. Choose **Android App Bundle**
3. Select your `faithselect-release.jks`
4. Enter your keystore password and key alias
5. Select **Release** build variant
6. Click **Finish**
7. AAB will be in: `app/release/app-release.aab`

### OR use `gradle.properties` for CI/CD:
```properties
# gradle.properties (DO NOT commit to Git)
KEYSTORE_PATH=/path/to/faithselect-release.jks
KEYSTORE_PASSWORD=yourpassword
KEY_ALIAS=faithselect
KEY_PASSWORD=yourkeypassword
```

Then in `app/build.gradle.kts` release block:
```kotlin
signingConfigs {
    create("release") {
        storeFile = file(project.property("KEYSTORE_PATH") as String)
        storePassword = project.property("KEYSTORE_PASSWORD") as String
        keyAlias = project.property("KEY_ALIAS") as String
        keyPassword = project.property("KEY_PASSWORD") as String
    }
}
buildTypes {
    release {
        signingConfig = signingConfigs.getByName("release")
        ...
    }
}
```

---

## 7. PLAY STORE PUBLISHING STEPS

### Requirements Checklist:
- [ ] App icon: 512×512 PNG (no rounded corners — Play Store adds them)
- [ ] Feature graphic: 1024×500 PNG
- [ ] Screenshots: min 2, recommend 4–8 (phone screenshots required)
- [ ] Short description: max 80 chars
- [ ] Full description: max 4000 chars
- [ ] Privacy policy URL (required for subscription apps)
- [ ] Content rating completed
- [ ] AAB built and signed

### Upload Steps:
1. Play Console → **Create app**
2. App name: "Faith Select"
3. Default language: English (US)
4. App or game: App
5. Free or paid: **Free** (revenue comes from in-app subscription)
6. Accept policies → **Create app**

### Set Up App:
1. **Store presence** → Main store listing → Add all metadata
2. **App content** → Privacy policy → Enter your URL
3. **App content** → Content rating → Complete questionnaire
4. **Production** → Create new release → Upload AAB
5. Review and publish

### Phased Rollout (recommended for first release):
- Start with **10%** rollout
- Monitor crashes in Android Vitals
- Gradually increase to 100%

---

## 8. PRIVACY POLICY REQUIREMENTS

**Required because:** Your app collects personal data (via Firebase Auth) and has a subscription.

### Minimum Required Sections:
```
1. Data We Collect
   - Account information (if using Firebase Auth)
   - Usage analytics (Firebase Analytics)
   - Crash reports (Firebase Crashlytics)
   - Device information
   - Purchase information (handled by Google Play)

2. How We Use Data
   - To provide the subscription service
   - To personalize content
   - To improve the app

3. Data Sharing
   - Firebase (Google) — analytics and infrastructure
   - Google Play — subscription billing
   - We do NOT sell personal data

4. User Rights
   - Right to delete account
   - Contact: support@faithselect.app

5. Children's Privacy
   - App is intended for users 13+
   - We do not knowingly collect data from children under 13

6. Subscription Terms
   - Pricing, cancellation, refund policy

7. Contact
   - support@faithselect.app
```

### Free Privacy Policy Generators:
- https://www.privacypolicygenerator.info
- https://www.freeprivacypolicy.com

Host it on GitHub Pages or any free static hosting.

---

## 9. FIRESTORE SECURITY RULES

Replace test mode rules with these before going live:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    // ─── Content Collections (read-only, no auth required) ────────────────
    // All content is public to read — subscription validation is in the app
    // The app itself is locked behind the paywall
    match /religions/{doc} {
      allow read: if true;
      allow write: if false;
    }
    match /scriptures/{doc} {
      allow read: if true;
      allow write: if false;
    }
    match /chapters/{doc} {
      allow read: if true;
      allow write: if false;
    }
    match /verses/{doc} {
      allow read: if true;
      allow write: if false;
    }
    match /audio/{doc} {
      allow read: if true;
      allow write: if false;
    }
    match /daily_content/{doc} {
      allow read: if true;
      allow write: if false;
    }

    // ─── Admin writes (only via Firebase Admin SDK, never from app) ───────
    // Use Firebase Console or Admin SDK to populate content
  }
}
```

---

## 10. HOW TO ADD A NEW RELIGION (e.g., Bible / Quran)

The app is fully modular. Adding a new religion requires **zero code changes**.

### Step 1: Add to Firestore
```
religions/christianity/
  id: "christianity"
  name: "Christianity"
  nameHindi: "ईसाई धर्म"
  nameBengali: "খ্রিস্টান ধর্ম"
  isActive: true
  sortOrder: 2
```

### Step 2: Add Scriptures
```
scriptures/bible/
  religionId: "christianity"
  title: "Holy Bible"
  ...
```

### Step 3: Add Chapters and Verses
Follow the same structure. The app automatically picks up new data.

### Step 4: Upload Audio
Upload audio files to Firebase Storage under `/audio/prayers/` or `/audio/teachings/`.

### Step 5: Enable
Set `isActive: true` — the app immediately shows it in the Library.

### That's it! No app update required. 🎉

---

## 11. FCM DAILY VERSE NOTIFICATIONS

### Subscribe Users to Topic (add to FaithSelectApp.kt):
```kotlin
import com.google.firebase.messaging.FirebaseMessaging

// In FaithSelectApp.onCreate():
FirebaseMessaging.getInstance().subscribeToTopic("daily_verse")
```

### Send from Firebase Console:
1. Firebase Console → Engage → Messaging
2. New campaign → Notification
3. Topic: `daily_verse`
4. Schedule: Daily at 7:00 AM IST

### Send via Firebase Functions (Automated — recommended):
```javascript
// functions/index.js
const admin = require('firebase-admin');
const functions = require('firebase-functions');

// Runs every day at 7:00 AM IST (01:30 UTC)
exports.sendDailyVerse = functions.pubsub
  .schedule('30 1 * * *')
  .timeZone('Asia/Kolkata')
  .onRun(async (context) => {
    const today = new Date().toISOString().split('T')[0];
    const doc = await admin.firestore()
      .collection('daily_content')
      .doc(today)
      .get();

    if (!doc.exists) return null;
    const data = doc.data();

    await admin.messaging().sendToTopic('daily_verse', {
      notification: {
        title: '🕉️ Your Daily Verse',
        body: data.englishText.substring(0, 100) + '…',
      },
      data: {
        type: 'daily_verse',
        date: today,
        source: data.source,
      },
    });
  });
```

Deploy: `firebase deploy --only functions`

---

## 12. QUICK CHECKLIST BEFORE LAUNCH

### Technical:
- [ ] `google-services.json` placed in `app/` folder
- [ ] Firebase Firestore populated with at least 1 religion, 1 scripture, 3 chapters, 10 verses
- [ ] At least 1 audio item uploaded to Firebase Storage
- [ ] Daily content for today's date added to Firestore
- [ ] Play Console subscription product ID matches `faith_select_monthly_99`
- [ ] Release AAB built and signed
- [ ] ProGuard rules verified (test release build)
- [ ] Firestore security rules updated (not test mode)

### Play Store:
- [ ] App icon (512×512)
- [ ] Feature graphic (1024×500)
- [ ] 4+ screenshots
- [ ] Privacy policy URL live and accessible
- [ ] Content rating completed
- [ ] App reviewed and published

### Legal:
- [ ] Privacy policy covers subscription, data collection, and contact
- [ ] Terms of service published
- [ ] Comply with Google Play's Subscription policies
- [ ] App does not violate any religious sensitivity guidelines

---

*Built with ❤️ for spiritual seekers everywhere.*
*Faith Select — Sacred Wisdom. Pure Knowledge.*
