package com.magnesify.magnesifydungeons.mythic;

public class MythicAdapter {

    public MythicAdapter(){}
    private static boolean mythic = false;

    public static void setMythic(boolean vault) {
        mythic = vault;
    }

    public static boolean getMythic() {
        return mythic;
    }
}
