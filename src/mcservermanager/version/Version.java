package mcservermanager.version;

import mcservermanager.util.Constants;
import mcservermanager.util.URLGet;
import org.json.JSONObject;
import yanwittmann.file.File;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Version {

    public final String id, releaseTime, time;
    public final URL metaUrl;
    public final Type type;

    public Version(JSONObject versionJson) throws MalformedURLException {
        releaseTime = versionJson.getString("releaseTime");
        time = versionJson.getString("time");
        id = versionJson.getString("id");
        metaUrl = new URL(versionJson.getString("url"));
        type = Type.from(versionJson.getString("type"));
    }

    private URL serverJar, clientJar;

    public boolean getMetaData() throws IOException {
        ArrayList<String> metaData = URLGet.getFromUrl(metaUrl, false);
        if (metaData == null) return false;
        JSONObject jsonRoot = new JSONObject(String.join("", metaData));
        if (jsonRoot.has("downloads")) {
            serverJar = new URL(jsonRoot.getJSONObject("downloads").getJSONObject("server").getString("url"));
            clientJar = new URL(jsonRoot.getJSONObject("downloads").getJSONObject("client").getString("url"));
            return true;
        }
        return false;
    }

    public File getServer() throws IOException {
        File downloadFile = new File(Constants.DATA_DIRECTORY + Constants.SERVER_DIRECTORY + id + ".jar");
        if (!downloadFile.exists()) {
            downloadFile.getParentFile().mkdirs();
            if (serverJar == null)
                if (getMetaData()) {
                    downloadFile.download(serverJar);
                } else return null;
        }
        return downloadFile;
    }

    public File getClient() throws IOException {
        File downloadFile = new File(Constants.DATA_DIRECTORY + Constants.CLIENT_DIRECTORY + id + ".jar");
        if (!downloadFile.exists()) {
            downloadFile.getParentFile().mkdirs();
            if (clientJar == null)
                if (getMetaData()) {
                    downloadFile.download(clientJar);
                } else return null;
        }
        return downloadFile;
    }

    @Override
    public String toString() {
        return "Version{" +
               "releaseTime=" + releaseTime +
               ", time=" + time +
               ", id='" + id + '\'' +
               ", metaUrl=" + metaUrl +
               ", type=" + type +
               '}';
    }

    public enum Type {
        SNAPSHOT("snapshot"),
        RELEASE("release"),
        OLD_ALPHA("old_alpha"),
        OLD_BETA("old_beta"),
        NULL("null");

        public final String identifier;

        Type(String identifier) {
            this.identifier = identifier;
        }

        @Override
        public String toString() {
            return "Type{" +
                   "identifier='" + identifier + '\'' +
                   '}';
        }

        public static Type from(String type) {
            for (Type value : Type.values()) if (value.identifier.equals(type)) return value;
            return NULL;
        }
    }
}
