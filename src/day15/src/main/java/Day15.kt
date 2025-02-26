import java.io.File

enum class Direction(private val c: Char, val vector: Vector) {
    UP('^', Vector(0, -1)),
    DOWN('v', Vector(0, 1)),
    LEFT('<', Vector(-1, 0)),
    RIGHT('>', Vector(1, 0));

    companion object {
        fun fromChar(input: Char): Direction =
            Direction.entries.first { it.c == input }
    }
}

data class Vector(val x: Int, val y: Int) {
    operator fun plus(other: Vector): Vector =
        Vector(x + other.x, y + other.y)

    operator fun minus(other: Vector): Vector =
        Vector(x - other.x, y - other.y)

    operator fun unaryMinus(): Vector = Vector(-x, -y)

    fun neighbour(direction: Direction): Vector {
        return this + direction.vector
    }
}

class Grid(input: List<String>) {
    private val BOX = 'O'
    private val ROBOT = '@'
    private val WALL = '#'
    private val EMPTY = '.'

    private val numRows = input.size
    private val numCols = input[0].length

    private val data = MutableList(numRows) { row ->
        MutableList(numCols) { col ->
            input[row][col]
        }
    }

    private var robot: Vector = findRobot()

    fun tryMove(direction: Direction) {
        val toMove = mutableListOf(robot)

        var curr = robot
        while (true) {
            curr = curr.neighbour(direction)

            when (charAt(curr)) {
                WALL -> return
                BOX -> toMove += curr
                EMPTY -> {
                    // move over all the boxes one at a time - starting from the last
                    while (toMove.isNotEmpty()) {
                        val from = toMove.removeLast()
                        val to = from.neighbour(direction)
                        move(from, to)
                    }

                    // Finally set the robot position as empty
                    data[robot.y][robot.x] = EMPTY
                    robot = robot.neighbour(direction)
                    return
                }
            }
        }
    }

    private fun findRobot(): Vector {
        for (x in 0 until numCols) {
            for (y in 0 until numRows) {
                val vec = Vector(x, y)
                if (charAt(vec) == ROBOT)
                    return vec
            }
        }

        throw IllegalStateException()
    }

    fun gpsSum(): Long {
        var sum = 0L

        for (x in 0 until numCols) {
            for (y in 0 until numRows) {
                val vec = Vector(x, y)
                if (charAt(vec) == BOX) {
                    sum += (100 * y) + x
                }
            }
        }

        return sum
    }

    private fun move(from: Vector, to: Vector) {
        val c = charAt(from)
        data[to.y][to.x] = c
    }

    private fun charAt(position: Vector): Char {
        return data[position.y][position.x]
    }

    private fun withinBounds(position: Vector): Boolean {
        return (position.x in 0 until numCols) && (position.y in 0 until numRows)
    }

    override fun toString(): String = buildString {
        for (y in 0 until numRows) {
            for (x in 0 until numCols) {
                append(charAt(Vector(x, y)))
            }
            appendLine()
        }
    }
}


fun main() {
    val input = File("data/day15_input.txt")
        .readLines()

    val gridInput = input.takeWhile { it.startsWith('#') }
    val grid = Grid(gridInput)

    val moves = input
        .drop(gridInput.size + 1)
        .joinToString(separator = "")
        .map { c ->
            Direction.fromChar(c)
        }

    for (move in moves) {
        grid.tryMove(move)
    }

    println(grid)

    println(grid.gpsSum())
}