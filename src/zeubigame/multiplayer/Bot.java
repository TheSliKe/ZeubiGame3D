package zeubigame.multiplayer;

import org.lwjgl.util.vector.Vector3f;

import java.io.Serializable;

public class Bot implements Serializable {

    private int id;
    private String name;
    private Vector3f position;
    private float rotX, rotY, rotZ;

    public Bot(int id, String name, Vector3f position, float rotX, float rotY, float rotZ){

        this.id = id;
        this.name = name;
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;

    }

    public int getId() {
        return id;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void updatePos(Vector3f position, float rotX, float rotY, float rotZ){
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
    }

    public float getRotX() {
        return rotX;
    }

    public float getRotY() {
        return rotY;
    }

    public float getRotZ() {
        return rotZ;
    }
}
