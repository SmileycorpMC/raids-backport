package net.smileycorp.raids.config;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraftforge.common.config.Configuration;

public class EntityAttributesEntry {
    
    private double movementSpeed;
    private double flyingSpeed;
    private double followRange;
    private double attackDamage;
    private double maxHealth;
    private double armor;
    private double armorToughness;
    private double knockbackResistance;
    
    public EntityAttributesEntry(Configuration config, String name, double movementSpeed, double followRange, double attackDamage, double maxHealth, double armor, double armorToughness, double knockbackResistance, double flyingSpeed) {
        this.movementSpeed = config.get(name, "movementSpeed", movementSpeed, "Movement Speed").getDouble();
        this.followRange = config.get(name, "followRange", followRange, "Follow Range").getDouble();
        this.attackDamage = config.get(name, "attackDamage", attackDamage, "Attack Damage").getDouble();
        this.maxHealth = config.get(name, "maxHealth", maxHealth, "Max Health").getDouble();
        this.armor = config.get(name, "armor", armor, "Armor").getDouble();
        this.armorToughness = config.get(name, "armorToughness", armorToughness, "Armor Toughness").getDouble();
        this.knockbackResistance = config.get(name, "knockbackResistance", knockbackResistance, "Knockback Resistance").getDouble();
        this.flyingSpeed = flyingSpeed == 0 ? 0 : config.get(name, "flyingSpeed", flyingSpeed, "Flying Speed").getDouble();
    }
    
    public void applyAttributes(EntityLivingBase entity) {
        entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(movementSpeed);
        entity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(followRange);
        entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(attackDamage);
        entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(maxHealth);
        entity.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(armor);
        entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(armorToughness);
        entity.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(knockbackResistance);
        if (flyingSpeed != 0) entity.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(flyingSpeed);
    }
    
}
