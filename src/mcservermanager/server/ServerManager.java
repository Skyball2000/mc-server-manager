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
    private List<Server> servers = new ArrayList<>();

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

    public void createServer(boolean onlyReleases) {
        String version = Popup.dropDown(Constants.PROJECT_TITLE, "Select the server version from the drop down below:",
                versionManager.getVersions().stream().filter(ver -> !onlyReleases || ver.type == Version.Type.RELEASE)
                        .map(ver -> ver.id).collect(Collectors.toList()).toArray(new String[]{}));
        if (version == null || version.length() == 0) return;
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

    public void deleteServer(Server server) {
        String confirmation = Popup.input(Constants.PROJECT_TITLE,
                "Are you sure you want to delete the server '" + server.getName() + "'?\n" +
                "This cannot be undone! Type 'delete forever' to confirm:", "");
        if (confirmation == null || !confirmation.equals("delete forever")) return;
        Log.info("Deleting server {}", server.getName());
        server.getDirectory().deleteDirectory();
        if (!server.getDirectory().exists()) {
            Log.info("Deleted server {}", server.getName());
            Popup.message(Constants.PROJECT_TITLE, "Deleted Server '" + server.getName() + "'");
            servers.remove(server);
            return;
        }
        Popup.message(Constants.PROJECT_TITLE, "Unable to delete the server '" + server.getName() + "'.\nMake sure the server is not running.");
    }

    public List<Server> getServers() {
        servers = servers.stream().filter(Server::isValid).collect(Collectors.toList());
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
