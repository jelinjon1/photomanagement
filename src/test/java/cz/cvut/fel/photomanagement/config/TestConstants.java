/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.config;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Attempts to fetch/define constants required for testing such as a URL.
 *
 * @author jelinjon
 */
public final class TestConstants {

    public static final String URL = "http://host.docker.internal:8080/photo-management-app/";
    public static final Path TEST_FILES_DIRECTORY_PATH = Paths.get("src", "test", "resources", "images");
    public static final List<File> TEST_FILES;
    public static final String TEST_USERNAME;
    public static final String TEST_PASSWORD;

    static {
        TEST_FILES = loadFiles();
        TEST_USERNAME = loadArgument("username");
        TEST_PASSWORD = loadArgument("password");
    }

    private TestConstants() {
    }

    private static String loadArgument(String argumentKey) {
        String argumentValue = System.getProperty(argumentKey);
        if (argumentValue == null) {
            return "admin";
        } else {
            return argumentValue;
        }
    }

    private static List<File> loadFiles() {
        try {
            List<File> filesList = Files.list(TEST_FILES_DIRECTORY_PATH)
                    .map(Path::toFile)
                    .filter(file -> isPhoto(file))
                    .collect(Collectors.toList());
            return filesList;
        } catch (Exception e) {
            Logger.getLogger(TestConstants.class.getName()).log(Level.SEVERE, "Exception during loadFiles", e);
            return new ArrayList<>();
        }
    }

    private static boolean isPhoto(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".gif");
    }
}
