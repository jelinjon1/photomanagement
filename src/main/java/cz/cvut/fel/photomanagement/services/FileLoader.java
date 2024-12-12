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

    public boolean createDirectory(String localPath, String name) {
        File directory = Paths.get(photosDirectoryPath, localPath, name).toFile();
        return directory.mkdirs();
    }

    public void moveToBin(String fileName, String localPath) {
        File toMove = Paths.get(photosDirectoryPath, localPath, fileName).toFile();
        String targetPath = Paths.get(photosDirectoryPath, localPath, "bin", fileName).toString();
        toMove.renameTo(new File(targetPath));
    }

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
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("DIRECTORIES: error pri nacitani");
            return new ArrayList<>();
        }
    }
}
