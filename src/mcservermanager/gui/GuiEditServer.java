package mcservermanager.gui;

import mcservermanager.server.Server;
import mcservermanager.util.Constants;

import javax.swing.*;
import java.awt.event.*;

public class GuiEditServer extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton setServerIcon;
    private JButton setServerVersionButton;
    private JButton setServerNameButton;

    public GuiEditServer(Server server) {
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
        setServerVersionButton.addActionListener(e -> server.setVersion());
        setServerNameButton.addActionListener(e -> server.setName());
    }

    private void onOK() {
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public static void newInstance(Server server) {
        GuiEditServer dialog = new GuiEditServer(server);
        dialog.pack();
        dialog.setTitle(Constants.PROJECT_TITLE);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
