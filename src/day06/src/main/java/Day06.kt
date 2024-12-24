import java.io.File

enum class Direction(val vector: Pair<Int, Int>) {
    NORTH(Pair(-1, 0)),
    EAST(Pair(0, 1)),
    SOUTH(Pair(1, 0)),
    WEST(Pair(0, -1));

    override fun toString(): String =
        when (this) {
            NORTH -> "↑"
            EAST -> "→"
            SOUTH -> "↓"
            WEST -> "←"
        }
}

// Position + facing direction of guard
data class Transform(
    val position: Pair<Int, Int>,
    val direction: Direction
)

data class PatrolRoute(
    val transforms: Set<Transform>,
    val didExit: Boolean
)

operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> =
    Pair(this.first + other.first, this.second + other.second)

class Grid(input: List<String>) {
    private val numRows = input.size
    private val numCols = input[0].length

    companion object {
        private const val START = '^'
        private const val OBSTRUCTION = 'O'
        private const val OBSTACLE = '#'
        private const val CLEAR = '.'
    }

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

    // Move the obstruction to the specified location
    private fun updateObstruction(row: Int, col: Int) {
        // Clear out the previous obstruction
        val location = indexOf(OBSTRUCTION)
        if (location != Pair(-1, -1)) {
            values[location.first][location.second] = CLEAR
        }

        // Set the new obstruction location
        values[row][col] = OBSTRUCTION
    }

    // Count of number of unique positions visited (Part 1)
    fun uniquePositions(): Int =
        patrol().transforms.map { it.position }.toSet().count()

    // Part 2
    fun loopCount(): Int {
        // Perform a default patrol to get all the transforms (default scenario)
        val patrolRoute = patrol()

        val obstructionsThatCauseLoops = mutableSetOf<Pair<Int, Int>>()

        // for each transform, set an obstruction in the next position and evaluate the patrol
        for (transform in patrolRoute.transforms) {
            val next = transform.position + transform.direction.vector

            if (!withinBounds(next) || charAt(next) == OBSTACLE || charAt(next) == START) {
                // Nothing to do - can't put an obstacle in this location
            } else {
                // Put the obstruction in the next position and evaluate to see if this causes a loop
                updateObstruction(next.first, next.second)
                val newRoute = patrol()

                if (!newRoute.didExit) {
                    obstructionsThatCauseLoops.add(next)
                }
            }
        }

        return obstructionsThatCauseLoops.size
    }

    // Guard patrol - starting from ^ perform patrol
    private fun patrol(): PatrolRoute {
        val visited = mutableSetOf<Transform>()
        val directions = Direction.entries.toList()

        var position = indexOf(START)
        var direction = Direction.NORTH

        while (true) {
            val currentTransform = Transform(position, direction)
            if (currentTransform in visited) {
                // We've been here before so we're stuck in a loop
                return PatrolRoute(visited, didExit = false)
            } else {
                visited.add(currentTransform)
            }

            // Move to next location
            val next = position + direction.vector
            if (!withinBounds(next)) {
                return PatrolRoute(
                    visited,
                    didExit = true
                )
            } else if (charAt(next) == OBSTACLE || charAt(next) == OBSTRUCTION) {
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

    // Part 1
    val visitedCount = grid.uniquePositions()
    println(visitedCount)

    // Part 2
    val loopCount = grid.loopCount()
    println(loopCount)
}