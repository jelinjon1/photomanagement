/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.faces.util;

import java.nio.file.Path;

/**
 *
 * @author Jonáš
 */
public class Breadcrumb {

    private String value;
    private Path path;

    public Breadcrumb(String value, Path path) {
        this.value = value;
        this.path = path;

    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

}
