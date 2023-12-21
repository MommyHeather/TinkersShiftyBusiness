package co.uk.mommyheather.tsb.util;

import net.minecraft.entity.player.EntityPlayer;

public class AOEToolHelper {

    public static boolean isPlayerNotCrouching(boolean in, EntityPlayer player) {
        if (player.isSneaking()) return false;
        return in;
    }

}
