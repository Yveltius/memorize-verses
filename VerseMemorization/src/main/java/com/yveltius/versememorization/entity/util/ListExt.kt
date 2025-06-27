package com.yveltius.versememorization.entity.util

fun List<String>.containsAllIgnoreCase(other: List<String>): Boolean {
    return other.all { otherItem ->
        this.any { thisItem -> thisItem.equals(otherItem, ignoreCase = true) }
    }
}