package com.stylesmart.repository.Wardrobe;

import com.stylesmart.entity.WardrobeItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<WardrobeItem, Long> {
}
