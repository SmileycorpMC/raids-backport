package net.smileycorp.raids.client.entity.model;

import com.google.common.collect.Lists;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import java.util.List;

public class ModelPillagerHat {

    private static final float scale = 1f / 16f;
    private static final float xzOffset = -5;
    private static final float yOffset = -11;

    private final List<TexturedQuad> quads = Lists.newArrayList();

    public ModelPillagerHat() {
        //top
        addPlane(0, 0, 0, 10, 0, 10, 10, 0, 20, 10);
        //right
        addPlane(0, 0, 0, 0, 5, 10, 10, 10, 0, 15);
        addQuad(new float[][]{{0, 5, 10}, {0, 5, 1}, {-1, 9, 1}, {-1, 9, 11}}, 11, 15, 0, 19);
        //front
        addPlane(0, 0, 0, 10, 7, 0, 10, 10, 20, 17);
        //left
        addPlane(10, 0, 0, 10, 5, 10, 20, 10, 30, 15);
        addQuad(new float[][]{{10, 5, 10}, {10, 5, 1}, {11, 9, 1}, {11, 9, 11}}, 21, 15, 30, 19);
        //back
        addPlane(10, 0, 10, 0, 5, 10, 30, 10, 40, 15);
        addQuad(new float[][]{{0, 5, 10}, {10, 5, 10}, {11, 9, 11}, {-1, 9, 11}}, 30, 15, 40, 19);
    }

    public void render() {
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        for (TexturedQuad quad : quads) quad.draw(buffer, 1);
    }

    private void addPlane(int x1, int y1, int z1, int x2, int y2, int z2, int u1, int v1, int u2, int v2) {
        int dx = x2 - x1;
        int dz = z2 - z1;
        if (dx != 0 && dz == 0) addQuad(new float[][]{{x2, y1, z2}, {x1, y1, z2}, {x1, y2, z1}, {x2, y2, z1}}, u1, v1, u2, v2);
        else if (dz != 0 && dx == 0) addQuad(new float[][]{{x1, y1, z2}, {x1, y1, z1}, {x2, y2, z1}, {x2, y2, z2}}, u1, v1, u2, v2);
        else {
            float dy = y1 + (float)(y2 - y1) * 0.5f;
            addQuad(new float[][]{{x2, y2, z2}, {x1, dy, z2}, {x1, y1, z1}, {x2, dy, z1}}, u1, v1, u2, v2);
        }
    }

    private void addQuad(float[][] verts, int u1, int v1, int u2, int v2) {
        PositionTextureVertex[] vertices = new PositionTextureVertex[verts.length];
        for (int i = 0; i < verts.length; i++) {
            float[] vert = verts[i];
            vertices[i] = new PositionTextureVertex((vert[0] + xzOffset) * scale, (vert[1] + yOffset) * scale, (vert[2] + xzOffset) * scale, 0, 0);
        }
        quads.add(new TexturedQuad(vertices, u1, v1, u2, v2, 64, 32));

    }

}
