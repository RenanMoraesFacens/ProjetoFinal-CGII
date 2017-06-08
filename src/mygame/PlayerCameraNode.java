/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl;

/**
 *
 * @author R
 */
public class PlayerCameraNode extends Node {
    
    
    private final BetterCharacterControl physicsCharacter;
    private final AnimControl animationControl;
    private final AnimChannel animationChannel;
    private Vector3f walkDirection = new Vector3f(0, 0, 0);
    private Vector3f viewDirection = new Vector3f(0, 0, 0);
    private float airTime;
    public Node oto; 

    public PlayerCameraNode(String name,int pos,AssetManager assetManager, BulletAppState bulletAppState) {
        super(name);
        
    
  
        oto = (Node) assetManager.loadModel("Models/Oto/Oto.mesh.xml");
        oto.setLocalTranslation(0, 2.5f, 0);
        oto.setLocalScale(0.4f);
        setLocalTranslation(pos, 0, -30);
        attachChild(oto);
        
        physicsCharacter = new BetterCharacterControl(1,5, 16f);
         
        addControl(physicsCharacter);
        
        bulletAppState.getPhysicsSpace().add(physicsCharacter);
        
        animationControl = oto.getControl(AnimControl.class);
        animationChannel = animationControl.createChannel();
        animationChannel.setAnim("Walk", 0.05f);

   }

    
    public Vector3f getWalkDirection() {
        return walkDirection;
    }

    public void setWalkDirection(Vector3f walkDirection) {
        this.walkDirection = walkDirection;
    }

    public Vector3f getViewDirection() {
        return viewDirection;
    }

    public void setViewDirection(Vector3f viewDirection) {
        this.viewDirection = viewDirection;
    }
     
    void upDateAnimationPlayer(boolean jump) {
   
    }

    void upDateKeys(float tpf,boolean up,int inc)
    {
        
        Vector3f camDir  = getWorldRotation().mult(Vector3f.UNIT_Z);
       
        viewDirection.set(camDir);
        walkDirection.set(0, 0, 0);
            
        walkDirection.addLocal(camDir.mult(inc));
        
        if (up) {
            walkDirection.addLocal(camDir.mult(1));
        physicsCharacter.setWalkDirection(walkDirection);
        physicsCharacter.setViewDirection(viewDirection);
 
    }
    
    
    }  
    
}
