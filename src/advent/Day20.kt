package advent

import java.io.File
import java.lang.Math.abs
import java.lang.Math.sqrt
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.roundToInt

/*--- Day 20: Particle Swarm ---

Suddenly, the GPU contacts you, asking for help. Someone has asked it to simulate too many particles, and it won't be able to finish them all in time to render the next frame at this rate.

It transmits to you a buffer (your puzzle input) listing each particle in order (starting with particle 0, then particle 1, particle 2, and so on). For each particle, it provides the X, Y, and Z coordinates for the particle's position (p), velocity (v), and acceleration (a), each in the format <X,Y,Z>.

Each tick, all particles are updated simultaneously. A particle's properties are updated in the following order:

Increase the X velocity by the X acceleration.
Increase the Y velocity by the Y acceleration.
Increase the Z velocity by the Z acceleration.
Increase the X position by the X velocity.
Increase the Y position by the Y velocity.
Increase the Z position by the Z velocity.
Because of seemingly tenuous rationale involving z-buffering, the GPU would like to know which particle will stay closest to position <0,0,0> in the long term. Measure this using the Manhattan distance, which in this situation is simply the sum of the absolute values of a particle's X, Y, and Z position.

For example, suppose you are only given two particles, both of which stay entirely on the X-axis (for simplicity). Drawing the current states of particles 0 and 1 (in that order) with an adjacent a number line and diagram of current X positions (marked in parenthesis), the following would take place:

p=< 3,0,0>, v=< 2,0,0>, a=<-1,0,0>    -4 -3 -2 -1  0  1  2  3  4
p=< 4,0,0>, v=< 0,0,0>, a=<-2,0,0>                         (0)(1)

p=< 4,0,0>, v=< 1,0,0>, a=<-1,0,0>    -4 -3 -2 -1  0  1  2  3  4
p=< 2,0,0>, v=<-2,0,0>, a=<-2,0,0>                      (1)   (0)

p=< 4,0,0>, v=< 0,0,0>, a=<-1,0,0>    -4 -3 -2 -1  0  1  2  3  4
p=<-2,0,0>, v=<-4,0,0>, a=<-2,0,0>          (1)               (0)

p=< 3,0,0>, v=<-1,0,0>, a=<-1,0,0>    -4 -3 -2 -1  0  1  2  3  4
p=<-8,0,0>, v=<-6,0,0>, a=<-2,0,0>                         (0)
At this point, particle 1 will never be closer to <0,0,0> than particle 0, and so, in the long run, particle 0 will stay closest.

Which particle will stay closest to position <0,0,0> in the long term?*/

/*--- Part Two ---

To simplify the problem further, the GPU would like to remove any particles that collide. Particles collide if their positions ever exactly match. Because particles are updated simultaneously, more than two particles can collide at the same time and place. Once particles collide, they are removed and cannot collide with anything else after that tick.

For example:

p=<-6,0,0>, v=< 3,0,0>, a=< 0,0,0>
p=<-4,0,0>, v=< 2,0,0>, a=< 0,0,0>    -6 -5 -4 -3 -2 -1  0  1  2  3
p=<-2,0,0>, v=< 1,0,0>, a=< 0,0,0>    (0)   (1)   (2)            (3)
p=< 3,0,0>, v=<-1,0,0>, a=< 0,0,0>

p=<-3,0,0>, v=< 3,0,0>, a=< 0,0,0>
p=<-2,0,0>, v=< 2,0,0>, a=< 0,0,0>    -6 -5 -4 -3 -2 -1  0  1  2  3
p=<-1,0,0>, v=< 1,0,0>, a=< 0,0,0>             (0)(1)(2)      (3)
p=< 2,0,0>, v=<-1,0,0>, a=< 0,0,0>

p=< 0,0,0>, v=< 3,0,0>, a=< 0,0,0>
p=< 0,0,0>, v=< 2,0,0>, a=< 0,0,0>    -6 -5 -4 -3 -2 -1  0  1  2  3
p=< 0,0,0>, v=< 1,0,0>, a=< 0,0,0>                       X (3)
p=< 1,0,0>, v=<-1,0,0>, a=< 0,0,0>

------destroyed by collision------
------destroyed by collision------    -6 -5 -4 -3 -2 -1  0  1  2  3
------destroyed by collision------                      (3)
p=< 0,0,0>, v=<-1,0,0>, a=< 0,0,0>
In this example, particles 0, 1, and 2 are simultaneously destroyed at the time and place marked X. On the next tick, particle 3 passes through unharmed.

How many particles are left after all collisions are resolved?*/

data class Point3d(val x: Int, val y: Int, val z: Int) {
    operator fun plus(point: Point3d) = Point3d(x + point.x, y + point.y, z + point.z)
}

typealias Position = Point3d
typealias Velocity = Point3d
typealias Acceleration = Point3d

object Day20 {
    data class Particle(val position: Position, val velocity: Velocity, val acceleration: Acceleration)

    private val pattern: Pattern = Regex("p=<(-?\\d+),(-?\\d+),(-?\\d+)>, v=<(-?\\d+),(-?\\d+),(-?\\d+)>, a=<(-?\\d+),(-?\\d+),(-?\\d+)>").toPattern()

    fun firstStar(input: List<String>): Int =
            parseParticles(input)
                    .map { particle ->  particle.acceleration }
                    .map { acceleration -> abs(acceleration.x) + abs(acceleration.y) + abs(acceleration.z) }
                    .withIndex()
                    .minBy { it.value }!!.index


    fun secondStar(input: List<String>): Int {
        var particles = parseParticles(input)
        var bruteForceAttempts = 10_000

        while (--bruteForceAttempts > 0) {
            val grouped: Map<Position, List<Particle>> = particles.groupBy { it.position }
            val collision: Set<Position> = grouped.filter { it.value.size > 1 }.keys

            particles = particles
                    .filter { !collision.contains(it.position) }
                    .map {
                        val newVelocity = it.velocity + it.acceleration
                        Particle(it.position + newVelocity, newVelocity, it.acceleration)
                    }
        }

        return particles.size
    }


    private fun parseParticles(input: List<String>): List<Particle> =
            input.map { line -> pattern.matcher(line) }
                    .map { matcher -> matcher.findGroups() }
                    .map { groups -> groups.map { it.toInt() } }
                    .map { Particle(Position(it[0], it[1], it[2]), Velocity(it[3], it[4], it[5]), Acceleration(it[6], it[7], it[8])) }


    private fun Matcher.findGroups(): List<String> =
            find().let { (1..groupCount()).map { group(it) } }
}

fun main(args: Array<String>) {
    val input = File("src/advent/Day20-input").readLines()

    println(Day20.firstStar(input))
    println(Day20.secondStar(input))
}