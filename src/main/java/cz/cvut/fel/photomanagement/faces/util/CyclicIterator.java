/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.faces.util;

import cz.cvut.fel.photomanagement.faces.model.Photo;
import java.util.List;

/**
 *
 * @author Jonáš
 */
public class CyclicIterator {

    private List<Photo> objects;
    private int index = 0;

    public CyclicIterator(List<Photo> objects) {
        this.objects = objects;
    }

    public boolean isNotEmpty() {
        return objects.size() != 0;
    }

    public Photo getPrevious() {
        if (isNotEmpty()) {
            if (index - 1 >= 0) {
                return objects.get(index - 1);
            } else {
                return objects.getLast();
            }
        } else {
            return null;
        }
    }

    public Photo getCurrent() {
        if (objects.size() > 0) {
            return objects.get(index);
        } else {
            return null;
        }
    }

    public Photo getNext() {
        if (isNotEmpty()) {
            if (index + 1 < objects.size()) {
                return objects.get(index + 1);
            } else {
                return objects.getFirst();
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
