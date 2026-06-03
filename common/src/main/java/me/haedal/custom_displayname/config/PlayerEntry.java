package me.haedal.custom_displayname.config;

import com.google.gson.annotations.SerializedName;

public class PlayerEntry {
    @SerializedName(value = "playerName", alternate = "field1")
    public String playerName = "";

    @SerializedName(value = "displayName", alternate = "field2")
    public String displayName = "";

    public PlayerEntry() {}

    public PlayerEntry(String playerName) {
        this.playerName = playerName;
        this.displayName = playerName;
    }
}
