package net.smileycorp.raids.common;

import com.google.common.collect.Lists;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTBase;
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
import net.smileycorp.raids.common.entities.ICrossbowArrow;
import net.smileycorp.raids.common.entities.ICrossbowAttackMob;

import javax.vecmath.Vector3f;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class ItemCrossbow extends Item {

	private boolean startSoundPlayed = false;
	private boolean midLoadSoundPlayed = false;
	
	public ItemCrossbow() {
		setUnlocalizedName(Constants.name("Crossbow"));
		setRegistryName(Constants.loc("Crossbow"));
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.COMBAT);
		setMaxDamage(326);
		addPropertyOverride(new ResourceLocation("pulling"), (stack, worldIn, entityIn) ->
				entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F);
		addPropertyOverride(new ResourceLocation("pull"), (stack, worldIn, entityIn) -> {
			if (entityIn == null) return 0.0F;
			return stack.getMaxItemUseDuration() - entityIn.getItemInUseCount() / 20.0F;
		});
		addPropertyOverride(new ResourceLocation("charged"), (stack, worldIn, entityIn) -> isCharged(stack) ? 1 : 0);
		addPropertyOverride(new ResourceLocation("firework"), (stack, worldIn, entityIn) -> {
			List<ItemStack> projectiles = getChargedProjectiles(stack);
			if (projectiles.size() < 1) return 0;
			return projectiles.get(0).getItem() == Items.FIREWORKS ? 1 : 0;
		});
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (isCharged(stack)) {
			setCharged(stack, false);
			return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
		} else if (!isCharged(stack)) {
			System.out.println("eeee");
			this.startSoundPlayed = false;
			this.midLoadSoundPlayed = false;
			player.setActiveHand(hand);
			return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
		}
		return ActionResult.newResult(EnumActionResult.FAIL, stack);
	}
	
	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
	      if (!player.world.isRemote) {
	         //int i = EnchantmentHelper.getItemEnchantmentWorld(Enchantments.QUICK_CHARGE, stack);
	         SoundEvent soundevent = getStartSound(0);
	         SoundEvent soundevent1 = /*i == 0 ?*/ RaidsContent.CROSSBOW_LOADING_MIDDLE /*: null*/;
	         float f = (float)(stack.getMaxItemUseDuration() - count) / (float)getChargeDuration(stack);
	         if (f < 0.2F) {
	            this.startSoundPlayed = false;
	            this.midLoadSoundPlayed = false;
	         }

	         if (f >= 0.2F && !this.startSoundPlayed) {
	            this.startSoundPlayed = true;
	            player.playSound(soundevent, 0.5F, 1.0F);
	         }

	         if (f >= 0.5F && soundevent1 != null && !this.midLoadSoundPlayed) {
	            this.midLoadSoundPlayed = true;
				 player.playSound(soundevent1, 0.5F, 1.0F);
	         }
	      }
	}

	private SoundEvent getStartSound(int p_40852_) {
		switch(p_40852_) {
			case 1:
				return RaidsContent.CROSSBOW_QUICK_CHARGE_1;
			case 2:
				return RaidsContent.CROSSBOW_QUICK_CHARGE_2;
			case 3:
				return RaidsContent.CROSSBOW_QUICK_CHARGE_3;
			default:
				return RaidsContent.CROSSBOW_LOADING_START;
		}
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entity, int timeLeft) {
		if (!isCharged(stack)) {
			int duration = getUseDuration(stack) - timeLeft;
			float charge = getCharge(duration, stack);
			ItemStack ammo = getAmmo(entity);
			if (charge >= 1 && !isCharged(stack) && !ammo.isEmpty()) {
				setCharged(stack, true);
				SoundCategory soundcategory = entity instanceof EntityPlayer ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
				world.playSound(null, entity.posX, entity.posY, entity.posZ, new SoundEvent(Constants.loc("item.crossbow.load")), soundcategory, 1.0F, 1.0F / (world.rand.nextFloat() * 0.5F + 1.0F) + 0.2F);
			}
		}
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }
	
	@Override
	public int getItemEnchantability() {
		return 1;
	}
	
	public int getUseDuration(ItemStack stack) {
		return getChargeDuration(stack) + 3;
	}

	public static int getChargeDuration(ItemStack stack) {
		int i =  0 /*EnchantmentHelper.getEnchantmentWorld(Enchantments.QUICK_CHARGE, stack)*/;
		return i == 0 ? 25 : 25 - 5 * i;
	}

	public EnumAction getItemUseAction(ItemStack stack) {
      return EnumAction.BOW;
	}
	
	protected ItemStack getAmmo(EntityLivingBase entity) {
		if (entity instanceof EntityPlayer) {
			EntityPlayer EntityPlayer = (EntityPlayer) entity;
	        if (isAmmo(EntityPlayer.getHeldItem(EnumHand.OFF_HAND))) {
	            return EntityPlayer.getHeldItem(EnumHand.OFF_HAND);
	        }
	        else if (isAmmo(EntityPlayer.getHeldItem(EnumHand.MAIN_HAND))) {
	            return EntityPlayer.getHeldItem(EnumHand.MAIN_HAND);
	        }
	        else {
	            for (int i = 0; i < EntityPlayer.inventory.getSizeInventory(); ++i) {
	                ItemStack itemstack = EntityPlayer.inventory.getStackInSlot(i);
	
	                if (isAmmo(itemstack)) {
	                    return itemstack;
	                }
	            }
	
	            return ItemStack.EMPTY;
	        }
		} else return new ItemStack(Items.ARROW);
	}
	
	public static boolean isAmmo(ItemStack stack) {
		return stack.getItem() instanceof ItemArrow || stack.getItem() instanceof ItemFirework;
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
	}

	private static void addChargedProjectile(ItemStack stack, ItemStack projectile) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) nbt = new NBTTagCompound();
		NBTTagList taglist;
		if (nbt.hasKey("ChargedProjectiles", 9)) {
			taglist = nbt.getTagList("ChargedProjectiles", 10);
		} else {
			taglist = new NBTTagList();
		}
		NBTTagCompound stacknbt = new NBTTagCompound();
		projectile.writeToNBT(stacknbt);
		taglist.appendTag(stacknbt);
		nbt.setTag("ChargedProjectiles", taglist);
	}

	private static List<ItemStack> getChargedProjectiles(ItemStack stack) {
		List<ItemStack> list = Lists.newArrayList();
		NBTTagCompound NBTTagCompound = stack.getTagCompound();
		if (NBTTagCompound != null && NBTTagCompound.hasKey("ChargedProjectiles", 9)) {
			NBTTagList taglist = NBTTagCompound.getTagList("ChargedProjectiles", 10);
			if (taglist != null) {
				taglist.forEach(new Consumer<NBTBase>() {
					@Override
					public void accept(NBTBase t) {
						try {
							list.add(new ItemStack((NBTTagCompound)t));
						} catch (Exception e) {}
					}
				});
			}
		}
		return list;
	}

   private static void clearChargedProjectiles(ItemStack stack) {
      NBTTagCompound nbt = stack.getTagCompound();
      if (nbt != null) {
         nbt.setTag("ChargedProjectiles", new NBTTagList());
      }

   }

   public static boolean hasChargedProjectile(ItemStack stack, Item ammoItem) {
      return getChargedProjectiles(stack).stream().anyMatch((test) -> {
         return test.getItem() == ammoItem;
      });
   }
   
   private static float getCharge(int duration, ItemStack stack) {
      float charge = (float)duration / (float) getChargeDuration(stack);
      if (charge > 1.0F) {
         charge = 1.0F;
      }
	  System.out.println(charge);
      return charge;
   }

	public static void performShooting(World p_40888_, EntityLivingBase p_40889_, EnumHand p_40890_, ItemStack p_40891_, float p_40892_, float p_40893_) {
		if (p_40889_ instanceof EntityPlayer && net.minecraftforge.event.ForgeEventFactory.onArrowLoose(p_40891_, p_40888_, (EntityPlayer) p_40889_, 1, true) < 0) return;
		List<ItemStack> list = getChargedProjectiles(p_40891_);
		float[] afloat = getShotPitches(p_40889_.getRNG());

		for(int i = 0; i < list.size(); ++i) {
			ItemStack itemstack = list.get(i);
			boolean flag = p_40889_ instanceof EntityPlayer && ((EntityPlayer)p_40889_).capabilities.isCreativeMode;
			if (!itemstack.isEmpty()) {
				if (i == 0) {
					shootProjectile(p_40888_, p_40889_, p_40890_, p_40891_, itemstack, afloat[i], flag, p_40892_, p_40893_, 0.0F);
				} else if (i == 1) {
					shootProjectile(p_40888_, p_40889_, p_40890_, p_40891_, itemstack, afloat[i], flag, p_40892_, p_40893_, -10.0F);
				} else if (i == 2) {
					shootProjectile(p_40888_, p_40889_, p_40890_, p_40891_, itemstack, afloat[i], flag, p_40892_, p_40893_, 10.0F);
				}
			}
		}
		onCrossbowShot(p_40888_, p_40889_, p_40891_);
	}

	private static float[] getShotPitches(Random p_40924_) {
		boolean flag = p_40924_.nextBoolean();
		return new float[]{1.0F, getRandomShotPitch(flag, p_40924_), getRandomShotPitch(!flag, p_40924_)};
	}

	private static float getRandomShotPitch(boolean p_150798_, Random p_150799_) {
		float f = p_150798_ ? 0.63F : 0.43F;
		return 1.0F / (p_150799_.nextFloat() * 0.5F + 1.8F) + f;
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

	private static void shootProjectile(World p_40895_, EntityLivingBase p_40896_, EnumHand p_40897_, ItemStack p_40898_, ItemStack p_40899_, float p_40900_, boolean p_40901_, float p_40902_, float p_40903_, float p_40904_) {
		if (!p_40895_.isRemote) {
			boolean isFirework = p_40899_.getItem() == Items.FIREWORKS;
			Entity projectile = null;
			if (isFirework) {
				//projectile = new EntityFireworkRocket(p_40895_, p_40896_.posX, p_40896_.getEyeHeight() - (double)0.15F, p_40896_.posZ, p_40899_);
			} else {
				projectile = getArrow(p_40895_, p_40896_, p_40898_, p_40899_);
				if (p_40901_ || p_40904_ != 0.0F) {
					((EntityArrow)projectile).pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
				}
			}

			if (p_40896_ instanceof ICrossbowAttackMob) {
				ICrossbowAttackMob crossbowattackmob = (ICrossbowAttackMob)p_40896_;
				crossbowattackmob.shootCrossbowProjectile(p_40896_, crossbowattackmob.getTarget(), projectile, p_40904_, 1.6F);
			} else {
				Vec3d vec31 = p_40896_.getLook(1.0F);
				Vector3f angle = new Vector3f((float) vec31.x, (float) vec31.y, (float) vec31.z);
				Vec3d vec3 = p_40896_.getLook(1.0F);
				Vector3f vector3f = new Vector3f((float) vec3.x, (float) vec3.y, (float) vec3.z);
				vector3f.angle(angle);
				((IProjectile)projectile).shoot(vector3f.x, vector3f.y, vector3f.z, p_40902_, p_40903_);
			}

			p_40898_.damageItem(isFirework ? 3 : 1, p_40896_);
			p_40895_.spawnEntity(projectile);
			p_40895_.playSound(null, p_40896_.posX, p_40896_.posY, p_40896_.posZ, RaidsContent.CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0F, p_40900_);
		}
	}

	private static EntityArrow getArrow(World p_40915_, EntityLivingBase p_40916_, ItemStack p_40917_, ItemStack p_40918_) {
		ItemArrow arrowitem = (ItemArrow) (p_40918_.getItem() instanceof ItemArrow ? p_40918_.getItem() : Items.ARROW);
		EntityArrow abstractarrow = arrowitem.createArrow(p_40915_, p_40918_, p_40916_);
		if (p_40916_ instanceof EntityPlayer) {
			abstractarrow.setIsCritical(true);
		}
		ICrossbowArrow crossbowArrow = (ICrossbowArrow) abstractarrow;
		crossbowArrow.setSoundEvent(RaidsContent.CROSSBOW_HIT);
		crossbowArrow.setShotFromCrossbow(true);
		int i = 0/*EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PIERCING, p_40917_)*/;
		if (i > 0) {
			crossbowArrow.setPierceLevel((byte)i);
		}

		return abstractarrow;
	}

	@Override
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
		List<ItemStack> list = getChargedProjectiles(stack);
		if (isCharged(stack) && !list.isEmpty()) {
			ItemStack itemstack = list.get(0);
			tooltip.add(new TextComponentTranslation("item.raids.crossbow.projectile").getFormattedText() + (itemstack.getDisplayName()));
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
   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
	   //force ominous banners into creative tab, as this is our only item
	   if (tab == CreativeTabs.DECORATIONS || tab == CreativeTabs.SEARCH) {
		   NonNullList<ItemStack> newList = NonNullList.<ItemStack>create();
		   for (ItemStack stack : items) {
			   if (stack.getItem() == Items.END_CRYSTAL) newList.add(RaidsContent.OMINOUS_BANNER);
			   newList.add(stack);
		   }
		   items.clear();
		   items.addAll(newList);
	   }
	   super.getSubItems(tab, items);
   }
	
}
