package net.smileycorp.raids.common.world;

import com.google.common.collect.Lists;
import net.minecraft.init.Blocks;
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
        return Lists.newArrayList(new OutpostTemplate(manager, "watchtower", 1, pos, rot),
                new OutpostTemplate(manager, "watchtower_overgrown", 0.05f, pos, rot));
    }
    
    public static void registerStructurePieces() {
        MapGenStructureIO.registerStructureComponent(OutpostTemplate.class, "OutpostTemplate");
    }
    
    public static class OutpostTemplate extends StructureComponentTemplate {
    
        public OutpostTemplate(TemplateManager manager, String name, float integrity, BlockPos pos, Rotation rot) {
            super(0);
            templatePosition = pos;
            PlacementSettings settings = new PlacementSettings().setRotation(rot).setReplacedBlock(Blocks.AIR).setIntegrity(integrity);
            setup(manager.getTemplate(null, Constants.loc("pillager_outpost/" + name)), templatePosition, settings);
        }
    
        @Override
        protected void handleDataMarker(String function, BlockPos pos, World worldIn, Random rand, StructureBoundingBox sbb) {}
        
    }
    
}
