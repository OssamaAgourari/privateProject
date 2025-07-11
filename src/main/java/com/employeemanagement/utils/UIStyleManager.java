package com.employeemanagement.utils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class UIStyleManager {
    // Colors (New Palette)
    public static final Color PRIMARY_COLOR = new Color(67, 80, 95); // Dark Gray/Blue for text and headers
    public static final Color SECONDARY_COLOR = new Color(144, 164, 174); // Muted Blue-Gray for secondary elements
    public static final Color ACCENT_COLOR = new Color(102, 187, 106); // Muted Green for buttons
    public static final Color BACKGROUND_COLOR = new Color(240, 242, 245); // Very Light Gray background
    public static final Color TEXT_COLOR = new Color(47, 54, 64); // Very Dark Gray for main text
    public static final Color BUTTON_HOVER_COLOR = new Color(129, 201, 134); // Lighter Green on hover
    public static final Color BUTTON_PRESSED_COLOR = new Color(85, 139, 89); // Darker Green when pressed
    public static final Color TABLE_HEADER_COLOR = new Color(96, 125, 139); // Muted Blue for table headers
    public static final Color TABLE_ALTERNATE_ROW = new Color(230, 230, 230); // Slightly darker gray for alt rows
    public static final Color TABLE_SELECTION_COLOR = new Color(102, 187, 106, 70); // Semi-transparent muted green

    // Fonts
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 17); // Slightly larger title
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 13);

    // Borders
    public static final Border PANEL_BORDER = BorderFactory.createEmptyBorder(12, 12, 12, 12); // Slightly increased padding
    public static final Border COMPONENT_BORDER = BorderFactory.createEmptyBorder(6, 6, 6, 6); // Slightly increased padding

    public static void applyStyle() {
        // Set default colors
        UIManager.put("Panel.background", BACKGROUND_COLOR);
        UIManager.put("Panel.foreground", TEXT_COLOR);
        UIManager.put("Label.foreground", TEXT_COLOR);
        UIManager.put("Button.background", ACCENT_COLOR);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.font", BUTTON_FONT);
        UIManager.put("Label.font", NORMAL_FONT);
        UIManager.put("TextField.font", NORMAL_FONT);
        UIManager.put("TextArea.font", NORMAL_FONT);
        UIManager.put("ComboBox.font", NORMAL_FONT);
        UIManager.put("Table.font", NORMAL_FONT);
        UIManager.put("TableHeader.font", HEADER_FONT);
        UIManager.put("TabbedPane.font", NORMAL_FONT);
        UIManager.put("Menu.font", NORMAL_FONT);
        UIManager.put("MenuItem.font", NORMAL_FONT);
        UIManager.put("Title.font", TITLE_FONT); // Custom key for titles if used

        // Table styling
        UIManager.put("Table.gridColor", new Color(204, 204, 204)); // Lighter grid lines
        UIManager.put("Table.background", Color.WHITE);
        UIManager.put("TableHeader.background", TABLE_HEADER_COLOR);
        UIManager.put("TableHeader.foreground", PRIMARY_COLOR);
        UIManager.put("Table.selectionBackground", TABLE_SELECTION_COLOR);
        UIManager.put("Table.selectionForeground", TEXT_COLOR);
        UIManager.put("Table.rowHeight", 28); // Increased row height

        // Button styling
        UIManager.put("Button.select", BUTTON_HOVER_COLOR);
        UIManager.put("Button.focus", new Color(0, 0, 0, 0));
        UIManager.put("Button.border", BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR.darker()), // Darker border for buttons
            BorderFactory.createEmptyBorder(8, 18, 8, 18) // Increased button padding
        ));

        // Text field styling
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("TextField.border", BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(6, 6, 6, 6) // Increased padding
        ));

        // Combo box styling
        UIManager.put("ComboBox.background", Color.WHITE);
        UIManager.put("ComboBox.border", BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(6, 6, 6, 6) // Increased padding
        ));

        // Tabbed pane styling
        UIManager.put("TabbedPane.background", BACKGROUND_COLOR);
        UIManager.put("TabbedPane.selected", SECONDARY_COLOR);
        UIManager.put("TabbedPane.foreground", TEXT_COLOR);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(1, 1, 1, 1));
        UIManager.put("TabbedPane.tabsOverlapBorder", true);

        // Menu bar styling
        UIManager.put("MenuBar.background", BACKGROUND_COLOR);
        UIManager.put("Menu.foreground", TEXT_COLOR);
        UIManager.put("MenuItem.foreground", TEXT_COLOR);
        UIManager.put("Menu.selectionBackground", SECONDARY_COLOR);
        UIManager.put("MenuItem.selectionBackground", SECONDARY_COLOR);
        UIManager.put("Menu.selectionForeground", Color.WHITE);
        UIManager.put("MenuItem.selectionForeground", Color.WHITE);
    }

    public static void styleButton(JButton button) {
        button.setFont(BUTTON_FONT);
        button.setBackground(ACCENT_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR.darker()),
                BorderFactory.createEmptyBorder(8, 18, 8, 18)
        ));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_HOVER_COLOR);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(ACCENT_COLOR);
            }

            public void mousePressed(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_PRESSED_COLOR);
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_HOVER_COLOR);
            }
        });
    }

    public static void styleTable(JTable table) {
        table.setFont(NORMAL_FONT);
        table.setRowHeight(28);
        table.setShowGrid(true);
        table.setGridColor(new Color(204, 204, 204));
        table.setSelectionBackground(TABLE_SELECTION_COLOR);
        table.setSelectionForeground(TEXT_COLOR);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        
        // Style header
        table.getTableHeader().setFont(HEADER_FONT);
        table.getTableHeader().setBackground(TABLE_HEADER_COLOR);
        table.getTableHeader().setForeground(PRIMARY_COLOR);
        table.getTableHeader().setReorderingAllowed(false);
        
        // Set alternate row color using a custom renderer
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : TABLE_ALTERNATE_ROW);
                } else {
                    c.setBackground(table.getSelectionBackground());
                }
                return c;
            }
        });
    }

    public static void stylePanel(JPanel panel) {
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(PANEL_BORDER);
    }

    public static void styleLabel(JLabel label) {
        label.setFont(NORMAL_FONT);
        label.setForeground(TEXT_COLOR);
    }
    
     public static void styleTitleLabel(JLabel label) {
        label.setFont(TITLE_FONT);
        label.setForeground(PRIMARY_COLOR);
    }

    public static void styleTextField(JTextField textField) {
        textField.setFont(NORMAL_FONT);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));
    }

    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(NORMAL_FONT);
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));
    }
    
    public static void styleTextArea(JTextArea textArea) {
        textArea.setFont(NORMAL_FONT);
         textArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));
    }

    public static void styleMenuBar(JMenuBar menuBar) {
        menuBar.setBackground(BACKGROUND_COLOR);
    }

     public static void styleMenu(JMenu menu) {
        menu.setFont(NORMAL_FONT);
        menu.setForeground(TEXT_COLOR);
     }

    public static void styleMenuItem(JMenuItem menuItem) {
        menuItem.setFont(NORMAL_FONT);
        menuItem.setForeground(TEXT_COLOR);
    }

} 