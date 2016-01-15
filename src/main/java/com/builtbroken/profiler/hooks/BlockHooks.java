package com.builtbroken.profiler.hooks;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/14/2016.
 */
public class BlockHooks
{
    public void onBlockChangeStart()
    {
        System.out.println("onBlockChangeStart");
    }

    public void onBlockChangeEnd()
    {
        System.out.println("onBlockChangeEnd");
    }
}
