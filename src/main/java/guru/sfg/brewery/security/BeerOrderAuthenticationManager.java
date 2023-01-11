package guru.sfg.brewery.security;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import guru.sfg.brewery.domain.security.User;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BeerOrderAuthenticationManager {

    public boolean customerIdMatches(Authentication authentication, UUID customerId) {
        User authenticatedUser = (User) authentication.getPrincipal();

        log.debug("Auth user Customer Id: " + authenticatedUser.getCustomer().getId() + " Customer Id: " + customerId);

        return authenticatedUser.getCustomer().getId().equals(customerId);
    }
}
