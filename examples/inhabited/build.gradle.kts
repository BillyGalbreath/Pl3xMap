group = "net.pl3x.map.addon"
version = "1.0"
description = "Pl3xMap addon that renders chunk inhabited times as a heatmap"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

dependencies {
    compileOnly(project(":Pl3xMap"))
}

tasks.reobfJar {
    outputJar.set(rootProject.layout.buildDirectory.file("libs/${project.name}-${project.version}.jar"))
}
