import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Capitaly {
    private ArrayList<Player> players;
    private final ArrayList<Field> fields;
    private final ArrayList <Integer> sumRolledDice;
    private Player winner=null;
    Capitaly(ArrayList<Player> ps, ArrayList<Field> fs, ArrayList<Integer> rolledDice){
        players=ps;
        fields=fs;
        sumRolledDice=rolledDice;

    }

    public void setPlayersToBeginning(){
        players =(ArrayList<Player>) players.stream().map(x -> {
            x.position= fields.get(0);
            return x;
        }).collect(Collectors.toList());
    }

    //methods
    public void simulate(){
        setPlayersToBeginning();
        int i=0;
        while(winner==null && i!=sumRolledDice.size()){
            System.out.println(sumRolledDice.size());
            System.out.println(i);
            //roll the dice() *UNDER REVIEW*, potential errors
            int rolledDice = sumRolledDice.get(i); //corrected
            int playerNumber= i%players.size(); //corrected
            int fieldNumber= (fields.indexOf(players.get(playerNumber).getPosition())+rolledDice)%fields.size(); //should start from 0 becuase indexing
            System.out.println( "_____________________________");
            System.out.println(players.get(playerNumber).getName()+" was at " +fields.indexOf(players.get(playerNumber).getPosition()) +" at " + players.get(playerNumber).getPosition().getName()+" with amount "+ players.get(playerNumber).getAmount());
            System.out.println("rolled dice outcome: "+rolledDice);
            if (players.get(playerNumber).isAlive()){
                //i have doubt in next line
               // players.get(playerNumber).changePosition(fields.get((fields.indexOf(players.get(playerNumber).getPosition())+fieldNumber)%fields.size()));
                players.get(playerNumber).changePosition(fields.get(fieldNumber));

                System.out.println(players.get(playerNumber).getName()+" is at "+players.get(playerNumber).getPosition().getName()+ " at "+ fieldNumber);
                System.out.println("[player name:"+players.get(playerNumber).getName()+", Amount:"+players.get(playerNumber).getAmount()+", Position:"+players.get(playerNumber).getPosition().getName()+", Assets:"+players.get(playerNumber).getAssets().stream().map(x-> x.getName()).collect(Collectors.toList())+", Houses:"+players.get(playerNumber).getHouses().stream().map(x-> x.getName()).collect(Collectors.toList())+"]");
                players.get(playerNumber).play();
                System.out.println("[player name:"+players.get(playerNumber).getName()+", Amount:"+players.get(playerNumber).getAmount()+", Position:"+players.get(playerNumber).getPosition().getName()+", Assets:"+players.get(playerNumber).getAssets().stream().map(x-> x.getName()).collect(Collectors.toList())+", Houses:"+players.get(playerNumber).getHouses().stream().map(x-> x.getName()).collect(Collectors.toList())+"]");

                if (!players.get(playerNumber).isAlive()){
                    players.get(playerNumber).changePosition(fields.get(fieldNumber));
                    System.out.println(players.get(playerNumber).getName() + " is lost");
                    players.remove(playerNumber);
                    if(players.size()==1){
                        winner=players.get(playerNumber%players.size());
                        break;
                    }
                    continue;
                }
            }
            else{
                players.get(playerNumber).changePosition(fields.get(fieldNumber));
                System.out.println(players.get(playerNumber).getName()+" is at "+players.get(playerNumber).getPosition().getName()+ " at "+ fieldNumber);
                System.out.println(players.get(playerNumber).getName() + " is lost");
                players.remove(playerNumber);
                System.out.println("After removing "+players.get((playerNumber)%players.size()).getPosition().getName());
                if(players.size()==1){
                    winner=players.get(playerNumber%players.size());
                    break;
                }
                continue;
            }
            System.out.println(players.stream().map(x-> x.getName()).collect(Collectors.toList()));
            i++;

        }
        if (winner!=null){
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++");
            System.out.println("Winner is " + winner.getName()+ " with final amount he has is "+winner.getAmount());
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++");
        }
        else{
            System.out.println("There is no winner , player ran out of turns!");
        }
    }
    public static void main(String [] args) throws IOException {
        ReadInput in= new ReadInput("input.txt");
        ArrayList <Field> fields= in.getFields();
        ArrayList <Player> players= in.getPlayers();

        ArrayList <Integer> rolledDices= in.getRolledDice();
        Capitaly game = new Capitaly(players,fields, rolledDices);
        game.simulate();
    }



}

class ReadInput{
    private String input;
    private BufferedReader bf;
    ReadInput(String input){
        this.input=input;
        try{
            bf=new BufferedReader(new FileReader(this.input)) ;

        } catch(FileNotFoundException e){
            System.out.println(e.getStackTrace());
        }


    }
    public ArrayList<ArrayList> readEntity() throws IOException {
        String line=bf.readLine();
        int no_ents= Integer.parseInt(line);
        ArrayList<ArrayList> entities=new ArrayList<>();
        for(int i=0;i<no_ents;i++){
            line=bf.readLine();
            ArrayList<String> content = new ArrayList<> (Arrays.asList(line.split(" ")));
            entities.add(content);
        }
        return entities;

    }
    public ArrayList <Player> getPlayers() throws IOException{
        ArrayList<ArrayList> ents= readEntity();
        System.out.println(ents);
        System.out.println("G: Greedy, T: Tactical, C: Careful");
        //if the entities are players
        ArrayList<Player> players = new ArrayList<>();
        players=(ArrayList<Player>)ents.stream().map(x -> {
            Player p;
            if (((String)x.get(0)).equals("G")){
                p =new Greedy((String)x.get(1));
            }
            else if (((String)x.get(0)).equals("T")){
                p =new Tactical((String)x.get(1));
            }
            else if (((String)x.get(0)).equals("C")){
                p =new Careful((String)x.get(1));
            }
            else{
                System.out.println("Player cannot find its category");
                p =new Careful((String)x.get(1));
            }
            return p;
        }).collect(Collectors.toList());
        return players;
    }
    public ArrayList <Field> getFields() throws IOException{
        ArrayList<ArrayList> ents= readEntity();
        //if the entities are players
        ArrayList<Field> fields = new ArrayList<>();
        fields=(ArrayList<Field>)ents.stream().map(x -> {
            Field f;
            if (((String)x.get(0)).equals("P")){
                f=new Property((String)x.get(1));
            }
            else if (((String)x.get(0)).equals("L")){
                f =new Lucky((String)x.get(1), Integer.parseInt((String)x.get(2)));
            }

            else if (((String)x.get(0)).equals("S")){
                f =new Services((String)x.get(1),Integer.parseInt((String)x.get(2)));
            }
            else{
                System.out.println("Field cannot find its category");
                f =new Property((String)x.get(1));
            }
            return f;
        }).collect(Collectors.toList());
        return fields;
    }
    public ArrayList <Integer> getRolledDice() throws IOException{
        ArrayList<Integer> dice =new ArrayList<>();
        //if the entities are players
       String line = bf.readLine();
       while((line = bf.readLine())!=null){
           dice.add((Integer.parseInt(line)));
       }
       return dice;
    }
}

interface playingStrategy{
    void play();
}
interface ifStepped{
    void behaviour(Player p);
}

abstract class Player implements playingStrategy{
    private String name;  //name of the player
    private int amount=10000;  //amount the player has
    protected Field position; // position field the player is at
    private ArrayList<Property> assets= new ArrayList<Property>(); // properties player owns
    private ArrayList<Property> houses = new ArrayList<Property>(); //houses player owns
    private boolean alive=true;


    Player(String name){
        this.name=name;
    }
    public boolean isAlive(){
        return alive;
    }
    public void setAlive(boolean flag){ alive =flag; }
    public int getAmount(){
        return amount;
    }
    public String getName(){
        return name;
    }
    protected void setAssets(ArrayList<Property> asset){
        assets=asset;
    }
    protected void setHouses(ArrayList<Property> house){
        houses=house;
    }
    public Field getPosition(){
        return position;
    }
    public ArrayList<Property> getAssets(){
        return assets;
    }
    public ArrayList<Property> getHouses(){
        return houses;
    }
    public int rollDice(){
        return (int)((Math.random()*(12-1))+1);
    }

    public void buyField(Property field){
        assets.add(field);
        amount =amount - field.getCost();
        System.out.println(getName()+"has bought field "+field.getName() +" paid "+field.getCost());

    }
    public void buyHouse(Property field){
        houses.add(field);
        amount =amount - field.getBuyingCost();
        System.out.println(getName()+"has built house on "+field.getName() +" paid "+field.getBuyingCost());
    }
    public void add_amount(int amount){
        this.amount+=amount;
    }
    public void subtract_amount(int amount){
        this.amount-=amount;
    }
    public void payRent(Property field){
        Player owner = field.getOwner();
        int amounts;
        if (field.hasHouse()){
           amounts = 2000;
        }
        else{
            amounts = 500;
        }
        if (getAmount()>=amounts){
            owner.add_amount(amounts);
            this.amount -=amounts;
            System.out.println(getName()+" has paid the rent for "+ position.getName() + " to "+ ((Property) position).getOwner().getName());

        }
        else {
            owner.add_amount(getAmount());
            this.amount=0;
            setAlive(false);
            System.out.println(getName()+" can not pay the rent so he is lost");
        }
        if (getAmount()==0){
            setAlive(false);
        }
    }
    public void changePosition(Field field){
        position=field;
    }

}

abstract class Field  implements ifStepped{
    private String name;  //name of the property
    protected int rent;  //rent has to be paid by the other player if they step in this field

    Field(String name, int rent){
        this.name=name;
        this.rent= rent;
    }

    public String getName(){
        return name;
    }
    public int getRent(){
        return rent;
    }
}
class Tactical extends Player{
    private boolean turnToBuy=true;
    Tactical(String name){
        super(name);
    }
    @Override
    public void play() {
        if (position instanceof Property){
            if(!((Property) position).isSold() ) { //&& ((Property) position).getOwner().equals(null)
                //buying the property
                if (getAmount() >= ((Property) position).getCost()) {
                    if (turnToBuy){
                        buyField((Property) position);
                        ((Property) position).behaviour(this);
                    }
                    turnToBuy=!turnToBuy;
                } else {
                    setAlive(false);
                }
            }
            else if (!((Property) position).hasHouse() && ((Property) position).getOwner().equals(this)){
                //building house
                if (getAmount() >= ((Property) position).getBuyingCost()) {
                    if (turnToBuy){
                        buyHouse((Property) position);
                        ((Property) position).setHouseFlag(true);
                        ((Property) position).setRent(2000);

                    }
                    turnToBuy=!turnToBuy;
                } else {
                    setAlive(false);
                }

            }
            else if (!((Property) position).getOwner().equals(this)){
                //rent
                if (getAmount()>=((Property) position).getRent()){
                    payRent((Property) position);

                }
                else {
                    setAlive(false);
                }

            }
            if (!isAlive()){
                setAssets((ArrayList<Property>) getAssets().stream().map((x) -> {
                    x.setSold(false);
                    x.setOwner(null);

                    return x;
                }).collect(Collectors.toList()));
                getAssets().clear();
                setHouses((ArrayList<Property>) getHouses().stream().map( (x) -> {
                    x.setHouseFlag(false);
                    x.setRent(500);
                    return x;
                }).collect(Collectors.toList()));
                getHouses().clear();
            }
        }
        else if (position instanceof Services){
            ((Services) position).behaviour(this);

        }
        else if (position instanceof Lucky){
            ((Lucky) position).behaviour(this);
        }

    }
}
class Careful extends Player{

    Careful(String name) {
        super(name);
    }

    @Override
    public void play() {
        if (position instanceof Property){
            if(!((Property) position).isSold()) { // && ((Property) position).getOwner().equals(null)
                //buying the property
                if (getAmount() >= 2*((Property) position).getCost()) {
                    buyField((Property) position);
                    ((Property) position).behaviour(this);

                } else {
                    setAlive(false);
                }
            }
            else if (!((Property) position).hasHouse() && ((Property) position).getOwner().equals(this)){
                //building house
                if (getAmount() >= 2*((Property) position).getBuyingCost()) {
                    buyHouse((Property) position);
                    ((Property) position).setHouseFlag(true);
                    ((Property) position).setRent(2000);


                } else {
                    setAlive(false);
                }

            }
            else if (!((Property) position).getOwner().equals(this)){
                //rent
                if (getAmount()>=((Property) position).getRent()){
                    payRent((Property) position);

                }
                else {
                    setAlive(false);
                }

            }
            if (!isAlive()){

                setAssets((ArrayList<Property>) getAssets().stream().map((x) -> {
                    x.setSold(false);
                    x.setOwner(null);

                    return x;
                }).collect(Collectors.toList()));
                getAssets().clear();
                setHouses(((ArrayList<Property>) getHouses().stream().map( (x) -> {
                    x.setHouseFlag(false);
                    x.setRent(500);
                    return x;
                }).collect(Collectors.toList())));
                getHouses().clear();
            }
        }
        else if (position instanceof Services){
            ((Services) position).behaviour(this);
        }
        else if (position instanceof Lucky){
            ((Lucky) position).behaviour(this);
        }


    }
}
class Greedy extends Player{

    Greedy(String name) {
        super(name);
    }

    @Override
    public void play() {
        if (position instanceof Property){
            if(!((Property) position).isSold() ) { //&& ((Property) position).getOwner().equals(null)
                //buying the property
                if (getAmount() >= ((Property) position).getCost()) {
                    buyField((Property) position);
                    ((Property) position).behaviour(this);

                } else {
                    setAlive(false);
                }
            }
            else if (!((Property) position).hasHouse() && ((Property) position).getOwner().equals(this)){
                //building house
                if (getAmount() >= ((Property) position).getBuyingCost()) {
                    buyHouse((Property) position);
                    ((Property) position).setHouseFlag(true);
                    ((Property) position).setRent(2000);


                } else {
                    setAlive(false);
                }

            }
            else if (!((Property) position).getOwner().equals(this)){
                //rent
                if (getAmount() >= ((Property) position).getRent()) {
                    payRent((Property) position);

                } else {
                    setAlive(false);
                }

            }
            if (!isAlive()){

                setAssets((ArrayList<Property>) getAssets().stream().map((x) -> {
                    x.setSold(false);
                    x.setOwner(null);

                    return x;
                }).collect(Collectors.toList()));
                getAssets().clear();
                setHouses((ArrayList<Property>) getHouses().stream().map( (x) -> {
                    x.setHouseFlag(false);
                    x.setRent(500);
                    return x;
                }).collect(Collectors.toList()));
                getHouses().clear();
            }
        }
        else if (position instanceof Services){
            ((Services) position).behaviour(this);
        }
        else if (position instanceof Lucky){
            ((Lucky) position).behaviour(this);
        }

    }
}

class Property extends Field{
    private int cost=1000;  //cost of owning this property
    private int buyingCost=4000;  //cost of owning house for this property
    private Player owner = null;    //owner of this field
    private boolean sold=false;  // if the property is sold
    private boolean isHouse=false;  //if this field is house
    Property(String name) {
        super(name, 500);
    }
    public void setOwner(Player p){
        owner=p;
    }
    public int getBuyingCost(){
        return buyingCost;
    }
    public void setSold(boolean flag){
        sold=flag;
    }
    public void setHouseFlag(boolean flag){
        isHouse=flag;
    }
    public boolean isSold(){
        return sold;
    }
    public void setRent(int rent){

    }
    public boolean hasHouse(){
        return isHouse;
    }
    public Player getOwner(){
        return owner;
    }
    public int getCost(){
        return cost;
    }
    @Override
    public void behaviour(Player p) {
        setOwner(p);
        setSold(true);
    }
}
class Services extends Field{
    Services(String name, int amount) {
        super(name, amount);
    }

    @Override
    public void behaviour(Player p) {
        if (p.getAmount()>=getRent()){

            p.subtract_amount(getRent());
            System.out.println(p.getName()+ " has paid service charge of "+getRent()+"; Total amount = "+ p.getAmount() );
        }
        else{
            p.setAlive(false);
            p.subtract_amount(p.getAmount());
            System.out.println(p.getName()+ " couldnt pay the service charges is lost");
        }
    }
}
class Lucky extends Field{

    Lucky(String name, int rent) {
        super(name, rent);
    }

    @Override
    public void behaviour(Player p) {
        p.add_amount(getRent());
        System.out.println(p.getName()+ " has received amount of "+getRent()+"; Total amount = "+ p.getAmount());

    }
}