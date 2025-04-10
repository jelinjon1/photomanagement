package cz.cvut.fel.photomanagement.faces;

import cz.cvut.fel.photomanagement.comparator.PhotoByDate;
import cz.cvut.fel.photomanagement.entities.Album;
import cz.cvut.fel.photomanagement.entities.Photo;
import cz.cvut.fel.photomanagement.faces.model.FilePlaceholder;
import cz.cvut.fel.photomanagement.faces.util.AlbumMenuOption;
import cz.cvut.fel.photomanagement.faces.util.Breadcrumb;
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
    private Photo selectedPhoto = null;
    private Long selectedPhotoParameter;
    private DataModel<Photo> photosDataModel;
    private DataModel<FilePlaceholder> filesDataModel;
    private String filesPath;
    private ArrayList<String> paths;
    private boolean handlingredirectToNewAlbum = false;
    private UploadedFiles files;
    private static final int DISPLAYED_PHOTO_HEIGHT = 300;

    @PostConstruct
    public void init() {
        paths = new ArrayList<>();
        albumOptions = transformIntoOptions(albumDatabaseService.listAllAlbums());
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

    // receives full path from root as a String
    public void changeDirectory(String path) {
        loadFiles();
    }

    public void loadFiles() {
        Path path = Path.of(filesPath);
        boolean locationIsThumbnailsDirectory = false;

        System.out.print("CURRENT PATH: ");
        for (int i = 0; i < path.getNameCount(); i++) {
            System.out.print(path.getName(i) + " / ");
            if (path.getName(i).equals("thumbnails")) {
                locationIsThumbnailsDirectory = true;
            }
        }
        System.out.println();

        // fetch all present files located in directory given by navigation
        List<File> allFiles = fileManager.loadFiles(filesPath);

        // filter directories into separate data model
        filesDataModel = new ListDataModel<>(allFiles.stream()
                .filter(File::isDirectory)
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
                    System.out.println("Loaded existing photo: " + existingPhoto);
                } else {
                    Photo photo = new Photo(file, filesPath);
                    photoDatabaseService.savePhoto(photo);
                    photosList.add(photo);
                    System.out.println("Saved new photo: " + photo);
                }
            }

        }
        // sort list of photos
        Comparator dateComparator = new PhotoByDate();
        photosList.sort(dateComparator);

        for (Photo photo : photosList) {
            System.out.println(photo);
        }

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

            System.out.println(thumbnailFilePath);
            File thumbnailFile = thumbnailFilePath.toFile();

            // check if thumbnail already generated, if so, return
            if (thumbnailFile.exists()) {
                System.out.println("Thumbnail already exists for " + inputFile);
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

            //BICUBIC interpolation
//            BufferedImage thumbnailImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
//            Graphics2D g2d = thumbnailImage.createGraphics();
//
//            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//            g2d.drawImage(originalImage, 0, 0, w, h, null);
//            g2d.dispose();
            // write the thumbnail to a file
            ImageIO.write(thumbnailImage, "png", thumbnailFilePath.toFile());

            ThumbnailWebSocket.sendThumbnailUpdate(photoId, thumbnailFilePath.toString());

            System.out.println("Thumbnail created: " + thumbnailFilePath);
        } catch (Exception | Error e) {
            System.err.println("Failed to create thumbnail for: " + inputFile.getName());
            e.printStackTrace();
        }
    }

    private boolean isPhoto(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".gif");
    }

    /*
    check if photo is already inside /bin
    if not:
    check if ./bin exists
        if so: move photo to bin
        else: create ./bin and move photo
     */
    public void deletePhoto(Photo photo) {
        if ((photo.getLocalPath().endsWith("/bin"))) {

            photoDatabaseService.deletePhoto(photo);
        } else {
            fileManager.createDirectory(photo.getLocalPath(), "bin");
            fileManager.moveToBin(photo.getFileName(), photo.getLocalPath());
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
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Album not found", null));
            return null;
        }

        for (Photo photo : photosDataModel) {
            album.addPhoto(photo);
        }

        albumDatabaseService.update(album);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Photos added to album successfully", null));
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
}
