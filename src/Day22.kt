import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested

class Day22 {
    inner class Logic(input: List<String>) {
        val start = input.map { it.toLong() }

        fun mixAndPrune(secret:Long, value:Long): Long {
            return (value.xor(secret)) % 16777216
        }
        fun nextSecret(seed:Long): Long {
            var secret = seed
            secret = mixAndPrune(secret, secret * 64)
            secret = mixAndPrune(secret, secret / 32)
            secret = mixAndPrune(secret, secret * 2048)
            return secret
        }
        fun solvePart1() =start.sumOf { startValue ->
                (1..2000).fold(startValue) { acc, _ -> nextSecret(acc)}
            }


        fun simulatePattern(pattern: List<Long>, allPrices: List<List<Long>>): Long {
            return allPrices.sumOf { prices ->
                val diffs = prices.windowed(2).map { it[1] - it[0] }
                val idx = diffs.windowed(4).indexOfFirst {
                    it == pattern
                }
                if (idx > -1) {
                    prices[idx + 4]
                } else {
                    //allValues.zip(diffs).forEach { println("${it.first % 10}\t${it.second}") }
                    0L
                }
            }
        }
        fun getPrices() = start.map { startValue ->
                (1..2000).fold(listOf(startValue)) { acc, _ -> acc + nextSecret(acc.last()) }.map { it % 10 }
            }

        fun solvePart2():Long {
            val useRange = -9..9
            val masks = useRange.flatMap { a ->
                useRange.flatMap { b ->
                    useRange.flatMap { c ->
                        useRange.map { d -> listOf(a, b, c, d).map{it.toLong()} }
                    }
                }
            }

            val allPrices = getPrices()

            val result = masks.pmap { pattern ->
                simulatePattern(pattern, allPrices)
            }
            return result.maxOf{it}
        }
    }

    @Nested
    inner class TestCases {

        val testInput = """
1
10
100
2024
    """.trimIndent().lines()
        val testInput2 = """
1
2
3
2024
    """.trimIndent().lines()

        val realInput = Resources.resourceAsList("day22.txt")
        @Test
        fun `Part 1 Example`() {
            val answer = Logic(testInput).solvePart1()
            assertThat(answer).isEqualTo(37327623L)
        }
        @Test
        fun `Part 1 Answer`() {
            val answer = Logic(realInput).solvePart1()
            assertThat(answer).isEqualTo(13461553007L)
        }
        @Test
        fun `Part 2 Example`() {
            val answer = Logic(testInput2).solvePart2()
            assertThat(answer).isEqualTo(23L)
        }
        @Test
        fun `Part 2 Answer`() {
            val answer = Logic(realInput).solvePart2()
            assertThat(answer).isEqualTo(1499)
        }
    }

}