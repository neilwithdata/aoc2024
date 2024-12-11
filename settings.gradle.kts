plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "aoc2024"
include("src:day01")
findProject(":src:day01")?.name = "day01"
include("src:day02")
findProject(":src:day02")?.name = "day02"
