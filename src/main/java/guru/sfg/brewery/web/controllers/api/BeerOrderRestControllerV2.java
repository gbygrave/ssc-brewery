package guru.sfg.brewery.web.controllers.api;

import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.security.perms.OrderReadPermissionV2;
import guru.sfg.brewery.services.BeerOrderService;
import guru.sfg.brewery.web.model.BeerOrderDto;
import guru.sfg.brewery.web.model.BeerOrderPagedList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/orders/")
@Slf4j
public class BeerOrderRestControllerV2 {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE   = 25;

    private final BeerOrderService beerOrderService;

    @OrderReadPermissionV2
    @GetMapping
    // @formatter:off
    public BeerOrderPagedList listOrders(@AuthenticationPrincipal User user,
                                         @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                         @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        // @formatter:on
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        if (user.getCustomer() != null) {
            return beerOrderService.listOrders(user.getCustomer().getId(), PageRequest.of(pageNumber, pageSize));
        } else {
            return beerOrderService.listOrders(PageRequest.of(pageNumber, pageSize));
        }
    }

    @OrderReadPermissionV2
    @GetMapping("{orderId}")
    public BeerOrderDto getOrder(@PathVariable("orderId") UUID orderId) {
        BeerOrderDto beerOrderDto = beerOrderService.getOrderById(orderId);
        if (beerOrderDto == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order Not Found");
        }
        log.debug("Found Order: " + beerOrderDto);
        return beerOrderDto;
    }
}
