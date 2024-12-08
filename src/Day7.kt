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
            if(acc > expected) return false // optimise

            if(left.isEmpty()) {
                return acc == expected
            }
            val multiply = findEquations(expected, left.drop(1), acc * left.first(), concat)
            if(multiply) return true
            val addition = findEquations(expected, left.drop(1), acc + left.first(), concat)
            if(addition) return true
            val concatOutcome = concat && findEquations(
                expected,
                left.drop(1),
                acc.concatenate(left.first()),
                true)
            return concatOutcome
        }

        fun solvePart1():Long {
            return equations.sumOf {
                if (findEquations(it.first, it.second, 0L)) {
                    it.first
                } else {
                    0
                }
            }
        }
        fun solvePart2():Long {
            return equations.pmap {
                if (findEquations(it.first, it.second, 0L, true)) {
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
            assertThat(answer).isEqualTo(37598910447546)
        }
        @Test
        fun testConcat() {
            assertThat((12L).concatenate(34L)).isEqualTo(1234)
        }
    }

}