package com.builtbroken.profiler.commands;

import com.builtbroken.profiler.hooks.TickHandler;
import com.builtbroken.profiler.utils.debug.CommandSenderOutput;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/30/2017.
 */
public class CommandProfilier extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "profiler";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "profiler help";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args == null || args.length == 0 || args[0] == null || args[0].equalsIgnoreCase("help"))
        {
            sender.addChatMessage(new ChatComponentText("profiler data - displays CPU data for current log cycle"));
        }
        else
        {
            String command = args[0];
            if (command.equalsIgnoreCase("data"))
            {
                TickHandler.INSTANCE.dumpDebugToConsole(new CommandSenderOutput(sender));
            }
        }
    }
}
