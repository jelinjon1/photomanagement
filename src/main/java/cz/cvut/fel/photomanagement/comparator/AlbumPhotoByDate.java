/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.comparator;

import cz.cvut.fel.photomanagement.entities.AlbumPhoto;
import java.util.Comparator;

/**
 *
 * @author Jonáš
 */
public class AlbumPhotoByDate implements Comparator<AlbumPhoto> {

    @Override
    public int compare(AlbumPhoto o1, AlbumPhoto o2) {
        return o1.getPhoto().getTaken().compareTo(o2.getPhoto().getTaken());
    }

}
