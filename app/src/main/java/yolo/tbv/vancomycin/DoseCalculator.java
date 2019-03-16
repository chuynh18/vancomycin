package yolo.tbv.vancomycin;

public final class DoseCalculator {
    // Calculate max CrCL (if userInput over 150, CrCl is 150, else go with userInput)
    private static double calculateCrCl(double inputCrCl) {
        if (inputCrCl > 150) {
            return 150;
        }

        return inputCrCl;
    }

    // Ke is 0.00083*actualCrCL + 0.0044
    public static double calculateKe(double inputCrCl) {
        double actualCrCl = calculateCrCl(inputCrCl);
        double Ke = 0.00083*actualCrCl + 0.0044;

        System.out.println("Ke: " + Ke);
        return Ke;
    }

    // Half-life is 0.693/Ke
    public static double calculateHL(double Ke) {
        double halfLife = 0.693/Ke;
        System.out.println("Half-life: " + halfLife);
        return halfLife;
    }

    // Vd is 0.7 * bodyWeight
    public static double calculateVd(double bodyWeight) {
        double Vd = 0.7*bodyWeight;
        System.out.println("Vd: " + Vd);
        return Vd;
    }

    public static double calculateClvanGeneral(double Ke, double Vd) {
        double clvanGeneral = Ke*Vd;
        System.out.println("ClvanGeneral: " + clvanGeneral);
        return clvanGeneral;
    }

    public static double calculateClvanObese(double age, double scr, double sexIdMinus1, double bodyWeight) {
        double clvanObese = 9.565 - (0.078*age) - (2.009*scr) + (1.09*sexIdMinus1) + (0.04*Math.pow(bodyWeight, 0.75));
        System.out.println("Clvan obese: " + clvanObese);
        return clvanObese;
    }

    public static double calculateCappedClvanFinal(boolean isObese, double clvanGeneral, double clvanObese) {
        System.out.println("isObese: " + isObese);

        double clvanFinal = clvanGeneral;

        if (isObese) {
            clvanFinal = clvanObese;
        }

        if (clvanFinal > 9) {
            return 9;
        }

        System.out.println("clvan final: " + clvanFinal);
        return clvanFinal;
    }

    public static double calculateEDDFinal(double clvancoFinal, double targetAUC) {
        double calculatedEDD = clvancoFinal * targetAUC;
        System.out.println("calculated EDD: " + calculatedEDD);

        if (calculatedEDD > 4500) {
            System.out.println("recalculated EDD: capped at 4500");
            return 4500;
        }

        return calculatedEDD;
    }

    public static double calculateObese(double bodyWeight, double mgkg) {
        return mgkg*bodyWeight;
    }

}