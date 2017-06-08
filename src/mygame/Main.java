package mygame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.UrlLocator;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Main extends SimpleApplication implements ActionListener, PhysicsCollisionListener, AnimEventListener {

    public static void main(String[] args) {
        Main app = new Main();
        app.showSettings = false;
        app.start();
        
    }
    

    private BulletAppState bulletAppState;
    private Material mat;
    private Sphere bullet;
    private SphereCollisionShape bulletCollisionShape;
    private Spatial tree,elefante;
    private PlayerCameraNode player;
    private ArrayList<PlayerCameraNode> players = new ArrayList();
    private ArrayList recordes = new ArrayList();
    private float incrementoZ = 10;
    private int andar = 7;
    private int tiros = 55;
    private AudioNode audioSource;
    private AudioNode nature;
    private boolean pausar = false;
    private boolean isRunning = true;
    private ActionListener pauseActionListener;
    private AnalogListener pauseAnalogListener;
    private int otos=50;
    private int op=1, max=4;
    private long tempo;
  
    public Main() {
        this.pauseAnalogListener = new AnalogListener() {
            @Override
            public void onAnalog(String name, float value, float tpf) {
                if (isRunning) {

                } else {
                    if(pausar != true)
                        Menu();
                }
            }
        };
        this.pauseActionListener = new ActionListener() {
            @Override
            public void onAction(String name, boolean keyPressed,
                    float tpf) {
                if (name.equals("Pause") && !keyPressed) {
                    isRunning = !isRunning;
                    if(pausar != true)
                        Menu();
                }
            }
        };
    }

    @Override
    public void simpleInitApp() {
        
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        //bulletAppState.setDebugEnabled(true);
        bulletAppState.getPhysicsSpace().addCollisionListener(this);
        viewPort.setBackgroundColor(ColorRGBA.DarkGray);
        
        
        rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", false));
        
        /** An unshaded textured cube. 
         *  Uses texture from jme3-test-data library! */ 
        Box boxMesh = new Box(1f,8f,100f); 
        Geometry boxGeo = new Geometry("A Textured Box", boxMesh); 
        Material boxMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md"); 
        Texture monkeyTex = assetManager.loadTexture("Textures/floresta1.jpg"); 
        boxMat.setTexture("ColorMap", monkeyTex); 
        boxGeo.setMaterial(boxMat); 
        boxGeo.setLocalTranslation(-25, 0, 0);
        rootNode.attachChild(boxGeo); 
        
         Box boxMesh1 = new Box(1f,8f,100f); 
        Geometry boxGeo1 = new Geometry("A Textured Box", boxMesh1); 
        //Material boxMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md"); 
        //Texture monkeyTex1 = assetManager.loadTexture("Interface/Logo/Monkey.jpg"); 
        boxMat.setTexture("ColorMap", monkeyTex); 
        boxGeo1.setMaterial(boxMat); 
        boxGeo1.setLocalTranslation(25, 0, 0);
        rootNode.attachChild(boxGeo1); 
        
        Box boxMesh2 = new Box(25f,15f,1f); 
        Geometry boxGeo2 = new Geometry("A Textured Box", boxMesh2); 
        Material boxMat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md"); 
        Texture monkeyTex1 = assetManager.loadTexture("Interface/Logo/Monkey.jpg"); 
        boxMat.setTexture("ColorMap", monkeyTex); 
        boxGeo2.setMaterial(boxMat); 
        boxGeo2.setLocalTranslation(0, 0, -90);
        rootNode.attachChild(boxGeo2); 
        
        elefante = (Node) assetManager.loadModel("Models/Elephant/Elephant.mesh.xml");
        elefante.setLocalTranslation(-18, -2.5f, -5);
        elefante.setLocalScale(0.05f);
        //terreno.setLocalScale(1, 1.5f, 1.5f);
        rootNode.attachChild(elefante);

        
        //som tiro pode substituir por War.wav
        audioSource = new AudioNode(assetManager, "Sound/Effects/Gun.wav");
        audioSource.setVolume(4);
 
        //som ambiente
        nature = new AudioNode(assetManager, "Sound/Environment/Nature.ogg", true);
        nature.setPositional(false);
        nature.setVolume(2);
        nature.play();
        
        createBala();
        createLigth();
        createCubo();
        createTree();
        createPlayer();
        tempo = System.currentTimeMillis();
       

    }

    @Override
    public void simpleUpdate(float tpf) {
        

         initCrossHairs();
        
        incrementoZ = incrementoZ + andar;

        
            if (players.size() < max) {
                
                if(otos > (max -1))
                createPlayer();
                
            }
        
        
        for (PlayerCameraNode p : players) {
            p.upDateKeys(tpf, true,andar);
        }


            guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
            BitmapText helloText = new BitmapText(guiFont, false);
            helloText.setSize(guiFont.getCharSet().getRenderedSize());
            helloText.setText("Tiros: " + tiros + "");
            helloText.setLocalTranslation(500, 450, 0);
            helloText.setColor(ColorRGBA.White);
            guiNode.attachChild(helloText);
            
            guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
            BitmapText helloText1 = new BitmapText(guiFont, false);
            helloText1.setSize(guiFont.getCharSet().getRenderedSize());
            helloText1.setText("Otos restantes: " + otos + "");
            helloText1.setLocalTranslation(100, 450, 0);
            helloText1.setColor(ColorRGBA.White);
            guiNode.attachChild(helloText1);
            
         gameover();
         ganhou();
        
       
    }

    private void createBala() {
        bullet = new Sphere(32, 32, 0.1f, true, true);
        bullet.setTextureMode(Sphere.TextureMode.Projected);

        bulletCollisionShape = new SphereCollisionShape(0.1f);
        setupKeys();

        mat = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", ColorRGBA.DarkGray);
    }

    private void createCubo() {

        Box boxMesh = new Box(15f, 0.1f, 100f);
        Geometry boxGeo = new Geometry("A Textured Box", boxMesh);
        Material boxMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture monkeyTex = assetManager.loadTexture("Textures/pedra.jpg");
        boxMat.setTexture("ColorMap", monkeyTex);
        boxGeo.setMaterial(boxMat);
        boxGeo.setLocalTranslation(0, -4, 0);
        rootNode.attachChild(boxGeo);

        RigidBodyControl boxPhysicsNode = new RigidBodyControl(0);
        boxGeo.addControl(boxPhysicsNode);
        bulletAppState.getPhysicsSpace().add(boxPhysicsNode);
        
       
    }

    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }

    private void setupKeys() {
        inputManager.addMapping("shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("Pause", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addListener(this, "shoot");
        inputManager.addListener(pauseActionListener, new String[]{"Pause"});

    }

    private void createLigth() {

//        DirectionalLight l3 = new DirectionalLight();
//        l3.setDirection(new Vector3f(-0.01f, 0, -0.02f));
//        rootNode.addLight(l3);
//
//        DirectionalLight l4 = new DirectionalLight();
//        l4.setDirection(new Vector3f(0.01f, 0, 0.02f));
//        rootNode.addLight(l4);
    /** A white, spot light source. */ 
    PointLight lamp = new PointLight();
     lamp.setPosition(Vector3f.ZERO);
    lamp.setColor(ColorRGBA.White);
    rootNode.addLight(lamp); 
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    @Override
    public void onAction(String name, boolean value, float tpf) {
        if(tiros > 0)
        {
            if (name.equals("shoot") && !value) {
                Geometry bulletg = new Geometry("bullet", bullet);
                bulletg.setMaterial(mat);
                bulletg.setName("bullet");
                bulletg.setLocalTranslation(cam.getLocation());
                bulletg.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
                bulletg.addControl(new RigidBodyControl(bulletCollisionShape, 1));
                bulletg.getControl(RigidBodyControl.class).setCcdMotionThreshold(0.1f);
                bulletg.getControl(RigidBodyControl.class).setLinearVelocity(cam.getDirection().mult(50));
                rootNode.attachChild(bulletg);
                getPhysicsSpace().add(bulletg);

                tiros--;
                audioSource.playInstance();
            }
        }

    }

    @Override
    public void collision(PhysicsCollisionEvent event) {
        if (event.getNodeA().getName().equals("player") || event.getNodeB().getName().equals("player")) {

            if (event.getNodeA().getName().equals("bullet")) {
                Spatial s = event.getNodeA();
                Spatial s1 = event.getNodeB();
                bulletAppState.getPhysicsSpace().remove(s);
                rootNode.detachChild(s);
                bulletAppState.getPhysicsSpace().remove(s1);
                rootNode.detachChild(s1);
                players.remove(s1);
                otos --;

            } else if (event.getNodeB().getName().equals("bullet")) {
                Spatial s = event.getNodeB();
                Spatial s1 = event.getNodeA();
                bulletAppState.getPhysicsSpace().remove(s);
                rootNode.detachChild(s);
                bulletAppState.getPhysicsSpace().remove(s1);
                rootNode.detachChild(s1);
                players.remove(s1);
                otos --;

            }

        }

    }

    private void createPlayer() {
        Random r = new  Random();
        int pos = r.nextInt(10)-5;
        
        player = new PlayerCameraNode("player",pos, assetManager, bulletAppState);
         
        rootNode.attachChild(player);
        players.add(player);
    }

    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void initCrossHairs() {
        guiNode.detachAllChildren();
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText("+"); // crosshairs
        ch.setColor(ColorRGBA.White);
        ch.setLocalTranslation( // center
                settings.getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2,
                settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
        guiNode.attachChild(ch);
    }

    private void createTree() {
        tree = (Node) assetManager.loadModel("Models/Tree/Tree.mesh.xml");
        tree.setLocalTranslation(-7, -2.5f, 4);
        tree.setLocalScale(0.8f);
        tree.setLocalScale(1, 1.5f, 1.5f);
        rootNode.attachChild(tree);
        
        tree = (Node) assetManager.loadModel("Models/Tree/Tree.mesh.xml");
        tree.setLocalTranslation(-7, -2.5f, 0);
        tree.setLocalScale(0.8f);
        tree.setLocalScale(1, 1.5f, 1.5f);
        rootNode.attachChild(tree);
        
        tree = (Node) assetManager.loadModel("Models/Tree/Tree.mesh.xml");
        tree.setLocalTranslation(-7, -2.5f, -4);
        tree.setLocalScale(0.8f);
        tree.setLocalScale(1, 1.5f, 1.5f);
        rootNode.attachChild(tree);
        
        tree = (Node) assetManager.loadModel("Models/Tree/Tree.mesh.xml");
        tree.setLocalTranslation(-7, -2.5f,-8);
        tree.setLocalScale(0.8f);
        tree.setLocalScale(1, 1.5f, 1.5f);
        rootNode.attachChild(tree);
        
        tree = (Node) assetManager.loadModel("Models/Tree/Tree.mesh.xml");
        tree.setLocalTranslation(-7, -2.5f, -12);
        tree.setLocalScale(0.8f);
        tree.setLocalScale(1, 1.5f, 1.5f);
        rootNode.attachChild(tree);
        
         tree = (Node) assetManager.loadModel("Models/Tree/Tree.mesh.xml");
        tree.setLocalTranslation(7, -2.5f, 4);
        tree.setLocalScale(0.8f);
        tree.setLocalScale(1, 1.5f, 1.5f);
        rootNode.attachChild(tree);
        
        tree = (Node) assetManager.loadModel("Models/Tree/Tree.mesh.xml");
        tree.setLocalTranslation(7, -2.5f, 0);
        tree.setLocalScale(0.8f);
        tree.setLocalScale(1, 1.5f, 1.5f);
        rootNode.attachChild(tree);
        
        tree = (Node) assetManager.loadModel("Models/Tree/Tree.mesh.xml");
        tree.setLocalTranslation(7, -2.5f, -4);
        tree.setLocalScale(0.8f);
        tree.setLocalScale(1, 1.5f, 1.5f);
        rootNode.attachChild(tree);
        
        tree = (Node) assetManager.loadModel("Models/Tree/Tree.mesh.xml");
        tree.setLocalTranslation(7, -2.5f,-8);
        tree.setLocalScale(0.8f);
        tree.setLocalScale(1, 1.5f, 1.5f);
        rootNode.attachChild(tree);
        
        tree = (Node) assetManager.loadModel("Models/Tree/Tree.mesh.xml");
        tree.setLocalTranslation(7, -2.5f, -12);
        tree.setLocalScale(0.8f);
        tree.setLocalScale(1, 1.5f, 1.5f);
        rootNode.attachChild(tree);
    }
    
    public void Menu(){
        List<String> menu = new ArrayList<String>();
        menu.add("0");
        menu.add("1");
        menu.add("2");
        menu.add("3");
        menu.add("4");
        menu.add("5");
        menu.add("6");
        menu.add("7");
        menu.add("8");
        menu.add("9");
        Object[] options = menu.toArray();
        int value;
        value = JOptionPane.showOptionDialog(
                null,
                "Selecione um dos itens:\n"
                        + "0. Sair\n"
                        + "1. Novo Jogo\n"
                        + "2. Ajuda(Como jogar?)\n"
                        + "3. Sobre(Criadores)\n"
                        + "4. GitHub\n"
                        + "5. Referências\n"
                        + "6. Melhores\n"
                        + "7. Áudio\n"
                        + "8. Dificuldade\n"
                        + "9. Recordes\n"
                        + "Esc - Voltar ao Jogo",
                "                                    Menu",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                menu.get(0));
        
        if (value == 0) {
            System.out.println(1);
            System.exit(0);
        }
        
        if (value == 1) {
            System.out.println(1);
            reiniciar();
        }
        
        if(value == 2){
            JOptionPane.showMessageDialog(null,
                  "Teclas de Comando: \n"
                + "Mouse - Controla a mira da arma\n"
                + "Setas - Movimenta a camera\n"
                + "P - Pausar\n"
                + "Objetivo: "
                + "Matar todos os otos, antes que eles chegem ate você.");   
            Menu();
        }
        
         if(value == 3){
            JOptionPane.showMessageDialog(null,
                  "                  Autores: \n"
                   + "Guilherme Oishi Feitosa - 140896\n"
                   + "Renan Moraes - 141397"
                   + "\n\n    Facens - Sorocaba-SP\n"
                   + "Engenharia da Computação");
            Menu();
            
         }
            if(value == 4){
            JOptionPane.showMessageDialog(null,
                  "github.com/RenanMoraesFacens");
            Menu();
            
        }
            
        if(value == 5){
        JOptionPane.showMessageDialog(null,
              "Referências:\n"
            + "https://img.elo7.com.br/product/original/10C700C/painel-floresta-g-frete-gratis-painel-impresso.jpg\n"        
            + "https://banjomanbold.files.wordpress.com/2013/10/floresta_escura-02.jpg\n"
            + "https://img.elo7.com.br/product/original/8F2DE0/textura-pedras-270cm-x-2-0cm-paineis-fotograficos.jpg\n"          
            + "Todos com os devidos direitos autorais permitidos.");
            Menu();
        }
           
        if(value == 7){
            List<String> som = new ArrayList<String>();
            som.add("Sim");
            som.add("Não");
            Object[] somOp = som.toArray();
            int op;
            op = JOptionPane.showOptionDialog(
                    null,
                    "Deseja som?\n ",
                    "Opção:",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    somOp,
                    som.get(0));
            if(op == 0){
                nature.play();
                audioSource.setVolume(4);
            }
            if(op == 1){
                nature.stop();
                audioSource.setVolume(0);
            }
            Menu();
        }
        
        if(value == 8){
            List<String> nivel = new ArrayList<String>();
            nivel.add("Fácil");
            nivel.add("Medio");
            nivel.add("Difícil");
            Object[] nivels = nivel.toArray();
            
            op = JOptionPane.showOptionDialog(
                    null,
                    "Qual dificuldade?\n ",
                    "Opções:",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    nivels,
                    nivel.get(1));
            if(op == 0){
               tiros = 60; andar=5;max=3;
            }
            if(op == 1){
               tiros = 55; andar=7;max=4;
            }
            if(op == 2){
                tiros = 50; andar=9;max=4;
            }
            
            Menu();
        }
        if(value == 9)
        {
          
            Object[] rc = recordes.toArray();
            String r = "";
            for (Object recorde : recordes) {
                r =  r + recorde.toString() + "\n";
            }
            JOptionPane.showOptionDialog(
                    null,
                    "Melhores:\n "
                    +r,
                    "",
                    JOptionPane.PLAIN_MESSAGE,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null, null);
        }
            
    }
    
    private void reiniciar() {
        if(op == 0){
          tiros = 60; andar = 5;}
        else if(op == 1){
           tiros = 55; andar = 7;}
        else
           tiros = 50; andar = 9;
        for (PlayerCameraNode player1 : players) {
            bulletAppState.getPhysicsSpace().remove(player1);
        }
        players.removeAll(players);
        otos=50;
        
        rootNode.detachChildNamed("player");
        rootNode.detachChildNamed("player");
        rootNode.detachChildNamed("player");
        rootNode.detachChildNamed("player");  
        tempo = System.currentTimeMillis();
        
    }
    
    private void gameover()
    {
        for (int i = 0; i < players.size(); i++) {
            
            if(players.get(i).getLocalTranslation().z >= 7)
            {
                JOptionPane.showMessageDialog(null,"VOCE PERDEU!!! ");
                Menu();
                reiniciar();
            }
        }
    }
    
    private void ganhou()
    { 
        if(otos == 0)
        {   long tempofinal = (System.currentTimeMillis() - tempo);
            float t = (float)tempofinal / 1000;
            
             JOptionPane.showMessageDialog(null,"GANHOU!!! Seu tempo: " + t + "");
             Recordes(t);
            Menu();
            reiniciar();
        }
            
        } 

    public void Recordes(float t){

        recordes.add(t);
        Collections.sort(recordes);
    }

   
}