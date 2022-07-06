import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
/**
 * Class Room - a room in an adventure game.
 *
 * This class is part of the Prison Break application. 
 * Prison Break is a very simple, text based adventure game.  
 *
 * A "Room" represents one location in the scenery of the game.  It is 
 * connected to other rooms via exits. For each existing exit, the room 
 * stores a reference to the neighboring room.
 * 
 * @author  Michael Kölling, David J. Barnes and Michael Seiranian
 * @version 2016.02.29
 */

public class Room 
{
    private String description;
    private boolean locked;
    public HashMap<String, Room> exits;        // stores exits of this room.
    public static HashMap<Room, Item> roomUnlocker; // stores item that unlocks the room
    public static Set<Room> allRooms = new HashSet<Room>();
    private static Set<Room> unlockedRooms = new HashSet<Room>();

    /**
     * Create a room described "description". Initially, it has
     * no exits. "description" is something like "a kitchen" or
     * "an open court yard".
     * @param description The room's description.
     * @param locked If the room is locked or not.
     */
    public Room(String description, boolean locked) 
    {
        this.description = description;
        this.locked = locked;
        exits = new HashMap<>();
        roomUnlocker = new HashMap<>();
    }

    /**
     * Define an exit from this room.
     * @param direction The direction of the exit.
     * @param neighbour  The room to which the exit leads.
     */
    public void setExit(String direction, Room neighbour) 
    {
        exits.put(direction, neighbour);
    }

    /**
     * @return The short description of the room
     * (the one that was defined in the constructor).
     */
    public String getShortDescription()
    {
        return description;
    }

    /**
     * @return true if room is locked
     */
    public boolean getLocked()
    {
        return locked;
    }
    
    /**
     * Unlocks room
     */
    public void Unlock()
    {
        locked = false;
    }

    /**
     * Return a string describing the room's exits, for example
     * "Exits: north west".
     * @return Details of the room's exits.
     */
    public String getExitString()
    {
        String returnString = "Exits:";
        Set<String> keys = exits.keySet();
        for(String exit : keys) {
            returnString += " " + exit;
        }
        return returnString;
    }

    /**
     * Return the room that is reached if we go from this room in direction
     * "direction". If there is no room in that direction, return null.
     * @param direction The exit's direction.
     * @return The room in the given direction.
     */
    public Room getExit(String direction) 
    {
        return exits.get(direction);
    }
    
    /**
     * Get the item that unlocks the room
     * @param room The room that we want to know the unlocker of
     * @return the item that unlocks the room
     */
    public static Item getRoomUnlocker(Room room)
    {
        return roomUnlocker.get(room);    
    }
    
    /**
     * Set the item that unlocks the room
     * @param room The room that the item unlocks
     * @param item The item that unlocks the room
     */
    public static void setRoomUnlocker(Room room, Item item) 
    {
        roomUnlocker.put(room, item);
    }
    
    /**
     * Remove room and item pair since the unlock has occured
     * @param room The room we want to remove
     */
    public static void removeRoomUnlocker(Room room) 
    {
        roomUnlocker.remove(room);
    }
    
    /**
     * Get set of all unlocked rooms
     * @return unlockedRooms The Set that contains all unlocked rooms
     */
    public static Set<Room> getUnlockedRoomsSet()
    {
        for (Room room: allRooms) {
            if (room.getLocked() == false) {
                unlockedRooms.add(room);
            }
        }
        return unlockedRooms;
    }
    
    /**
     * Get array of all unlocked rooms
     * @return unlockedRoomsArray The Array that contains all unlocked rooms
     */
    public static Room[] getUnlockedRoomsArray()
    {
        Room[] unlockedRoomsArray = getUnlockedRoomsSet().toArray(new Room[getUnlockedRoomsSet().size()]);
        return unlockedRoomsArray;
    }
}

