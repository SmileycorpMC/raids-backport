package net.smileycorp.raids.common.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.smileycorp.raids.common.Constants;
import net.smileycorp.raids.common.raid.data.RaidHandler;

public class CommandFindRaiders extends CommandBase {
    
    @Override
    public String getName() {
        return "findRaiders";
    }
    
    @Override
    public String getUsage(ICommandSender sender) {
        return "commands."+ Constants.MODID+".FindRaiders.usage";
    }
    
    @Override
    public int getRequiredPermissionLevel() {
        return -1;
    }
    
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        server.addScheduledTask(() -> {
            RaidHandler.findRaiders(sender.getEntityWorld(), sender.getPosition());
        });
        //notifyCommandListener(sender, this, "commands."+Constants.modid+".HordeDebug.success", path.toAbsolutePath().toString());
    }
    
}
