# Physical Device Testing Guide: Samsung Galaxy M36 5G

## Target Device Specifications
- **Hardware**: Samsung Galaxy M36 5G
- **Operating System**: Android 16 (API 36)
- **Architecture**: ARM64 / `aarch64`
- **Memory**: 8 GB Physical RAM
- **Root Status**: Non-rooted stock Samsung One UI firmware

---

## 1. APK Installation & Sideloading

### Method A: Via Android Debug Bridge (ADB)
1. Enable **Developer Options** on Samsung Galaxy M36 5G (`Settings -> About Phone -> Software Information -> Tap Build Number 7 times`).
2. Enable **USB Debugging** in Developer Options.
3. Connect the phone to your build machine via USB.
4. Execute:
   ```bash
   adb install -r -d apps/android/app/build/outputs/apk/debug/app-debug.apk
   ```

### Method B: Direct On-Device Installation
1. Download or copy `app-debug.apk` to phone storage (`/sdcard/Download`).
2. Open **Samsung My Files** app, navigate to `Downloads`.
3. Tap `app-debug.apk` and select **Install**. If prompted, toggle "Allow from this source".

---

## 2. Initial Setup & Permissions
Upon launching **Hermes Evolution**:
1. **Notifications**: When prompted, allow `POST_NOTIFICATIONS`. This is essential for the persistent Android Foreground Service to maintain agent state when multitasking.
2. **Audio / Microphone**: When using the voice transcription tool in Chat or YouTube Studio, accept the runtime audio permission.
3. **Storage**: Hermes confines its files to its scoped sandbox (`/data/user/0/org.nousresearch.hermes.evolution/files/hermes`). For exporting videos or importing external GGUF models, use the built-in system document picker.

---

## 3. Physical Verification Checklist

| Screen / Feature | Action on Samsung M36 5G | Expected Behavior |
| :--- | :--- | :--- |
| **1. Dashboard** | Observe animated Agent Orb | Orb breathes with smooth 60fps animations; displays "HERMES EVOLUTION — READY TO WORK". |
| **2. Chat Workspace** | Type "Hello Hermes" and tap Send | Streaming tokens appear with thinking delta; status updates smoothly. |
| **3. Model Manager** | Select Local GGUF Model & tap Test | Executes local benchmark; RAM usage stays within ~1.2 GB (well below 8 GB limit). |
| **4. Skill Library** | Search "vox-doc" or "youtube" | Instantly filters manifests; displays category and version badges. |
| **5. Skill Factory** | Create custom skill `temp_conv` | Validates AST code safety; creates version 1.0.0 in scoped storage. |
| **6. Evolution Lab** | Tap "Rollback Version" | Restores previous immutable snapshot without crashing. |
| **7. GitHub Hub** | Paste `https://github.com/NousResearch/hermes-agent` | Produces compatibility scorecard (92/100) and displays sandbox constraints. |
| **8. Knowledge Vault** | Tap "Backup" | Exports encrypted AES-256 backup file to `hermes/backup_latest.enc`. |
| **9. Task Manager** | Inspect active workflow | Multi-track progress bar animates across sequential phases. |
| **10. Dev Workspace** | Edit Python script & check Git | Code editor updates file; Git tab displays branch `gemini/hermes-evolution-android`. |
| **11. YouTube Studio** | Tap "Generate Script" & "Audition Voice" | Renders Hindi Devanagari script; generates audio cue audition via Edge-TTS. |
| **12. Security Modal** | Trigger write tool in Chat | Displays "Security Authorization" dialog with Allow/Deny buttons. |
| **13. Background Persistence** | Press Home button during turn | Persistent notification keeps engine running; process survives Android 16 doze. |
