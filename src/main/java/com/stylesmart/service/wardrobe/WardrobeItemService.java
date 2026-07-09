package com.stylesmart.service.wardrobe;


import com.stylesmart.dto.response.Wardrobe.ItemResponseDTO;
import com.stylesmart.dto.wardrobe.Category;
import com.stylesmart.dto.wardrobe.ItemRequestDTO;
import org.springframework.web.multipart.MultipartFile;

public interface WardrobeItemService {

    ItemResponseDTO addItem(
            String itemName,
            Category category,
            String colorIdentity,
            MultipartFile image
    );;

}