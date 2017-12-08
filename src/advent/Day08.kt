package advent

import java.io.File

object Day08 {
    enum class Calculations(val calculate: (Int, Int) -> Int) { INC({ n, times -> n + times }), DEC({ n, times -> n - times }) }

    enum class Operator(val sign: String, val predicate: (Int, Int) -> Boolean) {
        BEQ(">=", { a, b -> a >= b }),
        B(">", { a, b -> a > b }),
        EQ("==", { a, b -> a == b }),
        NEQ("!=", { a, b -> a != b }),
        S("<", { a, b -> a < b }),
        SEQ("<=", { a, b -> a <= b });

        companion object {
            fun fromSign(sign: String): Operator? = values().find { it.sign == sign }
        }
    }

    data class Condition(val register: String, private val operator: Operator, private val number: Int) {
        fun calculate(registerValue: Int): Boolean = operator.predicate(registerValue, number)
    }

    data class Instruction(val register: String, val calculation: Calculations, val number: Int, val condition: Condition);


    fun firstStar(input: List<String>): Int = findMaximumValues(input.map { parseInstruction(it) }, mutableMapOf(), 0).first
            .maxBy { it.value }?.value ?: 0

    fun secondStar(input: List<String>): Int = findMaximumValues(input.map { parseInstruction(it) }, mutableMapOf(), 0).second


    private tailrec fun findMaximumValues(instructions: List<Instruction>, registers: MutableMap<String, Int>, highest: Int): Pair<MutableMap<String, Int>, Int> {
        if (instructions.isEmpty())
            return Pair(registers, highest)
        val elem = instructions[0]
        val condition = elem.condition.calculate(registers[elem.condition.register] ?: 0)

        return if (condition) {
            val newValue = elem.calculation.calculate(registers[elem.register] ?: 0, elem.number)
            registers[elem.register] =  newValue
            findMaximumValues(instructions.drop(1), registers, Math.max(highest, newValue))
        } else findMaximumValues(instructions.drop(1), registers, highest)
    }

    private fun parseInstruction(line: String): Instruction {
        val parts = line.split(Regex("\\s+"))
        val condition = Condition(parts[4], Operator.fromSign(parts[5])!!, parts[6].toInt())
        return Instruction(parts[0], Calculations.valueOf(parts[1].toUpperCase()), parts[2].toInt(), condition)
    }

}

fun main(args: Array<String>) {
    val input = File("src/advent/Day08-input").readLines()
    println(Day08.firstStar(input))
    println(Day08.secondStar(input))
}