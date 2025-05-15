/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.faces.util;

import java.io.Serializable;
import java.util.Comparator;

/**
 *
 * @author jelinjon
 */
public class SortMenuOption implements Serializable {

    private String name;
    private Long id;
    private Comparator<?> comparator;

    public SortMenuOption(String name, Long id, Comparator<?> comparator) {
        this.name = name;
        this.id = id;
        this.comparator = comparator;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Comparator<?> getComparator() {
        return comparator;
    }
}
