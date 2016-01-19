package com.builtbroken.profiler.utils.plot;

import com.builtbroken.profiler.utils.Pos;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/15/2016.
 */
public class PlotPos extends Plot
{
    public final Pos pos;
    public PlotPos(String name, Pos pos)
    {
        super(name);
        this.pos = pos;
    }
}
