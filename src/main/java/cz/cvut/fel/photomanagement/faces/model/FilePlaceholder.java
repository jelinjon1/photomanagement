package cz.cvut.fel.photomanagement.faces.model;

import java.io.File;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Jonáš
 */
public class FilePlaceholder implements Serializable {

    private static final long serialVersionUID = 1L;
    private String name;
    private int directSubdirectoryCount;
    private String lastModifiedFormatted;
    private String path;
    private int photosDirectoryPathLength;


    public FilePlaceholder(File file, int photosDirectoryPathLength) {
        this.photosDirectoryPathLength = photosDirectoryPathLength;
        this.name = file.getName();
        this.directSubdirectoryCount = file.list().length;
        LocalDateTime dateTime = Instant.ofEpochMilli(file.lastModified())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm - dd.MM.yyyy");
        this.lastModifiedFormatted = dateTime.format(formatter);
        this.path = formatPath(file.getAbsolutePath());
    }

    private String formatPath(String fullPath) {
        return fullPath.substring(photosDirectoryPathLength);
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDirectSubdirectoryCount() {
        return directSubdirectoryCount;
    }

    public void setDirectSubdirectoryCount(int directSubdirectoryCount) {
        this.directSubdirectoryCount = directSubdirectoryCount;
    }

    public String getLastModifiedFormatted() {
        return lastModifiedFormatted;
    }

    public void setLastModifiedFormatted(String lastModifiedFormatted) {
        this.lastModifiedFormatted = lastModifiedFormatted;
    }

}
