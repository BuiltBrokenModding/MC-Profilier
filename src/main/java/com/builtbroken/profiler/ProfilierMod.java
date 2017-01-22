package com.builtbroken.profiler;

import com.builtbroken.profiler.hooks.TickHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/14/2016.
 */
@Mod(modid = "bbmprofile", name = "BBM Profiler", version = "0.0.1", acceptableRemoteVersions = "*")
public class ProfilierMod
{
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        FMLCommonHandler.instance().bus().register(TickHandler.INSTANCE);
    }
}
