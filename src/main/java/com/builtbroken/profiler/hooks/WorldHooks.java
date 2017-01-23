package com.builtbroken.profiler.hooks;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/22/2017.
 */
public class WorldHooks
{
    public static void onUpdateEntity(World world, TileEntity tile)
    {
        System.out.println(world + "   " + tile);
    }
}
