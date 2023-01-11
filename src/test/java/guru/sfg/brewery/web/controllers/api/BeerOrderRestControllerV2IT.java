package guru.sfg.brewery.web.controllers.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;

import com.fasterxml.jackson.databind.ObjectMapper;

import guru.sfg.brewery.bootstrap.DefaultBreweryLoader;
import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.domain.BeerOrder;
import guru.sfg.brewery.domain.Customer;
import guru.sfg.brewery.repositories.BeerOrderRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.repositories.CustomerRepository;
import guru.sfg.brewery.web.controllers.BaseIT;

@SpringBootTest
public class BeerOrderRestControllerV2IT extends BaseIT {

    public static final String API_ROOT = "/api/v2/orders/";

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    ObjectMapper objectMapper;

    Customer   stPeteCustomer;
    Customer   dunedinCustomer;
    Customer   keyWestCustomer;
    List<Beer> loadedBeers;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        stPeteCustomer = customerRepository.findAllByCustomerName(DefaultBreweryLoader.ST_PETE_DISTRIBUTING).orElseThrow();
        dunedinCustomer = customerRepository.findAllByCustomerName(DefaultBreweryLoader.ST_PETE_DISTRIBUTING).orElseThrow();
        keyWestCustomer = customerRepository.findAllByCustomerName(DefaultBreweryLoader.ST_PETE_DISTRIBUTING).orElseThrow();
        loadedBeers = beerRepository.findAll();
    }

    @Test
    void listOrdersNOTAuth() throws Exception {
        mockMvc.perform(get(API_ROOT))
                .andExpect(status().isUnauthorized());
    }

    @WithUserDetails(value = "spring")
    @Test
    void listOrdersAdminAuth() throws Exception {
        mockMvc.perform(get(API_ROOT))
                .andExpect(status().isOk());
    }

    @WithUserDetails(value = DefaultBreweryLoader.STPETE_USER)
    @Test
    void listOrdersCustomerAuth() throws Exception {
        mockMvc.perform(get(API_ROOT))
                .andExpect(status().isOk());
    }

    @WithUserDetails(value = DefaultBreweryLoader.DUNEDIN_USER)
    @Test
    void listOrdersCustomerDunedinAuth() throws Exception {
        mockMvc.perform(get(API_ROOT))
                .andExpect(status().isOk());
    }

    @Test
    void listOrdersNoAuth() throws Exception {
        mockMvc.perform(get(API_ROOT))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @Transactional
    void getByOrderIdNotAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(get(API_ROOT + beerOrder.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails("spring")
    @Transactional
    void getByOrderIdADMIN() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(get(API_ROOT + beerOrder.getId()))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @WithUserDetails(DefaultBreweryLoader.STPETE_USER)
    @Transactional
    void getByOrderIdCustomerAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(get(API_ROOT + beerOrder.getId()))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @WithUserDetails(DefaultBreweryLoader.DUNEDIN_USER)
    @Transactional
    void getByOrderIdCustomerNOTAuth() throws Exception {
        BeerOrder beerOrder = stPeteCustomer.getBeerOrders().stream().findFirst().orElseThrow();
        mockMvc.perform(get(API_ROOT + beerOrder.getId()))
                .andExpect(status().isNotFound());
    }

}
