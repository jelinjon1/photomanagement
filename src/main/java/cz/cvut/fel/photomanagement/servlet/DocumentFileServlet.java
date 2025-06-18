/*

 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package cz.cvut.fel.photomanagement.servlet;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.primefaces.shaded.commons.io.FilenameUtils;

/**
 * Implements a servlet that hadles requests for photo files from server's file sysyem, and serves them to client.
 *
 * @author jelinjon
 */
@WebServlet(name = "DocumentFileServlet", urlPatterns = {"/documentfiles/*"})
public class DocumentFileServlet extends HttpServlet {

    @Inject
    @ConfigProperty(name = "photos.directory.path")
    private String photosDirectoryPath;

    private static final String HTTP_HEADER_LAST_MODIFIED_RESPONSE = "Last-Modified";
    private static final String HTTP_HEADER_LAST_MODIFIED_REQUEST = "if-modified-since";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fileName = request.getPathInfo();
        if (fileName == null || !fileName.startsWith("/") || fileName.length() < 2) {
            response.sendError(404);
            return;
        }
        long lastModified = request.getDateHeader(HTTP_HEADER_LAST_MODIFIED_REQUEST);
        final String extension = FilenameUtils.getExtension(fileName);
        final byte[] bytes;
        try {
            bytes = readAllBytes(fileName, lastModified);
        } catch (IOException e) {
            response.sendError(500, "Problem reading file " + fileName + ": " + e.getMessage());
            return;
        }
        if (bytes == null) {
            // file was not modified, just return 304
            response.sendError(304);
        } else {
            // set client caching, valid for a year, Expires for backwards comp. with HTTP 1.0
            // immutable because app should not modify files
            response.setHeader("Cache-Control", "public, max-age=31536000, immutable");
            long expires = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(365);
            response.setDateHeader("Expires", expires);

            response.setContentType("image/" + extension);
            response.setContentLength(bytes.length);
            response.setDateHeader(HTTP_HEADER_LAST_MODIFIED_RESPONSE, new Date().getTime());
            try (ServletOutputStream out = response.getOutputStream()) {
                out.write(bytes);
            }
        }
    }

    public Path getPath(String fileName) throws IOException {
        final String path = photosDirectoryPath;
        if (fileName.startsWith("../") || fileName.contains("/../")) {
            throw new IOException("Cannot get file outside data directory");
        }
        return Paths.get(path, fileName);
    }

    /**
     * return bytes of the file of null if it was not modified
     */
    public byte[] readAllBytes(String fileName, long ifModifiedSince) throws IOException {
        final Path path = getPath(fileName);
        if (path.toFile().lastModified() <= ifModifiedSince) {
            return null; // not modified
        } else {
            return Files.readAllBytes(path);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
