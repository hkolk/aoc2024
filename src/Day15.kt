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
        val map = input.splitBy { it.isEmpty() }.first().flatMapIndexed { y, line ->
            line.mapIndexed { x, c -> Point2D(x, y) to c }
        }.toMap().toMutableMap()
        val moves = input.splitBy { it.isEmpty() }.last().joinToString("")

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

        fun recursePush(pos: Point2D, dir: DIRECTION): Boolean {
            if(map[pos] == '#') {
                return false
            } else if(map[pos] == 'O'){
                // try to push the box
                val push = recursePush(pos.move(dir), dir)
                if(push) {
                    map[pos.move(dir)] = 'O'
                    map[pos] = '.'
                    return true
                } else {
                    return false
                }
            } else {
                // empty space
                return true
            }
        }

        fun recurseWidePush(pos: Point2D, dir: DIRECTION): Boolean {
            println("  Recurse: $pos, ${widemap[pos]}")
            if(widemap[pos] == '#') {
                return false
            } else if(widemap[pos] in setOf('[', ']')){
                // try to push the box
                if(dir in setOf(Point2D.EAST, Point2D.WEST)) {
                    val push = recurseWidePush(pos.move(dir), dir)
                    if (push) {
                        widemap[pos.move(dir)] = widemap[pos]!!
                        widemap[pos] = '.'
                        return true
                    } else {
                        return false
                    }
                } else {
                    val other = if(widemap[pos] == '[') pos.move(Point2D.EAST) else pos.move(Point2D.WEST)
                    //println("$pos, $other, ${widemap[pos]}, ${widemap[other]}")
                    TODO()
                    // push both things up or down
                }
            } else {
                // empty space
                return true
            }
        }

        fun solvePart1():Int {
            var robot = map.entries.first { it.value == '@' }.key

            moves.forEach { move ->
                //println("Move: $move")
                val dir = move.toDirection()
                val newPos = robot.move(dir)
                //map[robot] = '.'
                robot = if(map[newPos] == 'O') {
                    val push = recursePush(newPos, dir)
                    if (push) {
                        //println(" Box -> Pushed")
                        // take the spot
                        newPos
                    } else {
                        //println(" Box -> Stuck")
                        robot
                    }
                } else if(map[newPos] == '#') {
                    //println(" Wall -> Stuck")
                    robot
                } else {
                    //println(" Empty -> Moved")
                    newPos
                }
                //map[robot] = '@'
                //map.printChars('.')
            }
            return map.filter { it.value == 'O' }.keys.sumOf{it.y * 100 + it.x}
        }
        fun solvePart2():Int {
            widemap.printChars('.')
            var robot = widemap.entries.first { it.value == '@' }.key
            moves.forEach { move ->
                println("Move: $move")
                val dir = move.toDirection()
                val newPos = robot.move(dir)
                widemap[robot] = '.'
                robot = if(widemap[newPos] in setOf('[', ']')) {
                    val push = recurseWidePush(newPos, dir)
                    if (push) {
                        println(" Box -> Pushed")
                        // take the spot
                        newPos
                    } else {
                        println(" Box -> Stuck")
                        robot
                    }
                } else if(widemap[newPos] == '#') {
                    println(" Wall -> Stuck")
                    robot
                } else {
                    println(" Empty -> Moved")
                    newPos
                }
                widemap[robot] = '@'
                widemap.printChars('.')
            }
            return widemap.filter { it.value == 'O' }.keys.sumOf{it.y * 100 + it.x}
        }
    }

    @Nested
    inner class TestCases {
        val special = """
########
#.#....#
#......#
#.OOO..#
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
            assertThat(answer).isEqualTo(10092)
        }
        @Test
        fun `Part 1 Answer`() {
            val answer = Logic(realInput).solvePart1()
            assertThat(answer).isEqualTo(0)
        }
        @Test
        fun `Part 2 Example`() {
            val answer = Logic(special).solvePart2()
            assertThat(answer).isEqualTo(0)
        }
        @Test
        fun `Part 2 Answer`() {
            val answer = Logic(realInput).solvePart2()
            assertThat(answer).isEqualTo(0)
        }
    }

}