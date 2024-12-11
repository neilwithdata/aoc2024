package com.neilwithdata

import java.io.File
import kotlin.math.abs

class Report(private val levels: List<Int>) {
    fun isSafe(allowRemoval: Boolean = false): Boolean {
        if (isLevelsSafe(levels)) return true
        if (!allowRemoval) return false

        for (i in levels.indices) {
            val modifiedLevels = levels.toMutableList()
            modifiedLevels.removeAt(i)

            if (isLevelsSafe(modifiedLevels)) {
                return true
            }
        }

        // No matter which level we removed, was always unsafe
        return false
    }

    private fun isLevelsSafe(levels: List<Int>): Boolean {
        val direction = levels[0].compareTo(levels[1])
        if (direction == 0) return false

        for (i in 0 until levels.lastIndex) {
            val curr = levels[i]
            val next = levels[i + 1]

            // All increasing or decreasing
            if (curr.compareTo(next) != direction)
                return false

            if (abs(curr - next) > 3)
                return false
        }

        return true
    }

    companion object {
        fun fromString(input: String): Report {
            val parsedLevels = input.split(" ")
                .map { it.toInt() }

            return Report(parsedLevels)
        }
    }
}


fun main() {
    val reports = File("data/day02_input.txt")
        .readLines()
        .map { Report.fromString(it) }

    // Part 1
    val safeCount = reports.count { it.isSafe() }
    println(safeCount)

    // Part 2
    val safeCountWithRemoval = reports.count { it.isSafe(allowRemoval = true) }
    println(safeCountWithRemoval)
}