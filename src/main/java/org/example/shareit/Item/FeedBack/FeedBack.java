package org.example.shareit.Item.FeedBack;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="feedbacks")
public class FeedBack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(length=500,name="comment")
    String text;

    @NonNull
    @Column(name="id_user")
    private int userId;

    @NonNull
    @Column(name="id_item")
    private int itemId;

}
