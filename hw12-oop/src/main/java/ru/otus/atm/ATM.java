package ru.otus.atm;

import ru.otus.money.Banknote;
import ru.otus.money.Nominal;

import java.util.*;
import java.util.stream.Collectors;

public class ATM implements CashStorage {
    private final NavigableMap<Nominal, Cell> cells = new TreeMap<>(Comparator.comparingInt(Nominal::getValue));

    @Override
    public void setCash(List<Banknote> banknotes) {
        for (var banknote : groupBanknotes(banknotes).entrySet()) {
            Nominal nominal = banknote.getKey();
            Cell cell = cells.containsKey(nominal)
                    ? cells.get(nominal)
                    : new Cell(nominal);
            cell.setCash(banknote.getValue());
            cells.put(nominal, cell);
        }
    }

    private Map<Nominal, List<Banknote>> groupBanknotes(Collection<Banknote> banknotes) {
        return banknotes.stream()
                .collect(Collectors.groupingBy(Banknote::nominal));
    }

    @Override
    public List<Banknote> getCash(int requiredSum) {
        int currentSum = 0;
        Nominal nominal = cells.firstKey();
        return calculate(nominal, requiredSum, currentSum);
    }


    private List<Banknote> calculate(Nominal nominal, int requiredSum, int canCollect) {
        List<Banknote> banknotes = new ArrayList<>();

        if (nominal != null) {
            Cell currentCell = cells.get(nominal);
            int currentCellSum = currentCell.remaining();
            int totalSum = canCollect + currentCellSum;
            if (totalSum < requiredSum) {
                Nominal nextNominal = cells.higherKey(nominal);
                banknotes.addAll(calculate(nextNominal, requiredSum, totalSum));
            }
            requiredSum -= getCurrentSum(banknotes);
            banknotes.addAll(getMinimumOfBanknotes(nominal, requiredSum, canCollect));
            return banknotes;
        }

        throw new RuntimeException();
    }

    private int getCurrentSum(List<Banknote> banknotes) {
        return banknotes.stream()
                .mapToInt(banknote -> banknote.nominal().getValue())
                .sum();
    }

    private List<Banknote> getMinimumOfBanknotes(Nominal nominal, int requiredSum, int currentSum) {
        int requiredCurrentNominalSum = 0;
        while (currentSum < requiredSum) {
            requiredCurrentNominalSum += nominal.getValue();
            if (requiredCurrentNominalSum > requiredSum) {
                throw new RuntimeException();
            }
            currentSum += nominal.getValue();
        }
        return cells.get(nominal).getCash(requiredCurrentNominalSum);
    }



    @Override
    public List<Banknote> getRemaining() {
        return cells.values().stream()
                .map(Cell::getRemaining)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
