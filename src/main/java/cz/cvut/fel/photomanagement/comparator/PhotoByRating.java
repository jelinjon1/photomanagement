/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.comparator;

import cz.cvut.fel.photomanagement.entities.Photo;
import java.util.Comparator;

/**
 * This class implements a method that compares two {@link cz.cvut.fel.photomanagement.entities.Photo} by rating
 * ascending, prioritising those with null rating.
 *
 * @author jelinjon
 */
public class PhotoByRating implements Comparator<Photo> {

    /**
     * Compares two Photos based on rating.
     *
     * @param o1 first comapred Photo
     * @param o2 second comapred Photo
     * @return ==0 if equal, <0 if o1 < o2, >0 if o1>02
     */
    @Override
    public int compare(Photo o1, Photo o2) {
        if (o1.getRating() == null) {
            if (o2.getRating() == null) {
                return 0;
            } else {
                return 1;
            }
        }
        if (o2.getRating() == null) {
            return -1;
        }
        return o1.getRating() - o2.getRating();
    }
}
