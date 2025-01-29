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

    fun neighbours() = buildList {
        Direction.entries.forEach { direction ->
            add(this@Position + direction.vector)
        }
    }
}

class Region(private val id: Char) {
    private val plots = mutableSetOf<Position>()

    fun addPlot(position: Position) {
        plots.add(position)
    }

    operator fun contains(position: Position): Boolean = position in plots

    fun price(): Int = area() * perimeter()

    private fun area(): Int {
        return plots.size
    }

    private fun perimeter(): Int {
        var perimeter = 0

        for (plot in plots) {
            for (neighbour in plot.neighbours()) {
                if (neighbour !in this) {
                    perimeter++
                }
            }
        }

        return perimeter
    }

    override fun toString(): String {
        return "$id: ${plots.joinToString()}"
    }
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

    fun findRegions(): List<Region> {
        val regions = mutableListOf<Region>()

        for (row in 0 until numRows) {
            for (col in 0 until numCols) {
                val position = Position(row, col)

                if (!regions.any { region -> position in region }) {
                    // Position is not already in a captured region - create a new region and fully capture it
                    regions.add(captureRegion(position))
                }
            }
        }

        return regions
    }

    private fun captureRegion(startPosition: Position): Region {
        val id: Char = charAt(startPosition)
        val region = Region(id)

        val stack = mutableListOf(startPosition)
        while (stack.isNotEmpty()) {
            val curr = stack.removeFirst()

            if (charAt(curr) == id && curr !in region) {
                // if right id and not already in region - add it now
                region.addPlot(curr)

                // now add all neighbours within bounds and not already queue'd up for evaluation
                val validNeighbours = curr.neighbours()
                    .filter { withinBounds(it) && it !in stack }

                stack.addAll(validNeighbours)
            }
        }

        return region
    }

    private fun charAt(position: Position): Char =
        data[position.row][position.col]

    private fun withinBounds(position: Position): Boolean =
        withinBounds(position.row, position.col)

    private fun withinBounds(row: Int, col: Int): Boolean =
        (row in 0 until numRows) && (col in 0 until numCols)
}


fun main() {
    val input = File("data/day12_input.txt")
        .readLines()

    val grid = Grid(input)
    val regions = grid.findRegions()

    val total = regions.sumOf { it.price() }
    println(total)
}