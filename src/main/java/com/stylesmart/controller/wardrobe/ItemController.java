
package com.stylesmart.controller.wardrobe;

import com.stylesmart.dto.response.Wardrobe.ItemResponseDTO;
import com.stylesmart.dto.wardrobe.Category;
import com.stylesmart.service.wardrobe.WardrobeItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@Tag(name = "Wardrobe", description = "Wardrobe management APIs")
public class ItemController {

    private final WardrobeItemService wardrobeItemService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Add a wardrobe item")
    public ResponseEntity<ItemResponseDTO> addItem(

            @Parameter(description = "Item name")
            @RequestParam String itemName,

            @Parameter(description = "Item category")
            @RequestParam Category category,

            @Parameter(description = "Item color")
            @RequestParam String colorIdentity,

            @Parameter(description = "Item image")
            @RequestParam MultipartFile image
    ) {

        ItemResponseDTO response = wardrobeItemService.addItem(
                itemName,
                category,
                colorIdentity,
                image
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<ItemResponseDTO>> getAllItems(

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "12") int size

    ) {

        return ResponseEntity.ok(
                wardrobeItemService.getAllItems(page, size)
        );

    }
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ItemResponseDTO>> getCategory(

            @PathVariable Category category){

        return ResponseEntity.ok(
                wardrobeItemService.getItemsByCategory(category)
        );

    }

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> categories(){

        return ResponseEntity.ok(
                wardrobeItemService.getCategories()
        );

    }

    @PatchMapping("/{id}/favorite")
    public ResponseEntity<ItemResponseDTO> favorite(

            @PathVariable Long id){

        return ResponseEntity.ok(
                wardrobeItemService.favoriteItem(id)
        );

    }

    @GetMapping("/favorites")
    public ResponseEntity<List<ItemResponseDTO>> favorites(){

        return ResponseEntity.ok(
                wardrobeItemService.getFavoriteItems()
        );

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(

            @PathVariable Long id){

        wardrobeItemService.deleteItem(id);

        return ResponseEntity.noContent().build();

    }

    @GetMapping("/search")
    public ResponseEntity<Page<ItemResponseDTO>> search(

            @RequestParam String keyword,

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "12") int size

    ) {

        return ResponseEntity.ok(
                wardrobeItemService.searchItems(
                        keyword,
                        page,
                        size
                )
        );

    }
}