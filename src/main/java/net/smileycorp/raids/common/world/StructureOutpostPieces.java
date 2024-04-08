package net.smileycorp.raids.common.world;

import com.google.common.collect.Lists;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponentTemplate;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.smileycorp.raids.common.Constants;

import java.util.ArrayList;
import java.util.Random;

public class StructureOutpostPieces {
    
    public static ArrayList<OutpostTemplate> watchtower(TemplateManager manager, BlockPos pos, Rotation rot) {
        return Lists.newArrayList(new OutpostTemplate(manager, pos, "watchtower", rot, 1),
                new OutpostTemplate(manager, pos, "watchtower_overgrown", rot, 0.05f));
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
        protected void handleDataMarker(String function, BlockPos pos, World world, Random rand, StructureBoundingBox sbb) {}
        
    }
    
}
