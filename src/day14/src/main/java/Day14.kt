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

    val transforms = File("data/day14_input.txt")
        .readLines()
        .map { line ->
            val (px, py, vx, vy) = inputRegex.find(line)!!.destructured

            Transform(
                position = Position(px.toInt(), py.toInt()),
                velocity = Velocity(vx.toInt(), vy.toInt())
            )
        }.map { initial ->
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
        transforms.count { transform ->
            transform.position.x in quadrant.first && transform.position.y in quadrant.second
        }
    }.reduce { acc, i -> acc * i }

    println(result)
}