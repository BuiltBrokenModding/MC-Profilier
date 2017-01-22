package com.builtbroken.test.profilier;

import com.builtbroken.profiler.utils.Pos;
import junit.framework.TestCase;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/21/2017.
 */
public class PosTest extends TestCase
{
    public void testEquals()
    {
        Pos pos = new Pos(0,0,0);
        Pos pos2 = new Pos(0,0,0);
        assertTrue(pos.equals(pos2));
    }
}
