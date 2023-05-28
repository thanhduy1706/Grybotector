package Entity;

import TileMap.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Player extends MapObject {

   // player stuff
   private int health;
   private int maxHealth;
   private int bullet;
   private int bulletDamage;
   
   private boolean falling;
   



   // animations
   private ArrayList<BufferedImage[]> sprites;
   private final int[] numFrames = {3, 8, 2, 5, 2, 6 };

   //act


   // animation actions
   private int currentAct;
   private int IDLE= 3;
   private int RUN = 5;
   private int JUMP = 4;
   private int CROUCH = 0;
   private int FALLING = 2;
   


   public Player(TileMap tm) {
      super(tm);
      // size
      width = 48;
      height = 48;
      cwidth = 20;
      cheight = 30;

      //Move
      moveSpeed = 0.2;
      sneakySpeed = 0.4 ;
      maxSpeed = 1.6;
      maxFall = 2;
      jumpStart = -4.9;
      fallSpeed = 0.3;
      slowFall = 0.9;
      stopSpeed = 0.5;
      stopJumpSpeed=0.0001;

      facingRight=true;
      health = maxHealth = 5;
      bullet = 3;

      bulletDamage = 1;

      //load sprites
      try {

         BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Animation/spritesheet.png"));

         sprites = new ArrayList<BufferedImage[]>();
         for(int i = 0; i < 6; i++) {

            BufferedImage[] bi =
                    new BufferedImage[numFrames[i]];

            for(int j = 0; j < numFrames[i]; j++) {
               if(i==0){
                  bi[j] = spritesheet.getSubimage(
                          2 * width,
                          i * height,
                          width,
                          height
                  );
               }
               else {
               bi[j] = spritesheet.getSubimage(
                       j * width,
                       i * height,
                       width,
                       height
               );}
            }
            sprites.add(bi);
         }
      }
      catch(Exception e) {
         e.printStackTrace();
      }

      animation = new Animation();
      currentAct = IDLE;
      animation.setFrames(sprites.get(IDLE));
      animation.setDelay(400);
   }




   public int getHealth() {
      return health;
   }
   public void draw(Graphics2D g) {

      setMapPosition();

      // draw player

      if(facingRight) {
         g.drawImage(
                 animation.getImage(),
                 (int)(x + xmap - width / 2),
                 (int)(y + ymap - height / 2),
                 null
         );
      }
      else {
         g.drawImage(
                 animation.getImage(),
                 (int)(x + xmap - width / 2 + width),
                 (int)(y + ymap - height / 2),
                 -width-25,
                 height,
                 null
         );

      }

   }




   public int getMaxHealth() {
      return maxHealth;
   }

   public void setGliding(boolean b){
      falling =b;
   }
   //
//       public boolean isShooting() {
//          return shooting;
//       }
//
//       public boolean isMelee() {
//          return melee;
//       }
//
   public void getNextPosition() {
//          int doublejump = 0;
      //move normal
      if(down){
         if(right){
            dx+= (moveSpeed/10);
            if(dx>sneakySpeed) dx=sneakySpeed;
         }
         else if(left){
            dx-=(moveSpeed/10);
            if(dx<sneakySpeed) dx=-sneakySpeed;
         }
         else{
            if (dx > 0) {
               dx -= stopSpeed*100;
               if (dx < 0) dx = 0;
            } else if (dx < 0) {
               dx += stopSpeed*100;
               if (dx > 0) dx = 0;
            }
         }
      }
      else if (left) {
         dx -= moveSpeed;
         //Update movement
         if (dx < -maxSpeed) dx = -maxSpeed;
      } else if (right) {
         dx += moveSpeed;
         if (dx > maxSpeed) dx = maxSpeed;
      }
      else{
         if (dx > 0) {
            dx -= stopSpeed*3;
            if (dx < 0) dx = 0;
         } else if (dx < 0) {
            dx += stopSpeed*3;
            if (dx > 0) dx = 0;
         }
      }
      if((dx>0&&!botRight)||(dx<0&&!botLeft)){
         falling=true;
      }
//   System.out.println("dx= "+dx);

      //can move when act
//   if ( !(jumping || falling)) {
//      dx = 0;
//   }
//    jumping
      if(jumping && !falling){
         dy=jumpStart;
         falling=true;
      }
      System.out.println("Falling in Player: "+falling);
//   System.out.println("dy="+dy);
      //falling
      if(falling){
//      System.out.println("dy="+dy);
         if(dy>0){
            dy+=fallSpeed*2;}
         else dy+=fallSpeed;

         if(dy>0) jumping=false;
         if(dy<0&&!jumping) {
            dy+= stopJumpSpeed;
         }
         //Update movement
         if(dy>=maxFall) {
            dy=maxFall;
            falling=false;
//         jumping=false;
         }
         if(botRight&&botRight){
            falling=false;
         }
      }
   }

   public void update(){
      //update position
      setPosition(xtemp,ytemp);
      getNextPosition();
      checkCollision();
      System.out.println("tl: "+topLeft);
      System.out.println("tr: "+topRight);
      System.out.println("bl: "+botLeft);
      System.out.println("br: "+botRight);
      System.out.println("Right: " +right);
      System.out.println("Left: "+left);
      System.out.println("Dx= "+dx);
      System.out.println("Dy= "+dy);
      System.out.println();

      //set animation
      if(down){
         if(currentAct!=CROUCH){
            System.out.println("Crouch is working**************************************************");
            currentAct=CROUCH;
            animation.setFrames(sprites.get(CROUCH));
            animation.setDelay(400);
            width=20;
         }
      }
      else if(dy<0){
         if(currentAct!=JUMP){
            currentAct=JUMP;
            animation.setFrames(sprites.get(JUMP));
            animation.setDelay(1);
            width=20;
         }
      }
      else if(dy > 0) {
         if(falling) {
            if(currentAct != FALLING) {
               currentAct = FALLING;
               animation.setFrames(sprites.get(FALLING));
               animation.setDelay(100);
               width = 20;
            }
         }
      }
      else if(left||right){
         if(currentAct!=RUN){
            currentAct=RUN;
            animation.setFrames(sprites.get(RUN));
            animation.setDelay(40);
            width=20;
         }
      }
      else if(currentAct!=IDLE){
         currentAct=IDLE;
         animation.setFrames(sprites.get(IDLE));
         animation.setDelay(400);
         width=20;
      }
      animation.update();
      if(currentAct==RUN){
         if(right) facingRight=true;
         if(left) facingRight=false;
      }

      System.out.println("Current Act: " + currentAct);
      System.out.println();
   }
}