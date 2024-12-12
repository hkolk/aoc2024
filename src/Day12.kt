import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested

class Day12 {
    inner class Logic(input: List<String>) {
        val map = input.flatMapIndexed{ y, line -> line.mapIndexed{x, c -> Point2D(x, y) to c}}.toMap()

        fun getArea(name:Char, start: Point2D, covered:Set<Point2D>):Set<Point2D> {
            val covered = covered.toMutableSet()
            val explore = start.adjacent().filter { it !in covered && map[it] == name }
            explore.forEach { covered.addAll(getArea(name, it, covered + it)) }
            return covered
        }

        fun solvePart1():Int {
            val uncovered = map.keys.toMutableList()
            var score = 0
            while(uncovered.isNotEmpty()) {
                val start = uncovered.removeFirst()
                val name = map[start]!!
                val region = getArea(name, start, setOf(start))
                val perimeter = region.sumOf { it.adjacent().filter { it !in region }.count() }
                //println("[$name] [${region.size}] [${perimeter}]: $region")
                uncovered.removeAll(region)
                score += perimeter * region.size
            }
            return score
        }

        fun walkAndRemove(start: Pair<Point2D, Point2D>, process: List<Pair<Point2D, Point2D>>): Set<Pair<Point2D, Point2D>> {
            //println("Walk and Remove: $start --- $process")
            val connected = start.first.adjacent().flatMap { adj ->
                //println(adj)
                adj.adjacent().filter { it in start.second.adjacent() }
                    .map { adj to it }
                    .filter { process.contains(it) }
                    .toList()
            }.toList()
            //println("${start.first} --- $connected")
            return connected.flatMap {
                walkAndRemove(it, process.filterNot { it in connected })
            }.toSet() + start
        }

        fun connectedFences(process: List<Pair<Point2D, Point2D>>): Int {
            val process = process.toMutableList()
            var sides = 0
            while(process.isNotEmpty()) {
                sides++
                val active = process.removeFirst()
                val asdf = walkAndRemove(active, process)
                //println(asdf)
                process.removeAll(asdf)
            }
            //println("Sides: $sides")
            return sides
        }

        fun solvePart2():Int {
            val uncovered = map.keys.toMutableList()
            var score = 0
            while(uncovered.isNotEmpty()) {
                val start = uncovered.removeFirst()
                val name = map[start]!!
                val region = getArea(name, start, setOf(start))
                val fences = region.flatMap { pos -> pos.adjacent().filter { it !in region }.map { pos to it } }
                val sides = connectedFences(fences)
                println("[$name] [${region.size}] [${fences.count()}] [$sides]: $region")
                uncovered.removeAll(region)
                score += sides * region.size
            }
            return score        }
    }

    @Nested
    inner class TestCases {

        val testInput = """
RRRRIICCFF
RRRRIICCCF
VVRRRCCFFF
VVRCCCJFFF
VVVVCJJCFE
VVIVCCJJEE
VVIIICJJEE
MIIIIIJJEE
MIIISIJEEE
MMMISSJEEE
    """.trimIndent().lines()

        val realInput = Resources.resourceAsList("day12.txt")
        @Test
        fun `Part 1 Example`() {
            val answer = Logic(testInput).solvePart1()
            assertThat(answer).isEqualTo(1930)
        }
        @Test
        fun `Part 1 Answer`() {
            val answer = Logic(realInput).solvePart1()
            assertThat(answer).isEqualTo(1471452)
        }
        @Test
        fun `Part 2 Example`() {
            val answer = Logic(testInput).solvePart2()
            assertThat(answer).isEqualTo(1206)
        }
        @Test
        fun `Part 2 Answer`() {
            val answer = Logic(realInput).solvePart2()
            assertThat(answer).isEqualTo(0)
        }
    }

}