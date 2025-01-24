import java.io.File

fun main() {
    var stones = File("data/day11_input.txt")
        .readLines()[0]
        .split(" ")
        .map {
            it.toLong()
        }

    repeat(25) {
        stones = blink(stones)
    }

    println(stones.size)
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