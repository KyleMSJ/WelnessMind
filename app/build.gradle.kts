plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "ifsp.project.welnessmind"
    compileSdk = 34

    defaultConfig {
        applicationId = "ifsp.project.welnessmind"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        class RoomSchemaArgProvider(
            @get:InputDirectory
            @get:PathSensitive(PathSensitivity.RELATIVE)
            val schemaDir: File
        ) : CommandLineArgumentProvider {
            override fun asArguments(): Iterable<String> {
                return listOf("-Aroom.schemaLocation=${schemaDir.path}")
            }
        }

        javaCompileOptions {
            annotationProcessorOptions {
                compilerArgumentProviders(
                    RoomSchemaArgProvider(File(projectDir, "schemas"))
                )
            }
        }
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    sourceSets {
        getByName("androidTest").assets.srcDir("$projectDir/schemas")
    }
}

dependencies {
implementation("androidx.annotation:annotation:1.8.0")
    // Room
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-testing:$roomVersion")
    // To use Kotlin annotation processing tool (kapt)
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation ("androidx.room:room-ktx:$roomVersion")

// Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

// Navigation
    val nvgVersion = "2.7.7"

    implementation("androidx.navigation:navigation-fragment-ktx:$nvgVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$nvgVersion")

    implementation ("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")

// API Google
//    implementation ("com.google.android.gms:play-services-maps:18.2.0")
//    implementation ("com.google.api-client:google-api-client-android:1.30.10")

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")

    val lifecycleVersion = "2.8.3"
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Testes unitários
    testImplementation("junit:junit:4.13.2")

    // Testes instrumentados
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
