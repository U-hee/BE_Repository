package com.imfine.ngs.order.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Embeddable
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor // Required for JPA Embeddable
@EqualsAndHashCode
public class Game {
    private String name;
    private long price;
}
