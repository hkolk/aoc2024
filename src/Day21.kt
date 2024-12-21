import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested
import java.lang.IllegalStateException
import kotlin.math.absoluteValue

class Day21 {

    class KeyPad(val keypad: List<Pair<Char, Point2D>>) {
        val map = keypad.map { it.second to it.first }.toMap()
        val moves = keypad.combinations(2).flatMap { perm ->
            listOf(
                (perm[0].first to perm[1].first) to perm[0].second.movesTo(perm[1].second),
                (perm[1].first to perm[0].first) to perm[1].second.movesTo(perm[0].second)
            )
        }.toMap()



        internal fun recursiveCosts(translate: List<Char>, depth:Int): Int {
            val newOptions = translate.fold(listOf<List<Char>>() to 'A' ) { (acc, prev), cur ->
                val options = hardCodedGetMove(prev, cur)
                val generate = if(acc.isEmpty()) {
                    options
                } else {
                    options.flatMap { option -> acc.map { it + option } }
                }

                generate to cur
            }.first
            return if(depth == 0) {
                newOptions.minOf { it.size }
            } else {
                newOptions.minOf { option ->
                    recursiveCosts(option, depth - 1)
                }
            }
        }

        internal fun Point2D.movesTo(other: Point2D): List<Char> {
            val diff = (other - this)
            val moves = ((1..diff.x).map { '>' } +
                    (1..diff.y).map { 'v' } +
                    (-1 downTo diff.x).map { '<' } +
                    (-1 downTo diff.y).map { '^' }).toList()
            val costs = moves.permutations().map {
                val move = it.joinToString("").toList()
                val simulateMove = move.fold(this to true) { (prev, valid), cur ->
                    val next = prev.move(cur.toDirection())
                    if(!map.contains(next)) {
                        next to false
                    } else {
                        next to valid
                    }
                }
                if(simulateMove.second) {
                    recursiveCosts(move+'A', 4) to move
                } else {
                    Int.MAX_VALUE to move
                }
            }
            return costs.minBy { it.first }.second

        }
        internal fun Char.toDirection() = when(this) {
            '>' -> Point2D.EAST
            '<' -> Point2D.WEST
            '^' -> Point2D.NORTH
            'v' -> Point2D.SOUTH
            else -> throw IllegalArgumentException("Invalid direction identifier: $this")
        }

        // We need this to kickstart the list of moves
        fun hardCodedGetMove(from: Char, to: Char): List<List<Char>> {
            return when(from){
                'A' -> when(to) {
                    'A' -> listOf(listOf('A'))
                    '>' -> listOf(listOf('v','A'))
                    '^' -> listOf(listOf('<','A'))
                    'v' -> listOf(listOf('<','v','A'))//, listOf('v','<','A'))
                    '<' -> listOf(listOf('v','<', '<','A'))//, listOf('<', 'v', '<', 'A'))
                    else -> TODO()
                }
                '>' -> when(to) {
                    'A' -> listOf(listOf('^', 'A'))
                    '>' -> listOf(listOf('A'))
                    '^' -> listOf(listOf('<', '^', 'A'))//, listOf('^', '<', 'A'))
                    'v' -> listOf(listOf('<', 'A'))
                    '<' -> listOf(listOf('<', '<', 'A'))
                    else -> TODO()
                }
                '^' -> when(to) {
                    'A' -> listOf(listOf('>', 'A'))
                    '>' -> listOf(listOf('v', '>', 'A'))//, listOf('>', 'v', 'A'))
                    '^' -> listOf(listOf('A'))
                    'v' -> listOf(listOf('v', 'A'))
                    '<' -> listOf(listOf('v', '<', 'A'))
                    else -> TODO()
                }
                'v' -> when(to) {
                    'A' -> listOf(listOf('^', '>', 'A'))//, listOf('>', '^', 'A'))
                    '>' -> listOf(listOf('>', 'A'))
                    '^' -> listOf(listOf('^', 'A'))
                    'v' -> listOf(listOf('A'))
                    '<' -> listOf(listOf('<', 'A'))
                    else -> TODO()
                }
                '<' -> when(to) {
                    'A' -> listOf(listOf('>', '>', '^', 'A'))//, listOf('>', '^', '>', 'A'))
                    '>' -> listOf(listOf('>', '>', 'A'))
                    '^' -> listOf(listOf('>', '^', 'A'))
                    'v' -> listOf(listOf('>', 'A'))
                    '<' -> listOf(listOf('A'))
                    else -> TODO()
                }
                else -> TODO()
            }
        }

        fun getMove(from: Char, to: Char): List<Char> {
            if(from == to) {
                return listOf()
            }
            return moves[from to to] ?: throw IllegalStateException("Could not find move from $from to $to")
        }


        fun punch(codes: List<Char>): List<Char> {
            return (listOf('A') + codes).windowed(2) {
                this.getMove(it[0], it[1]) + 'A'
            }.flatten()
        }

        companion object {
            val NUMERIC = KeyPad(listOf(
                    '7' to Point2D(0,0),
                    '8' to Point2D(1,0),
                    '9' to Point2D(2,0),
                    '4' to Point2D(0,1),
                    '5' to Point2D(1,1),
                    '6' to Point2D(2,1),
                    '1' to Point2D(0,2),
                    '2' to Point2D(1,2),
                    '3' to Point2D(2,2),
                    '0' to Point2D(1,3),
                    'A' to Point2D(2,3),
                ))
            val DIRECTIONAL = KeyPad(listOf(
                '^' to Point2D(1,0),
                'A' to Point2D(2,0),
                '<' to Point2D(0,1),
                'v' to Point2D(1,1),
                '>' to Point2D(2,1)
            ))
        }
    }





    inner class Logic(input: List<String>) {
        val codes = input

        val recurseCached = FunctionCache(::recurseCount)
        fun recurseCount(punch: Pair<List<Char>, Int>): Long {
            if(punch.second <= 0) {
                return 1L
            }
            return KeyPad.DIRECTIONAL.punch(punch.first)
                .splitBy({it == 'A'}, true)
                .sumOf{ recurseCached(it to punch.second-1)}
        }

        fun solve(robots:Int):Long {
            val punchers = robots + 1 // humans are punchers too!
            return codes.sumOf{
                val size = recurseCached(KeyPad.NUMERIC.punch(it.toList()) to punchers)
                size * it.take(3).toInt()
            }
        }
        fun solvePart1() = solve(2)
        fun solvePart2() = solve(25)

    }

    @Nested
    inner class TestCases {

        val testInput = """
029A
980A
179A
456A
379A
    """.trimIndent().lines()

        val realInput = Resources.resourceAsList("day21.txt")
        @Test
        fun `Part 1 Example`() {
            val answer = Logic(testInput).solvePart1()
            assertThat(answer).isEqualTo(126384)
        }

        @Disabled("Only used for debugging")
        @Test
        fun testMapping() {
            val asdf = KeyPad.DIRECTIONAL.moves.map { it.key to it.value }.sortedBy { it.first.first }.forEach { println("{${it.first} -> ${it.second}") }
        }

        @Test
        fun `Part 1 Answer`() {
            val answer = Logic(realInput).solvePart1()
            assertThat(answer).isEqualTo(176870)
        }
        @Test
        fun `Part 2 Example`() {
            val answer = Logic(testInput).solvePart2()
            assertThat(answer).isEqualTo(154115708116294L)
        }
        @Test
        fun `Part 2 Answer`() {
            val answer = Logic(realInput).solvePart2()
            assertThat(answer).isEqualTo(223902935165512L)
        }
    }

}