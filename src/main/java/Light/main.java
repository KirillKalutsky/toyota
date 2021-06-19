package Light;


import car.Windows;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL;
import org.python.google.common.collect.ObjectArrays;

import Window.*;
import java.io.IOException;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class main {
    private static final int width=800;
    private static final int height = 600;


    public static void main(String[] args) throws IOException {
        glfwInit();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        var win = new Window("move",width,height);

        var camera = Camera.Init(width,height);

        glfwSetCursorPosCallback(win.getWindowID(), camera::mouse_callback);
        //glfwSetScrollCallback(win.getWindowID(), camera::scroll_callback);
        glfwSetMouseButtonCallback(win.getWindowID(),camera::mouse_button);

        var lightingShader = new ShaderProgram(
                "src/main/java/Light/basic_lighting.vs",
                "src/main/java/Light/basic_lighting.fs");
        var lightCubeShader = new ShaderProgram(
                "src/main/java/Light/light_cube.vs",
                "src/main/java/Light/light_cube.fs");

        var hdrFBO = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, hdrFBO);

        var colorBuffers = new int[2];
        glGenTextures(colorBuffers);
        for (var i = 0; i < 2; i++)
        {
            glBindTexture(GL_TEXTURE_2D, colorBuffers[i]);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, width, height, 0, GL_RGBA, GL_FLOAT, 0);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);  // we clamp to the edge as the blur filter would otherwise sample repeated texture values!
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            // attach texture to framebuffer
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + i, GL_TEXTURE_2D, colorBuffers[i], 0);
        }

        var rboDepth = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rboDepth);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboDepth);

        var attachments = new int[]{ GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1 };
        glDrawBuffers(attachments);
        // finally check if framebuffer is complete
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            System.out.println("Framebuffer not complete!");
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        // ping-pong-framebuffer for blurring
        var pingpongFBO = new int[2];
        var pingpongColorbuffers = new int[2];
        glGenFramebuffers(pingpongFBO);
        glGenTextures(pingpongColorbuffers);
        for (int i = 0; i < 2; i++)
        {
            glBindFramebuffer(GL_FRAMEBUFFER, pingpongFBO[i]);
            glBindTexture(GL_TEXTURE_2D, pingpongColorbuffers[i]);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, width, height, 0, GL_RGBA, GL_FLOAT, 0);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE); // we clamp to the edge as the blur filter would otherwise sample repeated texture values!
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, pingpongColorbuffers[i], 0);
            // also check if framebuffers are complete (no need for depth buffer)
            if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
               System.out.println("Framebuffer not complete!" );
        }


        float vertices[] = {
                -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
                0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
                0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
                0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
                -0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
                -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,

                -0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,
                0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,
                0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,
                0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,
                -0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,
                -0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,

                -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,
                -0.5f,  0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
                -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
                -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
                -0.5f, -0.5f,  0.5f, -1.0f,  0.0f,  0.0f,
                -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,

                0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,
                0.5f,  0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
                0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
                0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
                0.5f, -0.5f,  0.5f,  1.0f,  0.0f,  0.0f,
                0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,

                -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,
                0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,
                0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
                0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
                -0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
                -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,

                -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,
                0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,
                0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
                0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
                -0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
                -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f
        };


        float newVertices[] = {
                -0.5f, -0.5f, -0.5f,//0
                0.5f, -0.5f, -0.5f, //1
                0.5f,  0.5f, -0.5f,//2
                -0.5f,  0.5f, -0.5f, //3
                //(0,3,1) (3,2,1)
             /*   0.5f, -0.5f,  0.5f, //4
                0.5f,  0.5f,  0.5f, //5
                -0.5f,  0.5f,  0.5f, //6
                -0.5f, -0.5f,  0.5f, //7
                //(4,7,6)(6,5,7)
                -0.5f,  0.5f,  0.5f, //8
                -0.5f,  0.5f, -0.5f, //9
                -0.5f, -0.5f, -0.5f,//10
                -0.5f, -0.5f,  0.5f, //11
                //(8,9,10)()
                0.5f,  0.5f,  0.5f, //12
                0.5f,  0.5f, -0.5f,  //13
                0.5f, -0.5f, -0.5f, //14
                0.5f, -0.5f,  0.5f,  //15

                -0.5f, -0.5f, -0.5f, //16
                0.5f, -0.5f, -0.5f, //17
                0.5f, -0.5f,  0.5f, //18
                -0.5f, -0.5f,  0.5f,  //19

                -0.5f,  0.5f, -0.5f, //20
                0.5f,  0.5f, -0.5f,  //21
                0.5f,  0.5f,  0.5f,  //22
                -0.5f,  0.5f,  0.5f,  //23*/
        };

        var newI = new int[]{
                0,3,1,
                3,2,1
        };

        var res = findNormals(newVertices,newI);





      /*  var p0= new Vector3f(-0.5f, -0.5f, -0.5f);
        var p1= new Vector3f(0.5f, -0.5f, -0.5f);
        var p2= new Vector3f( -0.5f,  0.5f, -0.5f);
        var p4 = new Vector3f(0.5f,  0.5f, -0.5f);

        var exn1 = new Vector3f(p1.x-p0.x,p1.y-p0.y,p1.z-p0.z);
        var exn2 = new Vector3f(p2.x-p1.x,p2.y-p1.y,p2.z-p1.z);

        var exn3 = new Vector3f(p2.x-p4.x,p2.y-p4.y,p2.z-p4.z);
        var exn4 = new Vector3f(p1.x-p2.x,p1.y-p2.y,p1.z-p2.z);

        var n1 = exn3.cross(exn4).normalize();

        var n = exn1.cross(exn2).normalize();

        System.out.println(n.x+" "+n.y+" "+n.z);
        System.out.println(n1.x+" "+n1.y+" "+n1.z);
*/
        var lightColor = new Vector3f(1,1,0.75f);
        var diffuseColor = lightColor.mul(new Vector3f(0.7f));
        var ambientColor = diffuseColor.mul(new Vector3f(0.2f));
        var lightPosition = new Vector3f(15f, 15, 3);


        lightingShader.setVec3("dirLight.direction", new Vector3f(-1.5f, -1.0f, -0.3f));
        lightingShader.setVec3("dirLight.ambient", new Vector3f(0.05f, 0.05f, 0.05f));
        lightingShader.setVec3("dirLight.diffuse", new Vector3f(0.4f, 0.4f, 0.4f));
        lightingShader.setVec3("dirLight.specular", new Vector3f(0.5f, 0.5f, 0.5f));

        lightingShader.setVec3("material.ambient", new Vector3f(1.0f, 0.5f, 0.31f));
        lightingShader.setVec3("material.diffuse", new Vector3f(1.0f, 0.5f, 0.31f));
        lightingShader.setVec3("material.specular", new Vector3f(0.5f, 0.5f, 0.5f));
        lightingShader.setFloat("material.shininess", 32.0f);

        lightingShader.setVec3("pointLights[0].position", lightPosition);
        lightingShader.setFloat("pointLights[0].constant", 1.0f);
        lightingShader.setFloat("pointLights[0].linear", 0.09f);
        lightingShader.setFloat("pointLights[0].quadratic", 0.032f);
        lightingShader.setVec3("pointLights[0].ambient", ambientColor);
        lightingShader.setVec3("pointLights[0].diffuse", diffuseColor);
        lightingShader.setVec3("pointLights[0].specular", new Vector3f(1.0f, 1.0f, 1.0f));




        var lightPositions = new Vector3f[]{
                new Vector3f(1.2f, 1.0f, 2.0f),
                new Vector3f(1.2f, 1.0f, -2.0f)
        };


        var cubeVAO = glGenVertexArrays();
        var VBO = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glBindVertexArray(cubeVAO);

        // position attribute
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        // normal attribute
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 *  Float.BYTES, 3 *  Float.BYTES);
        glEnableVertexAttribArray(1);


        // second, configure the light's VAO (VBO stays the same; the vertices are the same for the light object which is also a 3D cube)

        var lightCubeVAO = glGenVertexArrays();
        glBindVertexArray(lightCubeVAO);

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        // note that we update the lamp's position attribute's stride to reflect the updated buffer data
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);



        var r = 2;
        var circle1 = drawWheel(r, 0.5f, 250, Math.PI/2,0.5f);
        var circle2 = drawWheel(r-0.5f, 0.5f, 250, Math.PI/2,0.5f);

        System.out.println(circle1.stream().count());
        System.out.println(circle2.stream().count());



        var resCircle1 = new LinkedList<Float>();
        for(var e = 0;e<circle1.stream().count()/9; e++){
            var startFirstInd = e*9;
            var startSecondInd = e*9+6;

            var x1Index = startFirstInd;
            var y1Index = startFirstInd+1;
            var z1Index = startFirstInd+2;

            var x2Index = startSecondInd;
            var y2Index = startSecondInd+1;
            var z2Index = startSecondInd+2;

            resCircle1.add(circle1.get(x1Index));
            resCircle1.add(circle1.get(y1Index));
            resCircle1.add(circle1.get(z1Index));

            resCircle1.add(circle1.get(x2Index));
            resCircle1.add(circle1.get(y2Index));
            resCircle1.add(circle1.get(z2Index));

            resCircle1.add(circle2.get(x1Index));
            resCircle1.add(circle2.get(y1Index));
            resCircle1.add(circle2.get(z1Index));




            resCircle1.add(circle1.get(x2Index));
            resCircle1.add(circle1.get(y2Index));
            resCircle1.add(circle1.get(z2Index));

            resCircle1.add(circle2.get(x1Index));
            resCircle1.add(circle2.get(y1Index));
            resCircle1.add(circle2.get(z1Index));

            resCircle1.add(circle2.get(x2Index));
            resCircle1.add(circle2.get(y2Index));
            resCircle1.add(circle2.get(z2Index));


        }
       /* resCircle1.addAll(circle1);
        resCircle1.addAll(circle2);*/

        resCircle1.addAll(circle1);
        resCircle1.addAll(circle2);

        var resCircle = ArrayUtils.toPrimitive(resCircle1.toArray(new Float[0]),0.0f);

        /*for(var e=0;e< circle.length;e++){
            System.out.print(circle[e]+" ");
            if((e+1)%3==0)
                System.out.println();
        }

        System.out.println(circle.length);*/

        var corpusVAO =glGenVertexArrays();
        var corpusVBO =glGenBuffers();

        glBindVertexArray(corpusVAO);

        glBindBuffer(GL_ARRAY_BUFFER, corpusVBO);
        glBufferData(GL_ARRAY_BUFFER, resCircle, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);


        var ellipse = DrawEllipse(1,1,0.5f,0.15f,10);
        var ellipseVAO =glGenVertexArrays();
        var ellipseVBO =glGenBuffers();

        glBindVertexArray(ellipseVAO);

        glBindBuffer(GL_ARRAY_BUFFER, ellipseVBO);
        glBufferData(GL_ARRAY_BUFFER, ArrayUtils.toPrimitive(ellipse.toArray(new Float[0]),0.0f), GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        var f = 0.0f;

        var radius = 5;

        while(!glfwWindowShouldClose(win.getWindowID()))
        {
            /*win.closeWindow();*/
            float currentFrame = (float) glfwGetTime();
            camera.deltaTime = currentFrame - camera.lastFrame;
            camera.lastFrame = currentFrame;
            camera.processInput(win.getWindowID());
            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glEnable(GL_DEPTH_TEST);

            glEnable(GL_AUTO_NORMAL);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);



            var view = new Matrix4f().lookAt(
                    camera.getCameraPos(),
                    new Vector3f(
                            camera.getCameraPos().x+camera.getCameraFront().x,
                            camera.getCameraPos().y+camera.getCameraFront().y,
                            camera.getCameraPos().z+camera.getCameraFront().z),
                    camera.getCameraUp());
            var projection = new Matrix4f().perspective(camera.fov, (float)win.getWidth() / (float)win.getHeight(), 0.1f, 100.0f);



            glUseProgram(lightingShader.getProgramID());

            lightingShader.setVec3("objectColor", new Vector3f(1.0f, 0.5f, 0.31f));
            lightingShader.setVec3("lightColor", new Vector3f(1.0f, 1.0f, 1.0f));
            lightingShader.setVec3("lightPos", lightPosition);
            lightingShader.setVec3("viewPos", camera.getCameraPos());


            lightingShader.setUniform("view",view);
            lightingShader.setUniform("projection",projection);
            lightingShader.setUniform("model",new Matrix4f());

            glBindVertexArray(cubeVAO);
            glDrawArrays(GL_TRIANGLES, 0, 36);

            glUseProgram(lightCubeShader.getProgramID());
            lightCubeShader.setUniform("view",view);
            lightCubeShader.setUniform("projection",projection);
            var model = new Matrix4f();
            model = model.translate(new Vector3f(1,0,0));
            model = model.scale(new Vector3f(0.2f)); // a smaller cube
            lightCubeShader.setUniform("model", model);

            glBindVertexArray(lightCubeVAO);
            glDrawArrays(GL_TRIANGLES, 0, 36);

            lightPosition.x = (float) Math.cos(f) * radius;
            lightPosition.y = (float) Math.sin(f) * radius;
            lightPosition.z = (float) Math.sin(f) * radius;



            glBindVertexArray(corpusVAO);

            model = new Matrix4f().translate(new Vector3f(-r-1,0,0));
            lightCubeShader.setUniform("model", model);
            glDrawArrays(GL_TRIANGLES, 0, resCircle.length/3);

            /*model.translate(new Vector3f(-0.5f,0,0));
            lightCubeShader.setUniform("model", model);
            glDrawArrays(GL_TRIANGLES, 0, circle.length/3);*/

            glBindVertexArray(ellipseVAO);
            model = new Matrix4f();
            lightCubeShader.setUniform("model", model);
            glDrawArrays(GL_TRIANGLES, 0, (int)ellipse.stream().count()/3);



            //f += 0.001;

            glfwSwapBuffers(win.getWindowID());
            glfwPollEvents();
        }


        glfwTerminate();
    }

    private static float[] findNormals(float[] v, int[] i){
        var result = new float[v.length*3];

        for(var e = 0; e<i.length-2;e+=3){
            var p0 = getPoint(v,i[e]);
            var p1 = getPoint(v,i[e+1]);
            var p2 = getPoint(v,i[e+2]);
            var list = Arrays.asList(p0,p1,p2);
            var exn1 = new Vector3f(p1.x-p0.x,p1.y-p0.y,p1.z-p0.z);
            var exn2 = new Vector3f(p2.x-p1.x,p2.y-p1.y,p2.z-p1.z);

            var n = exn1.cross(exn2).normalize();

            for(var p=0;p<list.stream().count();p++){
                var startIndex = i[e+p];
                result[startIndex]=list.get(p).x;
                result[startIndex+1]=list.get(p).y;
                result[startIndex+2]=list.get(p).z;
                result[startIndex+3]=n.x;
                result[startIndex+4]=n.y;
                result[startIndex+5]=n.z;

            }
        }

        return result;
    }

    private static Vector3f getPoint(float[] v, int numberPoint){
        var startIndex = numberPoint*3;
        System.out.println(startIndex);
        return new Vector3f(v[startIndex],v[startIndex+1],v[startIndex+2]);
    }

    public static float[] createCircle(double radius, double circumference,float width){
        var res = new ArrayList<Float>();
        var f = 0.0;
        var counter = 0;
        while (f<circumference /*&& res.stream().count()%9!=0*/){
            res.add((float) (Math.cos(f) * radius));
            res.add((float) (Math.sin(f) * radius));
            if(counter%2==0) {
                res.add(0f);
            }
            else
                res.add(width);
            counter++;
            f+=0.01f;
            if(res.stream().count()>9){
                for(var e = 0; e<6; e++){
                    res.add(res.get((int)res.stream().count()-6));
                }
            }

        }
        return ArrayUtils.toPrimitive(res.toArray(new Float[0]), 0.0F);
    }


    private static ArrayList<Float> drawWheel(float radius, float height, int elementCount, double circumference,float thickness) {
        var res = new ArrayList<Float>();
        var f = 0.0;

        var counter = 0;

        while (f<=circumference) {

            if(res.stream().count()>=9){
                var a = res.get((int) res.stream().count() - 6);
                var b = res.get((int) res.stream().count() - 5);
                var c = res.get((int) res.stream().count() - 4);
                var d = res.get((int) res.stream().count() - 3);
                var e = res.get((int) res.stream().count() - 2);
                var f1 = res.get((int) res.stream().count() - 1);
                /*var a1 = res.get((int) res.stream().count() - 12);
                var b1 = res.get((int) res.stream().count() - 11);
                var c1 = res.get((int) res.stream().count() - 10);
                var d1 = res.get((int) res.stream().count() - 9);
                var e1 = res.get((int) res.stream().count() - 8);
                var f11 = res.get((int) res.stream().count() - 7);*/

                /*res.add(a1);
                res.add(b1);
                res.add(c1);
                res.add(d1);
                res.add(e1);
                res.add(f11);*/
                res.add(a);
                res.add(b);
                res.add(c);
                res.add(d);
                res.add(e);
                res.add(f1);
            }

            /*res.add((float) Math.cos(f) * (radius-thickness));
            res.add((float) Math.sin(f) * (radius-thickness));
            res.add(counter % 2 == 0 ? 0 : height);*/

            res.add((float) Math.cos(f) * radius);
            res.add((float) Math.sin(f) * radius);
            res.add(counter % 2 == 0 ? 0 : height);



            counter++;
            f += (2 * Math.PI / (float) elementCount);
        }

        return res;
    }

    public static LinkedList<Float> DrawEllipse(float cx, float cy, float rx, float ry, int num_segments)
    {
        var res = new LinkedList<Float>();
        float theta =(float)( 2 * Math.PI / (float)num_segments);
        float c = (float) Math.cos(theta);//precalculate the sine and cosine
        float s = (float) Math.sin(theta);
        float t;

        float x = 1;//we start at angle = 0
        float y = 0;

        for(int ii = 0; ii <= num_segments+1; ii++)
        {
            if(res.stream().count()>=9){

                for (var e = 0; e < 3; e++) {
                        res.add(res.get((int)res.stream().count()-3));
                }
                res.add(cx);
                res.add(cy);//output vertex
                res.add(0.0f);
                res.add(x * rx + cx);
                res.add(y * ry + cy);//output vertex
                res.add(0.0f);

            }
            else {
                if(ii%3==0){
                    res.add(cx);
                    res.add(cy);//output vertex
                    res.add(0.0f);
                }
                else {
                    res.add(x * rx + cx);
                    res.add(y * ry + cy);//output vertex
                    res.add(0.0f);
                }
            }
            //apply radius and offset


            //apply the rotation matrix
            t = x;
            x = c * x - s * y;
            y = s * t + c * y;
        }
        return res;
    }
}
