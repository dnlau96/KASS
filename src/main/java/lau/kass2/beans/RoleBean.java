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
import lau.kass2.models.Role;
import lau.kass2.services.RoleService;
import org.primefaces.PrimeFaces;

@Named("roleBean")
@ViewScoped
public class RoleBean implements Serializable {

    @Inject
    private RoleService roleService;

    private List<Role> roleList;
    private Role selectedRole;

    @PostConstruct
    public void init() {
        this.roleList = roleService.findAll();
    }

    public void openNew() {
        this.selectedRole = new Role();
    }

    public void saveRole() {
        try {
            roleService.saveOrUpdate(this.selectedRole);
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Role Saved Successfully.");
            this.roleList = roleService.findAll(); // Recarga la lista
            PrimeFaces.current().executeScript("PF('roleDialogWidget').hide()");
            PrimeFaces.current().ajax().update("form:dt-roles");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occurred.");
        }
    }

    public void deleteRole() {
        try {
            roleService.delete(this.selectedRole.getId());
            this.selectedRole = null;
            this.roleList = roleService.findAll(); // Recarga la lista
            addMessage(FacesMessage.SEVERITY_INFO, "Success", "Role Deleted");
            PrimeFaces.current().ajax().update("form:dt-roles");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not delete role.");
        }
    }

    // --- Getters y Setters ---
    public List<Role> getRoleList() { return roleList; }
    public void setRoleList(List<Role> roleList) { this.roleList = roleList; }
    public Role getSelectedRole() { return selectedRole; }
    public void setSelectedRole(Role selectedRole) { this.selectedRole = selectedRole; }
    
    public void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }
}