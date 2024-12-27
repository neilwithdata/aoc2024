import java.io.File

class Equation(
    val result: Long,
    val numbers: List<Long>
) {
    enum class Operator {
        ADD,
        MULTIPLY,
    }

    data class Node(
        val operator: Operator,
        val depth: Int,
        val parent: Node?
    ) {
        var result: Long = 0L
    }

    // Perform a fast rough check if result is within (min, max) bounds
    private fun fastBoundsCheck(): Boolean {
        var max = numbers[0]
        var min = numbers[0]
        for (n in numbers.drop(1)) {
            if (max == 1L || n == 1L) {
                max += n
                min *= n
            } else {
                max *= n
                min += n
            }
        }

        return !(result > max || result < min)
    }

    fun isTrue(): Boolean {
        if (!fastBoundsCheck()) return false

        // Quick tree traversal approach (building the tree as we go / ignoring branches without potential)
        val stack = ArrayDeque<Node>()
        stack.add(Node(Operator.ADD, 0, null))
        stack.add(Node(Operator.MULTIPLY, 0, null))

        while (stack.isNotEmpty()) {
            val node = stack.removeFirst()

            val parentValue = if (node.parent == null) numbers[0] else node.parent.result

            // Evaluate
            node.result = when (node.operator) {
                Operator.ADD -> parentValue + numbers[node.depth + 1]
                Operator.MULTIPLY -> parentValue * numbers[node.depth + 1]
            }

            // chain of operators already exceeds target - no point evaluating further
            if (node.result > result) continue

            // if winner (depth is right and value is right) - return true
            if (node.depth == numbers.size - 2 && node.result == result) {
                return true
            }

            // Already completed evaluation of full equation
            if (node.depth == numbers.size - 2) continue

            // Add children
            stack.add(
                Node(
                    operator = Operator.ADD,
                    depth = node.depth + 1,
                    parent = node
                )
            )

            stack.add(
                Node(
                    operator = Operator.MULTIPLY,
                    depth = node.depth + 1,
                    parent = node
                )
            )
        }

        return false
    }

    private fun printOperators(node: Node) {
        println(buildString {
            var roving: Node? = node
            while (roving != null) {
                if (roving.operator == Operator.ADD) {
                    append(" + ")
                } else {
                    append(" x ")
                }

                roving = roving.parent
            }
        })
    }

    override fun toString(): String = "$result: ${numbers.joinToString()}"
}

fun main() {
    val equations = File("data/day07_input.txt")
        .readLines()
        .map { line ->
            val result = line.substringBefore(':').toLong()

            val numbers = line.substringAfter(':')
                .split(' ')
                .filter { it.isNotBlank() }
                .map { it.trim().toLong() }

            Equation(result, numbers)
        }

    val total = equations
        .filter { it.isTrue() }
        .sumOf { it.result }

    println(total)
}