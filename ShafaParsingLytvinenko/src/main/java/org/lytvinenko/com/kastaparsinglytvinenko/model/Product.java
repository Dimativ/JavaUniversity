package org.lytvinenko.com.kastaparsinglytvinenko.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String externalId;
    private String name;
    private String price;
    private String oldPrice;
    private String imageUrl;
    private String productUrl;
}
