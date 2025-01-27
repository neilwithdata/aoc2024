import java.io.File

fun main() {
    val stones = File("data/day11_input.txt")
        .readLines()[0]
        .split(" ")
        .map {
            it.toLong()
        }

    // Part 1
    var first = stones
    repeat(25) {
        first = blink(first)
    }

    println(first.size)

    // Part 2
    var counts = stones.groupingBy { it }
        .eachCount()
        .mapValues { it.value.toLong() }

    repeat(75) {
        counts = updateCounts(counts)
    }

    println(counts.values.sum())
}

fun updateCounts(counts: Map<Long, Long>): Map<Long, Long> {
    val updated = mutableMapOf<Long, Long>()

    fun increaseCount(stone: Long, n: Long) {
        updated[stone] = updated.getOrDefault(stone, 0) + n
    }

    for ((stone, count) in counts) {
        val str = stone.toString()
        val len = str.length

        when {
            stone == 0L -> {
                increaseCount(1L, count)
            }

            len % 2 == 0 -> {
                val leftHalf = str.substring(0 until len / 2)
                val rightHalf = str.substring(len / 2 until len)

                increaseCount(leftHalf.toLong(), count)
                increaseCount(rightHalf.toLong(), count)
            }

            else -> {
                increaseCount(stone * 2024, count)
            }
        }
    }

    return updated.toMap()
}

fun blink(stones: List<Long>): List<Long> {
    return buildList {
        for (stone in stones) {
            val str = stone.toString()
            val len = str.length

            when {
                stone == 0L -> {
                    add(1L)
                }

                len % 2 == 0 -> {
                    val leftHalf = str.substring(0 until len / 2)
                    val rightHalf = str.substring(len / 2 until len)

                    add(leftHalf.toLong())
                    add(rightHalf.toLong())
                }

                else -> {
                    add(stone * 2024)
                }
            }
        }
    }
}