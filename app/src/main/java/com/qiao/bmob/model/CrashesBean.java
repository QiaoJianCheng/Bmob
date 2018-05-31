package com.qiao.bmob.model;

import java.util.List;

/**
 * Created by Qiao on 2017/4/12.
 */

public class CrashesBean extends BaseBean {
    public List<CrashBean> results;

    public static class CrashBean extends BaseBean {
        public String api_level;
        public String application_id;
        public String crash_info;
        public String createdAt;
        public String model;
        public String objectId;
        public String updatedAt;
        public String version_name;
    }

}
