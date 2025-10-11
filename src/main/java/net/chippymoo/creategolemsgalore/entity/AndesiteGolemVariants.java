package net.chippymoo.creategolemsgalore.entity;

import java.util.Arrays;
import java.util.Comparator;

public enum AndesiteGolemVariants {
    BLUE(0),
    GREEN(1),
    ORANGE(2),
    RED(3);

    private static final AndesiteGolemVariants[] BY_ID = Arrays.stream(values()).sorted(
            Comparator.comparingInt(AndesiteGolemVariants::getId)).toArray(AndesiteGolemVariants[]::new);


    private final int id;

    AndesiteGolemVariants(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return id;
    }

    public static AndesiteGolemVariants byId(int id)
    {
        return BY_ID[id % BY_ID.length];
    }
}
