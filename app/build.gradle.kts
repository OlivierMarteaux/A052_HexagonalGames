plugins {
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.kotlin)
  alias(libs.plugins.ksp)
  alias(libs.plugins.hilt)
  alias(libs.plugins.googleservices)
  alias(libs.plugins.kotlin.compose)
}

android {
  namespace = "com.openclassrooms.hexagonal.games"
  compileSdk = 36

  defaultConfig {
    applicationId = "com.openclassrooms.hexagonal.games"
    minSdk = 24
    targetSdk = 34
    versionCode = 1
    versionName = "1.0.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
//  composeOptions {
//    kotlinCompilerExtensionVersion = "1.5.11"
//  }
/*  Replaced by JVM toolchain setting*/
//  compileOptions {
//    sourceCompatibility = JavaVersion.VERSION_1_8
//    targetCompatibility = JavaVersion.VERSION_1_8
//  }
//  kotlinOptions {
//    jvmTarget = "1.8"
//  }
  kotlin { jvmToolchain(17) }

  buildFeatures { compose = true }

}

dependencies {
  //_ Personal shared library
  implementation(libs.oliviermarteaux.compose)
  implementation(libs.oliviermarteaux.core)

  //_ kotlin
  implementation(platform(libs.kotlin.bom))

  //_ DI
  implementation(libs.hilt)
  ksp(libs.hilt.compiler)
  implementation(libs.hilt.navigation.compose)

  //_ compose
  implementation(platform(libs.compose.bom))
  implementation(libs.compose.ui)
  implementation(libs.compose.ui.graphics)
  implementation(libs.compose.ui.tooling.preview)
  implementation(libs.material)
  implementation(libs.compose.material3)
  implementation(libs.lifecycle.runtime.compose)
  debugImplementation(libs.compose.ui.tooling)
  debugImplementation(libs.compose.ui.test.manifest)

  implementation(libs.activity.compose)
  implementation(libs.navigation.compose)
  
  implementation(libs.kotlinx.coroutines.android)

  implementation(libs.accompanist.permissions)

  //_ Coil for image loading
  implementation(libs.coil.compose)
  implementation(libs.coil.network.okhttp) // to load images from internet

  //_ Firebase
  implementation(platform(libs.firebase.bom))
  implementation(libs.firebase.analytics)
  implementation(libs.firebase.ui.auth)
  implementation(libs.firebase.firestore)
  implementation(libs.firebase.messaging)
  implementation(libs.firebase.storage)

  //_ Android 8+ retro compatible PhotoPicker
  implementation(libs.activity)

  //_ Preferences DataStore
  implementation(libs.datastore.preferences)

  //_ tests
  testImplementation(libs.junit)
  testImplementation(libs.mockito.kotlin) // Mockito mocking framework
  testImplementation(libs.mockk) // kotlin mocking framework
  testImplementation(libs.kotlinx.coroutines.test) // coroutine test (runTest)
  testImplementation(libs.turbine)// Flow test


  androidTestImplementation(libs.ext.junit)
  androidTestImplementation(libs.espresso.core)

  implementation(libs.material.icons.extended)
}

/**
 * Helper function to force application run to be performed on physical device.
 */
val targetDevice = "adb-cb4d0d70-D3BuA7._adb-tls-connect._tcp" // ← Replace with your actual device ID
val checkPhysicalDevice = tasks.register("checkPhysicalDevice") {
  doFirst {
    val adbOutput = ProcessBuilder("adb", "devices")
      .redirectErrorStream(true)
      .start()
      .inputStream
      .bufferedReader()
      .readText()

    val connectedDevices = adbOutput.lines().filter { line ->
      line.isNotBlank() &&
              !line.startsWith("List") &&
              line.contains("device") &&
              !line.startsWith("emulator-")
    }

    if (connectedDevices.none { it.startsWith(targetDevice) }) {
      throw GradleException("ERROR: Required physical device ($targetDevice) is not connected.")
    } else {
      println("✅ Physical device ($targetDevice) is connected. Proceeding with build.")
    }
  }
}