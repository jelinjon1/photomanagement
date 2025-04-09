package cz.cvut.fel.photomanagement.comparator;

import cz.cvut.fel.photomanagement.entities.Photo;
import java.util.Comparator;

/**
 *
 * @author Jonáš
 */
public class PhotoByDate implements Comparator<Photo> {

    @Override
    public int compare(Photo o1, Photo o2) {
        return o1.getTaken().compareTo(o2.getTaken());
    }
}
