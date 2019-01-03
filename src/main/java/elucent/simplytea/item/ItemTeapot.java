package elucent.simplytea.item;

import java.util.List;

import elucent.simplytea.core.Config;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class ItemTeapot extends ItemBase {
	public ItemTeapot(String name, boolean addToTab) {
		super(name, addToTab);
		setMaxStackSize(1);
		this.hasSubtypes = true;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		if(stack.getMetadata() == 0) {
			RayTraceResult raytraceresult = this.rayTrace(worldIn, playerIn, true);
			if(raytraceresult != null && raytraceresult.typeOfHit != null && raytraceresult.typeOfHit == Type.BLOCK) {
				IBlockState state = worldIn.getBlockState(raytraceresult.getBlockPos());
				// if consuming source, must be a source
				if(state.getBlock() == Blocks.WATER) {
					stack.setItemDamage(1);
					playerIn.setHeldItem(handIn, stack);
					if(Config.teapot_consume_source) {
						worldIn.setBlockToAir(raytraceresult.getBlockPos());
					}
					return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
				}
				else if (state.getBlock() == Blocks.CAULDRON && state.getValue(BlockCauldron.LEVEL) == 3) {
					stack.setItemDamage(1);
					playerIn.setHeldItem(handIn, stack);
					Blocks.CAULDRON.setWaterLevel(worldIn, raytraceresult.getBlockPos(), state, 0);
					return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
				}
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
	}

	@Override
	public void initModel() {
		String name = getRegistryName().getResourcePath();
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(this, 1, new ModelResourceLocation(new ResourceLocation(SimplyTea.MODID, name + "_water"), "inventory"));
	}

	@Override
	public void addInformation(ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced) {
		if(stack.getMetadata() == 0) {
			tooltip.add(I18n.format("simplytea.tooltip.empty"));
		}
		if(stack.getMetadata() == 1) {
			tooltip.add(I18n.format("simplytea.tooltip.water"));
		}
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> stacks) {
		if(this.isInCreativeTab(tab)) {
			stacks.add(new ItemStack(this, 1));
			stacks.add(new ItemStack(this, 1, 1));
		}
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		if (stack.getItemDamage() == 0) {
			return new TeapotFluidHandler(stack);
		}
		return null;
	}
}