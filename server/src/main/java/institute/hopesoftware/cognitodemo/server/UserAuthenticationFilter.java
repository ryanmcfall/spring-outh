package institute.hopesoftware.cognitodemo.server;

import java.io.IOException;
import java.io.Writer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class UserAuthenticationFilter extends OncePerRequestFilter {
    public static final String CURRENT_USER_ATTRIBUTE = "currentUser";

    //private final UserRepository userRepository;

    @Autowired
    public UserAuthenticationFilter(/*UserRepository userRepository*/) {
        // this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // Here we are for sure, context object holds a valid and decoded JWT token.
        SecurityContext context = SecurityContextHolder.getContext();
        try {
            if (context.getAuthentication() instanceof JwtAuthenticationToken) {
                JwtAuthenticationToken auth = (JwtAuthenticationToken) context.getAuthentication();
                String cognitoSub = auth.getName();                
                request.setAttribute(CURRENT_USER_ATTRIBUTE, cognitoSub);
            }
        } catch (UsernameNotFoundException ex) {
            // log.error("Encountered error while finding user with current authentication token", ex);
            sendUnAuthorized(response, ex);
            return;
        }

        chain.doFilter(request, response);
    }

    private void sendUnAuthorized(HttpServletResponse response, UsernameNotFoundException ex) {
        response.setStatus(HttpStatus.FORBIDDEN.value());

        if (ex != null) {
            try (Writer writer = response.getWriter()) {
                writer.write(ex.getMessage());
            } catch (IOException ioEx) {
                throw new RuntimeException(ioEx);
            }
        }
    }
}
