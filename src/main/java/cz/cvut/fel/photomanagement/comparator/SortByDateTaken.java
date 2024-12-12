/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.comparator;

import cz.cvut.fel.photomanagement.faces.model.Photo;
import java.util.Comparator;

/**
 *
 * @author Jonáš
 */
public class SortByDateTaken implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
        Photo a = (Photo) o1;
        Photo b = (Photo) o2;

        if (a.getTaken().isBefore(b.getTaken())) {
            return -1;
        }
        if (a.getTaken().isAfter(b.getTaken())) {
            return 1;
        }
        return 0;
    }
}
