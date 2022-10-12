package com.phonecompany.billing;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

public class LogCalculator implements TelephoneBillCalculator {

    private final BigDecimal standardPrice;
    private final BigDecimal loweredPrice;
    private final BigDecimal discountPrice;
    private final LocalTime loweredPriceStart;
    private final LocalTime loweredPriceEnd;

    public LogCalculator() {
        this.standardPrice = new BigDecimal("1");
        this.loweredPrice = new BigDecimal("0.5");
        this.discountPrice = new BigDecimal("0.25");

        this.loweredPriceStart = LocalTime.of(8, 0);
        this.loweredPriceEnd = LocalTime.of(16, 0);
    }

    @Override
    public BigDecimal calculate(String phoneLog) {
        String[] splitResult = phoneLog.split("\n");
        BigDecimal totalPrice = new BigDecimal("0");
        ArrayList<PhoneLog> logs = new ArrayList<>();

        //převedení pole stringů do listu objektů
        for(String logString : splitResult) {
            PhoneLog log = new PhoneLog(logString);
            logs.add(log);

        }

        //nalezení nejčastějšího čísla
        String mostCommonNumber = findMostCommonNumber(logs);

        //výpočet celkové hodnoty
        for(PhoneLog log : logs){
            //pokud je číslo nejběžnější tak je přeskočeno
            if(log.getPhone().equals(mostCommonNumber))
                continue;
            BigDecimal tmp = log.calculatePrice(this);
            totalPrice = totalPrice.add(tmp);
        }

        return totalPrice;
    }

    //najít nejvolanější číslo
    private String findMostCommonNumber(ArrayList<PhoneLog> logs){
        HashMap<String, Integer> counts = new HashMap<>();
        ArrayList<String> mostCommon = new ArrayList<>();

        //zjistit počty opakování čísel
        for(PhoneLog log : logs){
            if(counts.containsKey(log.getPhone()))
                counts.put(log.getPhone(), counts.get(log.getPhone())+1);
            else
                counts.put(log.getPhone(), 1);
        }

        //zjistit nejvyšší hodnotu
        int maxValue = 0;
        for(int value : counts.values())
            maxValue = Math.max(value, maxValue);

        //najít všechny čísla se stejným počtem opakování
        for (String key : counts.keySet()) {
            if(counts.get(key) == maxValue)
                mostCommon.add(key);
        }

        //pokud je pouze jedno číslo
        if(mostCommon.size() == 1)
            return mostCommon.get(0);

        return getHighestPhoneNumber(mostCommon);
    }

    private String getHighestPhoneNumber(ArrayList<String> phoneList) {
        BigDecimal highestPhoneNumber = new BigDecimal(phoneList.get(0));
        for (String phone : phoneList)
            highestPhoneNumber = highestPhoneNumber.max(new BigDecimal(phone));

        return "" + highestPhoneNumber;
    }

    public BigDecimal getStandardPrice() { return standardPrice; }
    public BigDecimal getLoweredPrice() { return loweredPrice; }
    public BigDecimal getDiscountPrice() { return discountPrice; }
    public LocalTime getLoweredPriceStart() { return loweredPriceStart; }
    public LocalTime getLoweredPriceEnd() { return loweredPriceEnd; }
}
