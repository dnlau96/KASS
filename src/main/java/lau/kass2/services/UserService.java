package lau.kass2.services;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import lau.kass2.dao.UserDAO;
import lau.kass2.models.User;
// Importa una librería de Hashing si la usas, ej:
// import org.mindrot.jbcrypt.BCrypt;

@Stateless // Consistente con tu ProductService
public class UserService {

    @Inject
    private UserDAO userDAO;

    public User validateCredentialsAndGetUser(String username, String plainTextPassword) {
        User user = userDAO.findByUsername(username);
        if (user != null) {
            // Lógica de validación (por ahora simple, idealmente usa BCrypt)
            if (plainTextPassword.equals(user.getPasswordHash())) {
                return user;
            }
        }
        return null;
    }
    public User findUserByUsername(String username) {
       
        return userDAO.findByUsername(username);
    }
    public User authorizeActionByBadge(String badgeCode) {
        User user = userDAO.findByBadgeCode(badgeCode);
        if (user != null && ("ADMIN".equals(user.getRole()) || "SUPERVISOR".equals(user.getRole()))) {
            return user;
        }
        return null;
    }

    // --- MÉTODOS CRUD AÑADIDOS ---

    public List<User> findAll() {
        return userDAO.findAll();
    }

    @Transactional
    public void saveOrUpdate(User user, String newPassword) {
        // Lógica para manejar la contraseña
        if (newPassword != null && !newPassword.isEmpty()) {
            // AQUÍ ES DONDE DEBERÍAS HASHEAR LA CONTRASEÑA
            // Ejemplo (SIN HASH, SOLO PARA PRUEBAS):
            user.setPasswordHash(newPassword);
            
            // Ejemplo (CON HASH - RECOMENDADO):
            // String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            // user.setPasswordHash(hashedPassword);
        }
        
        if (user.getId() == null) {
            userDAO.create(user);
        } else {
            // Solo actualiza si la contraseña no está vacía o si no es un usuario nuevo
            if (newPassword != null && !newPassword.isEmpty()) {
                userDAO.update(user);
            } else if (user.getId() != null) {
                // Si no se provee contraseña, solo actualiza los otros campos
                User existingUser = userDAO.findByUsername(user.getUsername());
                existingUser.setRole(user.getRole());
                existingUser.setBadgeCode(user.getBadgeCode());
                userDAO.update(existingUser);
            }
        }
    }

    @Transactional
    public void delete(Long userId) {
        userDAO.delete(userId);
    }
}