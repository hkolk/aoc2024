import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested

class Day11 {
    inner class Logic(input: List<String>) {
        val initial = input.first().splitIgnoreEmpty(" ").map { it.toLong() }

        fun solvePart1_old():Int {
            var newStones = initial
            for(blink in 1..25) {
                newStones = newStones.flatMap { stone ->
                    if (stone == 0L) {
                        listOf(1L)
                    } else if ("$stone".length % 2 == 0) {
                        val strStone = "$stone"
                        listOf(
                            strStone.take(strStone.length / 2).toLong(),
                            strStone.takeLast(strStone.length / 2).toLong()
                        )
                    } else {
                        listOf(stone * 2024)
                    }

                }
            }
            return newStones.count()
        }
        var cache = mutableMapOf<Pair<Long, Int>, Long>()
        fun recurse(stone:Long, depth:Int): Long{
            cache[(stone to depth)].let {
                if(it != null) {
                    return it
                }
            }
            if(depth == 0) return 1

            val retVal = if (stone == 0L) {
                recurse(1L, depth-1)
            } else if ("$stone".length % 2 == 0) {
                val strStone = "$stone"
                listOf(
                    recurse(strStone.take(strStone.length / 2).toLong(), depth-1),
                    recurse(strStone.takeLast(strStone.length / 2).toLong(), depth-1)
                ).sum()
            } else {
                recurse(stone * 2024, depth-1)
            }
            cache[stone to depth] = retVal
            return retVal
        }

        fun solvePart1():Long  = initial.sumOf { stone -> recurse(stone, 25)}
        fun solvePart2():Long  = initial.sumOf { stone -> recurse(stone, 75)}
    }

    @Nested
    inner class TestCases {

        val testInput = """
125 17
    """.trimIndent().lines()

        val realInput = Resources.resourceAsList("day11.txt")
        @Test
        fun `Part 1 Example`() {
            val answer = Logic(testInput).solvePart1()
            assertThat(answer).isEqualTo(55312)
        }
        @Test
        fun `Part 1 Answer`() {
            val answer = Logic(realInput).solvePart1()
            assertThat(answer).isEqualTo(199753)
        }
        @Test
        fun `Part 2 Example`() {
            val answer = Logic(testInput).solvePart2()
            assertThat(answer).isEqualTo(65601038650482L)
        }
        @Test
        fun `Part 2 Answer`() {
            val answer = Logic(realInput).solvePart2()
            assertThat(answer).isEqualTo(239413123020116L)
        }
    }

}