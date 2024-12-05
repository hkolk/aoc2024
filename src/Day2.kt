import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested
import kotlin.math.absoluteValue

class Day2 {
    inner class Logic(input: List<String>) {
        private val reports = input.map { it.splitIgnoreEmpty(" ").map { it.toInt() } }

        private fun validateReport(report: List<Int>): Boolean {
            val diffs = report.windowed(2, 1).map { it[0] - it[1] }
            return diffs.all { (it in 1..3) } || diffs.all{(it in -3..-1) }
        }

        fun solvePart1(): Int = reports.count { validateReport(it) }
        fun solvePart2(): Int = reports.count { report ->
            report.combinations(report.size - 1).any { validateReport(it) }
        }
    }

    @Nested
    inner class TestCases {

        val testInput = """
7 6 4 2 1
1 2 7 8 9
9 7 6 2 1
1 3 2 4 5
8 6 4 4 1
1 3 6 7 9
    """.trimIndent().lines()

        val realInput = Resources.resourceAsList("day2.txt")
        @Test
        fun `Part 1 Example`() {
            val answer = Logic(testInput).solvePart1()
            assertThat(answer).isEqualTo(2)
        }
        @Test
        fun `Part 1 Answer`() {
            val answer = Logic(realInput).solvePart1()
            assertThat(answer).isEqualTo(660)
        }
        @Test
        fun `Part 2 Example`() {
            val answer = Logic(testInput).solvePart2()
            assertThat(answer).isEqualTo(4)
        }
        @Test
        fun `Part 2 Answer`() {
            val answer = Logic(realInput).solvePart2()
            assertThat(answer).isEqualTo(689)
        }
    }

}