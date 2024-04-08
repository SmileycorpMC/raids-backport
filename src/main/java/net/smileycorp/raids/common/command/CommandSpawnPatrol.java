package net.smileycorp.raids.common.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.smileycorp.raids.common.Constants;
import net.smileycorp.raids.common.raid.WorldDataRaids;

public class CommandSpawnPatrol extends CommandBase {
    
    @Override
    public String getName() {
        return "spawnPatrol";
    }
    
    @Override
    public String getUsage(ICommandSender sender) {
        return "commands."+ Constants.MODID+".RaidsDebug.usage";
    }
    
    @Override
    public int getRequiredPermissionLevel() {
        return 1;
    }
    
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        server.addScheduledTask(() -> {
            WorldServer world = (WorldServer) sender.getEntityWorld();
            WorldDataRaids.getData(world).getPatrolSpawner().spawnPatrol(world, sender.getCommandSenderEntity(), world.rand, true);
        });
    }
    
}
