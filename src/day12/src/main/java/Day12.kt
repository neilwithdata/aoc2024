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

data class Perimeter(
    val position: Position,
    val direction: Direction
)

class Region(private val id: Char) {
    private val plots = mutableSetOf<Position>()

    fun addPlot(position: Position) {
        plots.add(position)
    }

    operator fun contains(position: Position): Boolean = position in plots

    fun price(): Int = area() * findPerimeters().size

    fun newPrice(): Int = area() * sideCount()

    private fun area(): Int {
        return plots.size
    }

    private fun sideCount(): Int {
        val perimeters = findPerimeters().toMutableList()

        val sides = mutableListOf<List<Perimeter>>()
        while (perimeters.isNotEmpty()) {
            val first = perimeters.first()

            val side = captureSide(first, perimeters)
            sides.add(side)

            perimeters.removeAll(side)
        }

        return sides.count()
    }

    private fun captureSide(perimeter: Perimeter, perimeters: List<Perimeter>): List<Perimeter> {
        val walkDirection = if (perimeter.direction in listOf(Direction.NORTH, Direction.SOUTH)) {
            Direction.EAST.vector
        } else {
            Direction.NORTH.vector
        }

        val side = mutableListOf(perimeter)

        // Walk stepVector as far as you can
        fun walk(stepVector: Position) {
            var curr = perimeter.position + stepVector

            while (true) {
                val candidate = Perimeter(curr, perimeter.direction)
                if (candidate in perimeters) {
                    side.add(candidate)
                } else {
                    return
                }

                curr += stepVector
            }
        }

        walk(walkDirection)
        walk(-walkDirection)

        return side
    }

    private fun findPerimeters(): Set<Perimeter> {
        val perimeters = mutableSetOf<Perimeter>()

        for (plot in plots) {
            Direction.entries.forEach { direction ->
                val neighbour = plot + direction.vector

                if (neighbour !in this) {
                    perimeters.add(
                        Perimeter(
                            position = plot,
                            direction = direction
                        )
                    )
                }
            }
        }

        return perimeters
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

    // Part 1
    val total = regions.sumOf { it.price() }
    println(total)

    // Part 2
    val newTotal = regions.sumOf { it.newPrice() }
    println(newTotal)
}