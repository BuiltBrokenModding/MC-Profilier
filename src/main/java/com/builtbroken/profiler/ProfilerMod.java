package com.builtbroken.profiler;

import com.builtbroken.profiler.commands.CommandProfilier;
import com.builtbroken.profiler.hooks.TickHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
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

        logger.info("Checking if ASM was applied");
        logger.info("TileEntity#updateEntity(): " + ProfilerCoreMod.tileUpdateHookAdded);
        logger.info("World#setBlock: " + ProfilerCoreMod.blockChangeHookAdded);
        logger.info("World#setBlockMeta: " + ProfilerCoreMod.blockChangeMetaHookAdded);
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
