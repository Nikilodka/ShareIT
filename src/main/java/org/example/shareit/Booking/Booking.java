package org.example.shareit.Booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name="bookings")
public class Booking {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    int bookingID;


    @JsonFormat(pattern="yyyy-MM-dd")
    LocalDate startDate;
      
    @JsonFormat(pattern="yyyy-MM-dd")
    LocalDate endDate;

    @JsonIgnore
    @Column(name="id_user")
    int userId;

    @NonNull
    @Column(name="id_item")
    int itemId;

    @NonNull
    @Enumerated(EnumType.STRING)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    BookingStatus status;
}
