package yolo.tbv.vancomycin;

import org.junit.Test;

import static org.junit.Assert.*;

// random unit tests for AUCCalculator
public class AUCCalculatorUnitTest {
    @Test
    public void calculateKeTestRandom() {
        for (int i = 0; i < 100; i++) {
            double measuredPeak = UnitTestHelper.genRandNumBetween(50, 80);
            double measuredTrough = UnitTestHelper.genRandNumBetween(1, 30);
            double deltaT = UnitTestHelper.genRandNumBetween(3, 6);

            assertEquals(Math.log(measuredPeak / measuredTrough) / deltaT, AUCCalculator.calculateKe(measuredPeak, measuredTrough, deltaT), 0.0000000001);
        }
    }

    @Test
    public void calculateTruePeakTestRandom() {
        for (int i = 0; i < 100; i++) {
            double measuredPeak = UnitTestHelper.genRandNumBetween(50, 100);
            double ke = UnitTestHelper.genRandNumBetween(0.5, 1);
            double levelOneTime = UnitTestHelper.genRandNumBetween(0.5, 1);
            double infusionDuration = UnitTestHelper.genRandNumBetween(1, 2);

            assertEquals(
                    measuredPeak / Math.exp(ke * (infusionDuration - levelOneTime)),
                    AUCCalculator.calculateTruePeak(measuredPeak, ke, levelOneTime, infusionDuration),
                    0.0000000001
            );
        }
    }

    @Test
    public void calculateTrueTroughRandom() {
        for (int i = 0; i < 100; i++) {
            double truePeak = UnitTestHelper.genRandNumBetween(50, 101);
            double ke = UnitTestHelper.genRandNumBetween(0.5, 1);
            double initialDoseFreq = UnitTestHelper.genRandNumBetween(5, 24);
            double infusionDuration = UnitTestHelper.genRandNumBetween(0.8, 2);

            assertEquals(
                    truePeak * Math.exp(-ke * (initialDoseFreq - infusionDuration)),
                    AUCCalculator.calculateTrueTrough(truePeak, ke, initialDoseFreq, infusionDuration),
                    0.0000000001
            );
        }
    }

    @Test
    public void calculateVdRandom() {
        for (int i = 0; i < 100; i++) {
            double initialDose = UnitTestHelper.genRandNumBetween(100, 500);
            double infusionDuration = UnitTestHelper.genRandNumBetween(0.8, 2);
            double ke = UnitTestHelper.genRandNumBetween(0.5, 1);
            double truePeak = UnitTestHelper.genRandNumBetween(50, 100);
            double trueTrough = UnitTestHelper.genRandNumBetween(1, 20);

            double expKeAndInfDuration = Math.exp(ke * -infusionDuration);

            double vd = (initialDose / infusionDuration)
                    * (1 - expKeAndInfDuration)
                    / (ke * (truePeak - (trueTrough * expKeAndInfDuration)));

            assertEquals(
                    vd,
                    AUCCalculator.calculateVd(initialDose, infusionDuration, ke, truePeak, trueTrough),
                    0.0000000001
            );
        }
    }

    @Test
    public void calculateAuc24Random() {
        for (int i = 0; i < 100; i++) {
            double truePeak = UnitTestHelper.genRandNumBetween(50, 100);
            double trueTrough = UnitTestHelper.genRandNumBetween(0.5, 15);
            double infusionDuration = UnitTestHelper.genRandNumBetween(0.8, 1.8);
            double ke = UnitTestHelper.genRandNumBetween(0.5, 1);
            double initialDoseFreq = UnitTestHelper.genRandNumBetween(4, 24);

            double expectedAuc24 = 24
                    * (((truePeak + trueTrough) / 2 * infusionDuration) + ((truePeak - trueTrough) / ke))
                    / initialDoseFreq;

            assertEquals(
                    expectedAuc24,
                    AUCCalculator.calculateAUC24(truePeak, trueTrough, infusionDuration, ke, initialDoseFreq),
                    0.0000000001
            );
        }
    }

    @Test
    public void calculateSuggestedTRandom() {
        for (int i = 0; i < 100; i++) {
            double halfLife = UnitTestHelper.genRandNumBetween(0, 4);

            assertEquals(6, AUCCalculator.calculateSuggestedT(halfLife), 0.0000000001);
        }

        for (int i = 0; i < 100; i++) {
            double halfLife = UnitTestHelper.genRandNumBetween(4.00001, 5.5);

            assertEquals(8, AUCCalculator.calculateSuggestedT(halfLife), 0.0000000001);
        }

        for (int i = 0; i < 100; i++) {
            double halfLife = UnitTestHelper.genRandNumBetween(5.50001, 11);

            assertEquals(12, AUCCalculator.calculateSuggestedT(halfLife), 0.0000000001);
        }

        for (int i = 0; i < 100; i++) {
            double halfLife = UnitTestHelper.genRandNumBetween(11.00001, 420);

            assertEquals(24, AUCCalculator.calculateSuggestedT(halfLife), 0.0000000001);
        }
    }

    @Test
    public void calculateVancoDoseRandom() {
        for (int i = 0; i < 100; i++) {
            double goalAuc24 = UnitTestHelper.genRandNumBetween(400, 700);
            double calculatedAuc24 = UnitTestHelper.genRandNumBetween(400, 700);
            double initialDose = UnitTestHelper.genRandNumBetween(500, 1000);
            double initialDoseFreq = UnitTestHelper.genRandNumBetween(6, 24);
            double suggestedT = AUCCalculator.calculateSuggestedT(
                    UnitTestHelper.genRandNumBetween(0, 24)
            );

            double expectedVancoDose = (goalAuc24 / calculatedAuc24 * (initialDose * 24 / initialDoseFreq)) * suggestedT / 24;

            assertEquals(
                    expectedVancoDose,
                    AUCCalculator.calculateVancoDose(goalAuc24, calculatedAuc24, initialDose, initialDoseFreq, suggestedT),
                    0.0000000001
            );
        }
    }

    @Test
    public void calculatePredictedPeakRandom() {
        for (int i = 0; i < 100; i++) {
            double initialDose = UnitTestHelper.genRandNumBetween(100, 500);
            double infusionDuration = UnitTestHelper.genRandNumBetween(0.8, 2);
            double ke = UnitTestHelper.genRandNumBetween(0.5, 1);
            double truePeak = UnitTestHelper.genRandNumBetween(50, 100);
            double trueTrough = UnitTestHelper.genRandNumBetween(1, 20);
            double revDose = UnitTestHelper.genRandNumBetween(100, 500);
            double revDuration = UnitTestHelper.genRandNumBetween(0.8, 2);
            double revInterval = UnitTestHelper.genRandNumBetween(4, 24);

            double expKeAndInfDuration = Math.exp(ke * -infusionDuration);

            double vd = (initialDose / infusionDuration)
                    * (1 - expKeAndInfDuration)
                    / (ke * (truePeak - (trueTrough * expKeAndInfDuration)));

            double expectedPeak = (
                    (revDose / (ke * vd * revDuration))
                    * (1 - Math.exp(-ke * revDuration))
                    / (1 - Math.exp(-ke * revInterval))
                    );

            assertEquals(
                    expectedPeak,
                    AUCCalculator.calculatePredictedPeak(vd, revDose, revDuration, ke, revInterval),
                    0.0000000001
            );
        }
    }

    @Test
    public void calculatePredictedTroughRandom() {
        double initialDose = UnitTestHelper.genRandNumBetween(100, 500);
        double infusionDuration = UnitTestHelper.genRandNumBetween(0.8, 2);
        double ke = UnitTestHelper.genRandNumBetween(0.5, 1);
        double truePeak = UnitTestHelper.genRandNumBetween(50, 100);
        double trueTrough = UnitTestHelper.genRandNumBetween(1, 20);
        double revDose = UnitTestHelper.genRandNumBetween(100, 500);
        double revDuration = UnitTestHelper.genRandNumBetween(0.8, 2);
        double revInterval = UnitTestHelper.genRandNumBetween(4, 24);

        double expKeAndInfDuration = Math.exp(ke * -infusionDuration);

        double vd = (initialDose / infusionDuration)
                * (1 - expKeAndInfDuration)
                / (ke * (truePeak - (trueTrough * expKeAndInfDuration)));

        double calcPredictedPeak = AUCCalculator.calculatePredictedPeak(
                vd,
                revDose,
                revDuration,
                ke,
                revInterval
        );

        double expectedTrough = (
                    calcPredictedPeak
                    * Math.exp(-ke * (revInterval - revDuration))
                );

        assertEquals(
                expectedTrough,
                AUCCalculator.calculatePredictedTrough(calcPredictedPeak, ke, revInterval, revDuration),
                0.0000000001
        );
    }

    @Test
    public void calculatePredictedAuc24Random() {
        for (int i = 0; i < 100; i++) {
            double initialDose = UnitTestHelper.genRandNumBetween(100, 500);
            double infusionDuration = UnitTestHelper.genRandNumBetween(0.8, 2);
            double ke = UnitTestHelper.genRandNumBetween(0.5, 1);
            double truePeak = UnitTestHelper.genRandNumBetween(50, 100);
            double trueTrough = UnitTestHelper.genRandNumBetween(1, 20);
            double revDose = UnitTestHelper.genRandNumBetween(100, 500);
            double revDuration = UnitTestHelper.genRandNumBetween(0.8, 2);
            double revInterval = UnitTestHelper.genRandNumBetween(4, 24);

            double expKeAndInfDuration = Math.exp(ke * -infusionDuration);

            double vd = (initialDose / infusionDuration)
                    * (1 - expKeAndInfDuration)
                    / (ke * (truePeak - (trueTrough * expKeAndInfDuration)));

            double calcPredictedPeak = AUCCalculator.calculatePredictedPeak(
                    vd,
                    revDose,
                    revDuration,
                    ke,
                    revInterval
            );

            double expectedTrough = (
                    calcPredictedPeak
                            * Math.exp(-ke * (revInterval - revDuration))
            );

            double expectedAuc24 = (
                    (((calcPredictedPeak + expectedTrough) / 2 * revDuration)
                        + ((calcPredictedPeak - expectedTrough) / ke))
                    * 24 / revInterval
                    );

            assertEquals(
                    expectedAuc24,
                    AUCCalculator.calculatePredictedAuc24(calcPredictedPeak, expectedTrough, revDuration, ke, revInterval),
                    0.0000000001
            );
        }
    }
}
