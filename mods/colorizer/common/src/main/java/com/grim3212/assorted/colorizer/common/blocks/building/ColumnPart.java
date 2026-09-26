package com.grim3212.assorted.colorizer.common.blocks.building;

import net.minecraft.util.StringRepresentable;

/** Which piece of a run of columns a block is, worked out from the columns either end of it. */
public enum ColumnPart implements StringRepresentable {
    SINGLE("single"),
    BASE("base"),
    SHAFT("shaft"),
    CAPITAL("capital");

    private final String name;

    ColumnPart(String name) {
        this.name = name;
    }

    public static ColumnPart of(boolean below, boolean above) {
        return below ? (above ? SHAFT : CAPITAL) : (above ? BASE : SINGLE);
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
