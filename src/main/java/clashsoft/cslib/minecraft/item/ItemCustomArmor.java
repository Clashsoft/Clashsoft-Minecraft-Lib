package clashsoft.cslib.minecraft.item;

import clashsoft.cslib.minecraft.util.Constants;
import clashsoft.cslib.reflect.CSReflection;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemCustomArmor extends ItemArmor
{
	public ItemCustomArmor(ArmorMaterial material, int renderIndex, int armorType)
	{
		super(material, renderIndex, armorType & 3);
		CSReflection.setField(Constants.FIELD_ItemArmor_armorType, this, armorType);
		CSReflection.setField(Constants.FIELD_ItemArmor_damageReduction, this, this.getDamageReductionAmount(material));
	}
	
	@Override
	public void registerIcons(IIconRegister iconRegister)
	{
		this.itemIcon = iconRegister.registerIcon(this.getIconString());
	}
	
	public int getDamageReductionAmount(ArmorMaterial material)
	{
		return material.getDamageReductionAmount(this.armorType & 3);
	}
	
	@Override
	public boolean isValidArmor(ItemStack stack, int armorType, Entity entity)
	{
		return armorType == this.armorType;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (this.armorType < 4)
		{
			return super.onItemRightClick(stack, world, player);
		}
		return stack;
	}
}
