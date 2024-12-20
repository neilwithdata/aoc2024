package com.neilwithdata

import java.io.File

class Graph {
    private val nodes = mutableSetOf<Int>()

    // number -> numbers it points to (comes before)
    private val outgoingEdges = mutableMapOf<Int, MutableSet<Int>>()

    // number -> numbers that point to it
    private val incomingEdges = mutableMapOf<Int, MutableSet<Int>>()

    fun add(from: Int, to: Int) {
        nodes.add(from)
        nodes.add(to)

        // Update outgoing edges
        if (from in outgoingEdges) {
            outgoingEdges[from]!!.add(to)
        } else {
            outgoingEdges[from] = mutableSetOf(to)
        }

        // Update incoming edges
        if (to in incomingEdges) {
            incomingEdges[to]!!.add(from)
        } else {
            incomingEdges[to] = mutableSetOf(from)
        }
    }

    private fun removeEdge(from: Int, to: Int) {
        outgoingEdges[from]!!.remove(to)
        incomingEdges[to]!!.remove(from)
    }

    /**
    ~~ KAHN'S ALGORITHM - TOPOLOGICAL SORT ~~
    L ← Empty list that will contain the sorted elements
    S ← Set of all nodes with no incoming edge

    while S is not empty do
    remove a node n from S
    add n to L
    for each node m with an edge e from n to m do
    remove edge e from the graph
    if m has no other incoming edges then
    insert m into S

    if graph has edges then
    return error   (graph has at least one cycle)
    else
    return L   (a topologically sorted order)
     **/
    fun topologicalSort(): List<Int> {
        val sortedList = mutableListOf<Int>()

        val noIncoming = mutableSetOf<Int>()
        noIncoming.addAll(nodes.filter { it !in incomingEdges })

        while (noIncoming.isNotEmpty()) {
            val n = noIncoming.first()
            noIncoming.remove(n)

            sortedList.add(n)

            // find all the nodes m where n was incoming
            val connected = (outgoingEdges[n])?.toSet() ?: emptySet()
            for (m in connected) {
                // remove the edge from n -> m
                removeEdge(n, m)

                // does m have any other incoming edges?
                if (incomingEdges[m].isNullOrEmpty()) {
                    noIncoming.add(m)
                }
            }
        }

        return sortedList
    }
}

fun main() {
    val lines = File("data/day05_input.txt")
        .readLines()

    val rules: List<Pair<Int, Int>> = lines
        .takeWhile { it.contains("|") }
        .map { line ->
            val first = line.substringBefore('|').toInt()
            val second = line.substringAfter('|').toInt()

            first to second
        }

    val updates: List<List<Int>> = lines
        .filter { it.contains(",") }
        .map { line ->
            line.split(",").map { it.toInt() }
        }

    var correctTotal = 0
    var incorrectTotal = 0
    for (update in updates) {
        val updateMap = update
            .withIndex()
            .associate { (index, number) ->
                number to index
            }

        if (isUpdateValid(updateMap, rules)) {
            correctTotal += update[update.size / 2]
        } else {
            val graph = Graph().apply {
                // Build a graph using only those rules contained in the update
                for (rule in rules) {
                    if (rule.first in update && rule.second in update) {
                        add(rule.first, rule.second)
                    }
                }
            }

            // Requires re-ordering - Part 2
            val reordered = graph.topologicalSort()
                .filter { it in update }

            incorrectTotal += reordered[reordered.size / 2]
        }
    }

    // Part 1
    println(correctTotal)

    // Part 2
    println(incorrectTotal)
}

fun isUpdateValid(updateMap: Map<Int, Int>, rules: List<Pair<Int, Int>>): Boolean {
    for (rule in rules) {
        val firstIndex = updateMap[rule.first]
        val secondIndex = updateMap[rule.second]

        if (firstIndex != null && secondIndex != null) {
            if (firstIndex > secondIndex) {
                return false // update fails this rule
            }
        }
    }

    // All rules passed
    return true
}
