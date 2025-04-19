/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.comparator;

import cz.cvut.fel.photomanagement.entities.Photo;
import java.util.Comparator;

/**
 *
 * @author Jonáš
 */
public class PhotoByRating implements Comparator<Photo> {

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
//        return o1.getRating().compareTo(o2.getRating());
    }
}
