package cz.cvut.fel.photomanagement.entities;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Photo.
 *
 * @author jelinjon
 */
@Entity
@NamedQueries({
    @NamedQuery(name = Photo.QUERY_ALL, query = "SELECT photo FROM Photo"),
    @NamedQuery(name = Photo.QUERY_BY_LOCAL_DIR, query = "SELECT photo FROM Photo photo WHERE photo.localPath = :localPath")
})
public class Photo implements Serializable {

    public static final String QUERY_ALL = "Photo.all";
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

    public Photo(String fileName, String localPath, LocalDateTime taken) {
        this.fileName = fileName;
        this.tags = tags = new ArrayList<>();
        this.description = null;
        this.localPath = localPath;
        this.taken = taken;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getFormattedTaken() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm - dd.MM.yyyy");
        return taken.format(formatter);
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
