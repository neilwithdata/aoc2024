import java.io.File
import kotlin.math.abs

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

val Vector.toLeft: Vector get() = this.neighbour(Direction.LEFT)
val Vector.toRight: Vector get() = this.neighbour(Direction.RIGHT)

val Direction.isVertical get() = this == Direction.UP || this == Direction.DOWN

class Grid(input: List<String>) {
    private val BOX_LEFT = '['
    private val BOX_RIGHT = ']'
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
        val toMove = findItemsToMove(direction)

        val canMove = toMove.all { item ->
            charAt(item.neighbour(direction)) != WALL
        }

        if (canMove) {
            val sorted = toMove
                .toList()
                .sortedByDescending { item ->
                    val delta = item - robot

                    if (direction.isVertical) {
                        abs(delta.y)
                    } else {
                        abs(delta.x)
                    }

                }

            sorted.forEach {
                move(it, direction)
            }

            robot = findRobot()
        }
    }

    // Returns the set of items that would have to move if we attempted a move of the robot in the given direction
    private fun findItemsToMove(direction: Direction): Set<Vector> {
        val toMove = mutableSetOf(robot)
        val toProject = mutableListOf(robot)

        while (toProject.isNotEmpty()) {
            val curr = toProject.removeFirst()
            val projected = curr.neighbour(direction)

            when (charAt(projected)) {
                WALL, EMPTY -> {
                    // Projected into a wall or empty space - nothing to add
                }

                BOX_LEFT -> {
                    toMove += projected
                    toMove += projected.toRight

                    toProject += projected.toRight
                    if (direction.isVertical) {
                        toProject += projected
                    }
                }

                BOX_RIGHT -> {
                    toMove += projected
                    toMove += projected.toLeft

                    toProject += projected.toLeft
                    if (direction.isVertical) {
                        toProject += projected
                    }
                }
            }
        }

        return toMove
    }

    fun gpsSum(): Long {
        var sum = 0L

        for (x in 0 until numCols) {
            for (y in 0 until numRows) {
                val vec = Vector(x, y)
                if (charAt(vec) == BOX_LEFT) {
                    sum += (100 * y) + x
                }
            }
        }

        return sum
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

    private fun move(from: Vector, direction: Direction) {
        val c = charAt(from)

        val to = from.neighbour(direction)
        data[to.y][to.x] = c

        data[from.y][from.x] = EMPTY
    }

    private fun charAt(position: Vector): Char {
        return data[position.y][position.x]
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

    val gridInput = input
        .takeWhile { it.startsWith('#') }
        .expand()

    val grid = Grid(gridInput)
    println(grid)

    val moves = input
        .drop(gridInput.size + 1)
        .joinToString(separator = "")
        .map { c ->
            Direction.fromChar(c)
        }

    for (move in moves) {
        grid.tryMove(move)
    }

    println(grid.gpsSum())
}

private fun List<String>.expand(): List<String> =
    this.map { line ->
        line.map { c ->
            when (c) {
                '#' -> "##"
                'O' -> "[]"
                '.' -> ".."
                '@' -> "@."
                else -> throw IllegalStateException("invalid input")
            }
        }.joinToString(separator = "")
    }