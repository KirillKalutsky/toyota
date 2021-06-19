package Window;

import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {

    public Vector3f getCameraPos() {
        return cameraPos;
    }

    public Vector3f getCameraFront() {
        return cameraFront;
    }

    public Vector3f getCameraUp() {
        return cameraUp;
    }

    private Vector3f cameraPos;
    private Vector3f cameraFront;
    private Vector3f cameraUp;

    public float deltaTime = 0.0f;
    public float lastFrame = 0.0f;

    boolean firstMouse = true;
    boolean moveMouse = true;
    float yaw   = -90.0f;
    float pitch =  0.0f;
    float lastX ;
    float lastY ;
    public float fov = 45.0f;

    private Camera(int width,int height){
        lastX = width/2;
        lastY = height/2;
        cameraPos   = new Vector3f(0.0f, 0.0f, 3.0f);
        cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        cameraUp    = new Vector3f(0.0f, 1.0f, 0.0f);
    }

    private static Camera camera;

    public static Camera Init(int w, int h){
        if(camera==null)
            camera = new Camera(w,h);
        return camera;
    }

    public void mouse_button(long l, int i, int i1, int i2) {
        if( i1==GLFW_MOUSE_BUTTON_LEFT )
        {
            moveMouse =!moveMouse;
        }
    }


    public void processInput(Long window)
    {
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
            glfwSetWindowShouldClose(window, true);

        float cameraSpeed =  2.5f * deltaTime;
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS)
            cameraPos.add(new Vector3f( cameraFront.x* cameraSpeed,cameraFront.y*cameraSpeed,cameraFront.z*cameraSpeed));
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS)
            cameraPos.sub(new Vector3f(cameraSpeed * cameraFront.x,cameraSpeed * cameraFront.y,cameraSpeed * cameraFront.z));
    }


    public void mouse_callback(Long window, double xpos, double ypos)
    {
        if(moveMouse) {
            if (firstMouse) {
                lastX = (float) xpos;
                lastY = (float) ypos;
                firstMouse = false;
            }

            float xoffset = (float) xpos - lastX;
            float yoffset = lastY - (float) ypos; // reversed since y-coordinates go from bottom to top
            lastX = (float) xpos;
            lastY = (float) ypos;

            float sensitivity = 0.01f;
            xoffset *= sensitivity;
            yoffset *= sensitivity;

            yaw += xoffset;
            pitch += yoffset;

            var front = new Vector3f();
            front.x = (float) Math.cos(yaw) * (float) Math.cos(pitch);
            front.y = (float) Math.sin(pitch);
            front.z = (float) Math.sin(yaw) * (float) Math.cos(pitch);
            cameraFront = front.normalize();
        }
    }

}
