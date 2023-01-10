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

import guru.sfg.brewery.domain.*;
import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.Role;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.*;
import guru.sfg.brewery.repositories.security.AuthorityRepository;
import guru.sfg.brewery.repositories.security.RoleRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import guru.sfg.brewery.web.model.BeerStyleEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

/**
 * Created by jt on 2019-01-26.
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class DefaultBreweryLoader implements CommandLineRunner {

    public static final String PERMISSION_BEER_CREATE           = "beer.create";
    public static final String PERMISSION_BEER_READ             = "beer.read";
    public static final String PERMISSION_BEER_UPDATE           = "beer.update";
    public static final String PERMISSION_BEER_DELETE           = "beer.delete";
    public static final String PERMISSION_CUSTOMER_CREATE       = "customer.create";
    public static final String PERMISSION_CUSTOMER_READ         = "customer.read";
    public static final String PERMISSION_CUSTOMER_UPDATE       = "customer.update";
    public static final String PERMISSION_CUSTOMER_DELETE       = "customer.delete";
    public static final String PERMISSION_BREWERY_CREATE        = "brewery.create";
    public static final String PERMISSION_BREWERY_READ          = "brewery.read";
    public static final String PERMISSION_BREWERY_UPDATE        = "brewery.update";
    public static final String PERMISSION_BREWERY_DELETE        = "brewery.delete";
    public static final String PERMISSION_ORDER_CREATE          = "order.create";
    public static final String PERMISSION_ORDER_READ            = "order.read";
    public static final String PERMISSION_ORDER_UPDATE          = "order.update";
    public static final String PERMISSION_ORDER_DELETE          = "order.delete";
    public static final String PERMISSION_CUSTOMER_ORDER_CREATE = "customer.order.create";
    public static final String PERMISSION_CUSTOMER_ORDER_READ   = "customer.order.read";
    public static final String PERMISSION_CUSTOMER_ORDER_UPDATE = "customer.order.update";
    public static final String PERMISSION_CUSTOMER_ORDER_DELETE = "customer.order.delete";

    public static final String ROLE_CUSTOMER = "ROLE_CUSTOMER";
    public static final String ROLE_USER     = "ROLE_USER";
    public static final String ROLE_ADMIN    = "ROLE_ADMIN";

    public static final String TASTING_ROOM          = "Tasting Room";
    public static final String ST_PETE_DISTRIBUTING  = "St Pete Distributing";
    public static final String DUNEDIN_DISTRIBUTING  = "Dunedin Distributing";
    public static final String KEY_WEST_DISTRIBUTORS = "Key West Distributors";

    public static final String BEER_1_UPC = "0631234200036";
    public static final String BEER_2_UPC = "0631234300019";
    public static final String BEER_3_UPC = "0083783375213";

    private final BreweryRepository       breweryRepository;
    private final BeerRepository          beerRepository;
    private final BeerInventoryRepository beerInventoryRepository;
    private final BeerOrderRepository     beerOrderRepository;
    private final CustomerRepository      customerRepository;
    private final AuthorityRepository     authorityRepository;
    private final RoleRepository          roleRepository;
    private final UserRepository          userRepository;
    private final PasswordEncoder         passwordEncoder;

    @Override
    public void run(String... args) {
        loadAuthorityData();
        loadRoleData();
        loadUserData();

        loadBreweryData();
        loadTastingRoomData();
        loadCustomerData();
    }

    private void loadCustomerData() {
        Role customerRole = roleRepository.findByName("ROLE_CUSTOMER").orElseThrow();

        // create customers
        Customer stPeteCustomer = customerRepository.save(Customer.builder()
                .customerName(ST_PETE_DISTRIBUTING)
                .apiKey(UUID.randomUUID())
                .build());

        Customer dunedinCustomer = customerRepository.save(Customer.builder()
                .customerName(DUNEDIN_DISTRIBUTING)
                .apiKey(UUID.randomUUID())
                .build());

        Customer keyWestCustomer = customerRepository.save(Customer.builder()
                .customerName(KEY_WEST_DISTRIBUTORS)
                .apiKey(UUID.randomUUID())
                .build());

        // create users
        User stPeteUser = userRepository.save(User.builder().username("stpete")
                .password(passwordEncoder.encode("password"))
                .customer(stPeteCustomer)
                .role(customerRole).build());

        User dunedinUser = userRepository.save(User.builder().username("dunedin")
                .password(passwordEncoder.encode("password"))
                .customer(dunedinCustomer)
                .role(customerRole).build());

        User keyWestUser = userRepository.save(User.builder().username("keywest")
                .password(passwordEncoder.encode("password"))
                .customer(keyWestCustomer)
                .role(customerRole).build());

        // create orders
        createOrder(stPeteCustomer);
        createOrder(dunedinCustomer);
        createOrder(keyWestCustomer);
        
        log.debug("Orders Created: " + beerOrderRepository.count());
    }

    private BeerOrder createOrder(Customer customer) {
        return beerOrderRepository.save(BeerOrder.builder()
                .customer(customer)
                .orderStatus(OrderStatusEnum.NEW)
                .beerOrderLines(Set.of(BeerOrderLine.builder()
                        .beer(beerRepository.findByUpc(BEER_1_UPC))
                        .orderQuantity(2)
                        .build()))
                .build());
    }

    private void loadAuthorityData() {
        if (authorityRepository.count() == 0) {
            authorityRepository.save(Authority.builder().permission(PERMISSION_BEER_CREATE).build());
            authorityRepository.save(Authority.builder().permission(PERMISSION_BEER_READ).build());
            authorityRepository.save(Authority.builder().permission(PERMISSION_BEER_UPDATE).build());
            authorityRepository.save(Authority.builder().permission(PERMISSION_BEER_DELETE).build());

            authorityRepository.save(Authority.builder().permission(PERMISSION_CUSTOMER_CREATE).build());
            authorityRepository.save(Authority.builder().permission(PERMISSION_CUSTOMER_READ).build());
            authorityRepository.save(Authority.builder().permission(PERMISSION_CUSTOMER_UPDATE).build());
            authorityRepository.save(Authority.builder().permission(PERMISSION_CUSTOMER_DELETE).build());

            authorityRepository.save(Authority.builder().permission(PERMISSION_BREWERY_CREATE).build());
            authorityRepository.save(Authority.builder().permission(PERMISSION_BREWERY_READ).build());
            authorityRepository.save(Authority.builder().permission(PERMISSION_BREWERY_UPDATE).build());
            authorityRepository.save(Authority.builder().permission(PERMISSION_BREWERY_DELETE).build());

            authorityRepository.save(Authority.builder().permission(PERMISSION_ORDER_CREATE).build());
            authorityRepository.save(Authority.builder().permission(PERMISSION_ORDER_READ).build());
            authorityRepository.save(Authority.builder().permission(PERMISSION_ORDER_UPDATE).build());
            authorityRepository.save(Authority.builder().permission(PERMISSION_ORDER_DELETE).build());

            authorityRepository.save(Authority.builder().permission(PERMISSION_CUSTOMER_ORDER_CREATE).build());
            authorityRepository.save(Authority.builder().permission(PERMISSION_CUSTOMER_ORDER_READ).build());
            authorityRepository.save(Authority.builder().permission(PERMISSION_CUSTOMER_ORDER_UPDATE).build());
            authorityRepository.save(Authority.builder().permission(PERMISSION_CUSTOMER_ORDER_DELETE).build());

            log.debug("Authorities Created: " + authorityRepository.count());
        }
    }

    private void loadRoleData() {
        if (roleRepository.count() == 0) {
            Authority createBeer          = authorityRepository.findByPermission(PERMISSION_BEER_CREATE).orElseThrow();
            Authority readBeer            = authorityRepository.findByPermission(PERMISSION_BEER_READ).orElseThrow();
            Authority updateBeer          = authorityRepository.findByPermission(PERMISSION_BEER_UPDATE).orElseThrow();
            Authority deleteBeer          = authorityRepository.findByPermission(PERMISSION_BEER_DELETE).orElseThrow();
            Authority createCustomer      = authorityRepository.findByPermission(PERMISSION_CUSTOMER_CREATE)
                    .orElseThrow();
            Authority readCustomer        = authorityRepository.findByPermission(PERMISSION_CUSTOMER_READ)
                    .orElseThrow();
            Authority updateCustomer      = authorityRepository.findByPermission(PERMISSION_CUSTOMER_UPDATE)
                    .orElseThrow();
            Authority deleteCustomer      = authorityRepository.findByPermission(PERMISSION_CUSTOMER_DELETE)
                    .orElseThrow();
            Authority createBrewery       = authorityRepository.findByPermission(PERMISSION_BREWERY_CREATE)
                    .orElseThrow();
            Authority readBrewery         = authorityRepository.findByPermission(PERMISSION_BREWERY_READ).orElseThrow();
            Authority updateBrewery       = authorityRepository.findByPermission(PERMISSION_BREWERY_UPDATE)
                    .orElseThrow();
            Authority deleteBrewery       = authorityRepository.findByPermission(PERMISSION_BREWERY_DELETE)
                    .orElseThrow();
            Authority createOrder         = authorityRepository.findByPermission(PERMISSION_ORDER_CREATE).orElseThrow();
            Authority readOrder           = authorityRepository.findByPermission(PERMISSION_ORDER_READ).orElseThrow();
            Authority updateOrder         = authorityRepository.findByPermission(PERMISSION_ORDER_UPDATE).orElseThrow();
            Authority deleteOrder         = authorityRepository.findByPermission(PERMISSION_ORDER_DELETE).orElseThrow();
            Authority createCustomerOrder = authorityRepository.findByPermission(PERMISSION_CUSTOMER_ORDER_CREATE)
                    .orElseThrow();
            Authority readCustomerOrder   = authorityRepository.findByPermission(PERMISSION_CUSTOMER_ORDER_READ)
                    .orElseThrow();
            Authority updateCustomerOrder = authorityRepository.findByPermission(PERMISSION_CUSTOMER_ORDER_UPDATE)
                    .orElseThrow();
            Authority deleteCustomerOrder = authorityRepository.findByPermission(PERMISSION_CUSTOMER_ORDER_DELETE)
                    .orElseThrow();

            roleRepository.save(Role.builder()
                    .name(ROLE_ADMIN)
                    .authorities(new HashSet<>(Set.of(
                            createBeer, readBeer, updateBeer, deleteBeer,
                            createCustomer, readCustomer, updateCustomer, deleteCustomer,
                            createBrewery, readBrewery, updateBrewery, deleteBrewery,
                            createOrder, readOrder, updateOrder, deleteOrder)))
                    .build());

            roleRepository.save(Role.builder()
                    .name(ROLE_CUSTOMER)
                    .authorities(new HashSet<>(Set.of(
                            readBeer, readCustomer, readBrewery,
                            createCustomerOrder, readCustomerOrder, updateCustomerOrder, deleteCustomerOrder)))
                    .build());

            roleRepository.save(Role.builder()
                    .name(ROLE_USER)
                    .authorities(new HashSet<>(Set.of(readBeer)))
                    .build());

            log.debug("Roles Created: " + roleRepository.count());
        }
    }

    private void loadUserData() {
        if (userRepository.count() == 0) {
            Role admin    = roleRepository.findByName(ROLE_ADMIN).orElseThrow();
            Role customer = roleRepository.findByName(ROLE_CUSTOMER).orElseThrow();
            Role user     = roleRepository.findByName(ROLE_USER).orElseThrow();

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

    private void loadTastingRoomData() {
        Customer tastingRoom = Customer.builder()
                .customerName(TASTING_ROOM)
                .apiKey(UUID.randomUUID())
                .build();

        customerRepository.save(tastingRoom);

        beerRepository.findAll().forEach(beer -> {
            beerOrderRepository.save(BeerOrder.builder()
                    .customer(tastingRoom)
                    .orderStatus(OrderStatusEnum.NEW)
                    .beerOrderLines(Set.of(BeerOrderLine.builder()
                            .beer(beer)
                            .orderQuantity(2)
                            .build()))
                    .build());
        });
    }

    private void loadBreweryData() {
        if (breweryRepository.count() == 0) {
            breweryRepository.save(Brewery
                    .builder()
                    .breweryName("Cage Brewing")
                    .build());

            Beer mangoBobs = Beer.builder()
                    .beerName("Mango Bobs")
                    .beerStyle(BeerStyleEnum.IPA)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(BEER_1_UPC)
                    .build();

            beerRepository.save(mangoBobs);
            beerInventoryRepository.save(BeerInventory.builder()
                    .beer(mangoBobs)
                    .quantityOnHand(500)
                    .build());

            Beer galaxyCat = Beer.builder()
                    .beerName("Galaxy Cat")
                    .beerStyle(BeerStyleEnum.PALE_ALE)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(BEER_2_UPC)
                    .build();

            beerRepository.save(galaxyCat);
            beerInventoryRepository.save(BeerInventory.builder()
                    .beer(galaxyCat)
                    .quantityOnHand(500)
                    .build());

            Beer pinball = Beer.builder()
                    .beerName("Pinball Porter")
                    .beerStyle(BeerStyleEnum.PORTER)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(BEER_3_UPC)
                    .build();

            beerRepository.save(pinball);
            beerInventoryRepository.save(BeerInventory.builder()
                    .beer(pinball)
                    .quantityOnHand(500)
                    .build());

        }
    }
}
