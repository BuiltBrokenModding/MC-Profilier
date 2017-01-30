package com.builtbroken.profiler.hooks;

import com.builtbroken.profiler.utils.plot.Plot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.HashMap;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/22/2017.
 */
public class WorldHooks
{
    private static HashMap<TileEntity, Long> tileEntityTickTimes = new HashMap();
    public static HashMap<TileEntity, Plot> tileEntityUpdateLogs = new HashMap();

    public static void onUpdateEntity(World world, TileEntity tile)
    {
        tileEntityTickTimes.put(tile, System.nanoTime());
    }

    public static void onPostUpdateEntity(World world, TileEntity tile)
    {
        if(tileEntityTickTimes.containsKey(tile))
        {
            long time = System.nanoTime() - tileEntityTickTimes.get(tile);

            //Log placement data for block
            if(!tileEntityUpdateLogs.containsKey(tile))
            {
                tileEntityUpdateLogs.put(tile, new Plot("updateEntity"));
            }
            //Queue data point
            tileEntityUpdateLogs.get(tile).addPoint(tileEntityTickTimes.get(tile), (int)time);

            //Remove entry so not to cause a memory leak, or hold onto references
            tileEntityTickTimes.remove(tile);
        }
    }

    public static void clearLogs()
    {
        tileEntityTickTimes.clear();
        tileEntityUpdateLogs.clear();
    }
}
