package com.builtbroken.profiler.utils.plot;

import net.minecraft.block.Block;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/15/2016.
 */
public class PlotBlock extends Plot
{
    public final Block block;

    public PlotBlock(String name, Block block)
    {
        super(name);
        this.block = block;
    }
}
