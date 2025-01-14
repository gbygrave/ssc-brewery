package guru.sfg.brewery.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.matcher.RequestMatcher;

public class RestUrlAuthFilter extends AbstractRestAuthFilter {

    public RestUrlAuthFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    @Override
    String getUserName(HttpServletRequest request) {
        return request.getParameter("apiKey");
    }

    @Override
    String getPassword(HttpServletRequest request) {
        return request.getParameter("apiSecret");
    }

}
