package ewm.main.service.event.repository;

import ewm.main.service.event.standart.EventStandart;
import ewm.main.service.event.model.Event;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;


public class EventBase implements Specification<Event> {
    private final EventStandart eventStandart;
    private final List<Predicate> predicateList;

    public EventBase(EventStandart eventStandart) {
        this.eventStandart = eventStandart;
        this.predicateList = new ArrayList<>();
    }


    @Override
    public Predicate toPredicate(Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (eventStandart.getText() != null) {
            Predicate annotation = criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), contains(eventStandart.getText()));
            Predicate description = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), contains(eventStandart.getText()));
            Predicate combination = criteriaBuilder.or(annotation, description);
            this.predicateList.add(combination);
        }

        if (eventStandart.getCategories() != null) {
            Predicate category = root.get("category").in(eventStandart.getCategories());
            this.predicateList.add(category);
        }

        if (eventStandart.getUsers() != null) {
            Predicate users = root.get("initiator").in(eventStandart.getUsers());
            this.predicateList.add(users);
        }

        if (eventStandart.getPaid() != null) {
            Predicate paid = criteriaBuilder.equal(root.get("paid"), eventStandart.getPaid());
            this.predicateList.add(paid);
        }

        if (eventStandart.getRangeStart() != null) {
            Predicate start = criteriaBuilder.greaterThan(root.get("eventDate"), eventStandart.getRangeStart());
            this.predicateList.add(start);
        }

        if (eventStandart.getRangeEnd() != null) {
            Predicate end = criteriaBuilder.lessThan(root.get("eventDate"), eventStandart.getRangeEnd());
            this.predicateList.add(end);
        }

        if (eventStandart.getStates() != null) {
            Predicate states = root.get("state").in(eventStandart.getStates());
            this.predicateList.add(states);
        }

        return query.where(criteriaBuilder.and(predicateList.toArray(new Predicate[0])))
                .orderBy(criteriaBuilder.desc(root.get("eventDate")))
                .getRestriction();
    }

    private static String contains(String expression) {
        return MessageFormat.format("%{0}%", expression.toLowerCase());
    }
}