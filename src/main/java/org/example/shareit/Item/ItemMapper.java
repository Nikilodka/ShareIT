package org.example.shareit.Item;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemMapper implements RowMapper<Item> {
    public Item mapRow(ResultSet rs, int rowNum) throws SQLException {
        Item item = new Item();
        item.setId(rs.getInt("id"));
        item.setName(rs.getString("name"));
        item.setDescription(rs.getString("description"));
//        item.setOwnerId(rs.getInt("owner_id"));
//        item.setStatus(rs.getString("status"));
        item.setShareCount(rs.getInt("count"));

        return item;
    }

}
