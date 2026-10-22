package shop

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed interface OrderEvent {
    data class Placed(val orderId: String, val totalCents: Long) : OrderEvent
    data class Notified(val orderId: String, val channel: String) : OrderEvent
}

// OBSERVER: subscribers collect the flow; the shop never knows who is listening
class OrderEvents {
    private val flow = MutableSharedFlow<OrderEvent>()
    val events: SharedFlow<OrderEvent> = flow.asSharedFlow()
    suspend fun publish(e: OrderEvent) = flow.emit(e)
}
