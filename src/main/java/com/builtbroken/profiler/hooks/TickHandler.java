package com.builtbroken.profiler.hooks;

import com.builtbroken.jlib.type.Pair;
import com.builtbroken.profiler.ProfilerMod;
import com.builtbroken.profiler.utils.debug.ConsoleOutput;
import com.builtbroken.profiler.utils.debug.DebugOutput;
import com.builtbroken.profiler.utils.plot.Plot;
import com.builtbroken.profiler.utils.plot.PlotBlock;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/15/2016.
 */
public class TickHandler
{
    public static final int TICKS_PER_SECOND = 20;
    public static final int TICKS_PER_MIN = TICKS_PER_SECOND * 60;

    public static final TickHandler INSTANCE = new TickHandler();

    public static int reportTime = TICKS_PER_MIN * 5;
    public static int nanosecondWarningTrigger = 10000;

    private int tick = 0;
    private ConsoleOutput consoleOutput;

    private TickHandler()
    {
        consoleOutput = new ConsoleOutput(ProfilerMod.logger);
    }


    @SubscribeEvent
    public void serverTick(TickEvent.WorldTickEvent event)
    {
        //TODO log tick times
        //TODO log data per tick to better graph data
        //TODO sort data to show high CPU times at top
        if (event.world.provider.dimensionId == 0 && event.phase == TickEvent.Phase.END)
        {
            tick++;
            if (tick >= reportTime)
            {
                tick = 0;
                dumpDebugToConsole(consoleOutput);
            }
        }
    }

    public void dumpDebugToConsole(DebugOutput output)
    {
        output.out("====================================");
        output.out("\tGenerating profile report");
        output.out("\tBlock Placement Average(s)");
        for (Map.Entry<Block, PlotBlock> entry : BlockHooks.blockPlacementLogs.entrySet())
        {
            output.out("\tB:" + entry.getKey().getLocalizedName() + "  Average CPU Time: " + entry.getValue().getAvergateTimeDisplay());
        }
        output.out("--------------------------------------");
        output.out("\tPer:TileEntity#updateEntity() Average(s)");
        HashMap<Class<TileEntity>, Plot> classTickData = new HashMap();
        for (Map.Entry<TileEntity, Plot> entry : WorldHooks.tileEntityUpdateLogs.entrySet())
        {
            output.out("\tT:" + entry.getKey() + "  Average CPU Time: " + entry.getValue().getAvergateTimeDisplay());
            //Debug if time took way too long
            long time = entry.getValue().getAverageTime().longValue();
            if(time > nanosecondWarningTrigger)
            {
                output.out("Warning: High average CPU time detected. Dumping data for closer inspection.");
                for(Pair<Long, Integer> data : entry.getValue())
                {
                    output.out("\t\tNanoseconds: " + data.right() + "  TimeTaken: " + data.left());
                }
            }
            //Build class data
            Class<TileEntity> clazz = (Class<TileEntity>) entry.getKey().getClass();
            if(!classTickData.containsKey(clazz))
            {
                classTickData.put(clazz, new Plot("updateEntity"));
            }
            classTickData.get(clazz).addPoint(0, entry.getValue().getAverageTime().intValue());
        }
        output.out("--------------------------------------");
        output.out("\tClass:TileEntity#updateEntity() Average(s)");
        for (Map.Entry<Class<TileEntity>, Plot> entry : classTickData.entrySet())
        {
            output.out("\tClass:" + entry.getKey() + "  Average CPU Time: " + entry.getValue().getAvergateTimeDisplay());
        }
        //Clear data to free up RAM
        WorldHooks.tileEntityUpdateLogs.clear();
        BlockHooks.blockPlacementPosLogs.clear();
        output.out("====================================");
    }
}
