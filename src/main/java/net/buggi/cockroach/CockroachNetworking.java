package net.buggi.cockroach;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

public final class CockroachNetworking {
	public static final Identifier EAT_DIAMOND = CockroachMod.id("eat_diamond");

	private CockroachNetworking() {
	}

	public static void registerServerReceiver() {
		ServerPlayNetworking.registerGlobalReceiver(EAT_DIAMOND, (server, player, handler, buf, responseSender) -> {
			int slotId = buf.readInt();
			server.execute(() -> {
				ScreenHandler screenHandler = player.currentScreenHandler;
				if (screenHandler == null || slotId < 0 || slotId >= screenHandler.slots.size()) {
					return;
				}
				Slot slot = screenHandler.slots.get(slotId);
				ItemStack stack = slot.getStack();
				if (stack.isOf(Items.DIAMOND) && !stack.isEmpty()) {
					stack.decrement(1);
					if (stack.isEmpty()) {
						slot.setStack(ItemStack.EMPTY);
					}
					slot.markDirty();
					screenHandler.sendContentUpdates();
				}
			});
		});
	}
}
