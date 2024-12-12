package com.example;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;

class CustomScrollBarUI extends BasicScrollBarUI {
    @Override
    protected void configureScrollBarColors() {
        this.thumbColor = new Color(50, 50, 50); // Thumb (scroll indicator) - Dark gray
        this.thumbDarkShadowColor = new Color(30, 30, 30); // Thumb dark shadow - Blackish gray
        this.thumbHighlightColor = new Color(80, 80, 80); // Thumb highlight - Lighter gray
        this.thumbLightShadowColor = new Color(70, 70, 70); // Thumb light shadow - Medium gray
        this.trackColor = new Color(20, 20, 20); // Scrollbar track - Almost black
        this.trackHighlightColor = new Color(100, 100, 100); // Highlight on the track - Light gray

    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton(); // No default buttons
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton(); // No default buttons
    }

    private JButton createZeroButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        button.setVisible(false);
        return button;
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(trackColor);
        g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(thumbColor);
        g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 5, 5);
    }
}
