package org.barrysen.api;

import org.barrysen.vo.LoginUser;


public interface CommonAPI {

    /**
     * 5根据用户账号查询用户信息
     * @param username
     * @return
     */
    public LoginUser getUserByName(String username);
}
