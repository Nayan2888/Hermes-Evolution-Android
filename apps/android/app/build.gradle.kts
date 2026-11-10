plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.chaquo.python")
}

android {
    namespace = "org.nousresearch.hermes.evolution"
    compileSdk = 35

    defaultConfig {
        applicationId = "org.nousresearch.hermes.evolution"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0-evolution"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        ndk {
            abiFilters += listOf("arm64-v8a")
        }

        python {
            version = "3.14"

            pip {
                install("python-dotenv==1.2.4")
                install("httpx==0.28.1")
                install("openai==2.54.0")
                install("pydantic==2.14.1")
                install("rich==14.3.4")
                install("jinja2==3.1.6")
                install("ruamel.yaml==0.18.16")
                install("croniter==6.2.4")
                install("snowballstemmer==3.1.1")
                install("packaging==26.3")
                install("Markdown==3.11.2")
                install("certifi==2026.10.19")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.10.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.3.1")
    implementation("androidx.compose.material:material-icons-extended:1.7.5")
    implementation("androidx.navigation:navigation-compose:2.8.3")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    // Security & Encrypted Credential Vault (Android Keystore integration)
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // Background work orchestration & automations
    implementation("androidx.work:work-runtime-ktx:2.10.0")

    // JSON serialization for IPC bridge
    implementation("com.google.code.gson:gson:2.11.0")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    testImplementation("org.mockito:mockito-core:5.14.2")
    testImplementation("org.json:json:20240303")
}
