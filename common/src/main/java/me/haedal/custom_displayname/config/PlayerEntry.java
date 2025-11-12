package me.haedal.custom_displayname.config;

public class PlayerEntry {
    public String name = "Player";
    public String field1 = "";
    public String field2 = "";

    public PlayerEntry() {}
    public PlayerEntry(String name) {
        this.name = name;
        this.field1 = name;
        this.field2 = name;
    }
}
