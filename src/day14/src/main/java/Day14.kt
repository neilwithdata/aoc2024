import java.io.File

data class Position(val x: Int, val y: Int)

data class Velocity(
    val dx: Int,
    val dy: Int
)

data class Transform(
    val position: Position,
    val velocity: Velocity
)

private const val WIDTH = 101
private const val HEIGHT = 103
private const val SECONDS = 100

fun main() {
    val inputRegex = """p=(\d+),(\d+) v=(-?\d+),(-?\d+)""".toRegex()

    val initialTransforms = File("data/day14_input.txt")
        .readLines()
        .map { line ->
            val (px, py, vx, vy) = inputRegex.find(line)!!.destructured

            Transform(
                position = Position(px.toInt(), py.toInt()),
                velocity = Velocity(vx.toInt(), vy.toInt())
            )
        }

    // Part 1
    val finalTransforms = initialTransforms.map { initial ->
        // Now work out the final position for each robot after SECONDS iterations
        var finalX = (initial.position.x + (SECONDS * initial.velocity.dx)) % WIDTH
        if (finalX < 0) finalX += WIDTH

        var finalY = (initial.position.y + (SECONDS * initial.velocity.dy)) % HEIGHT
        if (finalY < 0) finalY += HEIGHT

        initial.copy(position = Position(finalX, finalY))
    }

    val xLeft = 0 until WIDTH / 2
    val xRight = (WIDTH / 2 + 1) until WIDTH
    val yTop = 0 until HEIGHT / 2
    val yBottom = (HEIGHT / 2 + 1) until HEIGHT

    val quadrants = listOf(
        xLeft to yTop,
        xRight to yTop,
        xLeft to yBottom,
        xRight to yBottom
    )

    val result = quadrants.map { quadrant: Pair<IntRange, IntRange> ->
        finalTransforms.count { transform ->
            transform.position.x in quadrant.first && transform.position.y in quadrant.second
        }
    }.reduce { acc, i -> acc * i }

    println(result)

    // Part 2
    var transforms = initialTransforms
    var seconds = 0
    while (true) {
        seconds++

        transforms = transforms.map { transform ->
            var finalX = (transform.position.x + transform.velocity.dx) % WIDTH
            if (finalX < 0) finalX += WIDTH

            var finalY = (transform.position.y + transform.velocity.dy) % HEIGHT
            if (finalY < 0) finalY += HEIGHT

            transform.copy(position = Position(finalX, finalY))
        }

        if (isCandidate(transforms)) {
            println("State at $seconds seconds:")
            display(transforms)
            println("\n")
        }
    }
}

private fun display(transforms: List<Transform>) {
    val positions = transforms.map { it.position }

    for (row in 0 until HEIGHT) {
        for (col in 0 until WIDTH) {
            val position = Position(x = col, y = row)
            val count = positions.count { it == position }

            print(if (count == 0) '.' else count)
        }
        println()
    }
}

private fun isCandidate(transforms: List<Transform>): Boolean {
    for (x in 0 until WIDTH step 10) {
        for (y in 0 until HEIGHT step 10) {
            val dx = x until (x + 10)
            val dy = y until (y + 10)

            // return the number of robots within this 10x10 section
            val count = transforms
                .count {
                    it.position.x in dx && it.position.y in dy
                }

            if (count > 30) {
                println("High density clustering found at ($x, $y)")
                return true
            }
        }
    }

    return false
}