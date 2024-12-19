package com.neilwithdata

import java.io.File

fun main() {
    val lines = File("data/day05_input.txt")
        .readLines()

    val rules: List<Pair<Int, Int>> = lines
        .takeWhile { it.contains("|") }
        .map { line ->
            val first = line.substringBefore('|').toInt()
            val second = line.substringAfter('|').toInt()

            first to second
        }

    val updates: List<List<Int>> = lines
        .filter { it.contains(",") }
        .map { line ->
            line.split(",").map { it.toInt() }
        }

    // for each update, build a map and then check each rule against this map
    var total = 0
    for (update in updates) {
        val updateMap = update
            .withIndex()
            .associate { (index, number) ->
                number to index
            }

        if (isUpdateValid(updateMap, rules)) {
            total += update[update.size / 2]
        }
    }

    println(total)
}

fun isUpdateValid(updateMap: Map<Int, Int>, rules: List<Pair<Int, Int>>): Boolean {
    for (rule in rules) {
        val firstIndex = updateMap[rule.first]
        val secondIndex = updateMap[rule.second]

        if (firstIndex != null && secondIndex != null) {
            if (firstIndex > secondIndex) {
                return false // update fails this rule
            }
        }
    }

    // All rules passed
    return true
}
