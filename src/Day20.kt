import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested

class Day20 {
    inner class Logic(input: List<String>) {
        val map = input.flatMapIndexed { y, line ->
            line.mapIndexed { x, c -> Point2D(x, y) to c }
        }.toMap()
        val start = map.firstNotNullOf { if(it.value == 'S') it.key else null }
        val end = map.firstNotNullOf { if(it.value == 'E') it.key else null }
        val empty = setOf('.', 'S', 'E')
        val baseline = Pathfinding.aStar(
            start, end, map,
            heuristic = { a, b -> a.distance(b) },
            adjacent = {a, map -> a.adjacent().filter { map[it] in empty }.toList()},
            moveCost = {_, _ -> 1},
        ).third


        fun solvePart1(atLeast: Int):Int {
            val validCheats = map.filter { it ->
                if(it.value == '#') {
                    if(it.key.adjacent().count { map[it] in empty } >= 2) {
                        true
                    } else {
                        false
                    }
                } else {
                    false
                }
            }.keys
            return validCheats.count { cheat ->
                //val cheatMap = map.map { it.key to if(it.key == cheat) '.' else it.value }.toMap()
                val cheatMap = map.toMutableMap().apply { this[cheat] = '.' }
                /*
                val path = Pathfinding.aStar(
                    start, end, cheatMap,
                    heuristic = { a, b -> a.distance(b) },
                    adjacent = {a, map -> a.adjacent().filter { map[it] in empty }.toList()},
                    moveCost = {_, _ -> 1}
                )
                val faster = (path.first && path.third <= (baseline - atLeast))
                               if(faster) {
                    //println("cheating at $cheat = ${path.third}")
                }
                faster
                 */
                val result = Pathfinding.BreadthFirstSearch(
                    start, end, cheatMap,
                    adjacent = {a, map -> a.adjacent().filter { map[it] in empty }.toList()},
                )
                result != null && result <= (baseline - atLeast)


            }
            return 0
        }
        fun solvePart2(atLeast: Int, cheatLength: Int):Long {
            val path = Pathfinding.aStar(
                start, end, map,
                heuristic = { a, b -> a.distance(b) },
                adjacent = {a, map -> a.adjacent().filter { map[it] in empty }.toList()},
                moveCost = {_, _ -> 1},
            ).second
            /*map.toMutableMap().apply {
                path.forEach { this[it] = 'o' }
                this[start] = 'S'
                this[end] = 'E'
            }.printChars()*/

            // iterate path
            val indexedPath = path.mapIndexed { idx, ele -> ele to idx }.toMap()
            return path.sumOf { curPos ->
                val wormholes = path.filter { it.distance(curPos) <= cheatLength }
                val curPosIdx = indexedPath[curPos]!!
                wormholes.count { wormEnd ->
                    val shortCutSize = indexedPath[wormEnd]!! - curPosIdx - wormEnd.distance(curPos)
                    val ret = shortCutSize >= atLeast
                    //if(ret) println("$curPos => $wormEnd = $shortCutSize")
                    ret
                }.toLong()
            }



            return 0
        }
    }

    @Nested
    inner class TestCases {

        val testInput = """
###############
#...#...#.....#
#.#.#.#.#.###.#
#S#...#.#.#...#
#######.#.#.###
#######.#.#...#
#######.#.###.#
###..E#...#...#
###.#######.###
#...###...#...#
#.#####.#.###.#
#.#...#.#.#...#
#.#.#.#.#.#.###
#...#...#...###
###############
    """.trimIndent().lines()

        val realInput = Resources.resourceAsList("day20.txt")
        @Test
        fun `Part 1 Example`() {
            val answer = Logic(testInput).solvePart1(20)
            assertThat(answer).isEqualTo(5)
        }
        @Test
        fun `Part 1 Answer`() {
            val answer = Logic(realInput).solvePart1(100)
            assertThat(answer).isEqualTo(1485)
        }
        @Test
        fun `Part 2 Example`() {
            val answer = Logic(testInput).solvePart2(50, 20)
            assertThat(answer).isEqualTo(285)
        }
        @Test
        fun `Part 2 Answer`() {
            val answer = Logic(realInput).solvePart2(100, 20)
            assertThat(answer).isEqualTo(1027501L)
        }
    }

}