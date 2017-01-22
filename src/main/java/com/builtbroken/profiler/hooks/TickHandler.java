package com.builtbroken.profiler.hooks;

import com.builtbroken.profiler.utils.plot.PlotBlock;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.block.Block;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private int tick = 0;
    public int reportTime = TICKS_PER_MIN;

    public final Logger logger = LogManager.getLogger("Profiler");

    private TickHandler() {}


    @SubscribeEvent
    public void serverTick(TickEvent.WorldTickEvent event)
    {
        if (event.world.provider.dimensionId == 0 && event.phase == TickEvent.Phase.END)
        {
            tick++;
            if (tick >= reportTime)
            {
                tick = 0;
                logger.info("====================================");
                logger.info("\tGenerating profile report");
                logger.info("\tSide: " + event.side);
                logger.info("\tBlock Placement Times");
                for (Map.Entry<Block, PlotBlock> entry : BlockHooks.blockPlacementLogs.entrySet())
                {
                    logger.info("\tB:" + entry.getKey().getLocalizedName() + ", " + entry.getValue().getAvergateTimeDisplay());
                }
                logger.info("====================================");
            }
        }
    }
}
