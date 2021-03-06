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

    public final static String[] JAVA_VM_RAM_OPTIONS = new String[]{"64M", "128M", "256M", "512M", "1024M", "2048M", "4096M", "8192M", "10000M", "12288M", "16384M"};
    public final static String[][] QUICK_PROPERTIES = new String[][]{
            {"server-port&&query.port", "gamemode", "enable-command-block", "motd", "hardcore", "pvp", "difficulty", "max-players", "spawn-protection", "allow-flight"},
            {"Port", "Gamemode", "Command blocks enabled", "Message of the day (MOTD)", "Hardcore", "PvP", "Difficulty", "Max players", "Spawn protection radius", "Allow flying"},
            {"25565", "survival", "false", "\\u00a77Created using the\\u00a7r\\n\\u00a79github.com\\/Skyball2000\\/mc-server-manager", "false", "true", "easy", "20", "16", "false"}};

}
