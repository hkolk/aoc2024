import apple.laf.JRSUIConstants.Direction
import org.assertj.core.api.Assertions.assertThat
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

        internal fun Point2D.diffToChar() = when(this) {
                Point2D(-1,  0) -> '<'
                Point2D( 1,  0) -> '>'
                Point2D( 0, -1) -> '^'
                Point2D( 0,  1) -> 'v'
                else -> throw IllegalStateException("Cannot map $this to character")
            }

        internal fun recursiveCosts(translate: List<Char>, depth:Int): Int {
            val newOptions = translate.fold(listOf<List<Char>>() to 'A' ) { (acc, prev), cur ->
                val options = hardCodedGetMove(prev, cur)
                val generate = if(acc.isEmpty()) {
                    options
                } else {
                    options.flatMap { option -> acc.map { it + option } }
                }
                //println("From $prev to $cur: $options, $generate")

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
            return moves.combinations(moves.size, true).map {
                val simulateMove = it.fold(this to true) { (prev, valid), cur ->
                    val next = prev.move(cur.toDirection())
                    if(!map.contains(next)) {
                        next to false
                    } else {
                        next to valid
                    }
                }
                if(simulateMove.second) {
                    recursiveCosts(it, 3) to it
                } else {
                    Int.MAX_VALUE to it
                }
            }.minBy { it.first }.second

            //return moves
        }
        internal fun Char.toDirection() = when(this) {
            '>' -> Point2D.EAST
            '<' -> Point2D.WEST
            '^' -> Point2D.NORTH
            'v' -> Point2D.SOUTH
            else -> throw IllegalArgumentException("asdf $this")
        }

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
            if(keypad.size == 5) {
                return hardCodedGetMove(from, to).first().dropLast(1)
            }
            return moves[from to to] ?: throw IllegalArgumentException("Could not find move from $from to $to")
        }

        fun reverseMove(start: Char, lMoves: List<Char>): Char {
            assert(lMoves.last() == 'A')
            val startPos = keypad.firstOrNull { it.first == start }?.second ?: throw IllegalArgumentException("Could not find start $start")
            val finish = lMoves.dropLast(1).fold(startPos) { acc, move ->
                val ret = acc.move(move.toDirection())
                if(keypad.none { it.second == ret }) {
                   throw IllegalStateException("Moved to empty spot while $start and $lMoves, landed on $ret")
                }
                ret
            }
            return keypad.firstOrNull{ it.second == finish }?.first ?: throw IllegalArgumentException("Could not find finish $finish")
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

        fun solvePart1():Int {

            fun splitByAandCount(input: String) : List<Int> {
                return input.splitIgnoreEmpty("A").map { it.length }
            }
            fun reverseDirectional(input: String, keypad: KeyPad = KeyPad.DIRECTIONAL): String {
                val moves = input.split("(?<=A)".toRegex()).map { it.toList() }
                var cur = 'A'
                val reversed = moves.dropLast(1).map{
                    cur = keypad.reverseMove(cur, it)
                    cur
                }
                //println(reversed)
                return reversed.joinToString("")
            }

            KeyPad.NUMERIC.moves.toList().sortedBy { it.first.first }.forEach{ move ->
                println("${move.first} -> ${move.second}")
            }

            val reverse = "v<<A>>^AAAvA^Av<<A>>^AAvA^A<vA^>AA<A>Av<<A>A^>AAA<Av>A^A"
            val step1 = reverseDirectional(reverse)
            println(step1)
            val step2 = reverseDirectional(step1)
            println(step2)
            val step3 = reverseDirectional(step2, KeyPad.NUMERIC)
            println(step3)


            fun recurse(start: Char, translate: List<Char>, depth:Int): List<List<Char>> {
                val newOptions = translate.fold(listOf<List<Char>>() to start ) { (acc, prev), cur ->
                    val options = KeyPad.DIRECTIONAL.hardCodedGetMove(prev, cur)
                    val generate = if(acc.isEmpty()) {
                        options
                    } else {
                        options.flatMap { option -> acc.map { it + option } }
                    }
                    //println("From $prev to $cur: $options, $generate")

                    generate to cur
                }.first
                return if(depth == 0) {
                    newOptions
                } else {
                    newOptions.flatMap { option ->
                        recurse(start, option, depth - 1)
                    }
                }
            }
            val result = recurse('A', listOf('v', 'A'), 3)
            //result.forEach { println("${it.size} -> $it") }
            //val run1 = KeyPad.DIRECTIONAL.hardCodedGetMove('A', '<')
            //println(run1)



            val ret = codes.sumOf{
                val humanMoves = KeyPad.DIRECTIONAL.punch(
                    KeyPad.DIRECTIONAL.punch(
                        KeyPad.NUMERIC.punch(it.toList())
                    )
                )
                println("$it: ${humanMoves.joinToString("")} [${humanMoves.count()}]")
                humanMoves.count() * it.take(3).toInt()
            }

            val totalMoves = ("A" + codes.first()).windowed(2) {
                KeyPad.NUMERIC.getMove(it[0], it[1]) + 'A'
            }.flatten()
            println("${splitByAandCount(totalMoves.joinToString(""))}")
            println("${splitByAandCount("<A^A>^^AvvvA")}")
            println("${codes.first()} -> $totalMoves")
            val subMoves = (listOf('A') + totalMoves).windowed(2) {
                KeyPad.DIRECTIONAL.getMove(it[0], it[1]) + 'A'
            }.flatten()
            println("${splitByAandCount(subMoves.joinToString(""))}")
            println("${splitByAandCount("v<<A>>^A<A>AvA<^AA>A<vAAA>^A")}")
            println("$subMoves")
            val subMoves2 = (listOf('A') + subMoves).windowed(2) {
                KeyPad.DIRECTIONAL.getMove(it[0], it[1]) + 'A'
            }.flatten()
            println("${splitByAandCount(subMoves2.joinToString(""))}")
            println("${splitByAandCount("<vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A")}")
            println("$subMoves2")



            return ret

            TODO()
        }
        fun solvePart2():Int {
            return 0
        }
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
            assertThat(answer).isEqualTo(0)
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