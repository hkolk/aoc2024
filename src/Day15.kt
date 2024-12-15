import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested

class Day15 {
    fun Char.toDirection(): DIRECTION {
        return when(this) {
            '<' -> Point2D.WEST
            '>' -> Point2D.EAST
            '^' -> Point2D.NORTH
            'v' -> Point2D.SOUTH
            else -> throw IllegalArgumentException("Invalid Move")

        }
    }


    inner class Logic(input: List<String>) {
        val smallmap = input.splitBy { it.isEmpty() }.first().flatMapIndexed { y, line ->
            line.mapIndexed { x, c -> Point2D(x, y) to c }
        }.toMap().toMutableMap()

        val widemap = input.splitBy { it.isEmpty() }.first().flatMapIndexed { y, line ->
            line.flatMapIndexed { x, c ->
                when(c) {
                    '#' -> listOf(Point2D(x*2, y) to c, Point2D(x*2+1, y) to c)
                    'O' -> listOf(Point2D(x*2, y) to '[', Point2D(x*2+1, y) to ']')
                    '.' -> listOf(Point2D(x*2, y) to c, Point2D(x*2+1, y) to c)
                    '@' -> listOf(Point2D(x*2, y) to c, Point2D(x*2+1, y) to '.')
                    else -> throw IllegalArgumentException("Unknown char: $c")
                }
            }
        }.toMap().toMutableMap()

        val moves = input.splitBy { it.isEmpty() }.last().joinToString("")

        lateinit var map: MutableMap<Point2D, Char>

        fun recurseWidePush(pos: List<Point2D>, dir: DIRECTION): Boolean {
            if(pos.any { map[it] == '#' }) {
                // any of the future spots has hit a wall
                return false
            } else if(pos.any{ map[it] in setOf('[', ']', 'O') }) {
                // not yet everything has spaces
                val front = if(dir in setOf(Point2D.EAST, Point2D.WEST)) {
                    assert(pos.size == 1)
                    pos.toSet()
                } else {
                    pos.filter {
                        map[it] in setOf('[', ']', 'O')
                    }.flatMap {
                        when(map[it]) {
                            '[' -> listOf(it, it.move(Point2D.EAST))
                            ']' -> listOf(it, it.move(Point2D.WEST))
                            'O' -> listOf(it)
                            else -> throw IllegalArgumentException("Unknown char: ${map[it]}")
                        }
                    }.toSet()
                }
                if(recurseWidePush(front.map { it.move(dir) }, dir)) {
                    // got space, so let's move everything
                    front.forEach {
                        map[it.move(dir)] = map[it]!!
                        map[it] = '.'
                    }
                    return true
                } else {
                    // no space in front
                    return false
                }
            } else  {
                // everything in front is space, so all clear to push (the startpos is empty as well)
                assert(pos.all { map[it] in setOf('.', '@') })
                return true
            }
        }

        fun solvePart1(): Int {
            map = smallmap
            return solve()
        }
        fun solvePart2(): Int {
            map = widemap
            return solve()
        }
        fun solve():Int {
            var robot = map.entries.first { it.value == '@' }.key
            moves.forEach { move ->
                val dir = move.toDirection()
                val newPos = robot.move(dir)
                robot = when(map[newPos]) {
                    '[', ']', 'O' -> {
                        if (recurseWidePush(listOf(newPos), dir)) {
                            newPos
                        } else {
                            robot
                        }
                    }
                    '#' -> robot
                    else -> newPos
                }
            }
            return map.filter { it.value in setOf('[', 'O') }.keys.sumOf{it.y * 100 + it.x}
        }
    }

    @Nested
    inner class TestCases {
        val special = """
########
#.#....#
#......#
#...O..#
#..OO@.#
#..O...#
#......#
########

<vv<<<^^
        """.trimIndent().lines()
        val testInput = """
########
#..O.O.#
##@.O..#
#...O..#
#.#.O..#
#...O..#
#......#
########

<^^>>>vv<v>>v<<
        """.trimIndent().lines()

        val testInput2 = """
##########
#..O..O.O#
#......O.#
#.OO..O.O#
#..O@..O.#
#O#..O...#
#O..O..O.#
#.OO.O.OO#
#....O...#
##########

<vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^
vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v
><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<
<<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^
^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><
^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^
>^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^
<><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>
^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>
v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^
    """.trimIndent().lines()

        val realInput = Resources.resourceAsList("day15.txt")
        @Test
        fun `Part 1 Example`() {
            val answer = Logic(testInput).solvePart1()
            assertThat(answer).isEqualTo(2028)
            val answer2 = Logic(testInput2).solvePart1()
            assertThat(answer2).isEqualTo(10092)
        }
        @Test
        fun `Part 1 Answer`() {
            val answer = Logic(realInput).solvePart1()
            assertThat(answer).isEqualTo(1485257)
        }
        @Test
        fun `Part 2 Example`() {
            //val answer = Logic(testInput).solvePart2()
            //assertThat(answer).isEqualTo(2028)
            val answer2 = Logic(testInput2).solvePart2()
            assertThat(answer2).isEqualTo(9021)
        }
        @Test
        fun `Part 2 Answer`() {
            val answer = Logic(realInput).solvePart2()
            assertThat(answer).isEqualTo(1475512)
        }
    }

}