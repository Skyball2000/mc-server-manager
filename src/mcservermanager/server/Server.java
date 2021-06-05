package mcservermanager.server;

import mcservermanager.util.Constants;
import mcservermanager.version.Version;
import mcservermanager.version.VersionManager;
import yanwittmann.file.File;
import yanwittmann.file.FileUtils;
import yanwittmann.log.Log;
import yanwittmann.utils.Popup;
import yanwittmann.utils.Sleep;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private final File directory;
    private File serverFile;
    private Version version;
    private boolean isValid = false;
    private boolean eulaAccepted = false;

    /**
     * Auto detect server from directory
     */
    public Server(File directory, VersionManager versionManager) throws IOException {
        Log.info("Creating server from directory {}", directory);
        this.directory = directory;
        List<File> files = ServerManager.getFilesFromDir(directory);
        for (File file : files)
            if (file.isFile() && file.getName().contains(".jar")) {
                this.serverFile = file;
                version = versionManager.getVersionByIdentifier(file.getName().replace(".jar", ""));
                break;
            }
        isValid = true;
        if (acceptEula()) {
            eulaAccepted = true;
            Log.info("Eula terms have been accepted for server with version {} {}", version.id, getName());
        }
    }

    /**
     * Create new Server from version and name
     */
    public Server(Version version, String name) throws IOException {
        Log.info("Creating server {} from version {}", name, version.id);
        this.version = version;
        directory = new File(ServerManager.SERVER_DIRECTORY, name);
        if (directory.exists()) {
            Popup.message(Constants.PROJECT_TITLE, "Server already exists");
            return;
        }
        directory.mkdirs();
        serverFile = new File(directory, version.id + ".jar");
        if (!serverFile.exists()) version.getServer().copyFile(serverFile);
        isValid = true;
        if (acceptEula()) {
            eulaAccepted = true;
            Log.info("Eula terms have been accepted for server with version {} {}", version.id, getName());
        }
    }

    public boolean acceptEula() throws IOException {
        if (serverFile != null && serverFile.exists()) {
            File eula = new File(directory, "eula.txt");
            if (eula.exists()) {
                ArrayList<String> eulaText = eula.readToArrayList();
                for (int i = 0; i < eulaText.size(); i++) {
                    if (eulaText.get(i).equals("eula=false")) {
                        int answer = Popup.selectButton(Constants.PROJECT_TITLE,
                                "Do you want to accept the eula for the minecraft server " + getName() + " with version " + version.id + "?",
                                new String[]{"Yes", "No"});
                        if (answer == 0) {
                            Log.info("Accepting eula terms...");
                            eulaText.set(i, "eula=true");
                            eula.write(eulaText);
                            return true;
                        } else return false;
                    } else if (eulaText.get(i).equals("eula=true")) return true;
                }
            } else {
                run();
                for (int i = 0; i < 4; i++) {
                    Sleep.seconds(4);
                    if (eula.exists()) break;
                }
                if (!eula.exists()) {
                    Popup.error(Constants.PROJECT_TITLE, "Unable to accept eula for " + getName());
                    return false;
                }
                return acceptEula();
            }
        }
        return false;
    }

    public File backup() throws IOException {
        if (!ServerManager.BACKUP_DIRECTORY.exists()) ServerManager.BACKUP_DIRECTORY.mkdirs();
        File serverBackupDir = new File(ServerManager.BACKUP_DIRECTORY, getName());
        if (!serverBackupDir.exists()) serverBackupDir.mkdirs();
        File backupDir = new File(serverBackupDir, "" + System.currentTimeMillis());
        directory.copyDirectory(backupDir);
        return backupDir;
    }

    public String getName() {
        return directory.getName();
    }

    public Version getVersion() {
        return version;
    }

    public ImageIcon getIcon() {
        try {
            return new ImageIcon(ImageIO.read(new File(directory, "world/icon.png")));
        } catch (IOException e) {
            return null;
        }
    }

    public boolean isValid() {
        return isValid;
    }

    public void run() throws IOException {
        Log.info("Running server {} {}", version.id, getName());
        FileUtils.openJar(serverFile.getAbsolutePath(), directory.getAbsolutePath(), new String[]{});
    }
}
