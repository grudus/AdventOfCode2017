package advent

import java.io.File
import java.lang.Math.abs
import java.lang.Math.max

/*Crossing the bridge, you've barely reached the other side of the stream when a program comes up to you, clearly in distress. "It's my child process," she says, "he's gotten lost in an infinite grid!"

Fortunately for her, you have plenty of experience with infinite grids.

Unfortunately for you, it's a hex grid.

The hexagons ("hexes") in this grid are aligned such that adjacent hexes can be found to the north, northeast, southeast, south, southwest, and northwest:

  \ n  /
nw +--+ ne
  /    \
-+      +-
  \    /
sw +--+ se
  / s  \
You have the path the child process took. Starting where he started, you need to determine the fewest number of steps required to reach him. (A "step" means to move from the hex you are in to any adjacent hex.)

For example:

ne,ne,ne is 3 steps away.
ne,ne,sw,sw is 0 steps away (back where you started).
ne,ne,s,s is 2 steps away (se,se).
se,sw,se,sw,sw is 3 steps away (s,s,sw).
Your puzzle answer was 698.

--- Part Two ---

How many steps away is the furthest he ever got from his starting position?
*/

object Day11 {
    data class Position(val x: Int, val y: Int, val z: Int) {
        fun distance() = max(abs(x), max(abs(y), abs(z)))
    }

    data class PositionHistory(val currentPosition: Position, val allPositions: List<Position> = listOf(currentPosition))

    enum class Direction(val dx: Int, val dy: Int) {
        N(0, 1),
        NE(-1, 1),
        SE(-1, 0),
        S(0, -1),
        SW(1, -1),
        NW(1, 0)
    }

    fun firstStar(input: List<String>): Int =
            calculatePosition(input, Position(0, 0, 0)).currentPosition.distance()

    fun secondStar(input: List<String>) =
            calculatePosition(input, Position(0, 0, 0))
                    .allPositions.maxBy { it.distance() }?.distance()


    private fun calculatePosition(input: List<String>, startPosition: Position): PositionHistory =
            input.map { Direction.valueOf(it.toUpperCase()) }
                    .fold(PositionHistory(startPosition)) { (position, allPositions), direction ->
                        val newX = position.x + direction.dx
                        val newY = position.y + direction.dy
                        val newPos = Position(newX, newY, 0 - (newX + newY))
                        PositionHistory(newPos, allPositions + newPos)
                    }
}

fun main(args: Array<String>) {
    val input = File("src/advent/Day11-input").readText().split(",")

    println(Day11.firstStar(input))
    println(Day11.secondStar(input))
}