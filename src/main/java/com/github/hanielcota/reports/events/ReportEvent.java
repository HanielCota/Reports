package com.github.hanielcota.reports.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Getter
public class ReportEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final String reportedPlayer;
    private final String reason;

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
