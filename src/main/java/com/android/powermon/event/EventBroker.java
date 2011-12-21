package com.android.powermon.event;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EventBroker {
    private List<EventRecord<?>> listenerList = new ArrayList<EventRecord<?>>();

    public <T> void publish(T event) {
        for (Iterator<EventRecord<?>> recordIterator = listenerList.iterator(); recordIterator.hasNext(); ) {
            EventRecord<?> eventRecord = recordIterator.next();
            EventListener<?> listener = eventRecord.getListener();
            if (listener == null) {
                recordIterator.remove();
            } else {
                if (eventRecord.isApplicableFor(event.getClass())) {
                    ((EventListener<T>) listener).onEvent(event);
                }
            }
        }
    }

    public <T> void subscribe(Class<T> event, EventListener<T> listener) {
        // Cleanup
        for (Iterator<EventRecord<?>> recordIterator = listenerList.iterator(); recordIterator.hasNext(); ) {
            EventRecord<?> eventRecord = recordIterator.next();
            if (eventRecord.getListener() == null) {
                recordIterator.remove();
            }
        }

        // Subscribe
        listenerList.add( new EventRecord<T>(event, listener) );
    }


    public <T> void unsubscribe(EventListener<T> listener) {
        // Cleanup
        for (Iterator<EventRecord<?>> recordIterator = listenerList.iterator(); recordIterator.hasNext(); ) {
            EventRecord<?> eventRecord = recordIterator.next();
            EventListener<?> currentListener = eventRecord.getListener();
            if ((listener == null) || (currentListener == listener)) {
                recordIterator.remove();
            }
        }
    }


    private static class EventRecord<T> {
        private Class<T> eventClass;
        private WeakReference<EventListener<T>> listenerWeakReference;

        public EventRecord(Class<T> eventClass, EventListener<T> listenerWeakReference) {
            this.eventClass = eventClass;
            this.listenerWeakReference = new WeakReference<EventListener<T>>(listenerWeakReference);
        }

        public boolean isApplicableFor(Class<?> eventType) {
            return eventClass.isAssignableFrom(eventType);
        }

        public EventListener<T> getListener() {
            return listenerWeakReference.get();
        }
    }
}
