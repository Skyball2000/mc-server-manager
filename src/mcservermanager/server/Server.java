package mcservermanager.server;

import mcservermanager.util.Constants;
import mcservermanager.version.Version;
import mcservermanager.version.VersionManager;
import yanwittmann.file.File;
import yanwittmann.log.Log;
import yanwittmann.utils.Popup;

import java.io.IOException;
import java.util.ArrayList;

public class Server {
    private final File directory;
    private Version version;

    public Server(File directory, Version version) throws IOException {
        this.directory = directory;
        this.version = version;
        Log.info("Creating server from directory {}", directory);
        if (acceptEula()) {

        }
    }

    public boolean acceptEula() throws IOException {
        File eula = new File(directory, "eula.txt");
        if (eula.exists()) {
            ArrayList<String> eulaText = eula.readToArrayList();
            for (int i = 0; i < eulaText.size(); i++) {
                if (eulaText.get(i).equals("eula=false")) {
                    int answer = Popup.selectButton(Constants.PROJECT_TITLE,
                            "Do you want to accept the eula for the minecraft server " + version.id + "?", new String[]{"Yes", "No"});
                    if (answer == 0) {
                        eulaText.set(i, "eula=true");
                        eula.write(eulaText);
                        Log.info("Eula terms have been accepted!");
                        return true;
                    } else return false;
                }
            }
        }
        return false;
    }

    public boolean run() {
        return false;
    }
}
