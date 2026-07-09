
package com.stylesmart.controller.wardrobe;

import com.stylesmart.dto.response.Wardrobe.ItemResponseDTO;
import com.stylesmart.dto.wardrobe.Category;
import com.stylesmart.service.wardrobe.WardrobeItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
}