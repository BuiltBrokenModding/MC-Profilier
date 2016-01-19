package com.builtbroken.profiler.hooks;

import com.builtbroken.profiler.utils.Pos;
import com.builtbroken.profiler.utils.plot.PlotBlock;
import com.builtbroken.profiler.utils.plot.PlotPos;
import net.minecraft.block.Block;
import net.minecraft.world.World;

import java.util.HashMap;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/14/2016.
 */
public class BlockHooks
{
    /** Temp cache of tick times for the location, create on blockChange, cleared on postBlockChange */
    private static HashMap<Pos, Long> tickTimes = new HashMap();
    public static HashMap<Block, PlotBlock> blockPlacementLogs = new HashMap();
    public static HashMap<Pos, PlotPos> blockPlacementPosLogs = new HashMap();

    public static void onBlockChange(World world, int x, int y, int z)
    {
        if (!world.isRemote)
        {
            tickTimes.put(new Pos(x, y, z), System.nanoTime());
        }
    }

    public static void onPostBlockChange(World world, int x, int y, int z)
    {
        if (!world.isRemote)
        {
            Pos pos = new Pos(x, y, z);
            if (tickTimes.containsKey(pos))
            {
                long time = System.nanoTime() - tickTimes.get(pos);
                Block block = world.getBlock(x, y, z);

                //Log placement data for block
                if(!blockPlacementLogs.containsKey(block))
                {
                    blockPlacementLogs.put(block, new PlotBlock("placementTimeLog", block));
                }
                blockPlacementLogs.get(block).addPoint(tickTimes.get(pos), (int)time);

                //Log placement data for position
                if(!blockPlacementPosLogs.containsKey(pos))
                {
                    blockPlacementPosLogs.put(pos, new PlotPos("placementTimeLog", pos));
                }
                blockPlacementPosLogs.get(pos).addPoint(tickTimes.get(pos), (int)time);

                //Clear for next entry
                tickTimes.remove(pos);
            }
        }
    }

    public static void onBlockMetaChange(World world, int x, int y, int z)
    {
        if (!world.isRemote)
        {
            tickTimes.put(new Pos(x, y, z), System.nanoTime());
        }
    }

    public static void onPostBlockMetaChange(World world, int x, int y, int z)
    {
        if (!world.isRemote)
        {
            Pos pos = new Pos(x, y, z);
            if (tickTimes.containsKey(pos))
            {
                long time = System.nanoTime() - tickTimes.get(pos);
                Block block = world.getBlock(x, y, z);

                //Log placement data for block
                if(!blockPlacementLogs.containsKey(block))
                {
                    blockPlacementLogs.put(block, new PlotBlock("placementTimeLog", block));
                }
                blockPlacementLogs.get(block).addPoint(tickTimes.get(pos), (int)time);

                //Log placement data for position
                if(!blockPlacementPosLogs.containsKey(pos))
                {
                    blockPlacementPosLogs.put(pos, new PlotPos("placementTimeLog", pos));
                }
                blockPlacementPosLogs.get(pos).addPoint(tickTimes.get(pos), (int)time);

                //Clear for next entry
                tickTimes.remove(pos);
            }
        }
    }

    private static void logHighTickTime(World world, Pos pos)
    {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        StackTraceElement element = trace[0];
        System.out.println("\t" + element.getMethodName());
        element = trace[1];
        System.out.println("\t" + element.getMethodName());
        element = trace[2];
        System.out.println("\t" + element.getMethodName());
        element = trace[3];
        System.out.println("\t" + element.getMethodName());
        element = trace[4];
        System.out.println("\t" + element.getMethodName());
    }
}
