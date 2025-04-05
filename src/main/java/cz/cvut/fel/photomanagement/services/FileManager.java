/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.services;

import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.imaging.Imaging;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.primefaces.model.file.UploadedFile;

/**
 *
 * @author Jonáš
 */
@Singleton
public class FileManager {

    @Inject
    @ConfigProperty(name = "photos.directory.path")
    private String photosDirectoryPath;

    public void saveUploadedFile(UploadedFile file, String filesPath) {
        byte[] fileBytes = file.getContent();

        // try reading
        ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBytes);
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = Imaging.getBufferedImage(inputStream);
        } catch (IOException e) {
            System.err.println("Exceptionn during reading of file: " + file.getFileName());
            e.printStackTrace();
        }

        if (bufferedImage == null) {
            System.out.println("Failed to decode image with Apache Imaging.");
            return;
        }

        // prepare for file creation, make directories when needed
        String prefix = this.getPhotosDirectoryPath();
        String filePath = prefix + filesPath + "/" + file.getFileName();

        File outputFile = new File(filePath);
        outputFile.getParentFile().mkdirs();

        // try writing the data into the file
        try {
            boolean success = javax.imageio.ImageIO.write(bufferedImage, "png", outputFile);

            if (success) {
                System.out.println("Image saved successfully: " + outputFile.getAbsolutePath());
            } else {
                System.out.println("Image writing failed.");
            }
        } catch (IOException e) {
            System.err.println("Exceptionn during writing of file: " + file.getFileName());
        }
    }

    // slo by otestovat pairwise
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

    public String getPhotosDirectoryPath() {
        return photosDirectoryPath;
    }
}
