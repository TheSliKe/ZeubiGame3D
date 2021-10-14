package zeubigame.renderengine;

import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Matrix4f;
import zeubigame.entities.Entity;
import zeubigame.models.RawModel;
import zeubigame.models.TextureModel;
import zeubigame.shaders.StaticShader;
import zeubigame.textures.ModelTexture;
import zeubigame.toolbox.Maths;

import java.util.List;
import java.util.Map;

public class EntityRenderer {


    private StaticShader shader;

    public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix){
        this.shader = shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void render(Map<TextureModel, List<Entity>> entities){
        for (TextureModel model:entities.keySet()){

            prepareTexturedModel(model);
            List<Entity> batch = entities.get(model);
            for (Entity entity:batch){
                prepareInstance(entity);
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawmodel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }
            unbindTexturedModel();
        }
    }

    private void prepareTexturedModel(TextureModel model){
        RawModel rawModel = model.getRawmodel();
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        ModelTexture texture = model.getTexture();
        shader.loadNumberOfRow(texture.getNumberOfRows());
        if (texture.isHasTransparency()){
            MasterRenderer.disableCulling();
        }
        shader.loadFakeLightingVariable(texture.isUseFakeLighting());
        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTexturetID());
    }

    private void unbindTexturedModel(){
        MasterRenderer.enableCulling();
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    private void prepareInstance(Entity entity){
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
        shader.loadOffset(entity.getTextureXOffset(), entity.getTextureYOffset());
    }




}
