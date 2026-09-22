package org.example.shareit.User;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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
@Entity
@Table(name="users")
public class User {
    @NonNull
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id=0;

    @NonNull
    @Column(nullable = false)
    private String name;

    @Email(message="Неккоректный e-mail!")
    @Column(nullable = false,unique=true)
    private String email;

    @NonNull
    @NotBlank
    @Length(min=8,max=20,message = "Минимальная длина логина - 8 символов, максимальная - 20.")
    @Pattern(regexp ="\\S+",message="Логин не должен содержать пробелы")
    @Column(nullable = false)
    private String login;

    @NonNull
    @NotBlank
    @Length(min=6,max=16, message = "Длина пароля должна быть от 6 до 16 символов.")
    @Pattern(regexp ="\\S+",message="Пароль не должен содержать пробелы")
    @Column(nullable = false)
    private String password;

    @BirthdayDateRange
    @Column(nullable = false)
    private LocalDate birthdayDate;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY,mappedBy="ownerId",orphanRemoval=true)
    private Set<Item> userItems;

    public User() {
        userItems = new HashSet<>();
    }

    public UserDTO convertToDTO() {
        UserDTO userDTO = new UserDTO();

        userDTO.setId(id);
        userDTO.setName(name);
        userDTO.setEmail(email);
        userDTO.setBirthdayDate(birthdayDate);
        userDTO.setUserItems(userItems);
        return userDTO;
    }
}
