package com.travel.gui;

import com.travel.model.User;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Keeps the logged-in user and a real screen history for Back navigation.
 */
public final class TravelSession {
    private static User currentUser;
    private static final Deque<JFrame> SCREEN_HISTORY = new ArrayDeque<>();

    private TravelSession() {}

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static int getUserId() {
        return currentUser == null ? 0 : currentUser.getUserId();
    }

    public static boolean isAdmin() {
        return currentUser != null
                && "ADMIN".equalsIgnoreCase(currentUser.getRole());
    }

    public static boolean isCustomer() {
        return currentUser != null
                && "CUSTOMER".equalsIgnoreCase(currentUser.getRole());
    }

    public static void resetHistory() {
        SCREEN_HISTORY.clear();
    }

    public static void openScreen(JFrame current, JFrame next) {
        if (current != null) {
            SCREEN_HISTORY.push(current);
            current.setVisible(false);
        }
        next.setLocationRelativeTo(null);
        next.setVisible(true);
    }

    public static void openRoot(JFrame next) {
        resetHistory();
        next.setLocationRelativeTo(null);
        next.setVisible(true);
    }

    public static void goBack(JFrame current) {
        if (!SCREEN_HISTORY.isEmpty()) {
            JFrame previous = SCREEN_HISTORY.pop();
            if (current != null) {
                current.dispose();
            }
            previous.setLocationRelativeTo(null);
            previous.setVisible(true);
            return;
        }

        boolean leave = CustomMessageDialog.showConfirmation(
                current,
                "Leave Application",
                "Are you sure you want to leave the Travel & Tourism Management System?"
        );

        if (leave) {
            if (current != null) {
                current.dispose();
            }
            System.exit(0);
        }
    }

    public static void exitApplication(Component parent) {
        boolean leave = CustomMessageDialog.showConfirmation(
                parent,
                "Exit Application",
                "Are you sure you want to leave the Travel & Tourism Management System?"
        );

        if (leave) {
            System.exit(0);
        }
    }

    public static void logout(JFrame current) {
        boolean logout = CustomMessageDialog.showConfirmation(
                current,
                "Logout",
                "Are you sure you want to logout?"
        );

        if (logout) {
            resetHistory();
            setCurrentUser(null);
            if (current != null) {
                current.dispose();
            }
            new LoginFrame().setVisible(true);
        }
    }
}
