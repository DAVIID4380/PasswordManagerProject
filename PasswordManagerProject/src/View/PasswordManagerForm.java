/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package View;

import Logic.ConnectionDB;
import Logic.EncryptionService;
import Logic.PasswordEntry;
import Logic.PasswordTableModel;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 *
 * @author David Botina
 */

public class PasswordManagerForm extends JFrame {

    private JTable passwordTable;
    private PasswordTableModel tableModel;
    private JButton addButton;
    private JButton deleteButton;
    private JButton showPasswordButton;
    private JTextField searchField;
    private JPopupMenu popupMenu;
    private List<PasswordEntry> passwordEntries;
    private int userId;

    public PasswordManagerForm(int userId) {
        this.userId = userId;
        setTitle("GESTOR DE CONTRASEÑAS");
        setSize(1000, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        passwordEntries = new ArrayList<>();
        tableModel = new PasswordTableModel(passwordEntries);

        passwordTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(passwordTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        addButton = new JButton("INSERTAR");
        deleteButton = new JButton("ELIMINAR TODO");
        showPasswordButton = new JButton("MOSTRAR CONTRASEÑA");
        showPasswordButton.setEnabled(false);
        searchField = new JTextField(20);

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(new JLabel("BUSCAR:"));
        buttonPanel.add(searchField);
        buttonPanel.add(showPasswordButton);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new AddPasswordForm(PasswordManagerForm.this, userId, tableModel).setVisible(true);
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteAllPasswords();
            }
        });

        showPasswordButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showSelectedPassword();
            }
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                searchPasswords(searchField.getText());
            }

            public void removeUpdate(DocumentEvent e) {
                searchPasswords(searchField.getText());
            }

            public void changedUpdate(DocumentEvent e) {
                searchPasswords(searchField.getText());
            }
        });

        passwordTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && passwordTable.getSelectedRow() != -1) {
                    showPasswordButton.setEnabled(true);
                } else {
                    showPasswordButton.setEnabled(false);
                }
            }
        });

        loadPasswords();
        createPopupMenu();
    }

    private void loadPasswords() {
        try (Connection connection = ConnectionDB.getConnection()) {
            String query = "SELECT * FROM passwordentries WHERE userId = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String username = resultSet.getString("username");
                String encryptedPassword = resultSet.getString("encryptedPassword");
                String url = resultSet.getString("url");
                String notes = resultSet.getString("notes");
                passwordEntries.add(new PasswordEntry(id, userId, title, username, encryptedPassword, url, notes));
            }

            tableModel.fireTableDataChanged();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteAllPasswords() {
        try (Connection connection = ConnectionDB.getConnection()) {
            String query = "DELETE FROM passwordentries WHERE userId = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            preparedStatement.executeUpdate();

            passwordEntries.clear();
            tableModel.fireTableDataChanged();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchPasswords(String keyword) {
        passwordEntries.clear();
        try (Connection connection = ConnectionDB.getConnection()) {
            String query = "SELECT * FROM passwordentries WHERE userId = ? AND (title LIKE ? OR username LIKE ? OR url LIKE ? OR notes LIKE ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            String searchPattern = "%" + keyword + "%";
            preparedStatement.setString(2, searchPattern);
            preparedStatement.setString(3, searchPattern);
            preparedStatement.setString(4, searchPattern);
            preparedStatement.setString(5, searchPattern);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String username = resultSet.getString("username");
                String encryptedPassword = resultSet.getString("encryptedPassword");
                String url = resultSet.getString("url");
                String notes = resultSet.getString("notes");
                passwordEntries.add(new PasswordEntry(id, userId, title, username, encryptedPassword, url, notes));
            }

            tableModel.fireTableDataChanged();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showSelectedPassword() {
        int selectedRow = passwordTable.getSelectedRow();
        if (selectedRow >= 0) {
            PasswordEntry entry = passwordEntries.get(selectedRow);
            try {
                // Instanciar el servicio de encriptación con la clave fija de 16 bytes
                EncryptionService encryptionService = new EncryptionService();
                String decryptedPassword = encryptionService.decrypt(entry.getEncryptedPassword());

                JOptionPane.showMessageDialog(this, "Password: " + decryptedPassword, "Decrypted Password", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error decrypting password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deletePassword(int id) {
        try (Connection connection = ConnectionDB.getConnection()) {
            String query = "DELETE FROM passwordentries WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createPopupMenu() {
        popupMenu = new JPopupMenu();

        JMenuItem editItem = new JMenuItem("EDITAR");
        editItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = passwordTable.getSelectedRow();
                if (selectedRow >= 0) {
                    PasswordEntry entry = passwordEntries.get(selectedRow);
                    new EditPasswordForm(PasswordManagerForm.this, entry, tableModel).setVisible(true);
                }
            }
        });
        popupMenu.add(editItem);

        JMenuItem deleteItem = new JMenuItem("ELIMINAR");
        deleteItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = passwordTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int id = passwordEntries.get(selectedRow).getId();
                    deletePassword(id);
                    tableModel.removeRow(selectedRow);
                }
            }
        });
        popupMenu.add(deleteItem);

        passwordTable.setComponentPopupMenu(popupMenu);

        passwordTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = passwordTable.rowAtPoint(e.getPoint());
                passwordTable.getSelectionModel().setSelectionInterval(row, row);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = passwordTable.rowAtPoint(e.getPoint());
                    passwordTable.getSelectionModel().setSelectionInterval(row, row);
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new UserForm().setVisible(true);
            }
        });
    }
}