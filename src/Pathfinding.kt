import java.util.*

object Pathfinding {

    /**
     * Example:
     *                 val path = Pathfinding.aStar(
     *                     start = Point2D(0, 0),
     *                     finish = Point2D(mapSize - 1, mapSize - 1),
     *                     map = map,
     *                     heuristic = {a, b -> a.distance(b)},
     *                     adjacent = {coord, map -> coord.adjacent().filter { !map.containsKey(it) }.toList()},
     *                     moveCost = {_, _ -> 1 })
     */
    fun <T, A> aStar(
        start: T,
        finish: T,
        map: Map<T, A>,
        heuristic: (T, T) -> Int,
        adjacent: (T, Map<T, A>)-> List<T>,
        moveCost: (T, T) -> Int
    ): Triple<Boolean, List<T>, Int>
    where T : Comparable<T> {

        // Helper function
        fun generatePath(currentPos: T, cameFrom: Map<T, T>): List<T> {
            val path = mutableListOf(currentPos)
            var current = currentPos
            while (cameFrom.containsKey(current)) {
                current = cameFrom.getValue(current)
                path.add(0, current)
            }
            return path.toList()
        }

        val MAX_SCORE = 9999999
        val openVertices = TreeSet(compareBy<Pair<T, Int>> {it.second}.thenBy { it.first })
        openVertices.add(Pair(start, heuristic(start, finish)))
        val closedVertices = mutableSetOf<T>()
        val costFromStart = mutableMapOf(start to 0)
        val estimatedTotalCost = mutableMapOf(start to heuristic(start, finish))
        val cameFrom = mutableMapOf<T, T>()  // Used to generate path by back tracking

        while (openVertices.isNotEmpty()) {

            val currentPos = openVertices.pollFirst()!!.first
            //val currentPos = openVertices.poll().first

            // Check if we have reached the finish
            if (currentPos == finish) {
                // Backtrack to generate the most efficient path
                val path = generatePath(currentPos, cameFrom)
                return Triple(true, path, estimatedTotalCost.getValue(finish)) // First Route to finish will be optimum route
            }

            closedVertices.add(currentPos)

            adjacent(currentPos, map)
                .filterNot { closedVertices.contains(it) }  // Exclude previous visited vertices
                .forEach { neighbour ->
                    val score = costFromStart.getValue(currentPos) + moveCost(currentPos, neighbour)
                    if (score < costFromStart.getOrDefault(neighbour, MAX_SCORE)) {
                        val estimatedCost = score + heuristic(neighbour, finish)
                        openVertices.add(neighbour to estimatedCost)
                        cameFrom[neighbour] = currentPos
                        costFromStart[neighbour] = score
                        estimatedTotalCost[neighbour] = score + heuristic(neighbour, finish)
                    }
                }

        }
        return Triple(false, listOf(), 0)
    }
}