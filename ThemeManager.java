import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
import java.awt.*;

public class ThemeManager {

    // COLOR PALETTE
    public static final Color BG_DARK       = new Color(18, 18, 24);
    public static final Color BG_PANEL      = new Color(28, 28, 36);
    public static final Color BG_CARD       = new Color(38, 38, 50);
    public static final Color ACCENT        = new Color(99, 102, 241);
    public static final Color ACCENT_HOVER  = new Color(129, 132, 255);
    public static final Color TEXT_PRIMARY  = new Color(240, 240, 255);
    public static final Color TEXT_MUTED    = new Color(140, 140, 160);
    public static final Color BORDER_COLOR  = new Color(55, 55, 75);
    public static final Color SLIDER_TRACK  = new Color(55, 55, 75);
    public static final Color SUCCESS       = new Color(34, 197, 94);
    public static final Color DANGER        = new Color(239, 68, 68);

    public static final Font FONT_REGULAR   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD      = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL     = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_TITLE     = new Font("Segoe UI", Font.BOLD, 15);

    public static void applyTheme() {
        // GLOBAL DEFAULTS
        UIManager.put("Panel.background",           BG_DARK);
        UIManager.put("Frame.background",           BG_DARK);
        UIManager.put("ScrollPane.background",      BG_DARK);
        UIManager.put("Viewport.background",        BG_DARK);
        UIManager.put("Label.foreground",           TEXT_PRIMARY);
        UIManager.put("Label.font",                 FONT_REGULAR);
        UIManager.put("Button.font",                FONT_BOLD);
        UIManager.put("Slider.font",                FONT_SMALL);
        UIManager.put("OptionPane.background",      BG_PANEL);
        UIManager.put("OptionPane.messageForeground",TEXT_PRIMARY);
        UIManager.put("OptionPane.messageFont",     FONT_REGULAR);
        UIManager.put("OptionPane.buttonFont",      FONT_BOLD);
        UIManager.put("FileChooser.background",     BG_PANEL);
        UIManager.put("ScrollBar.background",       BG_DARK);
        UIManager.put("ScrollBar.thumb",            BORDER_COLOR);
        UIManager.put("ScrollBar.track",            BG_DARK);
    }

    // STYLE A BUTTON
    public static void styleButton(JButton btn, boolean isPrimary) {
        btn.setFont(FONT_BOLD);
        btn.setForeground(TEXT_PRIMARY);
        btn.setBackground(isPrimary ? ACCENT : BG_CARD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(isPrimary ? ACCENT : BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            Color original = btn.getBackground();
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(isPrimary ? ACCENT_HOVER : BG_DARK);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(original);
            }
        });
    }

    // STYLE A SLIDER
    public static void styleSlider(JSlider slider) {
        slider.setBackground(BG_PANEL);
        slider.setForeground(ACCENT);
        slider.setFont(FONT_SMALL);
        slider.setPaintTicks(true);
        slider.setPaintLabels(false);
        slider.setMajorTickSpacing(50);
        slider.setMinorTickSpacing(10);
        slider.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
    }

    // STYLE A LABEL
    public static void styleLabel(JLabel label, boolean isMuted) {
        label.setFont(isMuted ? FONT_SMALL : FONT_REGULAR);
        label.setForeground(isMuted ? TEXT_MUTED : TEXT_PRIMARY);
        label.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
    }

    // STYLE A PANEL
    public static void stylePanel(JPanel panel, boolean isCard) {
        panel.setBackground(isCard ? BG_CARD : BG_PANEL);
        panel.setBorder(isCard
                ? BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(8, 8, 8, 8))
                : BorderFactory.createEmptyBorder(6, 6, 6, 6)
        );
    }

    // STYLE SCROLLPANE
    public static void styleScrollPane(JScrollPane sp) {
        sp.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        sp.getViewport().setBackground(BG_DARK);
        sp.setBackground(BG_DARK);
    }
}
