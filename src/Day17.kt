import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested

class Day17 {

    class TripleBitComputer(init: List<String>) {
        val reg = init.take(3).associate { line ->
            line.splitIgnoreEmpty(" ", ":").let {
                it[1] to it[2].toLong()
            }
        }.toMutableMap()
        val program = init.last().drop(9).split(",").map { it.toLong() }
        val output = mutableListOf<Long>()

        fun combo(address: Long): Long {
            return when (address) {
                in 0L..3L -> address
                4L -> reg["A"]!!
                5L -> reg["B"]!!
                6L -> reg["C"]!!
                else -> throw IllegalArgumentException()
            }
        }

        fun run(debug: Boolean = false) {
            var ptr = 0
            runloop@ while (true) {
                if(debug) print("${program.getOrElse(ptr, { 99L })}, ${program.getOrElse(ptr+1, { 99L })} :: ")
                when(program.getOrElse(ptr, { 99L })) {
                    0L -> reg["A"] = (reg["A"]!! / 2L.pow(combo(program[ptr+1])))
                    1L -> reg["B"] = reg["B"]!!.xor(program[ptr+1])
                    2L -> reg["B"] = combo(program[ptr+1]) % 8
                    3L -> if(reg["A"] != 0L) { ptr = program[ptr+1].toInt() - 2 }
                    4L -> reg["B"] = reg["B"]!!.xor(reg["C"]!!)
                    5L -> output.add(combo(program[ptr+1]) % 8)
                    6L -> reg["B"] = (reg["A"]!! / 2L.pow(combo(program[ptr+1])))
                    7L -> reg["C"] = (reg["A"]!! / 2L.pow(combo(program[ptr+1])))
                    99L -> break@runloop
                }
                if(debug) println(reg)
                ptr += 2
            }
        }
        fun getOutput(): String {
            return output.joinToString(",")
        }

        fun setRegA(value: Long) {
            reset()
            reg["A"] = value
        }
        fun reset() {
            reg["A"] = 0L
            reg["B"] = 0L
            reg["C"] = 0L
            output.clear()
        }
    }

    class FastComputer {
        fun run(aInit: Long, debug:Boolean=false): List<Long> {
            var A = aInit
            var B = 0L
            var C = 0L
            val output = mutableListOf<Long>()

            while(A != 0L) {
                B = A % 8          // L1
                if(debug) println(" B = $B = $A % 8")
                if(debug) println("L1: $A, $B, $C")

                var oldB = B
                B = B xor 3        // L2
                if(debug) println(" B = $B = $oldB xor 3")
                if(debug) println("L2: $A, $B, $C")

                C = A / 2L.pow(B)        // L3
                if(debug) println(" C = $C = $A / ${2L.pow(B)}")
                if(debug) println("L3: $A, $B, $C")

                A = A / 2L.pow(3L)        // L4
                if(debug) println(" A = $A = $A / 8")
                if(debug) println("L4: $A, $B, $C")
                if(A == 0L) {
                    if(debug) println("Exit mode: $A, $B, $C")
                }

                oldB = B
                B = B xor 5        // L5
                if(debug) println(" B = $B = $oldB xor 5")
                if(debug) println("L5: $A, $B, $C")

                oldB = B
                B = B xor C        // L6
                if(debug) println(" B = $B = $oldB xor $C")
                if(debug) println("L6: $A, $B, $C")

                output.add(B % 8)      // L7
                if(debug) println(" out = ${B % 8} = $B % 8")
                if(debug) println("L7: $A, $B, $C")
            }
            if(debug) println("Result: $output")
            if(debug) println("==================================")
            return output.toList()
        }
    }

    inner class Logic(val input: List<String>) {

        fun solvePart1():String {
            val comp = TripleBitComputer(input)
            comp.run()
            return comp.getOutput()
        }

        fun recurse(target: List<Long>):Long {
            var a = if (target.size == 1) {
                0L
            } else {
                8L * recurse(target.drop(1))
            }
            while(true) {
                val comp = TripleBitComputer(input)
                comp.setRegA(a)
                comp.run()
                if(comp.output == target) {
                    //println("Found $aStart for $target")
                    return a
                } else {
                    a++
                }
            }
        }

        fun solvePart2(): Long {
            val target = TripleBitComputer(input).program
            return recurse(target)
        }

        // OLD TESTING FUNCTIONS //
        fun solvePart2_test():Long {
            var prev = 0L
            (0L..1_000_000L).forEach { step ->
                val out = FastComputer().run(step.toLong(), (step in listOf(216148338630253L)))
                if(out.takeLast(2) == listOf(3L, 0L)) {
                    //println("Step: $step: ${out.takeLast(3)}")
                    if(out.takeLast(5) == listOf(1L, 5L, 5L, 3L, 0L)) {
                        println("Step: $step: ${out.takeLast(5)} -- ${step - prev}")
                        prev = step
                    }
                    //return 0
                }
            }
            return prev
        }

        fun solvePart2_testing(): Long {
            val step = 216148338630253L
            val comp = TripleBitComputer(input)
            comp.setRegA(step)
            comp.run(true)

            val fast = FastComputer()
            fast.run(step, true)
            TODO()
        }

        fun solvePart2_single():Long {

            val comp = TripleBitComputer(input)
            val program = comp.program
            (216148338630253L..216148338630253L).forEach { step ->
                comp.setRegA(step)
                comp.run()
                if(comp.output.getOrNull(0) == program[0]) {
                    println("Match on 0: $step")
                }
                if(comp.output.getOrNull(1) == program[1]) {
                    println("Match on 1: $step")
                }
                if(program == comp.output) {
                    return step
                }
            }
            TODO()
        }
    }

    @Nested
    inner class TestCases {

        val testInput = """
Register A: 729
Register B: 0
Register C: 0

Program: 0,1,5,4,3,0
    """.trimIndent().lines()
        val testInput2 = """
            Register A: 2024
            Register B: 0
            Register C: 0

            Program: 0,3,5,4,3,0
        """.trimIndent().lines()

        val realInput = Resources.resourceAsList("day17.txt")
        @Test
        fun `Part 1 Example`() {
            val answer = Logic(testInput).solvePart1()
            assertThat(answer).isEqualTo("4,6,3,5,6,3,5,2,1,0")
        }
        @Test
        fun `Part 1 Answer`() {
            val answer = Logic(realInput).solvePart1()
            assertThat(answer).isEqualTo(0)
        }
        @Test
        fun `Part 2 Example`() {
            val answer = Logic(testInput2).solvePart2()
            assertThat(answer).isEqualTo(117440)
        }
        @Test
        fun `Part 2 Answer`() {
            val answer = Logic(realInput).solvePart2()
            assertThat(answer).isEqualTo(216148338630253L)
        }
    }

}