package kifio.leningrib.levels.helpers

import kifio.leningrib.model.actors.fixed.Bottle

class BottleManager {

    val bottles = ArrayList<Bottle>()
    val removableBottles = ArrayList<Bottle>()

    fun updateBottles() {
        removableBottles.clear()

        for (b in bottles) {
            if (b.isRemovable()) {
                b.remove()
                removableBottles.add(b)
            }
        }

        bottles.removeAll(removableBottles)
    }

    fun addBottle(bottle: Bottle) {
        bottles.add(bottle)
    }
}