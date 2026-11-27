package lending.adapters

import catalogue.api.CatalogueEvent
import lending.domain.CopyId
import lending.domain.Shelf
import lending.domain.ShelfCopy
import platform.EventBus
import platform.subscribe

// The anti-corruption layer: the only lending class that knows the catalogue exists.
// It translates the catalogue's published events into lending's own model.
class CatalogueListener(private val shelf: Shelf) {

    fun subscribeTo(bus: EventBus) {
        bus.subscribe<CatalogueEvent.CopyAdded> { shelf.save(ShelfCopy(CopyId(it.barcode), it.title)) }
        bus.subscribe<CatalogueEvent.CopyWithdrawn> { event ->
            shelf.find(CopyId(event.barcode))?.let { shelf.save(it.copy(withdrawn = true)) }
        }
    }
}
