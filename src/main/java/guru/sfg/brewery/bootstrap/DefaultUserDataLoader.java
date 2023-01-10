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

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.Role;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.AuthorityRepository;
import guru.sfg.brewery.repositories.security.RoleRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class DefaultUserDataLoader implements CommandLineRunner {

    private static final String PERMISSION_BEER_CREATE = "beer.create";
    private static final String PERMISSION_BEER_READ   = "beer.read";
    private static final String PERMISSION_BEER_UPDATE = "beer.update";
    private static final String PERMISSION_BEER_DELETE = "beer.delete";

    private static final String ROLE_CUSTOMER = "ROLE_CUSTOMER";
    private static final String ROLE_USER     = "ROLE_USER";
    private static final String ROLE_ADMIN    = "ROLE_ADMIN";

    private final AuthorityRepository authorityRepository;
    private final RoleRepository      roleRepository;
    private final UserRepository      userRepository;
    private final PasswordEncoder     passwordEncoder;

    @Override
    public void run(String... args) {
        loadAuthorityData();
        loadRoleData();
        loadUserData();
    }

    private void loadAuthorityData() {
        if (authorityRepository.count() == 0) {
            authorityRepository.save(Authority.builder().permission(PERMISSION_BEER_CREATE).build());
            authorityRepository.save(Authority.builder().permission(PERMISSION_BEER_READ).build());
            authorityRepository.save(Authority.builder().permission(PERMISSION_BEER_UPDATE).build());
            authorityRepository.save(Authority.builder().permission(PERMISSION_BEER_DELETE).build());
            log.debug("Authorities Created: " + authorityRepository.count());
        }
    }

    private void loadRoleData() {
        if (roleRepository.count() == 0) {
            Authority createBeer = authorityRepository.findByPermission(PERMISSION_BEER_CREATE).orElseThrow();
            Authority readBeer   = authorityRepository.findByPermission(PERMISSION_BEER_READ).orElseThrow();
            Authority updateBeer = authorityRepository.findByPermission(PERMISSION_BEER_UPDATE).orElseThrow();
            Authority deleteBeer = authorityRepository.findByPermission(PERMISSION_BEER_DELETE).orElseThrow();

            roleRepository.save(Role.builder()
                    .name(ROLE_ADMIN)
                    .authorities(Set.of(createBeer, readBeer, updateBeer, deleteBeer))
                    .build());

            roleRepository.save(Role.builder()
                    .name(ROLE_CUSTOMER)
                    .authorities(Set.of(readBeer))
                    .build());

            roleRepository.save(Role.builder()
                    .name(ROLE_USER)
                    .authorities(Set.of(readBeer))
                    .build());
            log.debug("Roles Created: " + authorityRepository.count());
        }
    }

    private void loadUserData() {
        if (userRepository.count() == 0) {
            Role admin = roleRepository.findByName(ROLE_ADMIN).orElseThrow();
            Role customer = roleRepository.findByName(ROLE_CUSTOMER).orElseThrow();
            Role user = roleRepository.findByName(ROLE_USER).orElseThrow();
            
            userRepository.save(User.builder()
                    .username("spring")
                    .password(passwordEncoder.encode("guru"))
                    .role(admin)
                    .build());

            userRepository.save(User.builder()
                    .username("scott")
                    .password(passwordEncoder.encode("tiger"))
                    .role(customer)
                    .build());
            
            userRepository.save(User.builder()
                    .username("user")
                    .password(passwordEncoder.encode("password"))
                    .role(user)
                    .build());

            log.debug("Users Created: " + userRepository.count());
        }
    }

}
