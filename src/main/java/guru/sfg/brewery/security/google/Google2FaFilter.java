package guru.sfg.brewery.security.google;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.autoconfigure.security.servlet.StaticResourceRequest.StaticResourceRequestMatcher;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import guru.sfg.brewery.domain.security.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Google2FaFilter extends GenericFilterBean {

    private final AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();
    private final Google2FAFailureHandler     google2FAFailureHandler     = new Google2FAFailureHandler();
    private final RequestMatcher              urlIs2FA                    = new AntPathRequestMatcher("/user/verify2fa");
    private final RequestMatcher              urlResource                 = new AntPathRequestMatcher("/resources/**");

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest  request  = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // Skip 2FA filter for verify2fa page and static resources.
        StaticResourceRequestMatcher staticResourceRequestMatcher = PathRequest.toStaticResources().atCommonLocations();
        if (urlIs2FA.matches(request) || urlResource.matches(request) || staticResourceRequestMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Ensure 2FA has been completed if configured for user.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !authenticationTrustResolver.isAnonymous(authentication)) {
            log.debug("Processing 2fa filter...");
            if (authentication.getPrincipal() != null && authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                if (user.getUseGoogle2FA() && user.getGoogle2FARequired()) {
                    log.debug("2FA Required");
                    google2FAFailureHandler.onAuthenticationFailure(request, response, null);
                    return;
                }
            }
        }

        // If here, then 2FA either completed or not required.
        filterChain.doFilter(request, response);
    }

}
