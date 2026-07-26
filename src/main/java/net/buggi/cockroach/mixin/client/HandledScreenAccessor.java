package net.buggi.cockroach.mixin.client;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HandledScreen.class)
public interface HandledScreenAccessor {
	@Accessor("x")
	int cockroach$getX();

	@Accessor("y")
	int cockroach$getY();

	@Accessor("backgroundWidth")
	int cockroach$getBackgroundWidth();

	@Accessor("backgroundHeight")
	int cockroach$getBackgroundHeight();
}
