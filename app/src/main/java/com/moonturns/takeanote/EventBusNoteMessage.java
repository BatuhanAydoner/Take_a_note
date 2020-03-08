package com.moonturns.takeanote;

public class EventBusNoteMessage {
    private int noteId;
    private String note;
    private int notePriority;

    public EventBusNoteMessage(int noteId, String note, int notePriority) {
        this.noteId = noteId;
        this.note = note;
        this.notePriority = notePriority;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getNotePriority() {
        return notePriority;
    }

    public void setNotePriority(int notePriority) {
        this.notePriority = notePriority;
    }
}
