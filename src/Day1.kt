import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested
import kotlin.math.absoluteValue

class Day1 {
    inner class Logic(input: List<String>) {
        private val result  = input.map { it.splitIgnoreEmpty(" ").map { it.toInt() }.let { it[0] to it[1] } }
        private val left = result.map { it.first }.sorted()
        private val right = result.map { it.second }.sorted()

        fun solvePart1():Int =
            left.zipAll(right).sumOf { (it.first!! - it.second!!).absoluteValue }

        fun solvePart2():Int =
            left.sumOf { leftVal -> right.count { it == leftVal } * leftVal }
    }

    @Nested
    inner class TestCases {

        val testInput = """
3   4
4   3
2   5
1   3
3   9
3   3
    """.trimIndent().lines()

        val realInput = Resources.resourceAsList("day1.txt")
        @Test
        fun `Part 1 Example`() {
            val answer = Logic(testInput).solvePart1()
            assertThat(answer).isEqualTo(11)
        }
        @Test
        fun `Part 1 Answer`() {
            val answer = Logic(realInput).solvePart1()
            assertThat(answer).isEqualTo(2086478)
        }
        @Test
        fun `Part 2 Example`() {
            val answer = Logic(testInput).solvePart2()
            assertThat(answer).isEqualTo(31)
        }
        @Test
        fun `Part 2 Answer`() {
            val answer = Logic(realInput).solvePart2()
            assertThat(answer).isEqualTo(24941624)
        }
    }

}