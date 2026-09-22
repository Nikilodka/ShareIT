package org.example.shareit.User;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.example.shareit.Item.Item;
import org.example.shareit.User.UserValidators.BirthdayDateRange;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class UserDTO {
    @NonNull
    @JsonIgnore
    private int id=0;

    @NonNull
    private String name;

    @Email(message="Неккоректный e-mail!")
    private String email;

    @BirthdayDateRange
    private LocalDate birthdayDate;

    private Set<Item> userItems;

}
