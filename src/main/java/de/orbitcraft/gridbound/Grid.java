package de.orbitcraft.gridbound;

import java.util.*;

/** Pure grid geometry; cells cover [x,x+3) and [z,z+3). */
public final class Grid {
    public static long key(int x, int z) { return ((long)x << 32) | (z & 0xffffffffL); }
    public static int x(long key) { return (int)(key >> 32); }
    public static int z(long key) { return (int)key; }
    public static int cell(double coordinate, int origin) { return (int)Math.floor((coordinate-origin)/3.0); }
    public static boolean contains(Set<Long> cells, int ox, int oz, double x, double z, double radius) {
        for (int cx=cell(x-radius,ox); cx<=cell(x+radius,ox); cx++)
            for (int cz=cell(z-radius,oz); cz<=cell(z+radius,oz); cz++)
                if (!cells.contains(key(cx,cz))) return false;
        return true;
    }
    public static long target(Set<Long> cells, int cx, int cz, int dx, int dz) {
        if (Math.abs(dx)+Math.abs(dz)!=1 || !cells.contains(key(cx,cz))) throw new IllegalArgumentException();
        do { cx+=dx; cz+=dz; } while(cells.contains(key(cx,cz)));
        return key(cx,cz);
    }
}
