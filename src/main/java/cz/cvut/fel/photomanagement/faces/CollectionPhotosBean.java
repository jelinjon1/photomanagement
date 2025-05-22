package cz.cvut.fel.photomanagement.faces;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.jpeg.JpegDirectory;
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
import jakarta.faces.model.DataModel;
import jakarta.faces.model.ListDataModel;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import org.primefaces.model.file.UploadedFile;
import org.primefaces.model.file.UploadedFiles;

/**
 * A bean that feeds data regarding Photos to Faces, handles user input from photos.xhtml.
 *
 * @author jelinjon
 */
@Named(value = "collectionPhotosBean")
@SessionScoped
@ManagedExecutorDefinition(
        name = "java:app/concurrent/thumbnailGenerationExecutor",
        maxAsync = 5)
public class CollectionPhotosBean implements Serializable {

    private static final double DISPLAYED_PHOTO_HEIGHT = 300.0;
    private static final long serialVersionUID = 1L;

    @EJB
    private PhotoDatabaseService photoDatabaseService;
    @EJB
    private AlbumDatabaseService albumDatabaseService;
    @Inject
    private FileManager fileManager;
    @Inject
    private TableAlbumsBean tableAlbumsBean;
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
    private boolean handlingredirectToNewAlbum = false;
    private boolean locationIsBinDirectory = false;
    private UploadedFiles files;
    private boolean hideThumbnails = true;
    private boolean hideBin = false;
    private String newDirectoryName = null;
    private boolean displayDialog = false;
    private String dialogError = null;

    private static final Logger log = Logger.getLogger(CollectionPhotosBean.class.getName());

    @PostConstruct
    public void init() {
        albumOptions = transformIntoOptions(albumDatabaseService.listAllAlbums());
        sortMenuOptions = Arrays.asList(
                new SortMenuOption("Date", 1L, new PhotoByDate()),
                new SortMenuOption("Name", 2L, new PhotoByName()),
                new SortMenuOption("Rating", 3L, new PhotoByRating())
        );
    }

    public void resetDialog() {
        newDirectoryName = null;
        dialogError = null;
        displayDialog = false;
    }
    public void createDirectory() {
        if (newDirectoryName != null) {
            if (fileManager.createDirectory(filesPath, newDirectoryName)) {
                resetDialog();
            } else {
                dialogError = "Error while creating directory, try again.";
            }
        }
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
                .map(file -> new FilePlaceholder(file,
                fileManager.getPhotosDirectoryPath().length()))
                .collect(Collectors.toList()));

        // fetch existing records for given path
        List<Photo> photosList = new ArrayList<>();
        List<Photo> existingPhotos = photoDatabaseService.findAllPhotosInADirectory(filesPath);

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
                } else {
                    Photo photo = new Photo(file.getName(), filesPath, extractDate(file));
                    photoDatabaseService.savePhoto(photo);
                    photosList.add(photo);
                }
            }

        }

        // sort list of photos
        Comparator selectedComparator = this.sortMenuOptions.get(selectedComparatorId.intValue() - 1).getComparator();
        if (!ascending) {
            selectedComparator = selectedComparator.reversed();
        }
        photosList.sort(selectedComparator);

        // return datamodel from list of photos
        photosDataModel = new ListDataModel<>(photosList);
    }

    public LocalDateTime extractDate(File file) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file);
            ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

            if (directory != null) {
                Date date = directory.getDateOriginal();
                if (date != null) {
                    return LocalDateTime.ofInstant(date.toInstant(), java.time.ZoneId.systemDefault());
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error reading photo metadata: " + e.getMessage());
        }

        Instant creationTime;
        try {
            creationTime = Files.getFileAttributeView(
                    Paths.get(file.getAbsoluteFile().toURI()),
                    BasicFileAttributeView.class)
                    .readAttributes()
                    .creationTime()
                    .toInstant();

        } catch (IOException ex) {
            log.log(Level.SEVERE, "Exception during readAttributes", ex);
            creationTime = Instant.ofEpochMilli(file.lastModified());
        }
        return LocalDateTime.ofInstant(creationTime, ZoneId.systemDefault());
    }

    public void uploadMultiple() {
        if (files != null) {
            for (UploadedFile file : files.getFiles()) {
                fileManager.saveUploadedFile(file, filesPath);
            }
        } else {
            log.log(Level.SEVERE, "Files is numm");
        }
    }

    public List<AlbumMenuOption> transformIntoOptions(List<Album> albums) {
        List<AlbumMenuOption> res
                = albums.stream().map(album -> new AlbumMenuOption(album)).collect(Collectors.toList());
        res.add(new AlbumMenuOption(null));
        return res;
    }

    public void updatePhoto(Photo photo) {
        photoDatabaseService.merge(photo);
    }

    public void generateThumbnail(File inputFile, Long photoId) {
        try {
            Path thumbnailFilePath = Path.of(inputFile.getParent(), "thumbnails", inputFile.getName());
            File thumbnailFile = thumbnailFilePath.toFile();

            // check if thumbnail already generated, if so, return
            if (thumbnailFile.exists()) {
                return;
            }
            // generate parent dir
            thumbnailFile.getParentFile().mkdirs();

            // read and skip if cannot
            BufferedImage originalImage = ImageIO.read(inputFile);
            if (originalImage == null) {
                log.log(Level.SEVERE, "Skipping: Unable to read " + inputFile.getName());
                return;
            }

            rotateAndResize(inputFile, thumbnailFilePath);
            ThumbnailWebSocket.sendThumbnailUpdate(photoId, thumbnailFilePath.toString());

        } catch (Exception | Error e) {
            log.log(Level.SEVERE, "Album with the given id was not found.", e);
        }
    }

    public static void rotateAndResize(File imageFile, Path thPath)
            throws IOException, ImageProcessingException, MetadataException {
        BufferedImage originalImage = ImageIO.read(imageFile);

        // extract orientation and size from metadata
        Metadata metadata = ImageMetadataReader.readMetadata(imageFile);
        ExifIFD0Directory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        JpegDirectory jpegDirectory = (JpegDirectory) metadata.getFirstDirectoryOfType(JpegDirectory.class);

        int orientation = 1;
        try {
            orientation = exifIFD0Directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        int width = jpegDirectory.getImageWidth();
        int height = jpegDirectory.getImageHeight();

        double scale;
        int thumbnailWidth;
        // alter transformation based on extracted data
        if (orientation >= 5) {
            scale = DISPLAYED_PHOTO_HEIGHT / width;
            thumbnailWidth = (int) (height * scale);
        } else {
            scale = DISPLAYED_PHOTO_HEIGHT / height;
            thumbnailWidth = (int) (width * scale);
        }

        BufferedImage destinationImage = null;
        AffineTransformOp affineTransformOp = configureAffineTransformOp(scale, orientation, width, height);

        destinationImage = affineTransformOp.filter(originalImage, destinationImage);
        destinationImage = destinationImage.getSubimage(0, 0, thumbnailWidth, (int) DISPLAYED_PHOTO_HEIGHT);

        ImageIO.write(destinationImage, "png", thPath.toFile());
    }

    private static AffineTransformOp configureAffineTransformOp(double scale, int orientation, int width, int height) {
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.scale(scale, scale);
        switch (orientation) {
            case 1:
                break;
            case 2:
                affineTransform.scale(-1.0, 1.0);
                affineTransform.translate(-width, 0);
                break;
            case 3:
                affineTransform.translate(width, height);
                affineTransform.rotate(Math.PI);
                break;
            case 4:
                affineTransform.scale(1.0, -1.0);
                affineTransform.translate(0, -height);
                break;
            case 5:
                affineTransform.rotate(-Math.PI / 2);
                affineTransform.scale(-1.0, 1.0);
                break;
            case 6:
                affineTransform.translate(height, 0);
                affineTransform.rotate(Math.PI / 2);
                break;
            case 7:
                affineTransform.scale(-1.0, 1.0);
                affineTransform.translate(-height, 0);
                affineTransform.translate(0, width);
                affineTransform.rotate(3 * Math.PI / 2);
                break;
            case 8:
                affineTransform.translate(0, width);
                affineTransform.rotate(3 * Math.PI / 2);
                break;
            default:
                break;
        }
        return new AffineTransformOp(affineTransform,
                AffineTransformOp.TYPE_BILINEAR);
    }

    private boolean isPhoto(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg")
                || name.endsWith(".jpeg")
                || name.endsWith(".png")
                || name.endsWith(".gif");
    }

    public void restorePhoto(Photo photo) {
        Path photoLocalPath = Path.of(photo.getLocalPath());
        if ("bin".equals(photoLocalPath.getName(photoLocalPath.getNameCount() - 1).toString())) {
            fileManager.restoreFile(photo.getFileName(), photo.getLocalPath());
        }
    }

    public void deletePhoto(Photo photo) {
        Path photoLocalPath = Path.of(photo.getLocalPath());
        if (!"bin".equals(photoLocalPath.getName(photoLocalPath.getNameCount() - 1).toString())) {
            fileManager.deletePhoto(photo.getFileName(), photo.getLocalPath());
        }
    }

    public void addToAlbum(Album album) {
        handlingredirectToNewAlbum = false;

        if (album == null) {
            log.log(Level.WARNING, "Could not resolve addToAlbum, target album is null.");
            return;
        }
        for (Photo photo : photosDataModel) {
            album.addPhoto(photo);
        }

        albumDatabaseService.update(album);
        tableAlbumsBean.refreshAlbums();
        log.log(Level.INFO, photosDataModel.getRowCount() + " photos added to album successfully.");
        return;
    }

    public String addToAlbum() {
        //check selected option
        //if new album, redirect to create scree, keep reference to selected photos, add after creation is finished
        //else, add selected photos to selected album

        if (selectedAlbumId == 0) {
            handlingredirectToNewAlbum = true;
            return "albums-new.xhtml?faces-redirect=true";
        }

        Album album = albumDatabaseService.findAlbumById(selectedAlbumId);
        if (album == null) {
            log.log(Level.SEVERE, "Album with the given id was not found.");
            return null;
        }

        for (Photo photo : photosDataModel) {
            album.addPhoto(photo);
        }

        albumDatabaseService.update(album);
        tableAlbumsBean.refreshAlbums();
        log.log(Level.INFO, photosDataModel.getRowCount() + " photos added to album successfully.");
        return null;
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

    public String getNewDirectoryName() {
        return newDirectoryName;
    }

    public void setNewDirectoryName(String newDirectoryName) {
        this.newDirectoryName = newDirectoryName;
    }

    public boolean isDisplayDialog() {
        return displayDialog;
    }

    public void setDisplayDialog(boolean displayDialog) {
        this.displayDialog = displayDialog;
    }

    public String getDialogError() {
        return dialogError;
    }

    public void setDialogError(String dialogError) {
        this.dialogError = dialogError;
    }

    public void setTableAlbumsBean(TableAlbumsBean tableAlbumsBean) {
        this.tableAlbumsBean = tableAlbumsBean;
    }

}
