/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.comparator;

import cz.cvut.fel.photomanagement.faces.model.AlbumPhoto;
import cz.cvut.fel.photomanagement.faces.model.Photo;
import java.util.Comparator;

/**
 *
 * @author Jonáš
 */
public class SortByDateTaken implements Comparator<AlbumPhoto> {

    @Override
    public int compare(AlbumPhoto o1, AlbumPhoto o2) {
        Photo a = o1.getPhoto();
        Photo b = o2.getPhoto();
        return a.getTaken().compareTo(b.getTaken());
    }
}
