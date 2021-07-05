package mcservermanager.server;

import mcservermanager.util.Constants;
import mcservermanager.version.Version;
import mcservermanager.version.VersionManager;
import yanwittmann.file.File;
import yanwittmann.file.FileUtils;
import yanwittmann.log.Log;
import yanwittmann.types.Configuration;
import yanwittmann.utils.Popup;
import yanwittmann.utils.Sleep;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private File directory;
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
                if (new File(directory, "ops.txt").exists()) return true;
                if (new File(directory, "ops.json").exists()) return true;
                run();
                for (int i = 0; i < 4; i++) {
                    Sleep.seconds(4);
                    if (eula.exists()) break;
                }
                if (!eula.exists()) {
                    Popup.error(Constants.PROJECT_TITLE, "Unable to accept eula for " + getName() +
                                                         "\nMake sure that you use the latest java version!");
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

    public void setIcon() {
        java.io.File[] files = FileUtils.windowsFilePicker();
        if (files != null && files.length > 0) {
            File from = new File(files[0].getAbsolutePath());
            try {
                File dest = new File(directory, "world/icon.png");
                dest.getParentFile().mkdirs();
                FileUtils.copyFile(from, dest);
            } catch (IOException e) {
                Popup.error(Constants.PROJECT_TITLE, "Unable to copy file: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void setVersion(Version version) {
        this.version = version;
        if (!serverFile.delete()) {
            Popup.error(Constants.PROJECT_TITLE, "Unable to delete old version!\nMake sure the server " + getName() + " is not running.");
            return;
        }
        serverFile = new File(directory, version.id + ".jar");
        if (!serverFile.exists()) {
            try {
                version.getServer().copyFile(serverFile);
            } catch (IOException e) {
                e.printStackTrace();
                Popup.error(Constants.PROJECT_TITLE, "Unable to copy file to " + serverFile);
            }
        }
    }

    public void setName() {
        String name = Popup.input(Constants.PROJECT_TITLE, "Enter a name for the server:", "");
        if (name != null && name.length() > 0 && name.matches("^[^\\\\/:*?\"<>|]+$")) {
            File newDirectory = new File(directory.getParentFile(), name);
            if (!directory.renameTo(newDirectory))
                Popup.message(Constants.PROJECT_TITLE, "Unable to rename server.\nMake sure the server " +
                                                       getName() + " is not running.");
            else {
                directory = newDirectory;
                serverFile = new File(directory, version.id + ".jar");
                Log.info("Renamed server to {}", getName());
            }
        }
    }

    public void setJavaMemory() {
        try {
            initializeConfig();
            String min = Popup.dropDown(Constants.PROJECT_TITLE, "Select the minimum java memory", Constants.JAVA_VM_RAM_OPTIONS, "1024M");
            if (min == null || min.length() == 0) return;
            setConfigValue("-Xms", min);
            String max = Popup.dropDown(Constants.PROJECT_TITLE, "Select the maximum java memory", Constants.JAVA_VM_RAM_OPTIONS, "2048M");
            if (max == null || max.length() == 0) return;
            setConfigValue("-Xmx", max);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openProperties() {
        try {
            new File(directory, "server.properties").open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openDatapacks() {
        try {
            new File(directory, "world/datapacks").open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Configuration configuration = null;

    private String getConfigValue(String key, String defaultValue) {
        try {
            initializeConfig();
        } catch (IOException e) {
            e.printStackTrace();
            Log.error("Unable to create configuration file inside server directory: {}", directory.getAbsolutePath());
            return defaultValue;
        }
        return configuration.getOrDefault(key, defaultValue);
    }

    private void setConfigValue(String key, String value) {
        try {
            initializeConfig();
        } catch (IOException e) {
            e.printStackTrace();
            Log.error("Unable to create configuration file inside server directory: {}", directory.getAbsolutePath());
            return;
        }
        configuration.set(key, value);
    }

    private Configuration initializeConfig() throws IOException {
        if (configuration != null) return configuration;
        configuration = new Configuration(new File(directory, "mc-server-manager.cfg"));
        return configuration;
    }

    public File getDirectory() {
        return directory;
    }

    public File getServerFile() {
        return serverFile;
    }

    public boolean isValid() {
        return isValid && serverFile != null && serverFile.exists() && directory != null && directory.exists();
    }

    public void run() throws IOException {
        Log.info("Running server {} with version {}", getName(), version.id);

        String volume = directory.getAbsolutePath().replaceAll("([a-zA-Z]:).+", "$1");
        String startServerCommand = "java -Xms" + getConfigValue("-Xms", "1024M") + " -Xmx" + getConfigValue("-Xmx", "2048M") + " -jar " + serverFile.getName();
        String command = "cmd /c \"cd " + directory.getAbsolutePath() + " && " + volume + " && " + startServerCommand + "\"";
        System.out.println("Executing CMD command: " + command);
        Runtime.getRuntime().exec(command, null, directory);
    }

    @Override
    public String toString() {
        return "Server{" +
               "directory=" + directory +
               ", serverFile=" + serverFile +
               ", version=" + version +
               ", isValid=" + isValid +
               ", eulaAccepted=" + eulaAccepted +
               '}';
    }
}
