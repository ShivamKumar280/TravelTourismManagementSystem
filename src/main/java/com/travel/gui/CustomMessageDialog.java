package com.travel.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class CustomMessageDialog extends JDialog {

    private CustomMessageDialog(
            Window parent,
            String title,
            String message,
            String type) {

        super(parent, title, ModalityType.APPLICATION_MODAL);

        setSize(430, 240);
        setLocationRelativeTo(parent);
        setResizable(false);
        setUndecorated(true);

        Color mainColor;
        Color lightColor;
        String iconText;

        switch (type) {

            case "SUCCESS":
                mainColor = new Color(34, 139, 94);
                lightColor = new Color(232, 248, 240);
                iconText = "OK";
                break;

            case "WARNING":
                mainColor = new Color(220, 150, 30);
                lightColor = new Color(255, 248, 230);
                iconText = "!";
                break;

            case "ERROR":
                mainColor = new Color(210, 65, 65);
                lightColor = new Color(255, 238, 238);
                iconText = "X";
                break;

            default:
                mainColor = new Color(45, 105, 160);
                lightColor = new Color(235, 245, 255);
                iconText = "i";
                break;
        }

        JPanel mainPanel = new RoundedPanel(20);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(mainColor, 2),
                new EmptyBorder(0, 0, 13, 0)
        ));

        // ==========================================
        // CONTENT PANEL
        // ==========================================

        JPanel contentPanel = new JPanel(new BorderLayout(15, 10));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(
                new EmptyBorder(25, 25, 10, 25)
        );

        // ==========================================
        // ICON
        // ==========================================

        JLabel iconLabel = new JLabel(iconText);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);

        iconLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        30
                )
        );

        iconLabel.setForeground(mainColor);

        JPanel iconPanel = new JPanel(new GridBagLayout());
        iconPanel.setPreferredSize(
                new Dimension(65, 65)
        );

        iconPanel.setBackground(lightColor);

        iconPanel.setBorder(
                BorderFactory.createLineBorder(
                        mainColor,
                        2
                )
        );

        iconPanel.add(iconLabel);

        contentPanel.add(
                iconPanel,
                BorderLayout.WEST
        );

        // ==========================================
        // TEXT
        // ==========================================

        JPanel textPanel = new JPanel();
        textPanel.setLayout(
                new BoxLayout(
                        textPanel,
                        BoxLayout.Y_AXIS
                )
        );

        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);

        titleLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        21
                )
        );

        titleLabel.setForeground(
                new Color(35, 35, 35)
        );

        titleLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        textPanel.add(titleLabel);

        textPanel.add(
                Box.createVerticalStrut(8)
        );

        JLabel messageLabel = new JLabel(
                "<html><div style='width:270px;'>"
                        + message.replace(
                                "\n",
                                "<br>"
                        )
                        + "</div></html>"
        );

        messageLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        messageLabel.setForeground(
                new Color(85, 85, 85)
        );

        messageLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        textPanel.add(messageLabel);

        contentPanel.add(
                textPanel,
                BorderLayout.CENTER
        );

        mainPanel.add(
                contentPanel,
                BorderLayout.CENTER
        );

        // ==========================================
        // BUTTON
        // ==========================================

        JPanel buttonPanel = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        25,
                        0
                )
        );

        buttonPanel.setOpaque(false);

        JButton okButton = new JButton("OK");

        okButton.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        okButton.setForeground(Color.WHITE);
        okButton.setBackground(mainColor);

        okButton.setFocusPainted(false);
        okButton.setBorderPainted(false);

        okButton.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        okButton.setPreferredSize(
                new Dimension(90, 38)
        );

        okButton.addActionListener(
                e -> dispose()
        );

        buttonPanel.add(okButton);

        mainPanel.add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        setContentPane(mainPanel);

        // ==========================================
        // ENTER KEY
        // ==========================================

        getRootPane().setDefaultButton(
                okButton
        );
    }

    // =====================================================
    // SUCCESS
    // =====================================================

    public static void showSuccess(
            Component parent,
            String title,
            String message) {

        showMessage(
                parent,
                title,
                message,
                "SUCCESS"
        );
    }

    // =====================================================
    // INFORMATION
    // =====================================================

    public static void showInfo(
            Component parent,
            String title,
            String message) {

        showMessage(
                parent,
                title,
                message,
                "INFO"
        );
    }

    // =====================================================
    // WARNING
    // =====================================================

    public static void showWarning(
            Component parent,
            String title,
            String message) {

        showMessage(
                parent,
                title,
                message,
                "WARNING"
        );
    }

    // =====================================================
    // ERROR
    // =====================================================

    public static void showError(
            Component parent,
            String title,
            String message) {

        showMessage(
                parent,
                title,
                message,
                "ERROR"
        );
    }

    // =====================================================
    // COMMON METHOD
    // =====================================================

    private static void showMessage(
            Component parent,
            String title,
            String message,
            String type) {

        Window window = null;

        if (parent != null) {
            window =
                    SwingUtilities.getWindowAncestor(
                            parent
                    );
        }

        CustomMessageDialog dialog =
                new CustomMessageDialog(
                        window,
                        title,
                        message,
                        type
                );

        dialog.setVisible(true);
    }

    // =====================================================
    // CONFIRMATION
    // =====================================================

    public static boolean showConfirmation(
            Component parent,
            String title,
            String message) {

        Window window = null;
        if (parent != null) {
            window = SwingUtilities.getWindowAncestor(parent);
        }

        final boolean[] confirmed = {false};

        JDialog dialog = new JDialog(
                window,
                title,
                Dialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setSize(450, 245);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);
        dialog.setUndecorated(true);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 150, 30), 2),
                new EmptyBorder(22, 25, 18, 25)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(45, 45, 45));

        JLabel messageLabel = new JLabel(
                "<html><div style='width:350px;'>" +
                        message.replace("\n", "<br>") +
                        "</div></html>"
        );
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(new Color(85, 85, 85));

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(titleLabel);
        center.add(Box.createVerticalStrut(12));
        center.add(messageLabel);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);

        JButton noButton = new JButton("No");
        JButton yesButton = new JButton("Yes");

        for (JButton button : new JButton[]{noButton, yesButton}) {
            button.setFont(new Font("Segoe UI", Font.BOLD, 13));
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setPreferredSize(new Dimension(85, 36));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        noButton.setBackground(new Color(115, 125, 135));
        noButton.setForeground(Color.WHITE);
        yesButton.setBackground(new Color(210, 65, 65));
        yesButton.setForeground(Color.WHITE);

        noButton.addActionListener(e -> dialog.dispose());
        yesButton.addActionListener(e -> {
            confirmed[0] = true;
            dialog.dispose();
        });

        buttons.add(noButton);
        buttons.add(yesButton);

        panel.add(center, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        dialog.setContentPane(panel);
        dialog.getRootPane().setDefaultButton(noButton);
        dialog.setVisible(true);

        return confirmed[0];
    }

    // =====================================================
    // ROUNDED PANEL
    // =====================================================

    private static class RoundedPanel
            extends JPanel {

        private final int radius;

        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(
                Graphics g) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(getBackground());

            g2.fill(
                    new RoundRectangle2D.Double(
                            0,
                            0,
                            getWidth(),
                            getHeight(),
                            radius,
                            radius
                    )
            );

            g2.dispose();

            super.paintComponent(g);
        }
    }
}