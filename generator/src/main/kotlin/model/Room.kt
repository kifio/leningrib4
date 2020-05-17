package model

private const val TREE_SIZE = 2

class Room(var y: Int,
           var height: Int,
           levelWidth: Int) {
    var treesPositions: MutableList<Int> = MutableList(levelWidth / 2) { i -> i * TREE_SIZE }
}