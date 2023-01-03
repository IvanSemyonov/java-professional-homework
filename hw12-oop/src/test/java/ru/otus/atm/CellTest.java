package ru.otus.atm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.money.Banknote;
import ru.otus.money.Nominal;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

class CellTest {
    private static final Nominal nominal = Nominal.ONE_HUNDRED;
    private final Cell cell = new Cell(nominal);

    @BeforeEach
    void setUp() {
        List<Banknote> banknotes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            banknotes.add(new Banknote(nominal));
        }
        cell.setCash(banknotes);
    }

    @Test
    void getCash() {
        int sum = 400;
        List<Banknote> banknotes = cell.getCash(sum);
        int expectedSize = 4;
        assertThat(banknotes.size())
                .isEqualTo(expectedSize);
    }

    @Test
    void getInvalidSum() {
        int sum = 435;
        Throwable throwable = catchThrowable(() -> cell.getCash(sum));
        assertThat(throwable).isExactlyInstanceOf(RuntimeException.class);
    }

    @Test
    void remaining() {
        int actualSum = cell.remaining();
        int expectedSum = 1000;
        assertThat(actualSum).isEqualTo(expectedSum);
    }

    @Test
    void getRemaining() {
        List<Banknote> banknotes = cell.getRemaining();
        int expectedSize = 10;
        assertThat(banknotes.size())
                .isEqualTo(expectedSize);
    }
}