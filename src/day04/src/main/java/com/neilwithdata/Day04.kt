package com.neilwithdata

import java.io.File

data class Direction(
    val x: Int,
    val y: Int
) {
    companion object {
        val all = buildList {
            for (x in -1..1) {
                for (y in -1..1) {
                    if (x == 0 && y == 0) continue

                    add(Direction(x, y))
                }
            }
        }
    }
}

class Grid(private val rows: List<String>) {
    private val numRows: Int = rows.size
    private val numCols: Int = rows[0].length

    fun findAll(word: String): Int {
        var count = 0

        for (row in 0 until numRows) {
            for (col in 0 until numCols) {
                if (rows[row][col] == word[0]) {
                    count += Direction.all.count { direction ->
                        findInDirection(word, row, col, direction)
                    }
                }
            }
        }

        return count
    }

    private fun isValid(row: Int, col: Int): Boolean {
        return row in (0 until numRows) && col in (0 until numCols)
    }

    private fun findInDirection(word: String, row: Int, col: Int, direction: Direction): Boolean {
        // Starting at (row, col), true if you can step out word in the given direction
        var currRow = row
        var currCol = col

        for (c in word) {
            if (!isValid(currRow, currCol) || c != rows[currRow][currCol]) {
                return false
            }

            currRow += direction.y
            currCol += direction.x
        }

        return true
    }
}

fun main() {
    val input = File("data/day04_input.txt")
        .readLines()

    val grid = Grid(input)
    val count = grid.findAll("XMAS")
    println(count)
}