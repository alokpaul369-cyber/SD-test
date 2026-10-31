package com.medix.client.ui.frame;

import com.medix.client.util.AppConfig;
import net.miginfocom.swing.MigLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import java.awt.Dimension;
import java.awt.Font;

/**
 * Top-level MediX window. Must be created and shown on the Event Dispatch Thread.
 */
public class MainFrame extends JFrame {

    public MainFrame(AppConfig config) {
        super("MediX");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1024, 700));

        var content = new JPanel(new MigLayout("fill, wrap 1", "[center]", "push[]8[]push"));

        var title = new JLabel("MediX");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 36f));

        var subtitle = new JLabel("Medicine Availability Finder  ·  mode: " + config.apiMode().name().toLowerCase());
        subtitle.setForeground(UIManager.getColor("Label.disabledForeground"));

        content.add(title);
        content.add(subtitle);
        setContentPane(content);

        pack();
        setLocationRelativeTo(null);
    }
}
