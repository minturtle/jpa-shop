package jpabook.jpashop.util;

import javax.servlet.http.HttpSession;

public class SessionUtils {

    private static String USER_KEY = "userId";


    public static void putUserToSession(HttpSession session, Long id){
        session.setAttribute(USER_KEY, id);
    }
    public static Long getUserFromSession(HttpSession session){
        return (Long)session.getAttribute(USER_KEY);
    }

    public static void logout(HttpSession session){
        session.removeAttribute(USER_KEY);
    }
}
