import Point2D.Companion.rightTurn
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested

class Day6 {
    inner class Logic(input: List<String>) {
        val startmap = input.flatMapIndexed { y, line ->
            line.mapIndexed { x: Int, c: Char ->
                Point2D(x, y) to c
            }
        }.toMap()

        fun loopCheck(map: Map<Point2D, Char>): Pair<Boolean, Int> {
            var loc = map.filter { it.value == '^' }.keys.first()
            val collect = mutableSetOf<Pair<Point2D, DIRECTION>>()
            var direction = Point2D.NORTH
            while(loc in map.keys) {
                if(collect.contains(loc to direction)) {
                    return true to 0
                }
                collect += loc to direction
                loc = loc.move(direction)
                while(map[loc.move(direction)] == '#') {
                    direction = direction.rightTurn()
                }
            }
            //collect.map { it.first }.print()
            return false to collect.map { it.first }.toSet().size
        }

        fun solvePart1():Int = loopCheck(startmap).second
        fun solvePart2():Int {
            return startmap.filter { it.value == '.' }.keys.toList().pmap { replace ->
                val newMap = startmap.filterNot { it.key == replace } + (replace to '#')
                loopCheck(newMap).first
            }.count { it }
        }
    }

    @Nested
    inner class TestCases {

        val testInput = """
....#.....
.........#
..........
..#.......
.......#..
..........
.#..^.....
........#.
#.........
......#...
    """.trimIndent().lines()

        val realInput = Resources.resourceAsList("day6.txt")
        @Test
        fun `Part 1 Example`() {
            val answer = Logic(testInput).solvePart1()
            assertThat(answer).isEqualTo(41)
        }
        @Test
        fun `Part 1 Answer`() {
            val answer = Logic(realInput).solvePart1()
            assertThat(answer).isEqualTo(4789)
        }
        @Test
        fun `Part 2 Example`() {
            val answer = Logic(testInput).solvePart2()
            assertThat(answer).isEqualTo(6)
        }
        @Test
        fun `Part 2 Answer`() {
            val answer = Logic(realInput).solvePart2()
            assertThat(answer).isEqualTo(0)
        }
    }

}