import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested
import java.util.*
import kotlin.math.absoluteValue

class Day18 {
    inner class Logic(input: List<String>) {
        val drops = input.map { Point2D.fromString(it) }

        fun solvePart1(mapSize: Int, simulate:Int):Int {
            val map = mutableMapOf<Point2D, Int>()
            (-1 .. mapSize).forEach {
                map[Point2D(-1, it)] = 1
                map[Point2D(mapSize, it)] = 1
                map[Point2D(it, -1)] = 1
                map[Point2D(it, mapSize)] = 1
            }
            drops.take(simulate).forEach { map[it] = 1 }
            map.print()
            val path = Pathfinding.aStar(
                start = Point2D(0, 0),
                finish = Point2D(mapSize - 1, mapSize - 1),
                map = map,
                heuristic = Point2D::heuristic,
                adjacent = {coord, map -> coord.adjacent().filter { !map.containsKey(it) }.toList()},
                moveCost = {_, _ -> 1 })

            //val path = findPath(Point2D(0, 0), Point2D(mapSize-1, mapSize-1), map)
            println(path)
            return path.third
        }

        fun solvePart2(mapSize: Int, simulate:Int):String {
            val map = mutableMapOf<Point2D, Int>()
            (-1 .. mapSize).forEach {
                map[Point2D(-1, it)] = 1
                map[Point2D(mapSize, it)] = 1
                map[Point2D(it, -1)] = 1
                map[Point2D(it, mapSize)] = 1
            }
            drops.take(simulate).forEach { map[it] = 1 }
            drops.drop(simulate).forEach { drop ->
                map[drop] = 1
                val path = Pathfinding.aStar(
                    start = Point2D(0, 0),
                    finish = Point2D(mapSize - 1, mapSize - 1),
                    map = map,
                    heuristic = {a, b -> a.distance(b)},
                    //heuristic = Point2D::heuristic,
                    adjacent = {coord, map -> coord.adjacent().filter { !map.containsKey(it) }.toList()},
                    moveCost = {_, _ -> 1 })
                //val path = findPath(Point2D(0, 0), Point2D(mapSize - 1, mapSize - 1), map)
                if(!path.first) {
                    map.print()
                    return "${drop.x},${drop.y}"
                }
            }
            throw IllegalStateException("Exhausted blocks while still having a path")
        }
    }

    @Nested
    inner class TestCases {

        val testInput = """
5,4
4,2
4,5
3,0
2,1
6,3
2,4
1,5
0,6
3,3
2,6
5,1
1,2
5,5
2,5
6,5
1,4
0,4
6,4
1,1
6,1
1,0
0,5
1,6
2,0
    """.trimIndent().lines()

        val realInput = Resources.resourceAsList("day18.txt")
        @Test
        fun `Part 1 Example`() {
            val answer = Logic(testInput).solvePart1(7, 12)
            assertThat(answer).isEqualTo(22)
        }
        @Test
        fun `Part 1 Answer`() {
            val answer = Logic(realInput).solvePart1(71, 1024)
            assertThat(answer).isEqualTo(232)
        }
        @Test
        fun `Part 2 Example`() {
            val answer = Logic(testInput).solvePart2(7, 12)
            assertThat(answer).isEqualTo("6,1")
        }
        @Test
        fun `Part 2 Answer`() {
            val answer = Logic(realInput).solvePart2(71, 1024)
            assertThat(answer).isEqualTo("44,64")
        }
    }

}