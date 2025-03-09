package uk.tw.energy.generator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import uk.tw.energy.domain.ElectricityReading;

public class ElectricityReadingsGenerator {

    public List<ElectricityReading> generate(int number) {
        Instant now = Instant.now();

        return IntStream.range(0, number)
                .mapToObj(i -> new ElectricityReading(
                        now.minusSeconds(i * 10L),
                        BigDecimal.valueOf(Math.abs(ThreadLocalRandom.current().nextGaussian()))
                                .setScale(4, RoundingMode.CEILING)))
                .collect(Collectors.toList());
    }

}
