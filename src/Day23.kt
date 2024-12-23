import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested

class Day23 {
    inner class Logic(input: List<String>) {
        val connections = input.flatMap { it.splitIgnoreEmpty("-").let { listOf(it[0] to it[1], it[1] to it[0]) } }
        val groups = connections.groupBy { it.first }.mapValues { it.value.map { it.second } }

        fun findNetworks(size: Int): Set<Set<String>> {
            return groups.flatMap{ (start, conns) ->
                conns.combinations(size-1).mapNotNull { group ->
                    // everything in group must also be connected
                    if(group.combinations(2).all {connections.contains(it[0] to it[1])})
                        setOf(start) + group
                    else null
                }
            }.toSet()
        }

        fun solvePart1() = findNetworks(3).count { it.any{it.startsWith("t")}}

        fun solvePart2():String {
            val maxSize = groups.maxOf { it.value.size+1 }
            (maxSize downTo 0).forEach { size ->
                val asdf = findNetworks(size)
                if(asdf.isNotEmpty()) {
                    return asdf.first().sorted().joinToString(",")
                }
            }
            TODO()
        }
    }

    @Nested
    inner class TestCases {

        val testInput = """
kh-tc
qp-kh
de-cg
ka-co
yn-aq
qp-ub
cg-tb
vc-aq
tb-ka
wh-tc
yn-cg
kh-ub
ta-co
de-co
tc-td
tb-wq
wh-td
ta-ka
td-qp
aq-cg
wq-ub
ub-vc
de-ta
wq-aq
wq-vc
wh-yn
ka-de
kh-ta
co-tc
wh-qp
tb-vc
td-yn
    """.trimIndent().lines()

        val realInput = Resources.resourceAsList("day23.txt")
        @Test
        fun `Part 1 Example`() {
            val answer = Logic(testInput).solvePart1()
            assertThat(answer).isEqualTo(7)
        }
        @Test
        fun `Part 1 Answer`() {
            val answer = Logic(realInput).solvePart1()
            assertThat(answer).isEqualTo(1352)
        }
        @Test
        fun `Part 2 Example`() {
            val answer = Logic(testInput).solvePart2()
            assertThat(answer).isEqualTo("co,de,ka,ta")
        }
        @Test
        fun `Part 2 Answer`() {
            val answer = Logic(realInput).solvePart2()
            assertThat(answer).isEqualTo("dm,do,fr,gf,gh,gy,iq,jb,kt,on,rg,xf,ze")
        }
    }

}