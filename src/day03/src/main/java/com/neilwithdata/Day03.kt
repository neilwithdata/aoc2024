package com.neilwithdata

import java.io.File

fun main() {
    val input = File("data/day03_input.txt")
        .readLines()

    val regex = Regex("""mul\((\d{1,3}),(\d{1,3})\)""")

    var total = 0
    for (line in input) {
        for (match in regex.findAll(line)) {
            val (first, second) = match.destructured
            total += first.toInt() * second.toInt()
        }
    }

    println(total)
}