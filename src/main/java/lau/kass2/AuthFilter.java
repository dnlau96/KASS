package lau.kass2;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import lau.kass2.beans.AdminBean;
import lau.kass2.models.User;

@WebFilter(filterName = "AuthFilter", urlPatterns = {"/administrador/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false); 

        // --- LÍNEA CORREGIDA ---
        // Comparamos el objeto 'session' con la palabra clave 'null'
        AdminBean adminBean = (session != null) ? (AdminBean) session.getAttribute("adminBean") : null;
        
        // --- LÍNEA CORREGIDA ---
        // Comparamos el objeto 'adminBean' con la palabra clave 'null'
        User currentUser = (adminBean != null) ? adminBean.getCurrentUser() : null;
        
        if (currentUser == null) {
            res.sendRedirect(req.getContextPath() + "/index.xhtml");
            return;
        }

        String role = currentUser.getRole();
        String requestURI = req.getRequestURI();

        if (role.equals("Administrador")) {
            chain.doFilter(request, response);
            return;
        }
        
        if (role.equals("PRODUCT_MANAGER")) {
            if (requestURI.endsWith("/dashboard.xhtml") || 
                requestURI.endsWith("/products.xhtml") || 
                requestURI.endsWith("/kardex.xhtml")) 
            {
                chain.doFilter(request, response);
            } else {
                res.sendRedirect(req.getContextPath() + "/admin/dashboard.xhtml");
            }
            return;
        }
        
        res.sendRedirect(req.getContextPath() + "/index.xhtml");
    }

    // Dejamos los otros métodos vacíos o como los tengas
    @Override
    public void init(jakarta.servlet.FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}