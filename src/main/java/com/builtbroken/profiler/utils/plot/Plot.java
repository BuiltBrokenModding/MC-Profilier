package com.builtbroken.profiler.utils.plot;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/15/2016.
 */
public class Plot extends ArrayList<AbstractMap.SimpleEntry<Long, Integer>>
{
    public final String plotName;

    private boolean reCalculateAverage = false;
    private long averageTickTime = 0;

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
        add(new AbstractMap.SimpleEntry<Long, Integer>(timeLogged, timeTaken));
        reCalculateAverage = true;
    }

    /**
     * Removes any plot points older then the time provided
     *
     * @param time - time point to clear data before
     */
    public void removeDataOlderThan(long time)
    {
        Iterator<AbstractMap.SimpleEntry<Long, Integer>> it = iterator();
        while (it.hasNext())
        {
            AbstractMap.SimpleEntry<Long, Integer> entry = it.next();
            if (entry.getKey() < time)
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
    public long getAverageTime()
    {
        if (reCalculateAverage)
        {
            long time = get(0).getKey();
            for (AbstractMap.SimpleEntry<Long, Integer> entry : this)
            {
                time += entry.getKey();
            }
            averageTickTime = time / this.size();
        }
        return averageTickTime;
    }
}
