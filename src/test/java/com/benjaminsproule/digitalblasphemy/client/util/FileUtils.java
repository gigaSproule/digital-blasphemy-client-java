package com.benjaminsproule.digitalblasphemy.client.util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

public class FileUtils {
    @NotNull
    public static String readFile(String fileName) throws IOException, URISyntaxException {
        return Files.readString(
                Path.of(
                        requireNonNull(
                                FileUtils.class.getClassLoader().getResource(fileName)
                        ).toURI()
                ));
    }
}
