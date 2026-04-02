// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ktlint) apply false
}

tasks.register("lintProject") {
    group = "verification"
    description = "Runs Android Lint and ktlint on the app module (style + platform checks)."
    dependsOn(":app:lint", ":app:ktlintCheck")
}

tasks.register("unitTest") {
    group = "verification"
    description = "Runs JVM unit tests for the app module (core, data, domain, di, etc.)."
    dependsOn(":app:testDebugUnitTest")
}