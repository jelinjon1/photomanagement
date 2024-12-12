/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cz.cvut.fel.photomanagement.faces;

import cz.cvut.fel.photomanagement.faces.model.Photo;
import cz.cvut.fel.photomanagement.faces.util.CyclicIterator;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jonáš
 */
@Named(value = "albumDetailBean")
@SessionScoped
public class AlbumDetailBean implements Serializable {
    private Map<Long, Boolean> selectedPhotos = new HashMap<>();
    private List<Photo> photosList = new ArrayList<>();
    private CyclicIterator iterator = null;

}
