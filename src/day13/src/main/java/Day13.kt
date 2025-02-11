import java.io.File
import kotlin.math.floor

fun Double.isWholeNumber() = this == floor(this)

// Simple system of linear equations representation
// [[a, b], [c, d]]*[a, b] = [b11, b12]
data class Problem(
    val a11: Int, // a
    val a12: Int, // b
    val a21: Int, // c
    val a22: Int, // d
    val b11: Long,
    val b21: Long
) {
    // matrix is always invertible and rows are linearly independent
    fun solve(): Pair<Double, Double> {
        val determinant = (a11 * a22 - a12 * a21).toDouble()

        val aCount = (a22 * b11 - a12 * b21) / determinant
        val bCount = (-a21 * b11 + a11 * b21) / determinant

        return aCount to bCount
    }
}

fun main() {
    val input = File("data/day13_input.txt")
        .readLines()

    // Part 1
    val problems = readProblems(input)
    println(tokenCount(problems))

    // Part 2
    val newProblems = problems.map {
        it.copy(
            b11 = it.b11 + 10000000000000L,
            b21 = it.b21 + 10000000000000L
        )
    }
    println(tokenCount(newProblems))
}

private fun tokenCount(problems: List<Problem>): Long {
    var tokenCost = 0L

    for (problem in problems) {
        val (aPresses, bPresses) = problem.solve()

        if (listOf(aPresses, bPresses).all { it.isWholeNumber() && it >= 0.0 }) {
            tokenCost += (aPresses * 3 + bPresses).toLong()
        }
    }

    return tokenCost
}

private fun readProblems(input: List<String>): List<Problem> {
    val problems = mutableListOf<Problem>()

    val buttonRegex = """[X|Y]\+(\d+)""".toRegex()
    val prizeRegex = """Prize: X=(\d+), Y=(\d+)""".toRegex()

    var lineIndex = 0
    while (lineIndex <= input.lastIndex) {
        val aInput = input[lineIndex++]
        val bInput = input[lineIndex++]
        val prizeInput = input[lineIndex++]

        val (a11, a21, a12, a22) = buttonRegex.findAll(aInput + bInput)
            .map { it.groupValues[1] }
            .toList()

        val (b11, b21) = prizeRegex.find(prizeInput)!!.destructured

        problems += Problem(
            a11 = a11.toInt(),
            a12 = a12.toInt(),
            a21 = a21.toInt(),
            a22 = a22.toInt(),
            b11 = b11.toLong(),
            b21 = b21.toLong()
        )

        lineIndex++
    }

    return problems
}