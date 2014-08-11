package todo.app.todo;

import java.util.List;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.model.chart.PieChartModel;
import todo.domain.common.exception.BusinessException;
import todo.domain.model.Todo;
import todo.domain.service.todo.TodoService;

/**
 * TodoController.
 */
@Named(value = "todoController")
@RequestScoped
public class TodoController {
    
    /**
     * TodoService.
     */
    @EJB
    protected TodoService todoService;
    /**
     * 新規TODO作成フォームに対応するBean。
     */
    protected Todo todo = new Todo();
    /**
     * TODO一覧を格納したList。
     */
    protected List<Todo> todoList;
    
    /**
     * 画面に表示する円状グラフ。
     */
    protected PieChartModel model;

    /**
     * Creates a new instance of TodoController
     */
    public TodoController() {
        FacesContext.getCurrentInstance().getViewRoot().setLocale(new Locale("ja", "JP"));
    }
    
    /**
     * get Todo.
     * @return Todo object.
     */
    public Todo getTodo() {
        return todo;
    }
    
    /**
     * get Todo list.
     * @return Todo list.
     */
    public List<Todo> getTodoList() {
        return todoList;
    }
    
    /**
     * get PieChartModel.
     * @return PieChartModel
     */
    public PieChartModel getModel() {
        return model;
    }
    
    /**
     * 初期処理。
     */
    @PostConstruct
    public void init() {
        todoList = todoService.findAll();
        int finishedCount = 0;
        for(Todo tempTodo : todoList) {
            if (tempTodo.isFinished()) {
                finishedCount = finishedCount + 1;
            }
        }
        model = new PieChartModel();
        model.set("TODO未完了", todoList.size() - finishedCount);
        model.set("TODO完了", finishedCount);
    }
    
    /**
     * TODO新規登録。
     * フォームに入力された値を格納したTodoオブジェクトをEJBに渡し、新規TODOを登録する。
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
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "新規TODOを登録しました。", null));
        this.getTodoList().add(todo);
        return "list.xhtml?faces-redirect=ture";
    }
    
    /**
     * TODO完了。
     * @param todoId 完了にするTODO ID
     * @return 正常終了後、list.xhtmlへリダイレクトする処理を示す文字列
     */
    public String finish(Integer todoId) {
        try {
            todoService.finish(todoId);
        } catch (BusinessException except) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, except.getMessage(), null));
            return "list.xhtml";
        }
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "TODO ID:" + todoId + " を完了状態にしました。", null));
        return "list.xhtml?faces-redirect=true";
    }
    
    /**
     * TODO削除。
     * @param todoId 削除するTODO ID
     * @return 正常終了後、list.xhtmlへリダイレクトする処理を示す文字列
     */
    public String delete(Integer todoId) {
        todoService.delete(todoId);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "TODO ID:" + todoId + " を削除しました。", null));
        return "list.xhtml?faces-redirect=true";
    }
}
