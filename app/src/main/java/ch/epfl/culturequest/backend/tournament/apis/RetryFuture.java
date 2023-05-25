package ch.epfl.culturequest.backend.tournament.apis;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

public class RetryFuture {

    public static <T> CompletableFuture<T> ExecWithRetryOrFallback(Supplier<CompletableFuture<T>> action, Supplier<T> fallback, int MAX_RETRIES) {
        return ExecWithRetryOrFallbackHelper(action, fallback, MAX_RETRIES, 0);
    }

    private static <T> CompletableFuture<T> ExecWithRetryOrFallbackHelper(Supplier<CompletableFuture<T>> action, Supplier<T> fallback, int MAX_RETRIES, int attempt) {
        if (attempt >= MAX_RETRIES) {
            return CompletableFuture.completedFuture(fallback.get());
        }

        return action.get().handle((result, ex) -> {
            if (ex == null) {
                return CompletableFuture.completedFuture(result);
            } else {
                return ExecWithRetryOrFallbackHelper(action, fallback, MAX_RETRIES, attempt + 1);
            }
        }).thenCompose(Function.identity());
    }
}
