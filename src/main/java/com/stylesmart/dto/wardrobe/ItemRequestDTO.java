package com.stylesmart.dto.wardrobe;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class ItemRequestDTO {
        @NotBlank(message = "Item name is required")
        private String itemName;

        @NotNull(message = "Category is required")
        private Category category;

        @NotBlank(message = "Color identity is required")
        private String colorIdentity;

        private String imageUrl;
    }

