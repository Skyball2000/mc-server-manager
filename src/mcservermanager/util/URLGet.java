package mcservermanager.util;

import yanwittmann.file.File;
import yanwittmann.log.Log;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class URLGet {

    public static ArrayList<String> getFromUrl(String url, boolean forceReload) throws IOException {
        return getFromUrl(new URL(url), forceReload);
    }

    public static ArrayList<String> getFromUrl(URL url, boolean forceReload) throws IOException {
        File downloadFile = new File(Constants.DATA_DIRECTORY + Constants.DATA_DOWNLOAD_DIRECTORY, url.getFile());
        if (forceReload || !downloadFile.exists()) {
            Log.info("Downloading {} to {}", url, downloadFile.getPath());
            downloadFile.getParentFile().mkdirs();
            downloadFile.download(url);
        }
        if (downloadFile.exists())
            return downloadFile.readToArrayList();
        return null;
    }
}
