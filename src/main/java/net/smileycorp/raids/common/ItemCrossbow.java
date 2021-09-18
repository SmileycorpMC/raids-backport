package net.smileycorp.raids.common;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFirework;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import com.google.common.collect.Lists;

public class ItemCrossbow extends Item {
	
	//public static EnumAction ACTION = EnumHelper.addAction("CROSSBOW");
	
	public ItemCrossbow() {
		setUnlocalizedName(ModDefinitions.getName("Crossbow"));
		setRegistryName(ModDefinitions.getResource("Crossbow"));
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.COMBAT);
		setMaxDamage(326);
		addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter() {
            @Override
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
                if (entityIn == null)
                {
                    return 0.0F;
                }
                else
                {
                    return !(entityIn.getActiveItemStack().getItem() instanceof ItemBow) ? 0.0F : (stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / 20.0F;
                }
            }
        });
        this.addPropertyOverride(new ResourceLocation("pulling"), new IItemPropertyGetter() {
            @Override
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
            {
                return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
            }
        });
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (isCharged(stack)) {
			setCharged(stack, false);
			return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
		} else if (!isCharged(stack)) {
			System.out.print("eeee");
			player.setActiveHand(hand);
			return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
		}
		return ActionResult.newResult(EnumActionResult.FAIL, stack);
	}
	
	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
	      if (!player.world.isRemote) {
	         //int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, stack);
	         //SoundEvent soundevent = this.getStartSound(i);
	         //SoundEvent soundevent1 = i == 0 ? SoundEvents.CROSSBOW_LOADING_MIDDLE : null;
	         //float f = (float)(stack.getMaxItemUseDuration() - p_219972_4_) / (float)getChargeDuration(stack);
	         /*if (f < 0.2F) {
	            this.startSoundPlayed = false;
	            this.midLoadSoundPlayed = false;
	         }

	         if (f >= 0.2F && !this.startSoundPlayed) {
	            this.startSoundPlayed = true;
	            p_219972_1_.playSound((PlayerEntity)null, p_219972_2_.getX(), p_219972_2_.getY(), p_219972_2_.getZ(), soundevent, SoundCategory.PLAYERS, 0.5F, 1.0F);
	         }

	         if (f >= 0.5F && soundevent1 != null && !this.midLoadSoundPlayed) {
	            this.midLoadSoundPlayed = true;
	            p_219972_1_.playSound((PlayerEntity)null, p_219972_2_.getX(), p_219972_2_.getY(), p_219972_2_.getZ(), soundevent1, SoundCategory.PLAYERS, 0.5F, 1.0F);
	         }*/
	      }

	   }
	
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entity, int timeLeft) {
		if (!isCharged(stack)) {
			int duration = getUseDuration(stack) - timeLeft;
			float charge = getCharge(duration, stack);
			ItemStack ammo = getAmmo(entity);
			if (charge >= 1 && !isCharged(stack) &&  !ammo.isEmpty()) {
				setCharged(stack, true);
				SoundCategory soundcategory = entity instanceof EntityPlayer ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
				world.playSound(null, entity.posX, entity.posY, entity.posZ, new SoundEvent(ModDefinitions.getResource("item.crossbow.load")), soundcategory, 1.0F, 1.0F / (world.rand.nextFloat() * 0.5F + 1.0F) + 0.2F);
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
		return getChargeTime(stack) + 3;
	}

	public static int getChargeTime(ItemStack stack) {
		int i =  1 /*EnchantmentHelper.getEnchantmentLevel(Enchantments.QUICK_CHARGE, stack)*/;
		return i == 0 ? 25 : 25 - 5 * i;
	}
	
	public EnumAction getUseAction(ItemStack stack) {
      return isCharged(stack) ? EnumAction.BLOCK : EnumAction.BOW;
	}
	
	protected ItemStack getAmmo(EntityLivingBase entity) {
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
	        if (isAmmo(player.getHeldItem(EnumHand.OFF_HAND))) {
	            return player.getHeldItem(EnumHand.OFF_HAND);
	        }
	        else if (isAmmo(player.getHeldItem(EnumHand.MAIN_HAND))) {
	            return player.getHeldItem(EnumHand.MAIN_HAND);
	        }
	        else {
	            for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
	                ItemStack itemstack = player.inventory.getStackInSlot(i);
	
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

   private static void clearProjectiles(ItemStack stack) {
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
      float charge = (float)duration / (float)getChargeTime(stack);
      if (charge > 1.0F) {
         charge = 1.0F;
      }
      return charge;
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
