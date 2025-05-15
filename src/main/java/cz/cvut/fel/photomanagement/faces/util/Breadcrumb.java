/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.faces.util;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Represents a piece of breadcrumb navigation present on photos.xhtml, with a value for displaying and a partial path
 * to redirect to.
 *
 * @author jelinjon
 */
public class Breadcrumb {

    private String value;
    private Path path;

    public Breadcrumb(String value, Path path) {
        this.value = value;
        this.path = path;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Breadcrumb bc = (Breadcrumb) o;
        return Objects.equals(value, bc.value) && Objects.equals(path, bc.path);
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
