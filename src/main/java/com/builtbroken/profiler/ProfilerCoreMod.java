package com.builtbroken.profiler;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Map;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/14/2016.
 */
@IFMLLoadingPlugin.TransformerExclusions(value = {"com.builtbroken.profiler.asm.WorldTransformer"})
@IFMLLoadingPlugin.MCVersion("1.7.10")
public class ProfilerCoreMod implements IFMLLoadingPlugin
{
    public static final Logger logger = LogManager.getLogger("BBM-Profiler-ASM");

    public static boolean tileUpdateHookAdded = false;
    public static boolean blockChangeHookAdded = false;
    public static boolean blockChangeMetaHookAdded = false;

    public static final boolean obfuscated;

    static
    {
        boolean obf = true;
        try
        {
            obf = ((LaunchClassLoader) ProfilerCoreMod.class.getClassLoader()).getClassBytes("net.minecraft.world.World") == null;
        }
        catch (IOException iox)
        {
            ProfilerCoreMod.logger.catching(iox);
        }
        obfuscated = obf;
    }

    /**
     * Checks if the program is running in development mode.
     * This is normally used to enable additional debug such
     * as printing edited classes to file between runs.
     *
     * @return true if system arguments contain -Ddevelopmenet=true
     */
    public static boolean isDevMode()
    {
        return System.getProperty("development") != null && System.getProperty("development").equalsIgnoreCase("true");
    }

    @Override
    public String[] getASMTransformerClass()
    {
        return new String[]{"com.builtbroken.profiler.asm.WorldTransformer"};
    }

    @Override
    public String getModContainerClass()
    {
        return "com.builtbroken.profiler.asm.ModContainer";
    }

    @Override
    public String getSetupClass()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data)
    {

    }

    @Override
    public String getAccessTransformerClass()
    {
        // TODO Auto-generated method stub
        return "";
    }

}
