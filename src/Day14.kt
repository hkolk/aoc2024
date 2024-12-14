import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested

class Day14 {
    data class Robot(val pos:Point2D, val velocity:Point2D)

    fun Point2D.teleport(bounds: Point2D): Point2D {
        val x = if(this.x >= bounds.x) {
            0 + (this.x - bounds.x)
        } else if (this.x < 0) {
            bounds.x + this.x
        } else {
            this.x
        }
        val y = if(this.y >= bounds.y) {
            0 + (this.y - bounds.y)
        } else if (this.y < 0) {
            bounds.y + this.y
        } else {
            this.y
        }
        return Point2D(x, y)
    }

    inner class Logic(val input: List<String>) {
        val startingRobots = input.map { line ->
            val parts = line.splitIgnoreEmpty("=", ",", " ").let {
                listOf(it[1], it[2], it[4], it[5]) }.map { it.toInt()
                }
            Robot(Point2D(parts[0], parts[1]), Point2D(parts[2], parts[3]))
        }
        val max = if(input.size == 500) Point2D(101, 103) else Point2D(11, 7)
        val xmasTree = listOf(
            Point2D((max.x-1)/2, 0),
            Point2D((max.x-1)/2+1, 1),
            Point2D((max.x-1)/2-1, 1),
            Point2D((max.x-1)/2+2, 2),
            Point2D((max.x-1)/2-2, 2)
        )



        fun printRobots(robots: List<Robot>) {
            val map = robots.map { it.pos }.groupBy { it }.mapValues { it.value.size }
            val fullMap = map.mapValues { it.value.digitToChar() } + (Point2D(0, 0) to '.') + (Point2D(max.x-1, max.y-1) to '.')
            fullMap.printChars('.')
        }

        fun solvePart1():Long {
            var robots = startingRobots
            (1..100).forEach{second ->
                robots = robots.map { robot ->
                    val newPos = robot.pos.move(robot.velocity).teleport(max)
                    Robot(newPos, robot.velocity)
                }
                if(robots.any { it.pos == Point2D((max.x-1)/2, 0) }) {
                    printRobots(robots)
                }
            }

            // quadrants
            val map = robots.map { it.pos }.groupBy { it }.mapValues { it.value.size }
            val quadrants = mutableMapOf(1 to 0, 2 to 0, 3 to 0, 4 to 0)
            map.forEach { (pos, count) ->
                if(pos.x == (max.x-1)/2 || pos.y == (max.y-1)/2) {
                } else {
                    var quad = if(pos.x < (max.x-1)/2) 1 else 2
                    quad = if(pos.y < (max.y-1)/2) quad else quad+2
                    quadrants[quad] = quadrants[quad]!! + count
                }
            }
            println(quadrants)
            return quadrants.values.multiply()
        }

        
        fun solvePart2():Int {
            var robots = startingRobots
            (1..100).forEach{second ->
                robots = robots.map { robot ->
                    val newPos = robot.pos.move(robot.velocity).teleport(max)
                    Robot(newPos, robot.velocity)
                }

                if(robots.filter { it.pos in xmasTree }.toSet().count() == xmasTree.count()) {
                    println("== $second ==")
                    printRobots(robots)
                }
            }
            return 0
        }
    }

    @Nested
    inner class TestCases {

        val testInput = """
p=0,4 v=3,-3
p=6,3 v=-1,-3
p=10,3 v=-1,2
p=2,0 v=2,-1
p=0,0 v=1,3
p=3,0 v=-2,-2
p=7,6 v=-1,-3
p=3,0 v=-1,-2
p=9,3 v=2,3
p=7,3 v=-1,2
p=2,4 v=2,-3
p=9,5 v=-3,-3
    """.trimIndent().lines()

        val realInput = Resources.resourceAsList("day14.txt")
        @Test
        fun `Part 1 Example`() {
            val answer = Logic(testInput).solvePart1()
            assertThat(answer).isEqualTo(12)
        }
        @Test
        fun `Part 1 Answer`() {
            val answer = Logic(realInput).solvePart1()
            assertThat(answer).isEqualTo(231019008L)
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