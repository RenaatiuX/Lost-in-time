package com.rena.lost.client.model.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class SpearModel extends Model {

    private final ModelRenderer body;
    private final ModelRenderer cube_r1;
    private final ModelRenderer cube_r2;

    public SpearModel() {
        super(RenderType::getEntitySolid);

        textureWidth = 64;
        textureHeight = 64;

        body = new ModelRenderer(this);
        body.setRotationPoint(0.0F, 24.0F, 0.0F);


        cube_r1 = new ModelRenderer(this);
        cube_r1.setRotationPoint(0.0F, -23.0F, -1.4F);
        body.addChild(cube_r1);
        setRotationAngle(cube_r1, -1.5708F, 0.0F, 0.0F);
        cube_r1.setTextureOffset(0, 35).addBox(-0.5F, -2.0F, -5.1F, 1.0F, 1.0F, 28.0F, 0.0F, false);
        cube_r1.setTextureOffset(0, 6).addBox(0.0F, -6.0F, -15.1F, 0.0F, 9.0F, 10.0F, 0.0F, false);

        cube_r2 = new ModelRenderer(this);
        cube_r2.setRotationPoint(0.0F, -23.0F, -1.4F);
        body.addChild(cube_r2);
        setRotationAngle(cube_r2, 0.0F, -1.5708F, -1.5708F);
        cube_r2.setTextureOffset(0, 6).addBox(1.5F, -4.5F, -15.1F, 0.0F, 9.0F, 10.0F, 0.0F, false);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        body.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
