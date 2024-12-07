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