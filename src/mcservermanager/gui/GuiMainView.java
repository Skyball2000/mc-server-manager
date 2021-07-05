package mcservermanager.gui;

import mcservermanager.server.Server;
import mcservermanager.server.ServerManager;
import mcservermanager.util.Constants;
import mcservermanager.util.URLGet;
import yanwittmann.file.FileUtils;
import yanwittmann.utils.GeneralUtils;
import yanwittmann.utils.Popup;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class GuiMainView {
    private ServerManager serverManager;

    private JPanel mainPanel;
    private JScrollPane serverList;
    private JPanel serverListPanel;
    private JButton createNewServerButton;
    private JButton reloadButton;
    private JLabel yourIpLabel;
    private JLabel yourLocalIpLabel;

    public GuiMainView(ServerManager serverManager) {
        this.serverManager = serverManager;
        createNewServerButton.addActionListener(e -> createNewServer());
        reloadButton.addActionListener(e -> updateView());
        yourIpLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                GeneralUtils.copyString(yourIpLabel.getText().replaceAll("(.+) {2}.+", "$1"));
            }
        });
        new Thread(() -> yourIpLabel.setText(URLGet.detectIP() + "  (click to copy)")).start();
        yourLocalIpLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                GeneralUtils.copyString(yourLocalIpLabel.getText().replaceAll("(.+) {2}.+", "$1"));
            }
        });
        new Thread(() -> yourLocalIpLabel.setText(URLGet.detectLocalIp() + "  (click to copy)")).start();
        fixScrolling(serverList);
    }

    public void updateView() {
        for (Component component : serverListPanel.getComponents()) serverListPanel.remove(component);
        java.util.List<Server> servers = serverManager.getServers();
        if (servers == null || servers.size() == 0) {
            serverListPanel.setLayout(new GridLayout(1, 1));
            JLabel label = new JLabel();
            label.setText("<html><b>There are no servers! Create one using the button below.</b></html>");
            label.setHorizontalAlignment(SwingConstants.CENTER);
            serverListPanel.add(label);
            serverListPanel.revalidate();
            mainPanel.revalidate();
            mainPanel.repaint();
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

            JLabel startServerLabel = new JLabel();
            startServerLabel.setVerticalAlignment(SwingConstants.TOP);
            try {
                startServerLabel.setIcon(GeneralUtils.getScaledImage(new ImageIcon(
                        ImageIO.read(new File(Constants.DATA_DIRECTORY + Constants.IMG_DIRECTORY + "start_server.png"))), 16, 16));
                startServerLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
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

            JLabel editServerLabel = new JLabel();
            editServerLabel.setVerticalAlignment(SwingConstants.TOP);
            try {
                editServerLabel.setIcon(GeneralUtils.getScaledImage(new ImageIcon(
                        ImageIO.read(new File(Constants.DATA_DIRECTORY + Constants.IMG_DIRECTORY + "edit_server.png"))), 16, 16));
                editServerLabel.setBorder(new EmptyBorder(0, 5, 0, 0));
                editServerLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        editServer(server);
                    }
                });
                panel.add(editServerLabel);
            } catch (IOException e) {
                e.printStackTrace();
            }

            JLabel deleteServerLabel = new JLabel();
            deleteServerLabel.setVerticalAlignment(SwingConstants.TOP);
            try {
                deleteServerLabel.setIcon(GeneralUtils.getScaledImage(new ImageIcon(
                        ImageIO.read(new File(Constants.DATA_DIRECTORY + Constants.IMG_DIRECTORY + "delete_server.png"))), 16, 16));
                deleteServerLabel.setBorder(new EmptyBorder(0, 5, 0, 0));
                deleteServerLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        deleteServer(server);
                    }
                });
                panel.add(deleteServerLabel);
            } catch (IOException e) {
                e.printStackTrace();
            }

            JLabel serverVersionLabel = new JLabel();
            serverVersionLabel.setText("<html><b>Version: " + server.getVersion().id + "</b></html>");
            serverVersionLabel.setVerticalAlignment(SwingConstants.TOP);
            serverVersionLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
            panel.add(serverVersionLabel);

            serverListPanel.add(panel);
            serverListPanel.revalidate();
            mainPanel.revalidate();
            mainPanel.repaint();
        }
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
        updateView();
    }

    public void backupServer(Server server) {
        try {
            File backupLocation = server.backup();
            Popup.message(Constants.PROJECT_TITLE, "Backed up server to\n" + backupLocation.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            Popup.error(Constants.PROJECT_TITLE, "Unable to backup server " + server.getName() + ": " + e.getMessage());
        }
        updateView();
    }

    private void createNewServer() {
        //serverManager.createServer();
        //updateView();
        String additionalText = "";
        try {
            additionalText = "\nWith this, a total of " + String.join("", FileUtils.getResponseFromURL("http://yanwittmann.de/projects/countapi/hit.php?key=serversCreated&namespace=mcservermanager"))
                             + " servers have been created using this tool!";
        } catch (IOException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        Popup.message(Constants.PROJECT_TITLE, "Thank you for using the MC Server Manager!" + additionalText);
    }

    private void deleteServer(Server server) {
        serverManager.deleteServer(server);
        updateView();
    }

    private void editServer(Server server) {
        GuiEditServer.newInstance(server, serverManager);
        updateView();
    }

    public static void fixScrolling(JScrollPane jScrollPane) {
        JLabel systemLabel = new JLabel();
        FontMetrics metrics = systemLabel.getFontMetrics(systemLabel.getFont());
        int lineHeight = metrics.getHeight();
        int charWidth = metrics.getMaxAdvance();

        JScrollBar systemVBar = new JScrollBar(JScrollBar.VERTICAL);
        JScrollBar systemHBar = new JScrollBar(JScrollBar.HORIZONTAL);
        int verticalIncrement = systemVBar.getUnitIncrement();
        int horizontalIncrement = systemHBar.getUnitIncrement();

        jScrollPane.getVerticalScrollBar().setUnitIncrement(lineHeight * verticalIncrement);
        jScrollPane.getHorizontalScrollBar().setUnitIncrement(charWidth * horizontalIncrement);
    }

    public static void main(String[] args) {
    }
}
