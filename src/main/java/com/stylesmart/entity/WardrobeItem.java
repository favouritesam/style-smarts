package com.stylesmart.entity;

import com.stylesmart.dto.wardrobe.Category;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wardrobe_items")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WardrobeItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String itemName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private String colorIdentity;

    @Column(name = "image_url")
    private String imageUrl;
}
