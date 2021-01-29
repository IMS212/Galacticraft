/*
 * Copyright (c) 2019 HRZN LTD
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.hrznstudio.galacticraft.client.gui.screen.ingame;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.rocket.RocketData;
import com.hrznstudio.galacticraft.api.rocket.part.RocketPartType;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.block.entity.RocketAssemblerBlockEntity;
import com.hrznstudio.galacticraft.entity.rocket.RocketEntity;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import com.hrznstudio.galacticraft.recipe.RocketAssemblerRecipe;
import com.hrznstudio.galacticraft.screen.RocketAssemblerScreenHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Quaternion;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Environment(EnvType.CLIENT)
public class RocketAssemblerScreen extends HandledScreen<RocketAssemblerScreenHandler> {

    public static final int SELECTED_TAB_X = 324;
    public static final int SELECTED_TAB_Y = 4;
    public static final int SELECTED_TAB_WIDTH = 32;
    public static final int SELECTED_TAB_HEIGHT = 25;

    public static final int TAB_X = 325;
    public static final int TAB_Y = 30;
    public static final int TAB_WIDTH = 28;
    public static final int TAB_HEIGHT = 25;

    public static final int RED_BOX_X = 324;
    public static final int RED_BOX_Y = 56;
    public static final int RED_BOX_WIDTH = 24;
    public static final int RED_BOX_HEIGHT = 24;

    public static final int GREEN_BOX_X = 324;
    public static final int GREEN_BOX_Y = 81;
    public static final int GREEN_BOX_WIDTH = 24;
    public static final int GREEN_BOX_HEIGHT = 24;

    public static final int ARROW_X = 324;
    public static final int ARROW_Y = 106;
    public static final int ARROW_WIDTH = 6;
    public static final int ARROW_HEIGHT = 10;

    public static final int SELECTED_ARROW_X = 324;
    public static final int SELECTED_ARROW_Y = 117;
    public static final int SELECTED_ARROW_WIDTH = 6;
    public static final int SELECTED_ARROW_HEIGHT = 10;

    public static final int BACK_ARROW_X = ARROW_X + ARROW_WIDTH;
    public static final int BACK_ARROW_Y = ARROW_Y + ARROW_HEIGHT;
    public static final int BACK_ARROW_WIDTH = -ARROW_WIDTH;
    public static final int BACK_ARROW_HEIGHT = -ARROW_HEIGHT;

    public static final int BACK_SELECTED_ARROW_X = SELECTED_ARROW_X + ARROW_WIDTH;
    public static final int BACK_SELECTED_ARROW_Y = SELECTED_ARROW_Y + ARROW_HEIGHT;
    public static final int BACK_SELECTED_ARROW_WIDTH = -SELECTED_ARROW_WIDTH;
    public static final int BACK_SELECTED_ARROW_HEIGHT = -SELECTED_ARROW_HEIGHT;

    public static final int BACK_ARROW_OFFSET_X = ARROW_WIDTH;
    public static final int BACK_ARROW_OFFSET_Y = ARROW_HEIGHT;

    public static final int ENERGY_OVERLAY_WIDTH = -12;
    public static final int ENERGY_OVERLAY_HEIGHT = -60;
    public static final int ENERGY_OVERLAY_X = 336;
    public static final int ENERGY_OVERLAY_Y = 188;
    public static final int ENERGY_OVERLAY_RENDER_X = 169;
    public static final int ENERGY_OVERLAY_RENDER_Y = 67;

    public static final int BUILD_X = 338;
    public static final int BUILD_Y = 129;
    public static final int BUILD_WIDTH = 35;
    public static final int BUILD_HEIGHT = 16;

    public static final int PROGRESS_ARROW_WIDTH = 133;
    public static final int PROGRESS_ARROW_WIDTH_MAX = 136;
    public static final int PROGRESS_ARROW_HEIGHT = 4;
    public static final int PROGRESS_ARROW_X = 364;
    public static final int PROGRESS_ARROW_Y = 8;

    protected final Identifier TEXTURE = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.ROCKET_ASSEMBLER_SCREEN));
    private final RocketAssemblerBlockEntity blockEntity;
    private Tab tab = Tab.ROCKET;

    public RocketAssemblerScreen(RocketAssemblerScreenHandler handler, PlayerInventory inv, Text title) {
        super(handler, inv, title);
        this.backgroundWidth = 323;
        this.backgroundHeight = 175;
        this.blockEntity = handler.assembler;
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    protected void drawBackground(MatrixStack matrix, float delta, int mouseX, int mouseY) {
        this.renderBackground(matrix);
        DiffuseLighting.enableGuiDepthLighting();
        this.client.getTextureManager().bindTexture(TEXTURE);
        drawTexture(matrix, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        drawTexture(matrix, this.x + ENERGY_OVERLAY_RENDER_X, this.y + ENERGY_OVERLAY_RENDER_Y, ENERGY_OVERLAY_X, ENERGY_OVERLAY_Y, ENERGY_OVERLAY_WIDTH, (int) (((float) ENERGY_OVERLAY_HEIGHT) * (((float) this.blockEntity.getEnergyAttribute().getCurrentEnergy() / (float) this.blockEntity.getEnergyAttribute().getMaxEnergy()))));

        if (blockEntity.ready() && !blockEntity.building()) {
            drawTexture(matrix, this.x + 257, this.y + 18, BUILD_X, BUILD_Y, BUILD_WIDTH, BUILD_HEIGHT);
        }

        if (tab == Tab.ROCKET) {
            drawTexture(matrix, this.x - 29, this.y + 3, SELECTED_TAB_X, SELECTED_TAB_Y, SELECTED_TAB_WIDTH, SELECTED_TAB_HEIGHT);
            drawTexture(matrix, this.x - 27, this.y + 30, TAB_X, TAB_Y, TAB_WIDTH, TAB_HEIGHT);

            itemRenderer.renderGuiItemIcon(new ItemStack(GalacticraftItems.ROCKET_SCHEMATIC), this.x - 20, this.y + 8);
            itemRenderer.renderGuiItemIcon(new ItemStack(GalacticraftBlocks.MOON_TURF), this.x - 20, this.y + 35);

            if (!this.blockEntity.data.isEmpty()) {
                drawEntity(this.x + 186 + 17, this.y + 73, this.blockEntity.fakeEntity);
            }
        } else if (tab == Tab.LANDER) {
            drawTexture(matrix, this.x - 27, this.y + 3, TAB_X, TAB_Y, TAB_WIDTH, TAB_HEIGHT);
            drawTexture(matrix, this.x - 29, this.y + 30, SELECTED_TAB_X, SELECTED_TAB_Y, SELECTED_TAB_WIDTH, SELECTED_TAB_HEIGHT);

            itemRenderer.renderGuiItemIcon(new ItemStack(GalacticraftItems.ROCKET_SCHEMATIC), this.x - 20, this.y + 8);
            itemRenderer.renderGuiItemIcon(new ItemStack(GalacticraftBlocks.MOON_TURF), this.x - 20, this.y + 35);
        }

        if (blockEntity.building()) {
            float progress = blockEntity.getProgress();
            this.client.getTextureManager().bindTexture(TEXTURE);//OUT OF 600 //133 / 140
            final float maxProgress = Galacticraft.configManager.get().rocketAssemblerProcessTime();
            if (progress < ((maxProgress / 140F) * 133F)) {
                drawTexture(matrix, this.x + 176, this.y + 7, PROGRESS_ARROW_X, PROGRESS_ARROW_Y, (int) (((float) PROGRESS_ARROW_WIDTH) * (progress / ((maxProgress / 140F) * 133F))), PROGRESS_ARROW_HEIGHT);
            } else {
                drawTexture(matrix, this.x + 176, this.y + 7, PROGRESS_ARROW_X, PROGRESS_ARROW_Y, PROGRESS_ARROW_WIDTH_MAX, (int) ((PROGRESS_ARROW_HEIGHT) + (7 * ((progress - ((maxProgress / 140F) * 133F)) / (maxProgress - ((maxProgress / 140F) * 133F))))));
            }
        }

        if (tab == Tab.ROCKET) {
            int offsetY = 0;
            int offsetX = 0;
            int slot = 0;
            if (this.blockEntity.data != RocketData.EMPTY) {
                for (int i = 0; i < RocketPartType.values().length; i++) {
                    if (blockEntity.data.getPartForType(RocketPartType.values()[i]).hasRecipe()) {
                        if (offsetX != 0) {
                            offsetY++;
                            offsetX = 0;
                        }
                        this.client.getTextureManager().bindTexture(TEXTURE);
                        final int baOY = offsetY;
                        boolean aG = true;
                        offsetX++;

                        RocketAssemblerRecipe recipe = blockEntity.recipes.get(Galacticraft.ROCKET_PARTS.getId(blockEntity.data.getPartForType(RocketPartType.values()[i])));
                        for (ItemStack stack : recipe.getInput()) {
                            this.client.getTextureManager().bindTexture(TEXTURE);

                            if (this.blockEntity.getExtendedInventory().getStack(slot).getCount() == stack.getCount()) {
                                drawTexture(matrix, this.x + 9 + ((GREEN_BOX_WIDTH + 2) * offsetX), this.y + 9 + ((GREEN_BOX_HEIGHT + 2) * offsetY), GREEN_BOX_X, GREEN_BOX_Y, GREEN_BOX_WIDTH, GREEN_BOX_HEIGHT);
                            } else {
                                drawTexture(matrix, this.x + 9 + ((RED_BOX_WIDTH + 2) * offsetX), this.y + 9 + ((RED_BOX_HEIGHT + 2) * offsetY), RED_BOX_X, RED_BOX_Y, RED_BOX_WIDTH, RED_BOX_HEIGHT);
                                aG = false;
                            }

                            itemRenderer.renderGuiItemIcon(stack, this.x + 13 + ((GREEN_BOX_WIDTH + 2) * offsetX), this.y + 13 + ((GREEN_BOX_HEIGHT + 2) * offsetY));
                            itemRenderer.renderGuiItemOverlay(client.textRenderer, stack, this.x + 13 + (GREEN_BOX_WIDTH + 2) * offsetX, this.y + 13 + (GREEN_BOX_HEIGHT + 2) * offsetY, this.blockEntity.getExtendedInventory().getStack(slot).getCount() + "/" + stack.getCount());

                            if (check(mouseX, mouseY, (this.x + 9 + ((GREEN_BOX_WIDTH) + 2) * offsetX) + 2, (this.y + 9 + ((GREEN_BOX_HEIGHT + 2) * offsetY)) + 2, GREEN_BOX_WIDTH - 4, GREEN_BOX_HEIGHT - 4)) {
                                RenderSystem.disableDepthTest();
                                int n = (this.x + 9 + ((GREEN_BOX_WIDTH) + 2) * offsetX) + 2;
                                int r = (this.y + 9 + ((GREEN_BOX_HEIGHT + 2) * offsetY)) + 2;
                                RenderSystem.colorMask(true, true, true, false);
                                this.fillGradient(matrix, n, r, n + GREEN_BOX_WIDTH - 4, r + GREEN_BOX_HEIGHT - 4, -2130706433, -2130706433);
                                RenderSystem.colorMask(true, true, true, true);
                                RenderSystem.enableDepthTest();
                            }
                            if (++offsetX == 5) {
                                offsetX = 0;
                                offsetY++;
                            }
                            slot++;
                        }

                        this.client.getTextureManager().bindTexture(TEXTURE);
                        if (aG) {
                            drawTexture(matrix, this.x + 9, this.y + 9 + ((GREEN_BOX_HEIGHT + 2) * baOY), GREEN_BOX_X, GREEN_BOX_Y, GREEN_BOX_WIDTH, GREEN_BOX_HEIGHT);
                        } else {
                            drawTexture(matrix, this.x + 9, this.y + 9 + ((RED_BOX_HEIGHT + 2) * baOY), RED_BOX_X, RED_BOX_Y, RED_BOX_WIDTH, RED_BOX_HEIGHT);
                        }
                        itemRenderer.renderGuiItemIcon(blockEntity.data.getPartForType(RocketPartType.values()[i]).getRenderStack(), this.x + 13, this.y + 13 + ((GREEN_BOX_HEIGHT + 2) * baOY));

                    }
                }
            }
        } else if (tab == Tab.LANDER) {
            drawCenteredString(matrix, client.textRenderer, "WIP - TO BE DESIGNED", this.x / 2, this.y + (height) / 2, Integer.MAX_VALUE);
        }
    }


    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
        super.render(stack, mouseX, mouseY, delta);
        DiffuseLighting.enableGuiDepthLighting();

        if (blockEntity.data != null && blockEntity.data != RocketData.EMPTY) {
            client.textRenderer.draw(stack, new TranslatableText("tooltip.galacticraft-rewoven.rocket_info").asString(), this.x + 234, this.y + 41, 11184810);
            client.textRenderer.draw(stack, new TranslatableText("tooltip.galacticraft-rewoven.tier", blockEntity.data.getTier()).asString(), this.x + 234, this.y + 41 + 11, 11184810);
            client.textRenderer.draw(stack, new TranslatableText("tooltip.galacticraft-rewoven.assembler_status").asString(), this.x + 234, this.y + 41 + 22, 11184810);
            client.textRenderer.draw(stack, getStatus(), this.x + 234, this.y + 41 + 33, 11184810);
        } else {
            client.textRenderer.draw(stack, new TranslatableText("tooltip.galacticraft-rewoven.put_schematic").asString(), this.x + 234, this.y + 41, 11184810);
            client.textRenderer.draw(stack, new TranslatableText("tooltip.galacticraft-rewoven.put_schematic_2").asString(), this.x + 234, this.y + 41 + 11, 11184810);
            client.textRenderer.draw(stack, new TranslatableText("tooltip.galacticraft-rewoven.assembler_status").asString(), this.x + 234, this.y + 41 + 22, 11184810);
            client.textRenderer.draw(stack, getStatus(), this.x + 234, this.y + 41 + 33, 11184810);
        }

        this.drawMouseoverTooltip(stack, mouseX, mouseY);
    }

    private String getStatus() {
        if (blockEntity.building()) {
            return new TranslatableText("tooltip.galacticraft-rewoven.building").asString();
        } else if (blockEntity.ready()) {
            if (blockEntity.getEnergyAttribute().getCurrentEnergy() > 20) {
                return new TranslatableText("tooltip.galacticraft-rewoven.ready").asString();
            } else {
                return new TranslatableText("tooltip.galacticraft-rewoven.no_energy").asString();
            }
        } else if (this.blockEntity.data == null || this.blockEntity.data.isEmpty()) {
            return new TranslatableText("tooltip.galacticraft-rewoven.no_schematic").asString();
        } else {
            return new TranslatableText("tooltip.galacticraft-rewoven.missing_resources").asString();
        }
    }


    public static void drawEntity(int x, int y, RocketEntity entity) {
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float)x, (float)y, 1050.0F);
        RenderSystem.scalef(3.0F, 3.0F, -3.0F);
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(0.0D, 0.0D, 1000.0D);
        Quaternion quaternion = Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
        Quaternion quaternion2 = Vector3f.POSITIVE_X.getDegreesQuaternion(0.0F);
        quaternion.hamiltonProduct(quaternion2);
        matrixStack.multiply(quaternion);
        entity.yaw = 180.0F;
        entity.pitch = -20.0F;
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        quaternion2.conjugate();
        entityRenderDispatcher.setRotation(quaternion2);
        entityRenderDispatcher.setRenderShadows(false);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        RenderSystem.runAsFancy(() -> {
            entityRenderDispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixStack, immediate, 15728880);
        });
        immediate.draw();
        entityRenderDispatcher.setRenderShadows(true);
        RenderSystem.popMatrix();
    }

    @Override
    protected void drawMouseoverTooltip(MatrixStack matrixStack, int i, int j) {
        super.drawMouseoverTooltip(matrixStack, i, j);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button) | tabClicked(mouseX, mouseY, button) | contentClicked(mouseX, mouseY, button);
    }

    private boolean contentClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && tab == Tab.ROCKET) {
            int offsetX = 0;
            int offsetY = 0;
            int slot = 0;
            if (this.blockEntity.data != RocketData.EMPTY) {
                for (int i = 0; i < RocketPartType.values().length; i++) {
                    if (blockEntity.data.getPartForType(RocketPartType.values()[i]).hasRecipe()) {
                        if (offsetX != 0) {
                            offsetY++;
                        }
                        offsetX = 1;
                        RocketAssemblerRecipe recipe = blockEntity.recipes.get(Galacticraft.ROCKET_PARTS.getId(blockEntity.data.getPartForType(RocketPartType.values()[i])));
                        DefaultedList<ItemStack> input = recipe.getInput();
                        for (int i1 = 0; i1 < input.size(); i1++) {
                            if (check(mouseX, mouseY, this.x + 9 + ((GREEN_BOX_WIDTH + 2) * offsetX), this.y + 9 + ((GREEN_BOX_HEIGHT + 2) * offsetY), GREEN_BOX_WIDTH, GREEN_BOX_HEIGHT)) {
                                boolean success = false;
                                if (slot < blockEntity.getExtendedInventory().getSize()) {
                                    if (playerInventory.getCursorStack().isEmpty()) {
                                        success = true;
                                        playerInventory.setCursorStack(blockEntity.getExtendedInventory().getStack(slot));
                                        blockEntity.getExtendedInventory().setStack(slot, ItemStack.EMPTY);
                                    } else {
                                        if (blockEntity.getExtendedInventory().isAcceptableStack(slot, playerInventory.getCursorStack().copy())) {
                                            if (blockEntity.getExtendedInventory().getStack(slot).isEmpty()) {
                                                if (blockEntity.getExtendedInventory().getMaxStackSize(slot) >= playerInventory.getCursorStack().getCount()) {
                                                    blockEntity.getExtendedInventory().setStack(slot, playerInventory.getCursorStack().copy());
                                                    playerInventory.setCursorStack(ItemStack.EMPTY);
                                                } else {
                                                    ItemStack stack = playerInventory.getCursorStack().copy();
                                                    ItemStack stack1 = playerInventory.getCursorStack().copy();
                                                    stack.setCount(blockEntity.getExtendedInventory().getMaxStackSize(slot));
                                                    stack1.setCount(stack1.getCount() - blockEntity.getExtendedInventory().getMaxStackSize(slot));
                                                    blockEntity.getExtendedInventory().setStack(slot, stack);
                                                    playerInventory.setCursorStack(stack1);
                                                }
                                            } else { // IMPOSSIBLE FOR THE 2 STACKS TO BE DIFFERENT AS OF RIGHT NOW. THIS MAY CHANGE.
                                                // SO... IF IT DOES, YOU NEED TO UPDATE THIS.
                                                ItemStack stack = playerInventory.getCursorStack().copy();
                                                int max = blockEntity.getExtendedInventory().getMaxStackSize(slot);
                                                stack.setCount(stack.getCount() + blockEntity.getExtendedInventory().getStack(slot).getCount());
                                                if (stack.getCount() <= max) {
                                                    playerInventory.setCursorStack(ItemStack.EMPTY);
                                                } else {
                                                    ItemStack stack1 = stack.copy();
                                                    stack.setCount(max);
                                                    stack1.setCount(stack1.getCount() - max);
                                                    playerInventory.setCursorStack(stack1);
                                                }
                                                blockEntity.getExtendedInventory().setStack(slot, stack);
                                            }
                                            success = true;
                                        }
                                    }
                                }

                                if (success) {
                                    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer().writeInt(slot)).writeBlockPos(this.blockEntity.getPos());
                                    this.client.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "assembler_wc"), buf));
                                    return true;
                                }
                            }
                            slot++;
                            if (++offsetX == 5) {
                                offsetX = 0;
                                offsetY++;
                            }
                        }

                    }
                }
            }
        }

        if (button == 0) {
            if (check(mouseX, mouseY, this.x + 257, this.y + 18, BUILD_WIDTH, BUILD_HEIGHT)) {
                blockEntity.startBuilding();
                client.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "assembler_build"), new PacketByteBuf(Unpooled.buffer()).writeBlockPos(blockEntity.getPos())));
            }
        }
        return false;
    }

    public SlotActionType getType(int button, int slot) {
        if (this.client.player.inventory.getCursorStack().isEmpty()) {
            if (this.client.options.keyPickItem.matchesMouse(button)) {
                return SlotActionType.CLONE;
            } else {
                boolean ok = slot != -999 && (InputUtil.isKeyPressed(client.getWindow().getHandle(), 340) || InputUtil.isKeyPressed(client.getWindow().getHandle(), 344));
                if (ok) {
                    return SlotActionType.QUICK_MOVE;
                } else if (slot == -999) {
                    return SlotActionType.THROW;
                }
                return SlotActionType.PICKUP;
            }
        } else {
            return SlotActionType.SWAP;
        }
    }


    private boolean tabClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (tab == Tab.ROCKET) {
                if (check(mouseX, mouseY, this.x - 27, this.y + 30, TAB_WIDTH, TAB_HEIGHT)) {
                    tab = Tab.LANDER;
                }
            } else if (tab == Tab.LANDER) {
                if (check(mouseX, mouseY, this.x - 27, this.y + 3, TAB_WIDTH, TAB_HEIGHT)) {
                    tab = Tab.ROCKET;
                }
            } else {
                if (check(mouseX, mouseY, this.x - 27, this.y + 30, TAB_WIDTH, TAB_HEIGHT)) {
                    tab = Tab.LANDER;
                } else if (check(mouseX, mouseY, this.x - 27, this.y + 3, TAB_WIDTH, TAB_HEIGHT)) {
                    tab = Tab.ROCKET;
                }
            }
        }
        return false;
    }

    @Override
    public void drawTexture(MatrixStack stack, int x, int y, int u, int v, int width, int height) {
        drawTexture(stack, x, y, u, v, width, height, 512, 256);
    }

    private boolean check(double mouseX, double mouseY, int buttonX, int buttonY, int buttonWidth, int buttonHeight) {
        return mouseX >= buttonX && mouseY >= buttonY && mouseX <= buttonX + buttonWidth && mouseY <= buttonY + buttonHeight;
    }

    private enum Tab {
        ROCKET,
        LANDER
    }

}
