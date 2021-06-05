package mcservermanager.util;

import yanwittmann.file.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class URLGet {

    public static ArrayList<String> getFromUrl(String url) throws IOException {
        URL getUrl = new URL(url);
        System.out.println(getUrl.getFile());
        return FileUtils.readFileToArrayList(new File("README.md"));
    }
}
