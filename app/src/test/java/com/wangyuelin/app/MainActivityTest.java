package com.wangyuelin.app;

import android.util.Log;

import androidx.test.core.app.ApplicationProvider;

import com.wangyuelin.myandroidworld.util.CacheDiskUtils;
import com.wangyuelin.myandroidworld.util.Utils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.io.Serializable;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(shadows = {ShadowLog.class}, sdk = 28, application = TestApplication.class)
public class MainActivityTest {

    @Before
    public void setUp() throws Exception {
        ShadowLog.stream = System.out;//为了使Log输出在控制台能看见
        TestApplication application = ApplicationProvider.getApplicationContext();
        Utils.init(application);
    }


    @Test
    public void onCreate() {
        Stu stu = new Stu("王跃林", 27);
        CacheDiskUtils.getInstance().put("wang", stu);
        Stu stu2 = (Stu) CacheDiskUtils.getInstance().getSerializable("wang");
        Log.d("wyl", "获取到的名称：" + stu2.name);


    }


    private static class Stu implements Serializable {
        public String name;
        public int age;

        public Stu(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }
}