import java.io.File
import kotlin.math.min

fun lcm(a: Int, b: Int): Int {
    for (n in 2..min(a, b)) {
        if (a % n == 0 && b % n == 0) return n
    }

    return 1
}

fun reduce(a: Int, b: Int): Pair<Int, Int> {
    var numerator = a
    var denominator = b

    while (true) {
        val m = lcm(numerator, denominator)

        if (m <= 1) break

        numerator /= m
        denominator /= m
    }

    return numerator to denominator
}

data class Position(val row: Int, val col: Int) {
    operator fun plus(other: Position): Position =
        Position(row + other.row, col + other.col)

    operator fun minus(other: Position): Position =
        Position(row - other.row, col - other.col)

    operator fun unaryMinus(): Position = Position(-row, -col)
}

// Unique combinations of items: eg. (a,b,c) -> (a,b), (a,c), (b,c)
fun List<Position>.combinations(): List<Pair<Position, Position>> {
    val combinations = mutableListOf<Pair<Position, Position>>()

    for (i in 0 until size - 1) {
        for (j in i + 1 until size) {
            combinations.add(Pair(this[i], this[j]))
        }
    }

    return combinations
}

class Grid(input: List<String>) {
    private val numRows = input.size
    private val numCols = input[0].length

    private val data =
        Array(numRows) { row ->
            Array(numCols) { col ->
                input[row][col]
            }
        }

    private val antennas = findAntennas()

    private fun findAntennas(): Map<Char, List<Position>> {
        val antennaPositions = mutableMapOf<Char, MutableList<Position>>()

        for (row in 0 until numRows) {
            for (col in 0 until numCols) {
                val position = Position(row, col)
                val c = charAt(position)

                if (c != '.') {
                    if (c in antennaPositions) {
                        antennaPositions[c]!!.add(position)
                    } else {
                        antennaPositions[c] = mutableListOf(position)
                    }
                }
            }
        }

        return antennaPositions
    }

    // Part 1
    fun countAntinodes(): Int {
        val antinodes = mutableSetOf<Position>()

        for ((_, positions) in antennas) {
            val combinations = positions.combinations()

            for (combination in combinations) {
                val first = combination.first
                val second = combination.second

                // find the antinode positions
                val delta = first - second
                val firstAntinode = first + delta
                val secondAntinode = second - delta

                // find the extension positions if you extend out delta from both positions
                if (withinBounds(firstAntinode)) antinodes.add(firstAntinode)
                if (withinBounds(secondAntinode)) antinodes.add(secondAntinode)
            }
        }

        return antinodes.size
    }

    // Part 2
    fun countInlineAntinodes(): Int {
        val antinodes = mutableSetOf<Position>()

        for ((_, positions) in antennas) {
            val combinations = positions.combinations()

            // find the antinode positions for each combination
            for (combination in combinations) {
                val first = combination.first
                val second = combination.second

                var delta = first - second

                // Reduce the gradient fraction to the lowest form
                val (numerator, denominator) = reduce(delta.row, delta.col)
                delta = Position(numerator, denominator)

                // Starting from first, move out in both directions
                antinodes.add(first)

                var next = first + delta
                while (withinBounds(next)) {
                    antinodes.add(next)
                    next += delta
                }

                next = first - delta
                while (withinBounds(next)) {
                    antinodes.add(next)
                    next -= delta
                }
            }
        }

        return antinodes.size
    }

    private fun charAt(position: Position): Char =
        data[position.row][position.col]

    private fun withinBounds(position: Position): Boolean =
        withinBounds(position.row, position.col)

    private fun withinBounds(row: Int, col: Int): Boolean =
        (row in 0 until numRows) && (col in 0 until numCols)
}

fun main() {
    val input = File("data/day08_input.txt")
        .readLines()

    val grid = Grid(input)

    // Part 1
    val count = grid.countAntinodes()
    println(count)

    // Part 2
    val inlineAntinodes = grid.countInlineAntinodes()
    println(inlineAntinodes)
}