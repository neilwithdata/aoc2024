package com.neilwithdata

import java.io.File
import kotlin.math.abs

fun main() {
    val numsRegex = Regex("""(\d+)\s+(\d+)""")

    val pairs = File("data/day01_input.txt")
        .readLines()
        .map { line ->
            val (first, second) = requireNotNull(numsRegex.find(line)).destructured
            Pair(first.toInt(), second.toInt())
        }

    val leftSorted = pairs.map { it.first }.sorted()
    val rightSorted = pairs.map { it.second }.sorted()

    // Part 1
    val distance = leftSorted.zip(rightSorted)
        .sumOf { abs(it.first - it.second) }

    println(distance)

    // Part 2
    val leftCounts = leftSorted.groupingBy { it }.eachCount()
    val rightCounts = rightSorted.groupingBy { it }.eachCount()

    var similarity = 0
    for ((number, count) in leftCounts) {
        similarity += number * (rightCounts[number] ?: 0) * count
    }

    println(similarity)
}