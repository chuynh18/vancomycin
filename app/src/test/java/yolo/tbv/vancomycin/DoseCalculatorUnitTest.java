package yolo.tbv.vancomycin;

import org.junit.Test;

import static org.junit.Assert.*;

public class DoseCalculatorUnitTest {
    @Test
    public void keTest1() {
        assertEquals(0.00083*100+0.0044, DoseCalculator.calculateKe(100), 0.0000000001);
    }

    @Test // implicitly tests calculateCrCl (all CrCl > 150 are clamped to 150)
    public void keTest2() {
        assertEquals(0.00083*150+0.0044, DoseCalculator.calculateKe(200), 0.0000000001);
    }

    @Test
    public void halfLifeTest1() {
        assertEquals(0.693/150, DoseCalculator.calculateHL(150), 0.0000000001);
    }

    @Test
    public void halfLifeTest2() {
        assertEquals(0.693/100, DoseCalculator.calculateHL(100), 0.0000000001);
    }

    @Test
    public void halfLifeTest3() {
        assertEquals(0.693/80, DoseCalculator.calculateHL(80), 0.0000000001);
    }

    @Test
    public void vdTest1() {
        assertEquals(0.7*50, DoseCalculator.calculateVd(50), 0.0000000001);
    }

    @Test
    public void vdTest2() {
        assertEquals(0.7*30, DoseCalculator.calculateVd(30), 0.0000000001);
    }

    @Test
    public void vdTest3() {
        assertEquals(0.7*70, DoseCalculator.calculateVd(70), 0.0000000001);
    }

    @Test
    public void clvanGeneralTest1() {
        assertEquals((0.00083*100 + 0.0044) * (0.7*50),
        DoseCalculator.calculateClvanGeneral(DoseCalculator.calculateKe(100),DoseCalculator.calculateVd(50)),
        0.0000000001);
    }

    @Test
    public void clvanGeneralTest2() {
        assertEquals((0.00083*80 + 0.0044) * (0.7*70),
        DoseCalculator.calculateClvanGeneral(DoseCalculator.calculateKe(80),DoseCalculator.calculateVd(70)),
        0.0000000001);
    }

    @Test // age 30, scr 0.3, female (0), weight 95
    public void clvanObeseTest1() {
        assertEquals(9.565 - (0.078*30) - (2.009*0.3) + (1.09*0) + (0.04*Math.pow(95, 0.75)),
        DoseCalculator.calculateClvanObese(30, 0.3, 0, 95), 0.0000000001);
    }

    @Test // age 45, scr 0.5, male (1), weight 130
    public void clvanObeseTest2() {
        assertEquals(9.565 - (0.078*45) - (2.009*0.5) + (1.09*1) + (0.04*Math.pow(130, 0.75)),
                DoseCalculator.calculateClvanObese(45, 0.5, 1, 130), 0.0000000001);
    }

    @Test // obese
    public void cappedClvanTest1() {
        assertEquals(4,
        DoseCalculator.calculateCappedClvanFinal(true, 2, 4), 0.0000000001);
    }

    @Test // obese, over cap
    public void cappedClvanTest2() {
        assertEquals(9,
        DoseCalculator.calculateCappedClvanFinal(true, 2, 11), 0.0000000001);
    }

    @Test // not obese
    public void cappedClvanTest3() {
        assertEquals(5,
        DoseCalculator.calculateCappedClvanFinal(false, 5, 11), 0.0000000001);
    }

    @Test // not obese, over cap
    public void cappedClvanTest4() {
        assertEquals(9,
        DoseCalculator.calculateCappedClvanFinal(false, 42, 1), 0.0000000001);
    }

    @Test // test cap of 4500
    public void eddTest1() {
        assertEquals(4500,
        DoseCalculator.calculateEDDFinal(800, 800), 0.0000000001);
    }

    @Test // under cap
    public void eddTest2() {
        assertEquals(2500,
        DoseCalculator.calculateEDDFinal(50, 50), 0.0000000001);
    }

    @Test
    public void obeseDoseTest1() {
        assertEquals(1500,
        DoseCalculator.calculateObese(100, 15),0.0000000001);
    }

    @Test
    public void obeseDoseTest2() {
        assertEquals(3000,
        DoseCalculator.calculateObese(120, 25),0.0000000001);
    }

    @Test
    public void obeseDoseTest3() {
        assertEquals(3000,
        DoseCalculator.calculateObese(150, 20),0.0000000001);
    }
}
