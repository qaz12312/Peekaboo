# Java-Final

## 程式進入點
* peekaboo.Pkb

## 選單介面 package peekaboo.menu;
* PkbMenu -- 
    * StartPanel
        * ModePanel
            * NormalModePanel
            * SpecialModePanel
            * MultiplePlayerPanel
                * MultiplePanel
                * InputIDPanel
                * RoomPanel
        * InstrucPanel
* LoadingPanel
* PkbButton -

## 角色 package peekaboo.role;
* PkbHuman ------
    + import peekaboo.props.*;
    + import peekaboo.*;
* PkbGhost ------
    + import peekaboo.props.*;
    + import peekaboo.*;

## package peekaboo;
* CreateMaze ----
* InitMap -------
* BackgroundImage 
* Music
* KeyListener --- 
* PkbTimer ------ 倒數計時
    + import peekaboo.props.*;
* GameFrame ----- 
    + import peekaboo.props.*;
    + import peekaboo.role.*;
* PkbAPIHandler

## 道具 package peekaboo.props;
* PkbFlyingRock - 撿起來的地板
    + import peekaboo.props.*;
    + import peekaboo.role.PkbHuman;
    + import peekaboo.*;

* Enery --------- 以下class的父類
* PkbOnlinePlayer
* MazeExit
* Door ---------- 任意門
* Hole ---------- 坑洞
* Barrier ------- 障礙物
* Bewitch ------- 迷惑
* Turtle -------- 烏龜
* Shoe ---------- 跑鞋
* Stone --------- 地板
* Heart --------- 加hp
* Fruit --------- 惡魔果實

## 連線
* requestTestjava