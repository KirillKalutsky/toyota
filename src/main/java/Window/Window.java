package Window;

import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {

    private long windowID;

    private final int width;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private final int height;

    public long getWindowID() {
        return windowID;
    }

    public void setWindow(long windowID) {
        this.windowID = windowID;
    }

    public Window(String windowName,int width, int height){

        this.width = width;
        this.height = height;
        windowID = glfwCreateWindow(width, height, windowName, NULL, NULL);
        if (windowID == NULL)
        {
            System.out.println("Failed to create GLFW window");
            glfwTerminate();
            System.exit(1);
        }
        glfwMakeContextCurrent(windowID);
        GL.createCapabilities();
        glfwSetFramebufferSizeCallback(windowID,(wind,w,h)->glViewport(0, 0, w, h));
    }

}
