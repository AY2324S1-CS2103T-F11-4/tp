package seedu.address.model.person.lead;

import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents the potential of a client.
 */
public class Lead {
    public static final String MESSAGE_CONSTRAINTS = "Lead should only take values hot|warm|cold";
    private final LeadType leadType;

    /**
     * Returns a lead given a string. Checks if the string is a valid lead;
     *
     * @param lead the string representation for the lead.
     */
    public Lead(String lead) {
        checkArgument(isValidLead(lead.toUpperCase()), MESSAGE_CONSTRAINTS);
        this.leadType = Enum.valueOf(LeadType.class, lead.toUpperCase());
    }

    public Lead(LeadType lead) {
        this.leadType = lead;
    }

    /**
     * Determines whether a given string is a valid lead.
     * A valid lead is either "hot", "warm" or "cold".
     *
     * @param lead the string to check
     * @return true if the string is a valid lead, false otherwise
     */
    public static boolean isValidLead(String lead) {
        return lead.equalsIgnoreCase("HOT")
                || lead.equalsIgnoreCase("WARM")
                || lead.equalsIgnoreCase("COLD");
    }

    @Override
    public String toString() {
        return leadType.name();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Lead)) {
            return false;
        }

        Lead otherLead = (Lead) other;
        return leadType.equals(otherLead.leadType);
    }

    @Override
    public int hashCode() {
        return leadType.hashCode();
    }
}
