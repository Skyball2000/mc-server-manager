package mcservermanager;

import mcservermanager.gui.GuiMainView;
import mcservermanager.server.ServerManager;
import mcservermanager.util.Constants;
import mcservermanager.version.VersionManager;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Main main = new Main();
    }

    public Main() {
        init();
    }

    private GuiMainView guiMainView;
    private JFrame mainFrame;

    private void init() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        VersionManager versionManager = new VersionManager();
        ServerManager serverManager = new ServerManager(versionManager);

        guiMainView = new GuiMainView(serverManager);
        mainFrame = new JFrame(Constants.PROJECT_TITLE);

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
}
