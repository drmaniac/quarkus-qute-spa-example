INSERT
    INTO
        Todo(
            id,
            summary,
            description
        )
    VALUES(
        nextval('todo_idx'),
        'Do some stufe',
        'You should do something'
    ),
    (
        nextval('todo_idx'),
        'Do other stufe',
        'Do it now'
    ),
    (
        91000,
        'TodoPageTest-testPostTodoEdit',
        'To be updated'
    ),
    (
        91001,
        'TodoPageTest-testGetElementEdit',
        'To be updated'
    ),
    (
        92000,
        'TodoServiceTest-testUpdate',
        'To be updated'
    );