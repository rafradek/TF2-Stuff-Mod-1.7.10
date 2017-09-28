package rafradek.TF2weapons;

import net.minecraft.potion.Potion;

public class PotionStun extends Potion {

	public PotionStun(int id, boolean isBadEffectIn, int liquidColorIn) {
		super(id,isBadEffectIn, liquidColorIn);
		this.setIconIndex(3, 1);
	}
	
}
