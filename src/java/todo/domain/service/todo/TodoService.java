package todo.domain.service.todo;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import todo.domain.common.exception.BusinessException;
import todo.domain.common.exception.ResourceNotFoundException;
import todo.domain.model.Todo;

/**
 * Todo service class.
 * Statelessアノテーションを付けることで、ステートレスセッションBeanとして認識される。
 * このクラス内での各メソッドはトランザクション管理の対象になる。
 * メソッド開始時にトランザクション開始、正常終了時にコミットされる。
 * 途中でRuntimeExceptionが発生した場合はトランザクションがロールバックされる。
 */
@Stateless
public class TodoService {

    /**
     * Max unfinished count.
     */
    private static final long MAX_UNFINISHED_COUNT = 5;
    /**
     * EntityManager.
     * PersistenceContextアノテーションでJPAのエンティティを操作するEntityManagerをインジェクションする。
     */
    @PersistenceContext
    protected EntityManager entityManager;
    
    public Todo findByTodoId(Integer todoId) {
        Todo todo = entityManager.find(Todo.class, todoId);
        if(todoId == null) {
            throw new ResourceNotFoundException("[E404]該当IDのTODOは存在しません。TODO ID=" + todoId);
        }
        return todo;
    }
    /**
     * find all.
     * Todoエンティティを全件取得するNamedeQueryを作成する。
     * 指定した"Todo.findAll"に対応するQueryはTodoクラスに
     * @NamedQuery(name = "Todo.findAll", query = "SELECT t FROM Todo t")と定義されている。
     * TypedQuery型として宣言することで、タイプセーフになる。
     * @return Todo全件を格納したList
     */
    public List<Todo> findAll() {
        TypedQuery<Todo> query = entityManager.createNamedQuery("Todo.findAll", Todo.class);
        // Queryの結果をListとして取得する。
        return query.getResultList();
    }

    public Todo create(Todo todo) {
        TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(x) FROM Todo x WHERE x.finished = :finished", Long.class)
                .setParameter("finished", false);
        long unfinishedCount = query.getSingleResult();
        if(unfinishedCount > MAX_UNFINISHED_COUNT) {
            throw new BusinessException("[E001] 未完了TODO数:" + unfinishedCount +"。未完了TODO最大数"+ MAX_UNFINISHED_COUNT+ "を超えているため、これ以上のTODO登録はできません。");
        }
        
        todo.setFinished(Boolean.FALSE);
        todo.setCreatedAt(new Date());
        entityManager.persist(todo);
        return todo;
    }
    
    public Todo finish(Integer todoId) {
        Todo todo = findByTodoId(todoId);
        if(todo.isFinished()) {
            throw new BusinessException("[E002] 該当IDのTODOはすでに完了しています。TODO ID = " + todoId);    
        }
        todo.setFinished(Boolean.TRUE);
        entityManager.merge(todo);
        return todo;
    }
    
    public void delete(Integer todoId) {
        Todo todo = findByTodoId(todoId);
        entityManager.remove(todo);
    }
}
