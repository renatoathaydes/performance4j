package com.athaydes.performance4j.chart;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Result<T> {

    private final T value;
    private final Throwable error;

    public Result(T value) {
        this.value = value;
        this.error = null;
    }

    public Result(Throwable error) {
        Objects.requireNonNull(error);
        this.error = error;
        this.value = null;
    }

    public void use(Consumer<T> successAction, Consumer<Throwable> errorAction) {
        if (error == null) {
            successAction.accept(value);
        } else {
            errorAction.accept(error);
        }
    }

    public <R> R use(Function<T, R> successAction, Function<Throwable, R> errorAction) {
        if (error == null) {
            return successAction.apply(value);
        } else {
            return errorAction.apply(error);
        }
    }
}
