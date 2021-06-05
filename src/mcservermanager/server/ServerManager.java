package mcservermanager.server;

import mcservermanager.util.Constants;
import mcservermanager.version.VersionManager;
import yanwittmann.file.File;
import yanwittmann.log.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ServerManager {
    private final VersionManager versionManager;
    private final ArrayList<Server> servers = new ArrayList<>();

    public ServerManager(VersionManager versionManager) {
        this.versionManager = versionManager;
        if (!SERVER_DIRECTORY.exists()) SERVER_DIRECTORY.mkdirs();
        else detectServers();
    }

    public void detectServers() {
        for (File file : getFilesFromDir(SERVER_DIRECTORY))
            try {
                if (file.isDirectory())
                    servers.add(new Server(file, versionManager.getVersionByIdentifier(file.getName())));
            } catch (Exception e) {
                Log.error("Unable to read Server {}: {}", file, e.getMessage());
                e.printStackTrace();
            }
    }

    private static List<File> getFilesFromDir(File directory) {
        java.io.File[] files = directory.listFiles();
        if (files != null)
            return Arrays.stream(files).map(file -> new File(file.getAbsolutePath())).collect(Collectors.toList());
        return new ArrayList<>();
    }

    private final static File SERVER_DIRECTORY = new File(Constants.SERVER_DIRECTORY);
}
