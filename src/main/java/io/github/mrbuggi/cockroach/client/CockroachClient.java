package io.github.mrbuggi.cockroach.client;

import io.github.mrbuggi.cockroach.CockroachNetworking;
import io.github.mrbuggi.cockroach.mixin.client.HandledScreenAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;

public class CockroachClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!(screen instanceof HandledScreen<?> handledScreen)) {
                return;
            }

            HandledScreenAccessor panel = (HandledScreenAccessor) handledScreen;

            ScreenEvents.afterRender(screen).register((s, context, mouseX, mouseY, tickDelta) -> {
                if (!shouldRoam(client, handledScreen)) {
                    return;
                }

                int panelX = panel.cockroach$getX();
                int panelY = panel.cockroach$getY();

                int targetSlotId = -1;
                double targetX = Double.NaN;
                double targetY = Double.NaN;
                double bestDistSq = Double.MAX_VALUE;
                double roachX = Cockroach.INSTANCE.getCenterX();
                double roachY = Cockroach.INSTANCE.getCenterY();

                for (Slot slot : handledScreen.getScreenHandler().slots) {
                    if (!slot.getStack().isOf(Items.DIAMOND)) {
                        continue;
                    }
                    double sx = panelX + slot.x + 8.0;
                    double sy = panelY + slot.y + 8.0;
                    double dx = sx - roachX;
                    double dy = sy - roachY;
                    double distSq = Double.isNaN(roachX) ? 0.0 : dx * dx + dy * dy;
                    if (distSq < bestDistSq) {
                        bestDistSq = distSq;
                        targetX = sx;
                        targetY = sy;
                        targetSlotId = slot.id;
                    }
                }

                boolean ate = Cockroach.INSTANCE.tickAndRender(
                        context,
                        panelX,
                        panelY,
                        panel.cockroach$getBackgroundWidth(),
                        panel.cockroach$getBackgroundHeight(),
                        mouseX, mouseY,
                        targetX, targetY);

                // Алмаз удаляет сервер. Если мода на сервере нет, пакет некому принять - не едим.
                if (ate && targetSlotId >= 0 && ClientPlayNetworking.canSend(CockroachNetworking.EAT_DIAMOND)) {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeInt(targetSlotId);
                    ClientPlayNetworking.send(CockroachNetworking.EAT_DIAMOND, buf);

                    client.getSoundManager().play(
                            PositionedSoundInstance.master(SoundEvents.ENTITY_GENERIC_EAT, 1.0F, 1.0F));
                }
            });
        });
    }

    private static boolean shouldRoam(MinecraftClient client, HandledScreen<?> screen) {
        if (screen instanceof CreativeInventoryScreen) {
            return false;
        }
        if (screen instanceof InventoryScreen) {
            return true;
        }
        // В чужой контейнер таракана приманивает гнилая плоть, лежащая в самом контейнере.
        PlayerInventory playerInventory = client.player != null ? client.player.getInventory() : null;
        for (Slot slot : screen.getScreenHandler().slots) {
            if (slot.inventory != playerInventory && slot.getStack().isOf(Items.ROTTEN_FLESH)) {
                return true;
            }
        }
        return false;
    }
}
