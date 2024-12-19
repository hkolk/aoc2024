import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested

class Day19 {

    inner class Cacher<A, T> (val function: (A) -> T) {
        val cache = mutableMapOf<A, T>()
        fun invoke(value: A): T {
            if(!cache.containsKey(value)) {
                cache[value] = function(value)
            }
            return cache[value]!!
        }
    }

    inner class Logic(input: List<String>) {
        val towels = input.first().splitIgnoreEmpty(", ")
        val patterns = input.drop(2)

        val cache = mutableMapOf<String, Long>()

        fun recursePattern(pattern: String, debug:Boolean=false): Long {
            if(cache.containsKey(pattern)) return cache[pattern]!!
            if(pattern.isEmpty()) {
                return 1
            }
            cache[pattern] = towels.sumOf { towel ->
                if(pattern.startsWith(towel)) {
                    if(debug) println("Got a hit on $pattern for $towel")
                    recursePattern(pattern.drop(towel.length), debug)
                } else {
                    0
                }
            }
            return cache[pattern]!!
        }

        fun solvePart1() = patterns.count { recursePattern(it) > 0 }
        fun solvePart2() = patterns.sumOf { recursePattern(it) }
    }

    @Nested
    inner class TestCases {

        val testInput = """
r, wr, b, g, bwu, rb, gb, br

brwrr
bggr
gbbr
rrbgbr
ubwu
bwurrg
brgr
bbrgwb
    """.trimIndent().lines()

        val realInput = Resources.resourceAsList("day19.txt")
        @Test
        fun `Part 1 Example`() {
            val answer = Logic(testInput).solvePart1()
            assertThat(answer).isEqualTo(6)
        }
        @Test
        fun `Part 1 Answer`() {
            val answer = Logic(realInput).solvePart1()
            assertThat(answer).isEqualTo(213)
        }
        @Test
        fun `Part 2 Example`() {
            val answer = Logic(testInput).solvePart2()
            assertThat(answer).isEqualTo(16)
        }
        @Test
        fun `Part 2 Answer`() {
            val answer = Logic(realInput).solvePart2()
            assertThat(answer).isEqualTo(0)
        }
    }

}