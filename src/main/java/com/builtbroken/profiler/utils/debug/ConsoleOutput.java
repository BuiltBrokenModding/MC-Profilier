package com.builtbroken.profiler.utils.debug;

import org.apache.logging.log4j.Logger;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/30/2017.
 */
public class ConsoleOutput extends DebugOutput
{
    public final Logger logger;

    public ConsoleOutput(Logger logger)
    {
        this.logger = logger;
    }

    @Override
    public void out(String string)
    {
        logger.info(string);
    }
}
