package org.example.shareit.Item;

import lombok.extern.slf4j.Slf4j;
import org.example.shareit.Booking.Booking;
import org.example.shareit.Booking.BookingRepository;
import org.example.shareit.Booking.Exceptions.WrongBookingUserException;
import org.example.shareit.Item.Exceptions.ItemNotExistsException;
import org.example.shareit.Item.Exceptions.WrongOwnerException;
import org.example.shareit.Item.FeedBack.FeedBack;
import org.example.shareit.Item.FeedBack.FeedBackDTO;
import org.example.shareit.Item.FeedBack.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ItemService {

    ItemRepository itemRepository;
    BookingRepository bookingRepository;
    FeedbackRepository feedbackRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository,FeedbackRepository feedbackRepository) {
        this.itemRepository = itemRepository;
        this.feedbackRepository=feedbackRepository;
    }


    public Item createItem(ItemDTO itemDTO, int ownerId) {
        Item item = itemDTO.convertToItem();
        item.setOwnerId(ownerId);

        Item savedItem = itemRepository.save(item);

        return savedItem;
    }

    @Cacheable(value="items",key="#itemId")
    public ItemDTO getItemById(int itemId) throws ItemNotExistsException {
        log.info("Called getItemById");
        Item item=itemRepository.findById(itemId);
        log.info("getItemById: found item with id: "+itemId);
        if(item==null)
        {
            throw new ItemNotExistsException(itemId);
        }
        return item.convertToDTO();
    }

    public List<ItemDTO> getAllItems() {
        List<Item> items=itemRepository.findAll();
        List<ItemDTO> itemDTOs=new ArrayList<ItemDTO>();
        for(Item item:items) {
            itemDTOs.add(item.convertToDTO());
        }
        return itemDTOs;
    }

    public HashMap<Item, Map.Entry<Map.Entry<LocalDate,LocalDate>,Map.Entry<LocalDate, LocalDate>>> getAllOwnerItems(int ownerId){
        List<Item> items=itemRepository.findAllByOwnerId(ownerId);
        HashMap<Item, Map.Entry<Map.Entry<LocalDate,LocalDate>,Map.Entry<LocalDate, LocalDate>>> itemsWithBookingDates
                = new HashMap<Item, Map.Entry<Map.Entry<LocalDate,LocalDate>,Map.Entry<LocalDate, LocalDate>>>();
        for (int i=0;i<itemsWithBookingDates.size();i++) {

            Map.Entry<LocalDate, LocalDate> lastBookingDate = getLastBookingDate(items.get(i).getId());
            Map.Entry<LocalDate, LocalDate> nextBookingDate = getNearestNextBookingDate(items.get(i).getId());
            Map.Entry<Map.Entry<LocalDate, LocalDate>, Map.Entry<LocalDate, LocalDate>> bookingsDates = Map.entry(lastBookingDate, nextBookingDate);

            itemsWithBookingDates.put(items.get(i), bookingsDates);
        }

        return itemsWithBookingDates;
    }

    @CacheEvict(value="items", key="#itemId")
    public void deleteItemById(int itemId) throws ItemNotExistsException {
        if (itemRepository.existsById(itemId)) {
            itemRepository.deleteById(itemId);
        }
        else
        {
            throw new ItemNotExistsException(itemId);
        }
    }

    @CachePut(value="items",key="#result.id")
    public ItemDTO updateItem(ItemDTO itemDTO, int itemId, int ownerId) throws ItemNotExistsException, WrongOwnerException {
        Item item=itemRepository.findById(itemId);

        if(item==null)
        {
            throw new ItemNotExistsException(itemId);
        }

        log.info("Found item id "+item.getId()+" and owner id "+item.getOwnerId());

        if(ownerId!=item.getOwnerId()){
            throw new WrongOwnerException(itemId,ownerId);
        }

        item=itemDTO.convertToItem();
        item.setOwnerId(ownerId);
        item.setId(itemId);

        itemRepository.save(item);

        return item.convertToDTO();
    }

    public List<ItemSummary> findItemsByText(String text){
        List<ItemSummary> items=null;
        try{
            items=itemRepository.findAvailableItemsByText(text,ItemStatus.AVAILABLE);
        }
        catch(Exception e){
            log.error("Error while finding Items by text "+ e.getMessage());
            throw new RuntimeException();
        }
        return items;
    }

    private Map.Entry<LocalDate, LocalDate> getLastBookingDate(int itemId){
        List<Booking> bookings=bookingRepository.findAllByItemId(itemId);

        LocalDate lastBookingDate=bookings.get(0).getEndDate();
        int lastBookingIndex=0;

        for (Booking booking : bookings) {
            if (booking.getStartDate().isBefore(lastBookingDate) && booking.getEndDate().isBefore(LocalDate.now()))
            {
                lastBookingDate=booking.getStartDate();
                lastBookingIndex=bookings.indexOf(booking);
            }
        }
        return Map.entry(bookings.get(lastBookingIndex).getStartDate(),
                bookings.get(lastBookingIndex).getEndDate());

    }

    private Map.Entry<LocalDate, LocalDate> getNearestNextBookingDate(int itemId){
        List<Booking> bookings=bookingRepository.findAllByItemId(itemId);

        LocalDate nearestBookingDate =bookings.get(0).getStartDate();
        int nearestBookingIndex =0;

        for (Booking booking : bookings) {
            if (booking.getStartDate().isAfter(nearestBookingDate) && booking.getStartDate().isAfter(LocalDate.now()) )
            {
                nearestBookingDate =booking.getStartDate();
                nearestBookingIndex =bookings.indexOf(booking);
            }
        }
        return Map.entry(bookings.get(nearestBookingIndex).getStartDate(),
                bookings.get(nearestBookingIndex).getEndDate());

    }

    @CachePut(value="feedbacksById", key="#result.id")
    @CacheEvict(value="feedbacksByItem", key="#result.itemId")
    public FeedBack addFeedback(int userId, FeedBackDTO feedBackDTO)
    {
        if(!checkUserBookedItem(userId, feedBackDTO.getItemId()))
        {
            throw new WrongBookingUserException(userId,feedBackDTO.getItemId());
        }

        FeedBack feedback=new FeedBack();
        feedback.setUserId(userId);
        feedback.setItemId(feedBackDTO.getItemId());
        feedback.setText(feedBackDTO.getText());

        return feedbackRepository.save(feedback);
    }

    @Cacheable(value="feedbacksByItem", key="#itemId")
    public List<FeedBack> getItemFeedbacks(int itemId) {
        List<FeedBack> feedbacks = feedbackRepository.findAllByItemId(itemId);
        return feedbacks;
    }

    private boolean checkUserBookedItem(int userId, int itemId)
    {
        if (bookingRepository.existsByItemIdAndUserId(itemId,userId))
        {
            return true;
        }
        return false;
    }


}
