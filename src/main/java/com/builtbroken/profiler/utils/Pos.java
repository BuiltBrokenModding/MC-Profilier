package com.builtbroken.profiler.utils;

import com.builtbroken.jlib.data.vector.Pos3D;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/15/2016.
 */
public class Pos extends Pos3D<Pos>
{
    public Pos(int x, int y, int z)
    {
        super(x, y, z);
    }

    @Override
    public Pos newPos(double x, double y, double z)
    {
        return new Pos((int) x, (int) y, (int) z);
    }
}
