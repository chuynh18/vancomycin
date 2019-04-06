package yolo.tbv.vancomycin;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

// handles all the annoying math and related calculations necessary to compute AUC dosing
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

    public static double calculateVd(
            double initialDose,
            double infusionDuration,
            double ke,
            double truePeak,
            double trueTrough) {
        double expKeAndInfusionDuration = Math.exp(ke * -infusionDuration);

        double term1 = initialDose/infusionDuration;
        double term2 = 1 - expKeAndInfusionDuration;
        double term3 = ke * (truePeak - (trueTrough * expKeAndInfusionDuration));

        return term1 * term2 / term3;
    }

    // calculates estimated AUC24 based on initial dose and measured peak and trough
    public static double calculateAUC24(
            double truePeak,
            double trueTrough,
            double infusionDuration,
            double ke,
            double initialDoseFreq
    ) {
        double aucInf = (truePeak + trueTrough) / 2 * infusionDuration;
        double aucElim = (truePeak - trueTrough) / ke;

        return (aucInf + aucElim) * 24 / initialDoseFreq;
    }

    public static double calculateSuggestedT(double halfLife) {
        double suggestedT;

        if (halfLife <= 4) {
            suggestedT = 6;
        } else if (halfLife > 4 && halfLife <= 5.5) {
            suggestedT = 8;
        } else if (halfLife > 5.5 && halfLife <= 11) {
            suggestedT = 12;
        } else {
            suggestedT = 24;
        }

        return suggestedT;
    }

    public static double calculateVancoDose(
            double goalAUC24,
            double calculatedAUC24,
            double initialDose,
            double initialDoseFreq,
            double suggestedT
    ) {
        double initialTDD = initialDose * 24 / initialDoseFreq;
        double suggestedVancoTdd = goalAUC24 / calculatedAUC24 * initialTDD;

        return suggestedVancoTdd * suggestedT / 24;
    }

    public static double calculatePredictedPeak(
            double vVanco, // vVanco is same as Vd (L) in the spreadsheet
            double chosenDose,
            double chosenInfusionDuration,
            double ke,
            double chosenDosingInterval
    ) {
        double clVanco = ke * vVanco;
        double term1 = chosenDose / (clVanco * chosenInfusionDuration);
        double term2 = 1 - Math.exp(-ke * chosenInfusionDuration);
        double term3 = 1 - Math.exp(-ke * chosenDosingInterval);

        return term1 * term2 / term3;
    }

    public static double calculatePredictedTrough(
            double calculatedPredictedPeak,
            double ke,
            double chosenDosingInterval,
            double chosenInfusionDuration
    ) {
        return calculatedPredictedPeak * Math.exp(-ke * (chosenDosingInterval - chosenInfusionDuration));
    }

    // calculates revised AUC24 based on revised dosing regimen
    public static double calculatePredictedAuc24(
            double predictedPeak,
            double predictedTrough,
            double infusionDuration,
            double ke,
            double chosenDosingInterval
    ) {
        double predictedAucInf = (predictedPeak + predictedTrough) / 2 * infusionDuration;
        double predictedAucElim = (predictedPeak - predictedTrough) / ke;
        return (predictedAucInf + predictedAucElim) * 24 / chosenDosingInterval;
    }
}