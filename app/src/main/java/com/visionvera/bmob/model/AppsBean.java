package com.visionvera.bmob.model;

import java.util.ArrayList;

/**
 * Created by Qiao on 2017/4/11.
 */

public class AppsBean extends BaseBean {
    public ArrayList<AppBean> results;

    public static class AppBean extends BaseBean {
        public String application_id;
        public String app_name;
        public boolean bang;
        public String createdAt;
        public String objectId;
        public String updatedAt;
    }
}
