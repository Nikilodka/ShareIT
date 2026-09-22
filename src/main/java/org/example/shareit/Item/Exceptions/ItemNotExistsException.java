package org.example.shareit.Item.Exceptions;

public class ItemNotExistsException extends RuntimeException {
    public ItemNotExistsException(int itemId) {
        super("Item with id " + itemId + " does not exist");
    }
}
