package mcservermanager.gui;

import mcservermanager.server.Server;
import mcservermanager.server.ServerManager;
import mcservermanager.util.Constants;
import yanwittmann.utils.GeneralUtils;
import yanwittmann.utils.Popup;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GuiMainView {
    private ServerManager serverManager;

    private JPanel mainPanel;
    private JScrollPane serverList;
    private JPanel serverListPanel;
    private JButton createNewServerButton;

    public GuiMainView(ServerManager serverManager) {
        this.serverManager = serverManager;
        createNewServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createNewServer();
            }
        });
    }

    public void updateView() {
        for (Component component : serverListPanel.getComponents()) serverListPanel.remove(component);
        ArrayList<Server> servers = serverManager.getServers();
        if (servers == null) {
            serverListPanel.setLayout(new GridLayout(1, 1));
            JLabel label = new JLabel();
            label.setText("<html><b>Something went wrong while reading the server data!</b></html>");
            label.setHorizontalAlignment(SwingConstants.CENTER);
            serverListPanel.add(label);
            return;
        }

        GridLayout layout = new GridLayout(servers.size(), 1);
        layout.setHgap(4);
        layout.setVgap(4);
        serverListPanel.setLayout(layout);
        for (Server server : servers) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setAlignment(FlowLayout.LEADING);
            JPanel panel = new JPanel(flowLayout);
            panel.setBorder(new TitledBorder(server.getName()));

            ImageIcon serverIcon = server.getIcon();
            if (serverIcon != null) {
                JLabel serverIconLabel = new JLabel();
                serverIconLabel.setVerticalAlignment(SwingConstants.TOP);
                serverIconLabel.setIcon(GeneralUtils.getScaledImage(server.getIcon(), 64, 64));
                panel.add(serverIconLabel);
            }

            JLabel serverVersionLabel = new JLabel();
            serverVersionLabel.setText("<html><b>Version: " + server.getVersion().id + "</b></html>");
            serverVersionLabel.setVerticalAlignment(SwingConstants.TOP);
            serverVersionLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
            panel.add(serverVersionLabel);

            JLabel startServerLabel = new JLabel();
            startServerLabel.setVerticalAlignment(SwingConstants.TOP);
            try {
                startServerLabel.setIcon(GeneralUtils.getScaledImage(new ImageIcon(
                        ImageIO.read(new File(Constants.DATA_DIRECTORY + Constants.IMG_DIRECTORY + "start_server.png"))), 16, 16));
                startServerLabel.setBorder(new EmptyBorder(0, 20, 0, 0));
                startServerLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        runServer(server);
                    }
                });
                panel.add(startServerLabel);
            } catch (IOException e) {
                e.printStackTrace();
            }

            JLabel backupServerLabel = new JLabel();
            backupServerLabel.setVerticalAlignment(SwingConstants.TOP);
            try {
                backupServerLabel.setIcon(GeneralUtils.getScaledImage(new ImageIcon(
                        ImageIO.read(new File(Constants.DATA_DIRECTORY + Constants.IMG_DIRECTORY + "backup_server.png"))), 16, 16));
                backupServerLabel.setBorder(new EmptyBorder(0, 5, 0, 0));
                backupServerLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        backupServer(server);
                    }
                });
                panel.add(backupServerLabel);
            } catch (IOException e) {
                e.printStackTrace();
            }

            serverListPanel.add(panel);
            serverListPanel.revalidate();
        }
    }

    private void createNewServer() {

    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void runServer(Server server) {
        try {
            server.run();
        } catch (IOException e) {
            e.printStackTrace();
            Popup.error(Constants.PROJECT_TITLE, "Unable to run server " + server.getName() + ": " + e.getMessage());
        }
    }

    public void backupServer(Server server) {
        try {
            File backupLocation = server.backup();
            Popup.message(Constants.PROJECT_TITLE, "Backed up server to\n" + backupLocation.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            Popup.error(Constants.PROJECT_TITLE, "Unable to backup server " + server.getName() + ": " + e.getMessage());
        }
    }

    public static void main(String[] args) {
    }
}
