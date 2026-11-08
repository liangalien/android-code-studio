/*
 *  This file is part of AndroidIDE.
 *
 *  AndroidIDE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  AndroidIDE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with AndroidIDE.  If not, see <https://www.gnu.org/licenses/>.
 */


import com.tom.rv2ide.build.config.BuildConfig
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm")
  kotlin("kapt")
}



dependencies {
  kapt(libs.google.auto.service)
  implementation(libs.google.auto.service.annotations)

  implementation(kotlin("stdlib"))

  implementation(projects.annotation.annotations)

  implementation(libs.androidx.annotation)
  implementation(libs.common.javapoet)
  implementation(libs.common.ksp)
}

sourceSets.main {
  java.srcDirs("src/main/kotlin")
}

// 与 kaptGenerateStubsKotlin(Kotlin, JVM_17) 保持一致；否则 compileJava 默认可能走 21，
// 触发 "Inconsistent JVM-target compatibility"。
java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile> {
  compilerOptions {
    // 必须与兄弟模块 :annotation:processors 一致（JVM_17）：ksp 处理器在构建期由
    // Gradle daemon JVM 加载，命令行 JAVA_HOME 为 JDK 17，JVM_21 的 class（major 65）
    // 会先触发 kaptGenerateStubsKotlin 的 JVM-target 校验失败，即使跳过也会在
    // 运行期抛 UnsupportedClassVersionError。
    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
  }
}