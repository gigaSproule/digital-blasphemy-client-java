package com.benjaminsproule.digitalblasphemy.client.util;

import edu.umd.cs.findbugs.annotations.NonNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

public class FileUtils {
    @NonNull
    public static String readFile(String fileName) throws IOException, URISyntaxException {
        return requireNonNull(Files.readString(
                Path.of(
                        requireNonNull(
                                FileUtils.class.getClassLoader().getResource(fileName)
                        ).toURI()
                )));
    }
}
