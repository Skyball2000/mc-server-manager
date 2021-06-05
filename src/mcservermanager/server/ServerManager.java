package mcservermanager.server;

import mcservermanager.util.Constants;
import mcservermanager.version.Version;
import mcservermanager.version.VersionManager;
import yanwittmann.file.File;
import yanwittmann.log.Log;
import yanwittmann.utils.Popup;

import java.io.IOException;
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
        if (!BACKUP_DIRECTORY.exists()) BACKUP_DIRECTORY.mkdirs();
    }

    public void detectServers() {
        for (File file : getFilesFromDir(SERVER_DIRECTORY))
            try {
                if (file.isDirectory()) {
                    Server server = new Server(file, versionManager);
                    if (server.isValid())
                        servers.add(server);
                }
            } catch (Exception e) {
                Log.error("Unable to read Server {}: {}", file, e.getMessage());
                e.printStackTrace();
            }
    }

    public void createServer(String version) {
        Version serverVersion = versionManager.getVersionByIdentifier(version);
        if (serverVersion != null) {
            String name = Popup.input(Constants.PROJECT_TITLE, "Enter a name for the server:", "");
            if (name != null && name.length() > 0 && name.matches("^[^\\\\/:*?\"<>|]+$")) {
                Server server = null;
                try {
                    server = new Server(serverVersion, name);
                } catch (IOException e) {
                    e.printStackTrace();
                    Popup.error(Constants.PROJECT_TITLE, "Unable to create server:\n" + e.getMessage());
                }
                if (server != null && server.isValid())
                    servers.add(server);
            } else Popup.error(Constants.PROJECT_TITLE, "Invalid server name.");
        } else Popup.error(Constants.PROJECT_TITLE, "Unknown version " + version);
    }

    public ArrayList<Server> getServers() {
        return servers;
    }

    public static List<File> getFilesFromDir(File directory) {
        java.io.File[] files = directory.listFiles();
        if (files != null)
            return Arrays.stream(files).map(file -> new File(file.getAbsolutePath())).collect(Collectors.toList());
        return new ArrayList<>();
    }

    public final static File SERVER_DIRECTORY = new File(Constants.SERVER_DIRECTORY);
    public final static File BACKUP_DIRECTORY = new File(Constants.DATA_DIRECTORY + Constants.BACKUP_DIRECTORY);
}
