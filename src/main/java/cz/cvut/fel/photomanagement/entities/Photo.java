package cz.cvut.fel.photomanagement.entities;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jonáš
 */
@Entity
@NamedQueries({
    @NamedQuery(name = Photo.QUERY_BY_LOCAL_DIR, query = "SELECT photo FROM Photo photo WHERE photo.localPath = :localPath")
})
public class Photo implements Serializable {

    public static final String QUERY_BY_LOCAL_DIR = "Photo.byLocalDir";
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName; //example.jpg
    private String localPath; //dir/dir
    @ElementCollection
    private List<String> tags = new ArrayList<>();
    private String description = null;
    private LocalDateTime taken = LocalDateTime.now();
    private boolean selected = true;
    private Integer rating = null;

    public Photo() {
    }

    public Photo(String fileName, List<String> tags, String description, String localPath) {
        this.fileName = fileName;
        this.tags = tags;
        this.description = description;
        this.localPath = localPath;
    }

    public Photo(File file, String localPath) {
        this(file.getName(), new ArrayList<>(), null, localPath);
        this.taken = extractDate(file);
    }

    public Photo(File file, String localPath, String hash) {
        this(file.getName(), new ArrayList<>(), null, localPath);
    }

    // todo dat do service beany
    public LocalDateTime extractDate(File file) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file);
            ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

            if (directory != null) {
                Date date = directory.getDateOriginal();
                if (date != null) {
                    return LocalDateTime.ofInstant(date.toInstant(), java.time.ZoneId.systemDefault());
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading photo metadata: " + e.getMessage());
        }

        Instant creationTime;
        try {
            creationTime = Files.getFileAttributeView(
                    Paths.get(file.getAbsoluteFile().toURI()),
                    BasicFileAttributeView.class)
                    .readAttributes()
                    .creationTime()
                    .toInstant();

        } catch (IOException ex) {
            Logger.getLogger(Photo.class.getName()).log(Level.SEVERE, null, ex);
            creationTime = Instant.ofEpochMilli(file.lastModified());
        }
        return LocalDateTime.ofInstant(creationTime, ZoneId.systemDefault());
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTaken() {
        return taken;
    }

    public void setTaken(LocalDateTime taken) {
        this.taken = taken;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Long getId() {
        return id;
    }

    public String getLocalPath() {
        return localPath;
    }

    public String getRelativePathFromRoot() {
        return localPath + "/" + fileName;
    }

    public String getPathToThumbail() {
        return localPath + "/thumbnails/" + fileName;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Photo{" + "id=" + id + ", fileName=" + fileName + ", localPath=" + localPath + ", tags=" + tags + ", description=" + description + ", taken=" + taken + ", selected=" + selected + ", rating=" + rating + '}';
    }


}
