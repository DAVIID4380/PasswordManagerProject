/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logic;

/**
 *
 * @author David Botina
 */
public class PasswordEntry {

    private int id;
    private int userId;
    private String title;
    private String username;
    private String encryptedPassword;
    private String url;
    private String notes;

    
    // Constructor 
    public PasswordEntry(int id, int userId, String title, String username, String encryptedPassword, String url, String notes) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.url = url;
        this.notes = notes;
    }

    
    // Métodos para encriptar y desencriptar 
    public void encrypt(EncryptionService encryptionService) {
        this.encryptedPassword = encryptionService.encrypt(this.encryptedPassword);
    }

    public String decrypt(EncryptionService encryptionService) {
        return encryptionService.decrypt(this.encryptedPassword);
    }
    
    
    //Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    
}
