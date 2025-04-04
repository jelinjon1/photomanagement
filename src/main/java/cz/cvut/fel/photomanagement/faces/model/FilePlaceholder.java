/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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


    public FilePlaceholder(File file) {
        this.name = file.getName();
        this.directSubdirectoryCount = file.list().length;
        LocalDateTime dateTime = Instant.ofEpochMilli(file.lastModified())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm - dd.MM.yyyy");
        this.lastModifiedFormatted = dateTime.format(formatter);
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
