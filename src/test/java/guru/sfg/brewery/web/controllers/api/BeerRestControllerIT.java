package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.web.controllers.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class BeerRestControllerIT extends BaseIT {

    @Test
    void deleteBeerBadCreds() throws Exception {
        mockMvc.perform(
                delete("/api/v1/beer/e720bb15-f07f-4a18-9238-1df483d8c2b1")
                        .header("Api-Key", "spring")
                        .header("Api-Secret", "guruXXXX"))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void deleteBeerBadCredsUrl() throws Exception {
        mockMvc.perform(
                delete("/api/v1/beer/e720bb15-f07f-4a18-9238-1df483d8c2b1")
                        .param("apiKey", "spring")
                        .param("apiSecret", "guruXXXX"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteBeer() throws Exception {
        mockMvc.perform(
                delete("/api/v1/beer/e720bb15-f07f-4a18-9238-1df483d8c2b1")
                        .header("Api-Key", "spring")
                        .header("Api-Secret", "guru"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteBeerUrl() throws Exception {
        mockMvc.perform(
                delete("/api/v1/beer/e720bb15-f07f-4a18-9238-1df483d8c2b1")
                        .param("apiKey", "spring")
                        .param("apiSecret", "guru"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteBeerHttpBasic() throws Exception {
        mockMvc.perform(
                delete("/api/v1/beer/e720bb15-f07f-4a18-9238-1df483d8c2b1")
                        .with(httpBasic("spring", "guru")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void deleteBeerNoAuth() throws Exception {
        mockMvc.perform(
                delete("/api/v1/beer/e720bb15-f07f-4a18-9238-1df483d8c2b1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findBeers() throws Exception {
        mockMvc.perform(get("/api/v1/beer/")).andExpect(status().isOk());
    }

    @Test
    void findBeerById() throws Exception {
        mockMvc.perform(get("/api/v1/beer/e720bb15-f07f-4a18-9238-1df483d8c2b1"))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerByUpc() throws Exception {
        mockMvc.perform(get("/api/v1/beerUpc/0631234200036"))
                .andExpect(status().isOk());
    }
}
