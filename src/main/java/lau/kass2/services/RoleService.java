/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lau.kass2.services;

/**
 *
 * @author dnlau
 */

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import lau.kass2.dao.RoleDAO;
import lau.kass2.models.Role;

@Stateless
public class RoleService {

    @Inject
    private RoleDAO roleDAO;

    public List<Role> findAll() {
        return roleDAO.findAll();
    }

    @Transactional
    public void saveOrUpdate(Role role) {
        if (role.getId() == null) {
            roleDAO.create(role);
        } else {
            roleDAO.update(role);
        }
    }

    @Transactional
    public void delete(Long roleId) {
        roleDAO.delete(roleId);
    }
}