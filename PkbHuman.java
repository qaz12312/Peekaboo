package peekaboo.role;

import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Rectangle;
import javax.swing.ImageIcon;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.security.SecureRandom;
import peekaboo.*;
import peekaboo.props.*;

public class PkbHuman extends Thread {

    private boolean ignoreWalls = false;// DEBUG ONLY

    public KeyListener key;
    public GameFrame gameFrame;// 遊戲地圖
    public int x = 120, y = 360;// 角色的坐標(一開始在左下角)
    public int defaultSpeed = 12;
    public int xspeed = 12, yspeed = 12;
    public int turtleSpeed = 2;
    public int camelSpeed = 25;
    public int absoluteY = 1200, absoluteX = 1560;

    public int sequence = 0;// 記錄誰最後
    public static final int width = 120, height = 120;// 角色的寬高
    public Image img = new ImageIcon("img/human_downMove_gif_160.gif").getImage();// 角色圖片

    public boolean up = false, down = false, left = false, right = false;
    private static final String Str_Up = "Up", Str_Down = "Down", Str_Left = "Left", Str_Right = "Right";
    public String lastDirection = "Right";
    private static SecureRandom rnd = new SecureRandom();

    private int bound_x = 600 * 2;
    private int bound_y = 300 * 2;

    Timer timer = new Timer();
    private static boolean run = true;
    ArrayList<Integer> bagList = new ArrayList<Integer>(0);
    ArrayList<Enery> bageneryList = new ArrayList<Enery>(0);
    ArrayList<Integer> bagList2 = new ArrayList<Integer>(0);
    ArrayList<Point> pp = new ArrayList<>();

    private long portalCooldown = 2000;
    private long portalCooldownSince = 0;
    private boolean portalIsCooldown = false;

    public Image[] defaultImgSet = { new ImageIcon("img/human_upMove_gif_160.gif").getImage(),
            new ImageIcon("img/human_downMove_gif_160.gif").getImage(),
            new ImageIcon("img/human_leftMove_gif_160.gif").getImage(),
            new ImageIcon("img/human_rightMove_gif_160.gif").getImage() };

    public Image[] tracherImgSet = { new ImageIcon("img/teacher_downMove_GIF.gif").getImage(),
            new ImageIcon("img/teacher_downMove_GIF.gif").getImage(),
            new ImageIcon("img/teacher_downMove_GIF.gif").getImage(),
            new ImageIcon("img/teacher_downMove_GIF.gif").getImage() };

    public Image[] turtleImgSet = { new ImageIcon("img/slowHuman_upMove_GIF.gif").getImage(),
            new ImageIcon("img/slowHuman_downMove_GIF.gif").getImage(),
            new ImageIcon("img/slowHuman_leftMove_GIF.gif").getImage(),
            new ImageIcon("img/slowHuman_rightMove_GIF.gif").getImage() };

    public Image[] camelImgSet = { new ImageIcon("img/camelHuman_upMove_GIF.gif").getImage(),
            new ImageIcon("img/camelHuman_downMove_GIF.gif").getImage(),
            new ImageIcon("img/camelHuman_leftMove_GIF.gif").getImage(),
            new ImageIcon("img/camelHuman_rightMove_GIF.gif").getImage() };

    public Image[] dizzyImgSet = { new ImageIcon("img/dizzyHuman_upMove_GIF.gif").getImage(),
            new ImageIcon("img/dizzyHuman_downMove_GIF.gif").getImage(),
            new ImageIcon("img/dizzyHuman_leftMove_GIF.gif").getImage(),
            new ImageIcon("img/dizzyHuman_rightMove_GIF.gif").getImage() };

    public Image[] activeImgSet;
    private ArrayList<Image[]> imgSetQueue = new ArrayList<Image[]>();

    ArrayList<Enery> backpack = new ArrayList<Enery>(0);

    public boolean pick = false, use = false, div = false, teacher = false, isWitch = false, quickSend = false,
            camel = false;
    public int num = 0;

    public PkbHuman(GameFrame g) {
        this.gameFrame = g;
        this.activeImgSet = this.defaultImgSet;
        imgSetQueue.add(this.activeImgSet);
        this.xspeed = this.defaultSpeed;
        this.yspeed = this.defaultSpeed;
        teleport(1200, 1560); // 10, 13
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void Time(int t, int tool) {
        TimerTask test = new TimerTask() {
            @Override
            public void run() {
                switch (tool) {
                    case 2:
                        camel = false;
                        // imgSetQueue.remove(imgSetQueue.size() - 1);
                        // activeImgSet = imgSetQueue.get(imgSetQueue.size() - 1);
                        activeImgSet = defaultImgSet;
                        break;
                    case 3:
                        quickSend = false;
                        // imgSetQueue.remove(imgSetQueue.size() - 1);
                        // activeImgSet = imgSetQueue.get(imgSetQueue.size() - 1);
                        activeImgSet = defaultImgSet;
                        break;
                    case 5:
                        isWitch = false;// 蠍子
                        // imgSetQueue.remove(imgSetQueue.size() - 1);
                        // activeImgSet = imgSetQueue.get(imgSetQueue.size() - 1);
                        activeImgSet = defaultImgSet;
                        break;

                    case 6:
                        teacher = false;
                        // imgSetQueue.remove(imgSetQueue.size() - 1);
                        // activeImgSet = imgSetQueue.get(imgSetQueue.size() - 1);
                        activeImgSet = defaultImgSet;
                        break;
                    case 100:
                        gameFrame.save = false;
                        // imgSetQueue.remove(imgSetQueue.size() - 1);
                        // activeImgSet = imgSetQueue.get(imgSetQueue.size() - 1);
                        activeImgSet = defaultImgSet;
                        break;

                    default:
                        break;
                }
                xspeed = defaultSpeed;
                yspeed = defaultSpeed;
                Toolkit.getDefaultToolkit().beep();
            }
        };

        timer.schedule(test, t);
        run = false;
    }

    public void run() {
        this.bagList.add(0);
        while (true) {
            move();
            try {
                this.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            eneryInteract();
        }
    }

    public void move() {
        if (!isWitch && up || isWitch && down) {
            this.lastDirection = Str_Up;
            if (!hasBumpIntoWall(Str_Up, this.gameFrame.rockEneryByPos) || ignoreWalls)
                moveUp();
        }
        if (!isWitch && down || isWitch && up) {
            lastDirection = Str_Down;
            this.img = this.activeImgSet[1];
            if (!hasBumpIntoWall(Str_Down, this.gameFrame.rockEneryByPos) || ignoreWalls)
                moveDown();
        }
        if (!isWitch && left || isWitch && right) {
            lastDirection = Str_Left;
            this.img = this.activeImgSet[2];
            if (!hasBumpIntoWall(Str_Left, this.gameFrame.rockEneryByPos) || ignoreWalls)
                moveLeft();
        }
        if (!isWitch && right || isWitch && left) {
            lastDirection = Str_Right;
            this.img = this.activeImgSet[3];
            if (!hasBumpIntoWall(Str_Right, this.gameFrame.rockEneryByPos) || ignoreWalls)
                moveRight();
        }
        try {
            this.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void moveUp() {
        this.img = this.activeImgSet[0];
        if (this.y >= 500 && this.y <= bound_y) {
            this.y -= this.yspeed;
            this.absoluteY -= this.yspeed;
        } else if (this.y > bound_y || this.y < 500) {
            gameFrame.bg.y += this.yspeed;// 背景向下移動
            this.absoluteY -= this.yspeed;
            // 障礙物項左移動
            for (Enery enery : this.gameFrame.eneryList) {
                enery.y += this.yspeed;
            }
            for (PkbGhost ghost : this.gameFrame.ghosts) {
                ghost.y += this.yspeed;
            }
            for (PkbFlyingRock fock : this.gameFrame.flyingRocks) {
                fock.y += this.yspeed;
            }
            for (PkbOnlinePlayer op : this.gameFrame.onlinePlayers) {
                op.y += this.yspeed;
            }
        }
    }

    private void moveDown() {
        this.img = this.activeImgSet[1];
        if (this.y < bound_y) {
            this.y += this.yspeed;
            this.absoluteY += this.yspeed;
        } else if (this.y >= bound_y) {
            gameFrame.bg.y -= this.yspeed;// 背景向上移動
            this.absoluteY += this.yspeed;
            // 障礙物項左移動
            for (Enery enery : this.gameFrame.eneryList) {
                enery.y -= this.yspeed;
            }
            for (PkbGhost ghost : this.gameFrame.ghosts) {
                ghost.y -= this.yspeed;
            }
            for (PkbFlyingRock fock : this.gameFrame.flyingRocks) {
                fock.y -= this.yspeed;
            }
            for (PkbOnlinePlayer op : this.gameFrame.onlinePlayers) {
                op.y -= this.yspeed;
            }
        }
    }

    private void moveLeft() {
        this.img = this.activeImgSet[2];
        if (this.x >= bound_x) {
            this.x -= this.xspeed;
            this.absoluteX -= this.xspeed;
        } else if (this.x < bound_x) {
            gameFrame.bg.x += this.xspeed;// 背景向右移動
            this.absoluteX -= this.xspeed;
            // 障礙物項右移動
            for (Enery enery : this.gameFrame.eneryList) {
                enery.x += this.xspeed;
            }
            for (PkbGhost ghost : this.gameFrame.ghosts) {
                ghost.x += this.xspeed;
            }
            for (PkbFlyingRock fock : this.gameFrame.flyingRocks) {
                fock.x += this.xspeed;
            }
            for (PkbOnlinePlayer op : this.gameFrame.onlinePlayers) {
                op.x += this.xspeed;
            }
        }
    }

    private void moveRight() {
        this.img = this.activeImgSet[3];
        if (this.x <= bound_x) {
            this.x += this.xspeed;
            this.absoluteX += this.xspeed;
        } else if (this.x > bound_x) {
            gameFrame.bg.x -= this.xspeed;// 背景向左移動
            this.absoluteX += this.xspeed;

            // 障礙物項左移動
            for (Enery enery : this.gameFrame.eneryList) {
                enery.x -= this.xspeed;
            }
            for (PkbGhost ghost : this.gameFrame.ghosts) {
                ghost.x -= this.xspeed;
            }
            for (PkbFlyingRock fock : this.gameFrame.flyingRocks) {
                fock.x -= this.xspeed;
            }
            for (PkbOnlinePlayer op : this.gameFrame.onlinePlayers) {
                op.x -= this.xspeed;
            }
        }
    }

    public void eneryInteract() {
        // 檢查是否碰撞到道具
        Enery bumpedEnery = bump(this.gameFrame.mapEneryByPos);
        if (bumpedEnery != null) {
            if (bumpedEnery instanceof Shoe) {
                camel = true;
                // timeSinceCamel = Calendar.getInstance().getTimeInMillis();//另一個計時
                // img = new ImageIcon("img/camelHuman_downMove_GIF.gif").getImage();
                this.activeImgSet = this.camelImgSet;
                imgSetQueue.add(this.activeImgSet);
                xspeed = camelSpeed;
                yspeed = camelSpeed;
                Time(12000, 2);
                // 從 bump 判定的字典中將碰撞到的物件移除
                bumpedEnery.img = new ImageIcon("img/back.png").getImage();
                this.gameFrame.mapEneryByPos.get(String.valueOf(bumpedEnery.arrRow * 120))
                        .remove(String.valueOf(bumpedEnery.arrCol * 120));

            } else if (bumpedEnery instanceof Turtle) {
                this.activeImgSet = this.turtleImgSet;
                imgSetQueue.add(this.activeImgSet);
                xspeed = turtleSpeed;
                yspeed = turtleSpeed;
                Time(10000, 3);
                quickSend = true;
                // 從 bump 判定的字典中將碰撞到的物件移除
                bumpedEnery.img = new ImageIcon("img/back.png").getImage();
                this.gameFrame.mapEneryByPos.get(String.valueOf(bumpedEnery.arrRow * 120))
                        .remove(String.valueOf(bumpedEnery.arrCol * 120));
            } else if (bumpedEnery instanceof Door) {
                // 若是 Door
                if (!this.portalIsCooldown) {
                    int rnd_door = rnd.nextInt(this.gameFrame.doors.size());
                    Door door = this.gameFrame.doors.get(rnd_door);
                    this.absoluteY = door.absoluteY;
                    this.absoluteX = door.absoluteX + 120;
                    teleport(door.y, door.x + 120);

                    this.portalIsCooldown = true;
                    this.portalCooldownSince = Calendar.getInstance().getTimeInMillis();
                } else if (Calendar.getInstance().getTimeInMillis() - this.portalCooldownSince > this.portalCooldown) {
                    this.portalIsCooldown = false;
                }
            } else if (bumpedEnery instanceof Fruit) {
                this.activeImgSet = this.tracherImgSet;
                imgSetQueue.add(this.activeImgSet);
                teacher = true;
                for (PkbGhost ghost : this.gameFrame.ghosts) {
                    ghost.rageActivated = true;
                }
                Time(10000, 6);
                // 從 bump 判定的字典中將碰撞到的物件移除
                bumpedEnery.img = new ImageIcon("img/back.png").getImage();
                this.gameFrame.mapEneryByPos.get(String.valueOf(bumpedEnery.arrRow * 120))
                        .remove(String.valueOf(bumpedEnery.arrCol * 120));
            } else if (bumpedEnery instanceof Bewitch) {
                this.activeImgSet = this.dizzyImgSet;
                imgSetQueue.add(this.activeImgSet);
                isWitch = true;
                Time(10000, 5);
                // 從 bump 判定的字典中將碰撞到的物件移除
                bumpedEnery.img = new ImageIcon("img/back.png").getImage();
                this.gameFrame.mapEneryByPos.get(String.valueOf(bumpedEnery.arrRow * 120))
                        .remove(String.valueOf(bumpedEnery.arrCol * 120));
            } else if (bumpedEnery instanceof Heart) {
                this.gameFrame.hp++;
                // 從 bump 判定的字典中將碰撞到的物件移除
                bumpedEnery.img = new ImageIcon("img/back.png").getImage();
                this.gameFrame.mapEneryByPos.get(String.valueOf(bumpedEnery.arrRow * 120))
                        .remove(String.valueOf(bumpedEnery.arrCol * 120));
            } else if (bumpedEnery instanceof MazeExit) {//成功逃出
                this.gameFrame.hasWon = true;
                this.gameFrame.isGameOver = true;
            }
        }
        if (this.use) {
            if (this.backpack.size() != 0) {
                this.backpack.remove(this.backpack.size() - 1);
                PkbFlyingRock fr = new PkbFlyingRock(this, this.lastDirection);
                this.gameFrame.flyingRocks.add(fr);
                fr.start();
            }
        }
        if (this.pick && isDiggable()) {
            Enery diggableEnery = bump(this.gameFrame.backEneryByPos);
            diggableEnery.img = new ImageIcon("img/dig.png").getImage();
            this.gameFrame.backEneryByPos.get(String.valueOf(diggableEnery.arrRow * 120))
                    .remove(String.valueOf(diggableEnery.arrCol * 120));
            this.backpack.add(diggableEnery);
        }

    }

    public Enery bump(Map<String, Map<String, Enery>> eneryByPos) {
        Rectangle playerPoly = new Rectangle(this.x - (width / 2), this.y - (height / 2), width, height);
        Map<String, Map<String, Enery>> mapEneryByPos = eneryByPos;
        Set<String> keys = mapEneryByPos.keySet();
        for (String k : keys) {
            for (String y_l : mapEneryByPos.get(k).keySet()) {
                Enery e = mapEneryByPos.get(k).get(y_l);
                Rectangle eneryPoly = new Rectangle(e.x - (e.width / 2), e.y - (e.height / 2), e.width, e.height);
                if (playerPoly.intersects(eneryPoly)) {
                    return e;
                }
            }
        }
        return null;
    }

    public boolean hasBumpIntoWall(String directionString, Map<String, Map<String, Enery>> brickByPos) {
        Rectangle playerPoly;

        int xtol = width - (width / 10);
        int ytol = height - (height / 10);

        if (directionString.equals(Str_Up)) {
            playerPoly = new Rectangle(this.x - (width / 2), this.y - ((height / 2) + (height / 10)), xtol, ytol);
        } else if (directionString.equals(Str_Down)) {
            playerPoly = new Rectangle(this.x - (width / 2), (this.y - (height / 2)) + (height / 10), xtol, ytol);
        } else if (directionString.equals(Str_Left)) {
            playerPoly = new Rectangle(this.x - ((width / 2) + (width / 10)), this.y - (height / 2), xtol, ytol);
        } else if (directionString.equals(Str_Right)) {
            playerPoly = new Rectangle(this.x - (width / 2) + (width / 10), this.y - (height / 2), xtol, ytol);
        } else
            return false;

        Map<String, Map<String, Enery>> birckPos = brickByPos;
        Set<String> keys = birckPos.keySet();
        for (String k : keys) {
            for (String y_l : birckPos.get(k).keySet()) {
                Enery e = birckPos.get(k).get(y_l);
                Rectangle eneryPoly = new Rectangle(e.x - (e.width / 2), e.y - (e.height / 2), e.width, e.height);
                if (playerPoly.intersects(eneryPoly)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isDiggable() {
        Rectangle playerPoly = new Rectangle(this.x - (width / 2), this.y - (height / 2), width, height);
        Map<String, Map<String, Enery>> backEneryByPos = this.gameFrame.backEneryByPos;
        Set<String> keys = backEneryByPos.keySet();
        for (String k : keys) {
            for (String y_l : backEneryByPos.get(k).keySet()) {
                Enery e = backEneryByPos.get(k).get(y_l);
                Rectangle eneryPoly = new Rectangle(e.x - (e.width / 2), e.y - (e.height / 2), e.width, e.height);
                if (playerPoly.intersects(eneryPoly)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void teleport(int target_y, int target_x) {
        // MOVE Y
        int var_y = this.y - target_y;
        int var_x = this.x - target_x;
        gameFrame.bg.y += var_y;
        for (Enery enery : gameFrame.eneryList) {
            enery.y += var_y;
        }
        for (PkbGhost ghost : this.gameFrame.ghosts) {
            ghost.y += var_y;
        }
        for (PkbFlyingRock fock : this.gameFrame.flyingRocks) {
            fock.y += var_y;
        }

        gameFrame.bg.x += var_x;
        for (Enery enery : gameFrame.eneryList) {
            enery.x += var_x;
        }
        for (PkbGhost ghost : this.gameFrame.ghosts) {
            ghost.x += var_x;
        }
        for (PkbFlyingRock fock : this.gameFrame.flyingRocks) {
            fock.x += var_x;
        }
    }
}