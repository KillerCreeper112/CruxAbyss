plugins {
  java
  alias(libs.plugins.paperweight)
  alias(libs.plugins.shadow)
  kotlin("jvm") version "2.1.0"
}

version = "1.0"

repositories {
  mavenCentral()
  maven("https://dependency.download/releases")
}

dependencies {
  compileOnly("dev.kitteh:factions:4.4.0")
  paperweight.paperDevBundle(libs.versions.paper)
  compileOnly(
    files(
      "E:\\Plugins\\YO\\CruxCore\\build\\libs\\CruxCore-1.0-all.jar",
      "E:\\Plugins\\YO\\uSurvive\\build\\libs\\uSurvive-1.0-all.jar",
      "E:\\Plugins\\YO\\CruxTeleport\\build\\libs\\CruxTeleport-1.0-all.jar",
      "E:\\Plugins\\YO\\CruxQuestLine\\build\\libs\\CruxQuestLine-1.0-all.jar",
      "E:\\Plugins\\YO\\CruxChallenges\\build\\libs\\CruxChallenges-1.0-all.jar",
      "D:\\ModelEngine-4.0.7.jar",
      "E:\\Plugins\\YO\\CruxWorldGen\\build\\libs\\CruxWorldGen-1.0-all.jar"
      //"D:\\Geyser-Spigot (1).jar"
    )
  )

  compileOnly(platform(kotlin("bom")))
  compileOnly(kotlin("stdlib"))
  //implementation(kotlin("reflect"))
  //implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
  testCompileOnly(kotlin("test"))

  compileOnly(fileTree("libs") {
    include("*.jar")
  })
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

tasks {
  assemble {
    //dependsOn(reobfJar)
    dependsOn(shadowJar)
  }
}

allprojects {

  plugins.apply("java")

  repositories {
    mavenCentral()
  }

  tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
  }

  tasks.withType<Test> {
    systemProperty("file.encoding", "UTF-8")
  }

  tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
  }
}













