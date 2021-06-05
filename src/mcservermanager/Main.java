package mcservermanager;

import mcservermanager.server.ServerManager;
import mcservermanager.version.VersionManager;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        //getVersions();
        servers();
    }

    private static void servers() {
        VersionManager versionManager = new VersionManager();
        ServerManager serverManager = new ServerManager(versionManager);
    }

    private static void getVersions() throws IOException {
        VersionManager versionManager = new VersionManager();
        System.out.println(versionManager.getVersionByIdentifier("1.16.5").getServer());
    }
}
