import Grid.Node.Type
import java.io.File

enum class Direction(val vector: Vector) {
    UP(Vector(0, -1)),
    DOWN(Vector(0, 1)),
    LEFT(Vector(-1, 0)),
    RIGHT(Vector(1, 0));
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

// path contains a collection of visited nodes and accumulated cost
class Path {
    private val visited = mutableListOf<Entry>()
    var cost = 0

    fun add(position: Vector, direction: Direction) {
        visited.add(Entry(position, direction))
    }

    operator fun contains(entry: Entry): Boolean = entry in visited

    val currentEntry
        get() = visited.last()

    data class Entry(
        val position: Vector,
        val direction: Direction,
    )
}

val Vector.toLeft: Vector get() = this.neighbour(Direction.LEFT)
val Vector.toRight: Vector get() = this.neighbour(Direction.RIGHT)
val Vector.above: Vector get() = this.neighbour(Direction.UP)
val Vector.below: Vector get() = this.neighbour(Direction.DOWN)

class Grid(input: List<String>) {
    private val numRows = input.size
    private val numCols = input[0].length

    private val data = MutableList(numRows) { row ->
        MutableList(numCols) { col ->
            Node(input[row][col])
        }
    }

    private val startPosition: Vector = findFirst(Type.START)
    private val endPosition: Vector = findFirst(Type.END)

    private val paths = mutableListOf<Path>()

    init {
        paths.add(
            Path().apply {
                add(startPosition, Direction.RIGHT)
            }
        )
    }

    private fun findLowestCostPath(): Int {
        var lowestCost: Int? = null

        while (paths.isNotEmpty()) {
            val path = paths.removeFirst()
            val currentEntry = path.currentEntry

            // Is this path at the end?
            if (currentEntry.position == endPosition) {
                // Path is at the end position - track the lowest cost
                if (path.cost < (lowestCost ?: Int.MAX_VALUE)) {
                    lowestCost = path.cost
                }
                continue
            }

            // Is this path in a position that another path has already been in
            // for a lower cost?
            val node = nodeAt(currentEntry.position)
            val nodeCost = node.costs[currentEntry.direction]!!

            if (path.cost < nodeCost || nodeCost == -1) {
                // This is either the first or lowest cost path to come this way
                // Kill other paths that have come this way


                // Figure out what is the next steps for this path (if any)
                // do we need to fork this path?
                // have we already been here?
                // what possible directions, etc.

                // perform the step - update path
                // update costs
                // re-add the path to list of paths to continue
            }
        }

        return 0
    }

    private fun findFirst(type: Type): Vector {
        for (x in 0 until numCols) {
            for (y in 0 until numRows) {
                val vec = Vector(x, y)
                if (nodeAt(vec).type == type) {
                    return vec
                }
            }
        }

        throw IllegalStateException()
    }

    private fun nodeAt(position: Vector): Node {
        return data[position.y][position.x]
    }

    override fun toString(): String = buildString {
        for (y in 0 until numRows) {
            for (x in 0 until numCols) {
                val node = nodeAt(Vector(x, y))
                append(node.type.c)
            }
            appendLine()
        }
    }

    class Node(c: Char) {
        val type = Type.fromChar(c)

        val costs = mutableMapOf(
            Direction.UP to -1,
            Direction.RIGHT to -1,
            Direction.DOWN to -1,
            Direction.LEFT to -1
        )

        enum class Type(val c: Char) {
            EMPTY('.'),
            WALL('#'),
            START('S'),
            END('E');

            companion object {
                fun fromChar(c: Char): Type =
                    Type.entries.first {
                        it.c == c
                    }
            }
        }
    }
}

fun main() {
    val input = File("data/day16_testing.txt")
        .readLines()

    val grid = Grid(input)
    println(grid)
}