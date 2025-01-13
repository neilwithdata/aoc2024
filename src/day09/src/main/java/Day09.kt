import java.io.File

class Disk(diskMap: String) {
    private val data: ShortArray = initialize(diskMap)

    private fun initialize(diskMap: String): ShortArray {
        val arrayLength = diskMap.sumOf {
            it.digitToInt()
        }

        val data = ShortArray(arrayLength)

        var currFileId: Short = 0
        var currDataIndex = 0
        var isFile = true
        for (c in diskMap) {
            val size = c.digitToInt()
            val idToWrite = if (isFile) currFileId else FREE

            repeat(size) {
                data[currDataIndex] = idToWrite
                currDataIndex++
            }

            if (isFile) currFileId++
            isFile = !isFile
        }

        return data
    }

    private fun getNextFreeIndex(start: Int): Int {
        var curr = start
        while (curr <= data.lastIndex) {
            if (data[curr] == FREE) {
                return curr
            }
            curr++
        }

        return -1
    }

    private fun getNextFileIndex(start: Int): Int {
        var curr = start
        while (curr >= 0) {
            if (data[curr] != FREE) {
                return curr
            }
            curr--
        }

        return -1
    }

    fun compact() {
        while (true) {
            val freeIndex = getNextFreeIndex(0)
            val fileIndex = getNextFileIndex(data.lastIndex)

            if (freeIndex == -1 || fileIndex == -1 || freeIndex >= fileIndex) {
                return // finished compacting
            }

            data[freeIndex] = data[fileIndex]
            data[fileIndex] = FREE
        }
    }

    fun calculateChecksum(): Long {
        return data.withIndex().sumOf { (index: Int, value: Short) ->
            if (value == FREE) 0L else (index * value).toLong()
        }
    }

    override fun toString(): String {
        return data.joinToString(separator = "") { n ->
            if (n == FREE) "." else n.toString()
        }
    }

    companion object {
        private const val FREE = (-1).toShort()
    }
}

fun main() {
    val input = File("data/day09_input.txt")
        .readLines()
        .first()

    val disk = Disk(input)
    disk.compact()
    println(disk.calculateChecksum())
}




