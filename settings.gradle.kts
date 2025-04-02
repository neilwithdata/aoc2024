plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "aoc2024"
include("src:day01")
findProject(":src:day01")?.name = "day01"
include("src:day02")
findProject(":src:day02")?.name = "day02"
include("src:day03")
findProject(":src:day03")?.name = "day03"
include("src:day04")
findProject(":src:day04")?.name = "day04"
include("src:day05")
findProject(":src:day05")?.name = "day05"
include("src:day06")
findProject(":src:day06")?.name = "day06"
include("src:day07")
findProject(":src:day07")?.name = "day07"
include("src:day08")
findProject(":src:day08")?.name = "day08"
include("src:day09")
findProject(":src:day09")?.name = "day09"
include("src:day10")
findProject(":src:day10")?.name = "day10"
include("src:day11")
findProject(":src:day11")?.name = "day11"
include("src:day12")
findProject(":src:day12")?.name = "day12"
include("src:day13")
findProject(":src:day13")?.name = "day13"
include("src:day14")
findProject(":src:day14")?.name = "day14"
include("src:day15")
findProject(":src:day15")?.name = "day15"
include("src:day15b")
findProject(":src:day15b")?.name = "day15b"
include("src:day16")
findProject(":src:day16")?.name = "day16"
