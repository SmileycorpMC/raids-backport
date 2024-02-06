package net.smileycorp.raids.common.item;

import com.google.common.collect.Lists;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemFirework;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.smileycorp.raids.common.Constants;
import net.smileycorp.raids.common.RaidsContent;
import net.smileycorp.raids.common.RaidsSoundEvents;
import net.smileycorp.raids.common.entities.ICrossbowArrow;
import net.smileycorp.raids.common.entities.ICrossbowAttackMob;
import net.smileycorp.raids.common.entities.IFireworksProjectile;

import java.util.List;
import java.util.Random;

public class ItemCrossbow extends Item {

	private boolean startSoundPlayed = false;
	private boolean midLoadSoundPlayed = false;
	
	public ItemCrossbow() {
		setUnlocalizedName(Constants.name("Crossbow"));
		setRegistryName(Constants.loc("Crossbow"));
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.COMBAT);
		setMaxDamage(465);
		addPropertyOverride(Constants.loc("pull"), (stack, worldIn, entityIn) -> entityIn == null || isCharged(stack) ? 0.0F :
				(float)(stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / ((float)getChargeDuration(stack)));
		addPropertyOverride(Constants.loc("pulling"), (stack, worldIn, entityIn) ->
				entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack && !isCharged(stack) ? 1.0F : 0.0F);
		addPropertyOverride(Constants.loc("charged"), (stack, worldIn, entityIn) -> isCharged(stack) ? 1 : 0);
		addPropertyOverride(Constants.loc("firework"), (stack, worldIn, entityIn) -> containsChargedProjectile(stack, Items.FIREWORKS) ? 1 : 0);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (isCharged(stack)) {
			performShooting(world, player, stack, containsChargedProjectile(stack, Items.FIREWORKS) ? 1.6F : 3.15F, 1.0F);
			setCharged(stack, false);
			return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
		} else if (!getProjectile(player).isEmpty()) {
			if (!isCharged(stack)) {
				this.startSoundPlayed = false;
				this.midLoadSoundPlayed = false;
				player.setActiveHand(hand);
			}
			return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
		}
		return ActionResult.newResult(EnumActionResult.FAIL, stack);
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entity, int timeLeft) {
		int duration = getMaxItemUseDuration(stack) - timeLeft;
		float charge = entity instanceof EntityPlayer ? getPowerForTime(duration, stack) : 1f;
		if (charge >= 1.0F && !isCharged(stack) && tryLoadProjectiles(entity, stack)) {
			setCharged(stack, true);
			SoundCategory soundsource = entity instanceof EntityPlayer ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
			world.playSound(null, entity.posX, entity.posX, entity.posZ, RaidsSoundEvents.CROSSBOW_LOADING_END, soundsource, 1.0F, 1.0F / (world.rand.nextFloat() * 0.5F + 1.0F) + 0.2F);
		}
	}

	private static boolean tryLoadProjectiles(EntityLivingBase entity, ItemStack stack) {
		int i = EnchantmentHelper.getEnchantmentLevel(RaidsContent.MULTISHOT, stack);
		int j = i == 0 ? 1 : 3;
		boolean creative = entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.isCreativeMode;
		ItemStack itemstack = getProjectile(entity);
		ItemStack itemstack1 = itemstack.copy();
		for(int k = 0; k < j; ++k) {
			if (k > 0) itemstack = itemstack1.copy();
			if (itemstack.isEmpty() && creative) {
				itemstack = new ItemStack(Items.ARROW);
				itemstack1 = itemstack.copy();
			}
			if (!loadProjectile(entity, stack, itemstack, k > 0, creative)) return false;
		}
		return true;
	}

	private static boolean loadProjectile(EntityLivingBase entity, ItemStack stack, ItemStack ammo, boolean p_40866_, boolean creative) {
		if (ammo.isEmpty()) return false;
		else {
			boolean dontConsume = creative && ammo.getItem() instanceof ItemArrow;
			ItemStack itemstack;
			if (!dontConsume && !creative && !p_40866_) {
				itemstack = ammo.splitStack(1);
				if (ammo.isEmpty() && entity instanceof EntityPlayer) ((EntityPlayer)entity).inventory.deleteStack(stack);
			} else itemstack = ammo.copy();
			addChargedProjectile(stack, itemstack);
			return true;
		}
	}

	public static boolean isCharged(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) nbt = new NBTTagCompound();
		return nbt != null && nbt.getBoolean("Charged");
	}

	public static void setCharged(ItemStack stack, boolean chargedIn) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) nbt = new NBTTagCompound();
		nbt.setBoolean("Charged", chargedIn);
		stack.setTagCompound(nbt);
	}

	private static void addChargedProjectile(ItemStack stack, ItemStack projectile) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) nbt = new NBTTagCompound();
		NBTTagList taglist;
		if (nbt.hasKey("ChargedProjectiles", 9)) taglist = nbt.getTagList("ChargedProjectiles", 10);
		else taglist = new NBTTagList();
		NBTTagCompound stacknbt = new NBTTagCompound();
		projectile.writeToNBT(stacknbt);
		taglist.appendTag(stacknbt);
		nbt.setTag("ChargedProjectiles", taglist);
		stack.setTagCompound(nbt);
	}

	private static List<ItemStack> getChargedProjectiles(ItemStack stack) {
		List<ItemStack> list = Lists.newArrayList();
		NBTTagCompound NBTTagCompound = stack.getTagCompound();
		if (NBTTagCompound != null && NBTTagCompound.hasKey("ChargedProjectiles", 9)) {
			NBTTagList taglist = NBTTagCompound.getTagList("ChargedProjectiles", 10);
			if (taglist != null) taglist.forEach(t -> list.add(new ItemStack((NBTTagCompound)t)));
		}
		return list;
	}

	private static void clearChargedProjectiles(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt != null) nbt.setTag("ChargedProjectiles", new NBTTagList());
	}

	public static boolean containsChargedProjectile(ItemStack stack, Item item) {
		return getChargedProjectiles(stack).stream().anyMatch(proj -> proj.getItem() == item);
	}

	private static void shootProjectile(World world, EntityLivingBase entity, ItemStack stack, ItemStack ammo, float p_40900_, boolean p_40901_, float p_40902_, float p_40903_, float p_40904_) {
		if (world.isRemote) return;
		boolean isFirework = ammo.getItem() == Items.FIREWORKS;
		Entity projectile;
		if (isFirework) {
			projectile = new EntityFireworkRocket(world, entity.posX, entity.posY + entity.getEyeHeight() - 0.15F, entity.posZ, ammo);
			((IFireworksProjectile)projectile).setOwner(entity);
			((IFireworksProjectile)projectile).setShotAtAngle();
		} else {
			projectile = getArrow(world, entity, stack, ammo);
			if (p_40901_ || p_40904_ != 0.0F) {
				((EntityArrow)projectile).pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
			}
		}
		if (entity instanceof ICrossbowAttackMob) {
			ICrossbowAttackMob crossbowattackmob = (ICrossbowAttackMob)entity;
			crossbowattackmob.shootCrossbowProjectile(entity, crossbowattackmob.getTarget(), projectile, p_40904_, 1.6F);
		} else {
			Vec3d look = entity.getLook(1.0F);
			float angle =  p_40904_ * ((float)Math.PI / 180F);
			double s = (float) Math.sin(angle);
			float c = (float) Math.cos(angle);
			((IProjectile)projectile).shoot(look.x * c - look.z * s, look.y, look.x * s + look.z * c, p_40902_, p_40903_);
		}
		stack.damageItem(isFirework ? 3 : 1, entity);
		world.spawnEntity(projectile);
		world.playSound(null, entity.posX, entity.posY, entity.posZ, RaidsSoundEvents.CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0F, p_40900_);
	}

	private static EntityArrow getArrow(World world, EntityLivingBase entity, ItemStack crossbow, ItemStack ammo) {
		ItemArrow arrowitem = (ItemArrow) (ammo.getItem() instanceof ItemArrow ? ammo.getItem() : Items.ARROW);
		EntityArrow arrow = arrowitem.createArrow(world, ammo, entity);
		if (entity instanceof EntityPlayer) arrow.setIsCritical(true);
		ICrossbowArrow crossbowArrow = (ICrossbowArrow) arrow;
		crossbowArrow.setShotFromCrossbow(true);
		int i = EnchantmentHelper.getEnchantmentLevel(RaidsContent.PIERCING, crossbow);
		if (i > 0) crossbowArrow.setPierceLevel((byte)i);
		return arrow;
	}

	public static void performShooting(World world, EntityLivingBase entity, ItemStack stack, float p_40892_, float p_40893_) {
		if (entity instanceof EntityPlayer && ForgeEventFactory.onArrowLoose(stack, world, (EntityPlayer) entity, 1, true) < 0) return;
		List<ItemStack> list = getChargedProjectiles(stack);
		float[] afloat = getShotPitches(entity.getRNG());
		for(int i = 0; i < list.size(); ++i) {
			ItemStack itemstack = list.get(i);
			boolean flag = entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.isCreativeMode;
			if (!itemstack.isEmpty()) {
				if (i == 0) shootProjectile(world, entity, stack, itemstack, afloat[i], flag, p_40892_, p_40893_, 0.0F);
				else if (i == 1) shootProjectile(world, entity, stack, itemstack, afloat[i], flag, p_40892_, p_40893_, -10.0F);
				else if (i == 2) shootProjectile(world, entity, stack, itemstack, afloat[i], flag, p_40892_, p_40893_, 10.0F);
			}
		}
		onCrossbowShot(world, entity, stack);
	}

	private static float[] getShotPitches(Random rand) {
		boolean flag = rand.nextBoolean();
		return new float[]{1.0F, getRandomShotPitch(flag, rand), getRandomShotPitch(!flag, rand)};
	}

	private static float getRandomShotPitch(boolean flag, Random rand) {
		float f = flag ? 0.63F : 0.43F;
		return 1.0F / (rand.nextFloat() * 0.5F + 1.8F) + f;
	}

	private static void onCrossbowShot(World world, EntityLivingBase entity, ItemStack stack) {
		if (entity instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP)entity;
			if (!world.isRemote) {
				//CriteriaTriggers.SHOT_CROSSBOW.trigger(serverEntityPlayer, p_40908_);
			}
			player.addStat(StatList.getObjectUseStats(stack.getItem()));
		}
		clearChargedProjectiles(stack);
	}
	
	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase entity, int count) {
	      if (!entity.world.isRemote) {
	         int i = EnchantmentHelper.getEnchantmentLevel(RaidsContent.QUICK_CHARGE, stack);
	         SoundEvent startSound = getStartSound(0);
	         SoundEvent middleSound = i == 0 ? RaidsSoundEvents.CROSSBOW_LOADING_MIDDLE : null;
	         float f = (float)(stack.getMaxItemUseDuration() - count) / (float)getChargeDuration(stack);
	         if (f < 0.2F) {
	            startSoundPlayed = false;
	            midLoadSoundPlayed = false;
	         }

	         if (f >= 0.2F && !startSoundPlayed) {
	            startSoundPlayed = true;
				 entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, startSound, entity instanceof EntityPlayer ?
						 SoundCategory.PLAYERS : SoundCategory.HOSTILE, 0.5F, 1.0F);
	         }

	         if (f >= 0.5F && middleSound != null && !this.midLoadSoundPlayed) {
				 midLoadSoundPlayed = true;
				 entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, middleSound, entity instanceof EntityPlayer ?
						 SoundCategory.PLAYERS : SoundCategory.HOSTILE, 0.5F, 1.0F);
	         }
	      }
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return getChargeDuration(stack) + 3;
	}

	public static int getChargeDuration(ItemStack stack) {
		int i =  EnchantmentHelper.getEnchantmentLevel(RaidsContent.QUICK_CHARGE, stack);
		return i == 0 ? 25 : 25 - 5 * i;
	}

	private SoundEvent getStartSound(int quick_charge_level) {
		switch(quick_charge_level) {
			case 1:
				return RaidsSoundEvents.CROSSBOW_QUICK_CHARGE_1;
			case 2:
				return RaidsSoundEvents.CROSSBOW_QUICK_CHARGE_2;
			case 3:
				return RaidsSoundEvents.CROSSBOW_QUICK_CHARGE_3;
			default:
				return RaidsSoundEvents.CROSSBOW_LOADING_START;
		}
	}

	private static float getPowerForTime(int duration, ItemStack stack) {
		float charge = (float)duration / (float) getChargeDuration(stack);
		if (charge > 1.0F) {
			charge = 1.0F;
		}
		return charge;
	}

	@Override
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
		List<ItemStack> list = getChargedProjectiles(stack);
		if (isCharged(stack) && !list.isEmpty()) {
			ItemStack itemstack = list.get(0);
			tooltip.add(new TextComponentTranslation("item.raids.crossbow.projectile").getFormattedText() + " " + (itemstack.getDisplayName()));
			if (flag.isAdvanced() && itemstack.getItem() == Items.FIREWORKS) {
				List<String> firework_props = Lists.newArrayList();
				Items.FIREWORKS.addInformation(itemstack, world, firework_props, flag);
				if (!firework_props.isEmpty()) {
					for(int i = 0; i < firework_props.size(); ++i) {
						firework_props.set(i, (new TextComponentString("  " + firework_props.get(i))
								.setStyle(new Style().setColor(TextFormatting.GRAY)).getFormattedText()));
					}
					tooltip.addAll(firework_props);
				}
			}
		}
	}

	@Override
	public int getItemEnchantability()
	{
		return 1;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged || oldStack.getItem() != newStack.getItem();
	}
	
	protected static ItemStack getProjectile(EntityLivingBase entity) {
		if (isAmmo(entity.getHeldItem(EnumHand.OFF_HAND))) {
			return entity.getHeldItem(EnumHand.OFF_HAND);
		}
		else if (isAmmo(entity.getHeldItem(EnumHand.MAIN_HAND))) {
			return entity.getHeldItem(EnumHand.MAIN_HAND);
		}
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if (player.isCreative()) return new ItemStack(Items.ARROW);
			for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
				ItemStack itemstack = player.inventory.getStackInSlot(i);
				if (isAmmo(itemstack)) return itemstack;
			}
			return ItemStack.EMPTY;
		}
		return new ItemStack(Items.ARROW);
	}
	
	public static boolean isAmmo(ItemStack stack) {
		return stack.getItem() instanceof ItemArrow || stack.getItem() instanceof ItemFirework;
	}
	
}
