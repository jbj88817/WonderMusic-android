package us.bojie.wondermusic.login.user;


import us.bojie.wondermusic.model.BaseModel;

/**
 * 用户数据协议
 */
public class User extends BaseModel {
    public int ecode;
    public String emsg;
    public UserContent data;
}
