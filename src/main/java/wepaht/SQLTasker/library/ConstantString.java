package wepaht.SQLTasker.library;

public abstract class ConstantString {
    
    public static final String ROLE_STUDENT = "STUDENT";
    public static final String ROLE_TEACHER = "TEACHER";
    public static final String ROLE_ADMIN = "ADMIN";
    
    public static final String VIEW_DATABASES = "databases";
    public static final String VIEW_TASK = "task";
    public static final String VIEW_USERS = "users";
    public static final String VIEW_USER = "user";
    public static final String VIEW_CATEGORY_EDIT = "categoryEdit";
    
    public static final String REDIRECT_DEFAULT = "redirect:/";
    public static final String REDIRECT_CATEGORIES = "redirect:/categories";
    public static final String REDIRECT_TASKS = "redirect:/tasks";
    
    public static final String ATTRIBUTE_CATEGORIES = "categories";
    public static final String ATTRIBUTE_CATEGORY = "category";
    public static final String ATTRIBUTE_MESSAGES = "messages";
    public static final String ATTRIBUTE_STUDENTS = "students";
    public static final String ATTRIBUTE_TEACHERS = "teachers";
    public static final String ATTRIBUTE_ADMINS = "admins";
    public static final String ATTRIBUTE_ROLES = "roles";
    public static final String ATTRIBUTE_EDITEDUSER = "editedUser";
    public static final String ATTRIBUTE_TOKEN = "token";
    public static final String ATTRIBUTE_TASKS = "tasks";
    
    public static final String MESSAGE_UNAUTHORIZED_ACCESS = "Unauthorized access";
    public static final String MESSAGE_UNAUTHORIZED_ACTION = "Unauhtorized action";
    public static final String MESSAGE_SUCCESSFUL_ACTION = "Action succesful";
    public static final String MESSAGE_FAILED_ACTION = "Action failed";
    
    public static final String TAG_HIDDEN = "HIDDEN";
}
