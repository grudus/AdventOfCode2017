package advent

import java.awt.Point
import java.io.File

/*--- Day 19: A Series of Tubes ---

Somehow, a network packet got lost and ended up here. It's trying to follow a routing diagram (your puzzle input),
but it's confused about where to go.

Its starting point is just off the top of the diagram. Lines (drawn with |, -, and +) show the path it needs to take,
starting by going down onto the only line connected to the top of the diagram. It needs to follow this path until it reaches
the end (located somewhere within the diagram) and stop there.

Sometimes, the lines cross over each other; in these cases, it needs to continue going the same direction,
and only turn left or right when there's no other option. In addition, someone has left letters on the line;
these also don't change its direction, but it can use them to keep track of where it's been. For example:

     |
     |  +--+
     A  |  C
 F---|----E|--+
     |  |  |  D
     +B-+  +--+

Given this diagram, the packet needs to take the following path:

Starting at the only line touching the top of the diagram, it must go down, pass through A, and continue onward to the first +.
Travel right, up, and right, passing through B in the process.
Continue down (collecting C), right, and up (collecting D).
Finally, go all the way left through E and stopping at F.
Following the path to the end, the letters it sees on its path are ABCDEF.

The little packet looks up at you, hoping you can help it find the way. What letters will it see (in the order it would see them)
if it follows the path? (The routing diagram is very wide; make sure you view it without line wrapping.)*/
/*--- Part Two ---

The packet is curious how many steps it needs to go.

For example, using the same routing diagram from the example above...

     |
     |  +--+
     A  |  C
 F---|--|-E---+
     |  |  |  D
     +B-+  +--+

...the packet would go:

6 steps down (including the first line at the top of the diagram).
3 steps right.
4 steps up.
3 steps right.
4 steps down.
3 steps right.
2 steps up.
13 steps left (including the F it stops on).
This would result in a total of 38 steps.

How many steps does the packet need to go?

*/

object Day19 {
    private fun <T> List<List<T>>.contains(point: Point) = point.x >= 0 && point.y >= 0 && point.y < size && point.x < get(point.y).size
    private fun <T> List<List<T>>.get(point: Point) = get(point.y)[point.x]

    private fun Point.next(direction: Direction) = Point(x + direction.dx, y + direction.dy)

    data class DiagramInfo(val letters: String, val steps: Int)

    enum class Direction(val dx: Int, val dy: Int) {
        UP(0, -1),
        RIGHT(1, 0),
        DOWN(0, 1),
        LEFT(-1, 0)
    }

    fun firstStar(input: List<String>) = walkThroughDiagram(input).letters
    fun secondStar(input: List<String>) = walkThroughDiagram(input).steps


    private fun walkThroughDiagram(input: List<String>): DiagramInfo {
        val diagram: List<List<Char>> = input.map { it.toList() }
        val initialPosition = Point(diagram[0].indexOf('|'), 0)

        tailrec fun doWalk(position: Point, direction: Direction, letters: String, steps: Int): DiagramInfo {
            if (!diagram.contains(position))
                return DiagramInfo(letters, steps)

            val element = diagram.get(position)
            return when (element) {
                '|', '-' -> doWalk(position.next(direction), direction, letters, steps + 1)
                '+' -> {
                    val dir = if (direction.dx == 0) {
                        val rightPoint = Point(position.x + 1, position.y)
                        if (diagram.contains(rightPoint) && diagram.get(rightPoint) != ' ') Direction.RIGHT else Direction.LEFT
                    } else {
                        val topPoint = Point(position.x, position.y - 1)
                        if (diagram.contains(topPoint) && diagram.get(topPoint) != ' ') Direction.UP else Direction.DOWN
                    }
                    doWalk(position.next(dir), dir, letters, steps + 1)
                }
                in 'A'..'Z', in 'a'..'z' -> doWalk(position.next(direction), direction, letters + element, steps + 1)
                else -> DiagramInfo(letters, steps)
            }
        }
        return doWalk(initialPosition, Direction.DOWN, "", 0)
    }


}

fun main(args: Array<String>) {
    val input = File("src/advent/Day19-input").readLines()
    println(Day19.firstStar(input))
    println(Day19.secondStar(input))
}