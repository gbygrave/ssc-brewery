package guru.sfg.brewery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import guru.sfg.brewery.security.AbstractRestAuthFilter;
import guru.sfg.brewery.security.RestHeaderAuthFilter;
import guru.sfg.brewery.security.RestUrlAuthFilter;
import guru.sfg.brewery.security.SfgPasswordEncoderFactories;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private AbstractRestAuthFilter restHeaderAuthFilter(AuthenticationManager authenticationManager) {
        AbstractRestAuthFilter filter = new RestHeaderAuthFilter(new AntPathRequestMatcher("/api/**"));
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    private AbstractRestAuthFilter restParameterAuthFilter(AuthenticationManager authenticationManager) {
        AbstractRestAuthFilter filter = new RestUrlAuthFilter(new AntPathRequestMatcher("/api/**"));
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return SfgPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

//    @Autowired
//    JpaUserDetailsService jpaUserDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(
                restHeaderAuthFilter(authenticationManager()),
                UsernamePasswordAuthenticationFilter.class)
                .csrf().disable();
        http.addFilterBefore(
                restParameterAuthFilter(authenticationManager()),
                UsernamePasswordAuthenticationFilter.class);
        // Don't need to re-disable csrf() protection as this setting is global.

        http.authorizeRequests(authorize -> {
            authorize
                    .antMatchers("/h2-console/**").permitAll() // NOT FOR PROD
                    .antMatchers("/", "/webjars/**", "/login", "/resources/**").permitAll()
                    .mvcMatchers("/brewery/breweries").hasAnyRole("CUSTOMER", "ADMIN")
                    .mvcMatchers(HttpMethod.GET, "/brewery/api/v1/breweries").hasAnyRole("CUSTOMER", "ADMIN")
                    .mvcMatchers("/beers/find", "/beers/{beerId}").hasAnyRole("ADMIN", "USER", "CUSTOMER");

        }).authorizeRequests().anyRequest().authenticated().and().formLogin().and().httpBasic();

        // h2 console config
        http.headers().frameOptions().sameOrigin();
    }

    // REPLACED BY JpaUserDetailsService.java
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(this.jpaUserDetailsService).passwordEncoder(passwordEncoder());
//          OR
//        auth.inMemoryAuthentication()
//                .withUser("spring").password("{bcrypt}$2a$10$ukC.PwF/7QIV4FEdt1ihI.wkIwB4xBWBrBTQR.J0H8raiHKAEp7Ha")
//                .roles("ADMIN")
//                .and()
//                .withUser("user")
//                .password("{sha256}29e55dc39e0dc766306033bb9f77872de067ca670eb293fc53a5db346af78c5a1c4bbd3182b530a7")
//                .roles("USER")
//                .and()
//                .withUser("scott")
//                .password("{bcrypt}$2a$10$Sbub3hlrzlq84E0B4izpA.UjmxdOhlkaFvRImWA1CA1FMyUfNkJsq")
//                .roles("CUSTOMER");
//    }

//	@Override
//	@Bean
//	protected UserDetailsService userDetailsService() {
//		@SuppressWarnings("deprecation")
//		UserDetails admin = User.withDefaultPasswordEncoder()
//				.username("spring")
//				.password("guru")
//				.roles("ADMIN")
//				.build();
//		
//		@SuppressWarnings("deprecation")
//		UserDetails user = User.withDefaultPasswordEncoder()
//				.username("user")
//				.password("password")
//				.roles("USER")
//				.build();
//		
//		return new InMemoryUserDetailsManager(admin, user);
//	}

}
