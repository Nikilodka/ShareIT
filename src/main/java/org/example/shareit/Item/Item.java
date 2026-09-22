package org.example.shareit.Item;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.example.shareit.User.User;

@Getter
@Setter
@Entity
@Table(name="items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @NotBlank
    @NonNull
    @Column(length=50)
    String name;

    @Size(min = 1, max = 255)
    @Column(length=255)
    String description;

    @NonNull
    @Column(name="id_owner")
    private int ownerId;

    @Enumerated(EnumType.STRING)
    private ItemStatus status;

    @Column(name="share_count")
    int shareCount;

    public ItemDTO convertToDTO()
    {
        ItemDTO dto = new ItemDTO();

        dto.setName(name);
        dto.setDescription(description);
        dto.setShareCount(shareCount);
        dto.setStatus(status.toString());
        return dto;
    }

}
