package org.example.shareit.Item.Exceptions;

public class WrongOwnerException extends RuntimeException {
    public WrongOwnerException(int itemId, int ownerId) {
        super("User with id " + ownerId + " is not an owner of item with id " + itemId);
    }
}
