package de.orbitcraft.gridbound;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid="gridbound",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class Client {
    static final KeyMapping KEY=new KeyMapping("key.gridbound.open",InputConstants.Type.KEYSYM,GLFW.GLFW_KEY_G,"key.categories.gridbound");
    static Progress.Area area;
    static int points;
    static String dimension="";
    static Vec3 safe;
    static final String[] NAMES={"Norden","Osten","Süden","Westen"};
    static final int[] DX={0,1,0,-1}, DZ={-1,0,1,0};
    @SubscribeEvent public static void keys(RegisterKeyMappingsEvent e) {e.register(KEY);}
    static void accept(CompoundTag t) {area=Progress.Area.load(t);points=t.getInt("points");dimension=t.getString("dimension");safe=null;}
    static boolean active() {var mc=Minecraft.getInstance();return area!=null && mc.level!=null && mc.player!=null && dimension.equals(mc.level.dimension().location().toString());}

    @EventBusSubscriber(modid="gridbound",value=Dist.CLIENT)
    public static final class Events {
        @SubscribeEvent public static void tick(ClientTickEvent.Post e) {
            var mc=Minecraft.getInstance();
            if(mc.level==null) {area=null;safe=null;return;}
            if(!active()) return;
            while(KEY.consumeClick()) if(mc.screen==null) mc.setScreen(new UnlockScreen());
            var p=mc.player;
            if(Grid.contains(area.cells,area.ox,area.oz,p.getX(),p.getZ(),p.getBbWidth()/2.0)) safe=p.position();
            else if(safe!=null) {p.setPos(safe);p.setDeltaMovement(Vec3.ZERO);}
        }
        @SubscribeEvent public static void render(RenderLevelStageEvent e) {
            if(e.getStage()==RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS && active()) walls(e);
        }
    }
    // Render actual geometry so the border remains visible with particles disabled.
    static void walls(RenderLevelStageEvent event) {
        var mc=Minecraft.getInstance(); var p=mc.player;
        var pose=event.getPoseStack();var camera=event.getCamera().getPosition();
        pose.pushPose();pose.translate(-camera.x,-camera.y,-camera.z);
        var buffers=mc.renderBuffers().bufferSource();var lines=buffers.getBuffer(RenderType.lines());
        int pcx=Grid.cell(p.getX(),area.ox),pcz=Grid.cell(p.getZ(),area.oz);
        for(int cx=pcx-4;cx<=pcx+4;cx++) for(int cz=pcz-4;cz<=pcz+4;cz++) {
            if(!area.cells.contains(Grid.key(cx,cz))) continue;
            for(int d=0;d<4;d++) {
                if(area.cells.contains(Grid.key(cx+DX[d],cz+DZ[d]))) continue;
                double x=area.ox+cx*3,z=area.oz+cz*3;
                for(int t=0;t<3;t++) for(int h=-6;h<10;h++) {
                    double wx=x+(d==1?3:d==3?0:t),wz=z+(d==2?3:d==0?0:t);
                    LevelRenderer.renderLineBox(pose,lines,wx,Math.floor(p.getY())+h,wz,
                        wx+(d==0||d==2?1:0),Math.floor(p.getY())+h+1,wz+(d==1||d==3?1:0),0.15f,0.8f,1f,0.5f);
                }
            }
        }
        buffers.endBatch(RenderType.lines());pose.popPose();
    }
    static final class UnlockScreen extends Screen {
        int direction;
        Button buy;
        UnlockScreen() {
            super(Component.literal("Gridbound"));
            direction=switch(Minecraft.getInstance().player.getDirection()){case NORTH->0;case EAST->1;case SOUTH->2;default->3;};
        }
        @Override protected void init() {
            int x=width/2,y=height/2;
            for(int i=0;i<4;i++) {final int d=i;addRenderableWidget(Button.builder(Component.literal(NAMES[i]),b->direction=d).bounds(x-156+i*79,y+55,76,20).build());}
            buy=addRenderableWidget(Button.builder(Component.literal("3×3 freischalten · 1 Punkt"),b->PacketDistributor.sendToServer(new Network.Buy(direction))).bounds(x-120,y+82,240,20).build());
            addRenderableWidget(Button.builder(Component.literal("Schließen"),b->onClose()).bounds(x-60,y+109,120,20).build());
        }
        @Override public boolean isPauseScreen(){return false;}
        @Override public void render(GuiGraphics g,int mx,int my,float dt) {
            renderBackground(g,mx,my,dt);
            int x=width/2,y=height/2;
            g.drawCenteredString(font,"GRIDBOUND",x,y-115,0x55DDFF);
            g.drawCenteredString(font,"Punkte: "+points+"  ·  Richtung: "+NAMES[direction],x,y-96,0xFFFFFF);
            g.drawCenteredString(font,"Blickrichtung wird beim Öffnen übernommen",x,y-81,0xAAAAAA);
            buy.active=points>0 && active();
            if(active() && area.cells.contains(Grid.key(Grid.cell(Minecraft.getInstance().player.getX(),area.ox),Grid.cell(Minecraft.getInstance().player.getZ(),area.oz)))) {
                var p=Minecraft.getInstance().player;int cx=Grid.cell(p.getX(),area.ox),cz=Grid.cell(p.getZ(),area.oz);
                long target=Grid.target(area.cells,cx,cz,DX[direction],DZ[direction]);
                for(int i=-3;i<=3;i++) for(int j=-3;j<=3;j++) {
                    long k=Grid.key(cx+i,cz+j);int color=k==target?0xFFFFC857:area.cells.contains(k)?0xFF287F91:0xFF252D38;
                    int px=x+i*15-7,py=y+j*15-12;g.fill(px,py,px+13,py+13,color);
                }
                g.fill(x-2,y-7,x+3,y-2,0xFFFFFFFF);
                g.drawCenteredString(font,"N",x,y-69,0xFFFFFF);
            }
            super.render(g,mx,my,dt);
        }
    }
}
