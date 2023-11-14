---
layout: page
title: Developer Guide
---

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* Libraries used: [JavaFX](https://openjfx.io/), [Jackson](https://github.com/FasterXML/jackson), [JUnit5](https://github.com/junit-team/junit5)
* This project is based on the AddressBook-Level3 project created by the [SE-EDU initiative](https://se-education.org).

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<img src="images/ArchitectureDiagram.png" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<img src="images/ComponentManagers.png" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

![Structure of the UI Component](images/UiClassDiagram.png)

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `DashboardDisplay`, `ClientDisplay`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<img src="images/LogicClassDiagram.png" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

![Interactions Inside the Logic Component for the `delete 1` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.
</div>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<img src="images/ParserClasses.png" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<img src="images/ModelClassDiagram.png" width="800" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* stores a `UniqueReminderList` object that represents the reminders and a `ReminderScheduler` that periodically updates the reminders. This is exposed to the outside as a `ReadOnlyReminderList` objects.
* stores a `Dashboard` object that represents the statistics of the address book.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<div markdown="span" class="alert alert-info">:information_source: **Note:** A simpler model of the Person class is given below. 
Firstly the Addressbook a list of all the `Person` objects in the `AddressBook`. It also has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<img src="images/BetterModelClassDiagram.png" width="450" />

</div>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<img src="images/StorageClassDiagram.png" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.addressbook.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Interactions Feature

As a CRM software, taking notes of previous interactions with customers as well as their different outcomes is one of the most important features. It will allow the user to keep track of the progress of their interactions with their customers and help them to better plan their future interactions with them.

This section will describe in details the current implementation and design considerations of the interactions feature.

#### Current Implementation

The Interaction feature is implemented using the `Interaction` class. A `Person` has a list of interactions and interactions have the following fields:

![InteractionClassDiagram](images/InteractionClassDiagram.png)

`InteractionNote` will store the details of the interaction.
`Outcome` is an Enumeration used to store the outcome of the interaction.
There is also a `date` field to store the date of the interaction. This is set to the current date when the interaction is created.

When the user executes the `interaction` command, an `Interaction` is created and added to the `Person`'s list of interactions. The new `Interaction` is then displayed in the `PersonCard` automatically.

Interactions play a key role in the reminder feature, recall that a follow-up is made X weeks after the last interaction. The `Interaction` class has a `getFollowUpDate()` method that is used to calculate the date of the next follow up for the `Person`. It does this by getting the latest interaction date and adding the follow up period of the `Person` to it.

### Dashboard Feature

Dashboard shows the statistics of the current address book as specified in the user stories or other agreed upon metrics. 

#### Current Implementation

It is currently implemented as a `DashboardCommand` that is executed by the `LogicManager` when the user enters the `dashboard` command. This command returns a `CommandResult` object that contains the following statistics:
- Total number of interactions across all persons.
- Percentage of person with `INTERESTED` outcome.
- Percentage of person with `NOT_INTERESTED` outcome.

The following sequence diagram shows how the dashboard feature is currently implemented.
![Ui](images/UiSequenceDiagram.png)

#### Design Considerations

### Viewing a client's full profile

As we add more attributes and interactions with clients, we will need a better way to view all of the information associated with a client. Therefore instead of displaying all information in a `PersonCard` within the list of clients, we will need a new component to display the client profile in a better way.

The new `ClientProfilePanel` UI component is the replacement for displaying client profiles. Beside the require and optional fields, the user can also see all of their past interactions with the client.

The client profile to display is tracked using a `SimpleObjectProperty` inside a `Model`. Since a `SimpleObjectProperty` is an `ObservableValue`, we can add listeners to its change event and update our UI whenever the currently selected profile changes.

We currently support 2 ways of viewing a client's profile:

1. By clicking on a client's card from the client list
2. By using the `view` command

Both methods will update the currently selected client, which will then trigger a listener to update the UI to show the profile panel.

To exit out of the profile view, the user can enter the `list` command, which will hide the profile panel and restore the client list to occupy the full window width.


### Reminder feature

#### Proposed Implementation

The proposed Reminder mechanism is facilitated by `Reminder` and `UniqueReminderList` as shown below

![ReminderClassDiagram](images/ReminderClassDiagram.png)

Step 1. The user launches the application and the User's data such as interactions and leads are loaded. `Reminder` is derived from the `Person.lead` and `Person.interactions` data, with the `Reminder` date being X + the date of the latest `Interaction`, and X is dependant on the `Lead`'s `LeadCategory`. These initial list of `Reminder` will be stored in the `UniqueReminderList`.

Step 2. The user executes `interaction 1 o/INTERESTED Thinking of giving it a shot` command to add an interaction to the 1st person in the address book. The `Reminder` should later be updated to the latest `Interaction` date + X. Note this change should also happen when `EditCommand` is executed since the `Person.lead` or `Person.name` might have been changed.

Step 3. When displaying the `Reminder`'s, the `UniqueReminderList` will then be updated and should be sorted by date, and the `Reminder` that is due the soonest should be displayed first. 

Step 4. `UniqueReminderList` should also be updated daily with `ReminderScheduler` class. This will ensure that if the user leaves the dashboard open for prolonged periods of time, the reminders will be able to sync up to account for the time passed

#### Design considerations:

**Aspect: How Reminders executes:**

* **Alternative 1 (current choice):** Updates the reminderList only when the dashboard is being viewed
    * Pros: No need to update every single time a command is executed, increasing performance
    * Cons: In the future, if we want to stay on the dashboard view when other commands are executed, we would require the setting of the "DirtyFlag" in order to update the reminderList

* **Alternative 2:** Updates Reminders when commands are executed
    * Pros: Reminders will always be up to date
    * Cons: Will require more processing power and might slow down the application


### Edit Single field Macro feature

Keying in the ***edit*** command can be a little tedious to use especially if the user only want to edit a single field of a client profile. The edit single field is a macro to edit a single field of a client profile. This command is special because its command word is the same as the field you want to edit. 

#### Current Implementation

The proposed Edit Single field Macro mechanism is facilitated by `AddressBookParser` and `EditCommandMacroParser`

`AddressBookParser` contains an enum with the different person fields to expect as command words.

Then in its parseCommand method, if it is not another valid command word, it will check in the default if it is a valid person field by iterating through the enum. If it is, it will create a `EditCommandMacroParser` and pass the command to it through its constructor.

`EditCommandMacroParser` will then take in the input, and parse the `index` and the argument to the command without any prefix similar to the `FindCommand`. It will then create a `EditCommand` with only the `commandWord` changed to its single argument input and pass said `EditCommand` to the `LogicManager` to execute.

`EditCommandMacroParser` does the argument checking using `ParseUtil` commands to check if they are valid arguments. However other checks like whether it is a valid index or duplicated person is done by the `EditCommand` itself.

#### Design considerations:
This is a huge step up in terms of code efficiency as compared to creating a new command for each field.

--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* has a need to track information about business clients
* prefer desktop apps over other types
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: manage useful information about a clients faster than a typical mouse/GUI driven app


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …      | I want to …                                                                                      | So that I can…                                                     |
|----------|-------------|--------------------------------------------------------------------------------------------------|--------------------------------------------------------------------|
| `* * *`  | user        | record my client’s contact information                                                           | conveniently refer to it later                                     |
| `* * *`  | user        | access the full details of a particular client's information comprehensively                     | aid my future interaction with this client                         |
| `* * *`  | user        | delete client profiles                                                                           | remove entries that I no longer need                               |
| `* *`    | user        | edit client profiles                                                                             | record any changes in my client's information                      |
| `* *`    | user        | add notes of my meetings with my clients                                                         | track details for future interactions with my client               |
| `* *`    | user        | log the outcomes of my client interactions (e.g. interested, not interested, follow-up required) | track progress of client interactions                              |
| `* *`    | user        | mark a client as a "hot lead", "warm lead" or "cold lead"                                        | gauge the sales potential of the client                            |
| `* *`    | new user    | have example client data already present in the app                                              | have a reference or play around in the app to get familiar with it |
| `* *`    | salesperson | view analytics about my performance                                                              | continually improve                                                |
| `* *`    | salesperson | look at a summary of what's coming up next                                                       | plan my day around them                                            |


### Use cases

(For all use cases below, the **System** is the `Connectify` and the **Actor** is the `user`, unless specified otherwise)

For each use case, "User is in list view" means an expectation that user has already executed the `list` command and is viewing the list of clients. However the user could be in the dashboard view, in this case the command will still work, but the user will be redirected to the list view after the command finishes executing. 

To enter the list view see Use case UC01:

**Use case: UC01 - Enter list view**

**MSS**

1.  User opens the app and is in dashboard view
2.  User requests to enter list view
3.  Connectify shows the list view

    Use case ends.

**Use case: UC02 - Delete a client**

**MSS**

1.  User requests to delete a specific client in the list
2.  Connectify deletes the client

    Use case ends.

**Extensions**

* 1a. The list is empty.

  Use case ends.

* 1a. The given index is invalid.

    * 1a1. AddressBook shows an error message.

      Use case resumes at step 2.


**Use case: UC02 - Add a client interaction**

**MSS**

1.  User requests create a client interaction
2.  Connectify adds the interaction to the client profile

    Use case ends.

**Extensions**

* 1a. The user does not exist.

    * 1a1. Connectify shows an error message.

      Use case resumes at step 2.

* 1b. The entered client interaction is invalid
   
    * 1b1. Connectify shows an error message.
   
      Use case resumes at step 2.


**Use case: UC03 - Mark a client as “Cold”, “Warm” or “Hot” Leads**

**MSS**

1.  User requests to list clients
2.  Connectify shows a list of clients
3.  User requests to mark a client as “Cold”, “Warm” or “Hot” Lead
4.  Connectify displays the updated client profile

    Use case ends.

**Extensions**

* 1a. The user does not exist.

    * 1a1. Connectify shows an error message.

      Use case resumes at step 2.

* 1b. The lead category is key'ed in wrong or empty
   
    * 1b1. Connectify shows an error message.
   
      Use case resumes at step 2.

**Use case: UC04 - View a client’s full profile**

**MSS**

1. User requests to list clients
2. Connectify shows a list of clients
3. User requests to view a client’s full profile, this can be done with the `view` command or by clicking on the client’s card
4. Connectify shows the client’s full profile

    Use case ends.

**Extensions**

* 3a. The user does not exist. (because the index was invalid)

    * 3a1. Connectify shows an error message.

      Use case resumes at step 2.

**Use case: UC05 - Edit a client’s profile**

**Guarantees:**

* The client’s profile will be updated only if the command is executed successfully.

**MSS**

1. 

### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `11` or above installed.
2.  Should be able to hold up to 1000 persons without a noticeable sluggishness in performance for typical usage.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
4.  The system should work in both 32-bit and 64-bit environments.
5.  Response time for fetching a contact's details should not exceed 1.5 seconds. 
6.  Search operations should return results within 2 seconds for queries against the full dataset.
7.  The system must be backward compatible with data generated from previous versions of the software.

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, OS-X
* **Hot/Warm/Cold Lead**: A hot lead is a potential client who is ready to buy. A warm lead is a potential client who is interested in buying. A cold lead is a potential client who is not ready to buy.

*{More to be added}*

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<div markdown="span" class="alert alert-info">:information_source: **Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</div>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

1. _{ more test cases …​ }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{explain how to simulate a missing/corrupted file, and the expected behavior}_

1. _{ more test cases …​ }_
