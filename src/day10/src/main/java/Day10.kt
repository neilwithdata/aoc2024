import java.io.File

enum class Direction(val vector: Position) {
    NORTH(Position(-1, 0)),
    EAST(Position(0, 1)),
    SOUTH(Position(1, 0)),
    WEST(Position(0, -1))
}

data class Position(val row: Int, val col: Int) {
    operator fun plus(other: Position): Position =
        Position(row + other.row, col + other.col)

    operator fun minus(other: Position): Position =
        Position(row - other.row, col - other.col)

    operator fun unaryMinus(): Position = Position(-row, -col)
}

class Grid(input: List<String>) {
    private val numRows = input.size
    private val numCols = input[0].length

    private val data =
        Array(numRows) { row ->
            Array(numCols) { col ->
                input[row][col].digitToInt()
            }
        }

    fun trailheadsSum(): Int {
        var total = 0

        for (row in 0 until numRows) {
            for (col in 0 until numCols) {
                val position = Position(row, col)

                if (digitAt(position) == 0) {
                    total += calculateTrailheadScore(position)
                }
            }
        }

        return total
    }

    private fun calculateTrailheadScore(startPosition: Position): Int {
        val endpoints = mutableSetOf<Position>()

        val progress = mutableListOf(startPosition)
        while (progress.isNotEmpty()) {
            val currPosition = progress.removeFirst()
            val digit = digitAt(currPosition)

            if (digit == 9) {
                endpoints.add(currPosition)
            } else {
                Direction.entries.forEach { direction ->
                    val new = currPosition + direction.vector

                    if (withinBounds(new) && digitAt(new) == digit + 1) {
                        progress += new
                    }
                }
            }
        }

        return endpoints.size
    }

    private fun digitAt(position: Position): Int =
        data[position.row][position.col]

    private fun withinBounds(position: Position): Boolean =
        withinBounds(position.row, position.col)

    private fun withinBounds(row: Int, col: Int): Boolean =
        (row in 0 until numRows) && (col in 0 until numCols)
}


fun main() {
    val input = File("data/day10_input.txt")
        .readLines()

    val grid = Grid(input)
    println(grid.trailheadsSum())
}