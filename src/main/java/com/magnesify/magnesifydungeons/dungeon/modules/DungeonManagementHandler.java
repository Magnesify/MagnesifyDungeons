package com.magnesify.magnesifydungeons.dungeon.modules;

import com.magnesify.magnesifydungeons.dungeon.Dungeon;

import java.util.List;

public interface DungeonManagementHandler {

    boolean create();
    boolean delete();
    Dungeon.Types update();

    Dungeon.Events events();

    boolean exists();
}
