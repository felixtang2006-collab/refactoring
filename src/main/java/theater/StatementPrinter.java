package theater;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

/**
 * This class generates a statement for a given invoice of performances.
 */
public class StatementPrinter {
    private Invoice invoice;
    private Map<String, Play> plays;

    public StatementPrinter(Invoice invoice, Map<String, Play> plays) {
        this.invoice = invoice;
        this.plays = plays;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public Map<String, Play> getPlays() {
        return plays;
    }

    /**
     * Returns a formatted statement of the invoice associated with this printer.
     * @return the formatted statement
     * @throws RuntimeException if one of the play types is not known
     */
    public String statement() {
        int totalAmount = 0;
        int volumeCredits = 0;
        final StringBuilder result = new StringBuilder(
                "Statement for " + invoice.getCustomer() + System.lineSeparator());

        volumeCredits = getTotalVolumeCredits(volumeCredits);
        totalAmount = getTotalAmount(totalAmount);
        for (Performance performance : invoice.getPerformances()) {
            final Play play = plays.get(performance.getPlayID());

            // print line for this order
            result.append(String.format(
                    "  %s: %s (%s seats)%n", play.getName(), usd(
                            getAmount(performance, play)), performance.getAudience()));
        }
        result.append(String.format("Amount owed is %s%n", usd(totalAmount)));
        result.append(String.format("You earned %s credits%n", volumeCredits));
        return result.toString();
    }

    private int getTotalAmount(int totalAmount) {
        for (Performance performance : invoice.getPerformances()) {
            final Play play = plays.get(performance.getPlayID());

            final int thisAmount = getAmount(performance, play);

            totalAmount += thisAmount;
        }
        return totalAmount;
    }

    private int getTotalVolumeCredits(int volumeCredits) {
        for (Performance performance : invoice.getPerformances()) {
            final Play play = plays.get(performance.getPlayID());

            volumeCredits = getVolumeCredits(performance, volumeCredits, play);

        }
        return volumeCredits;
    }

    private static String usd(int totalAmount) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(totalAmount / Constants.PERCENT_FACTOR);
    }

    private static int getVolumeCredits(Performance performance, int volumeCredits, Play play) {
        // add volume credits
        int credits = 0;
        credits = Math.max(performance.getAudience() - Constants.BASE_VOLUME_CREDIT_THRESHOLD, 0);
        // add extra credit for every five comedy attendees
        if ("comedy".equals(play.getType())) {
            credits += performance.getAudience() / Constants.COMEDY_EXTRA_VOLUME_FACTOR;
        }
        return volumeCredits + credits;
    }

    private static int getAmount(Performance performance, Play play) {
        int thisAmount;
        switch (play.getType()) {
            case "tragedy":
                thisAmount = Constants.TRAGEDY_BASE_AMOUNT;
                if (performance.getAudience() > Constants.TRAGEDY_AUDIENCE_THRESHOLD) {
                    thisAmount += Constants.HISTORY_OVER_BASE_CAPACITY_PER_PERSON * (
                            performance.getAudience() - Constants.TRAGEDY_AUDIENCE_THRESHOLD);
                }
                break;
            case "comedy":
                thisAmount = Constants.COMEDY_BASE_AMOUNT;
                if (performance.getAudience() > Constants.COMEDY_AUDIENCE_THRESHOLD) {
                    thisAmount += Constants.COMEDY_OVER_BASE_CAPACITY_AMOUNT
                            + (Constants.COMEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (performance.getAudience() - Constants.COMEDY_AUDIENCE_THRESHOLD));
                }
                thisAmount += Constants.COMEDY_AMOUNT_PER_AUDIENCE * performance.getAudience();
                break;
            default:
                throw new RuntimeException(String.format("unknown type: %s", play.getType()));
        }
        return thisAmount;
    }
}
