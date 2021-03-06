package activitytest.example.com.android_homeword_20;


import android.util.DisplayMetrics;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

//import static activitytest.example.com.android_homeword_20.MainActivity.windowWidth;
//import static activitytest.example.com.android_homeword_20.MainActivity.windowHeight;

// 自定义视图类
class GameView extends View implements Runnable {

        private int windowHeight;
        private int windowWidth;

        private RefreshHandler mRedrawHandler = null;
        Ball ball=new Ball();
        List<Player> mPlayerList;
        List<Player> PlayerList;

        private int getNextP(int i){
                if(i == 9)return (int)(Math.random()*2)==0?7:8;
                if(i == 7)return (int)(Math.random()*2)==0?3:4;
                if(i == 8)return (int)(Math.random()*2)==0?5:6;
                if(i == 3)return 0;
                if(i == 4)return (int)(Math.random()*2)==0?0:1;
                if(i == 5)return (int)(Math.random()*2)==0?1:2;
                if(i == 6)return 2;
                return -1;
        }

        //计算两点距离
        private float getDis(float x1,float y1,float x2,float y2){
                return (float)Math.sqrt((double)((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2)));
        }

        //找到离球最近的人     这个要改，改为球前最近的人
        private int findNearP(){
                if(ball.myShooted==1){
                        float mind = 999999;int mini = 0;
                        for(int i=0;i<PlayerList.size();i++){
                                Player player = PlayerList.get(i);
                                float dis = getDis(player.getCenterX(),player.getCenterY(),ball.x,ball.y);
                                if(dis<mind){mind=dis;mini=i;}
                        }
                        if(ball.y<windowHeight/12)return -1;
                        if(ball.y<windowHeight/5)return 9;
                        if(ball.y<windowHeight*13/30){
                                for(int i=7;i<9;i++){
                                        Player player = PlayerList.get(i);
                                        float dis = getDis(player.getCenterX(),player.getCenterY(),ball.x,ball.y);
                                        if(dis<mind){mind=dis;mini=i;}
                                }
                                return mini;
                        }
                        if(ball.y<windowHeight*2/3){
                                for(int i=3;i<7;i++){
                                        Player player = PlayerList.get(i);
                                        float dis = getDis(player.getCenterX(),player.getCenterY(),ball.x,ball.y);
                                        if(dis<mind){mind=dis;mini=i;}
                                }
                                return mini;
                        }
                        for(int i=0;i<3;i++){
                                Player player = PlayerList.get(i);
                                float dis = getDis(player.getCenterX(),player.getCenterY(),ball.x,ball.y);
                                if(dis<mind){mind=dis;mini=i;}
                        }
                        return mini;
                }

                float mind = 999999;int mini = 0;
                for(int i=0;i<PlayerList.size();i++){
                        Player player = PlayerList.get(i);
                        float dis = getDis(player.getCenterX(),player.getCenterY(),ball.x,ball.y);
                        if(dis<mind){mind=dis;mini=i;}
                }
                if(ball.y<windowHeight/12)return 9;
                if(ball.y<windowHeight/5){
                        for(int i=7;i<9;i++){
                                Player player = PlayerList.get(i);
                                float dis = getDis(player.getCenterX(),player.getCenterY(),ball.x,ball.y);
                                if(dis<mind){mind=dis;mini=i;}
                        }
                        return mini;
                }
                if(ball.y<windowHeight*13/30){
                        for(int i=3;i<7;i++){
                                Player player = PlayerList.get(i);
                                float dis = getDis(player.getCenterX(),player.getCenterY(),ball.x,ball.y);
                                if(dis<mind){mind=dis;mini=i;}
                        }
                        return mini;
                }
                //if(ball.y<windowHeight*2/3){
                for(int i=0;i<3;i++){
                        Player player = PlayerList.get(i);
                        float dis = getDis(player.getCenterX(),player.getCenterY(),ball.x,ball.y);
                        if(dis<mind){mind=dis;mini=i;}
                }
                return mini;
                //}
                //return -1;
        }

        private float logicDx=0;

        //玄学ai，各种速度调整都会影响到ai性能，很容易进入ai死区
        private void aiLogic(){
                int pos =findNearP();

                if(pos==-1)return;
                Player nearP = PlayerList.get(pos);
                int dir = 1;//1为右，-1为左

                logicDx = (int)PlayerList.get(9).dx;
                //if(pos>2&&pos<7)logicDx=(logicDx*20/13);
                //if(pos>6&&pos<9)logicDx=(logicDx*3/4);


                float xdis=Math.abs(nearP.x+nearP.dx-ball.x);
                float ydis=Math.abs(nearP.y-ball.y);

                float t=Math.abs(ydis/ball.vy);
                float balldestinedX = ball.x+t*ball.vx;
                float diss =nearP.x+nearP.dx-balldestinedX;
                if(Math.abs((double)diss)<3)dir=0;
                else if(diss<-ball.vx*2)dir=1;
                else dir=-1;
                if(logicDx>60)dir=-1;


             /*   法一
              float dis=nearP.x+nearP.dx-ball.x;
             if(Math.abs((double)diss)<1e-3)dir=0;
             else if(diss>0)dir=-1;
             else dir=1;*/
             /*
              * 法二
              * if(logicDx>0){
                     if(nearP.x+nearP.dx>ball.x-100)dir=-1;}
             else if(nearP.x+nearP.dx>ball.x)dir=-1;*/
                //Log.d("aitest", "ai: @@@ "+pos+" "+ball.x+","+ball.y+"  "+(nearP.x+nearP.dx));
                Log.d("aitest", "ai: @@@ "+pos+" "+logicDx+","+5*dir);

                logicDx += 5*dir;
                float dx = logicDx;

                //相减
                if ((PlayerList.get(0).x + dx > 0) && (PlayerList.get(2).x + dx + 40 < MaxRight)) {
                        PlayerList.get(0).update(dx);
                        PlayerList.get(1).update(dx);
                        PlayerList.get(2).update(dx);
                }
                if ((PlayerList.get(3).x + dx*13/20 > 0)&&(PlayerList.get(6).x + dx*13/20 + 40 < MaxRight)){
                        PlayerList.get(3).update(dx*13/20);
                        PlayerList.get(4).update(dx*13/20);
                        PlayerList.get(5).update(dx*13/20);
                        PlayerList.get(6).update(dx*13/20);
                }
                if ((PlayerList.get(7).x + dx*4/3 > 0)&&(PlayerList.get(8).x + dx*4/3 + 40 < MaxRight)){
                        PlayerList.get(7).update(dx*4/3);
                        PlayerList.get(8).update(dx*4/3);
                }
                if((PlayerList.get(9).x + dx > MaxRight/4)&&(PlayerList.get(9).x + dx + 40 < MaxRight*3/4)){
                        PlayerList.get(9).update(dx);
                }
        }

        // 构造方法
        public GameView(Context context) {
                super(context);

                //获得手机屏幕的高宽
                DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                windowHeight = displayMetrics.heightPixels * 4 / 5;
                windowWidth = displayMetrics.widthPixels * 4 / 5;

                //mPaint.setColor(Color.BLACK);
                //定义己方球员和对方球员
                mPlayerList = new ArrayList<>();
                PlayerList = new ArrayList<>();
                //Player tem = new Player(80,80);
                //Player t2 = new Player(300,190);
                Player t1,t2,t3,t4,t5;
                Log.d("bbb", "MyView: @@@ "+getHeight()+" "+getWidth());
                Log.d("bbb", "MyView: ### "+windowWidth+" "+windowHeight);

                //PlayerList.add(new Player(windowWidth-40,windowHeight-40));

                //这里开始设置球员的布局
                //从上往下数第一行
            /* t1 = new Player(windowWidth/4, windowHeight/3);
             t2 = new Player(windowWidth*2/4, windowHeight/3);
             t3 = new Player(windowWidth*3/4, windowHeight/3);*/
                //由于目前是按照左上角的点确定的，所以用下面的代码进行初始化
                t1 = new Player(windowWidth/4 - 20, windowHeight/3);
                t2 = new Player(windowWidth*2/4 - 20, windowHeight/3);
                t3 = new Player(windowWidth*3/4 - 20, windowHeight/3);
                mPlayerList.add(t1);
                mPlayerList.add(t2);
                mPlayerList.add(t3);

                //从上往下数第二行
             /*t1 = new Player(windowWidth*2/16, windowHeight*17/30);
             t2 = new Player(windowWidth*6/16, windowHeight*17/30);
             t3 = new Player(windowWidth*10/16, windowHeight*17/30);
             t4 = new Player(windowWidth*14/16, windowHeight*17/30);*/
                //同上
                t1 = new Player(windowWidth*13/80 - 20, windowHeight*17/30);
                t2 = new Player(windowWidth*31/80 - 20, windowHeight*17/30);
                t3 = new Player(windowWidth*49/80 - 20, windowHeight*17/30);
                t4 = new Player(windowWidth*67/80 - 20, windowHeight*17/30);
                mPlayerList.add(t1);
                mPlayerList.add(t2);
                mPlayerList.add(t3);
                mPlayerList.add(t4);

                //从上往下数第三行
             /*t1 = new Player(windowWidth/3, windowHeight*4/5);
             t2 = new Player(windowWidth*2/3, windowHeight*4/5);*/
                //同理
                t1 = new Player(windowWidth/3 - 20, windowHeight*4/5);
                t2 = new Player(windowWidth*2/3 - 20, windowHeight*4/5);
                mPlayerList.add(t1);
                mPlayerList.add(t2);

                //守门员
                //t1 = new Player(windowWidth/2, windowHeight*11/12);
                t1 = new Player(windowWidth/2 - 20, windowHeight*11/12);
                mPlayerList.add(t1);
                //mPlayerList.add(tem);
                //mPlayerList.add(t2);

                //球门对象
                t1 = new Player(windowWidth/2 - 100, windowHeight - 40);
                t2 = new Player(windowWidth/2 - 60, windowHeight - 40);
                t3 = new Player(windowWidth/2 - 20, windowHeight - 40);
                t4 = new Player(windowWidth/2 + 20, windowHeight - 40);
                t5 = new Player(windowWidth/2 + 60, windowHeight - 40);
                mPlayerList.add(t1);
                mPlayerList.add(t2);
                mPlayerList.add(t3);
                mPlayerList.add(t4);
                mPlayerList.add(t5);

                //初始化对面的球员
                t1 = new Player(windowWidth/4 - 20, windowHeight*2/3);
                t2 = new Player(windowWidth*2/4 - 20, windowHeight*2/3);
                t3 = new Player(windowWidth*3/4 - 20, windowHeight*2/3);
                PlayerList.add(t1);
                PlayerList.add(t2);
                PlayerList.add(t3);
                t1 = new Player(windowWidth*13/80 - 20, windowHeight*13/30);
                t2 = new Player(windowWidth*31/80 - 20, windowHeight*13/30);
                t3 = new Player(windowWidth*49/80 - 20, windowHeight*13/30);
                t4 = new Player(windowWidth*67/80 - 20, windowHeight*13/30);
                PlayerList.add(t1);
                PlayerList.add(t2);
                PlayerList.add(t3);
                PlayerList.add(t4);
                t1 = new Player(windowWidth/3 - 20, windowHeight*1/5);
                t2 = new Player(windowWidth*2/3 - 20, windowHeight*1/5);
                PlayerList.add(t1);
                PlayerList.add(t2);
                t1 = new Player(windowWidth/2 - 20, windowHeight*1/12);
                PlayerList.add(t1);

                //初始化对面的球门对象
                t1 = new Player(windowWidth/2 - 100, 0);
                t2 = new Player(windowWidth/2 - 60, 0);
                t3 = new Player(windowWidth/2 - 20, 0);
                t4 = new Player(windowWidth/2 + 20, 0);
                t5 = new Player(windowWidth/2 + 60, 0);
                PlayerList.add(t1);
                PlayerList.add(t2);
                PlayerList.add(t3);
                PlayerList.add(t4);
                PlayerList.add(t5);

                // 获得焦点
                setFocusable(true);
                mRedrawHandler = new RefreshHandler();//放在主线程里面
                //       Player [] t =new Player[8];

                // 启动线程
                new Thread(this).start();
        }

        @Override
        public void run() {

                while (!Thread.currentThread().isInterrupted()) {
                        // 通过发送消息更新界面
                        Message m = new Message();
                        m.what = 0x101;
                        mRedrawHandler.sendMessage(m);
                        try {
                                Thread.sleep(100);
                        } catch (InterruptedException e) {
                                e.printStackTrace();
                        }
                }
        }

        @Override
        protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                // 实例化画笔
                Paint p = new Paint();
                // 设置画笔颜色
                p.setColor(Color.GREEN);

                Paint ballp =new Paint();

                boolean colliged=false;

                for(int i = 0;i<mPlayerList.size();i++){
                        Player player = mPlayerList.get(i);
                        player.onDraw(canvas, p);
                        if(ball.check(player)){
                                colliged=true;
                                if(ball.isCollided!=0)continue;
                                int to =getNextP(i);
                                if(to==-1){
                                        int topos =(int)(Math.random()*5)+10;
                                        ball.changeV(PlayerList.get(topos));continue;
                                }
                             /*int to=(int)(Math.random()*mPlayerList.size());
                             while(mPlayerList.get(to)==player){
                                 to=(int)(Math.random()*mPlayerList.size());
                             }*/
                                ball.changeV(mPlayerList.get(to));
                                //ball.changeV(mPlayerList.get());
                        }
                }

             /*for (int i=0;i<mPlayerList.size();i++){
                 mPlayerList.get(i).onDraw(canvas, p);
                 if(ball.check(mPlayerList.get(i))){
                     colliged=true;
                     int to=1-i;
                     if(ball.isCollided!=0)continue;
                     ball.changeV(mPlayerList.get(to));
                 }
             }*/

                if(colliged)ballp.setColor(Color.RED);
                else ballp.setColor(Color.YELLOW);

                p.setColor(Color.BLUE);
             /*for (Player player :
                     PlayerList) {
                 player.onDraw(canvas, p);
             }*/

                for(int i = 0;i<PlayerList.size();i++){
                        Player player = PlayerList.get(i);
                        player.onDraw(canvas, p);
                        if(ball.check(player)){
                                colliged=true;
                                if(ball.isCollided!=0)continue;
                                int to =getNextP(i);
                                if(to==-1){
                                        int topos =(int)(Math.random()*5)+10;
                                        ball.changeV(mPlayerList.get(topos));continue;
                                }
                     /*int to=(int)(Math.random()*mPlayerList.size());
                     while(mPlayerList.get(to)==player){
                         to=(int)(Math.random()*mPlayerList.size());
                     }*/
                                ball.changeV(PlayerList.get(to));
                                //ball.changeV(mPlayerList.get());
                        }
                }
                // 画球
                ball.myOnDraw(canvas, ballp);

        }

        //更新界面处理器
        class RefreshHandler extends Handler {
                @Override
                public void handleMessage(Message msg) {
                        if (msg.what == 0x101) {
                                if(GameView.this.ball.isCollided!=0) GameView.this.ball.isCollided++;//碰撞后的时间计数
                                if(GameView.this.ball.isCollided>5) GameView.this.ball.isCollided=0;//碰撞超过一定时间回到初始状态
                                GameView.this.ball.update(windowHeight,windowWidth);
                                aiLogic();
                                GameView.this.invalidate();
                        }
                        super.handleMessage(msg);
                }
        };

        private int lastX;
        private int MaxRight;
        @Override
        public boolean onTouchEvent(MotionEvent event) {

                int eventX = (int)event.getRawX();
                int eventY = (int)event.getRawY();

                switch(event.getAction()){
                        case MotionEvent.ACTION_DOWN: {
                                lastX = eventX;
                                MaxRight = windowWidth;
                                //看看这里的右侧数值有没有问题
                                Log.d("aaa", "onTouch: "+"  "+eventX+"  "+lastX);
                                break;
                        }
                        case MotionEvent.ACTION_MOVE:{
                                float dx = eventX - lastX;
                                Log.d("aaa", "onTouch: "+dx+"  "+eventX+"  "+lastX);
                                //float First_left = mPlayerList.get(0).dx + dx;

                                //相减
                                if ((mPlayerList.get(0).x + dx > 0) && (mPlayerList.get(2).x + dx + 40 < MaxRight)) {
                                        mPlayerList.get(0).update(dx);
                                        mPlayerList.get(1).update(dx);
                                        mPlayerList.get(2).update(dx);
                                }
                                if ((mPlayerList.get(3).x + dx*13/20 > 0)&&(mPlayerList.get(6).x + dx*13/20 + 40 < MaxRight)){
                                        mPlayerList.get(3).update(dx*13/20);
                                        mPlayerList.get(4).update(dx*13/20);
                                        mPlayerList.get(5).update(dx*13/20);
                                        mPlayerList.get(6).update(dx*13/20);
                                }
                                //从上往下数第3排的限制条件
                                if ((mPlayerList.get(7).x + dx*4/3 > 0)&&(mPlayerList.get(8).x + dx*4/3 + 40 < MaxRight)){
                                        mPlayerList.get(7).update(dx*4/3);
                                        mPlayerList.get(8).update(dx*4/3);
                                }
                                if((mPlayerList.get(9).x + dx > MaxRight/4)&&(mPlayerList.get(9).x + dx + 40 < MaxRight*3/4)){
                                        mPlayerList.get(9).update(dx);
                                }
                                break;
                        }
                        default:break;
                }
                return true;
        }

}//线程结束