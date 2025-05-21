package net.smileycorp.raids.common.world;

import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponentTemplate;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.smileycorp.raids.common.Constants;
import net.smileycorp.raids.common.Raids;
import net.smileycorp.raids.common.util.RaidsLogger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Random;

public class StructureOutpostPieces {

    private static ArrayList<String> features;

    public static ArrayList<OutpostTemplate> watchtower(TemplateManager manager, BlockPos pos, Rotation rot) {
        return Lists.newArrayList(new OutpostTemplate(manager, pos, "watchtower", rot, 1),
                new OutpostTemplate(manager, pos, "watchtower_overgrown", rot, 0.05f));
    }

    public static ArrayList<OutpostTemplate> feature(Random rand, TemplateManager manager, BlockPos pos, Rotation rot) {
        if (features == null) {
            features = Lists.newArrayList();
            try {
                Path directory = Raids.CONFIG_FOLDER.toPath().resolve("pillager_outpost");
                Files.find(directory.resolve("features"), Integer.MAX_VALUE, (matcher, options) -> options.isRegularFile())
                        .forEach(path -> features.add(directory.relativize(path).toString().replace(".nbt", "")));
            } catch (Exception e) {
                RaidsLogger.logError("Failed reading outpost feature files", e);
            }
        }
        return features.isEmpty() ? Lists.newArrayList() : Lists.newArrayList(
                new OutpostTemplate(manager, pos, features.get(rand.nextInt(features.size())), rot, 1) );
    }
    
    public static void registerStructurePieces() {
        MapGenStructureIO.registerStructureComponent(OutpostTemplate.class, "OutpostTemplate");
    }
    
    public static class OutpostTemplate extends StructureComponentTemplate {
        
        private String name;
        private Rotation rot;
        private float integrity;
    
        public OutpostTemplate() {};
    
        public OutpostTemplate(TemplateManager manager, BlockPos pos, String name, Rotation rot, float integrity) {
            super(0);
            templatePosition = pos;
            this.name = name;
            this.integrity = integrity;
            this.rot  = rot;
            loadTemplate(manager);
        }
    
        @Override
        protected void writeStructureToNBT(NBTTagCompound nbt) {
            super.writeStructureToNBT(nbt);
            nbt.setString("Template", name);
            nbt.setString("Rot", rot.name());
            nbt.setFloat("Integrity", integrity);
        }
    
        @Override
        protected void readStructureFromNBT(NBTTagCompound nbt, TemplateManager manager) {
            super.readStructureFromNBT(nbt, manager);
            if (nbt.hasKey("Template")) name = nbt.getString("Template");
            if (nbt.hasKey("Rot")) rot = Rotation.valueOf(nbt.getString("Rot"));
            if (nbt.hasKey("Integrity")) integrity = nbt.getFloat("Integrity");
            if (name != null && rot != null) loadTemplate(manager);
        }
    
        private void loadTemplate(TemplateManager manager) {
            PlacementSettings settings = new PlacementSettings().setRotation(rot).setReplacedBlock(Blocks.STRUCTURE_VOID).setIntegrity(integrity);
            setup(manager.getTemplate(null, Constants.loc("pillager_outpost/" + name)), templatePosition, settings);
        }
    
        @Override
        protected void handleDataMarker(String function, BlockPos pos, World world, Random rand, StructureBoundingBox sbb) {
            world.setBlockToAir(pos);
            if (function.contains("entity")) {
                String[] str = function.split(" ");
                if (str.length < 2) return;
                try {
                    EntityEntry entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(str[1]));
                    int min = 0;
                    if (str.length > 2) min = Integer.parseInt(str[2]);
                    int max = min;
                    if (str.length > 3) max += rand.nextInt(Integer.parseInt(str[3]) - min);
                    for (int i = min; i <= max; i++) {
                        Entity entity = entry.newInstance(world);
                        if (entity instanceof EntityLiving) {
                            ((EntityLiving) entity).enablePersistence();
                            ((EntityLiving) entity).onInitialSpawn(world.getDifficultyForLocation(pos), null);
                        }
                        entity.setPosition(pos.getX() + 0.5f, pos.getY(), pos.getZ() +0.5f);
                        world.spawnEntity(entity);
                    }
                } catch (Exception e) {}
            }
        }
        
    }
    
}
