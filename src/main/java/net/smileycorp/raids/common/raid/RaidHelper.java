package net.smileycorp.raids.common.raid;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.smileycorp.raids.config.RaidConfig;
import net.smileycorp.raids.config.raidevent.RaidSpawnTable;
import net.smileycorp.raids.common.util.RaidsLogger;
import net.smileycorp.raids.common.event.CustomRaidStartEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.registries.GameData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class RaidHelper {
    
    private static final double DEFAULT_DETECTION_RADIUS = 48.0D;
    
    private RaidHelper() {
    }
    
    public static Optional<Raid> triggerRaid(WorldServer world, BlockPos pos, @Nullable EntityPlayerMP player,
            String tableName, boolean requireVillageCheck, @Nullable List<String> detectionWhitelist,
            @Nullable String bossbarName, @Nullable Integer waveOverride) {
        if (world == null || pos == null || tableName == null) return Optional.empty();
        RaidSpawnTable table = RaidHandler.getSpawnTable(tableName);
        if (table == null) {
            RaidsLogger.logError("Unable to locate raid table " + tableName, new IllegalArgumentException(tableName));
            return Optional.empty();
        }
        if (requireVillageCheck && !Raid.isVillage(world, pos)) return Optional.empty();
        List<Class<? extends EntityLiving>> whitelist = resolveWhitelist(detectionWhitelist);
        if (!hasEligibleTargets(world, pos, whitelist)) return Optional.empty();
        Integer normalizedWaves = waveOverride != null ? Math.max(1, waveOverride) : null;
        WorldDataRaids data = WorldDataRaids.getData(world);
        if (data.getRaidAt(pos) != null) {
            RaidsLogger.logError("Raid already active at " + pos, new IllegalStateException());
            return Optional.empty();
        }
        Raid raid = data.forceStartRaid(pos, table, !requireVillageCheck, normalizedWaves);
        if (raid == null) return Optional.empty();
        String raidName = stripExtension(table.getName());
        raid.configureCustomRaid(player, requireVillageCheck, whitelist, bossbarName, raidName);
        CustomRaidStartEvent event = new CustomRaidStartEvent(raid);
        if (MinecraftForge.EVENT_BUS.post(event)) {
            data.removeRaid(raid);
            return Optional.empty();
        }
        if (player != null) RaidOmenTracker.setRaidStart(player);
        return Optional.of(raid);
    }
    
    private static boolean hasEligibleTargets(WorldServer world, BlockPos pos, Collection<Class<? extends EntityLiving>> detectionWhitelist) {
        AxisAlignedBB area = new AxisAlignedBB(pos).grow(DEFAULT_DETECTION_RADIUS);
        return !world.getEntitiesWithinAABB(EntityLiving.class, area, entity -> matchesDetection(entity, detectionWhitelist)).isEmpty();
    }
    
    private static boolean matchesDetection(EntityLiving entity, Collection<Class<? extends EntityLiving>> detectionWhitelist) {
        if (detectionWhitelist == null || detectionWhitelist.isEmpty()) {
            return entity instanceof EntityVillager || RaidConfig.isTickableVillager(entity);
        }
        for (Class<? extends EntityLiving> clazz : detectionWhitelist) {
            if (clazz != null && clazz.isAssignableFrom(entity.getClass())) return true;
        }
        return false;
    }

    private static List<Class<? extends EntityLiving>> resolveWhitelist(@Nullable List<String> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        List<Class<? extends EntityLiving>> result = new ArrayList<>();
        for (String id : ids) {
            if (id == null || id.trim().isEmpty()) continue;
            try {
                ResourceLocation loc = new ResourceLocation(id);
                EntityEntry entry = GameData.getEntityRegistry().getValue(loc);
                if (entry != null && EntityLiving.class.isAssignableFrom(entry.getEntityClass())) {
                    result.add((Class<? extends EntityLiving>) entry.getEntityClass());
                } else RaidsLogger.logError("Invalid detection whitelist entity " + id, new IllegalArgumentException("Entity not found or not living"));
            } catch (Exception e) {
                RaidsLogger.logError("Failed resolving detection whitelist entity " + id, e);
            }
        }
        return result;
    }

    private static String stripExtension(String name) {
        if (name == null) return "";
        int index = name.lastIndexOf('.');
        return index == -1 ? name : name.substring(0, index);
    }
    
}
