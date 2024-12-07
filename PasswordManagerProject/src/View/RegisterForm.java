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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author David Botina
 */
public class RegisterForm extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton registerButton;
    private JButton showPasswordButton; // Botón para mostrar la contraseña
    private boolean showPassword = false; // Estado de la visibilidad de la contraseña
    private UserForm parentFrame;

    public RegisterForm(UserForm parentFrame) {
        this.parentFrame = parentFrame;
        setTitle("CREAR USUARIO");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parentFrame);

        JPanel panel = new JPanel();
        add(panel);
        placeComponents(panel);

        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "TODOS LOS CAMPOS SON REQUERIDOS", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (registerUser(username, password)) {
                    JOptionPane.showMessageDialog(null, "Se ha creado el usuario correctamente");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "El usuario ya existe. Intenta nuevamente ");
                }
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

        registerButton = new JButton("ACEPTAR");
        registerButton.setBounds(100, 90, 164, 25);
        panel.add(registerButton);
    }

    private boolean registerUser(String username, String password) {
        try (Connection connection = ConnectionDB.getConnection()) {
            String query = "INSERT INTO users (username, masterPassword) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            EncryptionService encryptionService = new EncryptionService(); // Instancia del servicio de encriptación
            String encryptedPassword = encryptionService.encrypt(password); // Encriptar la contraseña

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, encryptedPassword); // Almacenar la contraseña encriptada
            preparedStatement.executeUpdate();
            return true;
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
}
