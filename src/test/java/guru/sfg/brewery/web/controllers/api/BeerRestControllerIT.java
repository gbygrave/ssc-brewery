package guru.sfg.brewery.web.controllers.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Random;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.web.controllers.BaseIT;
import guru.sfg.brewery.web.model.BeerStyleEnum;

@SpringBootTest
public class BeerRestControllerIT extends BaseIT {
    @Autowired
    BeerRepository beerRepository;

    @DisplayName("Delete Tests")
    @Nested
    class DeleteTests {

        public Beer beerToDelete() {
            Random rand = new Random();
            return beerRepository.saveAndFlush(Beer.builder()
                    .beerName("Delete Me Beer")
                    .beerStyle(BeerStyleEnum.IPA)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(String.valueOf(rand.nextInt(99999999)))
                    .build());
        }

        @Test
        void deleteBeerBadCreds() throws Exception {
            mockMvc.perform(
                    delete("/api/v1/beer/" + beerToDelete().getId())
                            .header("Api-Key", "spring")
                            .header("Api-Secret", "guruXXXX"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void deleteBeerBadCredsUrl() throws Exception {
            mockMvc.perform(
                    delete("/api/v1/beer/" + beerToDelete().getId())
                            .param("apiKey", "spring")
                            .param("apiSecret", "guruXXXX"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void deleteBeer() throws Exception {
            mockMvc.perform(
                    delete("/api/v1/beer/" + beerToDelete().getId())
                            .header("Api-Key", "spring")
                            .header("Api-Secret", "guru"))
                    .andExpect(status().isOk());
        }

        @Test
        void deleteBeerUrl() throws Exception {
            mockMvc.perform(
                    delete("/api/v1/beer/" + beerToDelete().getId())
                            .param("apiKey", "spring")
                            .param("apiSecret", "guru"))
                    .andExpect(status().isOk());
        }

        @Test
        void deleteBeerHttpBasic() throws Exception {
            mockMvc.perform(
                    delete("/api/v1/beer/" + beerToDelete().getId())
                            .with(httpBasic("spring", "guru")))
                    .andExpect(status().is2xxSuccessful());
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BaseIT#getStreamNotAdmin")
        void deleteBeerHttpBasicNotAuth(String user, String pwd) throws Exception {
            mockMvc.perform(
                    delete("/api/v1/beer/" + beerToDelete().getId()).with(httpBasic(user, pwd)))
                    .andExpect(status().isForbidden());
        }

        @Test
        void deleteBeerNoAuth() throws Exception {
            mockMvc.perform(
                    delete("/api/v1/beer/" + beerToDelete().getId()))
                    .andExpect(status().isUnauthorized());
        }

    }

    @DisplayName("Find All Beers")
    @Nested
    class FindAllBeers {
        @Test
        void findBeers() throws Exception {
            mockMvc.perform(get("/api/v1/beer/")).andExpect(status().isUnauthorized());
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
        void findBeers(String user, String pwd) throws Exception {
            mockMvc.perform(get("/api/v1/beer/").with(httpBasic(user, pwd)))
                    .andExpect(status().isOk());
        }
    }

    @DisplayName("Find Beer By ID")
    @Nested
    class FindBeerById {
        @Test
        void findBeerById() throws Exception {
            Beer beer = beerRepository.findAll().get(0);
            mockMvc.perform(get("/api/v1/beer/" + beer.getId()))
                    .andExpect(status().isUnauthorized());
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
        void findBeerById(String user, String pwd) throws Exception {
            Beer beer = beerRepository.findAll().get(0);
            mockMvc.perform(get("/api/v1/beer/" + beer.getId()).with(httpBasic(user, pwd)))
                    .andExpect(status().isOk());
        }
    }

    @DisplayName("Find Beer By UPC")
    @Nested
    class FindBeerByUPC {

        @Test
        void findBeerByUpc() throws Exception {
            mockMvc.perform(get("/api/v1/beerUpc/0631234200036"))
                    .andExpect(status().isUnauthorized());
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
        void findBeerByUpc(String user, String pwd) throws Exception {
            mockMvc.perform(
                    get("/api/v1/beerUpc/0631234200036").with(httpBasic(user, pwd)))
                    .andExpect(status().isOk());
        }

    }

    @DisplayName("Get Breweries")
    @Nested
    class GetBreweries {
        @Test
        void getBreweriesJsonCUSTOMER() throws Exception {
            mockMvc.perform(get("/brewery/api/v1/breweries").with(httpBasic("scott", "tiger")))
                    .andExpect(status().isOk());
        }

        @Test
        void getBreweriesJsonADMIN() throws Exception {
            mockMvc.perform(get("/brewery/api/v1/breweries").with(httpBasic("spring", "guru")))
                    .andExpect(status().isOk());
        }

        @Test
        void getBreweriesJsonUSER() throws Exception {
            mockMvc.perform(get("/brewery/api/v1/breweries").with(httpBasic("user", "password")))
                    .andExpect(status().isForbidden());
        }

        @Test
        void getBreweriesJsonNOAUTH() throws Exception {
            mockMvc.perform(get("/brewery/api/v1/breweries"))
                    .andExpect(status().isUnauthorized());
        }
    }

}
