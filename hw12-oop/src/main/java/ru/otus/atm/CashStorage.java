package ru.otus.atm;

import ru.otus.money.Banknote;

import java.util.List;

public interface CashStorage {
    void setCash(List<Banknote> banknote);

    List<Banknote> getCash(int sum);

    List<Banknote> getRemaining();

}
