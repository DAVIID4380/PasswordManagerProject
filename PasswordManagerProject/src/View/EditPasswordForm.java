/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package View;

import Logic.ConnectionDB;
import Logic.EncryptionService;
import Logic.PasswordEntry;
import Logic.PasswordTableModel;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author David Botina
 */
public class EditPasswordForm extends JDialog {

    private JTextField titleField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField urlField;
    private JTextArea notesArea;
    private JButton saveButton;
    private JButton cancelButton;
    private PasswordEntry entry;
    private PasswordTableModel tableModel;
    private EncryptionService encryptionService;
    private JButton showPasswordButton; // Botón para mostrar la contraseña
    private boolean showPassword = false; // Estado de la visibilidad de la contraseña

    public EditPasswordForm(JFrame parentFrame, PasswordEntry entry, PasswordTableModel tableModel) {
        super(parentFrame, "EDITAR REGISTRO", true);
        this.entry = entry;
        this.tableModel = tableModel;
        try {
            this.encryptionService = new EncryptionService(); // Instanciar EncryptionService y manejar excepciones
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error initializing encryption service.", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        setSize(400, 300);
        setLocationRelativeTo(parentFrame);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        add(panel);

        JLabel titleLabel = new JLabel("Sitio Web");
        titleLabel.setBounds(10, 20, 80, 25);
        panel.add(titleLabel);

        titleField = new JTextField(entry.getTitle());
        titleField.setBounds(100, 20, 165, 25);
        panel.add(titleField);

        JLabel usernameLabel = new JLabel("Usuario");
        usernameLabel.setBounds(10, 50, 80, 25);
        panel.add(usernameLabel);

        usernameField = new JTextField(entry.getUsername());
        usernameField.setBounds(100, 50, 165, 25);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Contraseña");
        passwordLabel.setBounds(10, 80, 80, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField(encryptionService.decrypt(entry.getEncryptedPassword())); // Desencriptar para mostrar
        passwordField.setBounds(100, 80, 165, 25);
        panel.add(passwordField);

        showPasswordButton = new JButton("MOSTRAR");
        showPasswordButton.setBounds(270, 80, 100, 25);
        panel.add(showPasswordButton);

        JLabel urlLabel = new JLabel("URL");
        urlLabel.setBounds(10, 110, 80, 25);
        panel.add(urlLabel);

        urlField = new JTextField(entry.getUrl());
        urlField.setBounds(100, 110, 165, 25);
        panel.add(urlField);

        JLabel notesLabel = new JLabel("Descripción");
        notesLabel.setBounds(10, 140, 80, 25);
        panel.add(notesLabel);

        notesArea = new JTextArea(entry.getNotes());
        notesArea.setBounds(100, 140, 165, 75);
        panel.add(notesArea);

        saveButton = new JButton("ACEPTAR");
        saveButton.setBounds(70, 230, 100, 25);
        panel.add(saveButton);

        cancelButton = new JButton("CANCELAR");
        cancelButton.setBounds(200, 230, 100, 25);
        panel.add(cancelButton);

        showPasswordButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                togglePasswordVisibility();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                savePassword();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void savePassword() {
        String title = titleField.getText();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String url = urlField.getText();
        String notes = notesArea.getText(); // Validar si los campos están vacíos 
        if (title.isEmpty() || username.isEmpty() || password.isEmpty() || url.isEmpty() || notes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "TODOS LOS CAMPOS SON REQUERIDOS", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String encryptedPassword;
        try {
            encryptedPassword = encryptionService.encrypt(password); // Encriptar antes de guardar 
        } catch (RuntimeException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error encrypting password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        entry.setTitle(title);
        entry.setUsername(username);
        entry.setEncryptedPassword(encryptedPassword);
        entry.setUrl(url);
        entry.setNotes(notes);
        try (Connection connection = Logic.ConnectionDB.getConnection()) {
            String query = "UPDATE passwordentries SET title = ?, username = ?, encryptedPassword = ?, url = ?, notes = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, encryptedPassword); // Guardar la contraseña encriptada 
            preparedStatement.setString(4, url);
            preparedStatement.setString(5, notes);
            preparedStatement.setInt(6, entry.getId());
            preparedStatement.executeUpdate();
            tableModel.fireTableDataChanged();
            dispose();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void togglePasswordVisibility() {
        if (showPassword) {
            passwordField.setEchoChar('*'); // Ocultar contraseña
            showPasswordButton.setText("MOSTRAR");
        } else {
            passwordField.setEchoChar((char) 0); // Mostrar contraseña
            showPasswordButton.setText("OCULTAR");
        }
        showPassword = !showPassword;
    }
}
