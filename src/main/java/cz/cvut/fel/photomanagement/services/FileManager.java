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

    public void cleanUpEmptyFile(File file) {
        if (file.listFiles().length == 0) {
            System.out.println("FILE " + file.getName() + " IS EMPTY, MARKED FOR DELETION");
            file.delete();
        }
    }

    public void restoreFile(String fileName, String localPath) {
        // find thumbnail in local file (/bin) and try to move it alongside the photo to the parent/thumbnails

        if (moveAFileUp(fileName, localPath)) {
            Path thumbnailPath = Paths.get(photosDirectoryPath, localPath, "thumbnails", fileName);
            File thumbnailImageFile = thumbnailPath.toFile();
            if (thumbnailImageFile.exists()) {
                // move thumbnail thumbnail file within bin to parent of bin thumbnail file
                // check local thumbnail for empty
                // check bin for empty

                Path thumbnailParentLocalPath = thumbnailPath.getParent().getParent().getParent();
                Path newThumbnailImagePath = Path.of(thumbnailParentLocalPath.toString(), "thumbnails", fileName);
                File newThumbnailImageFile = newThumbnailImagePath.toFile();
                newThumbnailImageFile.getParentFile().mkdirs();

                boolean success = thumbnailImageFile.renameTo(newThumbnailImageFile);
                if (!success) {
                    thumbnailImageFile.delete();
                }

                File localThumbnailsFile = thumbnailPath.getParent().toFile();
                cleanUpEmptyFile(localThumbnailsFile);
            }
        }
    }

    public boolean moveAFileUp(String fileName, String localPath) {

        Path originalPath = Paths.get(photosDirectoryPath, localPath);
        Path toMove = originalPath.resolve(fileName);
        Path parentDir = originalPath.getParent();

        if (parentDir == null) {
            throw new IllegalStateException("Cannot move up from root directory");
        }

        Path targetPath = parentDir.resolve(fileName);

        File sourceFile = toMove.toFile();
        File targetFile = targetPath.toFile();
        targetFile.getParentFile().mkdirs();

        boolean success = sourceFile.renameTo(targetFile);
        if (!success) {
            System.err.println("Failed to move file from " + sourceFile + " to " + targetFile);
        }
        return success;
    }

    public void deletePhoto(String fileName, String localPath) {
        if (moveToBin(fileName, localPath)) {
            System.out.println("GOING TO MOVE THUMBNAIL");
            Path thumbnailPath = Paths.get(photosDirectoryPath, localPath, "thumbnails", fileName);
            File thumbnailImageFile = thumbnailPath.toFile();
            if (thumbnailImageFile.exists()) {
                // move thumbnail thumbnail file within bin to parent of bin thumbnail file
                // check local thumbnail for empty
                // check bin for empty
                System.out.println("THUMBNAIL FOUND");

                Path targetThumbnailPath = Paths.get(photosDirectoryPath, localPath, "bin", "thumbnails", fileName);
                File newThumbnailImageFile = targetThumbnailPath.toFile();
                newThumbnailImageFile.getParentFile().mkdirs();

                boolean success = thumbnailImageFile.renameTo(newThumbnailImageFile);
                if (!success) {
                    thumbnailImageFile.delete();
                }

                File localThumbnailsFile = thumbnailPath.getParent().toFile();
                cleanUpEmptyFile(localThumbnailsFile);
            }

        }
    }

    public boolean moveToBin(String fileName, String localPath) {
        createDirectory(localPath, "bin");

        File sourceFile = Paths.get(photosDirectoryPath, localPath, fileName).toFile();
        String targetPath = Paths.get(photosDirectoryPath, localPath, "bin", fileName).toString();
        boolean success = sourceFile.renameTo(new File(targetPath));

        if (!success) {
            System.err.println("Failed to move file from " + sourceFile + " to " + targetPath);
        }
        return success;
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
