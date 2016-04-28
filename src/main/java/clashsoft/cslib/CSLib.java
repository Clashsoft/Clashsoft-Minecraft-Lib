package clashsoft.cslib;

import java.io.File;

import net.minecraft.block.Block;
import net.minecraft.command.ICommand;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.InitMapGenEvent.EventType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import clashsoft.cslib.block.CSBlocks;
import clashsoft.cslib.block.ore.BlockOre2;
import clashsoft.cslib.block.ore.BlockRedstoneOre2;
import clashsoft.cslib.block.ore.OreBase;
import clashsoft.cslib.cape.Capes;
import clashsoft.cslib.command.CSCommand;
import clashsoft.cslib.command.CommandModUpdate;
import clashsoft.cslib.common.CSLibProxy;
import clashsoft.cslib.config.CSConfig;
import clashsoft.cslib.crafting.CSCrafting;
import clashsoft.cslib.crafting.loader.FurnaceRecipeLoader;
import clashsoft.cslib.entity.CSEntities;
import clashsoft.cslib.init.ClashsoftMod;
import clashsoft.cslib.item.block.ItemCustomBlock;
import clashsoft.cslib.logging.CSLog;
import clashsoft.cslib.network.CSLibNetHandler;
import clashsoft.cslib.update.CSUpdate;
import clashsoft.cslib.update.reader.SimpleUpdateReader;
import clashsoft.cslib.update.updater.ModUpdater;
import clashsoft.cslib.util.Log4JLogger;
import clashsoft.cslib.world.gen.CustomCaveGen;

@Mod(modid = CSLib.MODID, name = CSLib.NAME, version = CSLib.VERSION)
public class CSLib extends ClashsoftMod
{
	public static final String	MODID			= "cslib";
	public static final String	NAME			= "Clashsoft Lib";
	public static final String	ACRONYM			= "cslib";
	public static final String	VERSION			= "1.7.10-2.7.3";
	public static final String	DEPENDENCY		= "required-after:" + MODID;
	
	public static boolean		workspaceMode;
	
	static
	{
		CSLog.logger = new Log4JLogger();
		
		File file = new File(".");
		String path = file.getAbsolutePath();
		if (path.endsWith("run/.") || path.endsWith("assets/."))
		{
			workspaceMode = true;
			CSLog.info("Clashsoft Lib is now running in Dev Workspace mode.");
		}
	}
	
	@Instance(MODID)
	public static CSLib			instance;
	
	public static CSLibProxy	proxy			= createProxy("clashsoft.cslib.minecraft.client.CSLibClientProxy", "clashsoft.cslib.minecraft.common.CSLibProxy");
	
	public static Block			coalOre2		= new BlockOre2(OreBase.TYPE_OVERWORLD).setHardness(1.5F).setResistance(2.5F).setStepSound(Block.soundTypePiston);
	public static Block			ironOre2		= new BlockOre2(OreBase.TYPE_OVERWORLD).setHardness(1.5F).setResistance(2.5F).setStepSound(Block.soundTypePiston);
	public static Block			goldOre2		= new BlockOre2(OreBase.TYPE_OVERWORLD).setHardness(1.5F).setResistance(2.5F).setStepSound(Block.soundTypePiston);
	public static Block			diamondOre2		= new BlockOre2(OreBase.TYPE_OVERWORLD).setHardness(1.5F).setResistance(2.5F).setStepSound(Block.soundTypePiston);
	public static Block			emeraldOre2		= new BlockOre2(OreBase.TYPE_OVERWORLD).setHardness(1.5F).setResistance(2.5F).setStepSound(Block.soundTypePiston);
	public static Block			redstoneOre2	= new BlockRedstoneOre2(OreBase.TYPE_OVERWORLD, false).setHardness(1.5F).setResistance(2.5F).setStepSound(Block.soundTypePiston);
	public static Block			litRedstoneOre2	= new BlockRedstoneOre2(OreBase.TYPE_OVERWORLD, true).setLightLevel(0.625F).setHardness(1.5F).setResistance(2.5F).setStepSound(Block.soundTypePiston);
	public static Block			lapisOre2		= new BlockOre2(OreBase.TYPE_OVERWORLD).setHardness(1.5F).setResistance(2.5F).setStepSound(Block.soundTypePiston);
	public static Block			quartzOre2		= new BlockOre2(OreBase.TYPE_NETHER).setHardness(1.5F).setResistance(2.5F).setStepSound(Block.soundTypePiston);
	
	public static boolean		printUpdateNotes;
	public static boolean		updateCheck;
	public static boolean		autoUpdate;
	public static boolean		enableMOTD;
	
	public CSLib()
	{
		super(proxy, MODID, NAME, ACRONYM, VERSION);
		this.hasConfig = true;
		this.netHandler = new CSLibNetHandler();
		this.eventHandler = this;
		this.url = "https://github.com/Clashsoft/CSLib-Minecraft/wiki/";
		this.description = "Clashsoft's Minecraft Library adds many useful Classes and APIs for modders to use.";
	}
	
	public static CSLibNetHandler getNetHandler()
	{
		return (CSLibNetHandler) instance.netHandler;
	}
	
	@Override
	public void readConfig()
	{
		printUpdateNotes = CSConfig.getBool("updates", "Print Update Notes", false);
		updateCheck = CSConfig.getBool("updates", "Update Check", "Disables update checks for ALL mods", true);
		if (!workspaceMode)
		{
			autoUpdate = CSConfig.getBool("updates", "Automatically Update", "Disables automatic updates", false);
		}
		enableMOTD = CSConfig.getBool("updates", "Enable MOTD", true);
	}
	
	@Override
	public void updateCheck()
	{
		final String url = "https://raw.githubusercontent.com/Clashsoft/Clashsoft-Lib/master/version.txt";
		CSUpdate.updateCheck(new ModUpdater(NAME, ACRONYM, VERSION, url, SimpleUpdateReader.instance));
	}
	
	@Override
	@EventHandler
	public void construct(FMLConstructionEvent event)
	{
		super.construct(event);
		
		CSCrafting.updateItemStacks();
		
		CSBlocks.replaceBlock(Blocks.coal_ore, coalOre2, new ItemCustomBlock(coalOre2));
		CSBlocks.replaceBlock(Blocks.iron_ore, ironOre2, new ItemCustomBlock(ironOre2));
		CSBlocks.replaceBlock(Blocks.gold_ore, goldOre2, new ItemCustomBlock(goldOre2));
		CSBlocks.replaceBlock(Blocks.diamond_ore, diamondOre2, new ItemCustomBlock(diamondOre2));
		CSBlocks.replaceBlock(Blocks.emerald_ore, emeraldOre2, new ItemCustomBlock(emeraldOre2));
		CSBlocks.replaceBlock(Blocks.redstone_ore, redstoneOre2, new ItemCustomBlock(redstoneOre2));
		CSBlocks.replaceBlock(Blocks.lit_redstone_ore, litRedstoneOre2);
		CSBlocks.replaceBlock(Blocks.lapis_ore, lapisOre2, new ItemCustomBlock(lapisOre2));
		CSBlocks.replaceBlock(Blocks.quartz_ore, quartzOre2, new ItemCustomBlock(quartzOre2));
	}
	
	@Override
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		super.preInit(event);
	}
	
	@Override
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		super.init(event);
		
		FurnaceRecipeLoader.instance.load();
	}
	
	@Override
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		super.postInit(event);
	}
	
	@EventHandler
	public void serverStarted(FMLServerStartedEvent event)
	{
		ServerCommandManager manager = (ServerCommandManager) MinecraftServer.getServer().getCommandManager();
		if (CSCommand.commands != null)
		{
			for (ICommand cmd : CSCommand.commands)
			{
				manager.registerCommand(cmd);
			}
		}
		
		manager.registerCommand(new CommandModUpdate());
	}
	
	@SubscribeEvent
	public void entityJoined(EntityJoinWorldEvent event)
	{
		if (event.entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) event.entity;
			Capes.updateCape(player);
		}
	}
	
	@SubscribeEvent
	public void entityConstructing(EntityConstructing event)
	{
		CSEntities.loadProperties(event.entity);
	}
	
	@SubscribeEvent
	public void playerLogin(PlayerLoggedInEvent event)
	{
		if (proxy.isClient())
		{
			CSUpdate.notifyAll(event.player);
		}
	}
	
	@SubscribeEvent
	public void getModdedMapGen(InitMapGenEvent event)
	{
		if (event.type == EventType.CAVE)
		{
			event.newGen = new CustomCaveGen();
		}
	}
}