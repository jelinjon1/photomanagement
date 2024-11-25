/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.services;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import jakarta.ejb.Singleton;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Stream;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import cz.cvut.fel.photomanagement.faces.model.Photo;
/**
 *
 * @author Jonáš
 */
@Singleton
public class PhotoLoader {

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    @ConfigProperty(name = "photo.directory.path")
    private String photoDirectoryPath;


    private String getImageFolderPath() {
        return FacesContext.getCurrentInstance().getExternalContext().getRealPath(photoDirectoryPath);
    }

    @Transactional
    public void loadAndPersistPhotos() {
        try (Stream<Path> paths = Files.list(Paths.get(getImageFolderPath()))) {
            paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().matches(".*\\.(jpg|jpeg|png|tiff)$"))
                    .forEach(this::processAndSavePhoto);
        } catch (IOException e) {
            System.err.println("Error reading photos from folder: " + e.getMessage());
        }
    }

    private void processAndSavePhoto(Path path) {
        File file = path.toFile();
        Photo photo = new Photo();
        photo.setFileName(file.getName());

        Date dateTaken = extractCaptureDate(file);
        if (dateTaken != null) {
            photo.setTaken(dateTaken.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        entityManager.persist(photo);
    }

    private Date extractCaptureDate(File file) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file);
            ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            if (directory != null) {
                return directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
            }
        } catch (Exception e) {
            System.err.println("Error reading metadata from photo: " + e.getMessage());
        }
        return null;
    }
}
