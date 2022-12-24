package com.rena.lost.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rena.lost.LostInTime;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Map;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = LostInTime.MOD_ID, value = Dist.CLIENT)
public class LITModels {
    public static final String[] HAND_MODEL_ITEMS = new String[] {};

    @SubscribeEvent
    public static void onModelBakeEvent(ModelBakeEvent event) {
        Map<ResourceLocation, IBakedModel> map = event.getModelRegistry();

        for (String item : HAND_MODEL_ITEMS) {
            ResourceLocation modelInventory = new ModelResourceLocation("lost:" + item, "inventory");
            ResourceLocation modelHand = new ModelResourceLocation("lost:" + item + "_in_hand", "inventory");

            IBakedModel bakedModelDefault = map.get(modelInventory);
            IBakedModel bakedModelHand = map.get(modelHand);
            IBakedModel modelWrapper = new IBakedModel() {
                @Override
                public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
                    return bakedModelDefault.getQuads(state, side, rand);
                }

                @Override
                public boolean isAmbientOcclusion() {
                    return bakedModelDefault.isAmbientOcclusion();
                }

                @Override
                public boolean isGui3d() {
                    return bakedModelDefault.isGui3d();
                }

                @Override
                public boolean isSideLit() {
                    return false;
                }

                @Override
                public boolean isBuiltInRenderer() {
                    return bakedModelDefault.isBuiltInRenderer();
                }

                @Override
                public TextureAtlasSprite getParticleTexture() {
                    return bakedModelDefault.getParticleTexture();
                }

                @Override
                public ItemOverrideList getOverrides() {
                    return bakedModelDefault.getOverrides();
                }

                @Override
                public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat) {
                    IBakedModel modelToUse = bakedModelDefault;
                    if (cameraTransformType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND || cameraTransformType == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND
                            || cameraTransformType == ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND || cameraTransformType == ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND) {
                        modelToUse = bakedModelHand;
                    }
                    return ForgeHooksClient.handlePerspective(modelToUse, cameraTransformType, mat);
                }
            };
            map.put(modelInventory, modelWrapper);
        }
    }

    @SubscribeEvent
    public static void onRegisterModels(ModelRegistryEvent modelRegistryEvent) {
        for (String item : LITModels.HAND_MODEL_ITEMS) {
            ModelLoader.addSpecialModel(new ModelResourceLocation(LostInTime.MOD_ID + ":" + item + "_in_hand", "inventory"));
        }

    }
}
