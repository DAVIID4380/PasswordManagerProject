/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package View;

import Logic.ConnectionDB;
import Logic.EncryptionService;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.*;

/**
 *
 * @author David Botina
 */

public class UserForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton showPasswordButton; // Botón para mostrar la contraseña
    private boolean showPassword = false; // Estado de la visibilidad de la contraseña

    public UserForm() {
        setTitle("INICIO DE SESIÓN");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        add(panel);
        placeComponents(panel);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (authenticate(username, password)) {
                    JOptionPane.showMessageDialog(null, "Inicio de sesión satisfactorio!");
                    new PasswordManagerForm(getUserId(username)).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Usuario y/o Contraseña incorrectos");
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new RegisterForm(UserForm.this).setVisible(true);
            }
        });

        showPasswordButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                togglePasswordVisibility();
            }
        });
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel userLabel = new JLabel("Usuario");
        userLabel.setBounds(10, 20, 80, 25);
        panel.add(userLabel);

        usernameField = new JTextField(20);
        usernameField.setBounds(100, 20, 165, 25);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Contraseña");
        passwordLabel.setBounds(10, 50, 80, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setBounds(100, 50, 165, 25);
        panel.add(passwordField);

        showPasswordButton = new JButton("MOSTRAR");
        showPasswordButton.setBounds(270, 50, 100, 25);
        panel.add(showPasswordButton);

        loginButton = new JButton("INICIAR SESIÓN");
        loginButton.setBounds(100, 90, 164, 25);
        panel.add(loginButton);

        registerButton = new JButton("CREAR USUARIO");
        registerButton.setBounds(100, 120, 164, 25);
        panel.add(registerButton);
    }

    private boolean authenticate(String username, String password) {
        try (Connection connection = ConnectionDB.getConnection()) {
            String query = "SELECT masterPassword FROM users WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String encryptedPassword = resultSet.getString("masterPassword");

                EncryptionService encryptionService = new EncryptionService(); // Instancia del servicio de encriptación
                String decryptedPassword = encryptionService.decrypt(encryptedPassword); // Desencriptar la contraseña almacenada

                return decryptedPassword.equals(password); // Verificar la contraseña
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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

    private int getUserId(String username) {
        try (Connection connection = ConnectionDB.getConnection()) {
            String query = "SELECT id FROM users WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Invalid ID if not found
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new UserForm().setVisible(true);
            }
        });
    }
}

