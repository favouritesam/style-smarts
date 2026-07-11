package com.stylesmart.repository.Wardrobe;

import com.stylesmart.dto.response.Wardrobe.ItemResponseDTO;
import com.stylesmart.dto.wardrobe.Category;
import com.stylesmart.entity.WardrobeItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<WardrobeItem, Long> {

    Page<WardrobeItem> findAll(Pageable pageable);

    Page<WardrobeItem> findByItemNameContainingIgnoreCase(String keyword, Pageable pageable);
    List<WardrobeItem> findByCategory(Category category);

    List<WardrobeItem> findByFavoriteTrue();
}
