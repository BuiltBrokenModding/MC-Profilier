package com.builtbroken.test.profilier;

import com.builtbroken.jlib.type.Pair;
import com.builtbroken.profiler.utils.plot.Plot;
import junit.framework.TestCase;

import java.math.BigInteger;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/21/2017.
 */
public class PlotTest extends TestCase
{
    public void testInit()
    {
        Plot plot = new Plot("blarg");
        assertEquals("blarg", plot.plotName);
    }

    public void testAdd()
    {
        Plot plot = new Plot("blarg");
        plot.addPoint(5L, 5);
        assertTrue(plot.list.contains(new Pair<Long, Integer>(5L, 5)));
    }

    public void testRemoveOlderThan()
    {
        Plot plot = new Plot("blarg");
        plot.addPoint(5L, 5);
        assertTrue(plot.list.contains(new Pair<Long, Integer>(5L, 5)));
        plot.removeDataOlderThan(6L);
        assertFalse(plot.list.contains(new Pair<Long, Integer>(5L, 5)));
    }

    public void testAverage()
    {
        Plot plot = new Plot("blarg");
        plot.addPoint(5L, 1000000);
        plot.addPoint(6L, 1000000);
        plot.addPoint(7L, 1000000);
        BigInteger value = plot.getAverageTime();
        assertEquals(BigInteger.valueOf(1000000), value);
    }
}
