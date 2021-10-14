package zeubigame.enginetester;

import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import org.lwjgl.util.vector.Vector4f;
import zeubigame.entities.Camera;
import zeubigame.entities.Entity;
import zeubigame.entities.Light;
import zeubigame.entities.Player;
import zeubigame.guis.GuiRenderer;
import zeubigame.guis.GuiTexture;
import zeubigame.models.TextureModel;
import zeubigame.multiplayer.Bot;
import zeubigame.objConverter.ModelData;
import zeubigame.objConverter.OBJFileLoader;
import zeubigame.renderengine.*;
import zeubigame.models.RawModel;
import zeubigame.terrains.Terrain;
import zeubigame.textures.ModelTexture;
import zeubigame.textures.TerrainTexture;
import zeubigame.textures.TerrainTexturePack;
import zeubigame.toolbox.MousePicker;
import zeubigame.water.WaterFrameBuffers;
import zeubigame.water.WaterRenderer;
import zeubigame.water.WaterShader;
import zeubigame.water.WaterTile;

import java.util.*;

import static org.lwjgl.Sys.getTime;

public class MainGameLoop {

    public static final int id = (int) (Math.random() * 50000000);

    private static int fps;
    private static long lastFPS;

    public static void main(String[] args) {

        // base setup

        System.out.println(id);
        DisplayManager.createDisplay();
        lastFPS = getTime();
        Loader loader = new Loader();
        MasterRenderer renderer = new MasterRenderer(loader);

        // texture pack for terrain

        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("texturePack/grass"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("texturePack/moss"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("texturePack/woodChip"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("texturePack/rock"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture ,bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap2"));

        // terrain + TODO terrain list

        Terrain terrain = new Terrain(0,0,loader, texturePack, blendMap, "test");

        // create tree model, set texture and render it randomly on the terrain

        ModelData data = OBJFileLoader.loadOBJ("entity/arbre2");
        RawModel model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
        TextureModel textureModel = new TextureModel(model , new ModelTexture(loader.loadTexture("entity/arbre")));

        List<Entity> entities = new ArrayList<Entity>();
        Random random = new Random();
        for(int i = 0; i < 150; i++){
            float x = random.nextFloat() * 900;
            float z = (random.nextFloat() * 1450) + 150;
            float y = terrain.getHeightOfTerrain(x, z);
            entities.add(new Entity(textureModel, new Vector3f(x,y,z),0,random.nextFloat()*360,0,1));
        }

        // list for the light and light data (16 Max)

        List<Light> lights = new ArrayList<Light>();

        Light selectEntity = new Light( new Vector3f(0,0,0), new Vector3f(2,1,2), new Vector3f(1, 0.01f, 0.002f));
        Light mouse = new Light( new Vector3f(0,0,0), new Vector3f(1,2,2), new Vector3f(1, 0.01f, 0.02f));
        Light sun = new Light( new Vector3f(0,10000,-5000), new Vector3f(0.4f,0.4f,0.4f));

        lights.add(sun);
        lights.add(selectEntity);
        lights.add(mouse);
        lights.add(new Light( new Vector3f(100,10,100), new Vector3f(2,0,0), new Vector3f(1, 0.01f, 0.002f)));
        lights.add(new Light( new Vector3f(100,10,150), new Vector3f(0,2,2),  new Vector3f(1, 0.01f, 0.002f)));

        // create player model, set texture and set his data / camera

        ModelData dataPlayer = OBJFileLoader.loadOBJ("player/player2");
        RawModel modelPlayer = loader.loadToVAO(dataPlayer.getVertices(), dataPlayer.getTextureCoords(), dataPlayer.getNormals(), dataPlayer.getIndices());
        TextureModel textureModelPlayer = new TextureModel(modelPlayer , new ModelTexture(loader.loadTexture("blanc")));

        Player player = new Player(textureModelPlayer, new Vector3f(800,5,500), 0,0,0,0.75f);
        Camera camera = new Camera(player);

        // list for the water and water data

        WaterShader waterShader = new WaterShader();
        WaterFrameBuffers fbos = new WaterFrameBuffers();
        WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), fbos);
        List<WaterTile> water = new ArrayList<WaterTile>();
        water.add(new WaterTile(800, 800, -40));

        // list for the GUI and GUI data

        List<GuiTexture> guis = new ArrayList<GuiTexture>();
        guis.add(new GuiTexture(loader.loadTexture("gui/rond"), new Vector2f(-0.9f, 0.8f), new Vector2f(100, 100)));

        // render the GUI

        GuiRenderer guiRenderer = new GuiRenderer(loader);

        // create a mouse picker that can return a world pos

        MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);

        // main game loop

        while (!Display.isCloseRequested()){

            // move player and camera

            player.move(terrain);
            camera.move();

            // picker

            picker.update();
            Vector3f terrainPointPicker = picker.getCurrentTerrainPoint();
            if (terrainPointPicker != null){

                Vector3f temp = new Vector3f(terrainPointPicker.x,terrainPointPicker.y + 3, terrainPointPicker.z);
                mouse.setPosition(temp);

                for (Entity entity :entities) {

                    float sizeTemp = 1.5f;

                    if (((entity.getPosition().x + sizeTemp) <= terrainPointPicker.x && terrainPointPicker.x <= (entity.getPosition().x - sizeTemp) || (entity.getPosition().x - sizeTemp) <= terrainPointPicker.x && terrainPointPicker.x <= (entity.getPosition().x + sizeTemp) && ((entity.getPosition().z + sizeTemp) <= terrainPointPicker.z && terrainPointPicker.z <= (entity.getPosition().z - sizeTemp) || (entity.getPosition().z - sizeTemp) <= terrainPointPicker.z && terrainPointPicker.z <= (entity.getPosition().z + sizeTemp)))) {

                        Vector3f pos = new Vector3f(entity.getPosition().x, 10, entity.getPosition().z);
                        selectEntity.setPosition(pos);
                        //entity.setModel( new TextureModel(model, new ModelTexture(loader.loadTexture("vert"))));

                    }

                }

            }

            // refraction and reflection

            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
            fbos.bindReflectionFrameBuffer();
            float distance = 2 * (camera.getPosition().y - water.get(0).getHeight());
            camera.getPosition().y -= distance;
            camera.invertPitch();
            renderer.renderScene(player, entities, terrain, lights, camera, new Vector4f(0,1,0, -water.get(0).getHeight()+1f));
            camera.getPosition().y += distance;
            camera.invertPitch();

            fbos.bindRefractionFrameBuffer();
            renderer.renderScene(player, entities, terrain, lights, camera, new Vector4f(0,-1,0,water.get(0).getHeight()+1f));

            fbos.unbindCurrentFrameBuffer();
            GL11.glDisable(GL30.GL_CLIP_DISTANCE0);

            // render the scene

            renderer.renderScene(player, entities, terrain, lights, camera, new Vector4f(0,-1,0,100000));
            waterRenderer.render(water, camera, sun);

            // render the gui

            guiRenderer.render(guis);

            // update display and FPS

            DisplayManager.updateDisplay();
            updateFPS();

        }

        // clean up and close game

        fbos.cleanUp();
        waterShader.cleanUp();
        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();


    }

    // update FPS and display it in the title

    public static void updateFPS() {
        if (getTime() - lastFPS > 1000) {
           Display.setTitle("FPS: " + fps + " ");
            fps = 0;
            lastFPS += 1000;
        }
        fps++;

    }

}
 /*ModelTexture texture = textureModel.getTexture();
        texture.setShineDamper(50);
        texture.setReflectivity(3);*/