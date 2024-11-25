/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.faces.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jonáš
 */
@Entity
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName; //example.jpg
    private String localPath; //dir/dir
    @ElementCollection
    private List<String> tags = new ArrayList<>();
    private String description = null;
    private LocalDate taken = LocalDate.now();
    private boolean selected = true;

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

    public LocalDate getTaken() {
        return taken;
    }

    public void setTaken(LocalDate taken) {
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

}
