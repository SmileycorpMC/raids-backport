package net.smileycorp.raids.integration.futuremc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetMetadata;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.village.MerchantTradeOffersEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.smileycorp.raids.common.Constants;
import net.smileycorp.raids.common.RaidsContent;
import net.smileycorp.raids.common.RaidsSoundEvents;
import net.smileycorp.raids.common.entities.ai.EntityAIGiveGift;
import net.smileycorp.raids.common.interfaces.ITradeDiscount;
import net.smileycorp.raids.common.items.ItemOminousBottle;
import net.smileycorp.raids.common.raid.*;
import net.smileycorp.raids.common.util.MathUtils;
import net.smileycorp.raids.common.util.accessors.ILootPool;
import net.smileycorp.raids.common.world.MapGenOutpost;
import net.smileycorp.raids.config.OutpostConfig;
import net.smileycorp.raids.config.RaidConfig;
import net.smileycorp.raids.integration.ModIntegration;
import net.smileycorp.raids.integration.crossbow.CrossbowIntegration;
import net.smileycorp.raids.integration.crossbows.CrossbowsBackportIntegration;
import net.smileycorp.raids.integration.spartanweaponry.SpartanWeaponryIntegration;
import net.smileycorp.raids.integration.tconstruct.TinkersConstructIntegration;
import thedarkcolour.futuremc.tile.BellTileEntity;

import java.util.List;

public class FutureMCIntegration {
	
	@CapabilityInject(BellTimer.class)
	public static Capability<BellTimer> BELL_TIMER = null;
	
	public static void init() {
		MinecraftForge.EVENT_BUS.register(new FutureMCIntegration());
		CapabilityManager.INSTANCE.register(BellTimer.class, new BellTimer.Storage(), () -> new BellTimer.Impl(null));
	}
	
	@SubscribeEvent
	public void attachCapabilities(AttachCapabilitiesEvent<TileEntity> event) {
		TileEntity te = event.getObject();
		if (te instanceof BellTileEntity) event.addCapability(Constants.loc("Timer"), new BellTimer.Provider(te));
	}
	
	@SubscribeEvent
	public void onBlockActivated(PlayerInteractEvent.RightClickBlock event) {
		EntityPlayer player = event.getEntityPlayer();
		World world = player.world;
		if (world.isRemote) return;
		BlockPos pos = event.getPos();
		TileEntity tile = world.getTileEntity(pos);
		if (!tile.hasCapability(BELL_TIMER, null)) return;
		BellTimer timer = tile.getCapability(BELL_TIMER, null);
		if (!timer.isRinging()) timer.setRinging();
	}
	
	@SubscribeEvent
	public void tick(TickEvent.ServerTickEvent event) {
		BellTimer.ACTIVE_BELLS.forEach(BellTimer::updateTimer);
	}
	
}
