import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested
import java.util.*

class Day9 {
    data class File(val id:Int, val size:Int, val diskPos:Int)
    data class Free(val diskPos:Int, val size:Int)

    inner class Logic(input: List<String>) {
        private val files = TreeSet<File>(compareBy{ -it.diskPos})
        private val free = TreeSet<Free>(compareBy{ it.diskPos })
        init {
            var diskIndex = 0
            var fileId = 0
            input.first().forEachIndexed { idx, c ->
                if (idx % 2 == 0) {
                    // block
                    files.add(File(fileId, c.digitToInt(), diskIndex))
                    fileId++
                } else {
                    // free
                    free.add(Free(diskIndex , c.digitToInt()))
                }
                diskIndex += c.digitToInt()
            }
        }


        fun solvePart1():Long {

            while(free.isNotEmpty()) {
                val freeSpace = free.pollFirst() ?: throw IllegalStateException()
                // consume from files
                var space = freeSpace.size
                var diskPos = freeSpace.diskPos
                while(space > 0) {
                    val lastFile = files.pollFirst() ?: throw IllegalStateException()
                    if(lastFile.diskPos < freeSpace.diskPos) {
                        files.add(lastFile)
                        space = 0
                        // skip this free space
                    } else if(lastFile.size > space) {
                        // fill the space
                        files.add(lastFile.copy(size=space, diskPos = diskPos))
                        // put back the remainder
                        files.add(lastFile.copy(size=lastFile.size-space))
                        // no need to update diskpos
                        space = 0

                    } else {
                        // consume fully
                        files.add(lastFile.copy(diskPos=diskPos))
                        diskPos += lastFile.size
                        space -= lastFile.size
                    }
                }
            }
            val newFiles = files.toList().sortedBy { it.diskPos }
            return newFiles.sumOf { file ->
                (0..<file.size).sumOf {
                    (file.diskPos + it) * file.id.toLong()
                }
            }

        }
        fun solvePart2():Long {
            val newFiles = files.map { file ->
                //- Take file from reverse files
                //- find a spot with a lower diskpos
                val freeSpot = free.find { it.size >= file.size && it.diskPos < file.diskPos }
                if(freeSpot != null ) {
                    //- if spot: insert into new files with new diskpos, claim space in free spots for leftover
                    free.remove(freeSpot)
                    if(freeSpot.size > file.size) {
                        free.add(Free(diskPos = freeSpot.diskPos+file.size, size=freeSpot.size-file.size))
                    }
                    file.copy(diskPos = freeSpot.diskPos)
                } else {
                    //- if not: insert into new files with old diskpos
                    file
                }
            }.sortedBy{it.diskPos}

            return newFiles.sumOf { file ->
                (0..<file.size).sumOf {
                    (file.diskPos + it) * file.id.toLong()
                }
            }
        }
    }

    @Nested
    inner class TestCases {

        val testInput = """
2333133121414131402
    """.trimIndent().lines()

        val realInput = Resources.resourceAsList("day9.txt")
        @Test
        fun `Part 1 Example`() {
            val answer = Logic(testInput).solvePart1()
            assertThat(answer).isEqualTo(1928)
        }
        @Test
        fun quickPrint() {
            val input = "0099811188827773336446555566"
            input.forEachIndexed { index, c ->
                println("$index * $c")
            }
        }
        @Test
        fun `Part 1 Answer`() {
            val answer = Logic(realInput).solvePart1()
            assertThat(answer).isEqualTo(6344673854800L)
        }
        @Test
        fun `Part 2 Example`() {
            val answer = Logic(testInput).solvePart2()
            assertThat(answer).isEqualTo(2858)
        }
        @Test
        fun `Part 2 Answer`() {
            val answer = Logic(realInput).solvePart2()
            assertThat(answer).isEqualTo(6360363199987L)
        }
    }

}