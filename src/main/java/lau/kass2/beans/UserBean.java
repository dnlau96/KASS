/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lau.kass2.beans;

/**
 *
 * @author dnlau
 */
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import lau.kass2.models.User;
import lau.kass2.services.UserService;
import org.primefaces.PrimeFaces;
import lau.kass2.models.Role; 
import lau.kass2.services.RoleService;

@Named("userBean")
@ViewScoped
public class UserBean implements Serializable {

    @Inject
    private UserService userService;
    
    @Inject
    private RoleService roleService;

    private List<User> userList;
    private User selectedUser;
    
   
    
    private List<Role> availableRoles; 

    @PostConstruct
    public void init() {
        this.userList = userService.findAll();
        this.availableRoles = roleService.findAll(); 
    }
    
    // Campo para la contraseña al crear/editar
    private String newPassword;

   
    public void openNew() {
        this.selectedUser = new User();
        this.newPassword = null; // Limpia el campo de contraseña
    }

    public void saveUser() {
        try {
            userService.saveOrUpdate(this.selectedUser, this.newPassword);
            
            // Mensaje de éxito
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "User Saved Successfully.");
            
            this.userList = userService.findAll(); // Recarga la lista
            PrimeFaces.current().executeScript("PF('userDialogWidget').hide()");
            PrimeFaces.current().ajax().update("form:dt-users");

        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occurred: " + e.getMessage());
        }
    }

    public void deleteUser() {
        try {
            userService.delete(this.selectedUser.getId());
            this.selectedUser = null;
            this.userList = userService.findAll(); // Recarga la lista
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "User Deleted");
            PrimeFaces.current().ajax().update("form:dt-users");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not delete user.");
        }
    }
    
    public List<Role> getAvailableRoles() {
        return availableRoles;
    }

    // --- Getters y Setters ---
    public List<User> getUserList() { return userList; }
    public void setUserList(List<User> userList) { this.userList = userList; }
    public User getSelectedUser() { return selectedUser; }
    public void setSelectedUser(User selectedUser) { this.selectedUser = selectedUser; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    
    // Método de utilidad
    public void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
}
