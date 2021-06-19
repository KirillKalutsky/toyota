package Window;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.io.*;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram{

    private int programID;

    public int getProgramID() {
        return programID;
    }

    private int vsID;

    private int fsID;

    public ShaderProgram(String vs, String fs) throws IOException {
        var vsSource = readShaderSource(vs);
        var fsSource = readShaderSource(fs);

        vsID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vsID, vsSource);
        glCompileShader(vsID);

        fsID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fsID,  fsSource);
        glCompileShader(fsID);

        programID = glCreateProgram();
        glAttachShader(programID, vsID);
        glAttachShader(programID, fsID);
        glLinkProgram(programID);
    }

    public void setUniform(String name, Matrix4f value){
        var location = glGetUniformLocation(programID,name);
        var matrix = BufferUtils.createFloatBuffer(16);
        value.get(matrix);
        if(location!=-1)
            glUniformMatrix4fv(location,false,matrix);
    }

    public void setVec3(String name, Vector3f value){
        var location = glGetUniformLocation(programID,name);
        var vector = BufferUtils.createFloatBuffer(3);
        value.get(vector);
        if(location!=-1)
            glUniform3fv(location,vector);
    }

    public void setInt(String name, int value){
        var location = glGetUniformLocation(programID,name);
        if(location!=-1)
            glUniform1i(location,value);
    }
    public void setFloat(String name, float value){
        var location = glGetUniformLocation(programID,name);
        if(location!=-1)
            glUniform1f(location,value);
    }

    private String readShaderSource(String filePath) throws IOException {
        var result = new StringBuilder();
        var br = new BufferedReader(new FileReader(new File(filePath)));
        String line;
        while((line = br.readLine())!=null){
            result.append(line);
            result.append("\n");
        }
        br.close() ;
        return result.toString();
    }

}