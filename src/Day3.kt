import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested

class Day3 {
    inner class Logic(val input: List<String>) {

        fun multiply(area: String): Int {
            val re = """mul\((\d{1,3}),(\d{1,3})\)""".toRegex()
            val matches = re.findAll(area)
            return matches.sumOf { it.groupValues[1].toInt() * it.groupValues[2].toInt() }
        }
        fun solvePart1():Int = multiply(input.joinToString(""))
        fun solvePart2():Int {
            val re = """(^|do\(\)).+?(?=don't\(\)|$)""".toRegex()
            val matches = re.findAll(input.joinToString(""))
            return matches.sumOf{ multiply(it.value)}
        }
    }

    @Nested
    inner class TestCases {

        val testInput = """
xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))
    """.trimIndent().lines()
        val testInput2 = """
xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))
        """.trimIndent().lines()

        val realInput = Resources.resourceAsList("day3.txt")
        @Test
        fun `Part 1 Example`() {
            val answer = Logic(testInput).solvePart1()
            assertThat(answer).isEqualTo(161)
        }
        @Test
        fun `Part 1 Answer`() {
            val answer = Logic(realInput).solvePart1()
            assertThat(answer).isEqualTo(196826776)
        }
        @Test
        fun `Part 2 Example`() {
            val answer = Logic(testInput2).solvePart2()
            assertThat(answer).isEqualTo(48)
        }
        @Test
        fun `Part 2 Answer`() {
            val answer = Logic(realInput).solvePart2()
            assertThat(answer).isEqualTo(106780429)
        }
    }

}