package guru.sfg.brewery.web.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.repositories.BeerRepository;

@SpringBootTest
public class BeerControllerIT extends BaseIT {

    @Autowired
    BeerRepository beerRepository;

    @DisplayName("Init New Form")
    @Nested
    class InitNewForm {
        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BaseIT#getStreamNotAdmin")
        void initCreationFormAuthNotAdmin(String user, String pwd) throws Exception {
            mockMvc.perform(get("/beers/new").with(httpBasic(user, pwd)))
                    .andExpect(status().isForbidden());
        }

        @Test
        void initCreationFormNotAuth() throws Exception {
            mockMvc.perform(get("/beers/new"))
                    .andExpect(status().isUnauthorized());
        }
        
        void initCreationFormAuthAdmin() throws Exception {
            mockMvc.perform(get("/beers/new").with(httpBasic("spring", "guru")))
                    .andExpect(status().isOk())
                    .andExpect(view().name("beers/createBeer"))
                    .andExpect(model().attributeExists("beer"));
        }
    }

    @DisplayName("Init Find Beer Form")
    @Nested
    class FindForm {
        @Test
        void findBeers() throws Exception {
            mockMvc.perform(get("/beers/find").with(anonymous()))
                    .andExpect(status().isUnauthorized());
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BaseIT#getStreamAllUsers")
        void findBeersWithHttpBasic(String user, String pwd) throws Exception {
            mockMvc.perform(get("/beers/find").with(httpBasic(user, pwd)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("beers/findBeers"))
                    .andExpect(model().attributeExists("beer"));
        }
    }

    @DisplayName("Process Find Beer Form")
    @Nested
    class ProcessFindBeerForm {
        @Test
        void findBeerForm() throws Exception {
            mockMvc.perform(get("/beers").param("beerName", "MANGO BOBS"))
                    .andExpect(status().isUnauthorized());
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BaseIT#getStreamAllUsers")
        void findBeerForm(String user, String pwd) throws Exception {
            mockMvc.perform(get("/beers").param("beerName", "MANGO_BOBS").with(httpBasic(user, pwd)))
                    .andExpect(status().isOk());
        }
    }

    @DisplayName("Get Beer By ID")
    @Nested
    class GetByID {

        Beer beer = beerRepository.findAll().get(0);

        @Test
        void getBeerById() throws Exception {
            mockMvc.perform(get("/beers/" + beer.getId()).with(anonymous()))
                    .andExpect(status().isUnauthorized());
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BaseIT#getStreamAllUsers")
        void getBeerById(String user, String pwd) throws Exception {
            mockMvc.perform(get("/beers/" + beer.getId()).with(httpBasic(user, pwd)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("beers/beerDetails"))
                    .andExpect(model().attributeExists("beer"));
        }

    }

    @DisplayName("List Breweries")
    @Nested
    class ListBreweries {
        @Test
        void listBreweriesCUSTOMER() throws Exception {
            mockMvc.perform(get("/brewery/breweries").with(httpBasic("scott", "tiger")))
            .andExpect(status().is2xxSuccessful());
        }
        
        @Test
        void listBreweriesADMIN() throws Exception {
            mockMvc.perform(get("/brewery/breweries").with(httpBasic("spring", "guru")))
            .andExpect(status().is2xxSuccessful());
        }
        
        @Test
        void listBreweriesUSER() throws Exception {
            mockMvc.perform(get("/brewery/breweries")
                    .with(httpBasic("user", "password")))
            .andExpect(status().isForbidden());
        }
        
        @Test
        void listBreweruesNOAUTH() throws Exception {
            mockMvc.perform(get("/brewery/breweries"))
            .andExpect(status().isUnauthorized());
        }
    }

}
