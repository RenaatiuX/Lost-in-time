package com.rena.lost.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.rena.lost.LostInTime;
import com.rena.lost.client.model.item.SpearModel;
import com.rena.lost.common.entity.misc.SpearEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class SpearRenderer <T extends SpearEntity> extends EntityRenderer<T> {

    private final SpearModel spearModel = new SpearModel();

    public SpearRenderer(EntityRendererManager p_i46179_1_) {
        super(p_i46179_1_);
    }

    @Override
    public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        matrixStackIn.push();
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.prevRotationYaw, entityIn.rotationYaw) - 90.0F));
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTicks, entityIn.prevRotationPitch, entityIn.rotationPitch) + 90.0F));
        IVertexBuilder ivertexbuilder = ItemRenderer.getEntityGlintVertexBuilder(bufferIn, RenderType.getEntityCutout(getEntityTexture(entityIn)), false, entityIn.isEnchanted());
        this.spearModel.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStackIn.pop();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getEntityTexture(T entity) {
        return entity.getTexture();
    }

    public static class SpearItemStackRenderer extends ItemStackTileEntityRenderer {
        private final SpearModel spearModel = new SpearModel();
        final ResourceLocation texture;

        public SpearItemStackRenderer(final String name) {
            texture = new ResourceLocation(LostInTime.MOD_ID, "textures/entity/spear/" + name + ".png");
        }

        @Override
        public void func_239207_a_(ItemStack itemStack, ItemCameraTransforms.TransformType transform, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
                matrixStack.push();
                matrixStack.scale(-1.0F, -1.0F, 1.0F);
                IVertexBuilder vertexBuilder = ItemRenderer.getEntityGlintVertexBuilder(buffer, RenderType.getEntityCutout(texture), false, itemStack.hasEffect());
                spearModel.render(matrixStack, vertexBuilder, combinedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                matrixStack.pop();
        }
    }
}
