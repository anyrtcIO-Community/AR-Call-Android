package org.ar.arcall;

import org.json.JSONArray;

public class ARGroupOption {
    public ARCallMode callMode;
    public String userData;
    public JSONArray userArray;

    public ARGroupOption(ARCallMode callMode, String userData, JSONArray userArray) {
        this.callMode = callMode;
        this.userData = userData;
        this.userArray = userArray;
    }
}
