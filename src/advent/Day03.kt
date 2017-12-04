package advent

/*You come across an experimental new kind of memory stored on an infinite two-dimensional grid.

Each square on the grid is allocated in a spiral pattern starting at a location marked 1 and then counting up while spiraling outward. For example, the first few squares are allocated like this:
65  64  63  62  61  60  59  58  57
66  37  36  35  34  33  32  31  56
67  38  17  16  15  14  13  30  55
68  39  18   5   4   3  12  29  54
69  40  19   6   1   2  11  28  53
70  41  20   7   8   9  10  27  52
71  42  21  22  23  24  25  26  51
72  43  44  45  46  47  48  49  50
73  74  75  76  77  78  79  80  81 ->
While this is very space-efficient (no squares are skipped), requested data must be carried back to square 1 (the location of the only access port for this memory system) by programs that can only move up, down, left, or right. They always take the shortest path: the Manhattan Distance between the location of the data and square 1.

For example:

Data from square 1 is carried 0 steps, since it's at the access port.
Data from square 12 is carried 3 steps, such as: down, left, left.
Data from square 23 is carried only 2 steps: up twice.
Data from square 1024 must be carried 31 steps.
How many steps are required to carry the data from the square identified in your puzzle input all the way to the access port?

*/

/*As a stress test on the system, the programs here clear the grid and then store the value 1 in square 1. Then, in the same allocation order as shown above, they store the sum of the values in all adjacent squares, including diagonals.

So, the first few squares' values are chosen as follows:

Square 1 starts with the value 1.
Square 2 has only one adjacent filled square (with value 1), so it also stores 1.
Square 3 has both of the above squares as neighbors and stores the sum of their values, 2.
Square 4 has all three of the aforementioned squares as neighbors and stores the sum of their values, 4.
Square 5 only has the first and fourth squares as neighbors, so it gets the value 5.
Once a square is written, its value does not change. Therefore, the first few squares would receive the following values:

147  142  133  122   59
304    5    4    2   57
330   10    1    1   54
351   11   23   25   26
362  747  806--->   ...
What is the first value written that is larger than your puzzle input?

*/

object Day03 {
    fun findDepth(number: Int): Int {
        tailrec fun depth(currentLevel: Int): Int =
                if (currentLevel * currentLevel < number) depth(currentLevel + 2) else currentLevel
        return depth(1)
    }

    fun distanceFromMiddle(number: Int, depth: Int): Int {
        val max = depth * depth
        val halfRow = depth / 2
        val distance = max - number
        return depth / 2 + distance - halfRow
    }

    fun firstStar(input: Int) = distanceFromMiddle(input, findDepth(input))


    fun secondStar(input: Int): Int {
        val size = 21
        val data = Array(size, { Array(size, { 0 }) })
        var row = size / 2
        var col = size / 2
        data[row][col] = 1
        var currentLevel = 3
        var indexInLevel = 1
        var direction = 0
        var number = 0
        col++
        while (number <= input) {
            number = calculate(data, row, col)
            data[row][col] = number

            when (direction) {
                0 -> row--
                1 -> col--
                2 -> row++
                3 -> col++
                5 -> {
                    col++; direction = 0
                }
            }

            indexInLevel++
            if (indexInLevel == currentLevel - 1) {
                indexInLevel = 0
                direction++
            }
            if (direction == 4) {
                direction = 5
                currentLevel += 2
            }
        }

        return number
    }

    private fun calculate(data: Array<Array<Int>>, row: Int, col: Int) =
            (-1..1).flatMap { r -> (-1..1).map { c -> Pair(r, c) } }
                    .sumBy { (r, c) -> data[row + r][col + c] }


}

fun main(args: Array<String>) {
    val input = 368078

    println(Day03.firstStar(input))
    println(Day03.secondStar(input))
}