package jpabook.jpashop.util;

import javax.servlet.http.HttpSession;

public class SessionUtils {

    private static String USER_KEY = "userId";

    public static String getUserFromSession(HttpSession session){
        return (String)session.getAttribute(USER_KEY);
    }
}
