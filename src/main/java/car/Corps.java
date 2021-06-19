package car;

public class Corps {
    public static float[] getOnlySmoothCorpusVertices = new float[]{

            /*Нижняя часть капота*/
            4.4f, 0.3f, 0.0f, //14
            4.4f, 0.3f, 1,//25
            4.4f, 0.0f, 1,//26

            4.4f, 0.3f, 0.0f, //14
            4.4f, 0.0f, 0.0f, //15
            4.4f, 0.0f, 1,//26
            /*-------------*/

            /*Участок между капотом и крылом*/
            3.9f, 0.5f, 0.0f,  //12
            3.8f, 0.3f, 0.0f, //13
            4.4f, 0.3f, 0.0f, //14

            3.8f, 0.3f, 0.0f, //13
            4.4f, 0.3f, 0.0f, //14
            4.4f, 0.0f, 0.0f, //15

            3.8f, 0.3f, 0.0f, //13
            4.4f, 0.0f, 0.0f, //15
            3.9f, 0.0f, 0.0f,  //16

            3.15f, 0.5f, 0.005f,  //10
            3.15f, 0.3f, 0.0f, //11
            3.9f, 0.5f, 0.0f,  //12

            3.15f, 0.5f, 0.005f,  //10
            3.9f, 0.5f, 0.0f,  //12
            3.9f, 0.3f, 0.0f, //13
            /*-------------*/

            /*Капот*/
            3.9f, 0.5f, 0.0f,//21
            2.85f, 0.65f, 1,//28
            3.9f, 0.5f, 1,//29

            2.85f, 0.65f, 0.3f,//20
            3.9f, 0.5f, 0.0f,//21
            2.85f, 0.65f, 1,//28
            /*-------------*/

            /*Скос с копота на крышу*/
            2.25f, 1.0f, 0.4f,//18
            2.95f, 0.525f, 0.125f,//33
            2.95f, 0.575f, 0.25f,//34

            2.25f, 1.0f, 0.4f,//18
            2.95f, 0.525f, 0.125f,//33
            2.0f, 1.0f, 0.4f, //35
            /*--------------*/

            /*Крыша*/
            2.25f, 1.0f, 0.4f,//18
            1.3f, 1.0f, 1,//30
            2.25f, 1.0f, 1,//31

            1.3f, 1.0f, 0.35f,//17
            2.25f, 1.0f, 0.4f,//18
            1.3f, 1.0f, 1,//30
            /*----------------*/

            /*Скос с крыши на конец двери*/
            1.3f, 1.0f, 0.35f,//17
            1.51f, 1.0f, 0.375f,//40
            1.275f, 0.5f, 0.0f, //41

            1.3f, 1.0f, 0.35f,//17
            1.275f, 0.5f, 0.0f, //41
            0.95f, 0.5f, 0.0f, //42
            /*--------------*/

            /*Вертикальныя часть багажника*/
            -0.15f, 0.0f, 0.0f, //0
            -0.15f, 0.5f, 0.00f, //2
            -0.15f, 0.0f, 1,//22

            -0.15f, 0.5f, 0.00f, //2
            -0.15f, 0.0f, 1,//22
            -0.15f, 0.5f, 1,//23
            /*-----------*/

            /*Скос на багажнике,от окна до спойлера*/
            0.5f, 0.5f, 0.0f,  //3
            0.625f, 0.6f, 0.2f,//45
            -0.15f, 0.5f, 0.0f,//46

            0.5f, 0.5f, 0.0f,  //3
            0.7f, 0.6f, 0.075f,//43
            0.45f, 0.5f, 0.15f, //38

            0.7f, 0.6f, 0.075f,//43
            0.45f, 0.5f, 0.15f, //38
            0.625f, 0.6f, 0.2f,//45
            /*------------*/

            /*заднее крыло*/
            -0.15f, 0.0f, 0.0f, //0
            0.5f, 0.0f, 0.0f, //1
            -0.15f, 0.5f, 0.00f, //2
            /*-------------*/

            /*Предние фары*/
            4.315f, 0.325f, 0.175f, //49
            4.315f, 0.55f, 0.175f,//50
            4f, 0.45f, 0.175f, //51

            4.315f, 0.325f, 0.45f, //52
            4.315f, 0.55f, 0.45f,//53
            4f, 0.45f, 0.45f, //54


            4f, 0.45f, 0.175f, //51
            4.315f, 0.55f, 0.45f,//53
            4f, 0.45f, 0.45f, //54

            4f, 0.45f, 0.175f, //51
            4.315f, 0.55f, 0.175f,//53
            4.315f, 0.55f,0.45f, //54
            /*--------------*/
    };


    public static float[] onlyCorpusVertices = new float[]{

            /*Боковые зеркала*/
            2.9f,0.56f,0.175f,
            2.725f,0.625f,0.1f,
            2.725f,0.68f,0.1f,

            2.9f,0.56f,0.175f,
            2.725f,0.625f,0,
            2.725f,0.68f,0,

            2.9f,0.56f,0.175f,
            2.725f,0.625f,0.1f,
            2.725f,0.625f,0,

            2.9f,0.56f,0.175f,
            2.725f,0.68f,0.1f,
            2.725f,0.68f,0,
            /*--------*/

            /*Участок капота на котором находятся фары*/
            4.4f, 0.3f, 0.0f, //14
            4.4f, 0.3f, 1,//25
            3.9f, 0.5f, 1,//27

            3.9f, 0.5f, 0.0f,  //12
            4.4f, 0.3f, 0.0f, //14
            3.9f, 0.5f, 1,//27
            /*---------------*/

            /*Скос от капота к крылу*/
            2.85f, 0.5f, 0.0f,//19
            2.85f, 0.65f, 0.3f,//20
            3.9f, 0.5f, 0.0f,//21
            /*-------------*/

            /*Задняя часть, багажник*/
            0.5f, 0.0f, 0.0f, //1
            -0.15f, 0.5f, 0.00f, //2
            0.5f, 0.5f, 0.0f,  //3

            0.625f, 0.6f, 0.2f,//45
            -0.15f, 0.5f, 0.0f,//46
            -0.15f, 0.5f, 1,//48

            0.625f, 0.6f, 0.2f,//45
            -0.15f, 0.5f, 1,//48
            0.625f, 0.6f, 1,//47
            /*-----------*/

            /*Боковая часть*/
            0.5f, 0.15f, 0.0f, //4
            0.5f, 0.5f, 0.00f, //5
            1.25f, 0.5f, 0.0f, //6

            0.5f, 0.5f, 0.00f,  //5
            1.25f, 0.5f, 0.0f,//6
            1.25f, 0.15f, 0.0f,//7

            1.25f, 0.0f, 0.0f, //8
            3.15f, 0.0f, 0.0f,  //9
            3.15f, 0.5f, 0.005f,  //10

            1.25f, 0.5f, 0.0f,//6
            1.25f, 0.0f, 0.0f, //8
            3.15f, 0.5f, 0.005f,  //10

            2.85f, 0.5f, 0.0f,//19
            1.0f, 0.5f, 0.0f,//36
            2.9f, 0.575f, 0.175f,//37

            0.5f, 0.5f, 0.0f,  //3
            0.7f, 0.6f, 0.075f,//43
            2.0f, 0.5f, 0.0f, //44

            0.45f, 0.5f, 0.25f,//32
            0.45f, 0.5f, 0.15f, //38
            1.3f, 1.0f, 0.35f,//17

             0.45f, 0.5f, 0.25f,//32
            1.3f, 1.0f, 0.35f,//17
            1.3f, 1.0f, 0.45f,//39
            /*-------------------*/

            /*крепление на багажник для спойлера*/
            0f,0.615f,0.4f,
            0f,0.5f,0.25f,
            0.5f,0.5f,0f,

            0f,0.615f,0.4f,
            0f,0.5f,0.35f,
            0.5f,0.5f,0.125f,

            0f,0.615f,0.4f,
            0f,0.5f,0.35f,
            0f,0.5f,0.25f,

            0f,0.615f,0.4f,
            0.5f,0.5f,0f,
            0.5f,0.5f,0.125f,
            /*------------*/

            /*Выпуклость на капоте*/
            3.9f, 0.525f, 0.7f,//21
            2.85f, 0.675f, 1,//28
            3.9f, 0.525f, 1,//29

            2.85f, 0.675f, 0.55f,//20
            3.9f, 0.525f, 0.7f,//21
            2.85f, 0.675f, 1,//28

            4.4f, 0.3f, 0.8f, //14
            4.4f, 0.3f, 1,//25
            3.9f, 0.525f, 1,//27

            3.9f, 0.525f, 0.7f,  //12
            4.4f, 0.3f, 0.8f, //14
            3.9f, 0.525f, 1,//27

            3.9f, 0.525f, 0.7f,//21
            3.9f, 0.5f, 0.675f,//21
            2.85f, 0.65f, 0.525f,//20

            2.85f, 0.65f, 0.525f,//20
            3.9f, 0.525f, 0.7f,//21
            2.85f, 0.675f, 0.55f,//28

            3.9f, 0.525f, 0.7f,//21
            3.9f, 0.5f, 0.675f,//21
            4.4f, 0.3f, 0.8f, //14

            /*-----------*/
    };

    public static float[] salonVertices = new float[]{
            /*основа*/
            -0.1f,0.35f,0.01f,
            2.65f,0.35f,0.01f,
           -0.1f,0.35f,1,

            2.65f,0.35f,1,
            -0.1f,0.35f,1,
            2.65f,0.35f,0.01f,

            2.65f,0.35f,0.01f,
            2.65f,0.35f,0.5f,
            2.85f,0.65f,0.3f,

            2.85f,0.65f,0.3f,
            2.65f,0.35f,0.5f,
            2.85f,0.65f,1,

            2.65f,0.35f,1,
            2.85f,0.65f,1,
            2.65f,0.35f,0.5f,

            0.625f,0.6f,0.2f,
            0.8f,0.35f,0.01f,
            0.8f,0.35f,1,

            0.625f,0.6f,0.2f,
            0.8f,0.35f,1,
            0.625f,0.6f,1,

            /*сидение*/

            1.75f,0.35f,0.4f,
            1.75f,0.85f,0.4f,
            1.5f,0.85f,0.4f,

            1.5f,0.35f,0.4f,
            1.75f,0.35f,0.4f,
            1.5f,0.85f,0.4f,

            1.75f,0.35f,0.4f,
            1.75f,0.85f,0.4f,
            1.75f,0.85f,0.85f,

            1.75f,0.85f,0.85f,
            1.75f,0.35f,0.85f,
            1.75f,0.35f,0.4f,

            1.5f,0.35f,0.4f,
            1.5f,0.85f,0.4f,
            1.5f,0.85f,0.85f,

            1.5f,0.85f,0.85f,
            1.5f,0.35f,0.85f,
            1.5f,0.35f,0.4f,

            1.75f,0.35f,0.85f,
            1.75f,0.85f,0.85f,
            1.5f,0.85f,0.85f,

            1.5f,0.35f,0.85f,
            1.75f,0.35f,0.85f,
            1.5f,0.85f,0.85f,

    };

}
