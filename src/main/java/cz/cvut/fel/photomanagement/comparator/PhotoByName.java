/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.comparator;

import cz.cvut.fel.photomanagement.entities.Photo;
import java.util.Comparator;

/**
 *
 * @author jelinjon
 */
public class PhotoByName implements Comparator<Photo> {

    @Override
    public int compare(Photo o1, Photo o2) {
        return o1.getFileName().compareTo(o2.getFileName());
    }
}
