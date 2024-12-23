import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested

class Day23 {
    inner class Logic(input: List<String>) {
        val connections = input.flatMap { it.splitIgnoreEmpty("-").let { listOf(it[0] to it[1], it[1] to it[0]) } }

        fun solvePart1():Int {
            val groups = connections.groupBy { it.first }.mapValues { it.value.map { it.second } }
            val asdf2 = groups.flatMap{ (start, conns) ->
                val asdf = conns.combinations(2).map { setOf(it[0], it[1], start) to connections.contains(it[0] to it[1]) }.filter { it.second }.toList()
                val grouped = asdf.map { it.first }
                grouped
            }.toSet()
            return asdf2.count { it.any{it.startsWith("t")} }
            TODO()
        }
        fun solvePart2():Int {
            val groups = connections.groupBy { it.first }.mapValues { it.value.map { it.second } }
            val asdf2 = groups.flatMap{ (start, conns) ->
                val asdf = conns.combinations(3).map {
                    setOf(it[0], it[1], start) to connections.contains(it[0] to it[1])
                }.filter { it.second }.toList()
                val grouped = asdf.map { it.first }
                grouped
            }.toSet()
            return asdf2.count { it.any{it.startsWith("t")} }
            TODO()
        }


        fun solvePart1_old():Int {
            println(connections)
            val map = connections.groupBy { it.first }.mapValues { it.value.map { it.second } }
            val withT = map.keys.filter { it.startsWith("t") }
            println(withT)
            withT.forEach { cur ->
                val conns = map[cur]!!
                conns.filter { it != cur }.forEach { conn ->
                }
            }

            val scan = map.keys.toMutableSet()
            val graphs = mutableListOf<Set<String>>()
            while(scan.isNotEmpty()) {
                val start = scan.first()
                val nodes = Pathfinding.floodFill(start, map, {cur, map -> map[cur]!!})
                println(scan)
                scan.removeAll(nodes)
                graphs.add(nodes)
                println(scan)
            }
            println(graphs)
            return graphs.count { it.any { it.startsWith("t") } }
            return 0
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
            assertThat(answer).isEqualTo(0)
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