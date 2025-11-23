package com.payment.utils;

import java.util.Objects;

public class HelperUtils {
    public static long getStartingIndex(int pageNumber, int pageSize) {
        return Objects.nonNull(pageNumber) ?
                (long) pageSize * ((long) pageNumber != 0 ? pageNumber - 1 : 0) : 0;
    }
}
