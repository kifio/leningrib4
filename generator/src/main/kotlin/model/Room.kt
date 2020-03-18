package model

private const val TREE_SIZE = 2

class Room(var y: Int,
           var height: Int,
           levelWidth: Int) {
    var treesPositions: MutableList<Int> = MutableList(levelWidth / 2) { i -> i * TREE_SIZE }

    var borderY: Int

    init {
        var borderY = y + (height - 2)
        if (borderY % 2 != 0) {
            if (height > 4) {
                borderY -= 1
                height -= 1
            } else {
                borderY += 1
                height += 1
            }
        }
        this.borderY = borderY
    }
}