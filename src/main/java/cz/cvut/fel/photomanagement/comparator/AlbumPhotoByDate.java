package cz.cvut.fel.photomanagement.comparator;

import cz.cvut.fel.photomanagement.entities.AlbumPhoto;
import java.util.Comparator;

/**
 * This class implements a method that compares two {@link cz.cvut.fel.photomanagement.entities.Photo} by date when they
 * were taken.
 *
 * @author jelinjon
 */
public class AlbumPhotoByDate implements Comparator<AlbumPhoto> {

    /**
     * Compares two AlbumPhotos based on date taken.
     *
     * @param o1 first comapred AlbumPhoto
     * @param o2 second comapred AlbumPhoto
     * @return ==0 if equal, <0 if o1 < o2, >0 if o1>02
     */
    @Override
    public int compare(AlbumPhoto o1, AlbumPhoto o2) {
        return o1.getPhoto().getTaken().compareTo(o2.getPhoto().getTaken());
    }

}
