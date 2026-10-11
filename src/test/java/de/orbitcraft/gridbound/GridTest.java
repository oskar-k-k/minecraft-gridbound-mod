package de.orbitcraft.gridbound;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
class GridTest {
    @Test void negativeCoordinates() {assertEquals(-1,Grid.cell(-0.1,0));assertEquals(-2,Grid.cell(-3.1,0));assertEquals(-12,Grid.x(Grid.key(-12,-8)));assertEquals(-8,Grid.z(Grid.key(-12,-8)));}
    @Test void bodyMustFitAndSharedEdgesAreOpen() {
        Set<Long> s=new HashSet<>(Set.of(Grid.key(0,0)));
        assertTrue(Grid.contains(s,0,0,1.5,1.5,0.3));assertFalse(Grid.contains(s,0,0,2.9,1.5,0.3));
        s.add(Grid.key(1,0)); assertTrue(Grid.contains(s,0,0,2.9,1.5,0.3));
        assertFalse(Grid.contains(s,0,0,2.9,2.9,0.3));
    }
    @Test void buysOnlyFirstClosedCellInDirection() {
        var s=Set.of(Grid.key(0,0),Grid.key(1,0),Grid.key(2,0),Grid.key(0,1));
        assertEquals(Grid.key(3,0),Grid.target(s,0,0,1,0));
        assertEquals(Grid.key(1,1),Grid.target(s,1,0,0,1));
        assertEquals(Grid.key(-1,0),Grid.target(s,0,0,-1,0));
        assertThrows(IllegalArgumentException.class,()->Grid.target(s,0,0,1,1));
    }
}
