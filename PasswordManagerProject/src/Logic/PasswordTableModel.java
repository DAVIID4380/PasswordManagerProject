/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logic;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 *
 * @author David Botina
 */
public class PasswordTableModel extends AbstractTableModel {

    private List<PasswordEntry> passwordEntries;
    private String[] columnNames = {"ID", "SITIO WEB", "USUARIO", "CONTRASEÑA", "URL", "DESCRIPCIÓN"};
    private boolean showPasswords = false; // Bandera para mostrar u ocultar contraseñas 

    public PasswordTableModel(List<PasswordEntry> passwordEntries) {
        this.passwordEntries = passwordEntries;
    }

    public void setShowPasswords(boolean showPasswords) {
        this.showPasswords = showPasswords;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return passwordEntries.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PasswordEntry entry = passwordEntries.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return rowIndex + 1; // Numerador visual 
            case 1:
                return entry.getTitle();
            case 2:
                return entry.getUsername();
            case 3:
                return showPasswords ? entry.getEncryptedPassword() : "********"; // Mostrar contraseña encriptada u oculta 
            case 4:
                return entry.getUrl();
            case 5:
                return entry.getNotes();
            default:
                return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    public void addRow(PasswordEntry entry) {
        passwordEntries.add(entry);
        fireTableRowsInserted(passwordEntries.size() - 1, passwordEntries.size() - 1);
    }

    public void removeRow(int rowIndex) {
        passwordEntries.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }
}
