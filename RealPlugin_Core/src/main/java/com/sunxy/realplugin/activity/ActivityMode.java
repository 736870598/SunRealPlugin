package com.sunxy.realplugin.activity;

import android.app.Activity;

/**
 * --
 * <p>
 * Created by sunxy on 2018/8/17 0017.
 */
public class ActivityMode extends Activity {

    /**
     * stander启动模式 默认启动模式
     */
    private static class StanderStub extends ActivityMode {

    }

    /**
     * singleTop启动模式， 当activity已经在栈顶了，就不去创建，直接使用栈顶的
     */
    private static class SingleTopStub extends ActivityMode{

    }

    /**
     * singleTask启动模式，当前栈内已经有了activity是，则不会去创建，
     * 而是直接调用栈里的activity，并且该activity以上的全部出栈。
     */
    private static class SingleTaskStub extends ActivityMode{

    }

    /**
     * singleInstance启动模式  任务栈内单例
     * 启动singleInstance的Activity时，系统会创建一个新的任务栈，并且这个任务栈只有他一个Activity。
     */
    private static class SingleInstanceStub extends ActivityMode{

    }



    public static class P01{
        public static class Standard extends StanderStub {
        }
        public static class SingleInstance extends SingleInstanceStub {
        }
        public static class SingleTask extends SingleTaskStub {
        }

        public static class SingleTop extends SingleTopStub {
        }
    }

    public static class P02{
        public static class Standard extends StanderStub {
        }
        public static class SingleInstance extends SingleInstanceStub {
        }
        public static class SingleTask extends SingleTaskStub {
        }

        public static class SingleTop extends SingleTopStub {
        }
    }

    public static class P03{
        public static class Standard extends StanderStub {
        }
        public static class SingleInstance extends SingleInstanceStub {
        }
        public static class SingleTask extends SingleTaskStub {
        }

        public static class SingleTop extends SingleTopStub {
        }
    }

    public static class P04{
        public static class Standard extends StanderStub {
        }
        public static class SingleInstance extends SingleInstanceStub {
        }
        public static class SingleTask extends SingleTaskStub {
        }

        public static class SingleTop extends SingleTopStub {
        }
    }

    public static class P05{
        public static class Standard extends StanderStub {
        }
        public static class SingleInstance extends SingleInstanceStub {
        }
        public static class SingleTask extends SingleTaskStub {
        }

        public static class SingleTop extends SingleTopStub {
        }
    }



}
