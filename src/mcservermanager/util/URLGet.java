package mcservermanager.util;

import yanwittmann.file.File;
import yanwittmann.log.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
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

    public static String detectIP() {
        URL whatismyip;
        try {
            whatismyip = new URL("http://checkip.amazonaws.com");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "";
        }
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String ip = null;
        try {
            ip = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ip;
    }

    public static String detectLocalIp() {
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            return socket.getLocalAddress().getHostAddress();
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }
        return "";
    }
}
