import java.util.*;
/**
 *  This is the main class of the Prison Break game. 
 * 
 *  To play this game, create an instance of this class and call the "play"
 *  method.
 * 
 *  This main class creates and initialises all the others: it creates all
 *  rooms, items, characters, creates the parser and starts the game.  It also evaluates and
 *  executes the commands that the parser returns.
 * 
 * @author  Michael Kölling, David J. Barnes and Michael Seiranian
 * @version 2016.02.29
 */

public class Game 
{
    private Parser parser;
    private Room cell, corridor, guardoffice, staircase, showers, canteen, kitchen, vents, exit, outside, magicRoom, currentRoom, nextRoom;
    private final int maxWeight = 10;        //maximum weight of objects the character can carry
    private Set<Item> itemsHolding = new HashSet<Item>();   //stores items currently holding
    private ArrayList<Room> roomHistory = new ArrayList<Room>();    //stores visited rooms
    private Item key, stool, wrench, bed, matches, pliers, table;
    private Character neighbour, guard, chef;
    public HashMap<Character, Room> characterLocation = Character.characterLocation;
    private boolean fire = false;
    private boolean finished = false;
    private boolean distractionText = true;

    /**
     * Create the game, initialise its internal map, items, players, their starting locations and which items unlock which rooms.
     */
    public Game() 
    {
        createRooms();
        addRoomsToSet();    //a set storing all rooms
        initialiseRoomExits();      //sets the exits each room has
        createItems();
        setItemLocations();     //sets items' initial locations
        setRoomUnlockers();     //sets which items unlock which rooms
        createCharacters();
        setCharacterLocations();    //sets items' initial locations
        parser = new Parser();
        
        play();
    }

    /**
     * Print out the main terminal for the player.
     */
    private void printWelcome()
    {
        System.out.print('\u000C');
        System.out.println("Welcome to the Prison Break!");
        System.out.println("Prison Break is a new, boring adventure game.");
        System.out.println("Type 'help' if you need help.");
        System.out.println();
        System.out.println("You are " + currentRoom.getShortDescription());
        System.out.println(Item.getItemsByRoom(Item.itemLocation, currentRoom));
        System.out.println(getItemsHoldingString());
        System.out.println();
        System.out.println(Character.getCharactersByRoom(Character.characterLocation, currentRoom));
        System.out.println();
        System.out.println("Total weight of items carrying: "+getTotalWeightOfItemsHolding());
        System.out.println("Carrying weight limit: "+maxWeight);
        System.out.println();
        System.out.println(currentRoom.getExitString());
        System.out.println();
    }

    /**
     * Create all the rooms.
     *
     */
    private void createRooms()
    {
        // create the rooms
        cell = new Room("in your prison cell", false);
        corridor = new Room("in the corridor", false);
        guardoffice = new Room("in the guard's office", true);
        staircase = new Room("on the staircase", false);
        showers = new Room("in the shower room", false);
        canteen = new Room("in the canteen", false);
        kitchen = new Room("in the kitchen", false);
        vents = new Room("in the vents", true);
        exit = new Room("at the exit", true);
        outside = new Room("outside", true);
        magicRoom = new Room("in the magic room", false);

        currentRoom = cell;  // start game in the cell
        roomHistory.add(currentRoom);    //keeps track of visited rooms for the "back" command
    }

    /**
     * Add rooms to set "allRooms".
     * 
     */
    private void addRoomsToSet() {
        Room.allRooms.add(cell);
        Room.allRooms.add(corridor);
        Room.allRooms.add(guardoffice);
        Room.allRooms.add(staircase);
        Room.allRooms.add(showers);
        Room.allRooms.add(canteen);
        Room.allRooms.add(kitchen);
        Room.allRooms.add(vents);
        Room.allRooms.add(exit);
    }

    /**
     * Link room exits together.
     *
     */
    private void initialiseRoomExits() {
        // initialise room exits
        cell.setExit("east", corridor);
        corridor.setExit("west", cell);
        corridor.setExit("south", guardoffice);
        corridor.setExit("north", staircase);
        guardoffice.setExit("north", corridor);
        staircase.setExit("north", showers);
        staircase.setExit("down", canteen);
        staircase.setExit("south", corridor);
        showers.setExit("south", staircase);
        showers.setExit("up", vents);
        canteen.setExit("up", staircase);
        canteen.setExit("west", kitchen);
        kitchen.setExit("east", canteen);
        kitchen.setExit("north", magicRoom);
        vents.setExit("down", showers);
        vents.setExit("north", exit);
        exit.setExit("south", vents);
        exit.setExit("north", outside);
    }

    /**
     *  Create the items, sets their names and weights
     *  
     */
    private void createItems() {
        // create the items
        key = new Item("key", 4);
        stool = new Item("stool", 4);
        wrench = new Item("wrench", 4);
        bed = new Item("bed", 200);
        matches = new Item("matches", 1);
        pliers = new Item("pliers", 4);
        table = new Item("table", 100);
    }

    /**
     * Set the starting locations of the items.
     * 
     */
    private void setItemLocations() {
        // initialise item locations
        Item.setItemRoom(key, staircase);
        Item.setItemRoom(stool, canteen);
        Item.setItemRoom(wrench, cell);
        Item.setItemRoom(bed, cell);
        Item.setItemRoom(matches, kitchen);
        Item.setItemRoom(pliers, guardoffice);
        Item.setItemRoom(table, canteen);
    }

    /**
     * Set which item unlocks which room
     */
    private void setRoomUnlockers() {
        // specify which items can unlock which rooms
        Room.setRoomUnlocker(guardoffice, key);
        Room.setRoomUnlocker(vents, wrench);
        Room.setRoomUnlocker(outside, pliers);
    }

    /**
     * Create the characters. Set their name and default response
     */
    private void createCharacters() {
        neighbour = new Character("neighbour", "Alright, I will help you. Just promise me you will take care of my daughter when you're out.");
        guard = new Character("guard", "I don't want to talk to you.");
        chef = new Character("chef", "Leave me alone.");
    }

    /**
     * Set character starting locations
     */
    private void setCharacterLocations() {
        Character.setCharacterRoom(neighbour, corridor);
        Character.setCharacterRoom(guard, exit);
        Character.setCharacterRoom(chef, kitchen);
    }

    /**
     *  Main play routine.  Loops until end of play.
     */
    public void play() 
    {            
        printWelcome();

        // Enter the main command loop.  Here we repeatedly read commands and
        // execute them until the game is over.

        while (! finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }

        System.out.println("Thank you for playing.  Good bye.");
    }

    /**
     * Given a command, process (that is: execute) the command.
     * @param command The command to be processed.
     * @return true If the command ends the game, false otherwise.
     */
    private boolean processCommand(Command command) 
    {
        boolean wantToQuit = false;

        if(command.isUnknown()) {
            printWelcome();
            System.out.println("I don't know what you mean...");
            return false;
        }

        String commandWord = command.getCommandWord();
        if (commandWord.equals("help")) {
            printHelp();
        }
        else if (commandWord.equals("go")) {
            goRoom(command);
        }
        else if (commandWord.equals("take")) {
            takeItem(command);
        }
        else if (commandWord.equals("drop")) {
            dropItem(command);
        }
        else if (commandWord.equals("back")) {
            back(command);
        }
        else if (commandWord.equals("use")) {
            wantToQuit = useItem(command);
        }
        else if (commandWord.equals("talk")) {
            if (command.getSecondWord().equals("to")) {
                talkToCharacter(command);
            }
        }
        else if (commandWord.equals("give")) {
            giveItem(command);
        }
        else if (commandWord.equals("quit")) {
            wantToQuit = quit(command);
        }
        // else command not recognised.
        return wantToQuit;
    }

    // implementations of user commands:

    /**
     * Print out some help information.
     * Here we print some stupid, cryptic message and a list of the 
     * command words.
     */
    private void printHelp() 
    {
        printWelcome();
        System.out.println("You are trying to escape prison.");
        System.out.println("You wander around in the prison.");
        System.out.println();
        System.out.println("Your command words are:");
        parser.showCommands();
    }

    /** 
     * Try to go in one direction. If there is an exit, enter the new
     * room, otherwise print an error message.
     * @param command The command to be processed.
     */
    private void goRoom(Command command) 
    {
        if(!command.hasSecondWord()) {
            printWelcome();
            // if there is no second word, we don't know where to go...
            System.out.println("Go where?");
            return;
        }

        String direction = command.getSecondWord();

        // Try to leave current room.
        nextRoom = currentRoom.getExit(direction);

        if (nextRoom == null) {
            printWelcome();
            System.out.println("There is no door!");
        }
        else if (nextRoom.getLocked() && itemsHolding.contains(Room.getRoomUnlocker(nextRoom))) {
            printWelcome();
            System.out.println("Use the 'use' command, followed by the item name to get " + nextRoom.getShortDescription());
        }
        else if (nextRoom.getLocked() && nextRoom == exit) {
            printWelcome();
            System.out.println("Can't go there! The guards are there, they must be distracted and leave the prison exit");
        }
        else if (!nextRoom.getLocked() && nextRoom == outside) {
            finished = true;
            printWelcome();
            System.out.println("Congratulations! You escaped!");
        }
        else if (nextRoom.getLocked()) {
            printWelcome();
            System.out.println("Can't get in! You need some item to get " + nextRoom.getShortDescription());
        }
        else if (nextRoom == magicRoom) {
            roomHistory.add(currentRoom);
            Random random = new Random();
            int randomNumber = random.nextInt(Room.getUnlockedRoomsSet().size());
            currentRoom = Room.getUnlockedRoomsArray()[randomNumber];
            printWelcome();
            System.out.println("You entered the magic transporter room.");
            System.out.println("You have been teleported and are now " + currentRoom.getShortDescription()+"!");
        }
        else if (currentRoom == showers && nextRoom == vents && Item.getItemLocation(stool) != showers) {
            printWelcome();
            System.out.println("Too high! You need the stool in this room to be able to climb into the vents.");
        }
        else {
            currentRoom = nextRoom;
            roomHistory.add(nextRoom);
            Character.moveCharacters();
            printWelcome();
            if (fire==true) {
                System.out.println("Your neighbour has started a fire, the guards are distracted, you can escape!");
                fire = false;
            }
        }
    }

    /** 
     * Try to take item. If there such item in current room, take the item,
     * otherwise print an error message.
     * @param command The command to be processed.
     */
    private void takeItem(Command command) 
    {
        if(!command.hasSecondWord()) {
            printWelcome();
            // if there is no second word, we don't know what to take...
            System.out.println("Take what?");
            return;
        }

        String itemText = command.getSecondWord();
        Item itemTaken = null;
        for(Item item: Item.itemLocation.keySet()) {        //shows the items that are in this room
            if(item.getItemDescription().equals(itemText))      //finds the item that matches user's input
            {
                itemTaken = item;
            }
        }

        if (getItemsHoldingString().contains(itemText)) {
            printWelcome();
            System.out.println("You are already carrying this item.");
            return;
        }

        if (itemTaken == null) {
            printWelcome();
            System.out.println("There is no such item!");
            return;
        }

        if (Item.getItemLocation(itemTaken) != currentRoom) {
            printWelcome();
            System.out.println("There is no such item in this room!");
        }
        else if (itemTaken.getWeight()>maxWeight) {
            printWelcome();
            System.out.println("You can't carry this item. It is too heavy.");
        }
        else if (getTotalWeightOfItemsHolding() + itemTaken.getWeight() > maxWeight) {
            printWelcome();
            System.out.println("Weight limit exceeded. Can't take item");
        }else{
            itemsHolding.add(itemTaken);
            Item.RemoveItemFromRoom(itemTaken);
            printWelcome();
            System.out.println(itemTaken.getItemDescription() + " taken");
        }
    }

    /** 
     * @return string of items currently holding
     */
    private String getItemsHoldingString()
    {
        String returnString = "Items currently holding:";
        for(Item item : itemsHolding) {
            returnString += " " + item.getItemDescription();
        }
        return returnString;
    }

    /** 
     * @return total weight of items holding
     */
    private int getTotalWeightOfItemsHolding()
    {
        int totalWeight=0;
        for (Item item: itemsHolding) {
            totalWeight += item.getWeight();
        }
        return totalWeight;
    }

    /** 
     * Try to drop item. If you are holding such item, drop the item,
     * otherwise print an error message.
     * @param command The command to be processed.
     */
    private void dropItem(Command command)
    {
        if(!command.hasSecondWord()) {
            printWelcome();
            // if there is no second word, we don't know what to drop...
            System.out.println("Drop what?");
            return;
        }

        String itemText = command.getSecondWord();

        Item itemDropping = null; //The item that we want to drop
        for(Item item: itemsHolding) {
            if(item.getItemDescription().equals(itemText))
            {
                itemDropping = item;
            }
        }

        if (!itemsHolding.contains(itemDropping)) {
            printWelcome();
            System.out.println("There is no such item in your inventory!");
        }
        else{
            itemsHolding.remove(itemDropping);
            Item.setItemRoom(itemDropping, currentRoom);
            printWelcome();
            System.out.println(itemDropping.getItemDescription() + " dropped " + currentRoom.getShortDescription());
        }
    }

    /** 
     * Use item. If the item exists in current items holding and it is the 
     * correct item the unlocks the room, unlock the room and go in it,
     * otherwise print an error message.
     * @param command The command to be processed.
     * @return return true when game must be finished
     */
    private boolean useItem(Command command)
    {
        if(!command.hasSecondWord()) {
            printWelcome();
            // if there is no second word, we don't know what to drop...
            System.out.println("Use what?");
            return false;
        }

        String itemText = command.getSecondWord();

        Item itemUsing = null; //The item that we wnat to drop
        for(Item item: itemsHolding) {
            if(item.getItemDescription().equals(itemText))
            {
                itemUsing = item;
            }
        }

        if (!itemsHolding.contains(itemUsing)) {
            printWelcome();
            System.out.println("There is no such item in your inventory!");
        }
        else if (itemUsing != Room.getRoomUnlocker(nextRoom)) {
            printWelcome();
            System.out.println("You can not use this item here!");
        }
        else if (currentRoom == showers && nextRoom == vents && Item.getItemLocation(stool) != showers) {
            printWelcome();
            System.out.println("You need to have the stool in this room to reach the vents.");
        }
        else{
            itemsHolding.remove(itemUsing);
            nextRoom.Unlock();
            Room.removeRoomUnlocker(nextRoom);
            currentRoom = nextRoom;
            System.out.println(nextRoom.getShortDescription());
            if (nextRoom==outside) {
                printWelcome();
                System.out.println("Congratulations! You escaped!");
                return true;
            }
            else {
                printWelcome();
                System.out.println("Room unlocked");
            }
        }
        return false;
    }

    /** 
     * Go back. Goes back a room.
     * @param command The command to be processed.
     */
    private void back(Command command) 
    {
        if(command.hasSecondWord()) {
            printWelcome();
            System.out.println("Back where?");
        }
        else {
            if (roomHistory.size() == 1){     // make the previous room the current one
                currentRoom = roomHistory.get(0);
                printWelcome();
                System.out.println("You are in the room you started");
            }
            else {
                currentRoom = roomHistory.get(roomHistory.size()-2);
                roomHistory.remove(roomHistory.size()-1);
                printWelcome();
            }
        }
    }

    /** 
     * Talk to character. If you are in a room with a character, you can talk to them,
     * otherwise print an error message.
     * @param command The command to be processed.
     */
    private void talkToCharacter(Command command) {
        if(!command.hasThirdWord()) {
            printWelcome();
            // if there is no third word, we don't know who to talk to...
            System.out.println("Talk to who?");
            return;
        }

        String characterText = command.getThirdWord();

        Character characterToTalkTo = null;
        for(Character character: Character.characterLocation.keySet()) {        //shows the characters that are in this room
            if(character.getCharacterDescription().equals(characterText))      //finds the character that matches user's input
            {
                characterToTalkTo = character;
            }
        }

        if (characterToTalkTo == null) {
            printWelcome();
            System.out.println("There is no such character!");
            return;
        }

        if (Character.getCharacterLocation(characterToTalkTo) != currentRoom) {
            printWelcome();
            System.out.println("There is no such character in this room!");
        }
        else if (characterToTalkTo == neighbour) {
            printWelcome();
            if (distractionText == true) {
                System.out.println("you: Hey man, can you cause a distraction while I escape prison?");
            }
            distractionText = false;
            System.out.println(characterToTalkTo.getCharacterDescription()+": "+characterToTalkTo.getResponse());
            characterToTalkTo.setResponse("Get me something I can start a fire with");
        }
        else{
            printWelcome();
            System.out.println(characterToTalkTo.getCharacterDescription()+": "+characterToTalkTo.getResponse());
        }
    }

    /** 
     * Give an item to a character. If the item you are giving is matches and the character is the neighbour,
     * The neighbour starts a fire after you move again,
     * otherwise print an appropriate error message.
     * @param command The command to be processed.
     */
    private void giveItem(Command command) {
        if(!command.hasSecondWord()) {
            printWelcome();
            // if there is no second word, we don't know what to give...
            System.out.println("Give what?");
            return;
        }

        String itemText = command.getSecondWord();

        Item itemGiving = null; //The item that we want to give
        for(Item item: itemsHolding) {
            if(item.getItemDescription().equals(itemText))
            {
                itemGiving = item;
            }
        }

        if (!itemsHolding.contains(itemGiving)) {
            printWelcome();
            System.out.println("There is no such item in your inventory!");
        }
        else {
            if(!command.hasThirdWord()) {
                printWelcome();
                // if there is no third word, we don't know who to give the item to...
                System.out.println("Give item to who?");
                return;
            }

            String characterText = command.getThirdWord();

            Character characterToGiveTo = null;
            for(Character character: Character.characterLocation.keySet()) {        //shows the characters that are in this room
                if(character.getCharacterDescription().equals(characterText))      //finds the character that matches user's input
                {
                    characterToGiveTo = character;
                }
            }

            if (characterToGiveTo == null) {
                printWelcome();
                System.out.println("There is no such character!");
                return;
            }

            if (Character.getCharacterLocation(characterToGiveTo) != currentRoom) {
                printWelcome();
                System.out.println("There is no such character in this room!");
            }
            else if (characterToGiveTo == neighbour && itemGiving == matches) {
                itemsHolding.remove(itemGiving);
                fire = true;
                exit.Unlock();
                neighbour.setResponse("You can escape now, go!!");
                Character.setCharacterRoom(guard, kitchen);
                printWelcome();
                System.out.println(characterToGiveTo.getCharacterDescription() + ": Get moving and I'll start the fire to distract the guards. Look after my daughter!");
            }
            else {
                printWelcome();
                System.out.println(characterToGiveTo.getCharacterDescription() + ": I don't know what to do with this.");
            }
        }
    }

    /** 
     * "Quit" was entered. Check the rest of the command to see
     * whether we really quit the game.
     * @param command The command to be processed.
     * @return true, if this command quits the game, false otherwise.
     */
    private boolean quit(Command command) 
    {
        if(command.hasSecondWord()) {
            printWelcome();
            System.out.println("Quit what?");
            return false;
        }
        else {
            return true;  // signal that we want to quit
        }
    }

}
