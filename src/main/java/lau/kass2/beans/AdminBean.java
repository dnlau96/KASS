package lau.kass2.beans;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import lau.kass2.models.User; 
import lau.kass2.services.UserService;

@Named("adminBean")
@SessionScoped
public class AdminBean implements Serializable {

    @Inject
    private UserService userService;

    private String username;
    private String password;
    private User currentUser; 

    public String login() {
       
        this.currentUser = userService.validateCredentialsAndGetUser(username, password);
        
        if (this.currentUser != null) {
            return "/admin/dashboard?faces-redirect=true";
        } else {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Access Denied", "Incorrect username or password."));
            return null; 
        }
    }
    
 public String logout() {
      
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        
    
        return "/index.xhtml?faces-redirect=true";
    }

    // Método para saber si el usuario está logueado
    public boolean isLoggedIn() {
        return this.currentUser != null;
    }
    
    

    // --- Getters & Setters ---
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public User getCurrentUser() { return currentUser; } // Getter para el usuario actual
}