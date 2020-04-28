package kifio.leningrib.levels.helpers

import kifio.leningrib.model.items.Bottle

class BottleManager {

    val bottles = ArrayList<Bottle>()

    fun updateBottles() {
        for (b in bottles) {
            if (b.isRemovable()) {
                b.remove()
                bottles.remove(b)
            }
        }
    }
}