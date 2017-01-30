package com.builtbroken.profiler;

import com.builtbroken.profiler.asm.checks.CheckFakeWorld;
import com.builtbroken.profiler.commands.CommandProfilier;
import com.builtbroken.profiler.hooks.BlockHooks;
import com.builtbroken.profiler.hooks.TickHandler;
import com.builtbroken.profiler.hooks.WorldHooks;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.init.Blocks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/14/2016.
 */
@Mod(modid = "bbmprofiler", name = "BBM Profiler", version = "0.0.1", acceptableRemoteVersions = "*")
public class ProfilerMod
{
    public static final Logger logger = LogManager.getLogger("Profiler");

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        FMLCommonHandler.instance().bus().register(TickHandler.INSTANCE);

        logger.info("============================================");
        logger.info("============================================");




        logger.info("Checking if ASM was applied");
        logger.info("TileEntity#updateEntity(): " + ProfilerCoreMod.tileUpdateHookAdded);
        logger.info("World#setBlock: " + ProfilerCoreMod.blockChangeHookAdded);
        logger.info("World#setMeta: " + ProfilerCoreMod.blockChangeMetaHookAdded);



        logger.info("Checking if ASM is working");
        CheckFakeWorld world = CheckFakeWorld.newWorld("Test");
        world.setBlock(0, 0, 0, Blocks.stone);

        boolean works = BlockHooks.blockPlacementLogs.containsKey(Blocks.stone);
        logger.info("World#setBlock: " + works);
        BlockHooks.clearLogs();

        world.setBlockMetadataWithNotify(0, 0, 0, 1, 0);
        works = BlockHooks.blockPlacementLogs.containsKey(Blocks.stone);
        logger.info("World#setMeta: " + works);
        BlockHooks.clearLogs();

        world.setBlock(0, 0, 0, Blocks.chest);
        world.updateEntities();
        works = WorldHooks.tileEntityUpdateLogs.size() > 0;
        logger.info("TileEntity#updateEntity(): " + works);
        WorldHooks.clearLogs();

        logger.info("============================================");
        logger.info("============================================");
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        // Setup command
        ICommandManager commandManager = FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager();
        ServerCommandManager serverCommandManager = ((ServerCommandManager) commandManager);
        serverCommandManager.registerCommand(new CommandProfilier());
    }
}
