package com.rena.lost.client;

import com.rena.lost.client.render.SpearRenderer;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;

import java.util.HashMap;
import java.util.Map;

public class ClientISTERProvider {

    private static final Map<String, ItemStackTileEntityRenderer> spearMap = new HashMap<>();

    public static ItemStackTileEntityRenderer bakeSpearISTER(final String itemName) {
        if (!spearMap.containsKey(itemName)) {
            spearMap.put(itemName, new SpearRenderer.SpearItemStackRenderer(itemName));
        }
        return spearMap.get(itemName);
    }
}
