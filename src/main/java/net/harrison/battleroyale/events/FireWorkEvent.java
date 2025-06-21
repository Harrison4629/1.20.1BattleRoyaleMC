package net.harrison.battleroyale.events;

import net.harrison.battleroyale.Battleroyale;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = Battleroyale.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FireWorkEvent {
    private static int TimeTicks;
    private static int PlayTimes;
    private static Vec3 Pos;
    private static Level level;


    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            return;
        }
        if (event.side.isClient()) {
            return;
        }

        if (PlayTimes > 0) {
            TimeTicks--;
            if (TimeTicks < 0) {
                PlayTimes--;
                TimeTicks = (int) (Math.random() * 10);

                ItemStack fireworkItemStack = new ItemStack(Items.FIREWORK_ROCKET);
                CompoundTag fireworkTag = fireworkItemStack.getOrCreateTagElement ("Fireworks");
                fireworkTag.putByte("Flight", (byte) (int)(Math.random() * 4));
                fireworkTag.put("Explosions", createFancyExplosions() );
                FireworkRocketEntity firework = new FireworkRocketEntity(level, Pos.x, Pos.y, Pos.z, fireworkItemStack);

                firework.setDeltaMovement(Math.random() - 0.5, Math.random() + 1, Math.random() - 0.5);

                level.addFreshEntity(firework);
            }
        }

    }

    private static ListTag createFancyExplosions() {
        ListTag explosions = new ListTag();
        Random random = new Random();

        CompoundTag smallRedBall = new CompoundTag();
        smallRedBall.putByte("Type", (byte) 0);
        smallRedBall.putIntArray("Colors", new int[]{0xFF0000});
        smallRedBall.putBoolean("Flicker", random.nextBoolean());
        smallRedBall.putBoolean("Trail", random.nextBoolean());

        CompoundTag largeBlueStar = new CompoundTag();
        largeBlueStar.putByte("Type", (byte) 2); // Star
        largeBlueStar.putIntArray("Colors", new int[]{0x0000FF});
        largeBlueStar.putIntArray("FadeColors", new int[]{0x00FFFF});
        largeBlueStar.putBoolean("Flicker", false);
        largeBlueStar.putBoolean("Trail", true);

        CompoundTag greenCreeper = new CompoundTag();
        greenCreeper.putByte("Type", (byte) 3); // Creeper
        greenCreeper.putIntArray("Colors", new int[]{0x00FF00});
        greenCreeper.putBoolean("Flicker", true);

        CompoundTag burstYellow = new CompoundTag();
        burstYellow.putByte("Type", (byte) 4); // Burst
        burstYellow.putIntArray("Colors", new int[]{0xFFFF00});
        burstYellow.putBoolean("Trail", true);

        switch (random.nextInt(4)) {
            case 0:
                explosions.add(smallRedBall);
                break;
            case 1:
                explosions.add(largeBlueStar);
                break;
            case 2:
                explosions.add(greenCreeper);
                break;
            case 3:
                explosions.add(burstYellow);
                break;
        }
        return explosions;
    }


    public static void setPlayTimesAndPos(int count, Vec3 pos, Level level) {
        PlayTimes = count;
        Pos = pos.add(0F, 2F, 0F);
        FireWorkEvent.level = level;
    }
}
