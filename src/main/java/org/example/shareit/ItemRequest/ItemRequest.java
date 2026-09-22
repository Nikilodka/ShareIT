package org.example.shareit.ItemRequest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class ItemRequest {
    @JsonIgnore
    private int id;

    @NonNull
    @NotBlank
    private String name;

    private String description;

    @NonNull
    @NotBlank
    private String status;
}
