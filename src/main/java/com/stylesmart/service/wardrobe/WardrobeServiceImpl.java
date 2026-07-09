package com.stylesmart.service.wardrobe;

import com.stylesmart.dto.response.Wardrobe.ItemResponseDTO;
import com.stylesmart.dto.wardrobe.Category;
import com.stylesmart.dto.wardrobe.ItemRequestDTO;
import com.stylesmart.entity.WardrobeItem;
import com.stylesmart.repository.Wardrobe.ItemRepository;
import com.stylesmart.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class WardrobeServiceImpl implements WardrobeItemService {

    private final ItemRepository itemRepository;
    private final CloudinaryService cloudinaryService;
    @Override
    public ItemResponseDTO addItem(

            String itemName,
            Category category,
            String colorIdentity,
            MultipartFile image

    ) {

        String imageUrl = cloudinaryService.uploadImage(image);

        WardrobeItem item = WardrobeItem.builder()
                .itemName(itemName)
                .category(category)
                .colorIdentity(colorIdentity)
                .imageUrl(imageUrl)
                .build();

        WardrobeItem saved = itemRepository.save(item);

        return ItemResponseDTO.builder()
                .id(saved.getId())
                .itemName(saved.getItemName())
                .category(saved.getCategory())
                .colorIdentity(saved.getColorIdentity())
                .imageUrl(saved.getImageUrl())
                .build();
    }
}