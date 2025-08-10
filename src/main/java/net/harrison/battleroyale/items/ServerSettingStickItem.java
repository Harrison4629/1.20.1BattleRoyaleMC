package net.harrison.battleroyale.items;

import net.harrison.battleroyale.data.ServerData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;


public class ServerSettingStickItem extends Item {

    public ServerSettingStickItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getLevel().isClientSide) {
            return InteractionResult.PASS;
        }

        Player player = pContext.getPlayer();


        if (pContext.getHand() == InteractionHand.MAIN_HAND) {

            BlockPos pos = pContext.getClickedPos();
            ServerData data = ServerData.get((ServerLevel) pContext.getLevel());

            if (!player.isCrouching()) {
                if (data.getPlatformLocations().contains(pos)) {
                    data.removePlatform(pos);
                } else {
                    data.addPlatform(pos);
                }
            } else {
                data.setHobbyLocation(pos);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        return super.use(pLevel, pPlayer, pUsedHand);
    }
}
