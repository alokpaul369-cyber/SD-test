package com.medix.client;

import com.formdev.flatlaf.FlatLightLaf;
import com.medix.client.ui.frame.MainFrame;
import com.medix.client.util.AppConfig;

import javax.swing.SwingUtilities;

/**
 * MediX desktop client entry point.
 */
public final class MediXApp {

    private MediXApp() {
    }

    public static void main(String[] args) {
        var config = AppConfig.load();

        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup();
            new MainFrame(config).setVisible(true);
        });
    }
}
