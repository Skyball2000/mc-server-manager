package mcservermanager;

import mcservermanager.version.VersionManager;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        VersionManager versionManager = new VersionManager();
        versionManager.getVersionByIdentifier("1.16.5").getMetaData();
    }
}
