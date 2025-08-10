package net.harrison.battleroyale.events;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.harrison.battleroyale.Battleroyale;
import net.harrison.battleroyale.data.ClientMarkerData;
import net.harrison.battleroyale.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Battleroyale.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class MarkerBlockPosRenderEvent {


    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();

        if (mc.player != null && !(mc.player.getMainHandItem().getItem() == ModItems.SERVER_SETTING_STICK.get())) {
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lines());

        Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();

        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        for (BlockPos pos : ClientMarkerData.platform_locations) {
            poseStack.pushPose();

            poseStack.translate(pos.getX() - cameraPos.x, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z);

            LevelRenderer.renderLineBox(poseStack, consumer, 0, 0, 0, 1, 1, 1, 0.0F, 1.0F, 1.0F, 0.8F, 0.0F, 1.0F, 1.0F);

            poseStack.popPose();
        }

        if (ClientMarkerData.hobby_location != null) {
            BlockPos pos = ClientMarkerData.hobby_location;
            poseStack.pushPose();

            poseStack.translate(pos.getX() - cameraPos.x, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z);

            LevelRenderer.renderLineBox(poseStack, consumer, 0, 0, 0, 1, 1, 1, 0.0F, 0.0F, 1.0F, 0.8F, 0.0F, 0.0F, 1.0F);


            poseStack.popPose();
        }



        bufferSource.endBatch(RenderType.lines());

        //RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F); // 恢复默认颜色
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
    }




}
