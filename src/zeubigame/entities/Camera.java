package zeubigame.entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

    private Vector3f position = new Vector3f(0,0,0);
    private float pitch;
    private float yaw;
    private float roll;

    private Player player;

    private float distanceFromPlayer = 50;
    private float angleAroundPlayer = 0;

    public Camera(Player player){
        this.player = player;
        this.pitch = 15.0f;
    }

    public void move(){
        calculateZoom();
        calculatePitch();
        calculateAngleAroundPlayer();

        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance, verticalDistance);
        this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    private void calculateCameraPosition(float horizontalDistance, float verticalDistance){

        float theta = player.getRotY() + angleAroundPlayer;
        float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));

        position.x = player.getPosition().x - offsetX;
        position.z = player.getPosition().z - offsetZ;
        position.y = player.getPosition().y + verticalDistance;
    }

    private float calculateHorizontalDistance(){
        return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
    }

    private float calculateVerticalDistance(){
        return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)) + 10);
    }

    private void calculateZoom() {


        if (distanceFromPlayer >= 50.0f && distanceFromPlayer <= 150.0f) {
            float zoomLevel = Mouse.getDWheel() * 0.1f;
            distanceFromPlayer -= zoomLevel;
        } else {
            if (distanceFromPlayer < 100.0f){
                distanceFromPlayer = 50.0f;
            } else {
                distanceFromPlayer = 150.0f;
            }
        }

    }

    public void invertPitch(){
        this.pitch = -pitch;
    }

    private void calculatePitch(){

        if (Mouse.isButtonDown(1)){
            if (pitch >= 10.0f && pitch <= 90.0f) {
                float pitchChange = Mouse.getDY() * 0.1f;
                pitch -= pitchChange;
            } else {
                if (pitch < 45.0f){
                    pitch = 10.0f;
                } else {
                    pitch = 90.0f;
                }
            }
        }
    }

    private void calculateAngleAroundPlayer(){
        if (Mouse.isButtonDown(1)){
            float angleChange = Mouse.getDX() * 0.3f;
            angleAroundPlayer -= angleChange;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_R)){
            angleAroundPlayer = 0;
        }
    }

}
