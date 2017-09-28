package rafradek.TF2weapons;

import cpw.mods.fml.client.config.GuiConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;

public class TF2GuiConfig extends GuiConfig {

	public TF2GuiConfig(GuiScreen parentScreen) {
		super(parentScreen,
                new ConfigElement(TF2weapons.conf.getCategory("gameplay")).getChildElements(),
                "rafradek_tf2_weapons", false, false, "TF2 Stuff Configuration");
		
	}

}
