package jpabook.jpashop.dao;

import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import java.util.List;


@RequiredArgsConstructor
public abstract class EntityManagerRepository<T>{

    private final EntityManager em;
    private final Class<T> clazz;

    public void save(T entity){
        em.persist(entity);
    }

    public T findByName(String name){

        try{
            T entity = em.createQuery(getSelectByNameQlStirng(), clazz)
                    .setParameter("name", name).getSingleResult();
            checkIsEntityNull(entity);
            return entity;

        }catch (NoResultException e){
            throw new EntityNotFoundException();
        }
    }

    public T findById(Long id) throws EntityNotFoundException{
        T entity = em.find(clazz, id);
        checkIsEntityNull(entity);

        return entity;
    }

    private String getSelectByNameQlStirng() {
        return  getSelectQlString() + " where m.name =:name";
    }

    private String getSelectQlString(){
        return String.format("select m from %s m", clazz.getName());
    }

    public List<T> findAll(){
        return em.createQuery(getSelectQlString(), clazz).getResultList();
    }

    private void checkIsEntityNull(Object entity) {
        if (entity == null) throw new EntityNotFoundException();
    }
}
