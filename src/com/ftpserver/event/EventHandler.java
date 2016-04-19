package com.ftpserver.event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by windr on 4/18/16.
 */
public class EventHandler {
    private List<Event> eventList;

    public EventHandler() {
        eventList = new ArrayList<Event>();
    }

    public void addEvent(Event e) {
        eventList.add(e);
    }

    public void invokeAll(Object... args) throws Exception {
        for (Event e : eventList) {
            e.invoke(args);
        }
    }
}
