package com.mena97villalobos.convention

import dev.detekt.gradle.Detekt
import dev.detekt.gradle.extensions.DetektExtension
import dev.detekt.gradle.report.ReportMergeTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType

class DetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = versionCatalogNamed("libs")
            pluginManager.apply("dev.detekt")
            dependencies {
                detektPlugins(libs.requireLibrary("detekt-complexity"))
                detektPlugins(libs.requireLibrary("detekt-coroutines"))
                detektPlugins(libs.requireLibrary("detekt-naming"))
                detektPlugins(libs.requireLibrary("detekt-style"))
                detektPlugins(libs.requireLibrary("detekt-compose-rules"))
                detektPlugins(libs.requireLibrary("detekt-ktlint-rules"))
            }

            extensions.configure<DetektExtension> {
                config.setFrom(
                    files("$rootDir/.detekt/detekt-rules.yml"),
                )

                val baselineFile = file("$projectDir/detekt-baseline.xml")
                if (baselineFile.exists()) {
                    baseline.set(baselineFile)
                }
                buildUponDefaultConfig.set(true)
                ignoreFailures.set(false)
                allRules.set(false)
                parallel.set(true)
            }

            tasks.withType<Detekt>().configureEach {
                reports {
                    html.required.set(true)
                    sarif.required.set(true)
                }
            }
            if (this == rootProject) {
                val mergeTask = tasks.register<ReportMergeTask>("mergeDetektReports") {
                    output.set(layout.buildDirectory.file("reports/detekt/detekt-report.sarif"))
                }
                // After all projects are evaluated, wire everything safely
                gradle.projectsEvaluated {
                    subprojects.forEach { subproject ->
                        subproject.plugins.withId("dev.detekt") {
                            subproject.tasks.withType(Detekt::class.java).forEach { _ ->
                                mergeTask.configure {
                                    input.from(
                                        subproject.layout.buildDirectory.file(
                                            "reports/detekt/detekt.sarif",
                                        ),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun Project.versionCatalogNamed(name: String): VersionCatalog {
    val catalogs =
        extensions.findByType(VersionCatalogsExtension::class.java)
            ?: rootProject.extensions.getByType(VersionCatalogsExtension::class.java)
    return catalogs.named(name)
}

private fun VersionCatalog.requireLibrary(alias: String) =
    findLibrary(alias).orElseThrow {
        IllegalArgumentException("Missing version catalog library alias: $alias")
    }.get()

fun DependencyHandlerScope.detektPlugins(name: Any) {
    add("detektPlugins", name)
}