package net.smileycorp.raids.integration.deeperdepths;

import com.deeperdepths.common.items.DeeperDepthsItems;
import com.deeperdepths.common.potion.DeeperDepthsPotions;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;

public class DeeperDepthsIntegration {
    
    public static Item getOminousBottle() {
        return DeeperDepthsItems.OMINOUS_BOTTLE;
    }
    
    public static Potion getBadOmen() {
        return DeeperDepthsPotions.BAD_OMEN;
    }
    
}
