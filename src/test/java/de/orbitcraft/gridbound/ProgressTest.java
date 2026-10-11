package de.orbitcraft.gridbound;
import org.junit.jupiter.api.Test;
import net.minecraft.nbt.CompoundTag;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
class ProgressTest {
    @Test void roundTripPreservesPointsClaimsAndDimensions() {
        Progress p=new Progress();UUID id=UUID.randomUUID();var a=p.account(id);a.points=7;a.rewarded.add(456L);
        var r=new Progress.Area();r.ox=-25;r.oz=8;r.safeX=-23.5;r.safeY=72;r.safeZ=9.5;r.cells.add(Grid.key(-1,3));a.areas.put("minecraft:overworld",r);
        var restored=Progress.load(p.save(new CompoundTag(),null),null).account(id);
        assertEquals(7,restored.points);assertTrue(restored.rewarded.contains(456L));assertFalse(restored.rewarded.add(456L));
        assertEquals(r.cells,restored.areas.get("minecraft:overworld").cells);
        assertEquals(-23.5,restored.areas.get("minecraft:overworld").safeX);
        assertEquals(-25,restored.areas.get("minecraft:overworld").ox);
    }
}
