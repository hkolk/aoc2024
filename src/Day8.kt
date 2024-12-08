import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested

class Day8 {
    inner class Logic(input: List<String>) {
        val map = input.flatMapIndexed { y, line ->
            line.mapIndexed { x, c -> Point2D(x, y) to c }
        }.toMap()
        val nodeNames = map.values.filter { it != '.' }.toSet()

        fun solvePart1():Int {
            val antiNodes = mutableSetOf<Point2D>()
            nodeNames.forEach { nodeName ->
                val locations = map.filter { it.value == nodeName }.keys
                val pairs = locations.combinations(2)
                //println("$nodeName -> ${pairs.toList()}")
                pairs.forEach { pair ->
                    val distance = pair.first() - pair.last()
                    //println("Distance: $distance")
                    val p1 = pair.first() + distance
                    val p2 = pair.last() - distance
                    antiNodes.add(p1)
                    antiNodes.add(p2)
                    //println("$p1, $p2")
                }
            }
            return antiNodes.filter { map.containsKey(it) }.count()
        }
        fun solvePart2():Int {
            val antiNodes = mutableSetOf<Point2D>()
            nodeNames.forEach { nodeName ->
                val locations = map.filter { it.value == nodeName }.keys
                val pairs = locations.combinations(2)
                //println("$nodeName -> ${pairs.toList()}")
                pairs.forEach { pair ->
                    val distance = pair.first() - pair.last()
                    //println("Distance: $distance")
                    var loc = pair.first()
                    while(map.containsKey(loc)) {
                        antiNodes.add(loc)
                        loc += distance
                    }
                    loc = pair.last()
                    while(map.containsKey(loc)) {
                        antiNodes.add(loc)
                        loc -= distance
                    }
                }
            }
            return antiNodes.filter { map.containsKey(it) }.count()        }
    }

    @Nested
    inner class TestCases {

        val testInput = """
............
........0...
.....0......
.......0....
....0.......
......A.....
............
............
........A...
.........A..
............
............
    """.trimIndent().lines()

        val realInput = Resources.resourceAsList("day8.txt")
        @Test
        fun `Part 1 Example`() {
            val answer = Logic(testInput).solvePart1()
            assertThat(answer).isEqualTo(14)
        }
        @Test
        fun `Part 1 Answer`() {
            val answer = Logic(realInput).solvePart1()
            assertThat(answer).isEqualTo(261)
        }
        @Test
        fun `Part 2 Example`() {
            val answer = Logic(testInput).solvePart2()
            assertThat(answer).isEqualTo(34)
        }
        @Test
        fun `Part 2 Answer`() {
            val answer = Logic(realInput).solvePart2()
            assertThat(answer).isEqualTo(898)
        }
    }

}