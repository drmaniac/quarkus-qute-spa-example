package de.pieczewski.qute.helper;

import java.io.IOException;

public class FileHelper {

    private FileHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static String readResource(String path) throws IOException {
        return new String(FileHelper.class.getResourceAsStream(path).readAllBytes());
    }
}
