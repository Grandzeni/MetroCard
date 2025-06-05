package com.example.geektrust; 

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
public class Metro{
    private MetroCardSave cardStorage =new MetroCardSave();
    private Map<Station,stationSummary> stationSummaries = new HashMap<>();
    private Map<String, Station> lastStationMap = new HashMap<>();
    stationSummary st=new stationSummary();

    public void processCommands(String commands){
        String[]parts=commands.split(" ");
        String command=parts[0];

        switch(command)
        {
            case "BALANCE":
            {
                String cardNo=parts[1];
                double money=Double.parseDouble(parts[2]);
                cardStorage.addCard(cardNo,money);
                break;
            }
            case "CHECK_IN":
            {
                String metroCard=parts[1];
                PassengerType passenger=PassengerType.valueOf(parts[2]);
                Station location=Station.valueOf(parts[3]);

                MetroCard card = cardStorage.getCard(metroCard);
                if(card==null)
                break;
                int fullFare=passenger.getFare();
                int discount=0;
                Station lastStation=lastStationMap.get(metroCard);
                if(lastStation!=null && lastStation!=location){
                    discount=fullFare/2;
                }
                int fareToDeduct=fullFare-discount;
                if (!card.deduct((double) fareToDeduct)) {
                    double remaining=card.recharge((double)fareToDeduct);
                    st.addReacharge(remaining);
                }

                lastStationMap.put(metroCard, location);

                stationSummary summary = stationSummaries.getOrDefault(location, new stationSummary());
                summary.addPassenger(passenger, fareToDeduct, discount);
                stationSummaries.put(location, summary);

                break;
            }

            case "PRINT_SUMMARY": {
                for (Map.Entry<Station, stationSummary> entry : stationSummaries.entrySet()) {
                    entry.getValue().printSummary(entry.getKey());
                }
                break;
            }

            default:
                System.out.println("Unknown command: " + command);
        }
    }
        public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<String> commands = new ArrayList<>();
        Metro metro=new Metro();
        while (true) {
            String line = sc.nextLine();
            commands.add(line);
            if (line.equals("PRINT_SUMMARY")) {
                break;
            }
        }
        for (String command : commands) {
        metro.processCommands(command);
        }

        sc.close();
    }

}


class MetroCard{
    String cardNumber;
    Double balance;
    public MetroCard(String cardNumber,Double balance)
    {
        this.cardNumber=cardNumber;
        this.balance=balance;
    }
    public Double recharge(Double amount)
    {
        Double addOn=amount-this.balance;
        this.balance+=addOn+(2.0/100.0)*addOn;
        return addOn;
        
    }
    public boolean deduct(Double amount)
    {
        if(this.balance>amount)
        {
            this.balance-=amount;
            return true;
         }
         else{
            return false;
         }
    }
}
class MetroCardSave{
    Map <String,MetroCard> Map=new HashMap<>();
    void addCard(String cardNumber,Double balance)
    {
        Map.put(cardNumber,new MetroCard(cardNumber, balance));
    }
    
    MetroCard getCard(String cardNumber)
    {
    return Map.get(cardNumber);
    }
}

 enum PassengerType{
    ADULT(200),
    SENIOR_CITIZEN(100),
    KID(50);
    private final int fare;
    PassengerType(int fare)
    {
        this.fare=fare;
    }
    public int getFare()
    {
        return fare;
    }
}
 enum Station{
    AIRPORT,CENTRAL;
}

class stationSummary{
    private int totalCollection=0;
    private int totalDiscount=0;

    private Map<PassengerType,Integer> passengerCount=new HashMap<>();

    
    public void addPassenger(PassengerType type,int farepaid,int discount){
        totalCollection+=farepaid;
        totalDiscount+=discount;
        passengerCount.put(type,passengerCount.getOrDefault(type, 0)+1);
    }
    public int getTotalCollection(){
        return totalCollection;
    }
    public int getTotalDiscount(){
        return totalDiscount;
    }
    public void addReacharge(Double Recharge)
    {
      totalCollection+=Recharge;
    }
    public Map<PassengerType,Integer> getPassengerCount(){
        return passengerCount;
    }
    public void printSummary(Station station) {
        System.out.println("Station: " + station.name());
        System.out.println("Total Collection: " + totalCollection);
        System.out.println("Total Discount: " + totalDiscount);
        System.out.println("Passenger Counts:");
        for (PassengerType type : PassengerType.values()) {
            int count = passengerCount.getOrDefault(type, 0);
            System.out.println(type.name() + ": " + count);
        }
        System.out.println();
    }
    
}

            
