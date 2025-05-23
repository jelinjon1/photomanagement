package cz.cvut.fel.photomanagement.faces.util;

import cz.cvut.fel.photomanagement.entities.AlbumPhoto;
import java.io.Serializable;
import java.util.List;

/**
 * Custom iterator to cyclically traverse AlbumPhotos inside an Album, used on albums-detail.xhtml.
 *
 * @author jelinjon
 */
public class CyclicIterator implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<AlbumPhoto> objects;
    private int index = 0;

    public CyclicIterator(List<AlbumPhoto> objects) {
        this.objects = objects;
    }

    public boolean isNotEmpty() {
        return !objects.isEmpty();
    }

    public AlbumPhoto getPrevious() {
        if (isNotEmpty()) {
            if (index - 1 >= 0) {
                return objects.get(index - 1);
            } else {
                return objects.get(objects.size() - 1);
            }
        } else {
            return null;
        }
    }

    public AlbumPhoto getCurrent() {
        if (objects.size() > 0) {
            return objects.get(index);
        } else {
            return null;
        }
    }

    public AlbumPhoto getNext() {
        if (isNotEmpty()) {
            if (index + 1 < objects.size()) {
                return objects.get(index + 1);
            } else {
                return objects.get(0);
            }
        } else {
            return null;
        }
    }

    public void goToNext() {
        index++;
        if (index >= objects.size()) {
            index = 0;
        }
    }

    public void goToPrevious() {
        index--;
        if (index < 0) {
            index = objects.size() - 1;
        }
    }
}
