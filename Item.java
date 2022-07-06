import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
/**
 * Class Item - an item in an adventure game.
 *
 * This class is part of the Prison Break application. 
 * Prison Break is a very simple, text based adventure game.  
 *
 * An "Item" represents an item in the scenery of the game.  
 * It is in a room at all times. It can be picked up and dropped in different rooms by the main player
 * Some items can be used
 * Some items can be given to characters
 * 
 * @author  Michael Seiranian
 * @version 2016.02.29
 */
public class Item
{
    private String itemDescription;
    private int weight;
    public static HashMap<Item, Room> itemLocation = new HashMap<Item, Room>(); //providing public access to static field to be accessed and edited by other classes

    /**
     * Constructor for objects of class Item
     * 
     * @param itemDescription The name of the item
     * @param weight The weight of the item
     */
    public Item(String itemDescription, int weight)
    {
        this.itemDescription = itemDescription;
        this.weight = weight;
        itemLocation = new HashMap<>();
    }

    /**
     * Set item's room
     * 
     * @param item The item we want to place somewhere
     * @param room The room we want to place the item in
     */
    public static void setItemRoom(Item item, Room room) 
    {
        itemLocation.put(item, room);
    }
    
    /**
     * Remove item from room
     * 
     * @param item The item we want to remove from the room
     */
    public static void RemoveItemFromRoom(Item item) 
    {
        itemLocation.remove(item);
    }

    /**
     * Get string of all items in current room
     * @param itemLocation The HashMap which has all items and their locations
     * @param room The current room
     * @return what getItemsString returns
     */
    public static String getItemsByRoom(HashMap<Item, Room> itemLocation, Room room) {
        Set<String> items = new HashSet<String>();
        for (Map.Entry<Item, Room> entry : itemLocation.entrySet()) {
            if (room == entry.getValue()) {
                items.add(entry.getKey().getItemDescription());
            }
        }
        
        return getItemsString(items);
    }
    
    /**
     * Continuation of getItemsByRoom method
     * Converts Set of items to string and returns the string
     * @param items The set of items passed on by getItemsByRoom
     * @return String of all items in current room
     */
    public static String getItemsString(Set<String> items)
    {
        String returnString = "Items in this room:";
        for(String item : items) {
            returnString += " " + item;
        }
        return returnString;
    }
    
    /**
     * Get item's location
     * @param item The item which we want to know the whereabouts of
     * @return The room where the item is
     */
    public static Room getItemLocation(Item item)
    {
        return itemLocation.get(item);    
    }
    
    /**
     * Get item's name
     * @return String of item's name from constructor
     */
    public String getItemDescription()
    {
        return itemDescription;
    }
    
    /**
     * Get weight of item from constructor
     * @return weight integer of item
     */    
    public int getWeight()
    {
        return weight;
    }

}
