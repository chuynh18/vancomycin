package yolo.tbv.vancomycin;

import org.junit.Test;

import static org.junit.Assert.*;

public class InitialDoseCalculatorUnitTest {
    @Test
    public void keTest1() {
        assertEquals(0.00083*100+0.0044, InitialDoseCalculator.calculateKe(100), 0.0000000001);
    }

    @Test // implicitly tests calculateCrCl (all CrCl > 150 are clamped to 150)
    public void keTest2() {
        assertEquals(0.00083*150+0.0044, InitialDoseCalculator.calculateKe(200), 0.0000000001);
    }

    @Test
    public void keTestRandom() {
        for (int i = 0; i < 100; i++) {
            double crcl = UnitTestHelper.genRandNumBetween(30, 218);

            if (crcl > 150) {
                crcl = 150;
            }

            double expectedKe = 0.00083 * crcl + 0.0044;

            assertEquals(expectedKe, InitialDoseCalculator.calculateKe(crcl), 0.0000000001);
        }
    }

    @Test
    public void halfLifeTestRandom() {
        for (int i = 0; i < 100; i++) {
            double crcl = UnitTestHelper.genRandNumBetween(30, 218);

            if (crcl > 150) {
                crcl = 150;
            }

            double ke = InitialDoseCalculator.calculateKe(crcl);

            double expectedHL = 0.693 / ke;

            assertEquals(expectedHL, InitialDoseCalculator.calculateHL(ke), 0.0000000001);
        }
    }

    @Test
    public void vdTest1() {
        assertEquals(0.7*50, InitialDoseCalculator.calculateVd(50), 0.0000000001);
    }

    @Test
    public void vdTest2() {
        assertEquals(0.7*30, InitialDoseCalculator.calculateVd(30), 0.0000000001);
    }

    @Test
    public void vdTest3() {
        assertEquals(0.7*70, InitialDoseCalculator.calculateVd(70), 0.0000000001);
    }

    @Test
    public void vdTestRandom() {
        for (int i = 0; i < 100; i++) {
            double bodyWeight = UnitTestHelper.genRandNumBetween(40, 200);
            double expectedVd = 0.7 * bodyWeight;

            assertEquals(expectedVd, InitialDoseCalculator.calculateVd(bodyWeight), 0.0000000001);
        }
    }

    @Test
    public void clvanGeneralTest1() {
        assertEquals((0.00083*100 + 0.0044) * (0.7*50),
        InitialDoseCalculator.calculateClvanGeneral(InitialDoseCalculator.calculateKe(100), InitialDoseCalculator.calculateVd(50)),
        0.0000000001);
    }

    @Test
    public void clvanGeneralTest2() {
        assertEquals((0.00083*80 + 0.0044) * (0.7*70),
        InitialDoseCalculator.calculateClvanGeneral(InitialDoseCalculator.calculateKe(80), InitialDoseCalculator.calculateVd(70)),
        0.0000000001);
    }

    @Test
    public void clvanGeneralTestRandom() {
        for (int i = 0; i < 100; i++) {
            double crcl = UnitTestHelper.genRandNumBetween(30, 218);

            if (crcl > 150) {
                crcl = 150;
            }

            double ke = InitialDoseCalculator.calculateKe(crcl);

            double bodyWeight = UnitTestHelper.genRandNumBetween(40, 200);
            double vd = InitialDoseCalculator.calculateVd(bodyWeight);

            double expectedClvanGeneral = ke * vd;

            assertEquals(expectedClvanGeneral, InitialDoseCalculator.calculateClvanGeneral(ke, vd), 0.0000000001);
        }
    }

    @Test // age 30, scr 0.3, female (0), weight 95
    public void clvanObeseTest1() {
        assertEquals(9.565 - (0.078*30) - (2.009*0.3) + (1.09*0) + (0.04*Math.pow(95, 0.75)),
        InitialDoseCalculator.calculateClvanObese(30, 0.3, 0, 95), 0.0000000001);
    }

    @Test // age 45, scr 0.5, male (1), weight 130
    public void clvanObeseTest2() {
        assertEquals(9.565 - (0.078*45) - (2.009*0.5) + (1.09*1) + (0.04*Math.pow(130, 0.75)),
                InitialDoseCalculator.calculateClvanObese(45, 0.5, 1, 130), 0.0000000001);
    }

    @Test
    public void clvanObeseTestRandom() {
        for (int i = 0; i < 100; i++) {
            double age = UnitTestHelper.genRandNumBetween(18, 90);
            double scr = UnitTestHelper.genRandNumBetween(0.1, 1);
            double bodyWeight = UnitTestHelper.genRandNumBetween(40, 220);

            double expectedMale = 9.565 - (0.078 * age) - (2.009 * scr) + 1.09 + (0.04 * Math.pow(bodyWeight, 0.75));
            double expectedFemale = 9.565 - (0.078 * age) - (2.009 * scr) + (0.04 * Math.pow(bodyWeight, 0.75));

            assertEquals(
                    expectedMale,
                    InitialDoseCalculator.calculateClvanObese(age, scr, 1, bodyWeight),
                    0.0000000001
            );

            assertEquals(
                    expectedFemale,
                    InitialDoseCalculator.calculateClvanObese(age, scr, 0, bodyWeight),
                    0.0000000001
            );
        }
    }

    @Test // obese
    public void cappedClvanTest1() {
        assertEquals(4,
        InitialDoseCalculator.calculateCappedClvanFinal(true, 2, 4), 0.0000000001);
    }

    @Test // obese, over cap
    public void cappedClvanTest2() {
        assertEquals(9,
        InitialDoseCalculator.calculateCappedClvanFinal(true, 2, 11), 0.0000000001);
    }

    @Test // not obese
    public void cappedClvanTest3() {
        assertEquals(5,
        InitialDoseCalculator.calculateCappedClvanFinal(false, 5, 11), 0.0000000001);
    }

    @Test // not obese, over cap
    public void cappedClvanTest4() {
        assertEquals(9,
        InitialDoseCalculator.calculateCappedClvanFinal(false, 42, 1), 0.0000000001);
    }

    @Test
    public void cappedClvanTestRandom() {
        for (int i = 0; i < 100; i++) {
            double crcl = UnitTestHelper.genRandNumBetween(30, 218);

            if (crcl > 150) {
                crcl = 150;
            }

            double ke = InitialDoseCalculator.calculateKe(crcl);

            double bodyWeight = UnitTestHelper.genRandNumBetween(40, 220);
            double vd = InitialDoseCalculator.calculateVd(bodyWeight);
            double age = UnitTestHelper.genRandNumBetween(18, 90);
            double scr = UnitTestHelper.genRandNumBetween(0.1, 1);

            double clvanGeneral = InitialDoseCalculator.calculateClvanGeneral(ke, vd);
            double clvanObeseMale = InitialDoseCalculator.calculateClvanObese(age, scr, 1, bodyWeight);
            double clvanObeseFemale = InitialDoseCalculator.calculateClvanObese(age, scr, 0, bodyWeight);

            double cappedClvanGen = clvanGeneral;
            double cappedClvanObeseMale = clvanObeseMale;
            double cappedClvanObeseFemale = clvanObeseFemale;

            if (cappedClvanGen > 9) {
                cappedClvanGen = 9;
            }

            if (cappedClvanObeseMale > 9) {
                cappedClvanObeseMale = 9;
            }

            if (cappedClvanObeseFemale > 9) {
                cappedClvanObeseFemale = 9;
            }

            // non-obese male
            assertEquals(
                    cappedClvanGen,
                    InitialDoseCalculator.calculateCappedClvanFinal(false, clvanGeneral, clvanObeseMale),
                    0.0000000001
            );

            // obese male
            assertEquals(
                    cappedClvanObeseMale,
                    InitialDoseCalculator.calculateCappedClvanFinal(true, clvanGeneral, clvanObeseMale),
                    0.0000000001
            );

            // non-obese female
            assertEquals(
                    cappedClvanGen,
                    InitialDoseCalculator.calculateCappedClvanFinal(false, clvanGeneral, clvanObeseFemale),
                    0.0000000001
            );

            // obese female
            assertEquals(
                    cappedClvanObeseFemale,
                    InitialDoseCalculator.calculateCappedClvanFinal(true, clvanGeneral, clvanObeseFemale),
                    0.0000000001
            );
        }
    }

    @Test // test cap of 4500
    public void eddTest1() {
        assertEquals(4500,
        InitialDoseCalculator.calculateEDDFinal(800, 800), 0.0000000001);
    }

    @Test // under cap
    public void eddTest2() {
        assertEquals(2500,
        InitialDoseCalculator.calculateEDDFinal(50, 50), 0.0000000001);
    }

    @Test
    public void eddTestRandom() {
        for (int i = 0; i < 100; i++) {
            double clvancoFinal = UnitTestHelper.genRandNumBetween(2, 9);
            double targetAuc = UnitTestHelper.genRandNumBetween(400, 700);

            double expectedEdd = clvancoFinal * targetAuc;

            if (expectedEdd > 4500) {
                expectedEdd = 4500;
            }

            assertEquals(expectedEdd, InitialDoseCalculator.calculateEDDFinal(clvancoFinal, targetAuc), 0.0000000001);
        }
    }

    @Test
    public void obeseDoseTest1() {
        assertEquals(1500,
        InitialDoseCalculator.calculateObese(100, 15),0.0000000001);
    }

    @Test
    public void obeseDoseTest2() {
        assertEquals(3000,
        InitialDoseCalculator.calculateObese(120, 25),0.0000000001);
    }

    @Test
    public void obeseDoseTest3() {
        assertEquals(3000,
        InitialDoseCalculator.calculateObese(150, 20),0.0000000001);
    }

    @Test
    public void obeseDoseTestRandom() {
        for (int i = 0; i < 100; i++) {
            double mgkg = UnitTestHelper.genRandNumBetween(15, 25);
            double bodyWeight = UnitTestHelper.genRandNumBetween(40, 250);

            double expectedObeseDose = mgkg * bodyWeight;

            assertEquals(expectedObeseDose, InitialDoseCalculator.calculateObese(bodyWeight, mgkg), 0.0000000001);
        }
    }

    @Test
    public void calculateCrClRandomTest() {
        for (int i = 0; i < 100; i++) {
            double age = UnitTestHelper.genRandNumBetween(18, 100);
            double weight = UnitTestHelper.genRandNumBetween(40, 250);
            double scr = UnitTestHelper.genRandNumBetween(0.1, 1);

            double expectedCrClMale = ((140 - age) * weight) / (72 * scr);
            double expectedCrClFemale = 0.85 * expectedCrClMale;

            if (expectedCrClMale > 150) {
                expectedCrClMale = 150;
            }

            if (expectedCrClFemale > 150) {
                expectedCrClFemale = 150;
            }

            // male
            assertEquals(expectedCrClMale, InitialDoseCalculator.calculateCrCl(1, age, weight, scr), 0.0000000001);

            // female
            assertEquals(expectedCrClFemale, InitialDoseCalculator.calculateCrCl(0, age, weight, scr), 0.0000000001);
        }
    }
}
