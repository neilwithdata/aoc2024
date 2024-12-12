package com.neilwithdata

import java.io.File

fun main() {
    val input = File("data/day03_input.txt")
        .readLines()

    val regex = Regex("""mul\((\d{1,3}),(\d{1,3})\)""")

    // Part 1
    var total = 0
    for (line in input) {
        for (match in regex.findAll(line)) {
            val (first, second) = match.destructured
            total += first.toInt() * second.toInt()
        }
    }

    println(total)

    // Part 2
    val conditionalRegex = Regex("""mul\((\d{1,3}),(\d{1,3})\)|do\(\)|don't\(\)""")
    var conditionalTotal = 0
    var enabled = true

    for (line in input) {
        for (match in conditionalRegex.findAll(line)) {
            when (match.value) {
                "do()" -> enabled = true
                "don't()" -> enabled = false
                else -> {
                    if (enabled) {
                        val (first, second) = match.destructured
                        conditionalTotal += first.toInt() * second.toInt()
                    }
                }
            }
        }
    }

    println(conditionalTotal)
}