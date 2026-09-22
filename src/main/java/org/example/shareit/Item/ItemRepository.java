package org.example.shareit.Item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    Item findById(int id);
    List<Item> findByIdIn(List<Integer> ids);
    List<Item> findAll();
    List<Item> findAllByOwnerId(int ownerId);
    Item save(Item item);
    void deleteById(int id);

    @Query("SELECT i.name as name, " +
            "i.description as description, " +
            "i.status as status, " +
            "i.shareCount as shareCount " +
            "FROM Item i " +
            "WHERE (i.name LIKE %:text% OR i.description LIKE %:text%) " +
            "AND i.status=:status")
    List<ItemSummary> findAvailableItemsByText(@Param("text")String name, @Param("status")ItemStatus status);

    @Query("SELECT i.id FROM Item i " +
            "WHERE i.ownerId=:ownerId")
    List<Integer> findItemIdsByOwnerId(@Param("ownerId")int ownerId);

    List<Item> findItemsByIdIn(List<Integer> ids);

    List<Integer> findItemIdsByOwnerIdAndStatus(int ownerId, ItemStatus status);
//    boolean existsByIdAndOwnerId(int ownerId);
}
