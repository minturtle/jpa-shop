package jpabook.jpashop.dao;

import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import java.util.List;


@RequiredArgsConstructor
public abstract class EntityManagerRepository<T>{

    protected final EntityManager em;
    private final Class<T> clazz;

    public void save(T entity){
        em.persist(entity);
    }

    public T findByName(String name){

        try{
            T entity = em.createQuery(getSelectQlStringWhere("name"), clazz)
                    .setParameter("name", name).getSingleResult();
            return entity;

        }catch (NoResultException e){
            throw new EntityNotFoundException();
        }
    }

    public T findById(Long id) throws EntityNotFoundException{
        return em.find(clazz, id);
    }

    public List<T> findAll(){
        return em.createQuery(getSelectQlString(), clazz).getResultList();
    }

    protected String getSelectQlString(){
        return String.format("select m from %s m", clazz.getSimpleName());
    }
    protected String getSelectQlStringWhere(String field){
        return String.format("select m from %s m where m.%s =:%s", clazz.getSimpleName(), field, field);
    }


}
