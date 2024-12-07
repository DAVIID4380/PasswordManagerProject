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
import java.sql.Statement;
import java.sql.ResultSet;

/**
 *
 * @author David Botina
 */
public class AddPasswordForm extends JDialog {

    private JTextField titleField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField urlField;
    private JTextArea notesArea;
    private JButton addButton;
    private JButton cancelButton;
    private JButton showPasswordButton; // Botón para mostrar la contraseña
    private boolean showPassword = false; // Estado de la visibilidad de la contraseña
    private int userId;
    private PasswordTableModel tableModel;
    private EncryptionService encryptionService;

    public AddPasswordForm(JFrame parentFrame, int userId, PasswordTableModel tableModel) {
        super(parentFrame, "AÑADIR CONTRASEÑAS", true);
        this.userId = userId;
        this.tableModel = tableModel;
        try {
            this.encryptionService = new EncryptionService(); // Instanciar EncryptionService y manejar excepciones
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error initializing encryption service.", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        setSize(400, 400);
        setLocationRelativeTo(parentFrame);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        add(panel);

        JLabel titleLabel = new JLabel("Sitio Web");
        titleLabel.setBounds(10, 20, 80, 25);
        panel.add(titleLabel);

        titleField = new JTextField();
        titleField.setBounds(100, 20, 165, 25);
        panel.add(titleField);

        JLabel usernameLabel = new JLabel("Usuario");
        usernameLabel.setBounds(10, 50, 80, 25);
        panel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(100, 50, 165, 25);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Contraseña");
        passwordLabel.setBounds(10, 80, 80, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(100, 80, 165, 25);
        panel.add(passwordField);

        showPasswordButton = new JButton("MOSTRAR");
        showPasswordButton.setBounds(270, 80, 100, 25);
        panel.add(showPasswordButton);

        JLabel urlLabel = new JLabel("URL");
        urlLabel.setBounds(10, 110, 80, 25);
        panel.add(urlLabel);

        urlField = new JTextField();
        urlField.setBounds(100, 110, 165, 25);
        panel.add(urlField);

        JLabel notesLabel = new JLabel("Descripción");
        notesLabel.setBounds(10, 140, 80, 25);
        panel.add(notesLabel);

        notesArea = new JTextArea();
        notesArea.setBounds(100, 140, 165, 75);
        panel.add(notesArea);

        addButton = new JButton("ACEPTAR");
        addButton.setBounds(70, 230, 100, 25);
        panel.add(addButton);

        cancelButton = new JButton("CANCELAR");
        cancelButton.setBounds(180, 230, 100, 25);
        panel.add(cancelButton);

        showPasswordButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                togglePasswordVisibility();
            }
        });

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addPassword();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void addPassword() {
        String title = titleField.getText();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String url = urlField.getText();
        String notes = notesArea.getText();

        // Validar si los campos están vacíos
        if (title.isEmpty() || username.isEmpty() || password.isEmpty() || url.isEmpty() || notes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "TODOS LOS CAMPOS SON REQUERIDOS.", "Error", JOptionPane.ERROR_MESSAGE);
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

        try (Connection connection = Logic.ConnectionDB.getConnection()) {
            String query = "INSERT INTO passwordentries (userId, title, username, encryptedPassword, url, notes) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, title);
            preparedStatement.setString(3, username);
            preparedStatement.setString(4, encryptedPassword); // Guardar la contraseña encriptada
            preparedStatement.setString(5, url);
            preparedStatement.setString(6, notes);
            preparedStatement.executeUpdate();

            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                tableModel.addRow(new PasswordEntry(id, userId, title, username, encryptedPassword, url, notes));
            }
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
