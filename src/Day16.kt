import Point2D.Companion.leftTurn
import Point2D.Companion.rightTurn
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested
import java.util.PriorityQueue

fun DIRECTION.nice(): String {
    return when(this) {
        Point2D.NORTH -> "NORTH"
        Point2D.WEST -> "WEST"
        Point2D.SOUTH -> "SOUTH"
        Point2D.EAST -> "EAST"
        else -> throw IllegalArgumentException("Cannot map")
    }
}

class Day16 {
    data class Move(val pos: Point2D, val direction: DIRECTION, val cost:Int) {
        override fun toString(): String {
            return "Move($pos, ${direction.nice()}, cost=$cost)"
        }
    }

    inner class Logic(input: List<String>) {
        val map = input.flatMapIndexed { y, line ->
            line.mapIndexed { x, c -> Point2D(x, y) to c }
        }.toMap()


        fun solvePart1():Int {
            val start = map.filter { it.value == 'S' }.keys.first()
            val end = map.filter { it.value == 'E' }.keys.first()

            val queue = PriorityQueue<Move>(compareBy {  it.cost })
            queue.add(Move(start, Point2D.EAST, 0))
            val moveHistory = mutableSetOf<Pair<Point2D, DIRECTION>>()
            var i = 0
            while (queue.isNotEmpty()) {
                val current = queue.poll()
                moveHistory.add(current.pos to current.direction)
                if(i++ < 10) {
                    println(current)
                }
                if(current.pos == end) {
                    // end state
                    return current.cost
                }
                val newMoves = listOf(
                    Move(current.pos, current.direction.rightTurn(), current.cost+1000),
                    Move(current.pos, current.direction.leftTurn(), current.cost+1000),
                    Move(current.pos.move(current.direction), current.direction, current.cost+1),
                    )
                queue.addAll(newMoves.filter { map[it.pos] != '#' && !moveHistory.contains(it.pos to it.direction) })
                /*val newMoves = Point2D.DIRECTIONS.mapNotNull { dir ->
                    val next = current.pos.move(dir)
                    if(map[next]!='#') {
                        val extraCost = if(dir == current.direction) 1 else 1001
                        Move(next, dir, current.cost+extraCost)
                    } else {
                        null
                    }
                }
                queue.addAll(newMoves)
                 */
            }
            TODO()
            return 0
        }
        fun solvePart2():Int {
            return 0
        }
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
            assertThat(answer).isEqualTo(0)
        }
        @Test
        fun `Part 2 Answer`() {
            val answer = Logic(realInput).solvePart2()
            assertThat(answer).isEqualTo(0)
        }
    }

}