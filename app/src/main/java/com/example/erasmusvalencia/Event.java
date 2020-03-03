package com.example.erasmusvalencia;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class Event implements Comparable<Event> {
    public static HashMap<Integer, Event> allEvents = new HashMap<>();

    private int id;
    private String title;
    private String company;
    private int companyID = 4;


    private String description;
    private String location;
    private String url;
    private Date startDate;
    private Date endDate;
    private boolean favourite;
    private boolean custom;
    static public final String[] companies = {"ESN", "Happy Erasmus", "Soy Erasmus", "Erasmus Life"};
    static public final int imagesrc[] = {R.drawable.esn, R.drawable.happyerasmus, R.drawable.soyerasmus,
            R.drawable.erasmuslife, R.drawable.questionmark};
    public static final int FILTER_TITLE = 1;
    public static final int FILTER_DESCRIPTION = 2;
    public static final int FILTER_LOCATION = 3;
    public static final int FILTER_COMPANY = 4;
    public static final int FILTER_DATE = 5;
    public static final int FILTER_FAVOURITE = 6;

    public Event() {
        title = "no title";
        company = "no company";
        description = "no description";
        location = "no location";
        startDate = new Date();
        endDate = new Date();
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void findCompany() {
        for (int i = 0; i < companies.length; i++) {
            if (title.contains(companies[i]) ||  location.contains(companies[i]) || description.contains(companies[i])) {
                this.company = companies[i];
                this.companyID = i;
            }
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
            event.setStartDate(new Date(item.getAsJsonObject("start").getAsJsonPrimitive("dateTime").getAsString()));
            event.setEndDate(new Date(item.getAsJsonObject("end").getAsJsonPrimitive("dateTime").getAsString()));
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
                        if (!(filters[i] instanceof Date || !(filters[i+1] instanceof Date))) throw new IllegalArgumentException();
                        if (((Date) filters[i]).compareTo((Date) filters[i+1]) >= 0) {
                            if (((Date) filters[i++]).compareTo(event.getStartDate()) > 0) return false;
                        }
                        else if (((Date) filters[i]).compareTo(event.getStartDate()) > 0 || event.getStartDate().compareTo((Date) filters[++i]) >= 0) return false;
                        break;
                    case FILTER_FAVOURITE:
                        if (!(filters[i] instanceof Boolean)) throw new IllegalArgumentException();
                        return event.isFavourite() == (Boolean) filters[i];
                }
                i++;
            }
            else {
                throw new IllegalArgumentException();
            }
        }
        return true;
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

    public static class Date implements Comparable<Date> {
        private int minute;
        private int hour;
        private int day;
        private int month;
        private int year;

        public Date() {
            minute = hour = 0;
            day = month = 1;
            year = 2020;
        }

        public Date(int day, int month, int year, int hour, int minute) {
            this(day, month, year);
            this.minute = minute;
            this.hour = hour;
        }
        public Date(int day, int month, int year) {
            this(day, month);
            this.year = year;

        }
        public Date(int day, int month) {
            this();
            this.day = day;
            this.month = month;
        }


        public Date(String date) {
            parseString(date);
        }

        public int compareTo(Date other) {
            return Long.compare(this.toLong(), other.toLong());
        }

        /**
         * Converts a date to a single long value used to compare dates. Note it is NOt an actual representation, leap
         * years and months of different lengths not accounted for, just to get a different long value for any date
         * @return this Date objects long representation
         */
        private long toLong() {
            return ((((((long) this.year)*12 + this.month)*31 + this.day)*24 + this.hour)*60) + this.minute;
        }

        /**
         * Parses a String as a date and saves the relevant data to this Date object. See param for format. This method
         * doesn't check the sense of the values
         * @param date the String in the format "yyyy-mm-ddThh:mm:ssZ" or "yyyy-mm-dd" to be parsed
         */
        public void parseString(String date) {
            year = Integer.parseInt(date.substring(0,4));
            month = Integer.parseInt(date.substring(5,7));
            day = Integer.parseInt(date.substring(8,10));
            if (date.length()>=16) {
                hour = Integer.parseInt(date.substring(11,13));
                minute = Integer.parseInt(date.substring(14,16));
            }
            else {
                hour = minute = 0;
            }
        }

        public String toString() {
            return String.format(Locale.ENGLISH,"%d.%d.%d, %02d:%02d", day, month, year, hour, minute);
        }

        public String dayToString() {
            return String.format(Locale.ENGLISH,"%d.%d.%d", day, month, year);
        }

        public String localeDateString() {
            return String.format(Locale.ENGLISH,"%04d-%02d-%02d", year, month, day);
        }

        public Date addDays(int days) {
            LocalDate temp = LocalDate.parse(this.localeDateString());
            return new Date(temp.plusDays(days).toString());
        }

    }
}

