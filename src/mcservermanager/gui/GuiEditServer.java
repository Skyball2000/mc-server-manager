package mcservermanager.gui;

import mcservermanager.server.Server;
import mcservermanager.server.ServerManager;
import mcservermanager.util.Constants;

import javax.swing.*;
import java.awt.event.*;

public class GuiEditServer extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton setServerIcon;
    private JButton setServerVersionButton;
    private JButton setServerNameButton;
    private JButton openDatapackFolderButton;
    private JButton editPropertiesButton;
    private JButton setJavaMemoryButton;
    private JButton quickEditPropertiesButton;

    public GuiEditServer(Server server, ServerManager serverManager) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        setServerIcon.addActionListener(e -> server.setIcon());
        setServerVersionButton.addActionListener(e -> serverManager.setServerVersion(server));
        setServerNameButton.addActionListener(e -> server.setName());
        editPropertiesButton.addActionListener(e -> server.openProperties());
        openDatapackFolderButton.addActionListener(e -> server.openDatapacks());
        setJavaMemoryButton.addActionListener(e -> server.setJavaMemory());
        quickEditPropertiesButton.addActionListener(e -> server.quickEditProperties());
    }

    private void onOK() {
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public static void newInstance(Server server, ServerManager serverManager) {
        GuiEditServer dialog = new GuiEditServer(server, serverManager);
        dialog.setIconImage(new ImageIcon(Constants.DATA_DIRECTORY + Constants.IMG_DIRECTORY + "icon.png").getImage());
        dialog.setTitle(Constants.PROJECT_TITLE);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
