import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested

class Day4 {
    inner class Logic(input: List<String>) {
        val map = input.flatMapIndexed{ y, line ->
            line.mapIndexed{ x, c -> Point2D(x, y) to c }
        }.toMap()

        fun solvePart1():Int {
            // Mapping out how to spell it
            val steps = listOf(1 to 'M', 2 to 'A', 3 to 'S')

            // Find all X, scan all directions, count any hits and sum them for all Xs
            return map.filter { (_, c) -> c == 'X' }.map { (pos, _) ->
                (Point2D.ALLDIRECTIONS).count { direction ->
                    steps.all { map[pos.move(direction, it.first)] == it.second }
                }
            }.sum()
        }
        fun solvePart2():Int {
            val validSets = setOf(
                listOf('M', 'M', 'S', 'S'),
                listOf('S', 'M', 'M', 'S'),
                listOf('S', 'S', 'M', 'M'),
                listOf('M', 'S', 'S', 'M'),
                )
            // find the A's, check the surrounding, count valid X's
            return map.filter { (_, c) -> c == 'A' }.count { (pos, _) ->
                Point2D.DIRECTIONSDIAG.map { map[pos.move(it)] ?:' ' } in validSets
            }
        }
    }

    @Nested
    inner class TestCases {

        val testInput = """
MMMSXXMASM
MSAMXMSMSA
AMXSXMAAMM
MSAMASMSMX
XMASAMXAMM
XXAMMXXAMA
SMSMSASXSS
SAXAMASAAA
MAMMMXMMMM
MXMXAXMASX
    """.trimIndent().lines()
        val testInput2 = """
M.S
.A.
S.M
    """.trimIndent().lines()

        val realInput = Resources.resourceAsList("day4.txt")
        @Test
        fun `Part 1 Example`() {
            val answer = Logic(testInput).solvePart1()
            assertThat(answer).isEqualTo(18)
        }
        @Test
        fun `Part 1 Answer`() {
            val answer = Logic(realInput).solvePart1()
            assertThat(answer).isEqualTo(2578)
        }
        @Test
        fun `Part 2 Example`() {
            val answer = Logic(testInput).solvePart2()
            assertThat(answer).isEqualTo(9)
        }
        @Test
        fun `Part 2 Answer`() {
            val answer = Logic(realInput).solvePart2()
            assertThat(answer).isEqualTo(1972)
        }
    }

}