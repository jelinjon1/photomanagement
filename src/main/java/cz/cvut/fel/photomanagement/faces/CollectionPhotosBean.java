package cz.cvut.fel.photomanagement.faces;

import cz.cvut.fel.photomanagement.comparator.PhotoByDate;
import cz.cvut.fel.photomanagement.comparator.PhotoByName;
import cz.cvut.fel.photomanagement.comparator.PhotoByRating;
import cz.cvut.fel.photomanagement.entities.Album;
import cz.cvut.fel.photomanagement.entities.Photo;
import cz.cvut.fel.photomanagement.faces.model.FilePlaceholder;
import cz.cvut.fel.photomanagement.faces.util.AlbumMenuOption;
import cz.cvut.fel.photomanagement.faces.util.Breadcrumb;
import cz.cvut.fel.photomanagement.faces.util.SortMenuOption;
import cz.cvut.fel.photomanagement.services.AlbumDatabaseService;
import cz.cvut.fel.photomanagement.services.FileManager;
import cz.cvut.fel.photomanagement.services.PhotoDatabaseService;
import cz.cvut.fel.photomanagement.services.ThumbnailWebSocket;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.enterprise.concurrent.ManagedExecutorDefinition;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.DataModel;
import jakarta.faces.model.ListDataModel;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import org.primefaces.model.file.UploadedFile;
import org.primefaces.model.file.UploadedFiles;

/**
 *
 * @author Jonáš
 */
@Named(value = "collectionPhotosBean")
@SessionScoped
@ManagedExecutorDefinition(
        name = "java:app/concurrent/thumbnailGenerationExecutor",
        maxAsync = 5)
public class CollectionPhotosBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private PhotoDatabaseService photoDatabaseService;
    @EJB
    private AlbumDatabaseService albumDatabaseService;
    @Inject
    private FileManager fileManager;
    @Resource(lookup = "java:app/concurrent/thumbnailGenerationExecutor")
    private ManagedExecutorService managedExecutor;

    private Long selectedAlbumId = 1L;
    private List<AlbumMenuOption> albumOptions;
    private List<SortMenuOption> sortMenuOptions;
    private Long selectedComparatorId = 1L;
    private boolean ascending = true;
    private Photo selectedPhoto = null;
    private Long selectedPhotoParameter;
    private DataModel<Photo> photosDataModel;
    private DataModel<FilePlaceholder> filesDataModel;
    private String filesPath = "";
    private ArrayList<String> paths;
    private boolean handlingredirectToNewAlbum = false;
    private boolean locationIsBinDirectory = false;
    private UploadedFiles files;
    private static final int DISPLAYED_PHOTO_HEIGHT = 300;
    private boolean hideThumbnails = true;
    private boolean hideBin = false;

    @PostConstruct
    public void init() {
        paths = new ArrayList<>();
        albumOptions = transformIntoOptions(albumDatabaseService.listAllAlbums());
        sortMenuOptions = Arrays.asList(
                new SortMenuOption("Sort by date", 1L, new PhotoByDate()),
                new SortMenuOption("Sort by name", 2L, new PhotoByName()),
                new SortMenuOption("Sort by rating", 3L, new PhotoByRating())
        );
    }

    public DataModel<Breadcrumb> getBreadcrumbs() {

        Path partialPath = Path.of(filesPath);
        List<Breadcrumb> breadcrumbs = new ArrayList<>();

        for (int i = 0; i < partialPath.getNameCount(); i++) {
            Path subPath = partialPath.subpath(0, i + 1);

            breadcrumbs.add(
                    new Breadcrumb(
                            partialPath.getName(i).toString(),
                            subPath
                    )
            );
        }

        return new ListDataModel(breadcrumbs);
    }

    public void loadFiles() {
        System.out.println("HEJ: " + this.selectedComparatorId);

        Path path = Path.of(filesPath);
        boolean locationIsThumbnailsDirectory = false;
        this.locationIsBinDirectory = false;

        for (int i = 0; i < path.getNameCount(); i++) {
            if ("thumbnails".equals(path.getName(i).toString())) {
                locationIsThumbnailsDirectory = true;
            }
            if ("bin".equals(path.getName(i).toString())) {
                this.locationIsBinDirectory = true;
            }
        }

        // fetch all present files located in directory given by navigation
        List<File> allFiles = fileManager.loadFiles(filesPath);

        // filter directories into separate data model
        filesDataModel = new ListDataModel<>(allFiles.stream()
                .filter(File::isDirectory)
                .filter(file -> !hideThumbnails || !"thumbnails".equals(file.getName()))
                .filter(file -> !hideBin || !"bin".equals(file.getName()))
                .map(file -> new FilePlaceholder(file, fileManager.getPhotosDirectoryPath().length()))
                .collect(Collectors.toList()));

        // fetch existing records for given path
        List<Photo> photosList = new ArrayList<>();
        List<Photo> existingPhotos = photoDatabaseService.findAllExistingPhotos(filesPath);

        // create map of <file name, Photo instance>
        Map<String, Photo> fileNameMap = existingPhotos
                .stream()
                .collect(Collectors.toMap(p -> p.getFileName(), p -> p));

        for (File file : allFiles) {
            if (file.isFile() && isPhoto(file)) {
                Photo existingPhoto = fileNameMap.get(file.getName());
                if (!locationIsThumbnailsDirectory) {
                    managedExecutor.submit(() -> generateThumbnail(file, existingPhoto.getId()));
                }
                if (existingPhoto != null) {
                    photosList.add(existingPhoto);
//                    System.out.println("Loaded existing photo: " + existingPhoto);
                } else {
                    Photo photo = new Photo(file, filesPath);
                    photoDatabaseService.savePhoto(photo);
                    photosList.add(photo);
//                    System.out.println("Saved new photo: " + photo);
                }
            }

        }

//        System.out.println("BEFORE SORT");
//        for (Photo photo : photosList) {
//            System.out.println(photo);
//        }

        // sort list of photos
        Comparator selectedComparator = this.sortMenuOptions.get(selectedComparatorId.intValue() - 1).getComparator();
        if (!ascending) {
            selectedComparator = selectedComparator.reversed();
        }
        photosList.sort(selectedComparator);

//        System.out.println("AFTER SORT");
//        for (Photo photo : photosList) {
//            System.out.println(photo);
//        }

        // return datamodel from list of photos
        photosDataModel = new ListDataModel<>(photosList);
    }

    public void uploadMultiple() {
        if (files != null) {
            for (UploadedFile file : files.getFiles()) {
                fileManager.saveUploadedFile(file, filesPath);
            }
        } else {
            System.err.println("FILES IS NULL");
        }
    }

    public List<AlbumMenuOption> transformIntoOptions(List<Album> albums) {
        List<AlbumMenuOption> res = albums.stream().map(album -> new AlbumMenuOption(album)).collect(Collectors.toList());
        res.add(new AlbumMenuOption(null));
        return res;
    }

    public void updatePhoto(Photo photo) {
        photoDatabaseService.update(photo);
        System.out.println(photo);
    }

    public void generateThumbnail(File inputFile, Long photoId) {
        try {
            Path thumbnailFilePath = Path.of(inputFile.getParent(), "thumbnails", inputFile.getName());

//            System.out.println(thumbnailFilePath);
            File thumbnailFile = thumbnailFilePath.toFile();

            // check if thumbnail already generated, if so, return
            if (thumbnailFile.exists()) {
//                System.out.println("Thumbnail already exists for " + inputFile);
                return;
            }
            thumbnailFile.getParentFile().mkdirs();

            // read original image, if unable to, return
            BufferedImage originalImage = ImageIO.read(inputFile);
            if (originalImage == null) {
                System.err.println("Skipping: Unable to read " + inputFile.getName());
                return;
            }

            // scale width and height to displayed dimensions
            int h = DISPLAYED_PHOTO_HEIGHT;
            double ratio = (double) h / originalImage.getHeight();
            int w = Math.max(1, (int) (originalImage.getWidth() * ratio)); // Ensure w is at least 1

            // resize the image, bilinear interpolation
            Image scaledImage = originalImage.getScaledInstance(w, h, Image.SCALE_SMOOTH);

            // convert to BufferedImage
            BufferedImage thumbnailImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = thumbnailImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.drawImage(scaledImage, 0, 0, null);
            g2d.dispose();

            // write the thumbnail to a file
            ImageIO.write(thumbnailImage, "png", thumbnailFilePath.toFile());

            ThumbnailWebSocket.sendThumbnailUpdate(photoId, thumbnailFilePath.toString());

//            System.out.println("Thumbnail created: " + thumbnailFilePath);
        } catch (Exception | Error e) {
            System.err.println("Failed to create thumbnail for: " + inputFile.getName());
            e.printStackTrace();
        }
    }

    private boolean isPhoto(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".gif");
    }

    public void restorePhoto(Photo photo) {
        Path photoLocalPath = Path.of(photo.getLocalPath());
        if ("bin".equals(photoLocalPath.getName(photoLocalPath.getNameCount() - 1).toString())) {
            fileManager.restoreFile(photo.getFileName(), photo.getLocalPath());
        }
    }

    /*
    check if photo is not already inside /bin
        check if ./bin exists
            if so: move photo to bin
            else: create ./bin and move photo
     */
    public void deletePhoto(Photo photo) {
        Path photoLocalPath = Path.of(photo.getLocalPath());
        if (!"bin".equals(photoLocalPath.getName(photoLocalPath.getNameCount() - 1).toString())) {
            fileManager.deletePhoto(photo.getFileName(), photo.getLocalPath());
        }
    }

    public String addToAlbum(Album album) {
        handlingredirectToNewAlbum = false;

        System.out.println("ALBUM: " + album);
        if (album == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Album not found", null));
            return null;
        }
        System.out.println("PHOTOS DATA MODEL: " + photosDataModel);
        for (Photo photo : photosDataModel) {
            album.addPhoto(photo);
        }

        albumDatabaseService.update(album);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Photos added to album successfully", null));
        return null;
    }

    public String addToAlbum() {
        //check selected option
        //if new album, redirect to create scree, keep reference to selected photos, add after creation is finished
        //else, add selected photos to selected album

        if (selectedAlbumId == 0) {
            handlingredirectToNewAlbum = true;
            return "albums-new.xhtml?faces-redirect=true";
        }

        Album album = albumDatabaseService.findAlbumById(selectedAlbumId); // Retrieve album by selected ID
        if (album == null) {
            System.err.println("Album with the given id was not found.");
            return null;
        }

        for (Photo photo : photosDataModel) {
            album.addPhoto(photo);
        }

        albumDatabaseService.update(album);
//        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Photos added to album successfully", null));
        System.out.println("photos added to album successfully");
        return null;
    }

    public void addToPaths(String fileName) {
        Objects.requireNonNull(fileName);
        if (!fileName.equals("")) {
            paths.add(fileName);
        }
    }

    public void loadSelectedPhoto() {
        selectedPhoto = photoDatabaseService.findPhotoById(selectedPhotoParameter);
    }

    public DataModel<Photo> getPhotosDataModel() {
        return photosDataModel;
    }

    public void setPhotosDataModel(DataModel<Photo> photosDataModel) {
        this.photosDataModel = photosDataModel;
    }

    public void selectPhoto() {
        selectedPhoto = photosDataModel.getRowData();
    }

    public String goToDetailRedirect() {
        selectedPhoto = photosDataModel.getRowData();
        return "photos-detail.xhtml?faces-redirect=true";
    }

    public void clearSelectedPhoto() {
        selectedPhoto = null;
    }

    public Photo getSelectedPhoto() {
        return selectedPhoto;
    }

    public void setSelectedPhoto(Photo selectedPhoto) {
        this.selectedPhoto = selectedPhoto;
    }

    public Long getSelectedPhotoParameter() {
        return selectedPhotoParameter;
    }

    public void setSelectedPhotoParameter(Long selectedPhotoParameter) {
        this.selectedPhotoParameter = selectedPhotoParameter;
    }

    public Long getSelectedAlbumId() {
        return selectedAlbumId;
    }

    public void setSelectedAlbumId(Long selectedAlbumId) {
        this.selectedAlbumId = selectedAlbumId;
    }

    public List<AlbumMenuOption> getAlbumOptions() {
        albumOptions = transformIntoOptions(albumDatabaseService.listAllAlbums());
        return albumOptions;
    }

    public void setAlbumOptions(List<AlbumMenuOption> albumOptions) {
        this.albumOptions = albumOptions;
    }

    public DataModel<FilePlaceholder> getFilesDataModel() {
        return filesDataModel;
    }

    public String getFilesPath() {
        return filesPath;
    }

    public void setFilesPath(String filesPath) {
        this.filesPath = filesPath;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(ArrayList<String> paths) {
        this.paths = paths;
    }

    public boolean isHandlingredirectToNewAlbum() {
        return handlingredirectToNewAlbum;
    }

    public void setHandlingredirectToNewAlbum(boolean handlingredirectToNewAlbum) {
        this.handlingredirectToNewAlbum = handlingredirectToNewAlbum;
    }

    public UploadedFiles getFiles() {
        return files;
    }

    public void setFiles(UploadedFiles files) {
        this.files = files;
    }

    public boolean isLocationIsBinDirectory() {
        return locationIsBinDirectory;
    }

    public void setPhotoDatabaseService(PhotoDatabaseService photoDatabaseService) {
        this.photoDatabaseService = photoDatabaseService;
    }

    public void setAlbumDatabaseService(AlbumDatabaseService albumDatabaseService) {
        this.albumDatabaseService = albumDatabaseService;
    }

    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public boolean isHideThumbnails() {
        return hideThumbnails;
    }

    public void setHideThumbnails(boolean hideThumbnails) {
        this.hideThumbnails = hideThumbnails;
    }

    public boolean isHideBin() {
        return hideBin;
    }

    public void setHideBin(boolean hideBin) {
        this.hideBin = hideBin;
    }

    public List<SortMenuOption> getSortMenuOptions() {
        return sortMenuOptions;
    }

    public void setSortMenuOptions(List<SortMenuOption> sortMenuOptions) {
        this.sortMenuOptions = sortMenuOptions;
    }

    public Long getSelectedComparatorId() {
        return selectedComparatorId;
    }

    public void setSelectedComparatorId(Long selectedComparatorId) {
        this.selectedComparatorId = selectedComparatorId;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

}
