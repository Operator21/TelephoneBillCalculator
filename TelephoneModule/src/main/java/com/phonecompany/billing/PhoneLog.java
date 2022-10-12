package com.phonecompany.billing;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PhoneLog {
    private String phone;
    private LocalDateTime start;
    private LocalDateTime end;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public PhoneLog(String phoneLog) {
        setPhoneLog(phoneLog);
    }

    public String getPhone() { return phone; }

    //nastavit hodnoty instance ze stringu
    private void setPhoneLog(String phoneLog) {
        //rozdělit do pole
        String[] splitResult = phoneLog.split(",");

        //přiřadit atributy, data a časy jsou parsovány ze stringů
        this.phone = splitResult[0];
        this.start = LocalDateTime.parse(splitResult[1], PhoneLog.formatter);
        this.end = LocalDateTime.parse(splitResult[2], PhoneLog.formatter);
    }

    //je čas v rámci daného rozsahu?
    private boolean isPriceLowered(LocalDateTime newStart, LogCalculator calculator) {
        boolean isAfter = newStart.isAfter(calculator.getLoweredPriceStart().atDate(newStart.toLocalDate()));
        boolean isBefore = newStart.isBefore(calculator.getLoweredPriceEnd().atDate(newStart.toLocalDate()));
        return !isAfter || !isBefore;
    }

    //získat cenu dle rozsahu
    private BigDecimal getPrice(LocalDateTime newStart, LogCalculator calculator) {
        return isPriceLowered(newStart, calculator) ? calculator.getLoweredPrice() : calculator.getStandardPrice();
    }

    //vypočítat cenu aktuálního záznamu
    public BigDecimal calculatePrice(LogCalculator calculator) {
        BigDecimal price = new BigDecimal("0");

        //počet vteřin mezi začátkem a koncem
        long duration = Duration.between(this.start, this.end).toSeconds();
        for(long x = 0; x < duration; x+=60) {
            LocalDateTime newStart = this.start.plusSeconds(x);

            //pokud je čas nad 5 minut, nastavit na zvýhodněnou cenu, jinak určit dle rozsahu
            BigDecimal temp = (x >= (60*5)) ? calculator.getDiscountPrice() : getPrice(newStart, calculator);
            price = price.add(temp);
        }

        return price;
    }
}
