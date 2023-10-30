package seedu.address.model.reminder;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.person.Person;
import seedu.address.model.person.interaction.Interaction;
import seedu.address.model.reminder.exceptions.DuplicateReminderException;

/**
 * A list of Reminders that enforces uniqueness between its elements and does not allow nulls.
 * A Reminder is considered unique by comparing using {@code Reminder#equals(Reminder)}. This will allow the reminder
 * to be added or deleted to maintain the uniqueness of the ReminderList
 * Supports a minimal set of list operations.
 *
 */
public class UniqueReminderList implements Iterable<Reminder> {

    private static UniqueReminderList reminderList;

    private final ObservableList<Reminder> internalList;
    private final ObservableList<Reminder> internalUnmodifiableList;
    private final HashMap<Person, Reminder> personToReminderMap;

    /**
     * Constructor of UniqueReminderList
     */
    private UniqueReminderList() {
        internalList = FXCollections.observableArrayList();
        internalUnmodifiableList = FXCollections.unmodifiableObservableList(internalList);
        personToReminderMap = new HashMap<>();
    }

    /**
     * Returns true if the list contains an equivalent Reminder as the given argument.
     */
    public boolean contains(Reminder toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::equals);
    }

    /**
     * Factory method of UniqueReminderList
     *
     * @return Produced UniqueReminderList object that is a Singleton.
     */
    public static UniqueReminderList getInstance() {
        if (reminderList == null) {
            reminderList = new UniqueReminderList();
        }
        return reminderList;
    }

    /**
     * Adds a Reminder to the list.
     * The Reminder must not already exist in the list.
     */
    public void add(Person person, Interaction interaction) {
        requireAllNonNull(person, interaction);
        Reminder toAdd = new Reminder(person.getName(), person.getPhone(), person.getTags(), person.getLead(),
                interaction.getDate());
        if (contains(toAdd)) {
            throw new DuplicateReminderException();
        }

        if (personToReminderMap.containsKey(person)) {
            internalList.remove(personToReminderMap.get(person));
        }

        internalList.add(toAdd);
        personToReminderMap.put(person, toAdd);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<Reminder> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    /**
     * Returns the list of reminders after a specific date.
     *
     * @param date The date that which is used to retrieve the list of reminders.
     * @return the list of reminders mapped from the given date.
     */
    public ObservableList<Reminder> getRemindersAfterDate(Date date) {
        ObservableList<Reminder> reminderList = FXCollections.observableArrayList();
        List<Reminder> retrievedReminders = personToReminderMap.entrySet()
                .stream()
                .map(x -> x.getValue())
                .filter(a->a.compareTo(date) == 1)
                .collect(Collectors.toList());
        if (retrievedReminders == null) {
            return reminderList;
        }
        reminderList.addAll(retrievedReminders);
        return reminderList;
    }

    /**
     * Resets the internal list of reminders using the given list of persons.
     *
     * @param personList The persons from which the reminders are to be produced.
     */
    public void setReminders(ObservableList<Person> personList) {
        for (Person person : personList) {
            Interaction interaction = person.getLastInteraction();
            if (interaction != null) {
                this.add(person, interaction);
            }
        }
    }

    public Iterator<Reminder> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UniqueReminderList // instanceof handles nulls
                && internalList.equals(((UniqueReminderList) other).internalList));
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

}
