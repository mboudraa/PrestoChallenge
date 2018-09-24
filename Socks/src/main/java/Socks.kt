import java.util.*

fun main(args: Array<String>) {
    println("Enter the socks list:")
    val input = Scanner(System.`in`)
    findSocksPairs(input.next())
}

private fun findSocksPairs(string: String) {
    val socks = string.split(",")

    val leftMap = TreeMap<String, Deque<String>>() // Use TreeMap to order by color, LinkedHashMap to keep list order, HashMap for faster runtime
    val rightMap = TreeMap<String, Deque<String>>() // Use TreeMap to order by color, LinkedHashMap to keep list order, HashMap for faster runtime

    //Group socks by side and color
    socks.forEach { sock ->
        val (id, color, side) = sock.split("/").run { Triple(this[0], this[1], this[2]) }
        when (side) {
            "left" -> leftMap[color] = leftMap.getOrDefault(color, ArrayDeque<String>(socks.size)).apply { add(id) }
            "right" -> rightMap[color] = rightMap.getOrDefault(color, ArrayDeque<String>(socks.size)).apply { add(id) }
        }
    }

    //Iterate over the shortest map for matched pairs
    val shortestMap = if (leftMap.size <= rightMap.size) leftMap else rightMap
    val longestMap = if (shortestMap == leftMap) rightMap else leftMap

    //print matched pairs
    shortestMap.forEach { color, shortestSocksStack ->
        longestMap[color]?.let { longestSocksStack ->
            while (shortestSocksStack.size > 0 && longestSocksStack.size > 0) {
                println("${shortestSocksStack.pop()} ${longestSocksStack.pop()}")
            }
        }
    }

    //print unmatched left socks ordered by color if TreeMap
    leftMap.forEach { _, stack ->
        while (stack.size > 0) {
            println(stack.pop())
        }
    }

    //print unmatched right socks ordered by color if TreeMap
    rightMap.forEach { _, stack ->
        while (stack.size > 0) {
            println(stack.pop())
        }
    }

}