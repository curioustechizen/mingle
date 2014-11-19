package mingle.processor.util;

import java.util.Random;

public class NameUtils {
    private static final Random RANDOM = new Random();
    public static String variableNameForClass(String fullyQualifiedName) {
        return new StringBuilder("__mingle_")
                .append(fullyQualifiedName.replace(".", "_"))
                .append("_$$")
                .append(RANDOM.nextInt(255))
                .toString();

    }
}
