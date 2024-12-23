import kotlin.math.absoluteValue
import kotlin.math.atan2

typealias DIRECTION = (Point2D) -> Point2D

data class Point2D(val x:Int, val y:Int): Comparable<Point2D> {
    fun adjacent(): Sequence<Point2D> = sequence {
        DIRECTIONS.map { yield(it(this@Point2D)) }
    }
    fun diag(): Sequence<Point2D> = sequence {
        DIRECTIONSDIAG.map { yield(it(this@Point2D)) }
    }
    operator fun times(other: Point2D) = Point2D(this.x * other.x, this.y * other.y)

    operator fun times(other: Int) = Point2D(this.x * other, this.y * other)

    operator fun div(other: Point2D) = Point2D(this.x / other.x, this.y / other.y)

    operator fun rem(other: Point2D) = Point2D(this.x % other.x, this.y % other.y)

    operator fun plus(other: Point2D) = Point2D(this.x + other.x, this.y + other.y)

    operator fun minus(other: Point2D) = Point2D(this.x - other.x, this.y - other.y)

    fun surrounding(inclusive: Boolean = true): List<Point2D> {
        return listOf(
            NORTHWEST(this),
            NORTH(this),
            NORTHEAST(this),
            WEST(this),
            this.copy(),
            EAST(this),
            SOUTHWEST(this),
            SOUTH(this),
            SOUTHEAST(this)
        ).filter { inclusive || it != this}
    }

    fun flip(): Point2D = Point2D(-x, y)

    fun manhattan(): Int {
        return x.absoluteValue + y.absoluteValue
    }

    fun distance(other: Point2D): Int {
        return x.coerceAtLeast(other.x) - x.coerceAtMost(other.x) + y.coerceAtLeast(other.y) - y.coerceAtMost(other.y)
    }

    fun move(direction: Point2D, times: Int=1): Point2D = move( { Point2D(it.x+direction.x, it.y + direction.y) }, times)

    fun move(direction: (Point2D) -> Point2D, times: Int=1): Point2D {
        return direction.repeated(times).fold(this) { acc, func -> func(acc) }
    }

    fun rotate(degrees:Int): Point2D {
        return when(degrees) {
            90  -> Point2D(y, -x)
            270 -> Point2D(-y, x)
            180 -> Point2D(-x, -y)
            0 -> Point2D(x, y)
            else -> throw IllegalArgumentException("Degrees must be 0, 90, 180, 270. Was: $degrees")
        }
    }

    fun allPointsTo(other: Point2D) = sequence {
        for(x in this@Point2D.x.coerceAtMost(other.x)..this@Point2D.x.coerceAtLeast(other.x)) {
            for(y in this@Point2D.y.coerceAtMost(other.y)..this@Point2D.y.coerceAtLeast(other.y)) {
                yield(Point2D(x, y))
            }
        }
    }
    fun isIn(a: Point2D, b: Point2D) = this.x >= a.x.coerceAtMost(b.x) &&
            this.x <= a.x.coerceAtLeast(b.x) &&
            this.y >= a.y.coerceAtMost(b.y) &&
            this.y <= a.y.coerceAtLeast(b.y)

    companion object {
        val NORTH: (Point2D) -> Point2D =        { Point2D(x = it.x,      y = it.y - 1  ) }
        val NORTHEAST: (Point2D) -> Point2D =    { Point2D(x = it.x + 1,  y = it.y - 1  ) }
        val EAST: (Point2D) -> Point2D =         { Point2D(x = it.x + 1,  y = it.y      ) }
        val SOUTHEAST: (Point2D) -> Point2D =    { Point2D(x = it.x + 1,  y = it.y + 1  ) }
        val SOUTH: (Point2D) -> Point2D =        { Point2D(x = it.x,      y = it.y + 1  ) }
        val SOUTHWEST: (Point2D) -> Point2D =    { Point2D(x = it.x - 1,  y = it.y + 1  ) }
        val WEST: (Point2D) -> Point2D =         { Point2D(x = it.x - 1,  y = it.y      ) }
        val NORTHWEST: (Point2D) -> Point2D =    { Point2D(x = it.x - 1,  y = it.y - 1  ) }

        val DIRECTIONS: List<DIRECTION> = listOf(
            NORTH, EAST, SOUTH, WEST
        )
        val DIRECTIONSDIAG: List<DIRECTION> = listOf(
            NORTHEAST, SOUTHEAST, SOUTHWEST, NORTHWEST
        )
        val ALLDIRECTIONS = DIRECTIONS + DIRECTIONSDIAG

        fun fromString(coords: String): Point2D {
            val (x, y) = coords.splitIgnoreEmpty(",").map { it.toInt() }
            return Point2D(x, y)
        }
        fun DIRECTION.rightTurn(): DIRECTION = when(this) {
            NORTH -> EAST
            EAST -> SOUTH
            SOUTH -> WEST
            WEST -> NORTH
            else -> throw IllegalArgumentException()
        }
        fun DIRECTION.leftTurn(): DIRECTION = when(this) {
            NORTH -> WEST
            EAST -> NORTH
            SOUTH -> EAST
            WEST -> SOUTH
            else -> throw IllegalArgumentException()
        }
        fun heuristic(start: Point2D, finish: Point2D): Int {
            val dx = (start.x - finish.x).absoluteValue
            val dy = (start.y - finish.y).absoluteValue
            return (dx + dy) + (-2) * minOf(dx, dy)
        }
    }

    override fun compareTo(other: Point2D): Int {
        return if(this.y == other.y) {
            if(this.x == other.x) {
                0
            } else {
                this.x.compareTo(other.x)
            }
        } else {
            this.y.compareTo(other.y)
        }
    }
}

data class Point2DWide(val x: Long, val y: Long) {
    fun distance(other: Point2DWide): Long {
        return x.coerceAtLeast(other.x) - x.coerceAtMost(other.x) + y.coerceAtLeast(other.y) - y.coerceAtMost(other.y)
    }
    fun toDouble(): Point2DDouble {
        return Point2DDouble(x.toDouble(), y.toDouble())
    }
}

data class Point2DDouble(val x: Double, val y: Double)


fun Map<Point2D, Int>.print() {
    for(y in keys.minAndMaxOf { it.y }.let { it.first..it.second }) {
        for(x in keys.minAndMaxOf { it.x }.let { it.first..it.second }) {
            if((this[Point2D(x, y)]?:0) != 0) {
                print('░')
            } else {
                print(' ')
            }
        }
        println()
    }
}
fun Map<Point2D, Char>.printChars(empty:Char=' ') {
    for(y in keys.minAndMaxOf { it.y }.let { it.first..it.second }) {
        for(x in keys.minAndMaxOf { it.x }.let { it.first..it.second }) {
            print(this[Point2D(x, y)]?:empty)
        }
        println()
    }
}
fun Collection<Point2D>.print() {
    for(y in this.minAndMaxOf { it.y }.let { it.first..it.second }) {
        for(x in this.minAndMaxOf { it.x }.let { it.first..it.second }) {
            if(this.contains(Point2D(x, y))) {
                print('░')
            } else {
                print('.')
            }
        }
        println()
    }
}
fun Char.toPipeChar() = when(this) {
    '-' -> '═'
    '|' -> '║'
    'F' -> '╔'
    '7' -> '╗'
    'J' -> '╝'
    'L' -> '╚'
    else -> this
}

fun DIRECTION.nice(): String {
    return when(this) {
        Point2D.NORTH -> "NORTH"
        Point2D.WEST -> "WEST"
        Point2D.SOUTH -> "SOUTH"
        Point2D.EAST -> "EAST"
        else -> throw IllegalArgumentException("Cannot map")
    }
}
