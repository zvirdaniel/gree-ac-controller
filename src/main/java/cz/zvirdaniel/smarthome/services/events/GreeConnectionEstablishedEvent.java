package cz.zvirdaniel.smarthome.services.events;

import org.springframework.context.ApplicationEvent;

public class GreeConnectionEstablishedEvent extends ApplicationEvent {
    /**
     * Create a new {@code ApplicationEvent} with its {@link #getTimestamp() timestamp}
     * set to {@link System#currentTimeMillis()}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public GreeConnectionEstablishedEvent(Object source) {
        super(source);
    }
}
