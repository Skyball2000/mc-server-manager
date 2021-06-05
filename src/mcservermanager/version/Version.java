package mcservermanager.version;

import mcservermanager.util.URLGet;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

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

    public void getMetaData() throws IOException {
        URLGet.getFromUrl(metaUrl, false);
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
