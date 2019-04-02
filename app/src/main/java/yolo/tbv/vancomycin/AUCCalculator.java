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

    public static double calculateTrueTrough(double truePeak, double ke, double initialDosingFreq, double infusionDuration) {
        return truePeak * Math.exp(-ke * (initialDosingFreq - infusionDuration));
    }
}
