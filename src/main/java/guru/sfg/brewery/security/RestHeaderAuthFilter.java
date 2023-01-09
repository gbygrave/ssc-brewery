package guru.sfg.brewery.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.matcher.RequestMatcher;

public class RestHeaderAuthFilter extends AbstractRestAuthFilter {

    public RestHeaderAuthFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
        // TODO Auto-generated constructor stub
    }

    @Override
    String getUserName(HttpServletRequest request) {
        return request.getHeader("Api-Key");
    }

    @Override
    String getPassword(HttpServletRequest request) {
        return request.getHeader("Api-Secret");
    }

}
