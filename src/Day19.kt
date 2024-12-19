import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested

class Day19 {

    inner class Logic(input: List<String>) {
        val towels = input.first().splitIgnoreEmpty(", ")
        val patterns = input.drop(2)

        val recurseCache = FunctionCache(::recursePattern)

        fun recursePattern(pattern: String): Long {
            if(pattern.isEmpty()) {
                return 1
            }
            return towels.sumOf { towel ->
                if(pattern.startsWith(towel)) {
                    recurseCache(pattern.drop(towel.length))
                } else {
                    0
                }
            }
        }

        fun solvePart1() = patterns.count { recurseCache(it) > 0 }
        fun solvePart2() = patterns.sumOf { recurseCache(it) }
    }

    @Nested
    inner class TestCases {

        val testInput = """
r, wr, b, g, bwu, rb, gb, br

brwrr
bggr
gbbr
rrbgbr
ubwu
bwurrg
brgr
bbrgwb
    """.trimIndent().lines()

        val realInput = Resources.resourceAsList("day19.txt")
        @Test
        fun `Part 1 Example`() {
            val answer = Logic(testInput).solvePart1()
            assertThat(answer).isEqualTo(6)
        }
        @Test
        fun `Part 1 Answer`() {
            val answer = Logic(realInput).solvePart1()
            assertThat(answer).isEqualTo(213)
        }
        @Test
        fun `Part 2 Example`() {
            val answer = Logic(testInput).solvePart2()
            assertThat(answer).isEqualTo(16)
        }
        @Test
        fun `Part 2 Answer`() {
            val answer = Logic(realInput).solvePart2()
            assertThat(answer).isEqualTo(1016700771200474L)
        }
    }

}