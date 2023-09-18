package ewm.main.service.event;

import ewm.main.service.common.models.State;
import ewm.main.service.event.model.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventRepositoryImpl {
    private final EntityManager entityManager;


    public List<Event> findAllEventsByFilterPublic(String text,
                                                   List<Integer> categoriesIdList,
                                                   Boolean paid,
                                                   LocalDateTime rangeStart,
                                                   LocalDateTime rangeEnd,
                                                   Boolean onlyAvailable,
                                                   String sort,
                                                   int from,
                                                   int size) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> cbQuery = cb.createQuery(Event.class);
        Root<Event> root = cbQuery.from(Event.class);
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(root.get("state"), State.PUBLISHED));

        if (text != null && !text.isEmpty()) {
            String searchText = "%" + text.toUpperCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.upper(root.get("annotation")), searchText),
                    cb.like(cb.upper(root.get("description")), searchText)
            ));
        }

        if (categoriesIdList != null && !categoriesIdList.isEmpty()) {
            predicates.add(root.get("category").get("id").in(categoriesIdList));
        }

        if (paid != null) {
            predicates.add(cb.equal(root.get("paid"), paid));
        }

        if (rangeStart != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }

        if (rangeEnd != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }

        if (rangeStart == null && rangeEnd == null) {
            predicates.add(cb.greaterThan(root.get("eventDate"), LocalDateTime.now()));
        }

        if (onlyAvailable != null && onlyAvailable) {
            Predicate noLimitPredicate = cb.equal(root.get("participantLimit"), 0);
            Predicate notFullPredicate = cb.lessThan(root.get("confirmedRequests"), root.get("participantLimit"));
            predicates.add(cb.or(noLimitPredicate, notFullPredicate));
        }

        Order order = cb.asc(root.get("eventDate"));
        if ("VIEWS".equals(sort)) {
            order = cb.desc(root.get("views"));
        }

        cbQuery.select(root).where(predicates.toArray(new Predicate[0])).orderBy(order);

        return entityManager.createQuery(cbQuery)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();
    }

    public List<Event> findAllEventsByFilterAdmin(List<Integer> usersIdList,
                                                  List<State> states,
                                                  List<Integer> categoriesIdList,
                                                  LocalDateTime rangeStart,
                                                  LocalDateTime rangeEnd,
                                                  int from,
                                                  int size) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> cbQuery = cb.createQuery(Event.class);
        Root<Event> root = cbQuery.from(Event.class);
        List<Predicate> predicates = new ArrayList<>();

        if (usersIdList != null && !usersIdList.isEmpty()) {
            predicates.add(root.get("initiator").get("id").in(usersIdList));
        }

        if (states != null && !states.isEmpty()) {
            predicates.add(root.get("state").in(states));
        }

        if (categoriesIdList != null && !categoriesIdList.isEmpty()) {
            predicates.add(root.get("category").get("id").in(categoriesIdList));
        }

        if (rangeStart != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }

        if (rangeEnd != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }

        cbQuery.select(root)
                .where(predicates.toArray(new Predicate[0]))
                .orderBy(cb.asc(root.get("id")));

        return entityManager.createQuery(cbQuery)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();
    }

}