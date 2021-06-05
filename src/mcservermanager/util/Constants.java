package mcservermanager.util;

import yanwittmann.file.File;

public class Constants {

    public final static String DATA_DIRECTORY = "data/";
    public final static String DATA_DOWNLOAD_DIRECTORY = "download/";
    public final static String SERVER_DIRECTORY = "server/";
    public final static String CLIENT_DIRECTORY = "client/";
    public final static String BACKUP_DIRECTORY = "backups/";
    public final static String IMG_DIRECTORY = "img/";
    public final static String CONFIGURATION_DIRECTORY = "config/";

    public final static File CONFIGURATION_MAIN_FILE = new File(DATA_DIRECTORY + CONFIGURATION_DIRECTORY + "main.cfg");

    public final static String VERSION_MANIFEST_URL = "https://launchermeta.mojang.com/mc/game/version_manifest.json";

    public final static String PROJECT_TITLE = "Minecraft Server Manager";

}
