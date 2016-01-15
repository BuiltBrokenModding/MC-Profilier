package com.builtbroken.profiler.hooks;

import com.builtbroken.jlib.lang.StringHelpers;
import net.minecraft.world.World;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/14/2016.
 */
public class BlockHooks
{
    public static void onBlockChange(World world, int x, int y, int z)
    {
        System.out.println("onBlockChange(" + world + ", " + x + ", " + y + ", " + z + ")");
        long time = System.nanoTime();
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        time = System.nanoTime() - time;
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
        System.out.println("\t" + StringHelpers.formatNanoTime(time));
    }

    public static void onPostBlockChange(World world, int x, int y, int z)
    {
        System.out.println("onPostBlockChange(" + world + ", " + x + ", " + y + ", " + z + ")");
    }

    public static void onBlockMetaChange(World world, int x, int y, int z)
    {
        //System.out.println("onBlockMetaChange(" + world + ", " + x + ", " + y + ", " + z + ")");
    }

    public static void onPostBlockMetaChange(World world, int x, int y, int z)
    {
        //System.out.println("onPostBlockMetaChange(" + world + ", " + x + ", " + y + ", " + z + ")");
    }

    public void postOnBlockChange()
    {

    }
}
