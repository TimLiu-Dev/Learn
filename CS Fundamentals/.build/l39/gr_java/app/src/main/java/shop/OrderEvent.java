package shop;

public sealed interface OrderEvent {
    record Placed(String orderId, long totalCents) implements OrderEvent {}
    record Notified(String orderId, String channel) implements OrderEvent {}
}
