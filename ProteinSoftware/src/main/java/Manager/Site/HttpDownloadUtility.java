package Manager.Site;

import javafx.beans.property.SimpleStringProperty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A utility that downloads a file from a URL.
 * @author www.codejava.net (modified)
 */
public abstract class HttpDownloadUtility  {
    private static final int BUFFER_SIZE = 4096;

    protected File savedFile;

    /**
     * Downloads a file from a URL
     * @param fileURL HTTP URL of the file to be downloaded
     * @param saveDir path of the directory to save the file
     * @throws IOException
     */
    public String downloadFile(String fileURL, String saveDir)
            throws IOException {
        System.out.println("download 1");
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        System.out.println("download 2");
        int responseCode = httpConn.getResponseCode();
        System.out.println("download 3");
        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();
//            DownloadManager manager = new DownloadManager(contentLength);

            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10,
                            disposition.length() - 1);
                }
            } else {
                // extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                        fileURL.length());
            }

            System.out.println("Content-Type = " + contentType);
            System.out.println("Content-Disposition = " + disposition);
            System.out.println("Content-Length = " + contentLength);
            System.out.println("fileName = " + fileName);
            this.savedFile = new File(saveDir+File.separator+fileName);

            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = saveDir + File.separator + fileName;

            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            int bytesRead;
            int actualBytes = 0;
            byte[] buffer = new byte[BUFFER_SIZE];
            System.out.println("download 4");
            while (/*!manager.stopOrdered() && */(bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                actualBytes += bytesRead;
//                manager.update(actualBytes);
            }
//            if (manager.stopOrdered()) {
//                outputStream.close();
//                inputStream.close();
//                new File(saveFilePath).delete();
//                return (null);
//            }
//            if (actualBytes < contentLength)
//                manager.downloadError();
//            else
//                manager.finishedDownload();
            outputStream.close();
            inputStream.close();

            System.out.println("File downloaded");
            this.onDownloadDone();
            return (fileName);
        } else {
            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();
        this.onDownloadDone();
        return (null);
    }

    public String downloadFileInThread(String fileUrl, String saveDir) throws IOException{
        final SimpleStringProperty ret = new SimpleStringProperty();

        Thread thread = new Thread(() -> {
            try {
                ret.setValue(downloadFile(fileUrl, saveDir));
            }
            catch (IOException e){
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
        try {
            thread.join();
            return (ret.getValue());
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        return (null);
    }

    public void onDownloadDone(){

    }
}