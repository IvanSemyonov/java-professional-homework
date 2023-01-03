package ru.otus.atm;

import ru.otus.money.Banknote;
import ru.otus.money.Nominal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cell implements CashStorage, CalculateRemaining {
    private final Nominal nominal;
    private final List<Banknote> banknotes = new ArrayList<>();

    public Cell(Nominal nominal) {
        this.nominal = nominal;
    }

    @Override
    public void setCash(List<Banknote> cash) {
        checkNominal(cash);
        banknotes.addAll(cash);
    }
    private void checkNominal(List<Banknote> cash) {
        for (Banknote banknote : cash) {
            if (!nominal.equals(banknote.nominal())) {
                throw new IllegalArgumentException();
            }
        }
    }

    @Override
    public List<Banknote> getCash(int requiredSum) {
        if (requiredSum == 0) {
            return Collections.emptyList();
        }
        validateSum(requiredSum);
        int requiredBanknotes = requiredSum / nominal.getValue();
        return getBanknotes(requiredBanknotes);
    }

    private void validateSum(int requiredSum) {
        if (requiredSum % nominal.getValue() != 0 || requiredSum > remaining()) {
            throw new RuntimeException();
        }
    }


    @Override
    public int remaining() {
        return nominal.getValue() * banknotes.size();
    }

    private List<Banknote> getBanknotes(int requiredBanknotes) {
        List<Banknote> extractedBanknotes = banknotes.size() > requiredBanknotes
                        ? List.copyOf(banknotes.subList(0, requiredBanknotes))
                        : List.copyOf(banknotes);
        clearCell(extractedBanknotes);
        return extractedBanknotes;
    }

    private void clearCell(List<Banknote> cash) {
        for (int i = 0; i < cash.size(); i++) {
            banknotes.remove(banknotes.size()-1);
        }
    }

    @Override
    public List<Banknote> getRemaining() {
        List<Banknote> remaining = List.copyOf(banknotes);
        banknotes.clear();
        return remaining;
    }
}
