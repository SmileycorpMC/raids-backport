package net.smileycorp.raids.config;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
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
        AbstractAttributeMap map = entity.getAttributeMap();
        map.getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(movementSpeed);
        map.getAttributeInstance(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(followRange);
        map.getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(attackDamage);
        map.getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(maxHealth);
        map.getAttributeInstance(SharedMonsterAttributes.ARMOR).setBaseValue(armor);
        map.getAttributeInstance(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(armorToughness);
        map.getAttributeInstance(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(knockbackResistance);
        if (flyingSpeed != 0) {
            map.registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
            map.getAttributeInstance(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(flyingSpeed);
        }
    }
    
}
