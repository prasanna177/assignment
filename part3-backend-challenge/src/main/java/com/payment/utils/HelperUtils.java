package com.payment.utils;

import java.util.Objects;
import java.util.UUID;

public class HelperUtils {
    public static long getStartingIndex(int pageNumber, int pageSize) {
        return Objects.nonNull(pageNumber) ?
                (long) pageSize * ((long) pageNumber != 0 ? pageNumber - 1 : 0) : 0;
    }

    public static synchronized String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static boolean isBlankOrNull(String str) {
        int strLen;
        if (str != null && (strLen = str.length()) != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(str.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }
}
