package com.builtbroken.profiler.utils.debug;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/30/2017.
 */
public class CommandSenderOutput extends DebugOutput
{
    public final ICommandSender sender;

    public CommandSenderOutput(ICommandSender sender)
    {
        this.sender = sender;
    }

    @Override
    public void out(String string)
    {
        sender.addChatMessage(new ChatComponentText(string));
    }
}
