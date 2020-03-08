package com.moonturns.takeanote;

public class EventBusChangeMessage {
    private boolean changed;

    public EventBusChangeMessage(boolean changed) {
        this.changed = changed;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }
}
