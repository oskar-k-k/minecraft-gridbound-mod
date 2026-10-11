package de.orbitcraft.gridbound;

import dev.ftb.mods.ftbquests.events.ObjectCompletedEvent;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.*;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import java.util.*;

@Mod("gridbound")
public final class Gridbound {
    private static List<Item> rewards;
    public Gridbound(IEventBus bus) {
        bus.addListener(Network::register);
        NeoForge.EVENT_BUS.addListener(Gridbound::tick);
        NeoForge.EVENT_BUS.addListener(Gridbound::login);
        NeoForge.EVENT_BUS.addListener(Gridbound::dimension);
        NeoForge.EVENT_BUS.addListener(Gridbound::respawn);
        NeoForge.EVENT_BUS.addListener(Gridbound::breaking);
        NeoForge.EVENT_BUS.addListener(Gridbound::rightClick);
        ObjectCompletedEvent.QUEST.register(e -> {
            for(ServerPlayer p:e.getOnlineMembers()) {
                Progress store=Progress.get(p.server); var a=store.account(p.getUUID());
                if(a.rewarded.add(e.getQuest().id)) {
                    a.points++; store.setDirty();
                    if(rewards==null) rewards=BuiltInRegistries.ITEM.stream().filter(i->i!=Items.AIR && i!=Items.BARRIER && i!=Items.COMMAND_BLOCK && i!=Items.CHAIN_COMMAND_BLOCK && i!=Items.REPEATING_COMMAND_BLOCK && i!=Items.STRUCTURE_BLOCK && i!=Items.STRUCTURE_VOID && i!=Items.JIGSAW && i!=Items.DEBUG_STICK && i!=Items.LIGHT).toList();
                    ItemStack stack=new ItemStack(rewards.get(p.getRandom().nextInt(rewards.size())));
                    Component name=stack.getHoverName().copy();
                    if(!p.getInventory().add(stack)) p.drop(stack,false);
                    p.sendSystemMessage(Component.literal("§bGridbound: +1 Punkt · ").append(name)); sync(p);
                }
            }
            return dev.architectury.event.EventResult.pass();
        });
    }
    static String dim(ServerPlayer p) {return p.level().dimension().location().toString();}
    static Progress.Area area(ServerPlayer p) {
        Progress s=Progress.get(p.server); var a=s.account(p.getUUID());
        return a.areas.computeIfAbsent(dim(p),k->{
            var r=new Progress.Area(); r.ox=p.blockPosition().getX()-1; r.oz=p.blockPosition().getZ()-1;
            r.cells.add(Grid.key(0,0)); r.safeX=p.getX(); r.safeY=p.getY(); r.safeZ=p.getZ(); s.setDirty(); return r;
        });
    }
    static boolean allowed(ServerPlayer p,BlockPos pos) {
        var a=area(p); return a.cells.contains(Grid.key(Grid.cell(pos.getX(),a.ox),Grid.cell(pos.getZ(),a.oz)));
    }
    static void breaking(BlockEvent.BreakEvent e) {if(e.getPlayer() instanceof ServerPlayer p && !allowed(p,e.getPos())) e.setCanceled(true);}
    static void rightClick(PlayerInteractEvent.RightClickBlock e) {
        if(e.getEntity() instanceof ServerPlayer p && (!allowed(p,e.getPos()) || (e.getItemStack().getItem() instanceof BlockItem && e.getFace()!=null && !allowed(p,e.getPos().relative(e.getFace()))))) e.setCanceled(true);
    }
    static void login(PlayerEvent.PlayerLoggedInEvent e) {if(e.getEntity() instanceof ServerPlayer p) sync(p);}
    static void dimension(PlayerEvent.PlayerChangedDimensionEvent e) {if(e.getEntity() instanceof ServerPlayer p) {enforce(p);sync(p);}}
    static void respawn(PlayerEvent.PlayerRespawnEvent e) {if(e.getEntity() instanceof ServerPlayer p) {enforce(p);sync(p);}}
    static void tick(PlayerTickEvent.Post e) {if(e.getEntity() instanceof ServerPlayer p) enforce(p);}
    static void enforce(ServerPlayer p) {
        var a=area(p);
        if(!Grid.contains(a.cells,a.ox,a.oz,p.getX(),p.getZ(),p.getBbWidth()/2.0)) {
            p.stopRiding(); p.teleportTo(a.safeX,a.safeY,a.safeZ); p.setDeltaMovement(Vec3.ZERO); p.fallDistance=0;
        } else {
            a.safeX=p.getX(); a.safeY=p.getY(); a.safeZ=p.getZ();
            Progress.get(p.server).setDirty();
        }
    }
    static void sync(ServerPlayer p) {
        CompoundTag t=area(p).save(); t.putInt("points",Progress.get(p.server).account(p.getUUID()).points); t.putString("dimension",dim(p));
        PacketDistributor.sendToPlayer(p,new Network.State(t));
    }
    static void buy(ServerPlayer p,int index) {
        if(index<0 || index>3) return;
        var store=Progress.get(p.server); var account=store.account(p.getUUID()); var a=area(p);
        if(account.points<=0) {sync(p);return;}
        Direction d=new Direction[]{Direction.NORTH,Direction.EAST,Direction.SOUTH,Direction.WEST}[index];
        int cx=Grid.cell(p.getX(),a.ox),cz=Grid.cell(p.getZ(),a.oz);
        if(!a.cells.contains(Grid.key(cx,cz))) return;
        long target=Grid.target(a.cells,cx,cz,d.getStepX(),d.getStepZ());
        int x=a.ox+Grid.x(target)*3,z=a.oz+Grid.z(target)*3;
        if(!p.level().getWorldBorder().isWithinBounds(new BlockPos(x,0,z)) || !p.level().getWorldBorder().isWithinBounds(new BlockPos(x+2,0,z+2))) return;
        if(a.cells.add(target)) {account.points--;store.setDirty();sync(p);}
    }
}
