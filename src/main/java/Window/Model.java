package Window;

import java.util.Arrays;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class Model {

    private float[] vertices;
    private int[] indices;
    private int[] layouts;

    /*
     * vao_id - с помощью vao мы указываем, какой именно vbo отрисовать
     * VBO (Vertex Buffer Object) - буфер вершин (и не только вершин, но также и прилагающейся к ним
     * 															информации: нормали, текстурные координаты и т.д.), хранится в памяти GPU
     * EBO (Element Buffer Objects) - позволяет указывать общие вершины для разных треугольников
     */
    private int vbo_id;
    private int ebo_id;
    private int vao_id;

    public Model(float[] vertices, int[] indices,int[] layouts){
        this.vertices = vertices;
        this.indices = indices;
        this.layouts = layouts;
    }

    public void draw(){
        if (indices==null)
            glDrawArrays(GL_TRIANGLES, 0, vertices.length/Arrays.stream(layouts).sum());
        else
            glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
    }

    public void bind(){
        glBindVertexArray(vao_id);
    }

    public void render(){
        vao_id=glGenVertexArrays();
        glBindVertexArray(vao_id);

        vbo_id=glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo_id);
        glBufferData(GL_ARRAY_BUFFER,  vertices, GL_STATIC_DRAW);

        if(indices!=null){
            ebo_id=glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo_id);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER,  indices, GL_STATIC_DRAW);
        }

        for(var i = 0; i<layouts.length; i++){
            int countPoints=0;
            for(var e = 0; e < i; e++)
                countPoints+=layouts[e];
            glVertexAttribPointer(i, layouts[i], GL_FLOAT, false, Arrays.stream(layouts).sum() * Float.BYTES, countPoints*Float.BYTES);
            glEnableVertexAttribArray(i);

        }
    }

    public void clear(){
        glDeleteVertexArrays(vao_id);
        glDeleteBuffers(ebo_id);
        glDeleteBuffers(vbo_id);
    }

}
