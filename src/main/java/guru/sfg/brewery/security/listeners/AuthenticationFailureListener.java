package guru.sfg.brewery.security.listeners;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import guru.sfg.brewery.domain.security.LoginFailure;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.LoginFailureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFailureListener {
    
    private final LoginFailureRepository loginFailureRepository;
    
    @EventListener
    public void listen(AuthenticationFailureBadCredentialsEvent event) {
        log.debug("Login attempt with bad credentials");
        
        if (event.getSource() instanceof UsernamePasswordAuthenticationToken) {
            
            LoginFailure.LoginFailureBuilder builder = LoginFailure.builder();
            
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken)event.getSource();
            if (token.getPrincipal() instanceof User) {
                User user = (User) token.getPrincipal();
                builder.user(user);
                builder.username(user.getUsername());
            } else if (token.getPrincipal() instanceof String) {
                String username = (String) token.getPrincipal();
                log.debug("Attempted login with username: " + username);
                builder.username(username);
            }
            
            if (token.getDetails() instanceof WebAuthenticationDetails) {
                WebAuthenticationDetails details = (WebAuthenticationDetails)token.getDetails();
                
                builder.sourceIp(details.getRemoteAddress());
                log.debug("Source IP: " + details.getRemoteAddress());
            }

            LoginFailure loginFailure = loginFailureRepository.save(builder.build());
            log.debug("Login Failure saved. Id: " + loginFailure.getId());
        }
    }
}
