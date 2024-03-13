package jpabook.jpashop.dao.jpa;

import jpabook.jpashop.dao.ItemRepository;
import jpabook.jpashop.domain.item.Item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaItemRepository implements ItemRepository {

    private final InnerJpaItemRepository repository;

    @Override
    public void save(Item item) {
        repository.save(item);
    }

    @Override
    public Item findByName(String name)throws EntityNotFoundException{
        Item item = repository.findByName(name).orElseThrow(EntityNotFoundException::new);
        return item;
    }

    @Override
    public Item findById(Long id) throws EntityNotFoundException{
        Item item = repository.findById(id).orElseThrow(EntityNotFoundException::new);
        return item;
    }

    @Override
    public List<Item> findAll(Pageable pageable){
        return repository.findAll(pageable).toList();
    }

    @Override
    public List<Item> findAll() {
        return findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id")));
    }

}

@Component
interface InnerJpaItemRepository extends JpaRepository<Item, Long>{
    Optional<Item> findByName(String name);
}