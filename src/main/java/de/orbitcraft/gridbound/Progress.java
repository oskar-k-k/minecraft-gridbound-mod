package de.orbitcraft.gridbound;

import java.util.*;
import net.minecraft.nbt.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

public final class Progress extends SavedData {
    public final Map<UUID, Account> accounts = new HashMap<>();
    public static final class Area {
        public int ox, oz;
        public double safeX, safeY, safeZ;
        public final Set<Long> cells = new HashSet<>();
        public CompoundTag save() {
            CompoundTag t=new CompoundTag(); t.putInt("x",ox); t.putInt("z",oz);
            t.putDouble("safeX",safeX); t.putDouble("safeY",safeY); t.putDouble("safeZ",safeZ);
            t.putLongArray("cells",cells.stream().mapToLong(Long::longValue).toArray()); return t;
        }
        public static Area load(CompoundTag t) {
            Area a=new Area(); a.ox=t.getInt("x"); a.oz=t.getInt("z");
            a.safeX=t.getDouble("safeX"); a.safeY=t.getDouble("safeY"); a.safeZ=t.getDouble("safeZ");
            for(long k:t.getLongArray("cells")) a.cells.add(k); return a;
        }
    }
    public static final class Account {
        public int points;
        public final Set<Long> rewarded=new HashSet<>();
        public final Map<String,Area> areas=new HashMap<>();
    }
    public Account account(UUID id) { return accounts.computeIfAbsent(id,k->new Account()); }
    public static Progress get(MinecraftServer s) {
        return s.overworld().getDataStorage().computeIfAbsent(new Factory<>(Progress::new,Progress::load),"gridbound");
    }
    public static Progress load(CompoundTag tag, HolderLookup.Provider lookup) {
        Progress p=new Progress();
        for(String id:tag.getAllKeys()) {
            CompoundTag t=tag.getCompound(id); Account a=p.account(UUID.fromString(id)); a.points=t.getInt("points");
            for(long q:t.getLongArray("rewarded")) a.rewarded.add(q);
            CompoundTag dims=t.getCompound("areas"); for(String d:dims.getAllKeys()) a.areas.put(d,Area.load(dims.getCompound(d)));
        } return p;
    }
    @Override public CompoundTag save(CompoundTag tag, HolderLookup.Provider lookup) {
        accounts.forEach((id,a)-> { CompoundTag t=new CompoundTag(); t.putInt("points",a.points);
            t.putLongArray("rewarded",a.rewarded.stream().mapToLong(Long::longValue).toArray());
            CompoundTag dims=new CompoundTag(); a.areas.forEach((d,v)->dims.put(d,v.save())); t.put("areas",dims); tag.put(id.toString(),t);
        }); return tag;
    }
}
