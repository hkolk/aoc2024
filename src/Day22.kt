import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested

class Day22 {


    data class Buyer(val patternMap: Map<Pattern, Long>) {
        fun getBid(pattern: Pattern): Long {
            return patternMap[pattern]?:0L
        }
        companion object {
            fun from(seed: Long): Buyer {
                val prices = (1..2000).fold(listOf(seed)) { acc, _ -> acc + nextSecret(acc.last()) }.map { it % 10 }
                val diffs = prices.windowed(2).map { it[1] - it[0] }
                val map = diffs.windowed(4).mapIndexed { index: Int, longs: List<Long> ->
                    Pattern.fromList(longs) to index
                }.groupBy { it.first }.mapValues { it.value.map { it.second }.first() }.mapValues { prices[it.value+4] }
                return Buyer(map)
            }

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
        }
    }
    data class Pattern(val a: Long, val b: Long, val c: Long, val d: Long) {

        val hashCode = generateHashCode()
        fun generateHashCode(): Int {
            var result = a.hashCode()
            result = 31 * result + b.hashCode()
            result = 31 * result + c.hashCode()
            result = 31 * result + d.hashCode()
            return result
        }

        override fun hashCode() = hashCode

        companion object {
            fun fromList(from: List<Long>) = Pattern(from[0], from[1], from[2], from[3])
        }
    }
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


        fun getPrices() = start.map { startValue ->
                (1..2000).fold(listOf(startValue)) { acc, _ -> acc + nextSecret(acc.last()) }.map { it % 10 }
            }

        fun getMasks(): List<List<Long>> {
            val useRange = -9..9
            return useRange.flatMap { a ->
                useRange.flatMap { b ->
                    useRange.flatMap { c ->
                        useRange.map { d -> listOf(a, b, c, d).map{it.toLong()} }
                    }
                }
            }
        }

        fun getMasksSequence() = sequence {
            val useRange = -9..9
            useRange.forEach { a ->
                useRange.forEach { b ->
                    useRange.forEach { c ->
                        useRange.forEach { d ->
                            yield(Pattern.fromList(listOf(a, b, c, d).map{it.toLong()}))
                        }
                    }
                }
            }
        }

        fun solvePart2():Long {

            val buyers = start.map { Buyer.from(it) }
            val masks = getMasksSequence()
            val result = masks.pmap { pattern ->
                buyers.sumOf { it.getBid(pattern) }
            }
            return result.maxOf { it }
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