package com.stylesmart.service.wardrobe;

import com.stylesmart.dto.response.Wardrobe.ItemResponseDTO;
import com.stylesmart.dto.wardrobe.Category;
import com.stylesmart.entity.WardrobeItem;
import com.stylesmart.repository.Wardrobe.ItemRepository;
import com.stylesmart.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

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

    @Override
    public List<ItemResponseDTO> getAllItems() {
        return List.of();
    }

    private ItemResponseDTO mapToResponse(WardrobeItem item) {

        return ItemResponseDTO.builder()
                .id(item.getId())
                .itemName(item.getItemName())
                .category(item.getCategory())
                .colorIdentity(item.getColorIdentity())
                .imageUrl(item.getImageUrl())
                .favourite(item.getFavorite())
                .build();
    }

    @Override
    public Page<ItemResponseDTO> getAllItems(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return itemRepository.findAll(pageable)
                .map(this::mapToResponse);

    }

    @Override
    public List<ItemResponseDTO> getItemsByCategory(Category category) {

        return itemRepository.findByCategory(category)

                .stream()

                .map(this::mapToResponse)

                .toList();
    }
    @Override
    public List<Category> getCategories() {

        return Arrays.asList(Category.values());

    }

    @Override
    public ItemResponseDTO favoriteItem(Long id) {

        WardrobeItem item = itemRepository.findById(id)

                .orElseThrow(() -> new RuntimeException("Item not found"));

        item.setFavorite(!item.getFavorite());

        itemRepository.save(item);

        return mapToResponse(item);
    }


    @Override
    public List<ItemResponseDTO> getFavoriteItems() {

        return itemRepository.findByFavoriteTrue()

                .stream()

                .map(this::mapToResponse)

                .toList();
    }

    @Override
    public void deleteItem(Long id) {

        itemRepository.deleteById(id);

    }

    @Override
    public Page<ItemResponseDTO> searchItems(
            String keyword,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        return itemRepository
                .findByItemNameContainingIgnoreCase(keyword, pageable)
                .map(this::mapToResponse);

    }
}