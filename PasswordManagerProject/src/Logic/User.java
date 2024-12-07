/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logic;

/**
 *
 * @author David Botina
 */
public class User {

    private int id;
    private String username;
    private String masterPassword; 

    // Constructor, getters y setters 
    public User(int id, String username, String masterPassword) { 
        this.id = id;
        this.username = username;
        this.masterPassword = masterPassword; 
    } 
    
    
    // Métodos para autenticar y cambiar la contraseña maestra 
    public boolean authenticate(String password) { 
        return this.masterPassword.equals(password); 
    }
    
    public void changeMasterPassword(String newPassword) {
        this.masterPassword = newPassword; 
    } 
    
    
    // Getters y setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMasterPassword() {
        return masterPassword;
    }

    public void setMasterPassword(String masterPassword) {
        this.masterPassword = masterPassword;
    }
    
    
}
