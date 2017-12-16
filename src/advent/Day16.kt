package advent

import java.io.File

/*--- Day 16: Permutation Promenade ---

You come upon a very unusual sight; a group of programs here appear to be dancing.

There are sixteen programs in total, named a through p. They start by standing in a line: a stands in position 0, b stands in position 1, and so on until p, which stands in position 15.

The programs' dance consists of a sequence of dance moves:

Spin, written sX, makes X programs move from the end to the front, but maintain their order otherwise. (For example, s3 on abcde produces cdeab).
Exchange, written xA/B, makes the programs at positions A and B swap places.
Partner, written pA/B, makes the programs named A and B swap places.
For example, with only five programs standing in a line (abcde), they could do the following dance:

s1, a spin of size 1: eabcd.
x3/4, swapping the last two programs: eabdc.
pe/b, swapping programs e and b: baedc.
After finishing their dance, the programs end up in order baedc.

You watch the dance for a while and record their dance moves (your puzzle input). In what order are the programs standing after their dance?

*/
/*--- Part Two ---

Now that you're starting to get a feel for the dance moves, you turn your attention to the dance as a whole.

Keeping the positions they ended up in from their previous dance, the programs perform it again and again: including the first dance, a total of one billion (1000000000) times.

In the example above, their second dance would begin with the order baedc, and use the same dance moves:

s1, a spin of size 1: cbaed.
x3/4, swapping the last two programs: cbade.
pe/b, swapping programs e and b: ceadb.
In what order are the programs standing after their billion dances?*/

fun <T> Array<T>.rotate() {
    val temp = this[size - 1]
    ((size - 1) downTo 1).forEach { this[it] = this[it - 1] }
    this[0] = temp
}


object Day16 {
    abstract class Move {
        abstract fun dance(programs: Array<Char>)
    }

    data class Spin(private val programsToMove: Int) : Move() {
        override fun dance(programs: Array<Char>) =
                (0 until programsToMove).forEach { programs.rotate() }

    }

    data class Exchange(private val aSwap: Int, private val bSwap: Int) : Move() {
        override fun dance(programs: Array<Char>) =
                programs.swap(aSwap, bSwap)

    }

    data class Partner(private val aSwap: Char, private val bSwap: Char) : Move() {
        override fun dance(programs: Array<Char>) =
                programs.swap(programs.indexOf(aSwap), programs.indexOf(bSwap))

    }


    private val ONE_BILLION = 1_000_000_000

    fun firstStar(input: List<Move>): String {
        val programs = ('a'..'p').toList().toTypedArray()
        input.forEach { it.dance(programs) }
        return programs.joinToString("")
    }

    fun secondStar(input: List<Move>): String {
        val programsInit = ('a'..'p').toList().toTypedArray()
        val programs = programsInit.copyOf()

        val loopIndex = (0 until ONE_BILLION).find {
            input.forEach { it.dance(programs) }
            programs contentEquals programsInit
        } !!

        val divider = ONE_BILLION / (loopIndex+1)
        val loopStart = (loopIndex+1) * divider

        (loopStart until ONE_BILLION).forEach {
            input.forEach { it.dance(programsInit) }
        }

        return programsInit.joinToString("")
    }
}

fun main(args: Array<String>) {
    val input = File("src/advent/Day16-input").readText().split(",")
            .map {
                when (it[0]) {
                    's' -> Day16.Spin(it.substring(1).toInt())
                    'x' -> it.substring(1).split("/").let {
                        Day16.Exchange(it[0].toInt(), it[1].toInt())
                    }
                    else -> Day16.Partner(it[1], it[3])
                }
            }
    println(Day16.firstStar(input))
    println(Day16.secondStar(input))
}