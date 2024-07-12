package hello.itemservice.repository.jpa;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@Transactional // JPA에서 데이터를 변경(등록,수정,삭제)할때 항상 있어야함 / 필수!
public class JpaItemRepository implements ItemRepository {

    private final EntityManager em; //EntityManager는 내부에 데이터소스를 가지고 있고 데이터베이스에 접근 가능

    public JpaItemRepository(EntityManager em) { //생성자를 보다시피 EntityManager를 주입 받음, JPA의 모든 동작은 EntityManager를 통해 이루어짐.
        this.em = em;
    }

    @Override
    public Item save(Item item) {
        em.persist(item);
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        Item findItem = em.find(Item.class, itemId);
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
        //언제 데이터베이스에 update 쿼리를 날리냐, 트랜잭션이 커밋되는 시점에..
    }

    @Override
    public Optional<Item> findById(Long id) {
        Item item = em.find(Item.class, id); //(타입, pk) , 하나를 조회할 때
        return Optional.ofNullable(item);
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String jpql = "SELECT i FROM Item i"; //객체 쿼리 언어 , i는 Item 엔티티를 말함, jpql문법은 테이블 대상이 아니라 Item 엔티티를 대상으로, 동적 쿼리에 약함

        // 동적 쿼리
        Integer maxPrice = cond.getMaxPrice();
        String itemName = cond.getItemName();

        if (StringUtils.hasText(itemName) || maxPrice != null) {
            jpql += " where";
        }

        boolean andFlag = false;
        if (StringUtils.hasText(itemName)) {
            jpql += " i.itemName like concat('%',:itemName,'%')";
            andFlag = true;
        }

        if (maxPrice != null) {
            if (andFlag) {
                jpql += " and";
            }
            jpql += " i.price <= :maxPrice";
        }

        log.info("jpql={}", jpql);

        TypedQuery<Item> query = em.createQuery(jpql, Item.class);
        if (StringUtils.hasText(itemName)) {
            query.setParameter("itemName", itemName);
        }

        if (maxPrice != null) {
            query.setParameter("maxPrice", maxPrice);
        }
        return query.getResultList();
    }
}
