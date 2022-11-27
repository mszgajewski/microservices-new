package com.mszgajewski.orderservice.service;

import brave.Span;
import brave.Tracer;
import com.mszgajewski.orderservice.dto.InventoryResponse;
import com.mszgajewski.orderservice.dto.OrderLineItemsDto;
import com.mszgajewski.orderservice.dto.OrderRequest;
import com.mszgajewski.orderservice.event.OrderPlacedEvent;
import com.mszgajewski.orderservice.model.Order;
import com.mszgajewski.orderservice.model.OrderLineItems;
import com.mszgajewski.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final Tracer tracer;
    private final KafkaTemplate <String, OrderPlacedEvent> kafkaTemplate;

    public String placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        
       List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);

       List<String> skuCodes = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

       Span inventoryServiceLookup = tracer.nextSpan().name("InventoryServiceLookup");

       try (Tracer.SpanInScope isLookup = tracer.withSpanInScope(inventoryServiceLookup.start())) {
           inventoryServiceLookup.tag("call", "inventory-service");

           InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                   .uri("http://inventory-service/api/inventory",
                           uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                   .retrieve()
                   .bodyToMono(InventoryResponse[].class)
                   .block();

           boolean allProductsInStock = Arrays.stream(inventoryResponseArray)
                   .allMatch(InventoryResponse::isInStock);


           if (allProductsInStock) {
               orderRepository.save(order);
               kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
               return "Zamówienie złożone";
           } else {
               throw new IllegalArgumentException("Produktu nie ma na stanie, prosimy spróbować później");
           }
       }finally {
           inventoryServiceLookup.flush();
       }
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
