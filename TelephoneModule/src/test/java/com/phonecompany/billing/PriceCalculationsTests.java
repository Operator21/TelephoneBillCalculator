package com.phonecompany.billing;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PriceCalculationsTests {
    //test jednotlivých základních výpočtů ceny
    @Test
    void basicIndividualCalculation() {
        LogCalculator calculator = new LogCalculator();

        PhoneLog log1 = new PhoneLog("420774577453,13-01-2020 18:10:00,13-01-2020 18:12:00"); // 2 minuty mimo rozsah
        PhoneLog log2 = new PhoneLog("420776562353,18-01-2020 08:50:00,18-01-2020 08:52:00"); // 2 minuty v rozsahu

        assertEquals(new BigDecimal("1"), log1.calculatePrice(calculator).stripTrailingZeros());
        assertEquals(new BigDecimal("2"), log2.calculatePrice(calculator).stripTrailingZeros());
    }

    //test jednotlivých výpočtů ceny zahrnující slevy po 5 minutách
    @Test
    void extendedIndividualCalculation() {
        LogCalculator calculator = new LogCalculator();

        PhoneLog log3 = new PhoneLog("420774577453,13-01-2020 18:10:00,13-01-2020 18:18:00"); // 8 minut mimo rozsah - 3 minuty zlevneny
        PhoneLog log4 = new PhoneLog("420774577453,13-01-2020 08:10:00,13-01-2020 08:18:00"); // 8 minut v rozsahu - 3 minuty zlevneny

        assertEquals(new BigDecimal("3.25"), log3.calculatePrice(calculator).stripTrailingZeros());
        assertEquals(new BigDecimal("5.75"), log4.calculatePrice(calculator).stripTrailingZeros());
    }

    //test kompletního CSV vstupu
    @Test
    void completeBatchCalculation() {
        LogCalculator calculator = new LogCalculator();
        //vstupní CSV
        //číslo začínající na 420 se 5x opakuje
        //číslo začínající na 333 se 2x opakuje
        //420 tedy nebude do ceny započítáno kvůli promoakci
        String input =
            """  
            420774577453,13-01-2020 18:10:00,13-01-2020 18:12:00
            420774577453,13-01-2020 19:10:00,13-01-2020 19:41:32
            420774577453,13-01-2020 07:37:42,13-01-2020 08:03:00
            420774577453,13-01-2020 18:10:00,13-01-2020 18:18:00
            420774577453,13-01-2020 08:10:00,13-01-2020 08:18:00
            333777777777,18-01-2020 08:50:00,18-01-2020 08:52:00
            333777777777,18-01-2020 05:23:00,18-01-2020 05:24:00
            """;

        assertEquals(new BigDecimal("2.5"), calculator.calculate(input).stripTrailingZeros());
    }
}