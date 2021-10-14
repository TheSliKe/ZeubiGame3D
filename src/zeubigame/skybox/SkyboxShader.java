package zeubigame.skybox;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import zeubigame.entities.Camera;
import zeubigame.shaders.ShaderProgram;
import zeubigame.toolbox.Maths;

public class SkyboxShader extends ShaderProgram {
    private static final String VERTEX_FILE = "src/zeubigame/skybox/skyboxVertexShader.txt";
    private static final String FRAGMENT_FILE = "src/zeubigame/skybox/skyboxFragmentShader.txt";

    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_fogColour;
    private int location_cubeMap;
    private int location_cubeMap2;
    private int location_blendFactor;

    public SkyboxShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    public void loadProjectionMatrix(Matrix4f matrix){
        super.loadMatrix(location_projectionMatrix, matrix);
    }

    public void loadViewMatrix(Camera camera){
        Matrix4f matrix = Maths.createViewMatrix(camera);
        matrix.m30 = 0;
        matrix.m31 = 0;
        matrix.m32 = 0;
        super.loadMatrix(location_viewMatrix, matrix);
    }

    public void loadFogColour(float r, float g, float b){
        super.loadVector(location_fogColour, new Vector3f(r, g, b));
    }

    public void connectTextureUnits(){
        super.loadInt(location_cubeMap, 0);
        super.loadInt(location_cubeMap2, 1);
    }

    public void loadBlendFactor(float blendFactor){
        super.loadFloat(location_blendFactor, blendFactor);
    }

    @Override
    protected void getAllUniformLocations() {
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_fogColour = super.getUniformLocation("fogColour");
        location_cubeMap = super.getUniformLocation("cubeMap");
        location_cubeMap2 = super.getUniformLocation("cubeMap2");
        location_blendFactor = super.getUniformLocation("blendFactor");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

}
