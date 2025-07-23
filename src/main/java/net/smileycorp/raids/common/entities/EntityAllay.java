package net.smileycorp.raids.common.entities;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.smileycorp.atlas.api.entity.ai.EntityAIMoveRandomFlying;
import net.smileycorp.atlas.api.entity.ai.FlyingMoveControl;
import net.smileycorp.atlas.api.util.DirectionUtils;
import net.smileycorp.atlas.api.util.MathUtils;
import net.smileycorp.raids.common.RaidsAdvancements;
import net.smileycorp.raids.common.RaidsSoundEvents;
import net.smileycorp.raids.common.entities.ai.EntityAIAllayDeliverItem;
import net.smileycorp.raids.common.entities.ai.EntityAIAllayPickupItem;
import net.smileycorp.raids.common.entities.ai.EntityAIAllayStayNearTarget;
import net.smileycorp.raids.config.EntityConfig;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.UUID;

public class EntityAllay extends EntityCreature implements IEntityOwnable {
    
    public static final Set<BlockJukebox.TileEntityJukebox> JUKEBOXES = Sets.newHashSet();
    
    private static final DataParameter<Boolean> IS_DANCING = EntityDataManager.createKey(EntityAllay.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> CAN_DUPLICATE = EntityDataManager.createKey(EntityAllay.class, DataSerializers.BOOLEAN);
    
    private final InventoryBasic inventory = new InventoryBasic(getName(), false, 1);
    private BlockPos jukebox;
    private BlockPos noteBlock;
    private ItemStack items = ItemStack.EMPTY;
    private EntityPlayer owner;
    private UUID ownerUUID;
    private int duplicationCooldown;
    private float holdingItemAnimationTicks;
    private float holdingItemAnimationTicks0;
    private float dancingAnimationTicks;
    private float spinningAnimationTicks;
    private float spinningAnimationTicks0;
    private int noteBlockCooldown = 0;
    
    public EntityAllay(World world) {
        super(world);
        moveHelper = new FlyingMoveControl(this);
        setSize(0.35f, 0.6f);
    }
    
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIAllayStayNearTarget(this));
        tasks.addTask(2, new EntityAIAllayPickupItem(this));
        tasks.addTask(3, new EntityAIAllayDeliverItem(this));
        tasks.addTask(8, new EntityAIMoveRandomFlying(this));
    }
    
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        EntityConfig.allay.applyAttributes(this);
    }
    
    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(IS_DANCING, false);
        dataManager.register(CAN_DUPLICATE, true);
    }
    
    @Override
    public boolean attackEntityFrom(DamageSource src, float amount) {
        if (src.getTrueSource() != null) if (src.getTrueSource() == getOwner()) return false;
        return super.attackEntityFrom(src, amount);
    }
    
    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();
        if (!world.isRemote |! isEntityAlive()) return;
        holdingItemAnimationTicks0 = holdingItemAnimationTicks;
        holdingItemAnimationTicks = MathHelper.clamp(holdingItemAnimationTicks + (hasItemInSlot(EntityEquipmentSlot.MAINHAND) ? 1 : -1), 0, 5);
        if (isDancing()) {
            dancingAnimationTicks++;
            spinningAnimationTicks0 = spinningAnimationTicks;
            if (isSpinning()) spinningAnimationTicks++;
            else spinningAnimationTicks--;
            spinningAnimationTicks = MathHelper.clamp(spinningAnimationTicks, 0, 15);
            return;
        }
        dancingAnimationTicks = 0;
        spinningAnimationTicks = 0;
        spinningAnimationTicks0 = 0;
    }
    
    @Override
    public void updateAITasks() {
        super.updateAITasks();
        if (!isEntityAlive()) return;
        if (ticksExisted % 5 == 2 && !canDance()) findJukebox();
        if (noteBlockCooldown > 0) if (noteBlockCooldown-- <=0) noteBlock = null;
        if (ticksExisted % 10 == 0) heal(1);
        if (duplicationCooldown > 0) duplicationCooldown--;
        if (duplicationCooldown == 0 &! canDuplicate()) dataManager.set(CAN_DUPLICATE, true);
    }
    
    @Override
    public boolean hasNoGravity() {
        return true;
    }
    
    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (world.isRemote) return false;
        ItemStack stack = player.getHeldItem(hand);
        ItemStack stack1 = getHeldItemMainhand();
        if (isDancing() && EntityConfig.isDuplicationItem(stack) && canDuplicate()) {
            stack.shrink(1);
            playSound(RaidsSoundEvents.ALLAY_DUPLICATE, 2, 1);
            duplicate();
            world.setEntityState(this, (byte)18);
            player.setActiveHand(hand);
            player.swingArm(hand);
            return true;
        }
        if (!stack.isEmpty() &!(itemIsEqual(stack, stack1))) {
            if (!stack1.isEmpty() &! world.isRemote) entityDropItem(stack1, 0);
            owner = player;
            ownerUUID = player.getUniqueID();
            stack1 = stack.copy();
            stack1.setCount(1);
            setHeldItem(EnumHand.MAIN_HAND, stack1);
            player.setActiveHand(hand);
            player.swingArm(hand);
            stack.shrink(1);
            return true;
        }
        if (hand == EnumHand.OFF_HAND && stack.isEmpty() &! stack1.isEmpty() && player.getHeldItem(EnumHand.MAIN_HAND).isEmpty()) {
            if (!world.isRemote) entityDropItem(stack1, 0);
            owner = null;
            ownerUUID = null;
            setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
        }
        return false;
    }
    
    private boolean itemIsEqual(ItemStack stack, ItemStack stack1) {
        return ItemStack.areItemStacksEqual(stack, stack1) && potionMatches(stack, stack1);
    }
    
    private boolean potionMatches(ItemStack stack, ItemStack stack1) {
        if (!stack1.hasTagCompound()) return true;
        NBTTagCompound nbt1 = stack1.getTagCompound();
        if (!nbt1.hasKey("Potion")) return true;
        if (!stack.hasTagCompound()) return false;
        NBTTagCompound nbt = stack1.getTagCompound();
        if (!nbt1.hasKey("Potion")) return false;
        return nbt.getCompoundTag("Potion").equals(nbt1.getCompoundTag("Potion"));
    }
    
    public float getHoldingItemAnimationProgress(float pt) {
        return MathUtils.lerp(pt, holdingItemAnimationTicks0, holdingItemAnimationTicks) / 5f;
    }
    
    public void duplicate() {
        EntityAllay allay = new EntityAllay(world);
        allay.setPosition(posX, posY, posZ);
        allay.enablePersistence();
        allay.resetDuplicationCooldown();
        resetDuplicationCooldown();
        world.spawnEntity(allay);
        if (!(owner instanceof EntityPlayerMP) || getHeldItemMainhand().getItem() != Items.LEAD) return;
        RaidsAdvancements.FRIEND_INSIDE_ME.trigger((EntityPlayerMP) owner);
    }
    
    private void resetDuplicationCooldown() {
        dataManager.set(CAN_DUPLICATE, false);
        duplicationCooldown = 6000;
    }
    
    private boolean canDuplicate() {
        return dataManager.get(CAN_DUPLICATE);
    }
    
    public boolean isDancing() {
        return dataManager.get(IS_DANCING);
    }
    
    public void setDancing(boolean dancing) {
        dataManager.set(IS_DANCING, dancing);
    }
    
    public boolean canDance() {
        if (jukebox == null) return false;
        if (jukebox.distanceSq(posX, posY, posZ) > 100 || world.getBlockState(jukebox).getBlock() != Blocks.JUKEBOX) return false;
        TileEntity te = world.getTileEntity(jukebox);
        if (!(te instanceof BlockJukebox.TileEntityJukebox)) return false;
        return !((BlockJukebox.TileEntityJukebox)te).getRecord().isEmpty();
    }
    
    private void findJukebox() {
        for (BlockJukebox.TileEntityJukebox tile : JUKEBOXES) if (tile.getDistanceSq(posX, posY, posZ) <= 100 &! tile.getRecord().isEmpty()) {
            jukebox = tile.getPos();
            setDancing(true);
            return;
        }
        setDancing(false);
    }
    
    @Nullable
    @Override
    public UUID getOwnerId() {
        return ownerUUID;
    }
    
    @Nullable
    @Override
    public Entity getOwner() {
        if (owner == null && ownerUUID != null) owner = world.getMinecraftServer().getPlayerList().getPlayerByUUID(ownerUUID);
        return owner;
    }
    
    public BlockPos getJukeboxPos() {
        return jukebox;
    }
    
    public BlockPos getNoteBlockPos() {
        return noteBlock;
    }
    
    public Vec3d getWantedPos() {
        if (noteBlock != null) {
            if (world.getBlockState(noteBlock).getBlock() == Blocks.NOTEBLOCK) return DirectionUtils.centerOf(noteBlock);
            noteBlock = null;
            noteBlockCooldown = 0;
        }
        if (getOwner() != null) return new Vec3d(owner.posX, owner.posY + owner.getEyeHeight(), owner.posZ);
        return null;
    }
    
    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        if (nbt.hasKey("owner")) ownerUUID = NBTUtil.getUUIDFromTag(nbt.getCompoundTag("owner"));
        if (nbt.hasKey("DuplicationCooldown")) duplicationCooldown = nbt.getInteger("DuplicationCooldown");
        if (nbt.hasKey("CanDuplicate")) dataManager.set(CAN_DUPLICATE, nbt.getBoolean("CanDuplicate"));
    }
    
    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        if (ownerUUID != null) nbt.setTag("owner", NBTUtil.createUUIDTag(ownerUUID));
        nbt.setInteger("DuplicationCooldown", duplicationCooldown);
        nbt.setBoolean("CanDuplicate", dataManager.get(CAN_DUPLICATE));
    }
    
    public boolean canPickupItem(Entity entity) {
        if (!(entity instanceof EntityItem)) return false;
        EntityItem item = (EntityItem) entity;
        if (item.cannotPickup()) return false;
        ItemStack stack = items.isEmpty() ? getHeldItemMainhand() : items;
        ItemStack stack1 = item.getItem();
        return stack.getItem() == stack1.getItem() && stack.getMetadata() == stack1.getMetadata()
            && (items.isEmpty() || ItemStack.areItemStackTagsEqual(stack, stack1)) && items.getCount() < stack1.getMaxStackSize();
    }
    
    public boolean isSpinning() {
        float f = this.dancingAnimationTicks % 55.0F;
        return f < 15.0F;
    }
    
    public float getSpinningProgress(float spin) {
        return MathUtils.lerp(spin, spinningAnimationTicks0, spinningAnimationTicks) / 15f;
    }
    
    @Override
    public void fall(float distance, float damageMultiplier) {}
    
    @Override
    protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {}
    
    @Override
    protected void playStepSound(BlockPos pos, Block block) {}
    
    @Override
    protected SoundEvent getAmbientSound() {
        return hasItemInSlot(EntityEquipmentSlot.MAINHAND) ? RaidsSoundEvents.ALLAY_AMBIENT_WITH_ITEM
                : RaidsSoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM;
    }
    
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return RaidsSoundEvents.ALLAY_HURT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return RaidsSoundEvents.ALLAY_DEATH;
    }
    
    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }
    
    public boolean isFull() {
        return !items.isEmpty() && items.getCount() < items.getMaxStackSize();
    }
    
    public ItemStack getItems() {
        return items;
    }
    
    public void pickupItem(EntityItem item) {
        playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.2f, ((rand.nextFloat() - rand.nextFloat()) * 0.7f + 1) * 2f);
        if (items.isEmpty()) {
            items = item.getItem();
            item.setDead();
            return;
        }
        int count = Math.min(item.getItem().getCount(), items.getMaxStackSize() - items.getCount());
        items.grow(count);
        item.getItem().shrink(count);
        if (item.getItem().getCount() <= 0) item.setDead();
    }
    
    public boolean canHearBlock(Vec3d pos) {
         if (getDistanceSq(pos.x, pos.y, pos.z) > 256) return false;
        Vec3d endpos = new Vec3d(posX, posY + getEyeHeight(), posZ);
        double dx = endpos.x - pos.x;
        double dy = endpos.y - pos.y;
        double dz = endpos.z - pos.z;
        double magnitude = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2) + Math.pow(dz, 2));
        double mx = (endpos.x- pos.x) / (magnitude * 4d);
        double my = (endpos.y- pos.y)/ (magnitude * 4d);
        double mz = (endpos.z- pos.z) / (magnitude * 4d);
        for (int i = 0; i < Math.ceil(magnitude) * 4; i++) {
            pos = pos.addVector(mx, my, mz);
            if (world.getBlockState(new BlockPos(pos)).getBlock() == Blocks.WOOL) return false;
        }
        return true;
    }
    
    public void setNoteBlockPos(BlockPos pos) {
        noteBlock = pos;
        noteBlockCooldown = 6000;
    }
    
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 18) for(int i = 0; i < 7; ++i) world.spawnParticle(EnumParticleTypes.HEART,
                posX + (double)(rand.nextFloat() * width * 2f) - (double)width, posY + 0.5 + (double)(rand.nextFloat() * height),
                posZ + (double)(rand.nextFloat() * width * 2f) - (double)width,
                rand.nextGaussian() * 0.02, rand.nextGaussian() * 0.02, rand.nextGaussian() * 0.02);
        else super.handleStatusUpdate(id);
    }
    
}
