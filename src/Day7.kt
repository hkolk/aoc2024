import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested

class Day7 {
    inner class Logic(input: List<String>) {
        val equations = input.map { line ->
            line.splitIgnoreEmpty(":", " ").let {
                it.first().toLong() to it.drop(1).map { it.toLong() }
        } }

        fun findEquations(expected: Long, left: List<Long>, acc: Long, concat:Boolean = false) : Boolean {
            if(left.isEmpty()) {
                return acc == expected
            }
            val multiply = findEquations(expected, left.drop(1), acc * left.first(), concat)
            val addition = findEquations(expected, left.drop(1), acc + left.first(), concat)
            val concatOutcome = concat && findEquations(
                expected,
                left.drop(1),
                (acc.toString() + left.first().toString()).toLong(),
                concat)
            return multiply || addition || concatOutcome
        }

        fun solvePart1():Long {
            return equations.map {
                if(findEquations(it.first, it.second, 0L)) {
                    it.first
                } else {
                    0
                }
            }.sum()
        }
        fun solvePart2():Long {
            return equations.map {
                if(findEquations(it.first, it.second, 0L, true)) {
                    it.first
                } else {
                    0
                }
            }.sum()
        }
    }

    @Nested
    inner class TestCases {

        val testInput = """
190: 10 19
3267: 81 40 27
83: 17 5
156: 15 6
7290: 6 8 6 15
161011: 16 10 13
192: 17 8 14
21037: 9 7 18 13
292: 11 6 16 20
    """.trimIndent().lines()

        val realInput = Resources.resourceAsList("day7.txt")
        @Test
        fun `Part 1 Example`() {
            val answer = Logic(testInput).solvePart1()
            assertThat(answer).isEqualTo(3749)
        }
        @Test
        fun `Part 1 Answer`() {
            val answer = Logic(realInput).solvePart1()
            assertThat(answer).isEqualTo(4998764814652L)
        }
        @Test
        fun `Part 2 Example`() {
            val answer = Logic(testInput).solvePart2()
            assertThat(answer).isEqualTo(11387)
        }
        @Test
        fun `Part 2 Answer`() {
            val answer = Logic(realInput).solvePart2()
            assertThat(answer).isEqualTo(0)
        }
    }

}