package guru.sfg.brewery.web.controllers.api;

import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import guru.sfg.brewery.security.perms.OrderCreatePermission;
import guru.sfg.brewery.security.perms.OrderPickupPermission;
import guru.sfg.brewery.security.perms.OrderReadPermission;
import guru.sfg.brewery.services.BeerOrderService;
import guru.sfg.brewery.web.model.BeerOrderDto;
import guru.sfg.brewery.web.model.BeerOrderPagedList;
import lombok.RequiredArgsConstructor;

@RequestMapping("/api/v1/customers/{customerId}/")
@RestController
@RequiredArgsConstructor
public class BeerOrderRestController {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE   = 25;

    private final BeerOrderService beerOrderService;

    @GetMapping("orders")
    @OrderReadPermission
    // @formatter:off
    public BeerOrderPagedList listOrders(@PathVariable("customerId")                            UUID    customerId,
                                         @RequestParam(value = "pageNumber", required = false)  Integer pageNumber,
                                         @RequestParam(value = "pageSize", required = false)    Integer pageSize) {
        //@formatter:on
        if (pageNumber == null || pageNumber == 0)
            pageNumber = DEFAULT_PAGE_NUMBER;
        if (pageSize == null || pageSize == 0)
            pageSize = DEFAULT_PAGE_SIZE;

        return beerOrderService.listOrders(customerId, PageRequest.of(pageNumber, pageSize));
    }

    @PostMapping("orders")
    @ResponseStatus(HttpStatus.CREATED)
    @OrderCreatePermission
    public BeerOrderDto placeOrder(@PathVariable("customerId") UUID customerId,
            @RequestBody BeerOrderDto beerOrderDto) {
        return beerOrderService.placeOrder(customerId, beerOrderDto);
    }

    @GetMapping("orders/{orderId}")
    @OrderReadPermission
    public BeerOrderDto getOrder(@PathVariable("customerId") UUID customerId, @PathVariable("orderId") UUID orderId) {
        return beerOrderService.getOrderById(customerId, orderId);
    }

    @PutMapping("/orders/{orderId}/pickup")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @OrderPickupPermission
    public void pickupOrder(@PathVariable("customerId") UUID customerId, @PathVariable("orderId") UUID orderId) {
        beerOrderService.pickupOrder(customerId, orderId);
    }
}
