package zeubigame.models;

import zeubigame.textures.ModelTexture;

public class TextureModel {

    private RawModel rawmodel;
    private ModelTexture texture;

    public TextureModel(RawModel model, ModelTexture texture){

        this.rawmodel = model;
        this.texture = texture;

    }

    public RawModel getRawmodel() {
        return rawmodel;
    }

    public ModelTexture getTexture() {
        return texture;
    }

}
