package com.travel.gui;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/** Reusable scenic background for the Travel & Tourism GUI. */
public class TravelBackgroundPanel extends JPanel {
    private final BufferedImage image;
    private final Color overlay;

    public TravelBackgroundPanel(String resourceName) {
        setOpaque(false);
        this.image = loadImage(resourceName);
        this.overlay = new Color(8, 24, 40, 38);
    }

    private BufferedImage loadImage(String resourceName) {
        String path = "/images/" + resourceName;
        try (InputStream in = TravelBackgroundPanel.class.getResourceAsStream(path)) {
            if (in == null) {
                return null;
            }
            return ImageIO.read(in);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);

            if (image != null && getWidth() > 0 && getHeight() > 0) {
                // The supplied 15 background images are the visual source of truth.
                // Stretch them to the window so the complete scenic composition remains visible.
                g2.drawImage(image, 0, 0, getWidth(), getHeight(), null);
            } else {
                g2.setColor(new Color(25, 65, 95));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }

            g2.setColor(overlay);
            g2.fillRect(0, 0, getWidth(), getHeight());
        } finally {
            g2.dispose();
        }
    }
}
