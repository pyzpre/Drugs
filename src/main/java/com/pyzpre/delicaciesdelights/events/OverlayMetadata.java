package com.pyzpre.delicaciesdelights.events;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class OverlayMetadata {
    private final ResourceLocation folderPath;
    private final float alphaIncrement;
    private final float fovChange;
    private final boolean pulsate;
    private final float pulsateDuration;
    private final int frameDuration;
    private List<ResourceLocation> frames;

    public OverlayMetadata(ResourceLocation folderPath, float alphaIncrement, float fovChange, boolean pulsate, float pulsateDuration, int frameDuration) {
        this.folderPath = folderPath;
        this.alphaIncrement = alphaIncrement;
        this.fovChange = fovChange;
        this.pulsate = pulsate;
        this.pulsateDuration = pulsateDuration;
        this.frameDuration = frameDuration;
    }

    public List<ResourceLocation> getFrames() {
        if (frames == null) {
            loadFrames();
        }
        return frames;
    }

    private void loadFrames() {
        if (frames == null && Minecraft.getInstance().getResourceManager() != null) {
            frames = new ArrayList<>();
            int frameIndex = 1;
            while (true) {
                ResourceLocation frameLocation = new ResourceLocation(folderPath.getNamespace(), folderPath.getPath() + "/" + frameIndex + ".png");
                if (!resourceExists(frameLocation)) {
                    break;
                }
                frames.add(frameLocation);
                frameIndex++;
            }
            if (frames.isEmpty()) {
                throw new IllegalArgumentException("Overlay frames cannot be empty. Folder path: " + folderPath);
            }
        }
    }

    private boolean resourceExists(ResourceLocation location) {
        try {
            Minecraft.getInstance().getResourceManager().getResource(location).get();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public ResourceLocation getLocation() {
        return folderPath;
    }

    public float getAlphaIncrement() {
        return alphaIncrement;
    }

    public float getFovChange() {
        return fovChange;
    }

    public boolean isPulsate() {
        return pulsate;
    }

    public float getPulsateDuration() {
        return pulsateDuration;
    }

    public int getFrameDuration() {
        return frameDuration;
    }
}

