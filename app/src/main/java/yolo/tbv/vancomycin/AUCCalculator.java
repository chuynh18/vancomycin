package yolo.tbv.vancomycin;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public final class AUCCalculator {
    public static double hoursBetween(LocalDateTime start, LocalDateTime end) {
        // get user's zoneId
        ZoneId zoneId = ZoneId.systemDefault();

        // convert LocalDateTimes to ZonedDateTimes
        ZonedDateTime zoneStart = start.atZone(zoneId);
        ZonedDateTime zoneEnd = end.atZone(zoneId);

        // use datetime objects to compute delta t in minutes
        long minutesBetweenStartAndEnd = ChronoUnit.MINUTES.between(
            zoneStart,
            zoneEnd
        );

        return (double) minutesBetweenStartAndEnd / 60;
    }

    public static double calculateKe(double measuredPeak, double measuredTrough, double deltaTimeLevelOneAndLevelTwo) {
        return Math.log(measuredPeak / measuredTrough) / deltaTimeLevelOneAndLevelTwo;
    }

    public static double calculateTruePeak(double measuredPeak, double ke, double levelOneTime, double infusionDuration) {
        return measuredPeak / Math.exp(ke * (infusionDuration - levelOneTime));
    }

    public static double calculateTrueTrough(double truePeak, double ke, double initialDoseFreq, double infusionDuration) {
        return truePeak * Math.exp(-ke * (initialDoseFreq - infusionDuration));
    }

    public static double calculateVd(double initialDose, double infusionDuration, double ke, double truePeak, double trueTrough) {
        double expKeAndInfusionDuration = Math.exp(ke * -infusionDuration);

        double term1 = initialDose/infusionDuration;
        double term2 = 1 - expKeAndInfusionDuration;
        double term3 = ke * (truePeak - (trueTrough * expKeAndInfusionDuration));

        return term1 * term2 / term3;
    }

    public static double calculateAUC24(double truePeak, double trueTrough, double infusionDuration, double ke, double initialDoseFreq) {
        double aucInf = (truePeak + trueTrough) / 2 * infusionDuration;
        double aucElim = (truePeak - trueTrough) / ke;

        return (aucInf + aucElim) * 24 / initialDoseFreq;
    }
}
