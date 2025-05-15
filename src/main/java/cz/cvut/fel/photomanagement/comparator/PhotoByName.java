/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.comparator;

import cz.cvut.fel.photomanagement.entities.Photo;
import java.util.Comparator;

/**
 * This class implements a method that compares two {@link cz.cvut.fel.photomanagement.entities.Photo} by name
 * alphabetically when they were taken.
 *
 * @author jelinjon
 */
public class PhotoByName implements Comparator<Photo> {

    /**
     * Compares two Photos based on name.
     *
     * @param o1 first comapred Photo
     * @param o2 second comapred Photo
     * @return ==0 if equal, <0 if o1 < o2, >0 if o1>02
     */
    @Override
    public int compare(Photo o1, Photo o2) {
        return o1.getFileName().compareTo(o2.getFileName());
    }
}
