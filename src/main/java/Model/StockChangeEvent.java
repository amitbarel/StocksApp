package Model;

import javafx.event.Event;
import javafx.event.EventType;

public class StockChangeEvent extends Event {
    public static final EventType<StockChangeEvent> ANY_CHANGE = new EventType<>(Event.ANY,"ANY_CHANGE");
    public static final EventType<StockChangeEvent> ANY = ANY_CHANGE;
    public static final EventType<StockChangeEvent> WATCHLIST_CHANGE = new EventType<>(StockChangeEvent.ANY,"WATCHLIST_CHANGE");
    public static final EventType<StockChangeEvent> PORTFOLIO_CHANGE = new EventType<>(StockChangeEvent.ANY,"PORTFOLIO_CHANGE");

    private Company c;

    public StockChangeEvent(EventType<? extends Event> eventType, Company c) {
        super(eventType);
        this.c = c;
    }

    public Company getCompany(){
        return c;
    }
}
