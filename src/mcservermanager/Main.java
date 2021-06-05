package mcservermanager;

import mcservermanager.util.URLGet;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        URLGet.getFromUrl("https://launchermeta.mojang.com/mc/game/version_manifest.json");
    }
}
