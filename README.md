# java-explore-with-me

**Описание**

Двухмодульное приложение-микросервис для размещения и поиска событий, в котором функционал различается в зависимости от роли (публичный, авторизованный пользователь или администратор).
Приложение состоит из основного сервиса, основной БД, сервиса статистики просмотров, БД для статистики. Каждая часть поднимается в отдельном docker-контейнере.



**Стек:**    
·Java 11 (Core, Collections, Optional, Stream)    
·Spring Boot    
·Hibernate    
·PostgreSQL    
·Maven    
·Lombok, MapStruct    
·Junit5, Mockito    
·Postman    
·Docker    

**Endpoints**    
·Техническое задание для API основного сервиса    
·Техническое задание для API сервиса статистики    
·В качестве дополнительной фичи реализован функционал комментариев.


***Comments***    
[GET] /admin/comments?from={from}&size={size} – получить список всех комментариев с пагинацией    
[DELETE] /admin/comments/{commentId} – удалить комментарий commentId    
[POST] /users/{userId}/comments?eventId={eventId} – создать новый комментарий к событию eventId пользователем userId    
[PATCH] /users/{userId}/comments/{commentId} – обновить свой комментарий commentId пользователем userId    
[DELETE] /users/{userId}/comments/{commentId} - удалить свой комментарий commentId пользователем userId    
[GET] /users/{userId}/comments?eventId={eventId}&from={from}&size={size} - получить список всех комментариев пользователя userId к событию eventId с пагинацией    
[GET] /users/{userId}/comments?from={from}&size={size} - получить список всех комментариев пользователя userId с пагинацией    
[GET] /comments?eventId={eventId}&from={from}&size={size} – получить список всех комментариев к событию eventId с пагинацией    
[GET] /comments/{commentId} – получить комментарий commentId    
