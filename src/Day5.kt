import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested

class Day5 {
    inner class Logic(input: List<String>) {
        val rules = input.splitBy { it.isEmpty() }.let { parts ->
            parts.first().map { line ->
                line.splitIgnoreEmpty("|").let {
                    it.last().toInt() to it.first().toInt()
                }
            }
        }.groupBy { it.first }.mapValues { it.value.map { it.second } }

        val updates = input.splitBy { it.isEmpty() }.let { parts ->
            parts.last().map { it.splitIgnoreEmpty(",").map { it.toInt() } }
        }

        val validated = updates.map { update ->
            var valid = true
            update.forEachIndexed { index, page ->
                (rules[page]?:listOf()).forEach { before ->
                    if(before in update && update.indexOf(before) > index) {
                        valid = false
                    }
                }
            }
            valid to update
        }

        fun solvePart1() = validated.filter { it.first }.sumOf { it.second[it.second.size/2] }

        fun solvePart2():Int {
            val invalid = validated.filter { !it.first}.map{it.second}

            val pageComparator = Comparator<Int>{ first, second ->
                val before = rules[first]
                if(before == null) {
                    0
                } else if (second in before) {
                    1
                } else {
                    -1
                }
            }

            return invalid.map { update ->
                update.sortedWith(pageComparator)
            }.sumOf { it[it.size/2] }
        }
    }

    @Nested
    inner class TestCases {

        val testInput = """
47|53
97|13
97|61
97|47
75|29
61|13
75|53
29|13
97|29
53|29
61|53
97|53
61|29
47|13
75|47
97|75
47|61
75|61
47|29
75|13
53|13

75,47,61,53,29
97,61,53,29,13
75,29,13
75,97,47,61,53
61,13,29
97,13,75,29,47
    """.trimIndent().lines()

        val realInput = Resources.resourceAsList("day5.txt")
        @Test
        fun `Part 1 Example`() {
            val answer = Logic(testInput).solvePart1()
            assertThat(answer).isEqualTo(143)
        }
        @Test
        fun `Part 1 Answer`() {
            val answer = Logic(realInput).solvePart1()
            assertThat(answer).isEqualTo(5248)
        }
        @Test
        fun `Part 2 Example`() {
            val answer = Logic(testInput).solvePart2()
            assertThat(answer).isEqualTo(123)
        }
        @Test
        fun `Part 2 Answer`() {
            val answer = Logic(realInput).solvePart2()
            assertThat(answer).isEqualTo(4507)
        }
    }

}