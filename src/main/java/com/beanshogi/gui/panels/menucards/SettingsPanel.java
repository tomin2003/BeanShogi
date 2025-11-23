package com.beanshogi.gui.panels.menucards;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;

import java.awt.Dimension;

import com.beanshogi.gui.ShogiWindow;
import com.beanshogi.gui.util.*;

public class SettingsPanel extends JPanel {
    private final ShogiWindow window;
    private Runnable backAction;
    private final JRadioButton windowedButton;
    private final JRadioButton fullscreenButton;

    public SettingsPanel(ShogiWindow window) {

        this.window = window;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Default back behavior returns to the main menu unless overridden.
        backAction = () -> window.showCard("MAIN");

        // Aspect ratio setting radio buttons
        windowedButton = new JRadioButton("Windowed");
        windowedButton.addActionListener(e -> window.setSmallWindow());
        
        // Windowed by default
        windowedButton.setSelected(true);
        
        fullscreenButton = new JRadioButton("Full screen");
        fullscreenButton.addActionListener(e -> window.setFullscreenWindow());

        ButtonGroup aButtonGroup = new ButtonGroup();
        aButtonGroup.add(windowedButton);
        aButtonGroup.add(fullscreenButton);

        JLabel dpLabel = new JLabel("Display mode");
        dpLabel.setPreferredSize(new Dimension(120, 20));

        JPanel dpPanel = new JPanel();
        dpPanel.setLayout(new BoxLayout(dpPanel, BoxLayout.X_AXIS));
        dpPanel.add(dpLabel);
        dpPanel.add(Box.createHorizontalStrut(30));
        dpPanel.add(windowedButton);
        dpPanel.add(fullscreenButton);

        // Volume sliders
        JSlider bgVolume = new JSlider(0, 100);
        bgVolume.setPreferredSize(new Dimension(200, 20));
        bgVolume.setMaximumSize(new Dimension(200, 20));
        bgVolume.setValue((int)(SoundPlayer.getBgmVolume() * 100)); // Set initial value
        bgVolume.addChangeListener(e -> {
            float volume = bgVolume.getValue() / 100.0f;
            SoundPlayer.setBgmVolume(volume);
        });

        JSlider seVolume = new JSlider(0, 100);
        seVolume.setPreferredSize(new Dimension(200, 20));
        seVolume.setMaximumSize(new Dimension(200, 20));
        seVolume.setValue((int)(SoundPlayer.getSfxVolume() * 100)); // Set initial value
        seVolume.addChangeListener(e -> {
            float volume = seVolume.getValue() / 100.0f;
            SoundPlayer.setSfxVolume(volume);
            // Play sound when slider is released
            if (!seVolume.getValueIsAdjusting()) {
                SoundPlayer.playPieceSfx();
            }
        });

        // Slider labels
        JLabel bgLabel = new JLabel("BGM Volume");
        bgLabel.setPreferredSize(new Dimension(120, 20));

        JLabel seLabel = new JLabel("SFX Volume");
        seLabel.setPreferredSize(new Dimension(120, 20));

        // Group label + slider (horizontal panels)
        JPanel bgPanel = new JPanel();
        bgPanel.setLayout(new BoxLayout(bgPanel, BoxLayout.X_AXIS));
        bgPanel.add(bgLabel);
        bgPanel.add(Box.createHorizontalStrut(10));
        bgPanel.add(bgVolume);

        JPanel sePanel = new JPanel();
        sePanel.add(seLabel);
        sePanel.setLayout(new BoxLayout(sePanel, BoxLayout.X_AXIS));
        sePanel.add(Box.createHorizontalStrut(10));
        sePanel.add(seVolume);

        // Back button
        JButton backButton = SwingUtils.makeButton("Back", e -> backAction.run());

        // UI panels alignment
        add(Box.createVerticalStrut(50));
        add(dpPanel);
        add(Box.createVerticalStrut(20));
        add(bgPanel);
        add(sePanel);
        add(Box.createVerticalStrut(50));
        add(backButton);
        add(Box.createVerticalGlue());
    }

    @Override
    public void setVisible(boolean aFlag) {
        if (aFlag) {
            boolean isFullScreen = window.isFullScreenMode();
            if (isFullScreen) {
                fullscreenButton.setSelected(true);
            } else {
                windowedButton.setSelected(true);
            }
        }
        super.setVisible(aFlag);
    }

    public void setBackAction(Runnable backAction) {
        this.backAction = backAction != null ? backAction : () -> window.showCard("MAIN");
    }

    public void setDisplayModeControlsEnabled(boolean enabled) {
        windowedButton.setEnabled(enabled);
        fullscreenButton.setEnabled(enabled);
    }
}
