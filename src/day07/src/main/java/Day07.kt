import java.io.File

class Equation(
    val result: Long,
    private val numbers: List<Long>
) {
    enum class Operator {
        ADD,
        MULTIPLY,
        CONCATENATION
    }

    data class Node(
        val operator: Operator,
        val depth: Int,
        val parent: Node?
    ) {
        var result: Long = 0L
    }

    fun isTrue(): Boolean {
        // Quick tree traversal approach (building the tree as we go / ignoring branches without potential)
        val stack = ArrayDeque<Node>()
        stack.add(Node(Operator.ADD, 0, null))
        stack.add(Node(Operator.MULTIPLY, 0, null))
        stack.add(Node(Operator.CONCATENATION, 0, null))

        while (stack.isNotEmpty()) {
            val node = stack.removeFirst()

            val parentValue = if (node.parent == null) numbers[0] else node.parent.result

            // Evaluate
            node.result = when (node.operator) {
                Operator.ADD -> parentValue + numbers[node.depth + 1]
                Operator.MULTIPLY -> parentValue * numbers[node.depth + 1]
                Operator.CONCATENATION -> "${parentValue}${numbers[node.depth + 1]}".toLong()
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

            stack.add(
                Node(
                    operator = Operator.CONCATENATION,
                    depth = node.depth + 1,
                    parent = node
                )
            )
        }

        return false
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