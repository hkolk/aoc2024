import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested

class Day10 {
    inner class Logic(input: List<String>) {
        val topoMap = input.flatMapIndexed{ y, line ->
            line.mapIndexed{ x, c -> Point2D(x, y) to (c.digitToIntOrNull()?:-1)}
        }.toMap()

        fun findTrial(start: Point2D): List<Point2D> {
            if(topoMap[start] == 9) {
                return listOf(start)
            }
            return start.adjacent().filter {
                topoMap.containsKey(it)
            }.filter {
                topoMap[it] == topoMap[start]!! + 1
            }.flatMap{findTrial(it)}.toList()
        }

        fun solvePart1() = topoMap.filter { it.value == 0 }
            .keys.sumOf{findTrial(it).toSet().count()}

        fun solvePart2() = topoMap.filter { it.value == 0 }
            .keys.sumOf{findTrial(it).count()}
    }

    @Nested
    inner class TestCases {

        val testInput = """
89010123
78121874
87430965
96549874
45678903
32019012
01329801
10456732
    """.trimIndent().lines()

        val realInput = Resources.resourceAsList("day10.txt")
        @Test
        fun `Part 1 Example`() {
            val answer = Logic(testInput).solvePart1()
            assertThat(answer).isEqualTo(36)
        }
        @Test
        fun `Part 1 Answer`() {
            val answer = Logic(realInput).solvePart1()
            assertThat(answer).isEqualTo(593)
        }
        @Test
        fun `Part 2 Example`() {
            val answer = Logic(testInput).solvePart2()
            assertThat(answer).isEqualTo(81)
        }
        @Test
        fun `Part 2 Answer`() {
            val answer = Logic(realInput).solvePart2()
            assertThat(answer).isEqualTo(1192)
        }
    }

}