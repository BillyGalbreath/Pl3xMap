plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.3.7" apply false
}

subprojects {
    apply {
        plugin<JavaLibraryPlugin>()
        plugin("io.papermc.paperweight.userdev")
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        paperDevBundle("1.19-R0.1-SNAPSHOT")
    }

    tasks {
        assemble {
            dependsOn(reobfJar)
        }

        compileJava {
            options.encoding = Charsets.UTF_8.name()
            options.release.set(17)
        }

        javadoc {
            options.encoding = Charsets.UTF_8.name()
        }

        processResources {
            filteringCharset = Charsets.UTF_8.name()
            filesMatching("plugin.yml") {
                expand(
                    "name" to project.name,
                    "group" to project.group,
                    "version" to project.version,
                    "description" to project.description
                )
            }
        }
    }
}
