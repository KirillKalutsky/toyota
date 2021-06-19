import Window.Window;
import car.Corps;
import car.Windows;

import org.apache.commons.lang3.ArrayUtils;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import Window.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class Main {

    private static final int width=800;
    private static final int height = 600;

    public static void main(String[] args) throws IOException {
        glfwInit();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        var window = new Window("Toyota Supra 1990",width,height);

        var camera = Camera.Init(width,height);

        glfwSetCursorPosCallback(window.getWindowID(), camera::mouse_callback);
        glfwSetMouseButtonCallback(window.getWindowID(),camera::mouse_button);

        /*Две шейдерные программы
        * первая используется для всех элементов машины, кроме дисков
        * вторая специально для дисков, она игнорирует значения с определенным альфаканалом
        * */
        var mainShader = new ShaderProgram(
                "src/main/resources/shaders/basic_lighting.vs",
                "src/main/resources/shaders/basic_lighting.fs");

        var textureShader = new ShaderProgram(
                "src/main/resources/shaders/diskShader.vs",
                "src/main/resources/shaders/diskShader.fs");

        var allModels = new LinkedList<Model>();

        /*Колеса*/
        var wheelRadius = 0.325f;

        var wheelVertices = ArrayUtils.toPrimitive(
                createTube(
                        wheelRadius,
                        0.325f,
                        50,
                        6.5
                ).toArray(new Float[0]),0.0f);

        var wheel = new Model(wheelVertices,null, new int[]{3});
        allModels.add(wheel);
        wheel.render();

        var wheelsPositions = new Vector3f[]{
                new Vector3f(-3.5f,0,2.05f),
                new Vector3f(-3.5f,0,-0.05f),
                new Vector3f(-0.875f,-0.05f,2.05f),
                new Vector3f(-0.875f,-0.05f,-0.05f)
        };

        /*Диски*/
        var diskRadius = wheelRadius+0.0125f;

        float diskVertices[] = {
                diskRadius,  diskRadius, 0.0f,    1.0f, 1.0f,
                diskRadius, -diskRadius, 0.0f,   1.0f, 0.0f,
                -diskRadius, -diskRadius, 0.0f,      0.0f, 0.0f,
                -diskRadius,  diskRadius, 0.0f,     0.0f, 1.0f
        };

        int diskIndices[] = {
                0, 1, 3,
                1, 2, 3
        };

        var disk = new Model(diskVertices,diskIndices,new int[]{3,2});
        disk.render();
        allModels.add(disk);

        var diskTexture=new Texture("src/main/resources/textures/disk.png");

        /*Корпус с грубыми гранями*/
        var roughCorpusVertices = findNormals(Corps.onlyCorpusVertices);
        var roughCorpus = new Model(roughCorpusVertices,null,new int[]{3,3});
        roughCorpus.render();
        allModels.add(roughCorpus);

        /*Корпус со сглаженными гранями*/
        var smoothCorpusVertices = Corps.getOnlySmoothCorpusVertices;
        var smoothCorpus =new Model(smoothCorpusVertices,null,new int[]{3});
        smoothCorpus.render();
        allModels.add(smoothCorpus);

        var corpusPosition =  new Vector3f[]{
                new Vector3f( 0.0f,  0.0f,  0.0f),
                new Vector3f( 0.0f,  0.0f,  2.0f)
        };

        /*Спойлер*/
        var spoilerVertices = findNormals(
                createCylinder(9,0.25f,400,Math.PI/12,0.05f)
        );

        var spoiler = new Model(spoilerVertices,null,new int[]{3,3});
        spoiler.render();
        allModels.add(spoiler);

        /*Салон*/
        var salon = new Model(findNormals(Corps.salonVertices),null,new int[]{3,3});
        salon.render();
        allModels.add(salon);

        /*Окна*/
        var windows = new Model(Windows.vertices, Windows.indices,new int[]{3});
        windows.render();
        allModels.add(windows);

        /*Задние фары*/
        var backLightVertices = createEllipse(-0.215f, 0, 0.05f, 0.08f, 25);
        backLightVertices.addAll( createEllipse(0.215f, 0, 0.05f, 0.08f, 25));
        backLightVertices.addAll( createEllipse(0f, 0.05f, 0.25f, 0.07f, 25));
        backLightVertices.addAll( createEllipse(0f, -0.05f, 0.25f, 0.07f, 25));

        var backLight = new Model(ArrayUtils.toPrimitive(backLightVertices.toArray(new Float[0]),0.0f),null,new int[]{3});
        backLight.render();
        allModels.add(backLight);

        /*
        По идеи должно было быть как радиаторная решетка
        Хотел сделать решетку как у оригинала, но оказалось довольно проблематично
        */
        var forwardHoleVertices = createEllipse(-4.4f, 0.15f, 0.5f, 0.04f, 25);
        var forwardHole = new Model( ArrayUtils.toPrimitive(forwardHoleVertices.toArray(new Float[0]),0),null, new int[]{3});
        forwardHole.render();
        allModels.add(forwardHole);

        /*
        Выхлопная труба
        Состоит из двух частей, крупного цилиндра и глушителя
        */
        var exhaustPipeVertices = createCylinder(0.075f,0.5f,100,6.5,0.075f);
        var exhaustPipe = new Model(exhaustPipeVertices,null,new int[]{3});
        exhaustPipe.render();
        allModels.add(exhaustPipe);

        var pipeVertices = createCylinder(0.035f,0.15f,100,6.5,0.005f);
        var pipe = new Model(pipeVertices,null,new int[]{3});
        pipe.render();
        allModels.add(pipe);

        /*Рулевое колесо*/
        var steeringWheelVertices = findNormals(
                createCylinder(
                        0.195f,
                        0.02f,
                        100,
                        6.5,
                        0.025f
                )
        );
        var steeringWheel = new Model(steeringWheelVertices,null,new int[]{3,3});
        steeringWheel.render();
        allModels.add(steeringWheel);

        /*Заднее рыло*/
        var backFenderVertices = findNormals(
                createCylinder(
                        wheelRadius+0.115f,
                        0.05f,
                        150,
                        3.075f,
                        0.1f
                )
        );
        var backFender = new Model(backFenderVertices,null,new int[]{3,3});
        backFender.render();
        allModels.add(backFender);

        /*Переднее крыло*/
        var forwardFenderVertices = findNormals(
                createCylinder(
                        wheelRadius+0.115f,
                        0.05f,
                        150,
                        Math.PI+0.35f,
                        0.1f)

        );
        var forwardFender = new Model(forwardFenderVertices,null,new int[]{3,3});
        forwardFender.render();
        allModels.add(forwardHole);

        /*Текстура дороги*/
        var roadY = -(diskRadius+0.055f);

        float roadVertices[] = {

                -6,  roadY, -1f,    1.0f, 1.0f,
                1.5f, roadY, -1f,   1.0f, 0.0f,
                1.5f,roadY, 3.5f,      0.0f, 0.0f,
                -5f, roadY, 3.5f,     0.0f, 1.0f
        };

        int roadIndices[] = {
                0, 1, 3,
                1, 2, 3
        };

        var road = new Model(roadVertices,roadIndices,new int[]{3,2});
        road.render();
        allModels.add(road);

        var roadTexture=new Texture("src/main/resources/textures/jordan-holbrook-rox.jpg");

        var lightColor = new Vector3f(1,1,1f);
        var diffuseColor = lightColor.mul(new Vector3f(0.7f));
        var ambientColor = diffuseColor.mul(new Vector3f(0.2f));

        /*Лампочки в передних фарах*/
        var lightBulbVertices = createEllipse(0,0,0.125f,0.085f,25);
        var lightBulb = new Model(
                ArrayUtils.toPrimitive(lightBulbVertices.toArray(new Float[0]),0),
                null,
                new int[]{3});
        lightBulb.render();

        Matrix4f projection;
        Matrix4f view;
        Matrix4f mat;

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_AUTO_NORMAL);
        while(!glfwWindowShouldClose(window.getWindowID()))
        {

            float currentFrame = (float) glfwGetTime();
            camera.deltaTime = currentFrame - camera.lastFrame;
            camera.lastFrame = currentFrame;
            camera.processInput(window.getWindowID());
            glClearColor(0.53f, 0.68f, 0.73f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            view = new Matrix4f().lookAt(
                    camera.getCameraPos(),
                    new Vector3f(
                            camera.getCameraPos().x+camera.getCameraFront().x,
                            camera.getCameraPos().y+camera.getCameraFront().y,
                            camera.getCameraPos().z+camera.getCameraFront().z),
                    camera.getCameraUp());
            projection = new Matrix4f().perspective(camera.fov, (float)window.getWidth() / (float)window.getHeight(), 0.1f, 100.0f);

            glUseProgram(mainShader.getProgramID());
            mainShader.setVec3("viewPos", camera.getCameraPos());
            mainShader.setUniform("view",view);
            mainShader.setUniform("projection",projection);

            /*
            * Указываем параметры 6 точечных источников света */
            mainShader.setVec3("pointLights[0].position", new Vector3f(-4,1,1f));
            mainShader.setFloat("pointLights[0].constant", 1.0f);
            mainShader.setFloat("pointLights[0].linear", 0.09f);
            mainShader.setFloat("pointLights[0].quadratic", 0.032f);
            mainShader.setVec3("pointLights[0].ambient", ambientColor);
            mainShader.setVec3("pointLights[0].diffuse", diffuseColor);
            mainShader.setVec3("pointLights[0].specular", new Vector3f(1.0f, 1.0f, 1.0f));

            mainShader.setVec3("pointLights[1].position", new Vector3f(0.5f,0f,1f));
            mainShader.setFloat("pointLights[1].constant", 1.0f);
            mainShader.setFloat("pointLights[1].linear", 0.09f);
            mainShader.setFloat("pointLights[1].quadratic", 0.032f);
            mainShader.setVec3("pointLights[1].ambient", ambientColor);
            mainShader.setVec3("pointLights[1].diffuse", diffuseColor);
            mainShader.setVec3("pointLights[1].specular", new Vector3f(1.0f, 1.0f, 1.0f));

            mainShader.setVec3("pointLights[2].position", new Vector3f(-2f,0.5f,2.5f));
            mainShader.setFloat("pointLights[2].constant", 1.0f);
            mainShader.setFloat("pointLights[2].linear", 0.09f);
            mainShader.setFloat("pointLights[2].quadratic", 0.032f);
            mainShader.setVec3("pointLights[2].ambient", ambientColor);
            mainShader.setVec3("pointLights[2].diffuse", diffuseColor);
            mainShader.setVec3("pointLights[2].specular", new Vector3f(1.0f, 1.0f, 1.0f));

            mainShader.setVec3("pointLights[3].position", new Vector3f(-2f,0.5f,-0.5f));
            mainShader.setFloat("pointLights[3].constant", 1.0f);
            mainShader.setFloat("pointLights[3].linear", 0.09f);
            mainShader.setFloat("pointLights[3].quadratic", 0.032f);
            mainShader.setVec3("pointLights[3].ambient", ambientColor);
            mainShader.setVec3("pointLights[3].diffuse", diffuseColor);
            mainShader.setVec3("pointLights[3].specular", new Vector3f(1.0f, 1.0f, 1.0f));

            mainShader.setVec3("pointLights[4].position", new Vector3f(-2.3f,1f,1f));
            mainShader.setFloat("pointLights[4].constant", 1.0f);
            mainShader.setFloat("pointLights[4].linear", 0.09f);
            mainShader.setFloat("pointLights[4].quadratic", 0.032f);
            mainShader.setVec3("pointLights[4].ambient", ambientColor);
            mainShader.setVec3("pointLights[4].diffuse", diffuseColor);
            mainShader.setVec3("pointLights[4].specular", new Vector3f(1.0f, 1.0f, 1.0f));

            mainShader.setVec3("pointLights[5].position", new Vector3f(0f,1f,1f));
            mainShader.setFloat("pointLights[5].constant", 1.0f);
            mainShader.setFloat("pointLights[5].linear", 0.09f);
            mainShader.setFloat("pointLights[5].quadratic", 0.032f);
            mainShader.setVec3("pointLights[5].ambient", ambientColor);
            mainShader.setVec3("pointLights[5].diffuse", diffuseColor);
            mainShader.setVec3("pointLights[5].specular", new Vector3f(1.0f, 1.0f, 1.0f));

            /*
            * Указываем параметры направленного источника света*/
            mainShader.setVec3("dirLight.direction", new Vector3f(-2f, 1.0f, 1f));
            mainShader.setVec3("dirLight.ambient",ambientColor);
            mainShader.setVec3("dirLight.diffuse", diffuseColor);
            mainShader.setVec3("dirLight.specular", new Vector3f(0.5f, 0.5f, 0.5f));

            /*
            * Указываем параметры материала объекта
            * При описании поверхности мы можем определить цвет материала для каждого из 3 компонентов освещения:
            *                                                   окружающего, рассеянного и зеркального освещения.
            * Указывая цвет для каждого из компонентов, мы получаем точный контроль над выводом цвета поверхности
            * Также можно указать прозрачность объекта*/
            mainShader.setVec3("material.ambient", new Vector3f(	   0.105882f, 0.058824f, 0.113725f));
            mainShader.setVec3("material.diffuse", new Vector3f( 0.427451f, 0.470588f, 0.541176f));
            mainShader.setVec3("material.specular", new Vector3f(0.333333f, 0.333333f, 0.521569f));
            mainShader.setFloat("material.shininess", 32);
            mainShader.setFloat("transparency", 1f);
            mainShader.setFloat("isSmoothVertex",0f);

            roughCorpus.bind();
            /*
            * По сути машина симметрична, поэтому отзеркаливаем имеющуюся часть*/
            for ( int i = 0; i <corpusPosition.length ; i++)
            {
                mat = new Matrix4f().translate(corpusPosition[i]);
                if(i==0)
                    mat = mat.scale(new Vector3f(-1,1,1));
                else
                    mat = mat.rotate((float) Math.PI,new Vector3f(0,1,0));
                mat = mat.rotate(-0.015f,new Vector3f(0,0,1));
                mainShader.setUniform("model",mat);
                roughCorpus.draw();
            }

            /*Гладкие поверхности корпуса*/

            mainShader.setFloat("isSmoothVertex",1f);

            smoothCorpus.bind();
            for ( int i = 0; i <corpusPosition.length ; i++)
            {
                mat = new Matrix4f().translate(corpusPosition[i]);
                if(i==0)
                    mat = mat.scale(new Vector3f(-1,1,1));
                else
                    mat = mat.rotate((float) Math.PI,new Vector3f(0,1,0));
                mat = mat.rotate(-0.015f,new Vector3f(0,0,1));
                mainShader.setUniform("model",mat);
                smoothCorpus.draw();
            }

            spoiler.bind();
            mat = new Matrix4f().translate(new Vector3f(0.6f,-8.3f,1f)).rotateX(1.45f).rotateY(1.65f);
            mainShader.setUniform("model",mat);
            spoiler.draw();

            /*Пытался подогнать крылья по координатам колес*/
            backFender.bind();
            for ( int i = 0; i <wheelsPositions.length ; i++)
            {
                if(i<2)
                    continue;
                if(i%2==0)
                    mat = new Matrix4f().translate(new Vector3f(wheelsPositions[i].x,wheelsPositions[i].y,wheelsPositions[i].z-0.085f));
                else
                    mat = new Matrix4f().translate(new Vector3f(wheelsPositions[i].x,wheelsPositions[i].y,wheelsPositions[i].z+0.0335f));
                mat = mat.rotateZ(0.06f);
                mainShader.setUniform("model",mat);
                backFender.draw();
            }

            forwardFender.bind();
            for ( int i = 0; i <wheelsPositions.length ; i++)
            {
                if(i>1)
                    break;
                if(i%2==0)
                    mat = new Matrix4f().translate(new Vector3f(wheelsPositions[i].x,wheelsPositions[i].y,wheelsPositions[i].z-0.085f));
                else
                    mat = new Matrix4f().translate(new Vector3f(wheelsPositions[i].x,wheelsPositions[i].y,wheelsPositions[i].z+0.0335f));
                mat = mat.rotateZ(-0.155f);
                mainShader.setUniform("model",mat);
                forwardFender.draw();
            }

            /*Выхлопная труба из 2 цилиндров*/
            mainShader.setVec3("material.ambient", new Vector3f(	   0.19225f, 0.19225f, 0.19225f));
            mainShader.setVec3("material.diffuse", new Vector3f(  0.50754f, 0.50754f, 0.50754f));
            mainShader.setVec3("material.specular", new Vector3f(0.508273f, 0.508273f, 0.508273f));
            mainShader.setFloat("material.shininess",  32f);

            exhaustPipe.bind();
            mat = new Matrix4f().translate(new Vector3f(-0.3f,0.075f,1.5f)).rotateY(1.55f).rotateX(0.295f);
            mainShader.setUniform("model",mat);
            exhaustPipe.draw();

            pipe.bind();
            mat = new Matrix4f().translate(new Vector3f(0.11f,-0.073f,1.51f)).rotateY(1.55f);
            mainShader.setUniform("model",mat);
            pipe.draw();

            mainShader.setVec3("material.ambient", new Vector3f(	  0f));
            mainShader.setVec3("material.diffuse", new Vector3f( 0.01f, 0.01f, 0.01f));
            mainShader.setVec3("material.specular", new Vector3f(0.50f));
            mainShader.setFloat("material.shininess",  32f);
            salon.bind();
            for ( int i = 0; i <corpusPosition.length ; i++)
            {
                mat = new Matrix4f().translate(corpusPosition[i]);
                if(i==0)
                    mat = mat.scale(new Vector3f(-1,1,1));
                else
                    mat = mat.rotate((float)Math.PI,new Vector3f(0,1,0));
                mat = mat.rotate(-0.015f,new Vector3f(0,0,1));
                mainShader.setUniform("model",mat);
                salon.draw();
            }


            mainShader.setVec3("material.specular", new Vector3f(0f));
            wheel.bind();
            for ( int i = 0; i <wheelsPositions.length ; i++)
            {
                mat = new Matrix4f().translate(wheelsPositions[i]);
                if(i%2==0)
                    mat = mat.rotate((float)Math.PI,new Vector3f(0,1,0));
                mainShader.setUniform("model", mat);
                wheel.draw();
            }

            forwardHole.bind();
            mat = new Matrix4f().translate(-4.405f,-0.1f,-3.39f).rotateY((float) Math.PI/2);
            mainShader.setUniform("model",mat);
            forwardHole.draw();

            steeringWheel.bind();
            mat = new Matrix4f().translate(-2.5f,0.5f,0.595f).rotateY(-1.6f);
            mainShader.setUniform("model",mat);
            steeringWheel.draw();

            mainShader.setVec3("material.ambient", new Vector3f( 1.0f));
            mainShader.setVec3("material.diffuse", new Vector3f(0.55f));
            mainShader.setVec3("material.specular", new Vector3f(0.70f));
            lightBulb.bind();
            for(var e = 0; e<2; e++){
                float z;
                if(e==0)
                    z=0.32f;
                else
                    z= 1.69f;
                mat = new Matrix4f().translate(-4.275f,0.38f,z).rotateY((float) Math.PI/2);
                mainShader.setUniform("model",mat);
                lightBulb.draw();
            }

            /*
            эти режимы очень затормаживают вывод изображения, поэтому их не рекомендуют устанавливать глобально.
            Я выделил объект, который требуется этот режим и включаю и отключаю его
            */

            /*Для получения требуемого эффекта прозрачности нужно разрешить наложение цветов*/
            glEnable(GL_BLEND);
            /*Устанавливаем алгоритм, по которуму будут смешиваться два цвета*/
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

            mainShader.setVec3("material.ambient", new Vector3f(0.0f,0.05f,0.05f));
            mainShader.setVec3("material.diffuse", new Vector3f(0.4f,0.5f,0.5f));
            mainShader.setVec3("material.specular", new Vector3f(0.04f,0.7f,0.7f));
            mainShader.setFloat("material.shininess", 32.0f);
            mainShader.setFloat("transparency", 0.75f);
            windows.bind();
            for ( int i = 0; i <corpusPosition.length ; i++)
            {
                mat = new Matrix4f().translate(corpusPosition[i]);
                if(i==0)
                    mat = mat.scale(new Vector3f(-1,1,1));
                else
                    mat = mat.rotate((float)Math.PI,new Vector3f(0,1,0));
                mat = mat.rotate(-0.015f,new Vector3f(0,0,1));
                mainShader.setUniform("model", mat);
                windows.draw();
            }

            glDisable(GL_BLEND);

            mainShader.setVec3("material.ambient", new Vector3f( 0.1745f, 0.01175f, 0.01175f));
            mainShader.setVec3("material.diffuse", new Vector3f(0.61424f, 0.04136f, 0.04136f));
            mainShader.setVec3("material.specular", new Vector3f(0.727811f, 0.626959f, 0.626959f));
            mainShader.setFloat("material.shininess", 32.0f);
            mainShader.setFloat("transparency", 0.95f);
            backLight.bind();
            for ( int i = 0; i <corpusPosition.length ; i++)
            {
                if(i==0) {
                    mat = new Matrix4f().translate(new Vector3f(0.15f,0.35f,0.35f));
                    mat = mat.scale(new Vector3f(-1, 1, 1));
                }
                else {
                    mat = new Matrix4f().translate(new Vector3f(0.15f,0.35f,1.65f));
                    mat = mat.rotate((float)Math.PI, new Vector3f(0, 1, 0));
                }
                mat = mat.rotate(-0.015f,new Vector3f(0,0,1));
                mat = mat.rotateY(1.575f);
                mainShader.setUniform("model",mat);
                backLight.draw();
            }

            glUseProgram(textureShader.getProgramID());
            textureShader.setUniform("view", view);
            textureShader.setUniform("projection",projection);
            diskTexture.bind();
            disk.bind();
            for ( int i = 0; i <wheelsPositions.length ; i++)
            {
                mat = new Matrix4f().translate(wheelsPositions[i]);
                if(i%2==0)
                    mat = mat.rotate((float)Math.PI,new Vector3f(0,1,0));
                textureShader.setUniform("model", mat);
                disk.draw();
            }

            glUseProgram(textureShader.getProgramID());
            textureShader.setUniform("view", view);
            textureShader.setUniform("projection",projection);
            roadTexture.bind();
            road.bind();

            mat = new Matrix4f().rotate(-0.015f,new Vector3f(0,0,1));;
            textureShader.setUniform("model", mat);
            road.draw();

            glfwSwapBuffers(window.getWindowID());
            glfwPollEvents();
        }
        allModels.forEach(x->x.clear());

        glfwTerminate();
    }


    private static ArrayList<Float> createTube(float radius, float height, int elementCount, double circumference) {
        var res = new ArrayList<Float>();
        var f = 0.0;
        var counter = 0;

        /*ограничиваем длину окружности трубы*/
        while (f<=circumference) {

            if(res.stream().count()>=9){
                for(var e = 0; e<6; e++)
                    res.add(res.get((int)res.stream().count()-6));
            }

            res.add((float) Math.cos(f) * radius);
            res.add((float) Math.sin(f) * radius);
            res.add(counter % 2 == 0 ? 0 : height);

            counter++;
            f += (2 * Math.PI / (float) elementCount);
        }

        return res;
    }

    private static Vector3f getPoint(float[] v, int numberPoint){
        return new Vector3f(v[numberPoint],v[numberPoint+1],v[numberPoint+2]);
    }

    public static LinkedList<Float> createEllipse(float cx, float cy, float rx, float ry, int num_segments)
    {
        var res = new LinkedList<Float>();
        float theta =(float)( 2 * Math.PI / (float)num_segments);
        float c = (float) Math.cos(theta);
        float s = (float) Math.sin(theta);
        float t;

        float x = 1;
        float y = 0;

        for(int ii = 0; ii <= num_segments+1; ii++)
        {
            if(res.stream().count()>=9){

                for (var e = 0; e < 3; e++) {
                    res.add(res.get((int)res.stream().count()-3));
                }
                res.add(cx);
                res.add(cy);
                res.add(0.0f);
                res.add(x * rx + cx);
                res.add(y * ry + cy);
                res.add(0.0f);
            }
            else {
                if(ii%3==0){
                    res.add(cx);
                    res.add(cy);
                    res.add(0.0f);
                }
                else {
                    res.add(x * rx + cx);
                    res.add(y * ry + cy);
                    res.add(0.0f);
                }
            }

            t = x;
            x = c * x - s * y;
            y = s * t + c * y;
        }
        return res;
    }

    private static float[] createCylinder(float radius, float height, int elementCount, double circumference, float thickness){

        /*
        * Создаются две трубы на заданном растоянии друг от друга
        * затем они соединяются треугольниками в цельный цилиндр
        * */
        ArrayList<Float> circle1 = createTube(radius, height, elementCount, circumference);
        ArrayList<Float> circle2 = createTube(radius-thickness, height, elementCount, circumference);

        var resCircle = new LinkedList<Float>();
        for(var e = 0;e<circle1.stream().count()/9; e++){
            var startFirstInd = e*9;
            var startSecondInd = e*9+6;

            addElementsInList(resCircle,circle2,circle1,startFirstInd,startSecondInd);
            addElementsInList(resCircle,circle1,circle2,startSecondInd,startFirstInd);
        }

        for(var e=6;e>0;e--){
            resCircle.add(circle1.get((int)circle1.stream().count()-e));
        }

        resCircle.add(circle2.get((int)circle2.stream().count()-3));
        resCircle.add(circle2.get((int)circle2.stream().count()-2));
        resCircle.add(circle2.get((int)circle2.stream().count()-1));

        for(var e=6;e>0;e--){
            resCircle.add(circle2.get((int)circle2.stream().count()-e));
        }

        resCircle.add(circle1.get((int)circle1.stream().count()-6));
        resCircle.add(circle1.get((int)circle1.stream().count()-5));
        resCircle.add(circle1.get((int)circle1.stream().count()-4));

        for(var e=0;e<6;e++){
            resCircle.add(circle1.get(e));
        }

        resCircle.add(circle2.get(0));
        resCircle.add(circle2.get(1));
        resCircle.add(circle2.get(2));

        for(var e=0;e<6;e++){
            resCircle.add(circle2.get(e));
        }

        resCircle.add(circle1.get(3));
        resCircle.add(circle1.get(4));
        resCircle.add(circle1.get(5));

        resCircle.addAll(circle1);
        resCircle.addAll(circle2);

        return ArrayUtils.toPrimitive(resCircle.toArray(new Float[0]),0.0f);
    }

    private static void addElementsInList(LinkedList<Float> resultList,
                                          List<Float> listGivingOneElement,
                                          List<Float> listGivingTwoElements,
                                          int firstStartIndex,
                                          int secondStartIndex){

        var x1Index = firstStartIndex;
        var y1Index = firstStartIndex+1;
        var z1Index = firstStartIndex+2;

        var x2Index = secondStartIndex;
        var y2Index = secondStartIndex+1;
        var z2Index = secondStartIndex+2;

        resultList.add(listGivingTwoElements.get(x1Index));
        resultList.add(listGivingTwoElements.get(y1Index));
        resultList.add(listGivingTwoElements.get(z1Index));

        resultList.add(listGivingTwoElements.get(x2Index));
        resultList.add(listGivingTwoElements.get(y2Index));
        resultList.add(listGivingTwoElements.get(z2Index));

        resultList.add(listGivingOneElement.get(x1Index));
        resultList.add(listGivingOneElement.get(y1Index));
        resultList.add(listGivingOneElement.get(z1Index));
    }

    public static float[] findNormals(float[] v){
        var result = new ArrayList<Float>();

        for(var e = 0; e<=v.length-9;e+=9){
            var p0 = getPoint(v,e);
            var p1 = getPoint(v,e+3);
            var p2 = getPoint(v,e+6);
            var list = Arrays.asList(p0,p1,p2);
            var exn1 = new Vector3f(p1.x-p0.x,p1.y-p0.y,p1.z-p0.z);
            var exn2 = new Vector3f(p2.x-p1.x,p2.y-p1.y,p2.z-p1.z);

            var n = exn1.cross(exn2).normalize();

            for(var p=0;p<list.stream().count();p++){

                result.add(list.get(p).x);
                result.add(list.get(p).y);
                result.add(list.get(p).z);
                result.add(n.x);
                result.add(n.y);
                result.add(n.z);
            }
        }
        return ArrayUtils.toPrimitive(result.toArray(new Float[0]), 0.0F);
    }

}
