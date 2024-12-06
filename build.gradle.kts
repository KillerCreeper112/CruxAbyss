
plugins {
    java
    alias(libs.plugins.paperweight)
    alias(libs.plugins.shadow)
}

version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    paperweight.paperDevBundle(libs.versions.paper)
    compileOnly(files(
        "E:\\Plugins\\YO\\CruxCore\\build\\libs\\CruxCore-1.0-all.jar",
        "E:\\Plugins\\YO\\uSurvive\\build\\libs\\uSurvive-1.0-all.jar",
        "E:\\Plugins\\YO\\CruxTeleport\\build\\libs\\CruxTeleport-1.0-all.jar",
        "D:\\ModelEngine-4.0.7.jar",
        "D:\\Geyser-Spigot (1).jar"
    ))

    compileOnly(fileTree("libs"){
        include("*.jar")
    })
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

tasks{
    assemble{
        //dependsOn(reobfJar)
        dependsOn(shadowJar)
    }
}

allprojects{

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

    tasks.withType<Javadoc>{
        options.encoding = "UTF-8"
    }
}













