package cz.cvut.fel.photomanagement.comparator;

import cz.cvut.fel.photomanagement.entities.Photo;
import java.util.Comparator;

/**
 * This class implements a method that compares two {@link cz.cvut.fel.photomanagement.entities.AlbumPhoto} by date when
 * they were taken.
 *
 * @author jelinjon
 */
public class PhotoByDate implements Comparator<Photo> {

    /**
     * Compares two Photos based on date taken.
     *
     * @param o1 first comapred Photo
     * @param o2 second comapred Photo
     * @return ==0 if equal, <0 if o1 < o2, >0 if o1>02
     */
    @Override
    public int compare(Photo o1, Photo o2) {
        return o1.getTaken().compareTo(o2.getTaken());
    }
}
