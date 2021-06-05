package mcservermanager;

import mcservermanager.gui.GuiMainView;
import mcservermanager.server.ServerManager;
import mcservermanager.util.Constants;
import mcservermanager.util.URLGet;
import mcservermanager.version.VersionManager;
import yanwittmann.types.Configuration;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        init();
    }

    private GuiMainView guiMainView;
    private JFrame mainFrame;
    private static Configuration configuration;

    private void init() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        try {
            if (!Constants.CONFIGURATION_MAIN_FILE.getParentFile().exists())
                Constants.CONFIGURATION_MAIN_FILE.getParentFile().mkdirs();
            configuration = new Configuration(Constants.CONFIGURATION_MAIN_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        VersionManager versionManager = new VersionManager();
        ServerManager serverManager = new ServerManager(versionManager);

        guiMainView = new GuiMainView(serverManager);
        mainFrame = new JFrame(Constants.PROJECT_TITLE);

        mainFrame.setIconImage(new ImageIcon(Constants.DATA_DIRECTORY + Constants.IMG_DIRECTORY + "icon.png").getImage());
        mainFrame.setContentPane(guiMainView.getMainPanel());
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mainFrame.dispose();
                System.exit(0);
            }
        });
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);

        guiMainView.updateView();
    }

    public static Configuration getConfiguration() {
        return configuration;
    }
}
