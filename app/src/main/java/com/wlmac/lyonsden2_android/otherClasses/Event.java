package com.wlmac.lyonsden2_android.otherClasses;

import java.util.Date;

/**
 * This data-class holds information about an individual event from the eventBank HashMap
 * from the LyonsCalendar class. It contains information like the title, description, date/time or
 * location of a particular event. To use this data-class, you must first create it using the
 * default constructor and then fill out its information using the given setter methods.
 *
 * @author sketch204
 * @version 1, 2016/08/14
 */
public class Event {
    /** The title of the event. */
    private String title = "";
    /** The description of the event. */
    private String description = "";
    /** The start date of the event. */
    private Date startDate = null;
    /** The end date of the event. */
    private Date endDate = null;
    /** The location of the event. */
    private String location = "";

    /** Creates an empty event. */
    public Event () {

    }

    /**
     * Sets the title of this event the passed title.
     * @param title The new title.
     */
    public void setTitle(String title) {
        this.title = formatField(title);
    }

    /**
     * Sets the description of this event the passed description.
     * @param description The new description.
     */
    public void setDescription(String description) {
        this.description = formatField(description);
    }

    /**
     * Sets the starting date of this event the passed date.
     * @param startDate The new starting date.
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Sets the ending date of this event the passed date.
     * @param endDate The new ending date.
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * Sets the location of this event the passed location.
     * @param location The new location.
     */
    public void setLocation(String location) {
        this.location = formatField(location);
    }

    /** Removes all unwanted characters from the fields, such as '\r' '\n' etc... */
    private String formatField (String field) {
        if (field.contains("\r")) {
            field = field.replaceAll("\r", "");
        } if (field.contains("\n")) {
            field = field.replaceAll("\n", "");
        } if (field.contains("\\")) {
            field = field.replaceAll("\\\\", "");
        }
        return field;
    }

    /**
     * Returns the title of this event.
     * @return The title of this event.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the description of this event.
     * @return The description of this event.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the start date of this event.
     * @return The start date of this event.
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Returns the end date of this event.
     * @return The end date of this event.
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Returns the location of this event.
     * @return The location of this event.
     */
    public String getLocation() {
        return location;
    }
}