package ru.otus.atm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.money.Banknote;
import ru.otus.money.Nominal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

class ATMTest {
    private final ATM atm = new ATM();

    @BeforeEach
    void setUp() {
        List<Banknote> banknotes = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            banknotes.add(new Banknote(Nominal.ONE_HUNDRED));
            banknotes.add(new Banknote(Nominal.FIVE_HUNDRED));
            banknotes.add(new Banknote(Nominal.ONE_THOUSAND));
            banknotes.add(new Banknote(Nominal.TWO_THOUSAND));
            banknotes.add(new Banknote(Nominal.FIVE_THOUSAND));
        }
        atm.setCash(banknotes);
    }

    @Test
    void getCash() {
        int requiredSum = 14800;
        List<Banknote> cash = atm.getCash(requiredSum);
        Map<Nominal, List<Banknote>> banknotesByNominal = cash.stream()
                        .collect(Collectors.groupingBy(Banknote::nominal));

        assertThat(calculateSum(cash)).isEqualTo(requiredSum);
        assertThat(banknotesByNominal.get(Nominal.ONE_HUNDRED).size()).isEqualTo(3);
        assertThat(banknotesByNominal.get(Nominal.FIVE_HUNDRED).size()).isEqualTo(5);
        assertThat(banknotesByNominal.get(Nominal.ONE_THOUSAND).size()).isEqualTo(4);
        assertThat(banknotesByNominal.get(Nominal.TWO_THOUSAND).size()).isEqualTo(4);

        List<Banknote> remaining = atm.getRemaining();
        assertThat(calculateSum(remaining)).isEqualTo(28200);
    }

    @Test
    void getTooMach() {
        int sum = 50000;
        Throwable throwable = catchThrowable(() -> atm.getCash(sum));
        assertThat(throwable).isExactlyInstanceOf(RuntimeException.class);
    }

    @Test
    void getInvalidSum() {
        int sum = 4343;
        Throwable throwable = catchThrowable(() -> atm.getCash(sum));
        assertThat(throwable).isExactlyInstanceOf(RuntimeException.class);
    }

    @Test
    void getRemaining() {
        List<Banknote> remaining = atm.getRemaining();

        assertThat(remaining.size()).isEqualTo(25);
        assertThat(calculateSum(remaining)).isEqualTo(43000);

        remaining = atm.getRemaining();

        assertThat(remaining.size()).isEqualTo(0);
        assertThat(calculateSum(remaining)).isEqualTo(0);
    }

    private int calculateSum(List<Banknote> banknotes) {
        return banknotes.stream()
                .map(Banknote::nominal)
                .mapToInt(Nominal::getValue)
                .sum();
    }
}