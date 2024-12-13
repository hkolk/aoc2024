import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested

class Day13 {
    data class ClawMachine(val A: Point2D, val B: Point2D,val  prize: Point2DWide)
    inner class Logic(val input: List<String>) {
        val machines = input.splitBy { it.isEmpty() }.map { machine ->
            val a = machine[0].splitIgnoreEmpty("+",",").let { Point2D(it[1].toInt(), it[3].toInt()) }
            val b = machine[1].splitIgnoreEmpty("+",",").let { Point2D(it[1].toInt(), it[3].toInt()) }
            val prize = machine[2].splitIgnoreEmpty("=",",").let { Point2DWide(it[1].toLong(), it[3].toLong()) }
            ClawMachine(a, b, prize)
        }

        fun solve(machines: List<ClawMachine>):Long {
            var cost = 0L
            machines.forEach { machine ->
                var localCost = Long.MAX_VALUE
                val maxA = machine.prize.x / machine.A.x
                (maxA downTo 0).forEach { aPresses ->
                    val remainder = machine.prize.x - (machine.A.x * aPresses)
                    if(remainder % machine.B.x == 0L) {
                        // possible hit, let's verify
                        val bPresses = remainder / machine.B.x
                        if(aPresses * machine.A.y + bPresses * machine.B.y == machine.prize.y) {
                            localCost = localCost.coerceAtMost(aPresses * 3 + bPresses)
                            println("got a hit: $aPresses, $bPresses, $localCost")                        }
                    }
                }
                if(localCost != Long.MAX_VALUE) {
                    cost += localCost
                }
            }
            return cost
        }
        fun solvePart1() = solve(machines)
        fun solvePart2():Long {
            val addition = 10_000_000_000_000
            val newMachines = machines.map { it.copy(prize = it.prize.copy(x = it.prize.x + addition, y = it.prize.y+addition)) }
            println(newMachines)
            return solve(newMachines)
            TODO()
        }
    }

    @Nested
    inner class TestCases {

        val testInput = """
Button A: X+94, Y+34
Button B: X+22, Y+67
Prize: X=8400, Y=5400

Button A: X+26, Y+66
Button B: X+67, Y+21
Prize: X=12748, Y=12176

Button A: X+17, Y+86
Button B: X+84, Y+37
Prize: X=7870, Y=6450

Button A: X+69, Y+23
Button B: X+27, Y+71
Prize: X=18641, Y=10279

Button A: X+48, Y+77
Button B: X+34, Y+12
Prize: X=6510, Y=1583
    """.trimIndent().lines()

        val realInput = Resources.resourceAsList("day13.txt")
        @Test
        fun `Part 1 Example`() {
            val answer = Logic(testInput).solvePart1()
            assertThat(answer).isEqualTo(480)
        }
        @Test
        fun `Part 1 Answer`() {
            val answer = Logic(realInput).solvePart1()
            assertThat(answer).isEqualTo(29598)
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