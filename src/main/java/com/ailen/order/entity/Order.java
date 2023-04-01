package com.ailen.order.entity;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Order {
    private Long id;
    private Long userId;
    private LocalDateTime createTime;
}