package de.orbitcraft.gridbound;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class Network {
    public record State(CompoundTag data) implements CustomPacketPayload {
        public static final Type<State> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath("gridbound","state"));
        public static final StreamCodec<RegistryFriendlyByteBuf,State> CODEC=StreamCodec.of((b,p)->b.writeNbt(p.data),b->new State(b.readNbt()));
        public Type<State> type(){return TYPE;}
    }
    public record Buy(int direction) implements CustomPacketPayload {
        public static final Type<Buy> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath("gridbound","buy"));
        public static final StreamCodec<RegistryFriendlyByteBuf,Buy> CODEC=StreamCodec.of((b,p)->b.writeInt(p.direction),b->new Buy(b.readInt()));
        public Type<Buy> type(){return TYPE;}
    }
    public static void register(RegisterPayloadHandlersEvent e) {
        var r=e.registrar("1");
        r.playToClient(State.TYPE,State.CODEC,(p,c)->c.enqueueWork(()->Client.accept(p.data)));
        r.playToServer(Buy.TYPE,Buy.CODEC,(p,c)->c.enqueueWork(()->Gridbound.buy((net.minecraft.server.level.ServerPlayer)c.player(),p.direction)));
    }
}
