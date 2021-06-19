package Window;

import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class Texture {
    public int getId() {
        return id;
    }

    private ByteBuffer pixels;

    private int id;
    private final int width;
    private final int height;

    public Texture(String filename) throws IOException {
        var image = ImageIO.read(new File(filename));

        width = image.getWidth();
        height = image.getHeight();


        var pixels_raw = image.getRGB(0,0,width,height,null,0,width);

        pixels = BufferUtils.createByteBuffer(width*height*4);

        for(int i=0; i<width; i++){
            for(int j=0; j<height; j++){
                var pixel = pixels_raw[i*width+j];
                var  r = ((byte) (pixel >> 16)& 0xff);
                pixels.put((byte) r);
                var  g = ((byte) (pixel >> 8)& 0xff);
                pixels.put((byte) g);
                var  b = (pixel & 0xff);
                pixels.put(((byte) b ));
                var  a = ((byte) (pixel  >> 24)& 0xff);
                pixels.put((byte) a);
            }
        }

        pixels.flip();

        id=glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
        glGenerateMipmap(GL_TEXTURE_2D);

        glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

    }

    public void bind(){
        glBindTexture(GL_TEXTURE_2D,id);
    }

}
