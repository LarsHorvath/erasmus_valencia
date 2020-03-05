package com.example.erasmusvalencia;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class Event implements Comparable<Event> {
    public static HashMap<Integer, Event> allEvents = new HashMap<>();

    static final String TAG = "Event";

    private int id;
    private String title;
    private String company;
    static public final int imagesrc[] = {R.drawable.esnupv, R.drawable.esnuv, R.drawable.happyerasmus, R.drawable.soyerasmus,
            R.drawable.erasmuslife, R.drawable.questionmark};
    private int companyID = imagesrc.length-1;


    private String description;
    private String location;
    private String url;
    private Calendar startDate;
    private Calendar endDate;
    private boolean favourite;
    private boolean custom;
    public static final int FILTER_TITLE = 1;
    public static final int FILTER_DESCRIPTION = 2;
    public static final int FILTER_LOCATION = 3;
    public static final int FILTER_COMPANY = 4;
    public static final int FILTER_DATE = 5;
    public static final int FILTER_FAVOURITE = 6;
    public static final int FILTER_TEXT_SEARCH = 7;

    public Event() {
        title = "no title";
        company = "no company";
        description = "no description";
        location = "no location";
        startDate = Calendar.getInstance();
        endDate = Calendar.getInstance();
        startDate.clear();
        endDate.clear();
        id = generateID();
        allEvents.put(id, this);
        favourite = false;
    }

    public int compareTo(Event other) {
        return startDate.compareTo(other.startDate);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    public void findCompany() {
        String[] companies = {"ESN", "ESN", "Happy Erasmus", "Soy Erasmus", "Erasmus Life"};
        for (int i = 0; i < companies.length; i++) {
            if (title.contains(companies[i]) ||  location.contains(companies[i]) || description.contains(companies[i])) {
                this.company = companies[i];
                this.companyID = i;
            }
        }
        if (company.equals("ESN") && (title.contains("UV") || location.contains("UV") || description.contains(("UV")))) {
            this.companyID = 1;
            this.company ="ESN UV";
        }
        else if (company.equals("ESN")) {
            this.companyID = 0;
            this.company ="ESN UPV";
        }
    }

    public void findLink() {
        String[] parts = description.split("\n");
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].contains("https://www.face")) {
                this.url = parts[i];
            }
        }
    }

    public static String toJson(final HashMap<Integer, Event> events) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(events);
    }

    public static HashMap<Integer, Event> parseFromJSON(final String json) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type type = new TypeToken<HashMap<Integer,Event>>() {}.getType();
        return gson.fromJson(json, type);
    }

    /**
     * Parses a String that is formatted as a JSON to generate an ArrayList of type Event
     * @param JSONString the String to be parsed
     * @return an ArrayList containing Event objects that were in the String
     */
    public static ArrayList<Event> parseFromJSONString(final String JSONString) {
        ArrayList<Event> result = new ArrayList<>();
        allEvents = new HashMap<>();
        JsonElement jsonElement = new JsonParser().parse(JSONString);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonArray items = jsonObject.getAsJsonArray("items");

        for (int i = 0; i<items.size(); i++) {
            JsonObject item = items.get(i).getAsJsonObject();
            Event event = new Event();
            event.setTitle(item.getAsJsonPrimitive("summary").getAsString());
            event.setDescription(item.getAsJsonPrimitive("description").getAsString());
            event.setLocation(item.getAsJsonPrimitive("location").getAsString());
            event.setStartDate(parseCalendarString(item.getAsJsonObject("start").getAsJsonPrimitive("dateTime").getAsString()));
            event.setEndDate(parseCalendarString(item.getAsJsonObject("end").getAsJsonPrimitive("dateTime").getAsString()));
            event.findCompany();
            event.findLink();
            event.setCustom(false);
            result.add(event);
        }
        return result;
    }

    /**
     * filters an ArrayList of Event according to the filter criteria specified
     * @param events the ArrayList containing the events
     * @param filters an array of Objects that are either identifiers for the type of filter applied or the filter itself
     * @return an ArrayList containing all the Event objects from event that match all the filters
     * @throws IllegalArgumentException
     */
    public static ArrayList<Event> filterEvents(final ArrayList<Event> events, final Object... filters) throws IllegalArgumentException {
        ArrayList<Event> result = new ArrayList<>();
        for (Event event : events) {
            if (eventMatches(event, filters)) result.add(event);
        }
        return result;
    }

    /**
     * Checks if an event matches filter criteria specified
     * @param event the event to be check
     * @param filters an array of Objects that are either identifiers for the type of filter applied or the filter itself
     * @return true iff the events matches all the filter
     * @throws IllegalArgumentException
     */
    public static boolean eventMatches(final Event event, final Object... filters) throws IllegalArgumentException {
        int i = 0;
        while (i<filters.length) {
            if (filters[i] instanceof Integer) {
                switch ((int) filters[i++]) {
                    case FILTER_TITLE:
                        if (!(filters[i] instanceof String)) throw new IllegalArgumentException();
                        if (!event.getTitle().contains((String) filters[i])) return false;
                        break;
                    case FILTER_DESCRIPTION:
                        if (!(filters[i] instanceof String)) throw new IllegalArgumentException();
                        if (!event.getDescription().contains((String) filters[i])) return false;
                        break;
                    case FILTER_LOCATION:
                        if (!(filters[i] instanceof String)) throw new IllegalArgumentException();
                        if (!event.getLocation().contains((String) filters[i])) return false;
                        break;
                    case FILTER_COMPANY:
                        if (!(filters[i] instanceof String)) throw new IllegalArgumentException();
                        if (!event.getCompany().contains((String) filters[i])) return false;
                        break;
                    case FILTER_DATE:
                        if (!(filters[i] instanceof Calendar || !(filters[i+1] instanceof Calendar))) throw new IllegalArgumentException();
                        if (((Calendar) filters[i]).compareTo((Calendar) filters[i+1]) >= 0) {
                            if (((Calendar) filters[i++]).compareTo(event.getStartDate()) > 0) return false;
                            Log.i(TAG, "eventMatches: Date presented: " + ((Calendar) filters[i-1]).getDisplayNames(Calendar.DAY_OF_YEAR,Calendar.SHORT,Locale.ENGLISH));
                            Log.i(TAG, "eventMatches: this Date to filter: " + (event.getStartDate()).getDisplayNames(Calendar.DAY_OF_YEAR,Calendar.SHORT,Locale.ENGLISH));
                            Log.i(TAG, "eventMatches: result: " + ((Calendar) filters[i-1]).compareTo((Calendar) filters[i]));
                        }
                        else if (((Calendar) filters[i]).compareTo(event.getStartDate()) > 0 || event.getStartDate().compareTo((Calendar) filters[++i]) >= 0) return false;
                        break;
                    case FILTER_FAVOURITE:
                        if (!(filters[i] instanceof Boolean)) throw new IllegalArgumentException();
                        return event.isFavourite() == (Boolean) filters[i];
                    case FILTER_TEXT_SEARCH:
                        if (!(filters[i] instanceof String)) throw new IllegalArgumentException();
                        if (!containsIgnoreCase(event.getTitle(),((String) filters[i])) && !containsIgnoreCase(event.getLocation(),((String) filters[i])) && !containsIgnoreCase(event.getCompany(),((String) filters[i]))) return false;
                        break;
                }
                i++;
            }
            else {
                throw new IllegalArgumentException();
            }
        }
        return true;
    }

    private static boolean containsIgnoreCase(String string, String sub) {
        return string.toLowerCase().contains(sub.toLowerCase());
    }

    public int getCompanyID() {
        return companyID;
    }

    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    //id is 6 digit number
    static public int generateID() {
        while (true) {
            int id = (int) (Math.random()*1000000);
            if (!allEvents.containsKey(id)) return id;
        }
    }

    public boolean isCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public String durationToString() {
        return String.format(Locale.ENGLISH,"From %s to %s", startDate.toString(), endDate.toString());
    }

    /**
     * Parses a String as a date and saves the relevant data to this Date object. See param for format. This method
     * doesn't check the sense of the values
     * @param date the String in the format "yyyy-mm-ddThh:mm:ssZ" or "yyyy-mm-dd" to be parsed
     */
    public static Calendar parseCalendarString(final String date) {
        int year = Integer.parseInt(date.substring(0,4));
        int month = Integer.parseInt(date.substring(5,7)) -1;
        int day = Integer.parseInt(date.substring(8,10));
        int hour = 0;
        int minute = 0;
        if (date.length()>=16) {
            hour = Integer.parseInt(date.substring(11,13));
            minute = Integer.parseInt(date.substring(14,16));
        }
        Calendar result = Calendar.getInstance();
        result.set(year, month, day, hour, minute);
        return result;
    }

    public static final int DAY = 0;
    public static final int DAY_AND_TIME = 1;
    public static final int NAME_AND_DAY = 2;
    public static final int NAME_AND_DAY_AND_TIME = 3;

    public static String dayToString(Calendar cal, int sel) {
        switch (sel) {
            case DAY: return String.format(Locale.ENGLISH,"%02d.%02d.%04d", cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH)+1, cal.get(Calendar.YEAR));
            case DAY_AND_TIME: return String.format(Locale.ENGLISH,"%02d.%02d.%04d, %02d:%02d", cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH)+1, cal.get(Calendar.YEAR), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
            case NAME_AND_DAY: return String.format(Locale.ENGLISH,"%s %02d.%02d.%04d", DayOfWeek.of(cal.get(Calendar.DAY_OF_WEEK)), cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH)+1, cal.get(Calendar.YEAR));
            case NAME_AND_DAY_AND_TIME: return String.format(Locale.ENGLISH,"%s %02d.%02d.%04d, %02d:%02d", DayOfWeek.of((cal.get(Calendar.DAY_OF_WEEK) + 6) % 7).getDisplayName(TextStyle.SHORT, Locale.ENGLISH), cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH)+1, cal.get(Calendar.YEAR), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
            default: return "You're in idiot";
        }
    }

    public static String localeDateString(Calendar cal) {
        return String.format(Locale.ENGLISH,"%04d-%02d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) +1, cal.get(Calendar.DAY_OF_MONTH));
    }

}

