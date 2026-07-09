package com.stylesmart.dto.response.Wardrobe;

import com.stylesmart.dto.wardrobe.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponseDTO  {
    private Long id;
    private String itemName;
    private Category category;
    private String colorIdentity;
    private String imageUrl;
}
