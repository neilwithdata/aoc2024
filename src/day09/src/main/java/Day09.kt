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

class FileDisk(diskMap: String) {
    private val fileMap: MutableMap<Int, IntRange> = mutableMapOf()
    private val freeList: MutableList<IntRange> = mutableListOf()

    init {
        var currFileId = 0
        var currDataIndex = 0
        var isFile = true
        for (c in diskMap) {
            val size = c.digitToInt()

            val range = currDataIndex until (currDataIndex + size)

            if (isFile) {
                fileMap[currFileId] = range
                currFileId++
            } else {
                freeList += range
            }

            isFile = !isFile
            currDataIndex = range.last + 1
        }
    }

    private fun findFree(minSize: Int, before: Int): Int {
        for ((index, block) in freeList.withIndex()) {
            if (block.count() >= minSize && block.last < before) {
                return index
            }
        }

        return -1
    }

    fun print() {
        println("Files")
        for ((file, range) in fileMap) {
            println("$file: $range")
        }

        println("Free")
        for (range in freeList) {
            println("$range")
        }
    }

    fun compact() {
        var currFile = fileMap.maxOf { it.key }
        while (currFile >= 0) {
            val file = fileMap[currFile]!!
            val fileSize = file.count()

            val freeIndex = findFree(fileSize, file.first)

            if (freeIndex != -1) {
                val freeBlock = freeList[freeIndex]

                // Update file location
                fileMap[currFile] = freeBlock.first until (freeBlock.first + fileSize)

                // Free block needs to be deleted or reduced in size
                if (freeBlock.count() == fileSize) {
                    freeList.removeAt(freeIndex)
                } else {
                    freeList[freeIndex] = (freeBlock.first + fileSize)..freeBlock.last
                }
            }

            currFile--
        }
    }

    fun calculateChecksum(): Long {
        var sum = 0L

        for ((fileId, range) in fileMap) {
            for (index in range) {
                sum += fileId * index
            }
        }

        return sum
    }
}

fun main() {
    val input = File("data/day09_input.txt")
        .readLines()
        .first()

    // Part 1
    val disk = Disk(input)
    disk.compact()
    println(disk.calculateChecksum())

    // Part 2
    val disk2 = FileDisk(input)
    disk2.compact()
    println(disk2.calculateChecksum())
}




