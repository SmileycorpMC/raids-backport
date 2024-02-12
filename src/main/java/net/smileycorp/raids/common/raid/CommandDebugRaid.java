package net.smileycorp.raids.common.raid;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.smileycorp.raids.common.Constants;

public class CommandDebugRaid extends CommandBase {
    
    @Override
    public String getName() {
        return "debugRaid";
    }
    
    @Override
    public String getUsage(ICommandSender sender) {
        return "commands."+ Constants.MODID+".RaidsDebug.usage";
    }
    
    @Override
    public int getRequiredPermissionLevel() {
        return -1;
    }
    
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        server.addScheduledTask(() -> {
            WorldDataRaids data = WorldDataRaids.getData((WorldServer) sender.getEntityWorld());
            data.logDebug();
        });
        //notifyCommandListener(sender, this, "commands."+Constants.modid+".HordeDebug.success", path.toAbsolutePath().toString());
    }
    
}
