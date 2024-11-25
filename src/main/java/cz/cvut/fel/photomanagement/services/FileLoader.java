/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.services;

import jakarta.ejb.Singleton;
import jakarta.faces.model.DataModel;
import jakarta.faces.model.ListDataModel;
import jakarta.inject.Inject;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 *
 * @author Jonáš
 */
@Singleton
public class FileLoader {

    @Inject
    @ConfigProperty(name = "photos.directory.path")
    private String photosDirectoryPath;

    public DataModel<File> loadFiles() {
        return loadFiles("");
    }

    public DataModel<File> loadFiles(String relativePath) {
        try {
            String fullPath;
            if (!Objects.equals(relativePath, null)) {
                fullPath = photosDirectoryPath.concat(relativePath);
            } else {
                fullPath = photosDirectoryPath;
            }
            List<File> filesList = Files.list(Paths.get(fullPath))
                    .peek(s -> System.out.println(s)) // Debug: Print the file paths
                    .map(Path::toFile) // Convert Path to File
                    .collect(Collectors.toList());

            DataModel<File> dataModel = new ListDataModel<>(filesList);

            System.out.println("DIRECTORIES:");
            System.out.println(filesList.isEmpty());
            return dataModel;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("DIRECTORIES: error pri nacitaci");
            // Return an empty DataModel in case of an error
            return new ListDataModel<>(List.of());
        }
    }

    public String getPhotosDirectoryPath() {
        return photosDirectoryPath;
    }
}
