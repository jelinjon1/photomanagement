package cz.cvut.fel.photomanagement.comparator;

import cz.cvut.fel.photomanagement.entities.AlbumPhoto;
import java.util.Comparator;

/**
 *
 * @author jelinjon
 */
public class AlbumPhotoByDate implements Comparator<AlbumPhoto> {

    @Override
    public int compare(AlbumPhoto o1, AlbumPhoto o2) {
        return o1.getPhoto().getTaken().compareTo(o2.getPhoto().getTaken());
    }

}
