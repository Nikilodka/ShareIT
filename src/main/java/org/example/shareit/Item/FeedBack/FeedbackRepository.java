package org.example.shareit.Item.FeedBack;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<FeedBack,Integer> {
    List<FeedBack> findAllByItemId(int itemId);
    FeedBack save(FeedBack feedBack);
}
