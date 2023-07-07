package cz.zvirdaniel.smarthome.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Predicate;
import java.util.function.Supplier;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ControlUtil {
    @SneakyThrows
    public static <T> T retryBlock(
            @NonNull String operationName,
            @NonNull Supplier<T> resultSupplier,
            @NonNull Predicate<T> successCondition,
            int maxRetryCount,
            int waitMillis
    ) {
        for (int i = 0; i < maxRetryCount; i++) {
            final T result = resultSupplier.get();
            if (successCondition.test(result)) {
                return result;
            }
            if (waitMillis > 0) {
                log.info("{} didn't meet all conditions, retrying after {}ms", operationName, waitMillis);
                Thread.sleep(waitMillis);
            } else {
                log.info("{} didn't meet all conditions, retrying...", operationName);
            }
        }

        return null;
    }
}
