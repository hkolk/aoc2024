import Point2D.Companion.leftTurn
import Point2D.Companion.rightTurn
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested
import java.util.PriorityQueue

class Day16 {
    data class Move(val pos: Point2D, val direction: DIRECTION, val cost:Int, val history: Set<Point2D>) {
        override fun toString(): String {
            return "Move($pos, ${direction.nice()}, cost=$cost)"
        }
    }

    inner class Logic(input: List<String>) {
        val map = input.flatMapIndexed { y, line ->
            line.mapIndexed { x, c -> Point2D(x, y) to c }
        }.toMap()


        fun solve():Pair<Int, Int> {
            val start = map.filter { it.value == 'S' }.keys.first()
            val end = map.filter { it.value == 'E' }.keys.first()

            val bestPathNodes = mutableSetOf<Point2D>()
            val queue = PriorityQueue<Move>(compareBy {  it.cost })
            queue.add(Move(start, Point2D.EAST, 0, setOf(start)))
            val moveHistory = mutableSetOf<Pair<Point2D, DIRECTION>>()
            var cheapest = Integer.MAX_VALUE

            while (queue.isNotEmpty()) {
                val current = queue.poll()
                moveHistory.add(current.pos to current.direction)
                if(current.pos == end) {
                    bestPathNodes.addAll(current.history)
                    cheapest = current.cost

                }
                val newMoves = listOf(
                    Move(current.pos, current.direction.rightTurn(), current.cost+1000, current.history),
                    Move(current.pos, current.direction.leftTurn(), current.cost+1000, current.history),
                    Move(current.pos.move(current.direction), current.direction, current.cost+1, current.history+current.pos.move(current.direction)),
                    )
                queue.addAll(newMoves.filter {
                    map[it.pos] != '#' &&
                            !moveHistory.contains(it.pos to it.direction) &&
                            it.cost <= cheapest
                })
            }
            return cheapest to bestPathNodes.count()
        }
        fun solvePart1() = solve().first
        fun solvePart2() = solve().second
    }

    @Nested
    inner class TestCases {

        val testInput = """
#################
#...#...#...#..E#
#.#.#.#.#.#.#.#.#
#.#.#.#...#...#.#
#.#.#.#.###.#.#.#
#...#.#.#.....#.#
#.#.#.#.#.#####.#
#.#...#.#.#.....#
#.#.#####.#.###.#
#.#.#.......#...#
#.#.###.#####.###
#.#.#...#.....#.#
#.#.#.#####.###.#
#.#.#.........#.#
#.#.#.#########.#
#S#.............#
#################
    """.trimIndent().lines()

        val realInput = Resources.resourceAsList("day16.txt")
        @Test
        fun `Part 1 Example`() {
            val answer = Logic(testInput).solvePart1()
            assertThat(answer).isEqualTo(11048)
        }
        @Test
        fun `Part 1 Answer`() {
            val answer = Logic(realInput).solvePart1()
            assertThat(answer).isEqualTo(89460)
        }
        @Test
        fun `Part 2 Example`() {
            val answer = Logic(testInput).solvePart2()
            assertThat(answer).isEqualTo(64)
        }
        @Test
        fun `Part 2 Answer`() {
            val answer = Logic(realInput).solvePart2()
            assertThat(answer).isEqualTo(504)
        }
    }

}