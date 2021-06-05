package mcservermanager.gui;

import mcservermanager.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GuiMainView {
    private JPanel mainPanel;
    private JScrollPane serverList;
    private JPanel serverListPanel;

    public GuiMainView() {
    }

    public void updateView() {
        GridLayout layout = new GridLayout(16, 1);
        layout.setHgap(4);
        layout.setVgap(4);
        serverListPanel.setLayout(layout);
        for (int i = 0; i < 7; i++) {
            JLabel label = new JLabel();
            label.setText("test " + i);
            label.setPreferredSize(new Dimension(40, 20));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVisible(true);
            serverListPanel.add(label);
        }
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        GuiMainView settings = new GuiMainView();
        JFrame frame = new JFrame(Constants.PROJECT_TITLE);

        frame.setContentPane(settings.getMainPanel());
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose();
                System.exit(0);
            }
        });
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
