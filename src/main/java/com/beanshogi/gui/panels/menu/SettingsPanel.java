package com.beanshogi.gui.panels.menu;

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

    public SettingsPanel(ShogiWindow window) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Aspect ratio setting radio buttons
        JRadioButton windowedButton = new JRadioButton("Windowed");
        windowedButton.addActionListener(e -> window.setSmallWindow());
        
        // Windowed by default
        windowedButton.setSelected(true);
        
        JRadioButton fullscreenButton = new JRadioButton("Full screen");
        fullscreenButton.addActionListener(e -> window.setFullscreenWindow());

        ButtonGroup aButtonGroup = new ButtonGroup();
        aButtonGroup.add(windowedButton);
        aButtonGroup.add(fullscreenButton);

        JLabel arLabel = new JLabel("Display mode");
        arLabel.setPreferredSize(new Dimension(120, 20));

        JPanel arPanel = new JPanel();
        arPanel.setLayout(new BoxLayout(arPanel, BoxLayout.X_AXIS));
        arPanel.add(arLabel);
        arPanel.add(Box.createHorizontalStrut(30));
        arPanel.add(windowedButton);
        arPanel.add(fullscreenButton);

        // Volume sliders
        JSlider bgVolume = new JSlider(0, 100);
        bgVolume.setPreferredSize(new Dimension(200, 20));
        bgVolume.setMaximumSize(new Dimension(200, 20));

        JSlider seVolume = new JSlider(0, 100);
        seVolume.setPreferredSize(new Dimension(200, 20));
        seVolume.setMaximumSize(new Dimension(200, 20));

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
        JButton backButton = SwingUtils.makeButton("Back", e -> window.showCard("MAIN"));

        // UI panels alignment
        add(Box.createVerticalStrut(50));
        add(arPanel);
        add(Box.createVerticalStrut(20));
        add(bgPanel);
        add(sePanel);
        add(Box.createVerticalStrut(50));
        add(backButton);
        add(Box.createVerticalGlue());
    }
}
