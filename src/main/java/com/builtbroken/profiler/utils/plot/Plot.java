package com.builtbroken.profiler.utils.plot;

import com.builtbroken.jlib.type.Pair;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/15/2016.
 */
public class Plot extends ArrayList<Pair<Long, Integer>>
{
    public final String plotName;

    private boolean reCalculateAverage = false;
    private BigInteger averageTickTime = BigInteger.ZERO;

    public Plot(String name)
    {
        this.plotName = name;
    }

    /**
     * Adds a point to plot
     *
     * @param timeLogged - time the data was taken
     * @param timeTaken  - time taken for the action
     */
    public void addPoint(long timeLogged, int timeTaken)
    {
        add(new Pair<Long, Integer>(timeLogged, timeTaken));
        reCalculateAverage = true;
    }

    /**
     * Removes any plot points older then the time provided
     *
     * @param time - time point to clear data before
     */
    public void removeDataOlderThan(long time)
    {
        Iterator<Pair<Long, Integer>> it = iterator();
        while (it.hasNext())
        {
            Pair<Long, Integer> entry = it.next();
            if (entry.left() < time)
            {
                it.remove();
            }
        }
    }

    /**
     * Gets the average tick time of points plotted. If
     * the average is not cached or data has changed it
     * will be recalculated. This is an O(n) operation
     * meaning it will take more time the more data is
     * entered.
     *
     * @return average tick time
     */
    public BigInteger getAverageTime()
    {
        if (reCalculateAverage)
        {
            BigInteger value = BigInteger.ZERO;
            for (Pair<Long, Integer> entry : this)
            {
                value = value.add(BigInteger.valueOf(entry.right()));
            }
            averageTickTime = value.divide(BigInteger.valueOf(this.size()));
            reCalculateAverage = false;
        }
        return averageTickTime;
    }

    public String getAvergateTimeDisplay()
    {
        return formatDisplayString(getAverageTime().toString()) + "    [" + getAverageTime().toString() + "ns]";
    }

    public String formatDisplayString(String nanoTime)
    {
        String ns = "";
        String ms = "";
        String s = "";
        //Parse for nano-seconds
        if (nanoTime.length() > 0)
        {
            ns = nanoTime.substring(Math.max(nanoTime.length() - 3, 0), nanoTime.length());
            if (ns.equals("000"))
            {
                ns = "";
            }
            else
            {
                ns = " " + ns + "ns";
            }
            //Parse for milli-seconds
            if (nanoTime.length() > 3)
            {
                ms = nanoTime.substring(Math.max(nanoTime.length() - 6, 0), nanoTime.length() - 3);
                if (ms.equals("000"))
                {
                    ms = "";
                }
                else
                {
                    ms = " " + ms + "ms";
                }
                //Parse for seconds
                if (nanoTime.length() > 6)
                {
                    s = nanoTime.substring(Math.max(nanoTime.length() - 9, 0), nanoTime.length() - 6);
                    if (s.startsWith("0"))
                    {
                        s = "";
                    }
                    else
                    {
                        s = s + "s";
                    }
                }
            }
        }
        return s + ms + ns;
    }
}
