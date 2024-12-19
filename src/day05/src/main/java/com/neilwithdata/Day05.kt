package com.neilwithdata

import java.io.File

fun main() {
    val lines = File("data/day05_input.txt")
        .readLines()

    // convert the rules into a list of pairs
    val rules = lines
        .takeWhile { it.contains("|") }
        .map { line ->
            val first = line.substringBefore('|').toInt()
            val second = line.substringAfter('|').toInt()

            first to second
        }

    val chain = processRules(rules)

    // test each of the rules against the chain we constructed
    for (rule in rules) {
        val firstIndex = chain.indexOf(rule.first)
        val secondIndex = chain.indexOf(rule.second)

        if (secondIndex < firstIndex) {
            println("Rule $rule not captured properly in the chain")
            return
        }
    }

    // List<List<Int>>
    val updates = lines
        .filter { it.contains(",") }
        .map { line ->
            line.split(",").map { it.toInt() }
        }

    var total = 0
    for (update in updates) {
        var updateIndex = 0

        for (c in chain) {
            if (c == update[updateIndex]) {
                updateIndex++
                if (updateIndex >= update.size) {
                    // Correct update. Following the rule chain sequentially we matched all the nums in update.
                    total += update[update.size / 2]
                    break
                }
            }
        }
    }

    println(total)
}

fun processRules(rules: List<Pair<Int, Int>>): List<Int> {
    val chains = mutableListOf<MutableList<Int>>()

    for (rule in rules) {
        println("Processing rule ${rule.first}|${rule.second}")

        // Which existing chains contain the values from the rule
        val first = chains.firstOrNull { it.contains(rule.first) }
        val second = chains.firstOrNull { it.contains(rule.second) }

        if (first == null && second == null) {
            // there are no chains that contain these numbers - make a new chain
            chains.add(mutableListOf(rule.first, rule.second))
        } else if (first == null) {
            // Add rule.first to start of the second
            second!!.add(0, rule.first)
        } else if (second == null) {
            // Add rule.second to the end of first
            first.add(rule.second)
        } else {
            if (first === second) {
                // Re-order within same chain (if necessary)
                val firstIndex = first.indexOf(rule.first)
                val secondIndex = first.indexOf(rule.second)

                if (firstIndex > secondIndex) {
                    first.removeAt(firstIndex)
                    first.add(secondIndex, rule.first)
                }
            } else {
                // they're in different chains - add second to the end of first (and delete second)
                chains.remove(second)
                first.addAll(second)
            }
        }

        displayChains(chains)
    }

    // All rules always flatten neatly into a single chain
    check(chains.size == 1)
    return chains[0]
}

fun displayChains(chains: List<List<Int>>) {
    println("Chains are now:")
    for ((index, chain) in chains.withIndex()) {
        println("$index: ${chain.joinToString()}")
    }
    println()
}
