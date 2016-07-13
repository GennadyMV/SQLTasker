package wepaht.SQLTasker.interceptor;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import static wepaht.SQLTasker.constant.ConstantString.*;
import wepaht.SQLTasker.domain.Account;
import wepaht.SQLTasker.service.AccountService;

public class AttributeInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    AccountService accountService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView mav) {
        Account user = null;
        
        user = accountService.getAuthenticatedUser();

        if (user != null) {
            if (request.getMethod().equals("GET")) {
                mav.getModel().put("role", user.getRole());
                setNavigationAttributes(user, mav);
            }            
        }
    }

    private void setNavigationAttributes(Account user, ModelAndView mav) {
        if (user != null) {
            HashMap<String, String> common = new HashMap<>();
            common.put(ATTRIBUTE_NAV_COURSES, LINK_COURSES);
            common.put(ATTRIBUTE_NAV_CATEGORIES, LINK_CATEGORIES);
            mav.getModel().put(ATTRIBUTE_PUBLIC_NAV, common);
            setTeacherNavigationAttributes(user, mav);
        }
    }

    private void setTeacherNavigationAttributes(Account user, ModelAndView mav) {
        if (!user.getRole().equals(ROLE_STUDENT)) {
            HashMap<String, String> teacher = new HashMap<>();
            teacher.put(ATTRIBUTE_NAV_USERS, LINK_USERS);
            teacher.put(ATTRIBUTE_NAV_TASKS, LINK_TASKS);
            teacher.put(ATTRIBUTE_NAV_DATABASES, LINK_DATABASES);
            teacher.put(ATTRIBUTE_NAV_SUBMISSIONS, LINK_SUBMISSIONS);
            teacher.put(ATTRIBUTE_NAV_FEEDBACK, LINK_FEEDBACK);
            mav.getModel().put(ATTRIBUTE_TEACHER_NAV, teacher);
        }
    }
}
