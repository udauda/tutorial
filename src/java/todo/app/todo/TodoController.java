package todo.app.todo;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import todo.domain.common.exception.BusinessException;
import todo.domain.model.Todo;
import todo.domain.service.todo.TodoService;

/**
 * TodoController.
 */
@Named(value = "todoController")
@RequestScoped
public class TodoController {
    
    @EJB
    protected TodoService todoService;
    /**
     * 新規作成フォームに対応するBean。
     */
    protected Todo todo = new Todo();
    protected List<Todo> todoList;

    /**
     * Creates a new instance of TodoController
     */
    public TodoController() {
    }
    
    public Todo getTodo() {
        return todo;
    }
    
    public List<Todo> getTodoList() {
        return todoList;
    }
    
    @PostConstruct
    public void init() {
        todoList = todoService.findAll();
    }
    
    /**
     * TODO新規登録。
     * フォームに入力された値を格納したTodoオブジェクトをEJBに渡す。
     * @return 正常終了後、list.xhtmlへリダイレクトする処理を示す文字列
     */
    public String create() {
        try {
            todoService.create(todo);   
        } catch (BusinessException except) {
            // BusinessExceptionをキャッチして、FacesContextにFacesMessageを追加する。
            // SERVERITY_ERRORを指定する。
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, except.getMessage(), null));
            return "list.xhtml";
        }
        // リダイレクト先にもメッセージが残るようにFacesContextのFlashスコープを使用する設定をする。
        // Flashスコープはリダイレクト後の次の1回だけアクセスできるスコープ。
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        // 成功メッセージ用のFacesMessageとしてSERVERITY_INFOを設定。
        // FacesContextに追加する。
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Created successfully!", null));
        return "list?faces-redirect=ture";
    }
    
    public String finish(Integer todoId) {
        try {
            todoService.finish(todoId);
        } catch (BusinessException except) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, except.getMessage(), null));
            return "list.xhtml";
        }
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Finished successfully!", null));
        return "list.xhtml?faces-redirect=true";
    }
    
    public String delete(Integer todoId) {
        todoService.delete(todoId);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Deleted successfully!", null));
        return "list.xhtml?faces-redirect=true";
    }
}
