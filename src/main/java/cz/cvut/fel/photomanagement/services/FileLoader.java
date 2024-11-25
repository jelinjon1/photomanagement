/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.services;

import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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

    public List<File> loadFiles() {
        return loadFiles("");
    }

    public List<File> loadFiles(String relativePath) {
        try {
            String fullPath;
            if (!Objects.equals(relativePath, null)) {
                fullPath = photosDirectoryPath.concat(relativePath);
            } else {
                fullPath = photosDirectoryPath;
            }
            List<File> filesList = Files.list(Paths.get(fullPath))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
            return filesList;
//            DataModel<File> dataModel = new ListDataModel<>(filesList);
//            return dataModel;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("DIRECTORIES: error pri nacitani");
            return new ArrayList<>();
//            return new ListDataModel<>();
        }
    }

    public String getPhotosDirectoryPath() {
        return photosDirectoryPath;
    }
}
