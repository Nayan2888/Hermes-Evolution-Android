# Patch Ledger: Hermes Evolution Android

## 1. Upstream Provenance & Baseline
- **Official Repository**: `https://github.com/NousResearch/hermes-agent`
- **Pinned Audited Upstream Commit**: `fc86fb39a1ffb2071e0bcda4e7f837e7d79e05b8`
- **Target Architecture**: Android 16 (API 36), ARM64 / `aarch64` (Samsung Galaxy M36 5G profile)
- **Active Branch**: `gemini/hermes-evolution-android`
- **Authorship & Attribution**: Original Nous Research Hermes Agent authors; independent Android native port.

---

## 2. Platform Adaptation Entries

| Entry ID | Subsystem | File Path | Modification Rationale |
| :--- | :--- | :--- | :--- |
| **PATCH-01** | **Storage & Sandbox** | `apps/android/app/src/main/python/hermes_android/android_platform_adapter.py` | Enforces scoped storage boundaries under `context.filesDir/hermes`. Overrides POSIX `/root` and `/home` defaults. Prevents path traversal outside permitted app sandbox. |
| **PATCH-02** | **IPC Bridge** | `apps/android/app/src/main/python/hermes_android/hermes_android_bridge.py` | Implements bidirectional JSON-RPC bridge between Android Service and embedded CPython runtime. Emits thinking deltas, token streams, and tool events. |
| **PATCH-03** | **Security Gate** | `apps/android/app/src/main/java/org/nousresearch/hermes/evolution/engine/SecurityGatekeeper.kt` | Enforces default-deny access for destructive file edits, shell execution, and untrusted repo installations. Disables `sudo` on stock non-rooted Android 16. |
| **PATCH-04** | **Secret Scrubbing** | `apps/android/app/src/main/python/hermes_android/android_platform_adapter.py` | Implements automated regex redaction for OpenAI (`sk-`), Gemini (`AIzaSy`), Groq (`gsk_`), and GitHub (`ghp_`) API tokens in activity logs. |
| **PATCH-05** | **Skill Factory** | `apps/android/app/src/main/python/hermes_android/android_skill_factory.py` | Autonomous skill generation engine with AST syntax checks, forbidden module detection, test definitions, snapshot versioning, and rollback. |
| **PATCH-06** | **GitHub Hub** | `apps/android/app/src/main/python/hermes_android/android_github_hub.py` | Analyzes public repository URLs for Android ARM64 compatibility, flags unsupported desktop GUI dependencies, and generates integration scorecards. |
| **PATCH-07** | **Foreground Service**| `apps/android/app/src/main/java/org/nousresearch/hermes/evolution/engine/HermesEngineService.kt` | Persistent Android Foreground Service hosting embedded Python engine, managing wake locks, notification updates, and Android 16 memory pressure. |
| **PATCH-08** | **Model Router** | `apps/android/app/src/main/java/org/nousresearch/hermes/evolution/engine/ModelRouter.kt` | Manages local GGUF models and Cloud providers with memory awareness (8 GB device profile), offline mode toggle, and cost consent checks. |
| **PATCH-09** | **CI & Packaging** | `.github/workflows/android-build.yml` | GitHub Actions workflow for building debug ARM64 APK with 16 KB page size alignment and running Python/Kotlin test suites. |

---

## 3. Upstream Code Integrity
- No files in upstream `agent/`, `hermes_agent/`, `tools/`, or `skills/` have been deleted or replaced with fake stubs.
- Upstream licenses (MIT) and copyright notices are strictly preserved in root `LICENSE` and headers.
