import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
/**
 * Class Character - a character in an adventure game.
 *
 * This class is part of the Prison Break application. 
 * Prison Break is a very simple, text based adventure game.  
 *
 * A "Character" represents a secondary character (not the main character) in the game.  
 * It is in a room at all times. Each character has a name and response to the main player.
 * 
 * @author  Michael Seiranian
 * @version 2016.02.29
 */
public class Character
{
    private String characterDescription;
    private String response;
    public static HashMap<Character, Room> characterLocation;

    /**
     * Constructor for objects of class Character
     * @param characterDescription The name of the character
     * @param response The default response character gives to main player
     */
    public Character(String characterDescription, String response)
    {
        this.characterDescription = characterDescription;
        this.response = response;
        characterLocation = new HashMap<>();
    }

    /**
     * Place characters in rooms
     * @param character The character which we want to place in a room
     * @param room The room where we want to place the character
     */
    public static void setCharacterRoom(Character character, Room room) 
    {
        characterLocation.put(character, room);
    }

    /**
     * Get string of all characters in current room
     * @param characterLocation The HashMap which has all characters and their locations
     * @param room The current room
     * @return what getCharactersString returns
     */
    public static String getCharactersByRoom(HashMap<Character, Room> characterLocation, Room room) {
        Set<String> characters = new HashSet<String>();
        for (Map.Entry<Character, Room> entry : characterLocation.entrySet()) {
            if (room == entry.getValue()) {
                characters.add(entry.getKey().getCharacterDescription());
            }
        }

        return getCharactersString(characters);
    }

    /**
     * Continuation of getCharactersByRoom method
     * Converts Set of characters to string and returns the string
     * @param characters The set of characters passed on by getCharactersByRoom
     * @return String of all characters in current room
     */
    public static String getCharactersString(Set<String> characters)
    {
        String returnString = "Characters in this room:";
        for(String character : characters) {
            returnString += " " + character;
        }
        return returnString;
    }

    /**
     * Get character location
     * @param character The character that we want to know the location of
     * @return the Room of the character
     */
    public static Room getCharacterLocation(Character character)
    {
        return characterLocation.get(character);    
    }

    /**
     * Get character's response
     * @return the response of the character
     */
    public String getResponse()
    {
        return response;
    }
    
    /**
     * Change character's response
     * @param newResponse The new response of the character
     */
    public void setResponse(String newResponse)
    {
        response = newResponse;
    }

    /**
     * Get character description
     * @return name of character
     */    
    public String getCharacterDescription()
    {
        return characterDescription;
    }

    /**
     * Move characters around if the character is not guard
     */
    public static void moveCharacters() 
    {
        for (Character character : characterLocation.keySet()) {
            if (character.getCharacterDescription().equals("guard") == false) {
                Random random = new Random();
                int randomNumber = random.nextInt(2);
                if (randomNumber == 0) {    //giving it a 50% chance for the character to move, to make their movement more random and independent
                    Room currentRoom = getCharacterLocation(character);     //find where character is
                    Random random1 = new Random();
                    Object[] roomsObj = currentRoom.exits.values().toArray();       //get its locations exits and convert to array
                    Room[] rooms = new Room[roomsObj.length];
                    System.arraycopy(roomsObj, 0, rooms, 0, roomsObj.length);       //copy object array into Room array
                    int randNum = random1.nextInt(rooms.length);
                    Room nextRoom = rooms[randNum];     //pick random room from the exits
                    Object[] nextRoomsObj = nextRoom.exits.values().toArray();  //get nextRoom's exits  
                    if (!nextRoom.getLocked() && nextRoomsObj.length>0) {       //if nextRoom is not locked and nextRoom has more than 0 exits, the character can proceed with moving to the next room
                        characterLocation.put(character, nextRoom);
                    }
                }
            }
        }
    }
}
