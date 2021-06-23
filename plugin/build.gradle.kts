import xyz.jpenilla.specialgradle.task.RemapJar

plugins {
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.4.0"
    id("xyz.jpenilla.run-paper") version "1.0.3"
    id("xyz.jpenilla.special-gradle") version "1.0.0-SNAPSHOT"
}

dependencies {
    implementation(project(":pl3xmap-api"))
    val cloudVersion = "1.5.0-SNAPSHOT"
    implementation("cloud.commandframework", "cloud-paper", cloudVersion)
    implementation("cloud.commandframework", "cloud-minecraft-extras", cloudVersion)
    implementation("net.kyori", "adventure-text-minimessage", "4.1.0-SNAPSHOT")
    implementation("io.undertow", "undertow-core", "2.2.3.Final")
    implementation("org.bstats", "bstats-bukkit", "2.2.1")
    compileOnly("io.papermc.paper", "paper", "1.17-R0.1-SNAPSHOT", classifier = "mojang-mapped")
}

tasks {
    productionMappedJar {
        archiveFileName.set("${rootProject.name}-${rootProject.version}.jar")
    }
    shadowJar {
        archiveClassifier.set("dev-all")
        from(rootProject.projectDir.resolve("LICENSE"))
        minimize {
            exclude { it.moduleName == "pl3xmap-api" }
            exclude(dependency("io.undertow:.*:.*")) // does not like being minimized _or_ relocated (xnio errors)
        }
        listOf(
            "cloud.commandframework",
            "io.leangen.geantyref",
            "net.kyori.adventure.text.minimessage",
            "org.bstats"
        ).forEach { relocate(it, "${rootProject.group}.plugin.lib.$it") }
    }
    build {
        dependsOn(productionMappedJar)
    }
    runServer {
        minecraftVersion("1.17")
        pluginJars.from(productionMappedJar.flatMap { it.archiveFile })
    }
    withType<RemapJar> {
        quiet.set(true)
    }
}

runPaper {
    disablePluginJarDetection()
}

specialGradle {
    injectRepositories.set(false)
    injectSpigotDependency.set(false)
    minecraftVersion.set("1.17")
    specialSourceVersion.set("1.10.0")
}

bukkit {
    main = "net.pl3x.map.plugin.Pl3xMapPlugin"
    name = rootProject.name
    apiVersion = "1.17"
    website = project.property("githubUrl") as String
    authors = listOf("BillyGalbreath", "jmp")
}
