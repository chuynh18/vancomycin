package yolo.tbv.vancomycin;

// There are many magic numbers in this class.
// Unfortunately, I can't improve on that.  I don't know why the numbers are what they are.
// These formulas were taken straight from a spreadsheet.
// I can only assume these numbers were empirically discovered.

public final class InitialDoseCalculator {
    // Calculate max CrCL (if userInput over 150, CrCl is 150, else go with userInput)
    private static double capCrClAt150(double inputCrCl) {
        if (inputCrCl > 150) {
            return 150;
        }

        return inputCrCl;
    }

    // Ke is 0.00083*actualCrCL + 0.0044
    public static double calculateKe(double inputCrCl) {
        double actualCrCl = capCrClAt150(inputCrCl);
        double Ke = 0.00083 * actualCrCl + 0.0044;

        System.out.println("Ke: " + Ke);
        return Ke;
    }

    // Half-life is 0.693/Ke
    public static double calculateHL(double Ke) {
        double naturalLogOf2 = 0.693;
        double halfLife = naturalLogOf2 / Ke;
        System.out.println("Half-life: " + halfLife);
        return halfLife;
    }

    // Vd is 0.7 * bodyWeight
    public static double calculateVd(double bodyWeight) {
        double Vd = 0.7 * bodyWeight;
        System.out.println("Vd: " + Vd);
        return Vd;
    }

    public static double calculateClvanGeneral(double Ke, double Vd) {
        double clvanGeneral = Ke * Vd;
        System.out.println("ClvanGeneral: " + clvanGeneral);
        return clvanGeneral;
    }

    public static double calculateClvanObese(double age, double scr, double sexIdMinus1, double bodyWeight) {
        double clvanObese = 9.565 - (0.078 * age) - (2.009 * scr) + (1.09 * sexIdMinus1) + (0.04 * Math.pow(bodyWeight, 0.75));
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

        System.out.println("Clvan final: " + clvanFinal);
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

    public static double calculateCrCl(double sexIdMinus1, double age, double weight, double scr) {
        double crcl = ((140 - age) * weight) / (72 * scr);

        if (sexIdMinus1 == 0) {
            System.out.println("Is female");
            crcl *= 0.85;
        } else {
            System.out.println("Is male");
        }

        return capCrClAt150(crcl);
    }
}