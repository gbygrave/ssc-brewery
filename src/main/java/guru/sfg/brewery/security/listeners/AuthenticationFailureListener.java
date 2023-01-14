package guru.sfg.brewery.security.listeners;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import guru.sfg.brewery.domain.security.User;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AuthenticationFailureListener {
    
    @EventListener
    public void listen(AuthenticationFailureBadCredentialsEvent event) {
        log.debug("Login attempt with bad credentials");
        
        if (event.getSource() instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken)event.getSource();
            if (token.getPrincipal() instanceof String) {
                log.debug("Attempted login with username: " + token.getPrincipal());
            }
            
            if (token.getDetails() instanceof WebAuthenticationDetails) {
                WebAuthenticationDetails details = (WebAuthenticationDetails)token.getDetails();
                
                log.debug("Source IP: " + details.getRemoteAddress());
            }
        }
    }

}
