package com.stylesmart.service.wardrobe;


import com.stylesmart.dto.response.Wardrobe.ItemResponseDTO;
import com.stylesmart.dto.wardrobe.Category;
import com.stylesmart.dto.wardrobe.ItemRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface WardrobeItemService {

    ItemResponseDTO addItem(
            String itemName,
            Category category,
            String colorIdentity,
            MultipartFile image
    );

    List<ItemResponseDTO> getAllItems();

    Page<ItemResponseDTO> getAllItems(int page, int size);

    List<ItemResponseDTO> getItemsByCategory(Category category);

    List<Category> getCategories();

    ItemResponseDTO favoriteItem(Long id);

    List<ItemResponseDTO> getFavoriteItems();

    void deleteItem(Long id);

    Page<ItemResponseDTO> searchItems(String keyword, int page, int size);

}