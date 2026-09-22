package org.example.shareit.Item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class ItemDTO {
    @NotBlank
    @NonNull
    String name;

    @Size(min = 1, max = 255)
    String description;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    String status;

    @JsonIgnore
    int shareCount=0;

    public Item convertToItem() {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setStatus(ItemStatus.AVAILABLE);
        item.setShareCount(shareCount);
        return item;
    }


}
