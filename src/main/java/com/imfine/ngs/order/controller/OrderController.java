package com.imfine.ngs.order.controller;

import com.imfine.ngs._global.config.security.jwt.JwtUserPrincipal;
import com.imfine.ngs.order.dto.OrderDetailsResponseDto;
import com.imfine.ngs.order.dto.OrderResponseDto;
import com.imfine.ngs.order.dto.mapper.OrderMapper;
import com.imfine.ngs.order.entity.Order;
import com.imfine.ngs.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderMapper mapper;
    private final OrderService orderService;

    @GetMapping("/cart")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponseDto> getCart(@AuthenticationPrincipal JwtUserPrincipal principal) {
        Long userId = principal.getUserId();
        Order cart = orderService.getOrCreateCart(userId);
        return ResponseEntity.ok(mapper.toOrderResponseDto(cart));
    }

    @PostMapping("/cart/add")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponseDto> addGameToCart(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @RequestParam Long gameId) {
        Long userId = principal.getUserId();
        Order updatedCart = orderService.addGameToCart(userId, gameId);
        return ResponseEntity.ok(mapper.toOrderResponseDto(updatedCart));
    }

    @DeleteMapping("/cart/remove/{gameId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponseDto> removeGameFromCart(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable Long gameId) {
        Long userId = principal.getUserId();
        Order updatedCart = orderService.removeGameFromCart(userId, gameId);
        return ResponseEntity.ok(mapper.toOrderResponseDto(updatedCart));
    }

    @DeleteMapping("/cart")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponseDto> clearCart(@AuthenticationPrincipal JwtUserPrincipal principal) {
        Long userId = principal.getUserId();
        Order clearedCart = orderService.clearCart(userId);
        return ResponseEntity.ok(mapper.toOrderResponseDto(clearedCart));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OrderResponseDto>> getMyOrders(@AuthenticationPrincipal JwtUserPrincipal principal) {
        Long userId = principal.getUserId();
        List<Order> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(
                orders
                        .stream()
                        .map(mapper::toOrderResponseDto)
                        .toList()
        );
    }
}