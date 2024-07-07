
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
    paperweight.paperDevBundle("1.21-R0.1-SNAPSHOT")
    compileOnly(files(
        "E:\\Plugins\\YO\\CruxCore\\build\\libs\\CruxCore-1.0-all.jar"
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













