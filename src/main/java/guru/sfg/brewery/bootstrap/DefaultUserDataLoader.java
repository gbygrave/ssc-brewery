/*
 *  Copyright 2020 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package guru.sfg.brewery.bootstrap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.AuthorityRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class DefaultUserDataLoader implements CommandLineRunner {

    private static final String CUSTOMER_ROLE = "CUSTOMER";
    private static final String USER_ROLE     = "USER";
    private static final String ADMIN_ROLE    = "ADMIN";

    private final UserRepository      userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder     passwordEncoder;

    @Override
    public void run(String... args) {
        loadAuthorityData();
        loadUserData();
    }

    private void loadAuthorityData() {
        if (authorityRepository.count() == 0) {
            authorityRepository.save(Authority.builder().role(ADMIN_ROLE).build());
            authorityRepository.save(Authority.builder().role(USER_ROLE).build());
            authorityRepository.save(Authority.builder().role(CUSTOMER_ROLE).build());
            log.debug("Authorities Loaded: " + authorityRepository.count());
        }
    }

    private void loadUserData() {
        if (userRepository.count() == 0) {

            Authority admin    = authorityRepository.findByRole(ADMIN_ROLE).orElseThrow();
            Authority user     = authorityRepository.findByRole(USER_ROLE).orElseThrow();
            Authority customer = authorityRepository.findByRole(CUSTOMER_ROLE).orElseThrow();

            userRepository.save(User.builder()
                    .username("spring")
                    .password(passwordEncoder.encode("guru"))
                    .authority(admin)
                    .build());

            userRepository.save(User.builder()
                    .username("user")
                    .password(passwordEncoder.encode("password"))
                    .authority(user)
                    .build());

            userRepository.save(User.builder()
                    .username("scott")
                    .password(passwordEncoder.encode("tiger"))
                    .authority(customer)
                    .build());

            log.debug("Users Loaded: " + userRepository.count());
        }
    }

}
