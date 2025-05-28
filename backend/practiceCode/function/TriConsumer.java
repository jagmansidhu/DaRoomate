//package com.roomate.app.function;
//
//import java.util.Objects;
//
//@FunctionalInterface
//public interface TriConsumer<T, U, V> {
//
//    void accept(T t, U u, V v);
//
//    /**
//     * Returns a composed {@code Consumer} that performs, in sequence, this
//     * operation followed by the {@code after} operation. If performing either
//     * operation throws an exception, it is relayed to the caller of the
//     * composed operation.  If performing this operation throws an exception,
//     * the {@code after} operation will not be performed.
//     *
//     * @param after the operation to perform after this operation
//     * @return a composed {@code Consumer} that performs in sequence this
//     * operation followed by the {@code after} operation
//     * @throws NullPointerException if {@code after} is null
//     */
//    default TriConsumer<T,U,V> andThen(TriConsumer<? super T, ? super U, ? super V> after) {
//        Objects.requireNonNull(after);
//        return (T t, U u, V v) -> { accept(t, u,v); after.accept(t, u, v); };
//    }
//}
