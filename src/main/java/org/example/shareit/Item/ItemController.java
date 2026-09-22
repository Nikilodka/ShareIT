package org.example.shareit.Item;


import lombok.extern.slf4j.Slf4j;
import org.example.shareit.Item.Exceptions.ItemNotExistsException;
import org.example.shareit.Item.Exceptions.WrongOwnerException;
import org.example.shareit.Item.FeedBack.FeedBack;
import org.example.shareit.Item.FeedBack.FeedBackDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Component
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping("/items")
    public ResponseEntity<?> createItem(@RequestHeader("X-Sharer-User-Id") int ownerId,
                                     @RequestBody ItemDTO itemDto) {
        try{
            Item item=itemService.createItem(itemDto, ownerId);
            log.info("Item created: " + item.getId());
            return new ResponseEntity<>(item, HttpStatus.CREATED);
        }
        catch(Exception e){
            log.error("Cannot create item: "+ e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/items/{itemId}")
    public ResponseEntity<?> updateItem(@RequestHeader("X-Sharer-User-Id") int ownerId,
                           @RequestBody ItemDTO itemDto,
                           @PathVariable int itemId) {
        try{
            Item item=itemService.updateItem(itemDto,itemId,ownerId);
            log.info("Item updated: " + item.getId());
            return new ResponseEntity<>(item, HttpStatus.OK);
        }
        catch(ItemNotExistsException e){
            log.error("Cannot update item: "+ e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        catch(WrongOwnerException e){
            log.error("Cannot update item: "+ e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/items/{itemId}")
    public ResponseEntity<?> getItem(@PathVariable int itemId) {
        try{
           ItemDTO itemDto=itemService.getItemById(itemId);
           log.info("Found item with id "+itemId);
           return new ResponseEntity<>(itemDto, HttpStatus.OK);
        }
        catch(ItemNotExistsException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/items")
    public ResponseEntity<?> getAllOwnerItems(@RequestHeader("X-Sharer-User-Id") int sharerId) { // добавить вывод дат бронирования
        try{
            HashMap<Item, Map.Entry<Map.Entry<LocalDate,LocalDate>,Map.Entry<LocalDate, LocalDate>>> ownerItems
                    =itemService.getAllOwnerItems(sharerId);
            log.info("Found "+ownerItems.size()+" items");
            return new ResponseEntity<>(ownerItems, HttpStatus.OK);
        }
        catch(Exception e){
            log.error("Cannot get owner items with id "+sharerId+": "+ e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/items/search")
    public ResponseEntity<?> searchItemByText(@RequestParam String searchText) {
        log.info("Searching for "+searchText);
        try{

            List<ItemSummary> items=itemService.findItemsByText(searchText);
            log.info("Found "+items.size()+" items");
            return new ResponseEntity<>(items, HttpStatus.OK);
        }
        catch(Exception e){
            log.error("Cannot get items with text "+searchText+": "+ e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/items/feedback/{itemId}")
    public ResponseEntity<?> getItemFeedbacks(@PathVariable int itemId) {
        log.info("Searching feedbacks for "+itemId);
        try{
            List<FeedBack> itemFeedbacks=itemService.getItemFeedbacks(itemId);
            log.info("Found "+itemFeedbacks.size()+" items");
            return new ResponseEntity<>(itemFeedbacks, HttpStatus.OK);
        }
        catch(Exception e){
            log.error("Cannot get item feedbacks with id "+itemId+": "+ e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/items/feedback")
    public ResponseEntity<?> createFeedback(@RequestHeader("X-User-Id") int userId,
                                            @RequestBody FeedBackDTO feedBackDTO) {
        log.info("Creating feedback for "+feedBackDTO);
        try{
            FeedBack feedBack=new FeedBack();
            feedBack.setUserId(userId);
            feedBack.setItemId(feedBackDTO.getItemId());
            feedBack.setText(feedBackDTO.getText());
            itemService.addFeedback(userId,feedBackDTO);
            log.info("Feedback created: " + feedBackDTO.getId());
            return new ResponseEntity<>(feedBack, HttpStatus.CREATED);
        }
        catch (Exception e)
        {
            log.error("Cannot create feedback: "+ e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }






}
