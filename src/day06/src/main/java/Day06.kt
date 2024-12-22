import java.io.File

enum class Direction(val vector: Pair<Int, Int>) {
    NORTH(Pair(-1, 0)),
    EAST(Pair(0, 1)),
    SOUTH(Pair(1, 0)),
    WEST(Pair(0, -1)),
}

operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> =
    Pair(this.first + other.first, this.second + other.second)

class Grid(input: List<String>) {
    private val numRows = input.size
    private val numCols = input[0].length

    private val values =
        Array(numRows) { row ->
            Array(numCols) { col ->
                input[row][col]
            }
        }

    // First (row, col) where c is found or (-1, -1) if not found
    private fun indexOf(c: Char): Pair<Int, Int> {
        for (row in 0 until numRows) {
            for (col in 0 until numCols) {
                if (values[row][col] == c) {
                    return Pair(row, col)
                }
            }
        }

        return Pair(-1, -1)
    }

    private fun charAt(position: Pair<Int, Int>): Char =
        values[position.first][position.second]

    private fun withinBounds(position: Pair<Int, Int>): Boolean =
        withinBounds(position.first, position.second)

    private fun withinBounds(row: Int, col: Int): Boolean =
        (row in 0 until numRows) && (col in 0 until numCols)

    // Guard patrol - starting from ^ patrol and return # distinct positions visited
    fun patrol(): Int {
        val visited = mutableSetOf<Pair<Int, Int>>()
        val directions = Direction.entries.toList()

        var position = indexOf('^')
        var direction = Direction.NORTH

        while (true) {
            visited.add(position)

            // Move to next location
            val next = position + direction.vector
            if (!withinBounds(next)) {
                return visited.count()
            } else if (charAt(next) == '#') {
                // Turn 90 degrees
                direction = directions[(directions.indexOf(direction) + 1) % 4]
            } else {
                position = next
            }
        }
    }
}

fun main() {
    val input = File("data/day06_input.txt")
        .readLines()

    val grid = Grid(input)
    val visited = grid.patrol()

    println(visited)
}