package mcservermanager.version;

import mcservermanager.Main;
import mcservermanager.util.Constants;
import mcservermanager.util.URLGet;
import org.json.JSONArray;
import org.json.JSONObject;
import yanwittmann.log.Log;
import yanwittmann.types.Configuration;

import java.io.IOException;
import java.util.ArrayList;

public class VersionManager {
    private final ArrayList<Version> versions = new ArrayList<>();

    public VersionManager() {
        try {
            if (!init()) Log.error("Unable to initialize version manager");
        } catch (Exception e) {
            Log.error("Unable to initialize version manager: Unable to get versions from {}: {}", Constants.VERSION_MANIFEST_URL, e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean init() throws IOException {
        Configuration config = Main.getConfiguration();
        long lastUpdatedTime = Long.parseLong(config.getOrDefault("lastVersionManifestUpdateTime", "0"));
        config.set("lastVersionManifestUpdateTime", System.currentTimeMillis());
        ArrayList<String> versionMaifest = URLGet.getFromUrl(Constants.VERSION_MANIFEST_URL, lastUpdatedTime + 43200000 < System.currentTimeMillis());
        if (versionMaifest == null) return false;
        JSONObject jsonRoot = new JSONObject(String.join("", versionMaifest));

        JSONArray versionsArrayJson = jsonRoot.getJSONArray("versions");
        for (int i = 0; i < versionsArrayJson.length(); i++) {
            JSONObject versionJson = versionsArrayJson.getJSONObject(i);
            versions.add(new Version(versionJson));
        }

        return true;
    }

    public Version getVersionByIdentifier(String identifier) {
        return versions.stream().filter(version -> version.id.equals(identifier)).findFirst().orElse(null);
    }

    public ArrayList<Version> getVersions() {
        return versions;
    }

    public void printAllVersions() {
        versions.forEach(Log::info);
    }
}
