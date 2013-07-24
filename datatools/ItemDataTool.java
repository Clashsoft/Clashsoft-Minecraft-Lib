package clashsoft.clashsoftapi.datatools;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Multimap;

import clashsoft.clashsoftapi.util.ICSItemRenderable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class ItemDataTool extends ItemTool implements ICSItemRenderable
{
	private float toolDamage;
	
	/** Array of blocks the tool has extra effect against. */
    private Block[] blocksEffectiveAgainst;
    
    private String toolType;
    
    public Map<EnumToolMaterial, Icon> icons = new HashMap<EnumToolMaterial, Icon>();
    public List<EnumToolMaterial> materials = new LinkedList<EnumToolMaterial>();
    public Map<String, EnumToolMaterial> nameToMaterial = new HashMap<String, EnumToolMaterial>();
    public Map<EnumToolMaterial, String> materialToName = new HashMap<EnumToolMaterial, String>();
	
	public ItemDataTool(int par1, float par2, EnumToolMaterial par3EnumToolMaterial, Block[] par4ArrayOfBlock, String par5)
	{
		super(par1, par2, par3EnumToolMaterial, par4ArrayOfBlock);
		blocksEffectiveAgainst = par4ArrayOfBlock;
		toolType = par5;
		toolDamage = par2;
		this.setNoRepair();
	}
	
	public void registerMaterial(EnumToolMaterial material, String name)
	{
		nameToMaterial.put(name, material);
		materialToName.put(material, name);
		materials.add(material);
	}
	
	public static EnumToolMaterial getToolMaterialFromItemStack(ItemStack stack)
	{
		if (stack != null)
		{
			NBTTagCompound nbt = stack.getTagCompound();
			return ((ItemDataTool)stack.getItem()).nameToMaterial.get(nbt.getString("ToolMaterial"));
		}
		return null;
	}
	
	public static ItemStack setToolMaterial(ItemStack stack, EnumToolMaterial material)
	{
		if (stack != null)
		{
			NBTTagCompound nbt = stack.getTagCompound();
			if (nbt == null)
				nbt = new NBTTagCompound();
			String materialName = ((ItemDataTool)stack.getItem()).materialToName.get(material);
			nbt.setString("ToolMaterial", materialName);
			stack.setTagCompound(nbt);
		}
		return stack;
	}
	
	public int getItemMaxDamageFromStack(ItemStack stack)
    {
		EnumToolMaterial tm = getToolMaterialFromItemStack(stack);
        if (tm != null)
        	return tm.getMaxUses();
        return toolMaterial.getMaxUses();
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        for (EnumToolMaterial tm : EnumToolMaterial.values())
        {
        	icons.put(tm, par1IconRegister.registerIcon(toolType.toLowerCase() + tm.toString().toLowerCase()));
        }
    }
	
	public Icon getIcon(ItemStack stack)
    {
        return icons.get(getToolMaterialFromItemStack(stack));
    }
	
	public Icon getIcon(ItemStack stack, int pass)
    {
    	return icons.get(getToolMaterialFromItemStack(stack));
    }
    
    @Override
	public String getItemDisplayName(ItemStack stack)
	{
		return StatCollector.translateToLocal(materialToName.get(getToolMaterialFromItemStack(stack)) + " " + toolType);
	}
    
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
    	//par3List.add(EnumChatFormatting.ITALIC + materialToName.get(getToolMaterialFromItemStack(par1ItemStack)));
    }

	/**
     * Returns the strength of the stack against a given block. 1.0F base, (Quality+1)*2 if correct blocktype, 1.5F if
     * sword
     */
    public float getStrVsBlock(ItemStack par1ItemStack, Block par2Block)
    {
        for (int i = 0; i < this.blocksEffectiveAgainst.length; ++i)
        {
            if (this.blocksEffectiveAgainst[i] == par2Block)
            {
            	EnumToolMaterial tm = getToolMaterialFromItemStack(par1ItemStack);
                if (tm != null)
                	return tm.getEfficiencyOnProperMaterial();
            }
        }

        return 1.0F;
    }

    /**
     * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
     * the damage on the stack.
     */
    public boolean hitEntity(ItemStack par1ItemStack, EntityLiving par2EntityLiving, EntityLiving par3EntityLiving)
    {
    	if (!(this instanceof ItemDataSword || this instanceof ItemDataHoe))
    		par1ItemStack.damageItem(2, par3EntityLiving);
        return true;
    }

    public boolean onBlockDestroyed(ItemStack par1ItemStack, World par2World, int par3, int par4, int par5, int par6, EntityLiving par7EntityLiving)
    {
        if ((double)Block.blocksList[par3].getBlockHardness(par2World, par4, par5, par6) != 0.0D)
        {
            par1ItemStack.damageItem(1, par7EntityLiving);
        }

        return true;
    }

    /**
     * Returns the damage against a given entity.
     */
    public float getDamageVsEntity(Entity par1Entity, ItemStack par2ItemStack)
    {
        EnumToolMaterial tm = getToolMaterialFromItemStack(par2ItemStack);
        if (tm != null)
        	return toolDamage + tm.getDamageVsEntity();
        return damageVsEntity;
    }
    
    public Multimap func_111205_h()
    {
        Multimap multimap = super.func_111205_h();
        multimap.put(SharedMonsterAttributes.field_111264_e.func_111108_a(), new AttributeModifier(field_111210_e, "Weapon modifier", (double)this.toolDamage, 0));
        return multimap;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns True is the item is renderer in full 3D when hold.
     */
    public boolean isFull3D()
    {
        return true;
    }

    /**
     * Return the enchantability factor of the item, most of the time is based on material.
     */
    public int getItemEnchantability()
    {
    	return this.toolMaterial.getEnchantability();
    }

    /**
     * Return the name for this tool's material.
     */
    public String getToolMaterialName()
    {
        return this.toolMaterial.toString();
    }

    /**
     * Return whether this item is repairable in an anvil.
     */
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack)
    {
    	EnumToolMaterial tm = getToolMaterialFromItemStack(par1ItemStack);
        if (tm != null)
        	return tm.getToolCraftingMaterial() == par2ItemStack.itemID ? true : super.getIsRepairable(par1ItemStack, par2ItemStack);
        return this.toolMaterial.getToolCraftingMaterial() == par2ItemStack.itemID ? true : super.getIsRepairable(par1ItemStack, par2ItemStack);
    }

    @Override
    public float getStrVsBlock(ItemStack stack, Block block, int meta) 
    {
        if (ForgeHooks.isToolEffective(stack, block, meta))
        {
        	EnumToolMaterial tm = getToolMaterialFromItemStack(stack);
            if (tm != null)
            	return tm.getEfficiencyOnProperMaterial();
        }
        return getStrVsBlock(stack, block);
    }
    
    @SideOnly(Side.CLIENT)

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (EnumToolMaterial tm : materials)
        {
        	par3List.add(this.setToolMaterial(new ItemStack(this), tm));
        }
    }

	@Override
	public int getGlintColor(ItemStack par1ItemStack)
	{
		return -1;
	}
}