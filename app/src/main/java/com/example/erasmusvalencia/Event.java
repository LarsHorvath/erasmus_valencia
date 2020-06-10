package com.example.erasmusvalencia;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;

public class Event implements Comparable<Event> {
    public static HashMap<Integer, Event> allEvents = new HashMap<>();

    static final String TAG = "Event";

    private int id;
    private String title;
    private String company;
    static public final int imagesrc[] = {R.mipmap.logo_erasmus_app, R.drawable.esnupv, R.drawable.esnuv, R.drawable.happyerasmus, R.drawable.soyerasmus,
            R.drawable.erasmuslife, R.drawable.questionmark};
    private int companyID = imagesrc.length-1;
    private String imageUrl;

    private String description;
    private String location;
    private String url;
    private String eventUrl;
    private OffsetDateTime datePosted;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private boolean favourite;
    private boolean custom;
    public static final int FILTER_TITLE = 1;
    public static final int FILTER_DESCRIPTION = 2;
    public static final int FILTER_LOCATION = 3;
    public static final int FILTER_COMPANY = 4;
    public static final int FILTER_DATE = 5;
    public static final int FILTER_FAVOURITE = 6;
    public static final int FILTER_TEXT_SEARCH = 7;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Event() {
        title = "no title";
        company = "no company";
        description = "no description";
        location = "no location";
        startDate = OffsetDateTime.now();
        endDate = OffsetDateTime.now();
        id = generateID();
        //allEvents.put(id, this);
        favourite = false;

    }

    public Event(int id, String title, String company, int companyID, String description,
                 String location, String url, OffsetDateTime datePosted, OffsetDateTime startDate,
                 OffsetDateTime endDate, String eventUrl, String imageUrl) {
        this.id = id;
        this.title = title;
        this.company = company;
        this.companyID = companyID;
        this.description = description;
        this.location = location;
        this.url = url;
        this.datePosted = datePosted;
        this.startDate = startDate;
        this.endDate = endDate;
        this.eventUrl = eventUrl;
        this.imageUrl = "https://www.erasmuscalendar.com" + imageUrl;
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

    public OffsetDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(OffsetDateTime startDate) {
        this.startDate = startDate;
    }

    public OffsetDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(OffsetDateTime endDate) {
        this.endDate = endDate;
    }

    public void findCompany() {
        String[] companies = {"", "ESN", "ESN", "Happy Erasmus", "Soy Erasmus", "Erasmus Life"};
        for (int i = 1; i < companies.length; i++) {
            if (title.contains(companies[i]) ||  location.contains(companies[i]) || description.contains(companies[i])) {
                this.company = companies[i];
                this.companyID = i;
            }
        }
        if (company.equals("ESN") && (title.contains("UV") || location.contains("UV") || description.contains(("UV")))) {
            this.companyID = 2;
            this.company ="ESN UV";
        }
        else if (company.equals("ESN")) {
            this.companyID = 1;
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
    @RequiresApi(api = Build.VERSION_CODES.O)
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
            try {
                event.setDescription(item.getAsJsonPrimitive("description").getAsString());
            } catch (NullPointerException npe) {
                event.setDescription("not available");
                npe.printStackTrace();
            }
            try {
                event.setLocation(item.getAsJsonPrimitive("location").getAsString());
            } catch (NullPointerException npe) {
                event.setLocation("not available");
                npe.printStackTrace();
            }
            //event.setStartDate(parseCalendarString(item.getAsJsonObject("start").getAsJsonPrimitive("dateTime").getAsString()));
            //event.setEndDate(parseCalendarString(item.getAsJsonObject("end").getAsJsonPrimitive("dateTime").getAsString()));
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<Event> filterEvents(final Collection<Event> events, final Object... filters) throws IllegalArgumentException {
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
    @RequiresApi(api = Build.VERSION_CODES.O)
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
                        if (!(filters[i] instanceof OffsetDateTime || !(filters[i+1] instanceof OffsetDateTime))) throw new IllegalArgumentException();
                        if (((OffsetDateTime) filters[i]).compareTo((OffsetDateTime) filters[i+1]) >= 0) {
                            if (((OffsetDateTime) filters[i++]).compareTo(event.getStartDate()) > 0) return false;
                            //Log.i(TAG, "eventMatches: Date presented: " + ((OffsetDateTime) filters[i-1]).getDisplayNames(OffsetDateTime.DAY_OF_YEAR,OffsetDateTime.SHORT,Locale.ENGLISH));
                            //Log.i(TAG, "eventMatches: this Date to filter: " + (event.getStartDate()).getDisplayNames(OffsetDateTime.DAY_OF_YEAR,OffsetDateTime.SHORT,Locale.ENGLISH));
                            //Log.i(TAG, "eventMatches: result: " + ((OffsetDateTime) filters[i-1]).compareTo((OffsetDateTime) filters[i]));
                        }
                        else if (((OffsetDateTime) filters[i]).compareTo(event.getStartDate()) > 0 || event.getStartDate().compareTo((OffsetDateTime) filters[++i]) >= 0) return false;
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
            case NAME_AND_DAY: return String.format(Locale.ENGLISH,"%s %02d.%02d.%04d", weekDayName(cal.get(Calendar.DAY_OF_WEEK)), cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH)+1, cal.get(Calendar.YEAR));
            case NAME_AND_DAY_AND_TIME: return String.format(Locale.ENGLISH,"%s %02d.%02d.%04d, %02d:%02d", weekDayName(cal.get(Calendar.DAY_OF_WEEK)), cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH)+1, cal.get(Calendar.YEAR), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
            default: return "You're an idiot";
        }
    }

    private static String weekDayName(int day) {
        switch (day) {
            case 1:
                return "Sun";
            case 2:
                return "Mon";
            case 3:
                return "Tue";
            case 4:
                return "Wed";
            case 5:
                return "Thu";
            case 6:
                return "Fri";
            case 7:
                return "Sat";
        }
        return "dfasdfjoasfdj";
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    static void addEasterEggEvent() {
        OffsetDateTime s = OffsetDateTime.now();
        s.plusDays(1);
        OffsetDateTime e = OffsetDateTime.now();
        e.plusDays(1);
        if (Event.allEvents.containsKey(7)) {
            Event.allEvents.get(7).setStartDate(s);
            Event.allEvents.get(7).setEndDate(e);
            return;
        }
        Event eee = new Event();
        eee.setTitle("Erasmus goes Easter");
        eee.setLocation("Valencia");
        eee.setStartDate(s);
        eee.setEndDate(e);
        eee.setId(7);
        eee.setCompanyID(0);
        eee.setCustom(true);
        allEvents.put(eee.getId(), eee);
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

